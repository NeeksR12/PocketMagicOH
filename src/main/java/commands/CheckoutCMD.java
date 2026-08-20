package commands;

import databases.*;
import entities.Cart;
import entities.Customer;
import entities.products.Product;
import entities.products.items.Item;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * CheckoutCMD
 * Description: Checkout Command to handle checking out a customer
 * Name: Nico Rotella
 * Date Created: July 29th, 2026
 * Last Edited: August 20th, 2026
 */

// Class
public class CheckoutCMD extends Command {

    // Attributes
    private Customer customer;


    // Constructor
    public CheckoutCMD(String i, Inventory inv, Customers c) {
        super(i, inv, c);
    }


    /**
     * Description: Takes the input and parses through it, determining who to check out
     * Pre-Condition: Input should be a checkout command string, if not will throw an exception related to where the issue was
     * Post-Condition: The input is parsed and the customer is decided
     * @throws IllegalArgumentException if the CHECKOUT command is malformed
     */
    @Override
    public void parse() throws IllegalArgumentException {
        String line = input.replace(";", "").trim();
        String[] tokens = line.split("\\s+");

        // Error, shouldn't get here but
        if (!tokens[0].equals("CHECKOUT")) {
            throw new IllegalArgumentException("Error, CHECKOUT command must start with CHECKOUT!");
        }

        // Customer to be checked out
        // Check if customer name is actually a customer
        try {
            customer = customers.getCustomerByName(tokens[1]);
        }
        catch (NoSuchElementException e) {
            throw new IllegalArgumentException("Error, this customer is not a shopper.");
        }

        // Checking if there is anything else
        if (tokens.length > 2) {
            throw new IllegalArgumentException(String.format("Error, unexpected arguments after %s.", line));
        }
    }

    /**
     * Description: Actually runs the command, updating as required and setting output
     * Pre-Condition: Parse has been run on this command
     * Post-Condition: Output has been set and command has been run
     */
    @Override
    public void run() {

        // The cart of the customer being checked out
        Cart c = customer.getCart();

        // Purchasing cart
        try {
            inventory.purchaseCart(c);

            // Success
            // Setting output
            output = String.format("%s total %d", customer.getName(), c.price());

            // Managing dirty customer and clearing cart
            customers.markDirty(customer);
            c.empty();
        }
        catch (IllegalArgumentException e) {
            Map<Item, Integer> insufficient = new HashMap<Item, Integer>();
            StringBuilder sb = new StringBuilder();

            // Checking which items were out of stock
            for (var entry : c.toItems().entrySet()) {
                if (entry.getKey().getStock() <= entry.getValue()) {
                    insufficient.put(entry.getKey(), entry.getValue());
                }
            }

            // Building output
            for (var entry : insufficient.entrySet()) {
                sb.append(String.format("\n- %s (requested %d, available %d)",
                        entry.getKey().getName(), entry.getValue(), entry.getKey().getStock()));
            }
            output = "Checkout not completed. Insufficient stock of:" + sb;
        }
    }

}
