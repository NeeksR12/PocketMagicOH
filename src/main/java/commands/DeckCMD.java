package commands;

import databases.Customers;
import databases.Inventory;
import entities.Customer;
import entities.products.Deck;
import utils.Utils;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * DeckCMD
 * Description: Deck command to handle all actions related to manipulating a deck
 * Name: Nico Rotella
 * Date Created: August 24th, 2026
 * Last Edited: August 28th, 2026
 */

// Class
public class DeckCMD extends Command {

    // Enum
    private enum Action {ADD, REMOVE, CLEAR, DELETE}

    // Attributes
    private Action action;
    private String deckName;
    private Customer customer;
    private Deck deck;
    private final Map<String, String> updates = new HashMap<>();


    // Constructor
    public DeckCMD(String i, Inventory inv, Customers c) {
        super(i, inv, c);
    }


    /**
     * Description: Takes the input and parses through it, determining the action and the provided fields
     * Pre-Condition: Input should be a deck command string, if not will throw an exception related to where the issue was
     * Post-Condition: The input is parsed, action is decided, the fields are updated with their values
     * @throws IllegalArgumentException if the DECK command is malformed
     */
    @Override
    public void parse() throws IllegalArgumentException {
        String line = input.replace(";", "").trim();
        String[] tokens = line.split("\\s+");
        int counter = 6; // Counter for conditionally iterating through extra updates

        // Error, shouldn't get here but
        if (!tokens[0].equals("DECK")) {
            throw new IllegalArgumentException("Error, DECK command must start with DECK!");
        }

        // Check if customer name is actually a customer
        try {
            customer = customers.getCustomerByName(tokens[1]);
        }
        catch (NoSuchElementException e) {
            throw new IllegalArgumentException("Error, this customer is not a shopper.");
        }

        // Deck name to run command on
        deckName = tokens[2];

        // Action being done with the deck
        try {
            action = Action.valueOf(tokens[3]);
        }
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(String.format("Error, %s is not a valid DECK command action! " +
                    "Expected ADD, REMOVE, CLEAR, or DELETE.", tokens[3]));
        }

        // Checking the desired updates (ADD and REMOVE)
        if (action == Action.ADD || action == Action.REMOVE) {
            // The next two arguments
            if (tokens.length >= 6) {
                updates.put(tokens[5], tokens[4]);
            }
            else { // Malformed command
                throw new IllegalArgumentException("Error, command not formatted correctly, expected a quantity" +
                        " followed by a card.");
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
                            + " Expected \"AND quantity card\"", tokens[counter - 1]));
                }
                counter += 3;
            } // Bonus updates
        } // Adding updates

        // Checking that the updates are valid
        switch (action) {
            case ADD -> {
                // Setting the deck if the customer has it already
                if (customer.hasDeck(deckName)) // Already existed
                    deck = customer.getDeckByName(deckName);

                // Checking updates
                for (var entry : updates.entrySet()) {
                    // Checking if this is a card in the inventory
                    if (!inventory.hasCard(entry.getKey())) {
                        throw new IllegalArgumentException(String.format("Error, %s is not a card being sold at the " +
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
                // Checking if the customer has this deck
                try {
                    deck = customer.getDeckByName(deckName);
                } catch (NoSuchElementException e) {
                    throw new IllegalArgumentException("Error, the customer does not have the deck that is trying " +
                            "to be updated.");
                }

                // Checking updates
                for (var entry : updates.entrySet()) {
                    // Checking that this is a card in this deck
                    if (!deck.hasProduct(entry.getKey())) { // This is only cards since deck implements pg<Card>
                        throw new IllegalArgumentException(String.format("Error, %s is not a card in %s's deck.",
                                entry.getKey(), deck.getName()));
                    }
                    // Checking that their desired quantity was a positive integer
                    if (!Utils.isNumeric(entry.getValue())) { // Was not a number
                        throw new IllegalArgumentException(String.format("Error, the quantity requested for %s " +
                                "must be a positive integer.", entry.getKey()));
                    } else if (Integer.parseInt(entry.getValue()) < 0) { // Was a number but was negative
                        throw new IllegalArgumentException(String.format("Error, desired quantity to ADD of %s " +
                                "must be positive.", entry.getKey()));
                    }
                    // Checking that it is possible to remove that many of this card from the deck
                    if (deck.quantityOf(entry.getKey()) < Integer.parseInt(entry.getValue())) {
                        throw new IllegalArgumentException(String.format("Error, cannot remove %s of %s from %s since" +
                                        " it only contains %d.", entry.getValue(), entry.getKey(), deck.getName(),
                                deck.quantityOf(entry.getKey())));
                    }
                }
            } // REMOVE
            case CLEAR -> {
                // Checking if the customer has this deck
                try {
                    deck = customer.getDeckByName(deckName);
                } catch (NoSuchElementException e) {
                    throw new IllegalArgumentException("Error, the customer does not have the deck that is trying " +
                            "to be updated.");
                }

                // Checking that there are no more arguments after the action
                if (tokens.length > 4) {
                    throw new IllegalArgumentException(String.format("Error, unexpected arguments after %s %s %s %s.",
                            tokens[0], tokens[1], tokens[2], tokens[3]));
                }
            } // CLEAR
            case DELETE -> {
                // Checking if the customer has this deck
                try {
                    deck = customer.getDeckByName(deckName);
                } catch (NoSuchElementException e) {
                    throw new IllegalArgumentException("Error, the customer does not have the deck that is trying " +
                            "to be updated.");
                }
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
        customers.markDirty(customer);
        customer.markDeckDirty(deck);
        switch (action) {
            case ADD -> add();
            case REMOVE -> remove();
            case CLEAR -> clear();
            case DELETE -> delete();
            default -> output = "Error this was not a valid DECK command.";
        }
    }

    /**
     * Description: Adds the desired updates to the deck and sets the output
     * Pre-Condition: Parse has already been called on the command object
     * Post-Condition: The items have been added to the deck and the output has been set
     */
    private void add() {
        // Adding the deck if it was new
        if (deck.getId() == null)
            customer.addDeck(deck);

        // Adding the updates to the deck
        for (var entry : updates.entrySet()) {
            deck.add(inventory.getCardByName(entry.getKey()),
                    Integer.parseInt(entry.getValue()));
        }
        output = String.format("%s deck %s updated", customer.getName(), deckName);
    }

    /**
     * Description: Removes the desired updates from the deck and sets the output
     * Pre-Condition: Parse has already been called on the command object
     * Post-Condition: The items have been removed from the deck and the output has been set
     */
    private void remove() {
        for (var entry : updates.entrySet()) {
            deck.remove(inventory.getCardByName(entry.getKey()),
                    Integer.parseInt(entry.getValue()));
        }
        output = String.format("%s deck %s updated", customer.getName(), deckName);
    }

    /**
     * Description: Clears the customers deck and sets the output
     * Pre-Condition: Parse has already been called on the command object
     * Post-Condition: The deck has been cleared and the output has been set
     */
    private void clear() {
        deck.empty();
        output = String.format("%s deck %s cleared", customer.getName(), deckName);
    }

    /**
     * Description: Deletes a deck from a customer and sets the output
     * Pre-Condition: Parse has already been called on the command object
     * Post-Condition: The deck has been deleted and the output has been set
     */
    private void delete() {
        customer.deleteDeckByName(deckName); // Won't throw if parse called
        output = String.format("%s deck %s deleted", customer.getName(), deckName);
    }

}
