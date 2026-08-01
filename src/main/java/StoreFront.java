import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import commands.*;
import databases.*;
import entities.Customer;
import entities.products.Card;
import entities.products.Product;
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

    // Record for cart entries while parsing the customers
    public record CartEntry(Product product, Integer quantity) {}

    // Enum
    enum ProductType {CARD, BUNDLE, DECK}

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
     * Description: Fills the databases with what was stored in the text files
     * Pre-Condition: Text files are accessible
     * Post-Condition: Databases are set up
     */
    public static void openStore() {
        // Inventory
        try {
            fillInventory();
            fillCustomers();
        }
        catch (IllegalStateException e) {
            System.out.println(e.getMessage());
        }
    } // openStore

    /**
     * Description: Adds all products to the inventory object that were stored in the text file
     * Pre-Condition: inventory.txt is accessible
     * Post-Condition: Inventory object has been filled with products
     * @throws IllegalStateException if the inventory file was malformed
     */
    public static void fillInventory() throws IllegalStateException {

        // Objects and Variables
        FileReader fr = null;
        Scanner sfr = null;
        boolean success = false;
        String line = "";
        ProductType type;

        // Initializing fileIO objects
        try {
            fr = new FileReader("inventory.txt");
            sfr = new Scanner(fr);

            success = true;
        }
        catch (FileNotFoundException e) {
            System.out.println("Issue finding the file.");
        }

        if (success) { // Low security and won't work if file for some reason was not formatted right
            while (sfr.hasNextLine()) {
                line = sfr.nextLine().trim();

                // If not the spacing line
                if (!line.isEmpty()) {
                    // Determine product
                    try {
                        type = ProductType.valueOf(line);
                    } catch (IllegalArgumentException e) {
                        throw new IllegalArgumentException("Error, the inventory file has been modified and does not " +
                                "contain proper products.");
                    }

                    // Create product
                    switch (type) {
                        case CARD -> {
                            try {
                                inventory.addProduct(parseCard(sfr));
                            } catch (IllegalStateException e) {
                                throw new IllegalStateException(e);
                            }
                        }
                    } // Switch type
                } // If line not empty
            } // While parsing
        } // Successful file reader
    } // fillInventory

    /**
     * Description: Adds a card to the inventory object from the text file
     * Pre-Condition: Scanner param must be the scanner being used to scan the file. It must have already scanned the
     * line that says CARD and be on to the next line with the fields.
     * Post-Condition: Scanner is advanced to empty line, card has been created and added to the inventory
     * @param sfr The scanner object scanning inventory.txt
     * @return The card that has been parsed and created
     * @throws IllegalStateException if the file is malformed
     */
    public static Card parseCard(Scanner sfr) throws IllegalStateException {

        // Variables and objects
        String line;
        Map<String, String> fields = new HashMap<>();
        int price, stock; // Need to convert the string from the map to an int later

        // Gathering fields
        for (int i = 0; i < 5; i++) {
            line = sfr.nextLine();

            if (line.contains(": ")) {
                String[] parts = line.split(": ", 2);
                fields.put(parts[0], parts[1]);
            }
            else {
                throw new IllegalStateException("Error, inventory file not formatted correctly.");
            }

        }

        // Verifying fields
        price = Utils.isNumeric(fields.get("Price")) // Checks if numeric and then gives int value
                ? Integer.parseInt(fields.get("Price"))
                : 0;

        stock = Utils.isNumeric(fields.get("Stock"))
                ? Integer.parseInt(fields.get("Stock"))
                : 0;

        if (!(fields.get("Name") == null || fields.get("Element") == null || fields.get("Rarity") == null))
            return new Card(
                    fields.get("Name"),
                    fields.get("Element"),
                    fields.get("Rarity"),
                    price,
                    stock
            );
        else
            throw new IllegalStateException("Error, inventory file not formatted correctly.");
    }

    /**
     * Description: Adds all customers to the customers object that were stored in the text file
     * Pre-Condition: Must be called after the inventory is filled and customers.txt is accessible
     * Post-Condition: Customers object has been filled with customers
     * @throws IllegalStateException if the file was malformed
     */
    public static void fillCustomers() {

        // Objects and Variables
        FileReader fr = null;
        Scanner sfr = null;
        boolean success = false;
        String line = "";

        // Initializing fileIO objects
        try {
            fr = new FileReader("customers.txt");
            sfr = new Scanner(fr);

            success = true;
        }
        catch (FileNotFoundException e) {
            System.out.println("Issue finding the file.");
        }

        if (success) { // Low security and won't work if file for some reason was not formatted right
            while (sfr.hasNextLine()) {
                line = sfr.nextLine().trim();

                // If not the spacing line
                if (!line.isEmpty()) {
                    customers.addShopper(parseCustomer(line, sfr));
                }
            }
        }
    } // Fill Customers

    /**
     * Description: Takes the name of the customer and the scanner parsing the customers file and adds their cart
     * Pre-Condition: The scanner param must be the scanner parsing the customer file and must have scanned their name
     * meaning the next line is the first cart entry
     * Post-Condition: The customer is created and the cart is added
     * @param name The name of the customer scanned from the file
     * @param sfr The scanner parsing customers.txt
     * @return The customer
     * @throws IllegalStateException if the file is malformed
     */
    public static Customer parseCustomer(String name, Scanner sfr){
        // Creating the customer
        Customer c = new Customer(name);

        // Adding their cart
        while (sfr.hasNextLine()) {
            String line = sfr.nextLine().trim();

            if (line.isEmpty())
                break;

            var entry = parseCartEntry(line); // If this throws, it will exit this method to and be caught above
            c.getCart().addToCart(entry.product(), entry.quantity());
        }

        return c;
    }

    /**
     * Description: Parses a line that is a cart entry string and returns the cart entry as objects
     * Pre-Condition: Line should be a cart entry string from the text file
     * Post-Condition: CartEntry is returned or exception is thrown
     * @param line The cart entry string from the parser
     * @return The CartEntry
     * @throws IllegalStateException if the cart entry string is malformed
     */
    public static CartEntry parseCartEntry(String line) throws IllegalStateException {
        // Checking if the line was formated correctly
        if (!line.contains(" - "))
            throw new IllegalStateException("Error, customers file not formatted correctly.");

        String[] parts = line.split(" - ", 2);

        String productName = parts[0];
        String quantityStr = parts[1];

        // Checking that the product and quantity are valid
        if (!inventory.hasProduct(productName) || !Utils.isNumeric(quantityStr))
            throw new IllegalStateException("Error, customer's cart malformed.");

        Product p = inventory.getProductByName(productName);
        int qty = Integer.parseInt(quantityStr);

        return new CartEntry(p, qty);
    }

    /**
     * Description: Fills the text files with what was stored in the databases
     * Pre-Condition: Databases are initialized
     * Post-Condition: Text files are updated
     */
    public static void closeStore() {
        updateInventoryFile();
        updateCustomersFile();
    }

    /**
     * Description: Updates the text file containing the inventory
     * Pre-Condition: Inventory object is declared and initialized
     * Post-Condition: inventory.txt is updated
     */
    public static void updateInventoryFile() {

        // Variables and Objects
        FileWriter fw = null;
        PrintWriter pw = null;
        boolean success = false;

        try {
            fw = new FileWriter("inventory.txt");
            pw = new PrintWriter(fw);

            success = true;
        }
        catch (IOException e) {
            System.out.println("Issue updating the inventory, could not access the file.");
        }

        if (success) {
            pw.println(inventory.toString());
            pw.close();
        }
    }

    /**
     * Description: Updates the text file containing the customers
     * Pre-Condition: Customers object is declared and initialized
     * Post-Condition: customer.txt is updated
     */
    public static void updateCustomersFile() {

        // Variables and Objects
        FileWriter fw = null;
        PrintWriter pw = null;
        boolean success = false;

        try {
            fw = new FileWriter("customers.txt");
            pw = new PrintWriter(fw);

            success = true;
        }
        catch (IOException e) {
            System.out.println("Issue updating the customers, could not access the file.");
        }

        if (success) {
            pw.println(customers.toString());
            pw.close();
        }
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
            } catch (IllegalArgumentException e) {
                commands.add(new InvalidCMD(input, inventory, customers));
            }
        }
    }

}
