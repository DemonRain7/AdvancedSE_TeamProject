package org.nullpointers.couponsystem;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

/**
 * Integration tests for the Coupon System API endpoints.
 */
@SpringBootTest(
    webEnvironment = WebEnvironment.RANDOM_PORT
)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles({
    "test"
})
class IntegrationTest {

  @LocalServerPort
  private int port;

  private static int staticPort;
  private static HttpClient httpClient;
  private static ObjectMapper objectMapper;
  private static int testStoreId;
  private static int testItemId;
  private static int testCouponId;
  private static int itemCouponId;
  private static int categoryCouponId;

  @BeforeAll
  static void setup() {
    httpClient = HttpClient.newHttpClient();
    objectMapper = new ObjectMapper();
  }

  @BeforeEach
  void setPort() {
    staticPort = this.port;
  }

  @Nested
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  class StoreValidationTests {

    @Test
    @Order(1)
    void createStoreWithEmptyNameShouldReturn400() throws Exception {
      String baseUrl = "http://localhost:" + IntegrationTest.staticPort;
      String request = "{\"name\": \"\"}";
      HttpRequest req = HttpRequest.newBuilder()
          .uri(URI.create(baseUrl + "/store"))
          .header("Content-Type", "application/json")
          .POST(BodyPublishers.ofString(request))
          .build();
      HttpResponse<String> response = 
          IntegrationTest.httpClient.send(req, BodyHandlers.ofString());
      Assertions.assertEquals(400, response.statusCode(), 
          "Empty store name should return 400");
      Assertions.assertTrue(response.body().contains("Store name cannot be empty"));
    }

    @Test
    @Order(2)
    void createStoreWithNullNameShouldReturn400() throws Exception {
      String baseUrl = "http://localhost:" + IntegrationTest.staticPort;
      String request = "{\"name\": null}";
      HttpRequest req = HttpRequest.newBuilder()
          .uri(URI.create(baseUrl + "/store"))
          .header("Content-Type", "application/json")
          .POST(BodyPublishers.ofString(request))
          .build();
      HttpResponse<String> response = 
          IntegrationTest.httpClient.send(req, BodyHandlers.ofString());
      Assertions.assertEquals(400, response.statusCode(), 
          "Null store name should return 400");
    }

    @Test
    @Order(3)
    void createStoreWithWhitespaceNameShouldReturn400() throws Exception {
      String baseUrl = "http://localhost:" + IntegrationTest.staticPort;
      String request = "{\"name\": \"   \"}";
      HttpRequest req = HttpRequest.newBuilder()
          .uri(URI.create(baseUrl + "/store"))
          .header("Content-Type", "application/json")
          .POST(BodyPublishers.ofString(request))
          .build();
      HttpResponse<String> response = 
          IntegrationTest.httpClient.send(req, BodyHandlers.ofString());
      Assertions.assertEquals(400, response.statusCode(), 
          "Whitespace-only store name should return 400");
    }

    @Test
    @Order(4)
    void getStoreNotFoundShouldReturn404() throws Exception {
      String baseUrl = "http://localhost:" + IntegrationTest.staticPort;
      HttpRequest req = HttpRequest.newBuilder()
          .uri(URI.create(baseUrl + "/store/99999"))
          .GET()
          .build();
      HttpResponse<String> response = 
          IntegrationTest.httpClient.send(req, BodyHandlers.ofString());
      Assertions.assertEquals(404, response.statusCode(), 
          "Non-existent store should return 404");
      Assertions.assertTrue(response.body().contains("Store not found"));
    }

    @Test
    @Order(5)
    void deleteStoreNotFoundShouldReturn404() throws Exception {
      String baseUrl = "http://localhost:" + IntegrationTest.staticPort;
      HttpRequest req = HttpRequest.newBuilder()
          .uri(URI.create(baseUrl + "/store/99999"))
          .DELETE()
          .build();
      HttpResponse<String> response = 
          IntegrationTest.httpClient.send(req, BodyHandlers.ofString());
      Assertions.assertEquals(404, response.statusCode(), 
          "Deleting non-existent store should return 404");
    }
  }

  @Nested
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  class ItemValidationTests {

    @Test
    @Order(1)
    void setupTestStore() throws Exception {
      String baseUrl = "http://localhost:" + IntegrationTest.staticPort;
      String request = "{\"name\": \"Item Validation Test Store\"}";
      HttpRequest req = HttpRequest.newBuilder()
          .uri(URI.create(baseUrl + "/store"))
          .header("Content-Type", "application/json")
          .POST(BodyPublishers.ofString(request))
          .build();
      HttpResponse<String> response = 
          IntegrationTest.httpClient.send(req, BodyHandlers.ofString());
      JsonNode node = IntegrationTest.objectMapper.readTree(response.body());
      IntegrationTest.testStoreId = node.get("id").asInt();
    }

    @Test
    @Order(2)
    void createItemWithEmptyNameShouldReturn400() throws Exception {
      String baseUrl = "http://localhost:" + IntegrationTest.staticPort;
      String request = "{\"name\": \"\", \"price\": 10.0, \"storeId\": " 
          + IntegrationTest.testStoreId + ", \"category\": \"test\"}";
      HttpRequest req = HttpRequest.newBuilder()
          .uri(URI.create(baseUrl + "/item"))
          .header("Content-Type", "application/json")
          .POST(BodyPublishers.ofString(request))
          .build();
      HttpResponse<String> response = 
          IntegrationTest.httpClient.send(req, BodyHandlers.ofString());
      Assertions.assertEquals(400, response.statusCode(), 
          "Empty item name should return 400");
      Assertions.assertTrue(response.body().contains("Item name cannot be empty"));
    }

    @Test
    @Order(3)
    void createItemWithNegativePriceShouldReturn400() throws Exception {
      String baseUrl = "http://localhost:" + IntegrationTest.staticPort;
      String request = "{\"name\": \"Test Item\", \"price\": -10.0, \"storeId\": " 
          + IntegrationTest.testStoreId + ", \"category\": \"test\"}";
      HttpRequest req = HttpRequest.newBuilder()
          .uri(URI.create(baseUrl + "/item"))
          .header("Content-Type", "application/json")
          .POST(BodyPublishers.ofString(request))
          .build();
      HttpResponse<String> response = 
          IntegrationTest.httpClient.send(req, BodyHandlers.ofString());
      Assertions.assertEquals(400, response.statusCode(), 
          "Negative price should return 400");
      Assertions.assertTrue(response.body().contains("Item price cannot be negative"));
    }

    @Test
    @Order(4)
    void createItemWithNonExistentStoreShouldReturn400() throws Exception {
      String baseUrl = "http://localhost:" + IntegrationTest.staticPort;
      String request = 
          "{\"name\": \"Test Item\", \"price\": 10.0, \"storeId\": 99999, " 
          + "\"category\": \"test\"}";
      HttpRequest req = HttpRequest.newBuilder()
          .uri(URI.create(baseUrl + "/item"))
          .header("Content-Type", "application/json")
          .POST(BodyPublishers.ofString(request))
          .build();
      HttpResponse<String> response = 
          IntegrationTest.httpClient.send(req, BodyHandlers.ofString());
      Assertions.assertEquals(400, response.statusCode(), 
          "Non-existent store should return 400");
      Assertions.assertTrue(response.body().contains("Store does not exist"));
    }

    @Test
    @Order(5)
    void getItemNotFoundShouldReturn404() throws Exception {
      String baseUrl = "http://localhost:" + IntegrationTest.staticPort;
      HttpRequest req = HttpRequest.newBuilder()
          .uri(URI.create(baseUrl + "/item/99999"))
          .GET()
          .build();
      HttpResponse<String> response = 
          IntegrationTest.httpClient.send(req, BodyHandlers.ofString());
      Assertions.assertEquals(404, response.statusCode(), 
          "Non-existent item should return 404");
      Assertions.assertTrue(response.body().contains("Item not found"));
    }

    @Test
    @Order(6)
    void deleteItemNotFoundShouldReturn404() throws Exception {
      String baseUrl = "http://localhost:" + IntegrationTest.staticPort;
      HttpRequest req = HttpRequest.newBuilder()
          .uri(URI.create(baseUrl + "/item/99999"))
          .DELETE()
          .build();
      HttpResponse<String> response = 
          IntegrationTest.httpClient.send(req, BodyHandlers.ofString());
      Assertions.assertEquals(404, response.statusCode(), 
          "Deleting non-existent item should return 404");
    }
  }

