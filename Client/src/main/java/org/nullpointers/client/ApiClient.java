package org.nullpointers.client;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import org.apache.hc.client5.http.classic.methods.HttpDelete;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;

/**
 * HTTP client wrapper for calling the Coupon Management System API.
 */
public class ApiClient {
  private final String baseUrl;
  private final CloseableHttpClient httpClient;
  private final Gson gson;

  /**
   * Creates a new API client.
   *
   * @param baseUrl The base URL of the service (e.g., "http://localhost:8080")
   */
  public ApiClient(String baseUrl) {
    this.baseUrl = baseUrl;
    this.httpClient = HttpClients.createDefault();
    this.gson = new Gson();
  }

  /**
   * Creates a new store.
   *
   * @param storeName The name of the store
   * @return The created store as JsonObject
   * @throws IOException if the request fails
   */
  public JsonObject createStore(String storeName) throws IOException {
    JsonObject requestBody = new JsonObject();
    requestBody.addProperty("name", storeName);
    return post("/store", requestBody);
  }

  /**
   * Creates a new item.
   *
   * @param name The item name
   * @param price The item price
   * @param storeId The store ID
   * @param category The item category
   * @return The created item as JsonObject
   * @throws IOException if the request fails
   */
  public JsonObject createItem(String name, double price, int storeId, String category)
      throws IOException {
    JsonObject requestBody = new JsonObject();
    requestBody.addProperty("name", name);
    requestBody.addProperty("price", price);
    requestBody.addProperty("storeId", storeId);
    requestBody.addProperty("category", category);
    return post("/item", requestBody);
  }

  /**
   * Creates a TotalPriceCoupon.
   *
   * @param storeId The store ID
   * @param discountValue The discount value
   * @param isPercentage Whether the discount is a percentage
   * @param minimumPurchase The minimum purchase amount
   * @return The created coupon as JsonObject
   * @throws IOException if the request fails
   */
  public JsonObject createTotalPriceCoupon(int storeId, double discountValue,
      boolean isPercentage, double minimumPurchase) throws IOException {
    JsonObject requestBody = new JsonObject();
    requestBody.addProperty("type", "totalprice");
    requestBody.addProperty("storeId", storeId);
    requestBody.addProperty("discountValue", discountValue);
    requestBody.addProperty("isPercentage", isPercentage);
    requestBody.addProperty("minimumPurchase", minimumPurchase);
    return post("/coupon", requestBody);
  }

  /**
   * Creates a CategoryCoupon.
   *
   * @param storeId The store ID
   * @param discountValue The discount value
   * @param isPercentage Whether the discount is a percentage
   * @param category The category this coupon applies to
   * @return The created coupon as JsonObject
   * @throws IOException if the request fails
   */
  public JsonObject createCategoryCoupon(int storeId, double discountValue,
      boolean isPercentage, String category) throws IOException {
    JsonObject requestBody = new JsonObject();
    requestBody.addProperty("type", "category");
    requestBody.addProperty("storeId", storeId);
    requestBody.addProperty("discountValue", discountValue);
    requestBody.addProperty("isPercentage", isPercentage);
    requestBody.addProperty("category", category);
    return post("/coupon", requestBody);
  }

  /**
   * Creates an ItemCoupon.
   *
   * @param storeId The store ID
   * @param discountValue The discount value
   * @param isPercentage Whether the discount is a percentage
   * @param targetItemId The target item ID
   * @return The created coupon as JsonObject
   * @throws IOException if the request fails
   */
  public JsonObject createItemCoupon(int storeId, double discountValue,
      boolean isPercentage, int targetItemId) throws IOException {
    JsonObject requestBody = new JsonObject();
    requestBody.addProperty("type", "item");
    requestBody.addProperty("storeId", storeId);
    requestBody.addProperty("discountValue", discountValue);
    requestBody.addProperty("isPercentage", isPercentage);
    requestBody.addProperty("targetItemId", targetItemId);
    return post("/coupon", requestBody);
  }

  /**
   * Finds the optimal coupon for a cart.
   *
   * @param itemIds The list of item IDs in the cart
   * @param storeId The store ID
   * @return The optimal coupon result as JsonElement
   * @throws IOException if the request fails
   */
  public JsonElement findOptimalCoupon(int[] itemIds, int storeId) throws IOException {
    JsonObject requestBody = new JsonObject();
    JsonArray itemIdsArray = new JsonArray();
    for (int id : itemIds) {
      itemIdsArray.add(id);
    }
    requestBody.add("itemIds", itemIdsArray);
    requestBody.addProperty("storeId", storeId);

    String response = postRaw("/cart/optimal-coupon", requestBody);
    return gson.fromJson(response, JsonElement.class);
  }

