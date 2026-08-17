import java.util.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import commands.*;
import databases.*;
import entities.Cart;
import entities.Customer;
import entities.products.Bundle;
import entities.products.items.Card;
import utils.*;

/**
 * StoreFront
 * Description: Main class for Pocket Magic Oh. Contains the inventory system
 * Name: Nico Rotella
 * Date Created: May 5th, 2026
 * Last Edited: August 1st, 2026
 */

// Class
public final class StoreFront {

    // Enum
    enum ProductType {CARD, BUNDLE}

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
        String input = "";
        String command = "";
        String output = "";


        // Filling the inventory array list with cards
        openStore();

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
            }
            catch (IllegalArgumentException e) {
                sb.append(e.getMessage()).append("\n");
            }
        }

        // Assigning actual output
        output = sb.toString();

        // Display output
        System.out.println(output);

        // Updating the text files
        closeStore();

    } // Main

    /**
     * Description: Takes all the info stored in the databases and saves them to their objects
     * Pre-Condition: None
     * Post-Condition: The objects are set up
     */
    public static void openStore() {

        // If they don't already exist, creating the tables in the database
        createTables();

        fillInventory();
        fillCustomers();
    } // openStore

    /**
     * Description: Creates the tables if they are not already in the .db file
     * Pre-Condition: None
     * Post-Condition: The .db file contains necessary tables
     */
    private static void createTables() {
        String url = "jdbc:sqlite:PocketMagicOH.db";

        // The sql code to update the tables
        String sql = """
                CREATE TABLE products (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                type TEXT NOT NULL
                );
                
                CREATE TABLE cards (
                product_id INTEGER PRIMARY KEY,
                name TEXT UNIQUE NOT NULL,
                element TEXT NOT NULL,
                rarity TEXT NOT NULL,
                price INTEGER NOT NULL CHECK (price > 0),
                stock INTEGER NOT NULL CHECK (price >= 0),
                FOREIGN KEY(product_id) REFERENCES products(id)
                );
                
                CREATE TABLE bundles (
                product_id INTEGER PRIMARY KEY,
                name TEXT UNIQUE NOT NULL,
                FOREIGN KEY(product_id) REFERENCES products(id)
                );
                
                CREATE TABLE bundle_items (
                bundle_id INTEGER,
                product_id INTEGER,
                quantity INTEGER NOT NULL,
                PRIMARY KEY(bundle_id, product_id),
                FOREIGN KEY(bundle_id) REFERENCES bundles(product_id),
                FOREIGN KEY(product_id) REFERENCES products(id)
                );
                
                CREATE TABLE customers (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT UNIQUE NOT NULL
                );
                
                CREATE TABLE carts (
                cart_id INTEGER PRIMARY KEY AUTOINCREMENT,
                customer_id INTEGER NOT NULL UNIQUE,
                FOREIGN KEY(customer_id) REFERENCES customers(id)
                );
                
                CREATE TABLE cart_items (
                cart_id INTEGER,
                product_id INTEGER,
                quantity INTEGER NOT NULL,
                PRIMARY KEY(cart_id, product_id),
                FOREIGN KEY(cart_id) REFERENCES carts(cart_id),
                FOREIGN KEY(product_id) REFERENCES products(id)
                );
                """;

        // Creating the tables
        try (Connection conn = DriverManager.getConnection(url);
             java.sql.Statement stmt = conn.createStatement()) { // Resource try catch, creates and closes these when done

            stmt.execute(sql);
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Description: Fills the inventory from all the info in the database
     * Pre-Condition: None
     * Post-Condition: Inventory object is filled
     */
    private static void fillInventory() {

        // Variables and objects
        Map<Integer, String> products = new HashMap<Integer, String>();
        ProductType productType;

        // SQL
        String url = "jdbc:sqlite:PocketMagicOH.db";

        String sql = "SELECT id, type FROM products";

        // Filling the inventory
        try (Connection conn = DriverManager.getConnection(url);
             java.sql.Statement stmt = conn.createStatement();
             java.sql.ResultSet rs = stmt.executeQuery(sql)) {

            // Checking each product in the inventory
            while (rs.next()) {
                products.put(rs.getInt("id"), rs.getString("type"));
            }

            // Adding each product to the inventory
            for (var entry : products.entrySet()) {
                try {
                    productType = ProductType.valueOf(entry.getValue());

                    // Adding the product
                    switch (productType) {
                        case CARD -> inventory.addProduct(parseCard(entry.getKey()));
                        case BUNDLE -> inventory.addProduct(parseBundle(entry.getKey()));
                    }
                }
                catch (IllegalArgumentException e) {
                    System.out.println("Error, invalid product type in database.");
                }
                catch (RuntimeException e) { // Also catches the NoSuchElementException
                    System.out.println(e.getMessage());
                }
            } // For each product
        } // Filling the inventory
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    } // fillInventory

    /**
     * Description: Returns a card object from its product id
     * Pre-Condition: Param should be a valid id in the cards list and database is formatted correctly
     * Post-Condition: The card is returned
     * @param product_id The id of the card
     * @return The card object that was created
     * @throws NoSuchElementException if the sql successfully works but there isn't a result with the product id
     * @throws RuntimeException if the sql is not successful.
     */
    private static Card parseCard(Integer product_id) {
        String url = "jdbc:sqlite:PocketMagicOH.db";

        String sql = String.format("""
                SELECT * FROM cards
                WHERE product_id = %d""", product_id);

        // Reading the card
        try (Connection conn = DriverManager.getConnection(url);
             java.sql.Statement stmt = conn.createStatement();
             java.sql.ResultSet rs = stmt.executeQuery(sql)) {

            // No rows found, should never get here
            if (!rs.next()) {
                throw new NoSuchElementException(String.format("Error, card with product_id %d not found.", product_id));
            }

            // Returning the card
            return new Card(
                    rs.getString("name"),
                    rs.getString("element"),
                    rs.getString("rarity"),
                    rs.getInt("price"),
                    rs.getInt("stock")
            );
        }
        catch (SQLException e) {
            throw new RuntimeException("Database error while fetching card", e);
        }
    }

    /**
     * Description: Returns a bundle object from its product id
     * Pre-Condition: Param should be a valid id in the bundle list and database is formatted correctly
     * Post-Condition: The bundle is returned
     * @param product_id The id of the bundle
     * @return The bundle object that was created
     * @throws NoSuchElementException if the sql successfully works but there isn't a result with the product id
     * @throws RuntimeException if the sql is not successful.
     */
    private static Bundle parseBundle(Integer product_id) {

        // Variables and object
        Bundle bundle;
        int subProduct_id, quantity;
        ProductType productType;

        // SQL
        String url = "jdbc:sqlite:PocketMagicOH.db";

        String sqlGetBundle = String.format("""
                SELECT name FROM bundles
                WHERE product_id = %d""", product_id);

        String sqlGetProducts = String.format("""
                SELECT * FROM bundle_items
                WHERE bundle_id = %d""", product_id);

        String sqlGetProductType = """
                SELECT type FROM products
                WHERE id = ?"""; // Not complete statement, needs id number


        // Establishing the connection
        try (Connection conn = DriverManager.getConnection(url)) {

            // Getting the bundle
            try (java.sql.Statement stmtGetBundle = conn.createStatement();
                 java.sql.ResultSet rsGetBundle = stmtGetBundle.executeQuery(sqlGetBundle)) {

                // No rows found, should never get here
                if (!rsGetBundle.next()) {
                    throw new NoSuchElementException(String.format("Error, bundle with product_id %d not found.", product_id));
                }

                // Creating the bundle
                bundle = new Bundle(rsGetBundle.getString("name"));

            }

            // Getting the products in the bundle
            try (java.sql.Statement stmtGetProducts = conn.createStatement();
                 java.sql.ResultSet rsGetProducts = stmtGetProducts.executeQuery(sqlGetProducts)) {

                // Each product
                while (rsGetProducts.next()) {

                    subProduct_id = rsGetProducts.getInt("product_id");
                    quantity = rsGetProducts.getInt("quantity");

                    // Getting the product type
                    try (java.sql.PreparedStatement pstmtGetProductType = conn.prepareStatement(sqlGetProductType)) {
                         pstmtGetProductType.setInt(1, subProduct_id);

                        // Result of getting the product type
                        try (java.sql.ResultSet rsGetProductType = pstmtGetProductType.executeQuery()) {

                            // No rows found, should never get here
                            if (!rsGetProductType.next()) {
                                throw new NoSuchElementException(String.format(
                                        "Error, product in bundle %d with product_id %d not found.",
                                        product_id, subProduct_id));
                            }

                            // Adding the product to the bundle
                            // (Don't need try/catch '.' exception will be caught in fillInventory)
                            productType = ProductType.valueOf(rsGetProductType.getString("type"));

                            // Adding the product
                            switch (productType) {
                                case CARD -> bundle.add(parseCard(subProduct_id), quantity);
                                case BUNDLE -> bundle.add(parseBundle(subProduct_id), quantity);
                            }
                        } // Product type result
                    } // Product type check
                } // While product in bundle
            } // Getting the products in the bundle

            // Bundle complete
            return bundle;

        } // Opening the connection
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    } // parseBundle

    /**
     * Description: Fills the customers from all the info in the database
     * Pre-Condition: None
     * Post-Condition: Customers object is filled
     */
    private static void fillCustomers() {

        // Variables and objects
        Map<Integer, String> customers = new HashMap<Integer, String>();

        // SQL
        String url = "jdbc:sqlite:PocketMagicOH.db";

        String sql = "SELECT id, name FROM customers";

        // Filling the customers
        try (Connection conn = DriverManager.getConnection(url);
             java.sql.Statement stmt = conn.createStatement();
             java.sql.ResultSet rs = stmt.executeQuery(sql)) {

            // Checking each customer in the list
            while (rs.next()) {
                customers.put(rs.getInt("id"), rs.getString("name"));
            }

            // Adding each customer to the customers and setting their cart
            for (var entry : customers.entrySet()) {

                // Local loop variables and objects
                int id = entry.getKey();
                String name = entry.getValue();

                // This customer
                Customer c = new Customer(name);

                // Filling their cart
                try {
                    c.setCart(fillCarts(id));
                }
                catch (IllegalArgumentException e) { // Catches a bad product type enum
                    System.out.println("Error, invalid product type in database.");
                }
                catch (RuntimeException e) { // Also catches the NoSuchElementException, from parseCard/Bundle
                    System.out.println(e.getMessage());
                }
            } // For each customer
        } // Filling the customers
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    } // fillCustomers

    /**
     * Description: Fills the cart objects a customer has using the customer id
     * Pre-Condition: Param should be a valid id in the customers list and database is formated correctly
     * Post-Condition: The cart is returned (To be updated if more carts per customer)
     * @param customer_id The id number of the customer
     * @return The cart object that was created
     * @throws RuntimeException if the sql is not successful.
     */
    private static Cart fillCarts(int customer_id) {

        // Variables and objects
        List<Integer> cart_ids = new ArrayList<Integer>(); // Modular for later
        List<Cart> carts = new ArrayList<Cart>();
        int product_id, quantity;
        ProductType productType;
        Cart cart;

        // SQL
        String url = "jdbc:sqlite:PocketMagicOH.db";

        String sqlGetCarts = String.format("""
                SELECT cart_id FROM carts
                WHERE customer_id = %d""", customer_id);

        String sqlGetProducts = """
                SELECT * FROM cart_items
                WHERE cart_id = ?"""; // Not complete statement, needs cart_id number

        String sqlGetProductType = """
                SELECT type FROM products
                WHERE id = ?"""; // Not complete statement, needs id number



        // Establishing the connection
        try (Connection conn = DriverManager.getConnection(url)) {

            // Getting the carts
            try (java.sql.Statement stmtGetCarts = conn.createStatement();
                 java.sql.ResultSet rsGetCarts = stmtGetCarts.executeQuery(sqlGetCarts)) {

                // No rows found
                if (!rsGetCarts.next()) {
                    return new Cart(); // Customer doesn't have a cart so they have their cart empty
                }

                // They have cart(s)
                do {
                    cart_ids.add(rsGetCarts.getInt("cart_id"));
                } while (rsGetCarts.next());
            }

            // Getting the products in each cart
            for (Integer cart_id : cart_ids) {

                cart = new Cart();

                // Getting the product itself
                try (java.sql.PreparedStatement pstmtGetProducts = conn.prepareStatement(sqlGetProducts)) {
                    pstmtGetProducts.setInt(1, cart_id);

                    // Result of getting the products for that cart
                    try (java.sql.ResultSet rsGetProducts = pstmtGetProducts.executeQuery()) {

                        // Each product in the cart
                        while (rsGetProducts.next()) {

                            product_id = rsGetProducts.getInt("product_id");
                            quantity = rsGetProducts.getInt("quantity");

                            // Getting the product type
                            try (java.sql.PreparedStatement pstmtGetProductType = conn.prepareStatement(sqlGetProductType)) {
                                pstmtGetProductType.setInt(1, product_id);

                                // Result of getting the product type
                                try (java.sql.ResultSet rsGetProductType = pstmtGetProductType.executeQuery()) {

                                    // No rows found, should never get here
                                    if (!rsGetProductType.next()) {
                                        throw new NoSuchElementException(String.format(
                                                "Error, product in cart %d with product_id %d not found.",
                                                cart_id, product_id));
                                    }

                                    // Adding the product to the bundle
                                    // (Don't need try/catch '.' exception will be caught in fillCustomers)
                                    productType = ProductType.valueOf(rsGetProductType.getString("type"));

                                    // Adding the product
                                    switch (productType) {
                                        case CARD -> cart.add(parseCard(product_id), quantity);
                                        case BUNDLE -> cart.add(parseBundle(product_id), quantity);
                                    }
                                } // Product type result
                            } // Product type check
                        } // While product in cart
                    } // Result of getting the products in the cart
                } // Getting the products in that cart

                // Adding the filled cart
                carts.add(cart);

            } // For each cart the customer has

            return carts.getFirst();

        } // Opening the connection
        catch (SQLException e) {
            throw new RuntimeException("Database error while fetching cart", e);
        }
    } // fillCarts

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