  @Nested
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  class CouponValidationTests {

    private static int couponTestStoreId;
    private static int couponTestItemId;

    @Test
    @Order(1)
    void setupTestData() throws Exception {
      String baseUrl = "http://localhost:" + IntegrationTest.staticPort;
      String storeRequest = "{\"name\": \"Coupon Validation Test Store\"}";
      HttpRequest storeReq = HttpRequest.newBuilder()
          .uri(URI.create(baseUrl + "/store"))
          .header("Content-Type", "application/json")
          .POST(BodyPublishers.ofString(storeRequest))
          .build();
      HttpResponse<String> storeResponse = 
          IntegrationTest.httpClient.send(storeReq, BodyHandlers.ofString());
      JsonNode storeNode = IntegrationTest.objectMapper.readTree(storeResponse.body());
      couponTestStoreId = storeNode.get("id").asInt();

      String itemRequest = "{\"name\": \"Test Item\", \"price\": 50.0, \"storeId\": " 
          + couponTestStoreId + ", \"category\": \"electronics\"}";
      HttpRequest itemReq = HttpRequest.newBuilder()
          .uri(URI.create(baseUrl + "/item"))
          .header("Content-Type", "application/json")
          .POST(BodyPublishers.ofString(itemRequest))
          .build();
      HttpResponse<String> itemResponse = 
          IntegrationTest.httpClient.send(itemReq, BodyHandlers.ofString());
      JsonNode itemNode = IntegrationTest.objectMapper.readTree(itemResponse.body());
      couponTestItemId = itemNode.get("id").asInt();
    }

    @Test
    @Order(2)
    void createCouponWithNegativeDiscountShouldReturn400() throws Exception {
      String baseUrl = "http://localhost:" + IntegrationTest.staticPort;
      String request = "{\"type\": \"totalprice\", \"storeId\": " + couponTestStoreId 
          + ", \"discountValue\": -10.0, \"isPercentage\": false, " 
          + "\"minimumPurchase\": 50.0}";
      HttpRequest req = HttpRequest.newBuilder()
          .uri(URI.create(baseUrl + "/coupon"))
          .header("Content-Type", "application/json")
          .POST(BodyPublishers.ofString(request))
          .build();
      HttpResponse<String> response = 
          IntegrationTest.httpClient.send(req, BodyHandlers.ofString());
      Assertions.assertEquals(400, response.statusCode(), 
          "Negative discount should return 400");
      Assertions.assertTrue(response.body().contains("Discount value cannot be negative"));
    }

    @Test
    @Order(3)
    void createCouponWithPercentageOver100ShouldReturn400() throws Exception {
      String baseUrl = "http://localhost:" + IntegrationTest.staticPort;
      String request = "{\"type\": \"totalprice\", \"storeId\": " + couponTestStoreId 
          + ", \"discountValue\": 150.0, \"isPercentage\": true, " 
          + "\"minimumPurchase\": 50.0}";
      HttpRequest req = HttpRequest.newBuilder()
          .uri(URI.create(baseUrl + "/coupon"))
          .header("Content-Type", "application/json")
          .POST(BodyPublishers.ofString(request))
          .build();
      HttpResponse<String> response = 
          IntegrationTest.httpClient.send(req, BodyHandlers.ofString());
      Assertions.assertEquals(400, response.statusCode(), 
          "Percentage > 100 should return 400");
      Assertions.assertTrue(
          response.body().contains("Percentage discount cannot exceed 100"));
    }

    @Test
    @Order(4)
    void createTotalPriceCouponWithNegativeMinimumShouldReturn400() throws Exception {
      String baseUrl = "http://localhost:" + IntegrationTest.staticPort;
      String request = "{\"type\": \"totalprice\", \"storeId\": " + couponTestStoreId 
          + ", \"discountValue\": 10.0, \"isPercentage\": false, " 
          + "\"minimumPurchase\": -50.0}";
      HttpRequest req = HttpRequest.newBuilder()
          .uri(URI.create(baseUrl + "/coupon"))
          .header("Content-Type", "application/json")
          .POST(BodyPublishers.ofString(request))
          .build();
      HttpResponse<String> response = 
          IntegrationTest.httpClient.send(req, BodyHandlers.ofString());
      Assertions.assertEquals(400, response.statusCode(), 
          "Negative minimum purchase should return 400");
      Assertions.assertTrue(
          response.body().contains("Minimum purchase cannot be negative"));
    }

    @Test
    @Order(5)
    void createCategoryCouponWithEmptyCategoryShouldReturn400() throws Exception {
      String baseUrl = "http://localhost:" + IntegrationTest.staticPort;
      String request = "{\"type\": \"category\", \"storeId\": " + couponTestStoreId 
          + ", \"discountValue\": 10.0, \"isPercentage\": false, \"category\": \"\"}";
      HttpRequest req = HttpRequest.newBuilder()
          .uri(URI.create(baseUrl + "/coupon"))
          .header("Content-Type", "application/json")
          .POST(BodyPublishers.ofString(request))
          .build();
      HttpResponse<String> response = 
          IntegrationTest.httpClient.send(req, BodyHandlers.ofString());
      Assertions.assertEquals(400, response.statusCode(), 
          "Empty category should return 400");
      Assertions.assertTrue(response.body().contains("Category cannot be empty"));
    }

    @Test
    @Order(6)
    void createCategoryCouponWithNullCategoryShouldReturn400() throws Exception {
      String baseUrl = "http://localhost:" + IntegrationTest.staticPort;
      String request = "{\"type\": \"category\", \"storeId\": " + couponTestStoreId 
          + ", \"discountValue\": 10.0, \"isPercentage\": false, \"category\": null}";
      HttpRequest req = HttpRequest.newBuilder()
          .uri(URI.create(baseUrl + "/coupon"))
          .header("Content-Type", "application/json")
          .POST(BodyPublishers.ofString(request))
          .build();
      HttpResponse<String> response = 
          IntegrationTest.httpClient.send(req, BodyHandlers.ofString());
      Assertions.assertEquals(400, response.statusCode(), 
          "Null category should return 400");
    }

    @Test
    @Order(7)
    void createItemCouponWithNonExistentItemShouldReturn400() throws Exception {
      String baseUrl = "http://localhost:" + IntegrationTest.staticPort;
      String request = "{\"type\": \"item\", \"storeId\": " + couponTestStoreId 
          + ", \"discountValue\": 10.0, \"isPercentage\": false, " 
          + "\"targetItemId\": 99999}";
      HttpRequest req = HttpRequest.newBuilder()
          .uri(URI.create(baseUrl + "/coupon"))
          .header("Content-Type", "application/json")
          .POST(BodyPublishers.ofString(request))
          .build();
      HttpResponse<String> response = 
          IntegrationTest.httpClient.send(req, BodyHandlers.ofString());
      Assertions.assertEquals(400, response.statusCode(), 
          "Non-existent target item should return 400");
      Assertions.assertTrue(response.body().contains("Target item does not exist"));
    }

    @Test
    @Order(8)
    void createCouponWithInvalidTypeShouldReturn400() throws Exception {
      String baseUrl = "http://localhost:" + IntegrationTest.staticPort;
      String request = "{\"type\": \"invalidtype\", \"storeId\": " + couponTestStoreId 
          + ", \"discountValue\": 10.0, \"isPercentage\": false}";
      HttpRequest req = HttpRequest.newBuilder()
          .uri(URI.create(baseUrl + "/coupon"))
          .header("Content-Type", "application/json")
          .POST(BodyPublishers.ofString(request))
          .build();
      HttpResponse<String> response = 
          IntegrationTest.httpClient.send(req, BodyHandlers.ofString());
      Assertions.assertEquals(400, response.statusCode(), 
          "Invalid coupon type should return 400");
      Assertions.assertTrue(response.body().contains("Invalid coupon type"));
    }

    @Test
    @Order(9)
    void createCouponWithNonExistentStoreShouldReturn400() throws Exception {
      String baseUrl = "http://localhost:" + IntegrationTest.staticPort;
      String request = "{\"type\": \"totalprice\", \"storeId\": 99999, " 
          + "\"discountValue\": 10.0, \"isPercentage\": false, " 
          + "\"minimumPurchase\": 50.0}";
      HttpRequest req = HttpRequest.newBuilder()
          .uri(URI.create(baseUrl + "/coupon"))
          .header("Content-Type", "application/json")
          .POST(BodyPublishers.ofString(request))
          .build();
      HttpResponse<String> response = 
          IntegrationTest.httpClient.send(req, BodyHandlers.ofString());
      Assertions.assertEquals(400, response.statusCode(), 
          "Non-existent store should return 400");
      Assertions.assertTrue(response.body().contains("Store does not exist"));
    }

    @Test
    @Order(10)
    void getCouponNotFoundShouldReturn404() throws Exception {
      String baseUrl = "http://localhost:" + IntegrationTest.staticPort;
      HttpRequest req = HttpRequest.newBuilder()
          .uri(URI.create(baseUrl + "/coupon/99999"))
          .GET()
          .build();
      HttpResponse<String> response = 
          IntegrationTest.httpClient.send(req, BodyHandlers.ofString());
      Assertions.assertEquals(404, response.statusCode(), 
          "Non-existent coupon should return 404");
      Assertions.assertTrue(response.body().contains("Coupon not found"));
    }

    @Test
    @Order(11)
    void deleteCouponNotFoundShouldReturn404() throws Exception {
      String baseUrl = "http://localhost:" + IntegrationTest.staticPort;
      HttpRequest req = HttpRequest.newBuilder()
          .uri(URI.create(baseUrl + "/coupon/99999"))
          .DELETE()
          .build();
      HttpResponse<String> response = 
          IntegrationTest.httpClient.send(req, BodyHandlers.ofString());
      Assertions.assertEquals(404, response.statusCode(), 
          "Deleting non-existent coupon should return 404");
    }
  }

