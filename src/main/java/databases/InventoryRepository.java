package databases;

import entities.products.Bundle;
import entities.products.Product;
import entities.products.items.Card;
import utils.ProductType;

import java.sql.*;

/**
 * InventoryRepository
 * Description: Helper class to take an inventory object and store it in a DB
 * Name: Nico Rotella
 * Date Created: August 19th, 2026
 * Last Edited: August 19th, 2026
 */

// Class
public final class InventoryRepository {

    // Private constructor, does not need an instance
    private InventoryRepository() {}

    /**
     * Description: Saves an inventory object to a DB based on all changes logged to the inventory
     * Pre-Condition: Connection is set up properly and inventory is not null
     * Post-Condition: The inventory object is saved properly to the DB
     * @param conn The connection to the DB
     * @param inventory The inventory
     * @throws SQLException if there is an SQL issue
     */
    public static void save(Connection conn, Inventory inventory) throws SQLException {

        // Dirty products (Insertions and direct updates)
        for (Product p : inventory.getDirtyProducts()) {
            // New products that don't have id yet, .'. are being added to the DB for the first time
            if (p.getId() == null) {
                insert(conn, p);
            }
            else { // Just an update, product has been in the DB before
                update(conn, p);
            }
        }

        // Saving any changes to bundle items
        for (Product p : inventory.getDirtyProducts()) {
            if (p instanceof Bundle b)
                saveBundleItems(conn, b);
        }

        // Deleting all deleted products
        for (Product p : inventory.getDeletedProducts()) {
            delete(conn, p);
        }

        // Clearing the dirty tracking since updates to DB have been completed
        inventory.clearDirtyTracking();
    }

    /**
     * Description: Inserts a product into all of its respective tables
     * Pre-Condition: This product must not be in the DB and connection must be set up properly
     * Post-Condition: This product is added to the DB
     * @param conn The connection to the DB
     * @param p The product
     * @throws SQLException if there is an SQL issue
     */
    private static void insert(Connection conn, Product p) throws SQLException {

        // Variables and objects
        ProductType productType;

        // SQL
        String sql = "INSERT INTO products (type) VALUES (?)"; // Not complete statement, needs type

        // Getting the type of the product
        productType = ProductType.valueOf(p.getType()); // Should never throw, products only return valid type strings

        // Adding the product to the products table
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, productType.toString());
            pstmt.executeUpdate();

