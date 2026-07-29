package commands;


import databases.*;
import entities.Customer;
import utils.Utils;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * CartCMD
 * Description: Cart Command to handle all actions related to CART
 * Name: Nico Rotella
 * Date Created: July 26th, 2026
 * Last Edited: July 29th, 2026
 */

// Class
public class CartCMD extends Command {

    // Enum
    private enum Action {ADD, REMOVE, CLEAR}

    // Attributes
    private Action action;
    private Customer customer;
    private final Map<String, String> updates = new HashMap<String, String>();


    // Constructor
    public CartCMD(String i, Inventory inv, Customers c) {
        super(i, inv, c);
    }


    /**
     * Description: Takes the input and parses through it, determining the action and the provided fields
     * Pre-Condition: Input should be a cart command string, if not will throw an exception related to where the issue was
     * Post-Condition: The input is parsed, action is decided, the fields are updated with their values
     * @throws IllegalArgumentException if the CART command is malformed
     */
    @Override
    public void parse() throws IllegalArgumentException {
        String line = input.replace(";", "").trim();
        String[] tokens = line.split("\\s+");
        int counter = 5; // Counter for conditionally iterating through extra updates

        // Error, shouldn't get here but
        if (!tokens[0].equals("CART")) {
            throw new IllegalArgumentException("Error, CART command must start with CART!");
        }

        // Check if customer name is actually a customer
        try {
            customer = customers.getCustomerByName(tokens[1]);
        }
        catch (NoSuchElementException e) {
            throw new IllegalArgumentException("Error, this customer is not a shopper.");
        }

        // Action being done
        try {
            action = Action.valueOf(tokens[2]);
        }
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(String.format("Error, %s is not a valid CART command action! " +
                    "Expected ADD, REMOVE, or CLEAR.", tokens[2]));
        }

        // Checking the desired updates
        if (action == Action.ADD || action == Action.REMOVE) {
            // The next two arguments
            if (tokens.length >= 5) {
                updates.put(tokens[4], tokens[3]);
            }
            else { // Malformed command
                throw new IllegalArgumentException("Error, command not formatted correctly, expected a quantity" +
                        " followed by a product.");
            }

            // If they have more arguments
            while (tokens.length > counter) {
                if (tokens.length >= counter + 3) { // Still enough commands
                    // Check for AND operator
                    if (!tokens[counter].equals("AND")) {
                        throw new IllegalArgumentException(String.format("Error, expected AND operator after %s.",
                                tokens[counter - 1])); // Was not AND
                    }

                    // Add values to updates
                    updates.put(tokens[counter + 2], tokens[counter + 1]);


                }
                else {
                    throw new IllegalArgumentException(String.format("Error, command not formatted correctly after %s."
                            + " Expected \"AND quantity product\"", tokens[counter - 1]));
                }
                counter += 3;
            } // Bonus updates
        } // Adding updates

        // Checking that the updates are valid
        // Adding to cart
        if (action == Action.ADD) {
            for (var entry : updates.entrySet()) {
                // Checking if this is a product we sell
                if (!inventory.hasProduct(entry.getKey())) {
                    throw new IllegalArgumentException(String.format("Error, %s is not a product being sold at the " +
                            "moment.", entry.getKey()));
                }
                // Checking that their desired quantity was a positive integer
                if (!Utils.isNumeric(entry.getValue())) { // Was not a number
                    throw new IllegalArgumentException(String.format("Error, the quantity requested for %s " +
                            "must be a positive integer.", entry.getKey()));
                }
                else if (Integer.parseInt(entry.getValue()) < 0) { // Was a number but was negative
                    throw new IllegalArgumentException(String.format("Error, desired quantity to ADD of %s " +
                            "must be positive.", entry.getKey()));
                }
            }
        }
        // Removing from cart
        else if (action == Action.REMOVE) {
            for (var entry : updates.entrySet()) {
                // Checking that this is a product in their cart
                if (!customer.getCart().hasProduct(entry.getKey())) {
                    throw new IllegalArgumentException(String.format("Error, %s is not a product in %s's cart.",
                            entry.getKey(), customer.getName()));
                }
                // Checking that their desired quantity was a positive integer
                if (!Utils.isNumeric(entry.getValue())) { // Was not a number
                    throw new IllegalArgumentException(String.format("Error, the quantity requested for %s " +
                            "must be a positive integer.", entry.getKey()));
                }
                else if (Integer.parseInt(entry.getValue()) < 0) { // Was a number but was negative
                    throw new IllegalArgumentException(String.format("Error, desired quantity to ADD of %s " +
                            "must be positive.", entry.getKey()));
                }
                // Checking that it is possible to remove that many of this product from their cart
                if (customer.getCart().quantityOf(entry.getKey()) < Integer.parseInt(entry.getValue())) {
                    throw new IllegalArgumentException(String.format("Error, cannot remove %s of %s from %s's cart " +
                                    "since they only have %d.", entry.getValue(), entry.getKey(), customer.getName(),
                            customer.getCart().quantityOf(entry.getKey())));
                }
            }
        }
    }


    /**
     * Description: Actually runs the command, updating as required and setting output
     * Pre-Condition: Parse has been run on this command
     * Post-Condition: Output has been set and command has been run
     */
    @Override
    public void run() {
        switch (action) {
            case ADD -> add();
            case REMOVE -> remove();
            case CLEAR -> clear();
            default -> output = "Error, this was not a valid CART command.";
        }
    }

    /**
     * Description: Adds the desired updates to the cart and sets the output
     * Pre-Condition: Parse has already been called on the command object
     * Post-Condition: The items have been added to the cart and the output has been set
     */
    private void add() {
        for (var entry : updates.entrySet()) {
            customer.getCart().addToCart(inventory.getProductByName(entry.getKey()),
                    Integer.parseInt(entry.getValue()));
        }
        output = String.format("%s cart updated", customer.getName());
    }

    /**
     * Description: Removes the desired updates from the cart and sets the output
     * Pre-Condition: Parse has already been called on the command object
     * Post-Condition: The items have been removed from the cart and the output has been set
     */
    private void remove() {
        for (var entry : updates.entrySet()) {
            customer.getCart().removeFromCart(inventory.getProductByName(entry.getKey()),
                    Integer.parseInt(entry.getValue()));
        }
        output = String.format("%s cart updated", customer.getName());
    }

    /**
     * Description: Clears the customers cart and sets the output
     * Pre-Condition: Parse has already been called on the command object
     * Post-Condition: The cart has been cleared and the output has been set
     */
    private void clear() {
        customer.getCart().emptyCart();
        output = String.format("%s cart cleared", customer.getName());
    }

}
