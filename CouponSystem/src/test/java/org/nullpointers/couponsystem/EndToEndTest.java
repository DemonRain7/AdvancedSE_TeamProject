package org.nullpointers.couponsystem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

/**
 * End-to-End Integration Tests for the Coupon Management System.
 *
 * <p>These tests simulate real client-server interactions by:
 * 1. Starting the Spring Boot application on a random port
 * 2. Making actual HTTP requests to the running service
 * 3. Verifying the complete workflow from a client's perspective
 *
 * <p>Test Scenarios:
 * - Scenario 1: Flower Shop complete workflow
 * - Scenario 2: Multi-client isolation verification
 * - Scenario 3: Cart optimization and coupon selection
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class EndToEndTest {

  @LocalServerPort
  private int port;

  private static HttpClient httpClient;
  private static ObjectMapper objectMapper;

  // Store data for multi-client testing
  private static int flowerShopStoreId;
  private static int electronicsStoreId;
  private static int[] flowerItemIds = new int[3];
  private static int[] electronicItemIds = new int[3];
  private static int flowerCouponId;
  private static int electronicsCouponId;

  @BeforeAll
  static void setup() {
    httpClient = HttpClient.newHttpClient();
    objectMapper = new ObjectMapper();
  }

  /**
   * Test 1: Complete Flower Shop Workflow.
   * Simulates a flower shop registering, adding products, creating coupons,
   * and using the coupon recommendation features.
   */
  @Test
  @Order(1)
  void testFlowerShopCompleteWorkflow() throws Exception {
    String baseUrl = "http://localhost:" + port;

    // Step 1: Register the flower shop
    String storeRequest = "{\"name\": \"Rose Garden Flower Shop\"}";
    HttpRequest createStoreReq = HttpRequest.newBuilder()
        .uri(URI.create(baseUrl + "/store"))
        .header("Content-Type", "application/json")
        .POST(HttpRequest.BodyPublishers.ofString(storeRequest))
        .build();

    HttpResponse<String> storeResponse = httpClient.send(createStoreReq,
        HttpResponse.BodyHandlers.ofString());
    assertEquals(201, storeResponse.statusCode(), "Store creation should return 201");

    JsonNode storeNode = objectMapper.readTree(storeResponse.body());
    flowerShopStoreId = storeNode.get("id").asInt();
    assertEquals("Rose Garden Flower Shop", storeNode.get("name").asText());

    // Step 2: Add flower products
    String[] flowerProducts = {
        "{\"name\": \"Red Roses\", \"price\": 29.99, \"storeId\": " + flowerShopStoreId
            + ", \"category\": \"flowers\"}",
        "{\"name\": \"White Lilies\", \"price\": 24.99, \"storeId\": " + flowerShopStoreId
            + ", \"category\": \"flowers\"}",
        "{\"name\": \"Tulips\", \"price\": 19.99, \"storeId\": " + flowerShopStoreId
            + ", \"category\": \"flowers\"}"
    };

    for (int i = 0; i < flowerProducts.length; i++) {
      HttpRequest createItemReq = HttpRequest.newBuilder()
          .uri(URI.create(baseUrl + "/item"))
          .header("Content-Type", "application/json")
          .POST(HttpRequest.BodyPublishers.ofString(flowerProducts[i]))
          .build();

      HttpResponse<String> itemResponse = httpClient.send(createItemReq,
          HttpResponse.BodyHandlers.ofString());
      assertEquals(201, itemResponse.statusCode(), "Item creation should return 201");

      JsonNode itemNode = objectMapper.readTree(itemResponse.body());
      flowerItemIds[i] = itemNode.get("id").asInt();
    }

    // Step 3: Create a coupon for the flower shop
    String couponRequest = "{\"type\": \"totalprice\", \"storeId\": " + flowerShopStoreId
        + ", \"discountValue\": 15.0, \"isPercentage\": true, \"minimumPurchase\": 50.0}";
    HttpRequest createCouponReq = HttpRequest.newBuilder()
        .uri(URI.create(baseUrl + "/coupon"))
        .header("Content-Type", "application/json")
        .POST(HttpRequest.BodyPublishers.ofString(couponRequest))
        .build();

    HttpResponse<String> couponResponse = httpClient.send(createCouponReq,
        HttpResponse.BodyHandlers.ofString());
    assertEquals(201, couponResponse.statusCode(), "Coupon creation should return 201");

    JsonNode couponNode = objectMapper.readTree(couponResponse.body());
    flowerCouponId = couponNode.get("id").asInt();
    assertNotNull(flowerCouponId, "Coupon ID should not be null");
    assertTrue(flowerCouponId > 0, "Coupon ID should be positive");

    // Step 4: Find optimal coupon for a cart
    String cartRequest = "{\"itemIds\": [" + flowerItemIds[0] + ", " + flowerItemIds[1]
        + ", " + flowerItemIds[2] + "], \"storeId\": " + flowerShopStoreId + "}";
    HttpRequest optimalCouponReq = HttpRequest.newBuilder()
        .uri(URI.create(baseUrl + "/cart/optimal-coupon"))
        .header("Content-Type", "application/json")
        .POST(HttpRequest.BodyPublishers.ofString(cartRequest))
        .build();

    HttpResponse<String> optimalResponse = httpClient.send(optimalCouponReq,
        HttpResponse.BodyHandlers.ofString());
    assertEquals(200, optimalResponse.statusCode(), "Optimal coupon search should return 200");

    JsonNode optimalNode = objectMapper.readTree(optimalResponse.body());
    assertNotNull(optimalNode.get("coupon"), "Should find an optimal coupon");
    assertTrue(optimalNode.get("discount").asDouble() > 0, "Discount should be positive");

    // Step 5: Get store recommendations
    HttpRequest recommendationsReq = HttpRequest.newBuilder()
        .uri(URI.create(baseUrl + "/stores/optimal?category=flowers"))
        .GET()
        .build();

    HttpResponse<String> recommendationsResponse = httpClient.send(recommendationsReq,
        HttpResponse.BodyHandlers.ofString());
    assertEquals(200, recommendationsResponse.statusCode(),
        "Store recommendations should return 200");

    JsonNode recommendations = objectMapper.readTree(recommendationsResponse.body());
    assertTrue(recommendations.isArray(), "Recommendations should be an array");
    assertTrue(recommendations.size() > 0, "Should have at least one recommendation");
  }

  /**
   * Test 2: Multi-Client Isolation.
   * Verifies that multiple clients (stores) can use the service simultaneously
   * without interfering with each other's data.
   */
  @Test
  @Order(2)
  void testMultiClientIsolation() throws Exception {
    String baseUrl = "http://localhost:" + port;

    // Create a second store (Electronics Store)
    String storeRequest = "{\"name\": \"Tech World Electronics\"}";
    HttpRequest createStoreReq = HttpRequest.newBuilder()
        .uri(URI.create(baseUrl + "/store"))
        .header("Content-Type", "application/json")
        .POST(HttpRequest.BodyPublishers.ofString(storeRequest))
        .build();

    HttpResponse<String> storeResponse = httpClient.send(createStoreReq,
        HttpResponse.BodyHandlers.ofString());
    assertEquals(201, storeResponse.statusCode());

    JsonNode storeNode = objectMapper.readTree(storeResponse.body());
    electronicsStoreId = storeNode.get("id").asInt();

    // Add electronic products
    String[] electronicProducts = {
        "{\"name\": \"Laptop\", \"price\": 999.99, \"storeId\": " + electronicsStoreId
            + ", \"category\": \"electronics\"}",
        "{\"name\": \"Mouse\", \"price\": 29.99, \"storeId\": " + electronicsStoreId
            + ", \"category\": \"electronics\"}",
        "{\"name\": \"Keyboard\", \"price\": 79.99, \"storeId\": " + electronicsStoreId
            + ", \"category\": \"electronics\"}"
    };

    for (int i = 0; i < electronicProducts.length; i++) {
      HttpRequest createItemReq = HttpRequest.newBuilder()
          .uri(URI.create(baseUrl + "/item"))
          .header("Content-Type", "application/json")
          .POST(HttpRequest.BodyPublishers.ofString(electronicProducts[i]))
          .build();

      HttpResponse<String> itemResponse = httpClient.send(createItemReq,
          HttpResponse.BodyHandlers.ofString());
      assertEquals(201, itemResponse.statusCode());

      JsonNode itemNode = objectMapper.readTree(itemResponse.body());
      electronicItemIds[i] = itemNode.get("id").asInt();
    }

    // Create coupon for electronics store
    String couponRequest = "{\"type\": \"category\", \"storeId\": " + electronicsStoreId
        + ", \"discountValue\": 50.0, \"isPercentage\": false, \"category\": \"electronics\"}";
    HttpRequest createCouponReq = HttpRequest.newBuilder()
        .uri(URI.create(baseUrl + "/coupon"))
        .header("Content-Type", "application/json")
        .POST(HttpRequest.BodyPublishers.ofString(couponRequest))
        .build();

    HttpResponse<String> couponResponse = httpClient.send(createCouponReq,
        HttpResponse.BodyHandlers.ofString());
    assertEquals(201, couponResponse.statusCode());

    JsonNode couponNode = objectMapper.readTree(couponResponse.body());
    electronicsCouponId = couponNode.get("id").asInt();

    // Verify data isolation: Get items for flower shop
    HttpRequest getFlowerItemsReq = HttpRequest.newBuilder()
        .uri(URI.create(baseUrl + "/items/store/" + flowerShopStoreId))
        .GET()
        .build();

    HttpResponse<String> flowerItemsResponse = httpClient.send(getFlowerItemsReq,
        HttpResponse.BodyHandlers.ofString());
    JsonNode flowerItems = objectMapper.readTree(flowerItemsResponse.body());

    assertEquals(3, flowerItems.size(), "Flower shop should have exactly 3 items");
    for (JsonNode item : flowerItems) {
      assertEquals("flowers", item.get("category").asText(),
          "All items should be in flowers category");
    }

    // Verify data isolation: Get items for electronics store
    HttpRequest getElectronicItemsReq = HttpRequest.newBuilder()
        .uri(URI.create(baseUrl + "/items/store/" + electronicsStoreId))
        .GET()
        .build();

    HttpResponse<String> electronicItemsResponse = httpClient.send(getElectronicItemsReq,
        HttpResponse.BodyHandlers.ofString());
    JsonNode electronicItems = objectMapper.readTree(electronicItemsResponse.body());

    assertEquals(3, electronicItems.size(), "Electronics store should have exactly 3 items");
    for (JsonNode item : electronicItems) {
      assertEquals("electronics", item.get("category").asText(),
          "All items should be in electronics category");
    }

    // Verify coupon isolation
    HttpRequest getFlowerCouponsReq = HttpRequest.newBuilder()
        .uri(URI.create(baseUrl + "/coupons/store/" + flowerShopStoreId))
        .GET()
        .build();

    HttpResponse<String> flowerCouponsResponse = httpClient.send(getFlowerCouponsReq,
        HttpResponse.BodyHandlers.ofString());
    JsonNode flowerCoupons = objectMapper.readTree(flowerCouponsResponse.body());

    assertTrue(flowerCoupons.size() >= 1, "Flower shop should have at least 1 coupon");

    HttpRequest getElectronicCouponsReq = HttpRequest.newBuilder()
        .uri(URI.create(baseUrl + "/coupons/store/" + electronicsStoreId))
        .GET()
        .build();

    HttpResponse<String> electronicCouponsResponse = httpClient.send(getElectronicCouponsReq,
        HttpResponse.BodyHandlers.ofString());
    JsonNode electronicCoupons = objectMapper.readTree(electronicCouponsResponse.body());

    assertTrue(electronicCoupons.size() >= 1, "Electronics store should have at least 1 coupon");
  }

  /**
   * Test 3: Cart Optimization Workflow.
   * Tests the complete workflow of cart optimization and item suggestions.
   * (Cart not reaching the coupon threshold, need to add items to reach the threshold)
   */
  @Test
  @Order(3)
  void testCartOptimizationWorkflow() throws Exception {
    String baseUrl = "http://localhost:" + port;

    // Test cart optimization: Suggest items to reach coupon threshold
    String suggestRequest = "{\"itemIds\": [" + flowerItemIds[2] + "], \"storeId\": "
        + flowerShopStoreId + ", \"couponId\": " + flowerCouponId + "}";
    HttpRequest suggestItemsReq = HttpRequest.newBuilder()
        .uri(URI.create(baseUrl + "/cart/suggest-items"))
        .header("Content-Type", "application/json")
        .POST(HttpRequest.BodyPublishers.ofString(suggestRequest))
        .build();

    HttpResponse<String> suggestResponse = httpClient.send(suggestItemsReq,
        HttpResponse.BodyHandlers.ofString());
    assertEquals(200, suggestResponse.statusCode(), "Item suggestions should return 200");

    JsonNode suggestions = objectMapper.readTree(suggestResponse.body());
    if (suggestions.isArray() && suggestions.size() > 0) {
      // Verify that suggested items are from the correct store
      for (JsonNode item : suggestions) {
        assertEquals(flowerShopStoreId, item.get("storeId").asInt(),
            "Suggested items should be from the same store");
      }
    }

    // Test with cart that already meets threshold
    String fullCartRequest = "{\"itemIds\": [" + flowerItemIds[0] + ", " + flowerItemIds[1]
        + ", " + flowerItemIds[2] + "], \"storeId\": " + flowerShopStoreId + ", \"couponId\": "
        + flowerCouponId + "}";
    HttpRequest fullCartReq = HttpRequest.newBuilder()
        .uri(URI.create(baseUrl + "/cart/suggest-items"))
        .header("Content-Type", "application/json")
        .POST(HttpRequest.BodyPublishers.ofString(fullCartRequest))
        .build();

    HttpResponse<String> fullCartResponse = httpClient.send(fullCartReq,
        HttpResponse.BodyHandlers.ofString());
    assertEquals(200, fullCartResponse.statusCode());

    // The response should indicate cart already meets threshold
    String responseBody = fullCartResponse.body();
    assertTrue(responseBody.contains("threshold") || responseBody.equals("[]"),
        "Response should indicate threshold is met or return empty array");
  }

  /**
   * Test 4: Cross-Client Cart Operation Isolation.
   * Verifies that a client cannot use items from another client's store.
   */
  @Test
  @Order(4)
  void testCrossClientIsolation() throws Exception {
    String baseUrl = "http://localhost:" + port;

    // Try to find optimal coupon using flower items with electronics store
    String crossCartRequest = "{\"itemIds\": [" + flowerItemIds[0] + "], \"storeId\": "
        + electronicsStoreId + "}";
    HttpRequest crossCartReq = HttpRequest.newBuilder()
        .uri(URI.create(baseUrl + "/cart/optimal-coupon"))
        .header("Content-Type", "application/json")
        .POST(HttpRequest.BodyPublishers.ofString(crossCartRequest))
        .build();

    HttpResponse<String> crossCartResponse = httpClient.send(crossCartReq,
        HttpResponse.BodyHandlers.ofString());

    // The response should indicate no applicable coupon or an error
    // because the item belongs to a different store
    String responseBody = crossCartResponse.body();
    assertTrue(responseBody.contains("No applicable coupon")
        || crossCartResponse.statusCode() == 400,
        "Cross-store cart operations should not find applicable coupons");
  }

  /**
   * Test 5: Concurrent Client Operations.
   * Simulates multiple clients making requests simultaneously.
   */
  @Test
  @Order(5)
  void testConcurrentClientOperations() throws Exception {
    String baseUrl = "http://localhost:" + port;

    // Simulate concurrent requests from both stores
    Thread flowerThread = new Thread(() -> {
      try {
        for (int i = 0; i < 5; i++) {
          HttpRequest req = HttpRequest.newBuilder()
              .uri(URI.create(baseUrl + "/items/store/" + flowerShopStoreId))
              .GET()
              .build();
          httpClient.send(req, HttpResponse.BodyHandlers.ofString());
        }
      } catch (Exception e) {
        // Ignore for this test
      }
    });

    Thread electronicsThread = new Thread(() -> {
      try {
        for (int i = 0; i < 5; i++) {
          HttpRequest req = HttpRequest.newBuilder()
              .uri(URI.create(baseUrl + "/items/store/" + electronicsStoreId))
              .GET()
              .build();
          httpClient.send(req, HttpResponse.BodyHandlers.ofString());
        }
      } catch (Exception e) {
        // Ignore for this test
      }
    });

    flowerThread.start();
    electronicsThread.start();

    flowerThread.join();
    electronicsThread.join();

    // Verify data integrity after concurrent operations
    HttpRequest verifyFlowerReq = HttpRequest.newBuilder()
        .uri(URI.create(baseUrl + "/items/store/" + flowerShopStoreId))
        .GET()
        .build();

    HttpResponse<String> verifyResponse = httpClient.send(verifyFlowerReq,
        HttpResponse.BodyHandlers.ofString());
    assertEquals(200, verifyResponse.statusCode());

    JsonNode items = objectMapper.readTree(verifyResponse.body());
    assertEquals(3, items.size(), "Data should remain consistent after concurrent access");
  }
}