            // Giving the product object its id
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    p.setId(rs.getInt("id"));
                }
            }
        }

        // Inserting to other tables based on product type
        switch (productType) {
            case CARD -> insertCard(conn, (Card) p);
            case BUNDLE -> insertBundle(conn, (Bundle) p);
        }
    }

    /**
     * Description: Inserts a card to the cards table after it has already been assigned its id in the products table
     * Pre-Condition: This card's id must not be null and connection is set up properly
     * @param conn The connection to the DB
     * @param c The card
     * @throws SQLException if there is an SQL issue
     */
    private static void insertCard(Connection conn, Card c) throws SQLException {

        // SQL
        String sql = """
                INSERT INTO cards (product_id, name, element, rarity, price, stock)
                VALUES (?, ?, ?, ?, ?, ?)"""; // Not complete statement, needs values

        // Inserting the card into the cards table
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Filling the attributes of the card
            pstmt.setInt(1, c.getId());
            pstmt.setString(2, c.getName());
            pstmt.setString(3, c.getElement());
            pstmt.setString(4, c.getRarity());
            pstmt.setInt(5, c.getPrice());
            pstmt.setInt(6, c.getStock());

            // Updating
            pstmt.executeUpdate();
        }
    }

    /**
     * Description: Inserts a bundle to the bundles table after it has already been assigned its id in the products table
     * This method does not fill the bundles contents, must do this after all new items are in the system.
     * Pre-Condition: This bundle's id must not be null and connection is set up properly
     * @param conn The connection to the DB
     * @param b The bundle
     * @throws SQLException if there is an SQL issue
     */
    private static void insertBundle(Connection conn, Bundle b) throws SQLException {

        // SQL
        String sql = "INSERT INTO bundles (product_id, name) VALUES (?, ?)"; // Not complete statement, needs values

        // Inserting the bundle into the bundles table
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Filling the attributes of the bundle
            pstmt.setInt(1, b.getId());
            pstmt.setString(2, b.getName());

            // Updating
            pstmt.executeUpdate();
        }
    }

    /**
     * Description: Updates an existing product in the DB
     * Pre-Condition: All products must have an id already and connection is set up properly
     * Post-Condition: The product is updated in the DB
     * @param conn The connection to the DB
     * @param p The product
     * @throws SQLException if there is an SQL issue
     */
    private static void update(Connection conn, Product p) throws SQLException {
        ProductType productType = ProductType.valueOf(p.getType());

        switch (productType) {
            case CARD -> updateCard(conn, (Card) p);
            case BUNDLE -> updateBundle(conn, (Bundle) p); // Probably never gets called but just in case
        }
    }

    /**
     * Description: Updates an existing card in the DB
     * Pre-Condition: This card must have an id already and connection is set up properly
     * Post-Condition: The card is updated in the DB
     * @param conn The connection to the DB
     * @param c The card
     * @throws SQLException if there is an SQL issue
     */
    private static void updateCard(Connection conn, Card c) throws SQLException {

        // SQL
        String sql = "DELETE FROM cards WHERE product_id = ?"; // Not complete statement, needs product_id

        // Removing the current version of the card
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, c.getId());

            pstmt.executeUpdate();
        }

        // Adding the current version of the card
        insertCard(conn, c);
    }

    /**
     * Description: Updates an existing bundle in the DB
     * Pre-Condition: This bundle must have an id already and connection is set up properly
     * Post-Condition: The bundle is updated in the DB
     * @param conn The connection to the DB
     * @param b The bundle
     * @throws SQLException if there is an SQL issue
     */
    private static void updateBundle(Connection conn, Bundle b) throws SQLException{

        // SQL
        String sql = "DELETE FROM bundles WHERE product_id = ?"; // Not complete statement, needs product_id

        // Removing the current version of the bundle
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, b.getId());

            pstmt.executeUpdate();
        }

        // Adding the current version of the bundle
        insertBundle(conn, b);
    }

    /**
     * Description: Saves the contents of a bundle to the bundle_items table in the DB
     * Pre-Condition: All dirty products have been added to the DB, no inserting still needs to be done and connection
     * is set up properly
     * Post-Condition: This bundle has its items saved properly in the DB
     * @param conn The connection to the DB
     * @param b The bundle
     * @throws SQLException if there is an SQL issue
     */
    private static void saveBundleItems(Connection conn, Bundle b) throws SQLException {

        // SQL
        String sqlDeleteBundleItems = "DELETE FROM bundle_items WHERE bundle_id = ?";
        // Not complete statement, needs bundle_id

        String sqlUpdateBundleItems = "INSERT INTO bundle_items (bundle_id, product_id, quantity) VALUES (?, ?, ?)";
        // Not complete statement, needs values

        // Removing all items currently in the bundle in the DB
        try (PreparedStatement pstmtDeleteBundleItems = conn.prepareStatement(sqlDeleteBundleItems)) {
            pstmtDeleteBundleItems.setInt(1, b.getId());

            pstmtDeleteBundleItems.executeUpdate();
        }

        // Adding all of them properly back
        try (PreparedStatement pstmtUpdateBundleItems = conn.prepareStatement(sqlUpdateBundleItems)) {
            // Adding values
            for (var entry : b.getProducts().entrySet()) {
                pstmtUpdateBundleItems.setInt(1, b.getId()); // bundle_id
                pstmtUpdateBundleItems.setInt(2, entry.getKey().getId()); // product_id
                pstmtUpdateBundleItems.setInt(3, entry.getValue()); // quantity

                pstmtUpdateBundleItems.addBatch(); // Batches all commands to be run at once
            }

            // Running the batch
            pstmtUpdateBundleItems.executeBatch();
        }
    }

    /**
     * Description: Removes a product from the DB
     * Pre-Condition: This product is already in the DB and connection is set up properly
     * Post-Condition: This product no longer exists in the DB
     * @param conn The connection to the DB
     * @param p The product
     * @throws SQLException if there is an SQL issue
     */
    private static void delete(Connection conn, Product p) throws SQLException {

        // Variables and objects
        ProductType productType = ProductType.valueOf(p.getType());

        // SQL
        String sqlRemoveFromBundleItems = "DELETE FROM bundle_items WHERE bundle_id = ? OR product_id = ?";
        // Not complete statement, needs values.

        String sqlRemoveFromCards = "DELETE FROM cards WHERE product_id = ?";
        // Not complete statement, needs product id

        String sqlRemoveFromBundles = "DELETE FROM bundles WHERE product_id = ?";
        // Not complete statement, needs product id

        String sqlRemoveFromProducts = "DELETE FROM products WHERE id = ?";
        // Not complete statement, needs id

        // Removing from bundle_items
        try (PreparedStatement pstmtRemoveFromBundleItems = conn.prepareStatement(sqlRemoveFromBundleItems)) {
            pstmtRemoveFromBundleItems.setInt(1, p.getId());
            pstmtRemoveFromBundleItems.setInt(2, p.getId());

            pstmtRemoveFromBundleItems.executeUpdate();
        }

        // Removing from type table
        switch (productType) {
            case CARD -> {
                try (PreparedStatement pstmtRemoveFromCards = conn.prepareStatement(sqlRemoveFromCards)) {
                    pstmtRemoveFromCards.setInt(1, p.getId());

                    pstmtRemoveFromCards.executeUpdate();
                }
            }
            case BUNDLE -> {
                try (PreparedStatement pstmtRemoveFromBundles = conn.prepareStatement(sqlRemoveFromBundles)) {
                    pstmtRemoveFromBundles.setInt(1, p.getId());

                    pstmtRemoveFromBundles.executeUpdate();
                }
            }
        }

        // Removing from products
        try (PreparedStatement pstmtRemoveFromProducts = conn.prepareStatement(sqlRemoveFromProducts)) {
            pstmtRemoveFromProducts.setInt(1, p.getId());

            pstmtRemoveFromProducts.executeUpdate();
        }
    }

}