  @Nested
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  class GetEndpointTests {

    private static int getTestStoreId;
    private static int getTestItemId;
    private static int getTestCouponId;

    @Test
    @Order(1)
    void setupTestData() throws Exception {
      String baseUrl = "http://localhost:" + IntegrationTest.staticPort;
      String storeRequest = "{\"name\": \"GET Test Store\"}";
      HttpRequest storeReq = HttpRequest.newBuilder()
          .uri(URI.create(baseUrl + "/store"))
          .header("Content-Type", "application/json")
          .POST(BodyPublishers.ofString(storeRequest))
          .build();
      HttpResponse<String> storeResponse = 
          IntegrationTest.httpClient.send(storeReq, BodyHandlers.ofString());
      JsonNode storeNode = IntegrationTest.objectMapper.readTree(storeResponse.body());
      getTestStoreId = storeNode.get("id").asInt();

      String itemRequest1 = "{\"name\": \"Searchable Laptop\", \"price\": 999.0, " 
          + "\"storeId\": " + getTestStoreId + ", \"category\": \"electronics\"}";
      HttpRequest itemReq1 = HttpRequest.newBuilder()
          .uri(URI.create(baseUrl + "/item"))
          .header("Content-Type", "application/json")
          .POST(BodyPublishers.ofString(itemRequest1))
          .build();
      HttpResponse<String> itemResponse1 = 
          IntegrationTest.httpClient.send(itemReq1, BodyHandlers.ofString());
      JsonNode itemNode = IntegrationTest.objectMapper.readTree(itemResponse1.body());
      getTestItemId = itemNode.get("id").asInt();

      String itemRequest2 = "{\"name\": \"Searchable Phone\", \"price\": 599.0, " 
          + "\"storeId\": " + getTestStoreId + ", \"category\": \"electronics\"}";
      HttpRequest itemReq2 = HttpRequest.newBuilder()
          .uri(URI.create(baseUrl + "/item"))
          .header("Content-Type", "application/json")
          .POST(BodyPublishers.ofString(itemRequest2))
          .build();
      IntegrationTest.httpClient.send(itemReq2, BodyHandlers.ofString());

      String couponRequest = "{\"type\": \"totalprice\", \"storeId\": " 
          + getTestStoreId + ", \"discountValue\": 10.0, \"isPercentage\": true, " 
          + "\"minimumPurchase\": 100.0}";
      HttpRequest couponReq = HttpRequest.newBuilder()
          .uri(URI.create(baseUrl + "/coupon"))
          .header("Content-Type", "application/json")
          .POST(BodyPublishers.ofString(couponRequest))
          .build();
      HttpResponse<String> couponResponse = 
          IntegrationTest.httpClient.send(couponReq, BodyHandlers.ofString());
      JsonNode couponNode = IntegrationTest.objectMapper.readTree(couponResponse.body());
      getTestCouponId = couponNode.get("id").asInt();
    }

    @Test
    @Order(2)
    void getExistingItemShouldReturn200() throws Exception {
      String baseUrl = "http://localhost:" + IntegrationTest.staticPort;
      HttpRequest req = HttpRequest.newBuilder()
          .uri(URI.create(baseUrl + "/item/" + getTestItemId))
          .GET()
          .build();
      HttpResponse<String> response = 
          IntegrationTest.httpClient.send(req, BodyHandlers.ofString());
      Assertions.assertEquals(200, response.statusCode());
      JsonNode node = IntegrationTest.objectMapper.readTree(response.body());
      Assertions.assertEquals(getTestItemId, node.get("id").asInt());
      Assertions.assertEquals("Searchable Laptop", node.get("name").asText());
    }

    @Test
    @Order(3)
    void getAllItemsShouldReturn200() throws Exception {
      String baseUrl = "http://localhost:" + IntegrationTest.staticPort;
      HttpRequest req = HttpRequest.newBuilder()
          .uri(URI.create(baseUrl + "/items"))
          .GET()
          .build();
      HttpResponse<String> response = 
          IntegrationTest.httpClient.send(req, BodyHandlers.ofString());
      Assertions.assertEquals(200, response.statusCode());
      JsonNode node = IntegrationTest.objectMapper.readTree(response.body());
      Assertions.assertTrue(node.isArray());
      Assertions.assertTrue(node.size() >= 2, "Should have at least 2 items");
    }

    @Test
    @Order(4)
    void getExistingCouponShouldReturn200() throws Exception {
      String baseUrl = "http://localhost:" + IntegrationTest.staticPort;
      HttpRequest req = HttpRequest.newBuilder()
          .uri(URI.create(baseUrl + "/coupon/" + getTestCouponId))
          .GET()
          .build();
      HttpResponse<String> response = 
          IntegrationTest.httpClient.send(req, BodyHandlers.ofString());
      Assertions.assertEquals(200, response.statusCode());
      JsonNode node = IntegrationTest.objectMapper.readTree(response.body());
      Assertions.assertEquals(getTestCouponId, node.get("id").asInt());
    }

    @Test
    @Order(5)
    void getAllCouponsShouldReturn200() throws Exception {
      String baseUrl = "http://localhost:" + IntegrationTest.staticPort;
      HttpRequest req = HttpRequest.newBuilder()
          .uri(URI.create(baseUrl + "/coupons"))
          .GET()
          .build();
      HttpResponse<String> response = 
          IntegrationTest.httpClient.send(req, BodyHandlers.ofString());
      Assertions.assertEquals(200, response.statusCode());
      JsonNode node = IntegrationTest.objectMapper.readTree(response.body());
      Assertions.assertTrue(node.isArray());
      Assertions.assertTrue(node.size() >= 1, "Should have at least 1 coupon");
    }

    @Test
    @Order(6)
    void searchItemsByKeywordShouldReturn200() throws Exception {
      String baseUrl = "http://localhost:" + IntegrationTest.staticPort;
      HttpRequest req = HttpRequest.newBuilder()
          .uri(URI.create(baseUrl + "/items/search?keyword=Searchable"))
          .GET()
          .build();
      HttpResponse<String> response = 
          IntegrationTest.httpClient.send(req, BodyHandlers.ofString());
      Assertions.assertEquals(200, response.statusCode());
      JsonNode node = IntegrationTest.objectMapper.readTree(response.body());
      Assertions.assertTrue(node.isArray());
      Assertions.assertTrue(node.size() >= 2, 
          "Should find at least 2 searchable items");
    }

    @Test
    @Order(7)
    void searchItemsByKeywordCaseInsensitiveShouldWork() throws Exception {
      String baseUrl = "http://localhost:" + IntegrationTest.staticPort;
      HttpRequest req = HttpRequest.newBuilder()
          .uri(URI.create(baseUrl + "/items/search?keyword=searchable"))
          .GET()
          .build();
      HttpResponse<String> response = 
          IntegrationTest.httpClient.send(req, BodyHandlers.ofString());
      Assertions.assertEquals(200, response.statusCode());
      JsonNode node = IntegrationTest.objectMapper.readTree(response.body());
      Assertions.assertTrue(node.isArray());
      Assertions.assertTrue(node.size() >= 2, 
          "Case-insensitive search should find items");
    }

    @Test
    @Order(8)
    void getItemsByCategoryShouldReturn200() throws Exception {
      String baseUrl = "http://localhost:" + IntegrationTest.staticPort;
      HttpRequest req = HttpRequest.newBuilder()
          .uri(URI.create(baseUrl + "/items/category/electronics"))
          .GET()
          .build();
      HttpResponse<String> response = 
          IntegrationTest.httpClient.send(req, BodyHandlers.ofString());
      Assertions.assertEquals(200, response.statusCode());
      JsonNode node = IntegrationTest.objectMapper.readTree(response.body());
      Assertions.assertTrue(node.isArray());
      Assertions.assertTrue(node.size() >= 2, "Should find electronics items");

      for (JsonNode item : node) {
        Assertions.assertEquals("electronics", 
            item.get("category").asText().toLowerCase());
      }
    }

    @Test
    @Order(9)
    void searchItemsWithNoMatchShouldReturnEmptyArray() throws Exception {
      String baseUrl = "http://localhost:" + IntegrationTest.staticPort;
      HttpRequest req = HttpRequest.newBuilder()
          .uri(URI.create(baseUrl + "/items/search?keyword=nonexistentxyz123"))
          .GET()
          .build();
      HttpResponse<String> response = 
          IntegrationTest.httpClient.send(req, BodyHandlers.ofString());
      Assertions.assertEquals(200, response.statusCode());
      JsonNode node = IntegrationTest.objectMapper.readTree(response.body());
      Assertions.assertTrue(node.isArray());
      Assertions.assertEquals(0, node.size(), 
          "Should return empty array for no matches");
    }
  }

