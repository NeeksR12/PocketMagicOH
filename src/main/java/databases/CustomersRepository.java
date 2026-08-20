package databases;

import entities.Cart;
import entities.Customer;

import java.sql.*;

/**
 * CustomersRepository
 * Description: Helper class to take a customers object and store it in a DB
 * Name: Nico Rotella
 * Date Created: August 19th, 2026
 * Last Edited: August 19th, 2026
 */

// Class
public final class CustomersRepository {

    // Private constructor, does not need an instance
    private CustomersRepository() {}

    /**
     * Description: Saves a customers object to a DB based on all changes logged to the customers
     * Pre-Condition: Connection is set up properly and customers is not null
     * Post-Condition: The customers object is saved properly to the DB
     * @param conn The connection to the DB
     * @param customers The customers
     * @throws SQLException if there is an SQL issue
     */
    public static void save(Connection conn, Customers customers) throws SQLException {

        // Dirty customers (Insertions and updates)
        for (Customer c : customers.getDirtyShoppers()) {
            // Insertion
            if (c.getId() == null) {
                insert(conn, c);
            }
            else { // Update
                update(conn, c);
            }
        }

        // Deleted customers
        for (Customer c : customers.getDeletedShoppers()) {
            delete(conn, c);
        }

        customers.clearDirtyTracking();
    }

    /**
     * Description: Inserts a customer into all of its respective tables
     * Pre-Condition: This customer must not be in the DB and connection must be set up properly
     * Post-Condition: This customer is added to the DB
     * @param conn The connection to the DB
     * @param c The customer
     * @throws SQLException if there is an SQL issue
     */
    private static void insert(Connection conn, Customer c) throws SQLException {

        // SQL
        String sqlAddCustomer = "INSERT INTO customers (name) VALUES (?)"; // Not complete statement, needs name

        String sqlAddCart = "INSERT INTO carts (customer_id) VALUES (?)"; // Not complete statement, needs customer_id

        // Adding the customer to the customers table
        try (PreparedStatement pstmtAddCustomer = conn.prepareStatement(sqlAddCustomer, Statement.RETURN_GENERATED_KEYS)) {
            pstmtAddCustomer.setString(1, c.getName());
            pstmtAddCustomer.executeUpdate();

            // Giving the customer object its id
            try (ResultSet rsAddCustomer = pstmtAddCustomer.getGeneratedKeys()) {
                if (rsAddCustomer.next()) {
                    c.setId(rsAddCustomer.getInt(1));
                }
            }
        }

        // Adding its cart to the carts table
        try (PreparedStatement pstmtAddCart = conn.prepareStatement(sqlAddCart, Statement.RETURN_GENERATED_KEYS)) {
            pstmtAddCart.setInt(1, c.getId());
            pstmtAddCart.executeUpdate();

            // Giving the cart its id
            try (ResultSet rsAddCart = pstmtAddCart.getGeneratedKeys()) {
                if (rsAddCart.next()) {
                    c.getCart().setId(rsAddCart.getInt(1));
                }
            }
        }

        // Updating the cart items
        saveCartItems(conn, c.getCart());
    }

    /**
     * Description: Updates an existing customer in the DB
     * Pre-Condition: All customers must have an id already and connection is set up properly
     * Post-Condition: The customer is updated in the DB
     * @param conn The connection to the DB
     * @param c The customer
     * @throws SQLException if there is an SQL issue
     */
    private static void update(Connection conn, Customer c) throws SQLException {

        /*
        // SQL
        String sql = "DELETE FROM customers WHERE customer_id = ?"; // Not complete statement, needs customer_id

        // Removing the current version of the customer
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, c.getId());

            pstmt.executeUpdate();
        }

        // Adding the current version of the customer
        insert(conn, c);

         */
        saveCartItems(conn, c.getCart());
    }

    /**
     * Description: Saves the contents of a cart to the cart_items table in the DB
     * Pre-Condition: All dirty products have been added to the DB, no inserting still needs to be done and connection
     * is set up properly
     * Post-Condition: This cart has its items saved properly in the DB
     * @param conn The connection to the DB
     * @param c The cart
     * @throws SQLException if there is an SQL issue
     */
    private static void saveCartItems(Connection conn, Cart c) throws SQLException {

        // SQL
        String sqlDeleteCartItems = "DELETE FROM cart_items WHERE cart_id = ?"; // Not complete statement, needs cart_id

        String sqlUpdateCartItems = "INSERT INTO cart_items (cart_id, product_id, quantity) VALUES (?, ?, ?)";
        // Not complete statement, needs values

        // Removing all items currently in cart in DB
        try (PreparedStatement pstmtDeleteCartItems = conn.prepareStatement(sqlDeleteCartItems)) {
            pstmtDeleteCartItems.setInt(1, c.getId());

            pstmtDeleteCartItems.executeUpdate();
        }

        // Adding all of them properly back
        try (PreparedStatement pstmtUpdateCartItems = conn.prepareStatement(sqlUpdateCartItems)) {
            // Adding values
            for (var entry : c.getProducts().entrySet()) {
                pstmtUpdateCartItems.setInt(1, c.getId()); // cart_id
                pstmtUpdateCartItems.setInt(2, entry.getKey().getId()); // product_id
                pstmtUpdateCartItems.setInt(3, entry.getValue()); // quantity

                pstmtUpdateCartItems.addBatch();
            }

            // Running the batch
            pstmtUpdateCartItems.executeBatch();
        }
    }

    /**
     * Description: Removes a customer from the DB
     * Pre-Condition: This customer is already in the DB and connection is set up properly
     * Post-Condition: This customer no longer exists in the DB
     * @param conn The connection to the DB
     * @param c The customer
     * @throws SQLException if there is an SQL issue
     */
    private static void delete(Connection conn, Customer c) throws SQLException {

        // SQL
        String sqlRemoveFromCartItems = "DELETE FROM cart_items WHERE cart_id = ?";
        // Not complete statement, needs cart_id

        String sqlRemoveFromCarts = "DELETE FROM carts WHERE customer_id = ?";
        // Not complete statement, needs customer_id

        String sqlRemoveFromCustomers = "DELETE FROM customers WHERE id = ?";
        // Not complete statement, needs id

        // Removing from cart_items
        try (PreparedStatement pstmtRemoveFromCartItems = conn.prepareStatement(sqlRemoveFromCartItems)) {
            pstmtRemoveFromCartItems.setInt(1, c.getCart().getId());

            pstmtRemoveFromCartItems.executeUpdate();
        }

        // Removing from carts
        try (PreparedStatement pstmtRemoveFromCarts = conn.prepareStatement(sqlRemoveFromCarts)) {
            pstmtRemoveFromCarts.setInt(1, c.getId());

            pstmtRemoveFromCarts.executeUpdate();
        }

        // Remove from products
        try (PreparedStatement pstmtRemoveFromCustomers = conn.prepareStatement(sqlRemoveFromCustomers)) {
            pstmtRemoveFromCustomers.setInt(1, c.getId());

            pstmtRemoveFromCustomers.executeUpdate();
        }
    }
    
}
