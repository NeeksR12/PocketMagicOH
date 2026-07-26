package commands;


import databases.Customers;
import databases.Inventory;
import entities.Customer;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * CartCMD
 * Description: Cart Command to handle all actions related to CART
 * Name: Nico Rotella
 * Date Created: July 26th, 2026
 * Last Edited: July 26th, 2026
 */

// Class
public class CartCMD extends Command {

    // Enums
    private enum Action {ADD, REMOVE, CLEAR}

    // Attributes
    private Action action;
    private Customer customer;
    private final Map<String, String> updates = new HashMap<String, String>();


    // Constructor, look at this later, definitely need customer list
    public CartCMD(String i, Inventory inv, Customers c) {
        super(i, inv, c);
    }





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
        } catch (NoSuchElementException e) {
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


        /*
        Must check that all updates are valid:
        Quantity is an int (value)
        Product is in inventory (key), could be more than just a card, could be out of stock, check must just verify
        it is sold
         */


    }



    @Override
    public void run() {

    }
}