  @Nested
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  class ItemCouponWorkflowTests {

    private static int itemCouponStoreId;
    private static int targetItemId;
    private static int otherItemId;
    private static int itemCouponTestId;

    @Test
    @Order(1)
    void setupItemCouponTestData() throws Exception {
      String baseUrl = "http://localhost:" + IntegrationTest.staticPort;
      String storeRequest = "{\"name\": \"ItemCoupon Test Store\"}";
      HttpRequest storeReq = HttpRequest.newBuilder()
          .uri(URI.create(baseUrl + "/store"))
          .header("Content-Type", "application/json")
          .POST(BodyPublishers.ofString(storeRequest))
          .build();
      HttpResponse<String> storeResponse = 
          IntegrationTest.httpClient.send(storeReq, BodyHandlers.ofString());
      JsonNode storeNode = IntegrationTest.objectMapper.readTree(storeResponse.body());
      itemCouponStoreId = storeNode.get("id").asInt();

      String targetItemRequest = 
          "{\"name\": \"Premium Headphones\", \"price\": 200.0, \"storeId\": " 
          + itemCouponStoreId + ", \"category\": \"audio\"}";
      HttpRequest targetReq = HttpRequest.newBuilder()
          .uri(URI.create(baseUrl + "/item"))
          .header("Content-Type", "application/json")
          .POST(BodyPublishers.ofString(targetItemRequest))
          .build();
      HttpResponse<String> targetResponse = 
          IntegrationTest.httpClient.send(targetReq, BodyHandlers.ofString());
      JsonNode targetNode = IntegrationTest.objectMapper.readTree(targetResponse.body());
      targetItemId = targetNode.get("id").asInt();

      String otherItemRequest = 
          "{\"name\": \"Basic Speaker\", \"price\": 50.0, \"storeId\": " 
          + itemCouponStoreId + ", \"category\": \"audio\"}";
      HttpRequest otherReq = HttpRequest.newBuilder()
          .uri(URI.create(baseUrl + "/item"))
          .header("Content-Type", "application/json")
          .POST(BodyPublishers.ofString(otherItemRequest))
          .build();
      HttpResponse<String> otherResponse = 
          IntegrationTest.httpClient.send(otherReq, BodyHandlers.ofString());
      JsonNode otherNode = IntegrationTest.objectMapper.readTree(otherResponse.body());
      otherItemId = otherNode.get("id").asInt();
    }

    @Test
    @Order(2)
    void createItemCouponShouldSucceed() throws Exception {
      String baseUrl = "http://localhost:" + IntegrationTest.staticPort;
      String request = "{\"type\": \"item\", \"storeId\": " + itemCouponStoreId 
          + ", \"discountValue\": 25.0, \"isPercentage\": true, \"targetItemId\": " 
          + targetItemId + "}";
      HttpRequest req = HttpRequest.newBuilder()
          .uri(URI.create(baseUrl + "/coupon"))
          .header("Content-Type", "application/json")
          .POST(BodyPublishers.ofString(request))
          .build();
      HttpResponse<String> response = 
          IntegrationTest.httpClient.send(req, BodyHandlers.ofString());
      Assertions.assertEquals(201, response.statusCode());
      JsonNode node = IntegrationTest.objectMapper.readTree(response.body());
      itemCouponTestId = node.get("id").asInt();
      Assertions.assertTrue(itemCouponTestId > 0);
    }

    @Test
    @Order(3)
    void findOptimalCouponWithTargetItemShouldReturnItemCoupon() throws Exception {
      String baseUrl = "http://localhost:" + IntegrationTest.staticPort;
      String request = "{\"itemIds\": [" + targetItemId + "], \"storeId\": " 
          + itemCouponStoreId + "}";
      HttpRequest req = HttpRequest.newBuilder()
          .uri(URI.create(baseUrl + "/cart/optimal-coupon"))
          .header("Content-Type", "application/json")
          .POST(BodyPublishers.ofString(request))
          .build();
      HttpResponse<String> response = 
          IntegrationTest.httpClient.send(req, BodyHandlers.ofString());
      Assertions.assertEquals(200, response.statusCode());
      JsonNode node = IntegrationTest.objectMapper.readTree(response.body());
      Assertions.assertNotNull(node.get("coupon"));
      Assertions.assertEquals(50.0, node.get("discount").asDouble(), 0.01);
    }

    @Test
    @Order(4)
    void findOptimalCouponWithNonTargetItemShouldReturnNoCoupon() throws Exception {
      String baseUrl = "http://localhost:" + IntegrationTest.staticPort;
      String request = "{\"itemIds\": [" + otherItemId + "], \"storeId\": " 
          + itemCouponStoreId + "}";
      HttpRequest req = HttpRequest.newBuilder()
          .uri(URI.create(baseUrl + "/cart/optimal-coupon"))
          .header("Content-Type", "application/json")
          .POST(BodyPublishers.ofString(request))
          .build();
      HttpResponse<String> response = 
          IntegrationTest.httpClient.send(req, BodyHandlers.ofString());
      Assertions.assertEquals(200, response.statusCode());
      Assertions.assertTrue(response.body().contains("No applicable coupon"));
    }

    @Test
    @Order(5)
    void findOptimalCouponWithMixedCartShouldApplyToTargetOnly() throws Exception {
      String baseUrl = "http://localhost:" + IntegrationTest.staticPort;
      String request = "{\"itemIds\": [" + targetItemId + ", " + otherItemId 
          + "], \"storeId\": " + itemCouponStoreId + "}";
      HttpRequest req = HttpRequest.newBuilder()
          .uri(URI.create(baseUrl + "/cart/optimal-coupon"))
          .header("Content-Type", "application/json")
          .POST(BodyPublishers.ofString(request))
          .build();
      HttpResponse<String> response = 
          IntegrationTest.httpClient.send(req, BodyHandlers.ofString());
      Assertions.assertEquals(200, response.statusCode());
      JsonNode node = IntegrationTest.objectMapper.readTree(response.body());
      Assertions.assertNotNull(node.get("coupon"));
      Assertions.assertEquals(50.0, node.get("discount").asDouble(), 0.01);
    }
  }

