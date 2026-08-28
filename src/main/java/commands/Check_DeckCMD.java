package commands;

import databases.Customers;
import databases.Inventory;
import entities.Customer;
import entities.products.Deck;

import java.util.NoSuchElementException;

/**
 * DeckCMD
 * Description: Command to check if a deck is legal or not
 * Name: Nico Rotella
 * Date Created: August 28th, 2026
 * Last Edited: August 28th, 2026
 */

public class Check_DeckCMD extends Command {

    // Attributes
    private Customer customer;
    private Deck deck;


    // Constructor
    public Check_DeckCMD(String i, Inventory inv, Customers c) {
        super(i, inv, c);
    }

    /**
     * Description: Takes the input and parses through it, determining the action and the provided fields
     * Pre-Condition: Input should be a check_deck command string, if not will throw an exception related to where the issue was
     * Post-Condition: The input is parsed, action is decided, the fields are updated with their values
     * @throws IllegalArgumentException if the CHECK_DECK command is malformed
     */
    @Override
    public void parse() throws IllegalArgumentException {
        String line = input.replace(";", "").trim();
        String[] tokens = line.split("\\s+");

        // Error, shouldn't get here but
        if (!tokens[0].equals("CHECK_DECK")) {
            throw new IllegalArgumentException("Error, CHECK_DECK command must start with DECK!");
        }

        // Check if the customer name is actually a customer
        try {
            customer = customers.getCustomerByName(tokens[1]);
        }
        catch (NoSuchElementException e) {
            throw new IllegalArgumentException("Error, this customer is not a shopper.");
        }

        // Check if the deck is actually a deck they have
        try {
            deck = customer.getDeckByName(tokens[2]);
        }
        catch (NoSuchElementException e) {
            throw new IllegalArgumentException(String.format("Error, %s does not have a deck called %s.", tokens[1],
                    tokens[2]));
        }

        // Checking that there are no more arguments after the command
        if (tokens.length > 3) {
            throw new IllegalArgumentException(String.format("Error, unexpected arguments after %s %s %s.",
                    tokens[0], tokens[1], tokens[2]));
        }
    } // parse

    /**
     * Description: Actually runs the command, updating as required and setting output
     * Pre-Condition: Parse has been run on this command
     * Post-Condition: Output has been set and command has been run
     */
    @Override
    public void run() {
        if (deck != null) { // In case called before parse
            if (deck.isLegal()) {
                output = String.format("%s %s legal", customer.getName(), deck.getName());
            }
            else {
                output = String.format("%s %s illegal", customer.getName(), deck.getName());
            }
        }
        else {
            output = "Error this was not a valid CHECK_DECK command.";
        }
    }

}