  /**
   * Gets optimal stores for a search query.
   *
   * @param keyword The search keyword (can be null)
   * @param category The category filter (can be null)
   * @return The list of store recommendations as JsonArray
   * @throws IOException if the request fails
   */
  public JsonArray getOptimalStores(String keyword, String category) throws IOException {
    StringBuilder url = new StringBuilder("/stores/optimal?");
    if (keyword != null && !keyword.isEmpty()) {
      url.append("keyword=").append(keyword);
    }
    if (category != null && !category.isEmpty()) {
      if (keyword != null && !keyword.isEmpty()) {
        url.append("&");
      }
      url.append("category=").append(category);
    }
    return get(url.toString()).getAsJsonArray();
  }

  /**
   * Gets item suggestions to meet a coupon threshold.
   *
   * @param itemIds The current cart item IDs
   * @param storeId The store ID
   * @param couponId The coupon ID
   * @return The suggested items as JsonElement
   * @throws IOException if the request fails
   */
  public JsonElement suggestItems(int[] itemIds, int storeId, int couponId) throws IOException {
    JsonObject requestBody = new JsonObject();
    JsonArray itemIdsArray = new JsonArray();
    for (int id : itemIds) {
      itemIdsArray.add(id);
    }
    requestBody.add("itemIds", itemIdsArray);
    requestBody.addProperty("storeId", storeId);
    requestBody.addProperty("couponId", couponId);

    String response = postRaw("/cart/suggest-items", requestBody);
    return gson.fromJson(response, JsonElement.class);
  }

  /**
   * Gets all items from a specific store.
   *
   * @param storeId The store ID
   * @return The list of items as JsonArray
   * @throws IOException if the request fails
   */
  public JsonArray getItemsByStore(int storeId) throws IOException {
    return get("/items/store/" + storeId).getAsJsonArray();
  }

  /**
   * Gets all coupons from a specific store.
   *
   * @param storeId The store ID
   * @return The list of coupons as JsonArray
   * @throws IOException if the request fails
   */
  public JsonArray getCouponsByStore(int storeId) throws IOException {
    return get("/coupons/store/" + storeId).getAsJsonArray();
  }

  /**
   * Performs a POST request.
   *
   * @param endpoint The API endpoint
   * @param requestBody The request body as JsonObject
   * @return The response as JsonObject
   * @throws IOException if the request fails
   */
  private JsonObject post(String endpoint, JsonObject requestBody) throws IOException {
    String response = postRaw(endpoint, requestBody);
    return gson.fromJson(response, JsonObject.class);
  }

  /**
   * Performs a POST request and returns raw response.
   *
   * @param endpoint The API endpoint
   * @param requestBody The request body as JsonObject
   * @return The response as String
   * @throws IOException if the request fails
   */
  private String postRaw(String endpoint, JsonObject requestBody) throws IOException {
    HttpPost request = new HttpPost(URI.create(baseUrl + endpoint));
    request.setHeader("Content-Type", "application/json");
    request.setEntity(new StringEntity(gson.toJson(requestBody), StandardCharsets.UTF_8));

    try (CloseableHttpResponse response = httpClient.execute(request)) {
      return EntityUtils.toString(response.getEntity());
    } catch (org.apache.hc.core5.http.ParseException e) {
      throw new IOException("Failed to parse response", e);
    }
  }

  /**
   * Performs a GET request.
   *
   * @param endpoint The API endpoint
   * @return The response as JsonElement
   * @throws IOException if the request fails
   */
  private JsonElement get(String endpoint) throws IOException {
    HttpGet request = new HttpGet(URI.create(baseUrl + endpoint));
    try (CloseableHttpResponse response = httpClient.execute(request)) {
      String responseBody = EntityUtils.toString(response.getEntity());
      return gson.fromJson(responseBody, JsonElement.class);
    } catch (org.apache.hc.core5.http.ParseException e) {
      throw new IOException("Failed to parse response", e);
    }
  }

  /**
   * Closes the HTTP client.
   *
   * @throws IOException if closing fails
   */
  public void close() throws IOException {
    httpClient.close();
  }
}
