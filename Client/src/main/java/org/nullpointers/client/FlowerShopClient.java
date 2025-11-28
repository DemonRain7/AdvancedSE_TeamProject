package org.nullpointers.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.util.Scanner;

/**
 * Flower Shop Client - A demonstration client for a local flower shop
 * that uses the Coupon Management System.
 *
 * <p>This client demonstrates how a local store (flower shop) can use
 * the centralized coupon management service to:
 * - Register their store
 * - Add products (flowers, bouquets, etc.)
 * - Create promotional coupons
 * - Find optimal coupons for customer carts
 * - Get recommendations for best-priced items
 */
public class FlowerShopClient {
  private static final String DEFAULT_SERVICE_URL = "http://localhost:8080";
  private final ApiClient apiClient;
  private Integer storeId;
  private String storeName;
  private int[] itemIds = new int[7]; // Track created item IDs
  private int itemIndex = 0;
  private int firstCouponId = -1; // Track first coupon ID

  /**
   * Creates a new Flower Shop Client.
   *
   * @param serviceUrl The URL of the coupon management service
   */
  public FlowerShopClient(String serviceUrl) {
    this.apiClient = new ApiClient(serviceUrl);
  }

  /**
   * Registers the flower shop with the coupon management system.
   *
   * @param shopName The name of the flower shop
   * @throws IOException if the registration fails
   */
  public void registerStore(String shopName) throws IOException {
    System.out.println("\n========== Registering Flower Shop ==========");
    System.out.println("Shop Name: " + shopName);

    JsonObject store = apiClient.createStore(shopName);
    this.storeId = store.get("id").getAsInt();
    this.storeName = store.get("name").getAsString();

    System.out.println("✓ Successfully registered!");
    System.out.println("Store ID: " + storeId);
    System.out.println("Store Name: " + storeName);
  }

  /**
   * Adds products to the flower shop inventory.
   *
   * @throws IOException if adding products fails
   */
  public void addProducts() throws IOException {
    if (storeId == null) {
      throw new IllegalStateException("Store must be registered first!");
    }

    System.out.println("\n========== Adding Products to Inventory ==========");

    // Add various flower products
    addProduct("Red Roses Bouquet", 29.99, "flowers");
    addProduct("White Lilies", 24.99, "flowers");
    addProduct("Mixed Tulips Bundle", 19.99, "flowers");
    addProduct("Orchid Plant", 34.99, "plants");
    addProduct("Succulent Garden", 15.99, "plants");
    addProduct("Ceramic Vase", 12.99, "accessories");
    addProduct("Greeting Card", 4.99, "accessories");

    System.out.println("✓ All products added successfully!");
  }

  /**
   * Adds a single product.
   *
   * @param name Product name
   * @param price Product price
   * @param category Product category
   * @throws IOException if adding the product fails
   */
  private void addProduct(String name, double price, String category) throws IOException {
    JsonObject item = apiClient.createItem(name, price, storeId, category);
    int itemId = item.get("id").getAsInt();
    if (itemIndex < itemIds.length) {
      itemIds[itemIndex++] = itemId;
    }
    System.out.println(String.format("  Added: %-25s $%.2f [%s] (ID: %d)",
        name, price, category, itemId));
  }

  /**
   * Creates promotional coupons for the flower shop.
   *
   * @throws IOException if creating coupons fails
   */
  public void createPromotionalCoupons() throws IOException {
    if (storeId == null) {
      throw new IllegalStateException("Store must be registered first!");
    }

    System.out.println("\n========== Creating Promotional Coupons ==========");

    // Create different types of coupons
    JsonObject coupon1 = apiClient.createTotalPriceCoupon(
        storeId, 15.0, true, 50.0);
    firstCouponId = coupon1.get("id").getAsInt();
    System.out.println("✓ Created: 15% off on orders $50+");
    System.out.println("  Coupon ID: " + firstCouponId);

    JsonObject coupon2 = apiClient.createCategoryCoupon(
        storeId, 5.0, false, "flowers");
    System.out.println("✓ Created: $5 off on all flowers");
    System.out.println("  Coupon ID: " + coupon2.get("id").getAsInt());

    JsonObject coupon3 = apiClient.createTotalPriceCoupon(
        storeId, 10.0, false, 30.0);
    System.out.println("✓ Created: $10 off on orders $30+");
    System.out.println("  Coupon ID: " + coupon3.get("id").getAsInt());

    System.out.println("✓ All coupons created successfully!");
  }

