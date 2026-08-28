package utils;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Utils
 * Description: Final class that contains any helper methods used throughout the PMO system
 * Name: Nico Rotella
 * Date Created: May 22nd, 2026
 * Last Edited: August 28th 2026
 */

public final class Utils {

    // Don't ever need an instance of this class
    private Utils() {}

    /**
     * Description: Checks if a string is also an int and returns a boolean result
     * Pre-Condition: Param is a string
     * Post-Condition: Boolean is returned true if param is also an int or false if not
     * @param str The string being checked
     * @return The result of the check
     */
    public static boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }

    /**
     * Description: Creates the tables if they are not already in the .db file
     * Pre-Condition: Connection has been passed properly
     * Post-Condition: The .db file contains necessary tables
     * @param conn The connection to the database
     */
    public static void createTables(Connection conn) {
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

}