  @Nested
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  class BoundaryConditionTests {

    private static int boundaryStoreId;
    private static int item49Id;
    private static int item50Id;
    private static int item51Id;
    private static int thresholdCouponId;

    @Test
    @Order(1)
    void setupBoundaryTestData() throws Exception {
      String baseUrl = "http://localhost:" + IntegrationTest.staticPort;
      String storeRequest = "{\"name\": \"Boundary Test Store\"}";
      HttpRequest storeReq = HttpRequest.newBuilder()
          .uri(URI.create(baseUrl + "/store"))
          .header("Content-Type", "application/json")
          .POST(BodyPublishers.ofString(storeRequest))
          .build();
      HttpResponse<String> storeResponse = 
          IntegrationTest.httpClient.send(storeReq, BodyHandlers.ofString());
      JsonNode storeNode = IntegrationTest.objectMapper.readTree(storeResponse.body());
      boundaryStoreId = storeNode.get("id").asInt();

      String item49Request = "{\"name\": \"Item49\", \"price\": 49.0, \"storeId\": " 
          + boundaryStoreId + ", \"category\": \"test\"}";
      HttpResponse<String> item49Response = IntegrationTest.httpClient.send(
          HttpRequest.newBuilder()
              .uri(URI.create(baseUrl + "/item"))
              .header("Content-Type", "application/json")
              .POST(BodyPublishers.ofString(item49Request))
              .build(),
          BodyHandlers.ofString());
      item49Id = IntegrationTest.objectMapper.readTree(item49Response.body())
          .get("id").asInt();

      String item50Request = "{\"name\": \"Item50\", \"price\": 50.0, \"storeId\": " 
          + boundaryStoreId + ", \"category\": \"test\"}";
      HttpResponse<String> item50Response = IntegrationTest.httpClient.send(
          HttpRequest.newBuilder()
              .uri(URI.create(baseUrl + "/item"))
              .header("Content-Type", "application/json")
              .POST(BodyPublishers.ofString(item50Request))
              .build(),
          BodyHandlers.ofString());
      item50Id = IntegrationTest.objectMapper.readTree(item50Response.body())
          .get("id").asInt();

      String item51Request = "{\"name\": \"Item51\", \"price\": 51.0, \"storeId\": " 
          + boundaryStoreId + ", \"category\": \"test\"}";
      HttpResponse<String> item51Response = IntegrationTest.httpClient.send(
          HttpRequest.newBuilder()
              .uri(URI.create(baseUrl + "/item"))
              .header("Content-Type", "application/json")
              .POST(BodyPublishers.ofString(item51Request))
              .build(),
          BodyHandlers.ofString());
      item51Id = IntegrationTest.objectMapper.readTree(item51Response.body())
          .get("id").asInt();

      String couponRequest = "{\"type\": \"totalprice\", \"storeId\": " 
          + boundaryStoreId + ", \"discountValue\": 10.0, \"isPercentage\": true, " 
          + "\"minimumPurchase\": 50.0}";
      HttpResponse<String> couponResponse = IntegrationTest.httpClient.send(
          HttpRequest.newBuilder()
              .uri(URI.create(baseUrl + "/coupon"))
              .header("Content-Type", "application/json")
              .POST(BodyPublishers.ofString(couponRequest))
              .build(),
          BodyHandlers.ofString());
      thresholdCouponId = IntegrationTest.objectMapper.readTree(couponResponse.body())
          .get("id").asInt();
    }

    @Test
    @Order(2)
    void cartJustBelowThresholdShouldNotApplyCoupon() throws Exception {
      String baseUrl = "http://localhost:" + IntegrationTest.staticPort;
      String request = "{\"itemIds\": [" + item49Id + "], \"storeId\": " 
          + boundaryStoreId + "}";
      HttpRequest req = HttpRequest.newBuilder()
          .uri(URI.create(baseUrl + "/cart/optimal-coupon"))
          .header("Content-Type", "application/json")
          .POST(BodyPublishers.ofString(request))
          .build();
      HttpResponse<String> response = 
          IntegrationTest.httpClient.send(req, BodyHandlers.ofString());
      Assertions.assertEquals(200, response.statusCode());
      Assertions.assertTrue(response.body().contains("No applicable coupon"), 
          "Cart at $49 should not meet $50 threshold");
    }

    @Test
    @Order(3)
    void cartExactlyAtThresholdShouldApplyCoupon() throws Exception {
      String baseUrl = "http://localhost:" + IntegrationTest.staticPort;
      String request = "{\"itemIds\": [" + item50Id + "], \"storeId\": " 
          + boundaryStoreId + "}";
      HttpRequest req = HttpRequest.newBuilder()
          .uri(URI.create(baseUrl + "/cart/optimal-coupon"))
          .header("Content-Type", "application/json")
          .POST(BodyPublishers.ofString(request))
          .build();
      HttpResponse<String> response = 
          IntegrationTest.httpClient.send(req, BodyHandlers.ofString());
      Assertions.assertEquals(200, response.statusCode());
      JsonNode node = IntegrationTest.objectMapper.readTree(response.body());
      Assertions.assertNotNull(node.get("coupon"), 
          "Cart at exactly $50 should meet threshold");
      Assertions.assertEquals(5.0, node.get("discount").asDouble(), 0.01);
    }

    @Test
    @Order(4)
    void cartJustAboveThresholdShouldApplyCoupon() throws Exception {
      String baseUrl = "http://localhost:" + IntegrationTest.staticPort;
      String request = "{\"itemIds\": [" + item51Id + "], \"storeId\": " 
          + boundaryStoreId + "}";
      HttpRequest req = HttpRequest.newBuilder()
          .uri(URI.create(baseUrl + "/cart/optimal-coupon"))
          .header("Content-Type", "application/json")
          .POST(BodyPublishers.ofString(request))
          .build();
      HttpResponse<String> response = 
          IntegrationTest.httpClient.send(req, BodyHandlers.ofString());
      Assertions.assertEquals(200, response.statusCode());
      JsonNode node = IntegrationTest.objectMapper.readTree(response.body());
      Assertions.assertNotNull(node.get("coupon"), 
          "Cart at $51 should exceed threshold");
      Assertions.assertEquals(5.1, node.get("discount").asDouble(), 0.01);
    }

    @Test
    @Order(5)
    void emptyCartShouldReturn400() throws Exception {
      String baseUrl = "http://localhost:" + IntegrationTest.staticPort;
      String request = "{\"itemIds\": [], \"storeId\": " + boundaryStoreId + "}";
      HttpRequest req = HttpRequest.newBuilder()
          .uri(URI.create(baseUrl + "/cart/optimal-coupon"))
          .header("Content-Type", "application/json")
          .POST(BodyPublishers.ofString(request))
          .build();
      HttpResponse<String> response = 
          IntegrationTest.httpClient.send(req, BodyHandlers.ofString());
      Assertions.assertEquals(400, response.statusCode());
      Assertions.assertTrue(response.body().contains("Cart cannot be empty"));
    }

    @Test
    @Order(6)
    void cartWithNonExistentItemShouldReturn400() throws Exception {
      String baseUrl = "http://localhost:" + IntegrationTest.staticPort;
      String request = "{\"itemIds\": [99999], \"storeId\": " + boundaryStoreId + "}";
      HttpRequest req = HttpRequest.newBuilder()
          .uri(URI.create(baseUrl + "/cart/optimal-coupon"))
          .header("Content-Type", "application/json")
          .POST(BodyPublishers.ofString(request))
          .build();
      HttpResponse<String> response = 
          IntegrationTest.httpClient.send(req, BodyHandlers.ofString());
      Assertions.assertEquals(400, response.statusCode());
      Assertions.assertTrue(response.body().contains("does not exist"));
    }

    @Test
    @Order(7)
    void findOptimalStoresWithNoParametersShouldReturn400() throws Exception {
      String baseUrl = "http://localhost:" + IntegrationTest.staticPort;
      HttpRequest req = HttpRequest.newBuilder()
          .uri(URI.create(baseUrl + "/stores/optimal"))
          .GET()
          .build();
      HttpResponse<String> response = 
          IntegrationTest.httpClient.send(req, BodyHandlers.ofString());
      Assertions.assertEquals(400, response.statusCode());
      Assertions.assertTrue(response.body()
          .contains("Either keyword or category must be provided"));
    }

    @Test
    @Order(8)
    void findOptimalStoresWithNoMatchShouldReturnMessage() throws Exception {
      String baseUrl = "http://localhost:" + IntegrationTest.staticPort;
      HttpRequest req = HttpRequest.newBuilder()
          .uri(URI.create(baseUrl + "/stores/optimal?keyword=nonexistentxyz123"))
          .GET()
          .build();
      HttpResponse<String> response = 
          IntegrationTest.httpClient.send(req, BodyHandlers.ofString());
      Assertions.assertEquals(200, response.statusCode());
      Assertions.assertTrue(response.body().contains("No matching items"));
    }
  }

