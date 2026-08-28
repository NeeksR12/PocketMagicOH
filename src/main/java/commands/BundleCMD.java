package commands;

import databases.*;
import entities.products.Bundle;
import utils.Utils;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * BundleCMD
 * Description: Bundle command to handle all actions related to bundle
 * Name: Nico Rotella
 * Date Created: August 6th, 2026
 * Last Edited: August 20th, 2026
 */

// Class
public class BundleCMD extends Command {

    // Enum
    private enum Action {ADD, REMOVE, CLEAR, DELETE}
    
    // Attributes
    private Action action;
    private String name;
    private Bundle bundle;
    private final Map<String, String> updates = new HashMap<>();
    
    
    // Constructor
    public BundleCMD(String i, Inventory inv, Customers c) {
        super(i, inv, c);
    }


    /**
     * Description: Takes the input and parses through it, determining the action and the provided fields
     * Pre-Condition: Input should be a bundle command string, if not will throw an exception related to where the issue was
     * Post-Condition: The input is parsed, action is decided, the fields are updated with their values
     * @throws IllegalArgumentException if the BUNDLE command is malformed
     */
    @Override
    public void parse() throws IllegalArgumentException {
        String line = input.replace(";", "").trim();
        String[] tokens = line.split("\\s+");
        int counter = 5; // Counter for conditionally iterating through extra updates

        // Error, shouldn't get here but
        if (!tokens[0].equals("BUNDLE")) {
            throw new IllegalArgumentException("Error, BUNDLE command must start with BUNDLE!");
        }
        
        // Bundle name to run command on
        name = tokens[1];
        
        // Action being done with the bundle
        try {
            action = Action.valueOf(tokens[2]);
        } 
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(String.format("Error, %s is not a valid BUNDLE command action! " +
                    "Expected ADD, REMOVE, CLEAR, or DELETE.", tokens[2]));
        }
        
        // Checking the desired updates (ADD and REMOVE)
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
        switch (action) {
            case ADD -> {
                // Setting the bundle if it is already in the inventory (if not, is set later)
                if (inventory.hasBundle(name)) // Already existed
                    bundle = inventory.getBundleByName(name);

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
                    } else if (Integer.parseInt(entry.getValue()) < 0) { // Was a number but was negative
                        throw new IllegalArgumentException(String.format("Error, desired quantity to ADD of %s " +
                                "must be positive.", entry.getKey()));
                    }
                }
            } // ADD
            case REMOVE -> {
                // Checking if the bundle is in the inventory
                try {
                    bundle = inventory.getBundleByName(name);
                } catch (NoSuchElementException e) {
                    throw new IllegalArgumentException("Error, the bundle that is trying to be updated is not in" +
                            " the inventory.");
                }

                for (var entry : updates.entrySet()) {
                    // Checking that this is a product in this bundle
                    if (!bundle.hasProduct(entry.getKey())) {
                        throw new IllegalArgumentException(String.format("Error, %s is not a product in %s's bundle.",
                                entry.getKey(), bundle.getName()));
                    }
                    // Checking that their desired quantity was a positive integer
                    if (!Utils.isNumeric(entry.getValue())) { // Was not a number
                        throw new IllegalArgumentException(String.format("Error, the quantity requested for %s " +
                                "must be a positive integer.", entry.getKey()));
                    } else if (Integer.parseInt(entry.getValue()) < 0) { // Was a number but was negative
                        throw new IllegalArgumentException(String.format("Error, desired quantity to ADD of %s " +
                                "must be positive.", entry.getKey()));
                    }
                    // Checking that it is possible to remove that many of this product from the bundle
                    if (bundle.quantityOf(entry.getKey()) < Integer.parseInt(entry.getValue())) {
                        throw new IllegalArgumentException(String.format("Error, cannot remove %s of %s from %s since" +
                                        " it only contains %d.", entry.getValue(), entry.getKey(), bundle.getName(),
                                bundle.quantityOf(entry.getKey())));
                    }
                }
            } // REMOVE
            case CLEAR -> {
                // Checking if the bundle is in the inventory
                try {
                    bundle = inventory.getBundleByName(name);
                } catch (NoSuchElementException e) {
                    throw new IllegalArgumentException("Error, the bundle that is trying to be cleared is not in" +
                            " the inventory.");
                }

                // Checking that there are no more arguments after the action
                if (tokens.length > 3) {
                    throw new IllegalArgumentException(String.format("Error, unexpected arguments after %s %s %s.",
                            tokens[0], tokens[1], tokens[2]));
                }
            } // CLEAR
            case DELETE -> {
                // Checking if the bundle is in the inventory
                try {
                    bundle = inventory.getBundleByName(name);
                } catch (NoSuchElementException e) {
                    throw new IllegalArgumentException("Error, the bundle that is trying to be updated is not in" +
                            " the inventory.");
                }

                // Checking if any product groups contain the bundle
                if (customers.isProductInACustomer(name) || inventory.isProductInProductGroup(name))
                    throw new IllegalArgumentException("Error, cannot delete this bundle because it is already being" +
                            " used");
            } // DELETE
        } // switch
    } // parse

    /**
     * Description: Actually runs the command, updating as required and setting output
     * Pre-Condition: Parse has been run on this command
     * Post-Condition: Output has been set and command has been run
     */
    @Override
    public void run() {
        inventory.markDirty(bundle);
        switch (action) {
            case ADD -> add();
            case REMOVE -> remove();
            case CLEAR -> clear();
            case DELETE -> delete();
            default -> output = "Error, this was not a valid BUNDLE command.";
        }
    }

    /**
     * Description: Adds the desired updates to the bundle and sets the output
     * Pre-Condition: Parse has already been called on the command object
     * Post-Condition: The items have been added to the bundle and the output has been set
     */
    private void add() {
        // Adding bundle to the inventory if it is new
        if (bundle == null) { // They are adding to a new bundle, not yet in inventory
            bundle = new Bundle(name);
            inventory.addProduct(bundle);
        }
        // Adding products to the bundle
        for (var entry : updates.entrySet()) {
            bundle.add(inventory.getProductByName(entry.getKey()),
                    Integer.parseInt(entry.getValue()));
        }
        output = String.format("bundle %s updated", bundle.getName());
    }

    /**
     * Description: Removes the desired updates from the bundle and sets the output
     * Pre-Condition: Parse has already been called on the command object
     * Post-Condition: The items have been removed from the bundle and the output has been set
     */
    private void remove() {
        for (var entry : updates.entrySet()) {
            bundle.remove(inventory.getProductByName(entry.getKey()),
                    Integer.parseInt(entry.getValue()));
        }
        output = String.format("bundle %s updated", bundle.getName());
    }

    /**
     * Description: Clears the bundle and sets the output
     * Pre-Condition: Parse has already been called on the command object
     * Post-Condition: The bundle has been cleared and the output has been set
     */
    private void clear() {
        bundle.empty();
        output = String.format("bundle %s cleared", bundle.getName());
    }

    /**
     * Description: Removes the bundle from the inventory and sets the output
     * Pre-Condition: Parse has been called already on this command object
     * Post-Condition: The bundle has been removed and the output has been set
     */
    private void delete() {
        customers.deleteProductFromAllCustomers(bundle); // Failsafe, should never execute
        inventory.deleteProductByName(name); // Marks deleted
        output = String.format("bundle %s deleted", name);
    }

}