  /**
   * Demonstrates finding the optimal coupon for a customer's cart.
   *
   * @throws IOException if the operation fails
   */
  public void demonstrateOptimalCoupon() throws IOException {
    if (storeId == null) {
      throw new IllegalStateException("Store must be registered first!");
    }

    System.out.println("\n========== Finding Optimal Coupon for Customer Cart ==========");

    // Simulate a customer cart with the first 3 items (roses, lilies, orchid)
    int[] cartItems = {itemIds[0], itemIds[1], itemIds[3]};
    System.out.println("Customer's cart contains item IDs: " + arrayToString(cartItems));

    JsonElement result = apiClient.findOptimalCoupon(cartItems, storeId);

    if (result.isJsonObject()) {
      JsonObject couponResult = result.getAsJsonObject();
      System.out.println("✓ Optimal coupon found!");
      System.out.println("  Discount amount: $" + couponResult.get("discount").getAsDouble());
      System.out.println("  Coupon details: " + couponResult.get("coupon"));
    } else {
      System.out.println("  " + result.getAsString());
    }
  }

  /**
   * Demonstrates getting store recommendations for customers.
   *
   * @throws IOException if the operation fails
   */
  public void demonstrateStoreRecommendations() throws IOException {
    System.out.println("\n========== Getting Best Store Recommendations ==========");
    System.out.println("Searching for best prices on 'flowers'...");

    JsonArray recommendations = apiClient.getOptimalStores(null, "flowers");

    if (recommendations.size() > 0) {
      System.out.println("✓ Found " + recommendations.size() + " store(s) selling flowers:");
      for (int i = 0; i < Math.min(3, recommendations.size()); i++) {
        JsonObject rec = recommendations.get(i).getAsJsonObject();
        System.out.println("\n  Store: " + rec.get("store").getAsJsonObject().get("name")
            .getAsString());
        System.out.println("  Item: " + rec.get("item").getAsJsonObject().get("name")
            .getAsString());
        System.out.println("  Final Price: $" + rec.get("finalPrice").getAsDouble());
        System.out.println("  Discount: $" + rec.get("discount").getAsDouble());
      }
    } else {
      System.out.println("  No stores found.");
    }
  }

  /**
   * Demonstrates cart optimization suggestions.
   *
   * @throws IOException if the operation fails
   */
  public void demonstrateCartOptimization() throws IOException {
    if (storeId == null) {
      throw new IllegalStateException("Store must be registered first!");
    }

    System.out.println("\n========== Cart Optimization Suggestions ==========");

    // Simulate a small cart with just the greeting card (last item)
    int[] cartItems = {itemIds[6]};

    System.out.println("Current cart: item ID " + cartItems[0]);
    System.out.println("Target coupon: 15% off on orders $50+");
    System.out.println("Finding cheapest items to add to reach threshold...");

    JsonElement result = apiClient.suggestItems(cartItems, storeId, firstCouponId);

    if (result.isJsonArray()) {
      JsonArray suggestions = result.getAsJsonArray();
      System.out.println("✓ Suggested items to add:");
      for (JsonElement item : suggestions) {
        JsonObject itemObj = item.getAsJsonObject();
        System.out.println(String.format("  - %s ($%.2f)",
            itemObj.get("name").getAsString(),
            itemObj.get("price").getAsDouble()));
      }
    } else {
      System.out.println("  " + result.getAsString());
    }
  }