  @Nested
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  class SuggestItemsValidationTests {

    private static int suggestStoreId;
    private static int suggestItemId;
    private static int suggestCouponId;

    @Test
    @Order(1)
    void setupSuggestTestData() throws Exception {
      String baseUrl = "http://localhost:" + IntegrationTest.staticPort;
      String storeRequest = "{\"name\": \"Suggest Test Store\"}";
      HttpResponse<String> storeResponse = IntegrationTest.httpClient.send(
          HttpRequest.newBuilder()
              .uri(URI.create(baseUrl + "/store"))
              .header("Content-Type", "application/json")
              .POST(BodyPublishers.ofString(storeRequest))
              .build(),
          BodyHandlers.ofString());
      suggestStoreId = IntegrationTest.objectMapper.readTree(storeResponse.body())
          .get("id").asInt();

      String itemRequest = "{\"name\": \"Suggest Item\", \"price\": 30.0, " 
          + "\"storeId\": " + suggestStoreId + ", \"category\": \"test\"}";
      HttpResponse<String> itemResponse = IntegrationTest.httpClient.send(
          HttpRequest.newBuilder()
              .uri(URI.create(baseUrl + "/item"))
              .header("Content-Type", "application/json")
              .POST(BodyPublishers.ofString(itemRequest))
              .build(),
          BodyHandlers.ofString());
      suggestItemId = IntegrationTest.objectMapper.readTree(itemResponse.body())
          .get("id").asInt();

      String couponRequest = "{\"type\": \"totalprice\", \"storeId\": " 
          + suggestStoreId + ", \"discountValue\": 10.0, \"isPercentage\": true, " 
          + "\"minimumPurchase\": 100.0}";
      HttpResponse<String> couponResponse = IntegrationTest.httpClient.send(
          HttpRequest.newBuilder()
              .uri(URI.create(baseUrl + "/coupon"))
              .header("Content-Type", "application/json")
              .POST(BodyPublishers.ofString(couponRequest))
              .build(),
          BodyHandlers.ofString());
      suggestCouponId = IntegrationTest.objectMapper.readTree(couponResponse.body())
          .get("id").asInt();
    }

    @Test
    @Order(2)
    void suggestItemsWithEmptyCartShouldReturn400() throws Exception {
      String baseUrl = "http://localhost:" + IntegrationTest.staticPort;
      String request = "{\"itemIds\": [], \"storeId\": " + suggestStoreId 
          + ", \"couponId\": " + suggestCouponId + "}";
      HttpRequest req = HttpRequest.newBuilder()
          .uri(URI.create(baseUrl + "/cart/suggest-items"))
          .header("Content-Type", "application/json")
          .POST(BodyPublishers.ofString(request))
          .build();
      HttpResponse<String> response = 
          IntegrationTest.httpClient.send(req, BodyHandlers.ofString());
      Assertions.assertEquals(400, response.statusCode());
      Assertions.assertTrue(response.body().contains("Cart cannot be empty"));
    }

    @Test
    @Order(3)
    void suggestItemsWithNonExistentItemShouldReturn400() throws Exception {
      String baseUrl = "http://localhost:" + IntegrationTest.staticPort;
      String request = "{\"itemIds\": [99999], \"storeId\": " + suggestStoreId 
          + ", \"couponId\": " + suggestCouponId + "}";
      HttpRequest req = HttpRequest.newBuilder()
          .uri(URI.create(baseUrl + "/cart/suggest-items"))
          .header("Content-Type", "application/json")
          .POST(BodyPublishers.ofString(request))
          .build();
      HttpResponse<String> response = 
          IntegrationTest.httpClient.send(req, BodyHandlers.ofString());
      Assertions.assertEquals(400, response.statusCode());
      Assertions.assertTrue(response.body().contains("does not exist"));
    }

    @Test
    @Order(4)
    void suggestItemsWithNonExistentCouponShouldReturn200WithMessage() throws Exception {
      String baseUrl = "http://localhost:" + IntegrationTest.staticPort;
      String request = "{\"itemIds\": [" + suggestItemId + "], \"storeId\": " 
          + suggestStoreId + ", \"couponId\": 99999}";
      HttpRequest req = HttpRequest.newBuilder()
          .uri(URI.create(baseUrl + "/cart/suggest-items"))
          .header("Content-Type", "application/json")
          .POST(BodyPublishers.ofString(request))
          .build();
      HttpResponse<String> response = 
          IntegrationTest.httpClient.send(req, BodyHandlers.ofString());
      Assertions.assertEquals(200, response.statusCode());
      Assertions.assertTrue(response.body().contains("threshold") 
          || response.body().contains("invalid"));
    }
  }

