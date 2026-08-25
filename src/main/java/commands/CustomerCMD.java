package commands;

import databases.*;
import entities.Customer;

/**
 * CustomerCMD
 * Description: Customer Command to handle all actions related to CUSTOMER
 * Name: Nico Rotella
 * Date Created: July 29th, 2026
 * Last Edited: August 19th, 2026
 */

// Class
public class CustomerCMD extends Command {

    // Enum
    private enum Action {CREATE, DELETE}

    // Attributes
    private Action action;
    private String name;


    // Constructor
    public CustomerCMD(String i, Inventory inv, Customers c) {
        super(i, inv, c);
    }


    /**
     * Description: Takes the input and parses through it, determining the action and the provided fields
     * Pre-Condition: Input should be a customer command string, if not will throw an exception related to where the issue was
     * Post-Condition: The input is parsed, action is decided, the fields are updated with their values
     * @throws IllegalArgumentException if the CUSTOMER command is malformed
     */
    @Override
    public void parse() throws IllegalArgumentException {
        String line = input.replace(";", "").trim();
        String[] tokens = line.split("\\s+");

        // Error, shouldn't get here but
        if (!tokens[0].equals("CUSTOMER")) {
            throw new IllegalArgumentException("Error, CUSTOMER command must start with CUSTOMER!");
        }

        // Customer name to run command on
        name = tokens[1];

        // Action being done
        try {
            action = Action.valueOf(tokens[2]);
        }
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(String.format("Error, %s is not a valid CUSTOMER command action! " +
                    "Expected CREATE or DELETE.", tokens[2]));
        }

        // Checking if there is anything else
        if (tokens.length > 3) {
            throw new IllegalArgumentException(String.format("Error, unexpected arguments after %s %s %s.",
                    tokens[0], tokens[1], tokens[2]));
        }

        // Checking if the action is possible
        // CREATE - Checking if there is already a customer with this name shopping
        if (action == Action.CREATE && customers.hasCustomer(name))
            throw new IllegalArgumentException(String.format("Error, %s is already a shopper.", name));
        // DELETE - Checking if this customer is actually a shopper
        else if (action == Action.DELETE && !customers.hasCustomer(name))
            throw new IllegalArgumentException(String.format("Error, %s is not a shopper.", name));
    }

    /**
     * Description: Actually runs the command, updating as required and setting output
     * Pre-Condition: Parse has been run on this command
     * Post-Condition: Output has been set and command has been run
     */
    @Override
    public void run() {
        switch (action) {
            case CREATE -> create();
            case DELETE -> delete();
            default -> output = "Error, this was not a valid CUSTOMER command.";
        }
    }

    /**
     * Description: Creates a customer with the given name and sets the output
     * Pre-Condition: Parse has already been called on the command object
     * Post-Condition: A customer with the given name has been created
     */
    private void create() {
        customers.addCustomer(new Customer(name)); // Marks dirty
        output = String.format("customer %s created", name);
    }

    /**
     * Description: Deletes the customer with the given name from shoppers
     * Pre-Condition: Parse has already been called on the command object
     * Post-Condition: The customer with the given name has been removed from shoppers
     */
    private void delete() {
        customers.removeCustomerByName(name); // Marks deleted
        output = String.format("customer %s deleted", name);
    }

}