  /**
   * Displays the current inventory.
   *
   * @throws IOException if the operation fails
   */
  public void displayInventory() throws IOException {
    if (storeId == null) {
      throw new IllegalStateException("Store must be registered first!");
    }

    System.out.println("\n========== Current Inventory ==========");
    JsonArray items = apiClient.getItemsByStore(storeId);

    if (items.size() > 0) {
      System.out.println(String.format("Total items: %d", items.size()));
      for (JsonElement item : items) {
        JsonObject itemObj = item.getAsJsonObject();
        System.out.println(String.format("  ID: %-2d | %-25s | $%-6.2f | [%s]",
            itemObj.get("id").getAsInt(),
            itemObj.get("name").getAsString(),
            itemObj.get("price").getAsDouble(),
            itemObj.get("category").getAsString()));
      }
    } else {
      System.out.println("No items in inventory.");
    }
  }

  /**
   * Displays all coupons for this store.
   *
   * @throws IOException if the operation fails
   */
  public void displayCoupons() throws IOException {
    if (storeId == null) {
      throw new IllegalStateException("Store must be registered first!");
    }

    System.out.println("\n========== Active Coupons ==========");
    JsonArray coupons = apiClient.getCouponsByStore(storeId);

    if (coupons.size() > 0) {
      System.out.println(String.format("✓ Successfully created %d promotional coupons",
          coupons.size()));
      System.out.println("  (Use automated tests for detailed coupon verification)");
    } else {
      System.out.println("No coupons available.");
    }
  }

  /**
   * Runs the complete demonstration.
   *
   * @throws IOException if any operation fails
   */
  public void runDemonstration() throws IOException {
    System.out.println("╔════════════════════════════════════════════════════════════════╗");
    System.out.println("║        FLOWER SHOP - Coupon Management System Client          ║");
    System.out.println("╚════════════════════════════════════════════════════════════════╝");

    try {
      // Step 1: Register the store
      registerStore("Rose Garden Flower Shop");

      // Step 2: Add products
      addProducts();

      // Step 3: Create promotional coupons
      createPromotionalCoupons();

      // Step 4: Display inventory
      displayInventory();

      // Step 5: Display coupons
      displayCoupons();

      // Step 6: Demonstrate optimal coupon finding
      demonstrateOptimalCoupon();

      // Step 7: Demonstrate store recommendations
      demonstrateStoreRecommendations();

      // Step 8: Demonstrate cart optimization
      demonstrateCartOptimization();

      System.out.println("\n╔════════════════════════════════════════════════════════════════╗");
      System.out.println("║              Demonstration Completed Successfully!             ║");
      System.out.println("╚════════════════════════════════════════════════════════════════╝");

    } finally {
      apiClient.close();
    }
  }

  /**
   * Helper method to convert int array to string.
   *
   * @param arr The array
   * @return String representation
   */
  private String arrayToString(int[] arr) {
    StringBuilder sb = new StringBuilder("[");
    for (int i = 0; i < arr.length; i++) {
      sb.append(arr[i]);
      if (i < arr.length - 1) {
        sb.append(", ");
      }
    }
    sb.append("]");
    return sb.toString();
  }

  /**
   * Main method to run the client.
   *
   * @param args Command line arguments
   */
  public static void main(String[] args) {
    String serviceUrl = DEFAULT_SERVICE_URL;

    // Allow override of service URL from command line
    if (args.length > 0) {
      serviceUrl = args[0];
    }

    System.out.println("Connecting to Coupon Management Service at: " + serviceUrl);

    FlowerShopClient client = new FlowerShopClient(serviceUrl);

    try {
      client.runDemonstration();
    } catch (IOException e) {
      System.err.println("\n✗ Error: " + e.getMessage());
      System.err.println("Make sure the Coupon Management Service is running at " + serviceUrl);
      System.exit(1);
    }
  }
}