  @Nested
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  class SingleItemCartTests {

    private static int singleItemStoreId;
    private static int singleItemId;

    @Test
    @Order(1)
    void setupSingleItemTestData() throws Exception {
      String baseUrl = "http://localhost:" + IntegrationTest.staticPort;
      String storeRequest = "{\"name\": \"Single Item Test Store\"}";
      HttpResponse<String> storeResponse = IntegrationTest.httpClient.send(
          HttpRequest.newBuilder()
              .uri(URI.create(baseUrl + "/store"))
              .header("Content-Type", "application/json")
              .POST(BodyPublishers.ofString(storeRequest))
              .build(),
          BodyHandlers.ofString());
      singleItemStoreId = IntegrationTest.objectMapper.readTree(storeResponse.body())
          .get("id").asInt();

      String itemRequest = 
          "{\"name\": \"Expensive Single Item\", \"price\": 500.0, \"storeId\": " 
          + singleItemStoreId + ", \"category\": \"luxury\"}";
      HttpResponse<String> itemResponse = IntegrationTest.httpClient.send(
          HttpRequest.newBuilder()
              .uri(URI.create(baseUrl + "/item"))
              .header("Content-Type", "application/json")
              .POST(BodyPublishers.ofString(itemRequest))
              .build(),
          BodyHandlers.ofString());
      singleItemId = IntegrationTest.objectMapper.readTree(itemResponse.body())
          .get("id").asInt();

      String couponRequest = "{\"type\": \"totalprice\", \"storeId\": " 
          + singleItemStoreId + ", \"discountValue\": 50.0, \"isPercentage\": false, " 
          + "\"minimumPurchase\": 100.0}";
      IntegrationTest.httpClient.send(
          HttpRequest.newBuilder()
              .uri(URI.create(baseUrl + "/coupon"))
              .header("Content-Type", "application/json")
              .POST(BodyPublishers.ofString(couponRequest))
              .build(),
          BodyHandlers.ofString());
    }

    @Test
    @Order(2)
    void singleItemCartShouldFindOptimalCoupon() throws Exception {
      String baseUrl = "http://localhost:" + IntegrationTest.staticPort;
      String request = "{\"itemIds\": [" + singleItemId + "], \"storeId\": " 
          + singleItemStoreId + "}";
      HttpRequest req = HttpRequest.newBuilder()
          .uri(URI.create(baseUrl + "/cart/optimal-coupon"))
          .header("Content-Type", "application/json")
          .POST(BodyPublishers.ofString(request))
          .build();
      HttpResponse<String> response = 
          IntegrationTest.httpClient.send(req, BodyHandlers.ofString());
      Assertions.assertEquals(200, response.statusCode());
      JsonNode node = IntegrationTest.objectMapper.readTree(response.body());
      Assertions.assertNotNull(node.get("coupon"), 
          "Single high-value item should qualify for coupon");
      Assertions.assertEquals(50.0, node.get("discount").asDouble(), 0.01);
    }
  }

