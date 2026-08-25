import java.sql.*;
import java.util.*;

import commands.*;
import databases.*;
import entities.Cart;
import entities.Customer;
import entities.products.Bundle;
import entities.products.Deck;
import entities.products.Product;
import entities.products.items.Card;
import utils.*;

/**
 * StoreFront
 * Description: Main class for Pocket Magic Oh. Contains the inventory system
 * Name: Nico Rotella
 * Date Created: May 5th, 2026
 * Last Edited: August 25th, 2026
 */

// Class
public final class StoreFront {

    // Static fields
    static Inventory inventory = new Inventory(); // Initialized in open store method
    static Customers customers = new Customers();
    static ArrayList<Command> commands = new ArrayList<Command>();
    static ArrayList<String> incomingCommands = new ArrayList<String>();

    // main
    public static void main(String[] args) {

        // Objects and variables
        Scanner s = new Scanner(System.in);
        StringBuilder sb = new StringBuilder();
        String input, command, output;


        // Establishing DB connection
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:PocketMagicOH.db")) {

            // Running program
            try {
                conn.setAutoCommit(false); // In case of error, no partial override occurs

                // Filling the inventory array list with cards
                openStore(conn);

                // Taking input from the user
                do {
                    sb.append(s.nextLine());
                    input = sb.toString();
                } while (!input.contains("REPORT INVENTORY;"));

                // Empty String builder
                sb.setLength(0);

                // Gathering their commands
                for (int i = 0; i < input.length(); i++) {

                    // Getting their command
                    sb.append(input.charAt(i));
                    command = sb.toString();

                    // Seeing if the command is complete
                    if (command.contains(";")) {
                        incomingCommands.add(command.trim());
                        sb.setLength(0);
                    }
                }

                // Creating their commands
                for (String incomingCommand : incomingCommands) {
                    createCommand(incomingCommand);
                }

                // Updates and outputs
                for (Command c : commands) {
                    try {
                        c.parse();
                        c.run();
                        sb.append(c.getOutput()).append("\n"); // Adding result of command output to actual output
                    } catch (IllegalArgumentException e) {
                        sb.append(e.getMessage()).append("\n");
                    }
                }

                // Assigning actual output
                output = sb.toString();

                // Display output
                System.out.println(output);

                // Updating the text files
                closeStore(conn);
                conn.commit(); // Committing changes

            }
            catch(SQLException e){ // Error with DB updates, rollback
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) { // Error with rollback
                    System.out.println("Error with rollback: " + rollbackEx.getMessage());
                }
                System.out.println("Save failed, changes were rolled back: " + e.getMessage());
            }
            finally{
                try {
                    conn.setAutoCommit(true);
                }
                catch (SQLException ignored) {}
            } // Running program
        } // Establishing connection
        catch (SQLException e) {
            System.out.printf("""
                    Error connecting to the database.
                    Error message: %s
                    """, e.getMessage());
        }
    } // Main

    /**
     * Description: Takes all the info stored in the databases and saves them to their objects
     * Pre-Condition: None
     * Post-Condition: The objects are set up
     */
    public static void openStore(Connection conn) {
        // If they don't already exist, creating the tables in the database
        createTables(conn);

        fillInventory(conn);
        fillCustomers(conn);
    } // openStore

    /**
     * Description: Creates the tables if they are not already in the .db file
     * Pre-Condition: Connection has been passed properly
     * Post-Condition: The .db file contains necessary tables
     * @param conn The connection to the database
     */
    private static void createTables(Connection conn) {
        // The sql code to update the tables
        String[] sqlCreateTables = {
                """
                CREATE TABLE IF NOT EXISTS products (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                type TEXT NOT NULL
                )""",
                """
                CREATE TABLE IF NOT EXISTS cards (
                product_id INTEGER PRIMARY KEY,
                name TEXT UNIQUE NOT NULL,
                element TEXT NOT NULL,
                rarity TEXT NOT NULL,
                price INTEGER NOT NULL CHECK (price > 0),
                stock INTEGER NOT NULL CHECK (stock >= 0),
                FOREIGN KEY(product_id) REFERENCES products(id)
                )""",
                """
                CREATE TABLE IF NOT EXISTS bundles (
                product_id INTEGER PRIMARY KEY,
                name TEXT UNIQUE NOT NULL,
                FOREIGN KEY(product_id) REFERENCES products(id)
                )""",
                """                
                CREATE TABLE IF NOT EXISTS bundle_items (
                bundle_id INTEGER,
                product_id INTEGER,
                quantity INTEGER NOT NULL,
                PRIMARY KEY(bundle_id, product_id),
                FOREIGN KEY(bundle_id) REFERENCES bundles(product_id),
                FOREIGN KEY(product_id) REFERENCES products(id)
                )""",
                """
                CREATE TABLE IF NOT EXISTS customers (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT UNIQUE NOT NULL
                )""",
                """
                CREATE TABLE IF NOT EXISTS carts (
                cart_id INTEGER PRIMARY KEY AUTOINCREMENT,
                customer_id INTEGER NOT NULL,
                name TEXT NOT NULL,
                FOREIGN KEY(customer_id) REFERENCES customers(id),
                UNIQUE(customer_id, name)
                )""",
                """
                CREATE TABLE IF NOT EXISTS cart_items (
                cart_id INTEGER,
                product_id INTEGER,
                quantity INTEGER NOT NULL,
                PRIMARY KEY(cart_id, product_id),
                FOREIGN KEY(cart_id) REFERENCES carts(cart_id),
                FOREIGN KEY(product_id) REFERENCES products(id)
                )""",
                """
                CREATE TABLE IF NOT EXISTS decks (
                deck_id INTEGER PRIMARY KEY AUTOINCREMENT,
                customer_id INTEGER NOT NULL,
                FOREIGN KEY(customer_id) REFERENCES customers(id)
                )""",
                """
                CREATE TABLE IF NOT EXISTS deck_cards (
                deck_id INTEGER,
                card_id INTEGER,
                quantity INTEGER NOT NULL,
                PRIMARY KEY(deck_id, card_id),
                FOREIGN KEY(deck_id) REFERENCES decks(deck_id),
                FOREIGN KEY(card_id) REFERENCES cards(product_id)
                )"""
        };

        // Creating the tables
        try (Statement stmt = conn.createStatement()) { // Resource try catch, creates and closes these when done
            for (String sqlCreateTable : sqlCreateTables) {
                try {
                    stmt.execute(sqlCreateTable);
                }
                catch (SQLException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Description: Fills the inventory from all the info in the database
     * Pre-Condition: Connection has been passed properly
     * Post-Condition: Inventory object is filled
     * @param conn The connection to the database
     */
    private static void fillInventory(Connection conn) {

        // Variables and objects
        Map<Integer, String> products = new HashMap<Integer, String>();
        List<Integer> bundle_ids = new ArrayList<Integer>();
        ProductType productType;

        // SQL
        String sqlGetProducts = "SELECT * FROM products";

        String sqlGetBundles = """
                SELECT id FROM products
                WHERE type = 'BUNDLE'""";


        try {
            // Filling the inventory (Pt 1, bundles empty)
            try (Statement stmtGetProducts = conn.createStatement();
                 ResultSet rsGetProducts = stmtGetProducts.executeQuery(sqlGetProducts)) {

                // Checking each product in the inventory
                while (rsGetProducts.next()) {
                    products.put(rsGetProducts.getInt("id"), rsGetProducts.getString("type"));
                }

                // Adding each product to the inventory
                for (var entry : products.entrySet()) {
                    try {
                        productType = ProductType.valueOf(entry.getValue());

                        // Adding the product
                        switch (productType) {
                            case CARD -> inventory.addProduct(parseCard(conn, entry.getKey()));
                            case BUNDLE -> inventory.addProduct(parseBundle(conn, entry.getKey()));
                        }
                    }
                    catch (IllegalArgumentException e) {
                        System.out.println("Error, invalid product type in database.");
                    }
                    catch (RuntimeException e) { // Also catches the NoSuchElementException
                        System.out.println(e.getMessage());
                    }
                } // For each product
            } // Filling the inventory (Bundles empty)

            // Filling the bundles
            try (Statement stmtGetBundles = conn.createStatement();
                 ResultSet rsGetBundles = stmtGetBundles.executeQuery(sqlGetBundles)) {

                // Checking each bundle in the inventory
                while (rsGetBundles.next()) {
                    bundle_ids.add(rsGetBundles.getInt("id"));
                }

                // Filling each bundle
                for (Integer bundle_id : bundle_ids) {
                    fillBundle(conn, bundle_id);
                }
            }
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    } // fillInventory

    /**
     * Description: Returns a card object from its product id
     * Pre-Condition: Param should be a valid id in the cards list and database is formatted correctly and connection
     * is connected to the correct database
     * Post-Condition: The card is returned
     * @param conn The connection to the database
     * @param product_id The id of the card
     * @return The card object that was created
     * @throws NoSuchElementException if the sql successfully works but there isn't a result with the product id
     * @throws RuntimeException if the sql is not successful.
     */
    private static Card parseCard(Connection conn, Integer product_id) {

        // Variables and objects
        Card c;

        // SQL
        String sql = """
                SELECT * FROM cards
                WHERE product_id = ?"""; // Not complete statement, needs product_id

        // Reading the card
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, product_id);

            try (ResultSet rs = pstmt.executeQuery()) {
                // No rows found, should never get here
                if (!rs.next()) {
                    throw new NoSuchElementException(String.format("Error, card with product_id %d not found.", product_id));
                }

                // Creating the card
                c = new Card(
                        rs.getString("name"),
                        rs.getString("element"),
                        rs.getString("rarity"),
                        rs.getInt("price"),
                        rs.getInt("stock")
                );

                // Setting its id
                c.setId(product_id);

                // Returning the card
                return c;
            }
        }
        catch (SQLException e) {
            throw new RuntimeException("Database error while fetching card", e);
        }
    }

    /**
     * Description: Returns an empty bundle object from its product id
     * Pre-Condition: Param should be a valid id in the bundle list and database is formatted correctly and the
     * connection is connected to the correct database
     * Post-Condition: The bundle is returned with no contents
     * @param conn The connection to the database
     * @param product_id The id of the bundle
     * @return The bundle object that was created
     * @throws NoSuchElementException if the sql successfully works but there isn't a result with the product id
     * @throws RuntimeException if the sql is not successful.
     */
    private static Bundle parseBundle(Connection conn, Integer product_id) {

        // Variables and objects
        Bundle b;

        // SQL
        String sql = """
                SELECT name FROM bundles
                WHERE product_id = ?"""; // Not complete statement, needs product_id

        // Preparing the statement
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, product_id);

            // Getting the results
            try (ResultSet rs = pstmt.executeQuery()) {

                // No rows found, should never get here
                if (!rs.next()) {
                    throw new NoSuchElementException(String.format("Error, bundle with product_id %d not found.", product_id));
                }

                // Creating the bundle
                b = new Bundle(rs.getString("name"));

                // Setting its id
                b.setId(product_id);

                // Returning the bundle
                return b;

            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    } // parseBundle

    /**
     * Description: Fills a bundle by its id
     * Pre-Condition: Param must be an integer that is a valid bundle id and inventory must be filled with all objects
     * and connection is connected to the correct database
     * Post-Condition: Bundle is filled
     * @param conn The connection to the database
     * @param bundle_id The id of the bundle being filled
     */
    private static void fillBundle(Connection conn, Integer bundle_id) {

        // Variables and objects
        Bundle bundle = (Bundle) inventory.getProductById(bundle_id);

        // SQL
        String sql = """
                SELECT * FROM bundle_items
                WHERE bundle_id = ?"""; // Not a complete statement, needs bundle_id

        // Preparing the statement
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, bundle_id);

            // Getting the results
            try (ResultSet rs = pstmt.executeQuery()) {
                // Each product in the bundle
                while (rs.next()) {
                    Product product = inventory.getProductById(rs.getInt("product_id"));
                    Integer quantity = rs.getInt("quantity");

                    // Adding the product
                    bundle.add(product, quantity);
                }
            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    } // fillBundle

    /**
     * Description: Fills the customers from all the info in the database
     * Pre-Condition: Connection has been passed properly, inventory has been filled
     * Post-Condition: Customers object is filled
     * @param conn The connection to the database
     */
    private static void fillCustomers(Connection conn) {

        // Variables and objects
        Map<Integer, String> shoppers = new HashMap<Integer, String>();

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
                    c.setCarts(fillCarts(conn, id));
                }
                catch (RuntimeException e) {
                    System.out.println(e.getMessage());
                }

                // Adding their decks
                try {
                    c.setDecks(fillDecks(conn, id));
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
     * Post-Condition: The cart is returned (To be updated if more carts per customer)
     * @param customer_id The id number of the customer
     * @return The cart object that was created
     * @throws RuntimeException if the sql is not successful.
     */
    private static TrackedCollection<Cart> fillCarts(Connection conn, int customer_id) {

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
            throw new RuntimeException("Database error while fetching cart", e);
        }

        return carts;
    } // fillCarts

    private static TrackedCollection<Deck> fillDecks(Connection conn, int customer_id) {

        // Variables and objects
        Map<Integer, String> deckInfo = new HashMap<Integer, String>(); // id -> name
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

                // Adding the filled cart
                decks.add(deck);

            } // For each cart the customer has
        }
        catch (SQLException e) {
            throw new RuntimeException("Database error while fetching cart", e);
        }

        return decks;
    } // fillDecks

    /**
     * Description: Updates the DB with all the updates
     * Pre-Condition: Connection is set up properly
     * Post-Condition: DB is updated
     * @param conn The connection to the DB
     * @throws SQLException if there is an SQL issue
     */
    public static void closeStore(Connection conn) throws SQLException{
        // Updating DB
        InventoryRepository.save(conn, inventory);
        CustomersRepository.save(conn, customers);
    }

    /**
     * Description: Creates a command object for the type of command run and adds it to the command list
     * Pre-Condition: Param is a string
     * Post-Condition: Command is selected and created
     * @param input The command from start to semicolon
     */
    public static void createCommand(String input) {

        // Variables
        String keyword = input.split(" ")[0];

        // Report inventory command
        if (input.equals("REPORT INVENTORY;")) {
            commands.add(new ReportInventoryCMD(input, inventory, customers)); // Bookmark
        }
        else {
            try {
                CommandType type = CommandType.valueOf(keyword); // Enum value check for keyword
                commands.add(type.create(input, inventory, customers));
            }
            catch (IllegalArgumentException e) {
                commands.add(new InvalidCMD(input, inventory, customers));
            }
        }
    }

}
