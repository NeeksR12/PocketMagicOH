package databases;

import entities.Cart;
import entities.Customer;
import entities.products.Deck;
import entities.products.Product;
import entities.products.items.Card;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * CustomersRepository
 * Description: Helper class to read and write to and from a customers object and stored in a DB
 * Name: Nico Rotella
 * Date Created: August 19th, 2026
 * Last Edited: August 28th, 2026
 */

// Class
public final class CustomersRepository {

    // Private constructor, does not need an instance
    private CustomersRepository() {}


    // Reading operations
    /**
     * Description: Fills the customers from all the info in the database
     * Pre-Condition: Connection has been passed properly, inventory has been filled
     * Post-Condition: Customers object is filled
     * @param conn The connection to the database
     * @param customers The customers object being filled
     * @param inventory The inventory object that the customers are shopping from
     */
    public static void fillCustomers(Connection conn, Customers customers, Inventory inventory) {

        // Variables and objects
        Map<Integer, String> shoppers = new HashMap<>();

        // SQL
        String sql = "SELECT id, name FROM customers";

        // Filling the customers
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            // Checking each customer in the list
            while (rs.next()) {
                shoppers.put(rs.getInt("id"), rs.getString("name"));
            }

            // Adding each customer to the customers and setting their cart
            for (var entry : shoppers.entrySet()) {

                // Local loop variables and objects
                int id = entry.getKey();
                String name = entry.getValue();

                // This customer
                Customer c = new Customer(name);
                c.setId(id);

                // Filling their cart
                try {
                    c.setCarts(fillCarts(conn, id, inventory));
                }
                catch (RuntimeException e) {
                    System.out.println(e.getMessage());
                }

                // Adding their decks
                try {
                    c.setDecks(fillDecks(conn, id, inventory));
                }
                catch (RuntimeException e) {
                    System.out.println(e.getMessage());
                }

                // Adding the customer to the customers
                customers.addCustomer(c);

            } // For each customer
        } // Filling the customers
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    } // fillCustomers

    /**
     * Description: Fills the cart objects a customer has using the customer id
     * Pre-Condition: Param should be a valid id in the customers list, database is formated correctly, fillInventory
     * has been called, and the connection is connected to the correct database
     * Post-Condition: The carts is returned
     * @param conn The connection to the database
     * @param customer_id The id number of the customer
     * @param inventory The inventory the carts are being filled from
     * @return The tracked collection of carts object that was created
     * @throws RuntimeException if the sql is not successful.
     */
    private static TrackedCollection<Cart> fillCarts(Connection conn, int customer_id, Inventory inventory) {

        // Variables and objects
        Map<Integer, String> cartInfo = new HashMap<>(); // id -> name
        TrackedCollection<Cart> carts = new TrackedCollection<>();
        Cart cart;

        // SQL
        String sqlGetCarts = """
                SELECT cart_id, name FROM carts
                WHERE customer_id = ?"""; // Not a complete statement, requires customer_id

        String sqlGetProducts = """
                SELECT * FROM cart_items
                WHERE cart_id = ?"""; // Not complete statement, needs cart_id number


        try {
            // Getting the carts
            try (PreparedStatement pstmtGetCarts = conn.prepareStatement(sqlGetCarts)) {
                pstmtGetCarts.setInt(1, customer_id);

                try (ResultSet rsGetCarts = pstmtGetCarts.executeQuery()) {
                    // No rows found
                    if (!rsGetCarts.next()) {
                        return carts; // Customer doesn't have a cart so they have their cart empty
                    }

                    // They have cart(s)
                    do {
                        cartInfo.put(rsGetCarts.getInt("cart_id"), rsGetCarts.getString("name"));
                    } while (rsGetCarts.next());
                }
            }

            // Getting the products in each cart
            for (var entry : cartInfo.entrySet()) {

                // Cart info
                int cart_id = entry.getKey();
                String name = entry.getValue();

                cart = new Cart(name);
                cart.setId(cart_id);

                // Getting the product itself
                try (PreparedStatement pstmtGetProducts = conn.prepareStatement(sqlGetProducts)) {
                    pstmtGetProducts.setInt(1, cart_id);

                    // Result of getting the products for that cart
                    try (ResultSet rsGetProducts = pstmtGetProducts.executeQuery()) {

                        // Each product in the cart
                        while (rsGetProducts.next()) {
                            Product product = inventory.getProductById(rsGetProducts.getInt("product_id"));
                            Integer quantity = rsGetProducts.getInt("quantity");

                            // Adding the product
                            cart.add(product, quantity);
                        }
                    }
                }

                // Adding the filled cart
                carts.add(cart);

            } // For each cart the customer has
        }
        catch (SQLException e) {
            throw new RuntimeException("Database error while fetching cart: " + e.getMessage());
        }

        return carts;
    } // fillCarts

    /**
     * Description: Fills the decks objects a customer has using the customer id
     * Pre-Condition: Param should be a valid id in the customers list, database is formated correctly, fillInventory
     * has been called, and the connection is connected to the correct database
     * Post-Condition: The decks is returned
     * @param conn The connection to the database
     * @param customer_id The id number of the customer
     * @param inventory The inventory the decks are being filled from
     * @return The tracked collection of decks object that was created
     * @throws RuntimeException if the sql is not successful.
     */
    private static TrackedCollection<Deck> fillDecks(Connection conn, int customer_id, Inventory inventory) {

        // Variables and objects
        Map<Integer, String> deckInfo = new HashMap<>(); // id -> name
        TrackedCollection<Deck> decks = new TrackedCollection<>();
        Deck deck;

        // SQL
        String sqlGetDecks = """
                SELECT deck_id, name FROM decks
                WHERE customer_id = ?"""; // Not a complete statement, requires customer_id

        String sqlGetCards = """
                SELECT * FROM deck_cards
                WHERE deck_id = ?"""; // Not complete statement, needs deck_id number


        try {
            // Getting the decks
            try (PreparedStatement pstmtGetDecks = conn.prepareStatement(sqlGetDecks)) {
                pstmtGetDecks.setInt(1, customer_id);

                try (ResultSet rsGetDecks = pstmtGetDecks.executeQuery()) {
                    // No rows found
                    if (!rsGetDecks.next()) {
                        return decks; // Customer doesn't have any decks so they have their decks empty
                    }

                    // They have deck(s)
                    do {
                        deckInfo.put(rsGetDecks.getInt("deck_id"), rsGetDecks.getString("name"));
                    } while (rsGetDecks.next());
                }
            }

            // Getting the cards in each deck
            for (var entry : deckInfo.entrySet()) {

                // Deck info
                int deck_id = entry.getKey();
                String name = entry.getValue();

                // The current deck object
                deck = new Deck(name);
                deck.setId(deck_id);

                // Getting the cards itself
                try (PreparedStatement pstmtGetCards = conn.prepareStatement(sqlGetCards)) {
                    pstmtGetCards.setInt(1, deck_id);

                    // Result of getting the cards for that deck
                    try (ResultSet rsGetCards = pstmtGetCards.executeQuery()) {

                        // Each card in the deck
                        while (rsGetCards.next()) {
                            Card card = inventory.getCardById(rsGetCards.getInt("card_id"));
                            Integer quantity = rsGetCards.getInt("quantity");

                            // Adding the card
                            deck.add(card, quantity);
                        }
                    }
                }

                // Adding the filled deck
                decks.add(deck);

            } // For each deck the customer has
        }
        catch (SQLException e) {
            throw new RuntimeException("Database error while fetching deck: " + e.getMessage());
        }

        return decks;
    } // fillDecks

    // Writing operations
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
        String sql = "INSERT INTO customers (name) VALUES (?)"; // Not complete statement, needs name

        // Adding the customer to the customers table
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, c.getName());
            pstmt.executeUpdate();

            // Giving the customer object its id
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    c.setId(rs.getInt(1));
                }
            }
        }

        // Adding carts to carts table and saving its items
        for (Cart cart : c.getDirtyCarts()) { // Should be all carts since customer is new
            insertCart(conn, c.getId(), cart);
            saveCartItems(conn, cart);
        }

        // Adding decks to deck table and saving its cards
        for (Deck deck : c.getDirtyDecks()) { // ^^^
            insertDeck(conn, c.getId(), deck);
            saveDeckCards(conn, deck);
        }
    }

    /**
     * Description: Inserts a cart to the carts table with a customer id
     * Pre-Condition: This customer has a cart that is not in the DB and the connection is set up properly
     * Post-Condition: This cart entry is added to the cart table and the cart id is returned
     * @param conn The connection to the database
     * @param customer_id The id of the customer
     * @param cart The cart being added
     * @throws SQLException if there is an SQL issue
     */
    private static void insertCart(Connection conn, Integer customer_id, Cart cart) throws SQLException {

        // SQL
        String sql = "INSERT INTO carts (customer_id, name) VALUES (?, ?)";
        // Not complete statement, needs customer_id and name

        // Adding the cart to the carts table
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, customer_id);
            pstmt.setString(2, cart.getName());

            pstmt.executeUpdate();

            // Getting the cart id
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    cart.setId(rs.getInt(1));
                }
            }
        }
    }

    /**
     * Description: Inserts a deck to the decks table with a customer id
     * Pre-Condition: This customer has a deck that is not in the DB and the connection is set up properly
     * Post-Condition: This deck entry is added to the deck table and the deck id is returned
     * @param conn The connection to the database
     * @param customer_id The id of the customer
     * @param deck The deck being added
     * @throws SQLException if there is an SQL issue
     */
    private static void insertDeck(Connection conn, Integer customer_id, Deck deck) throws SQLException {

        // SQL
        String sql = "INSERT INTO decks (customer_id, name) VALUES (?, ?)";
        // Not complete statement, needs customer_id and name

        // Adding the deck to the decks table
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, customer_id);
            pstmt.setString(2, deck.getName());

            pstmt.executeUpdate();

            // Getting the deck id
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    deck.setId(rs.getInt(1));
                }
            }
        }
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

        // Dirty carts in the customer
        for (Cart cart : c.getDirtyCarts()) {
            // Checking if this is a new cart
            if (cart.getId() == null) {
                insertCart(conn, c.getId(), cart);
            }

            saveCartItems(conn, cart);
        }

        // Dirty decks in the customer
        for (Deck deck : c.getDirtyDecks()) {
            // Checking if this is a new deck
            if (deck.getId() == null) {
                insertDeck(conn, c.getId(), deck);
            }

            saveDeckCards(conn, deck);
        }
        
        // Deleted carts in the customer
        for (Cart cart : c.getDeletedCarts()) {
            deleteCart(conn, cart);
        }
        
        // Deleted decks in the customer
        for (Deck deck : c.getDeletedDecks()) {
            deleteDeck(conn, deck);
        }
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
     * Description: Saves the contents of a deck to the deck_cards table in the DB
     * Pre-Condition: All dirty products have been added to the DB, no inserting still needs to be done and connection
     * is set up properly
     * Post-Condition: This deck has its items saved properly in the DB
     * @param conn The connection to the DB
     * @param d The deck
     * @throws SQLException if there is an SQL issue
     */
    private static void saveDeckCards(Connection conn, Deck d) throws SQLException{

        // SQL
        String sqlDeleteDeckCards = "DELETE FROM deck_cards WHERE deck_id = ?"; // Not complete statement, needs deck_id

        String sqlUpdateDeckCards = "INSERT INTO deck_cards (deck_id, card_id, quantity) VALUES (?, ?, ?)";
        // Not complete statement, needs values

        // Removing all items currently in cart in DB
        try (PreparedStatement pstmtDeleteDeckCards = conn.prepareStatement(sqlDeleteDeckCards)) {
            pstmtDeleteDeckCards.setInt(1, d.getId());

            pstmtDeleteDeckCards.executeUpdate();
        }

        // Adding all of them properly back
        try (PreparedStatement pstmtUpdateDeckCards = conn.prepareStatement(sqlUpdateDeckCards)) {
            // Adding values
            for (var entry : d.getCards().entrySet()) {
                pstmtUpdateDeckCards.setInt(1, d.getId()); // deck_id
                pstmtUpdateDeckCards.setInt(2, entry.getKey().getId()); // card_id
                pstmtUpdateDeckCards.setInt(3, entry.getValue()); // quantity

                pstmtUpdateDeckCards.addBatch();
            }

            // Running the batch
            pstmtUpdateDeckCards.executeBatch();
        }
    }

    /**
     * Description: Deletes a customer from the DB
     * Pre-Condition: This customer is already in the DB and connection is set up properly
     * Post-Condition: This customer no longer exists in the DB
     * @param conn The connection to the DB
     * @param c The customer
     * @throws SQLException if there is an SQL issue
     */
    private static void delete(Connection conn, Customer c) throws SQLException {

        // SQL
        String sqlDeleteFromCustomers = "DELETE FROM customers WHERE id = ?";
        // Not complete statement, needs id
        
        // Deleting all the carts this customer had
        for (Cart cart : c.getCarts().values()) {
            deleteCart(conn, cart);
        }

        // Deleting all the decks this customer had
        for (Deck deck : c.getDecks().values()) {
            deleteDeck(conn, deck);
        }
        
        // Delete from customers
        try (PreparedStatement pstmtDeleteFromCustomers = conn.prepareStatement(sqlDeleteFromCustomers)) {
            pstmtDeleteFromCustomers.setInt(1, c.getId()); // id

            pstmtDeleteFromCustomers.executeUpdate();
        }
    } // delete

    /**
     * Description: Deletes a cart from the DB
     * Pre-Condition: This cart is already in the DB and connection is set up properly
     * Post-Condition: This cart no longer exists in the DB
     * @param conn The connection to the DB
     * @param cart The cart
     * @throws SQLException if there is an SQL issue
     */
    private static void deleteCart(Connection conn, Cart cart) throws SQLException {

        // SQL
        String sqlDeleteFromCartItems = "DELETE FROM cart_items WHERE cart_id = ?";
        // Not complete statement, needs cart_id

        String sqlDeleteFromCarts = "DELETE FROM carts WHERE cart_id = ?";
        // Not complete statement, needs cart_id

        // Removing from cart_items
        try (PreparedStatement pstmtDeleteFromCartItems = conn.prepareStatement(sqlDeleteFromCartItems)) {
            pstmtDeleteFromCartItems.setInt(1, cart.getId()); // cart_id

            pstmtDeleteFromCartItems.executeUpdate();
        }

        // Removing from carts
        try (PreparedStatement pstmtDeleteFromCarts = conn.prepareStatement(sqlDeleteFromCarts)) {
            pstmtDeleteFromCarts.setInt(1, cart.getId()); // cart_id

            pstmtDeleteFromCarts.executeUpdate();
        }
    }

    /**
     * Description: Deletes a deck from the DB
     * Pre-Condition: This deck is already in the DB and connection is set up properly
     * Post-Condition: This deck no longer exists in the DB
     * @param conn The connection to the DB
     * @param deck The deck
     * @throws SQLException if there is an SQL issue
     */
    private static void deleteDeck(Connection conn, Deck deck) throws SQLException {
        
        // SQL
        String sqlDeleteFromDeckCards = "DELETE FROM deck_cards WHERE deck_id = ?";
        // Not complete statement, needs deck_id

        String sqlDeleteFromDecks = "DELETE FROM decks WHERE deck_id = ?";
        // Not complete statement, needs deck_id

        // Removing from deck_cards
        try (PreparedStatement pstmtDeleteFromDeckCards = conn.prepareStatement(sqlDeleteFromDeckCards)) {
            pstmtDeleteFromDeckCards.setInt(1, deck.getId()); // deck_id

            pstmtDeleteFromDeckCards.executeUpdate();
        }

        // Removing from decks
        try (PreparedStatement pstmtDeleteFromDecks = conn.prepareStatement(sqlDeleteFromDecks)) {
            pstmtDeleteFromDecks.setInt(1, deck.getId()); // deck_id

            pstmtDeleteFromDecks.executeUpdate();
        }
    }
    
}