  @Nested
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  class ZeroPriceTests {

    private static int zeroPriceStoreId;

    @Test
    @Order(1)
    void createItemWithZeroPriceShouldSucceed() throws Exception {
      String baseUrl = "http://localhost:" + IntegrationTest.staticPort;
      String storeRequest = "{\"name\": \"Zero Price Test Store\"}";
      HttpResponse<String> storeResponse = IntegrationTest.httpClient.send(
          HttpRequest.newBuilder()
              .uri(URI.create(baseUrl + "/store"))
              .header("Content-Type", "application/json")
              .POST(BodyPublishers.ofString(storeRequest))
              .build(),
          BodyHandlers.ofString());
      zeroPriceStoreId = IntegrationTest.objectMapper.readTree(storeResponse.body())
          .get("id").asInt();

      String itemRequest = "{\"name\": \"Free Sample\", \"price\": 0.0, " 
          + "\"storeId\": " + zeroPriceStoreId + ", \"category\": \"samples\"}";
      HttpRequest req = HttpRequest.newBuilder()
          .uri(URI.create(baseUrl + "/item"))
          .header("Content-Type", "application/json")
          .POST(BodyPublishers.ofString(itemRequest))
          .build();
      HttpResponse<String> response = 
          IntegrationTest.httpClient.send(req, BodyHandlers.ofString());
      Assertions.assertEquals(201, response.statusCode(), 
          "Zero price item should be allowed");
      JsonNode node = IntegrationTest.objectMapper.readTree(response.body());
      Assertions.assertEquals(0.0, node.get("price").asDouble(), 0.001);
    }
  }

  @Nested
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  class CouponComparisonTests {

    private static int comparisonStoreId;
    private static int comparisonItem1Id;
    private static int comparisonItem2Id;

    @Test
    @Order(1)
    void setupComparisonTestData() throws Exception {
      String baseUrl = "http://localhost:" + IntegrationTest.staticPort;
      String storeRequest = "{\"name\": \"Coupon Comparison Store\"}";
      HttpResponse<String> storeResponse = IntegrationTest.httpClient.send(
          HttpRequest.newBuilder()
              .uri(URI.create(baseUrl + "/store"))
              .header("Content-Type", "application/json")
              .POST(BodyPublishers.ofString(storeRequest))
              .build(),
          BodyHandlers.ofString());
      comparisonStoreId = IntegrationTest.objectMapper.readTree(storeResponse.body())
          .get("id").asInt();

      String item1Request = "{\"name\": \"Book A\", \"price\": 100.0, " 
          + "\"storeId\": " + comparisonStoreId + ", \"category\": \"books\"}";
      HttpResponse<String> item1Response = IntegrationTest.httpClient.send(
          HttpRequest.newBuilder()
              .uri(URI.create(baseUrl + "/item"))
              .header("Content-Type", "application/json")
              .POST(BodyPublishers.ofString(item1Request))
              .build(),
          BodyHandlers.ofString());
      comparisonItem1Id = IntegrationTest.objectMapper.readTree(item1Response.body())
          .get("id").asInt();

      String item2Request = "{\"name\": \"Book B\", \"price\": 50.0, " 
          + "\"storeId\": " + comparisonStoreId + ", \"category\": \"books\"}";
      HttpResponse<String> item2Response = IntegrationTest.httpClient.send(
          HttpRequest.newBuilder()
              .uri(URI.create(baseUrl + "/item"))
              .header("Content-Type", "application/json")
              .POST(BodyPublishers.ofString(item2Request))
              .build(),
          BodyHandlers.ofString());
      comparisonItem2Id = IntegrationTest.objectMapper.readTree(item2Response.body())
          .get("id").asInt();

      String totalCouponRequest = "{\"type\": \"totalprice\", \"storeId\": " 
          + comparisonStoreId + ", \"discountValue\": 10.0, \"isPercentage\": true, " 
          + "\"minimumPurchase\": 100.0}";
      IntegrationTest.httpClient.send(
          HttpRequest.newBuilder()
              .uri(URI.create(baseUrl + "/coupon"))
              .header("Content-Type", "application/json")
              .POST(BodyPublishers.ofString(totalCouponRequest))
              .build(),
          BodyHandlers.ofString());

      String categoryCouponRequest = "{\"type\": \"category\", \"storeId\": " 
          + comparisonStoreId + ", \"discountValue\": 20.0, \"isPercentage\": true, " 
          + "\"category\": \"books\"}";
      IntegrationTest.httpClient.send(
          HttpRequest.newBuilder()
              .uri(URI.create(baseUrl + "/coupon"))
              .header("Content-Type", "application/json")
              .POST(BodyPublishers.ofString(categoryCouponRequest))
              .build(),
          BodyHandlers.ofString());

      String itemCouponRequest = "{\"type\": \"item\", \"storeId\": " 
          + comparisonStoreId + ", \"discountValue\": 15.0, \"isPercentage\": false, " 
          + "\"targetItemId\": " + comparisonItem1Id + "}";
      IntegrationTest.httpClient.send(
          HttpRequest.newBuilder()
              .uri(URI.create(baseUrl + "/coupon"))
              .header("Content-Type", "application/json")
              .POST(BodyPublishers.ofString(itemCouponRequest))
              .build(),
          BodyHandlers.ofString());
    }

    @Test
    @Order(2)
    void shouldSelectBestCouponAmongMultipleTypes() throws Exception {
      String baseUrl = "http://localhost:" + IntegrationTest.staticPort;
      String request = "{\"itemIds\": [" + comparisonItem1Id + ", " 
          + comparisonItem2Id + "], \"storeId\": " + comparisonStoreId + "}";
      HttpRequest req = HttpRequest.newBuilder()
          .uri(URI.create(baseUrl + "/cart/optimal-coupon"))
          .header("Content-Type", "application/json")
          .POST(BodyPublishers.ofString(request))
          .build();
      HttpResponse<String> response = 
          IntegrationTest.httpClient.send(req, BodyHandlers.ofString());
      Assertions.assertEquals(200, response.statusCode());
      JsonNode node = IntegrationTest.objectMapper.readTree(response.body());
      Assertions.assertNotNull(node.get("coupon"));
      Assertions.assertEquals(30.0, node.get("discount").asDouble(), 0.01);
    }
  }
}
