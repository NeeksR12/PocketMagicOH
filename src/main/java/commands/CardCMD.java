package commands;

import databases.*;
import entities.products.items.Card;
import utils.Utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * CardCMD
 * Description: Card Command to handle all actions related to card
 * Name: Nico Rotella
 * Date Created: May 6th, 2026
 * Last Edited: August 20th, 2026
 */
public class CardCMD extends Command {

    // Enums
    private enum Action {CREATE, UPDATE, DELETE}
    private enum CreateRequired {element, rarity, price, stock}

    // Required fields as a string
    private final static String requiredFields = Arrays.stream(CreateRequired.values()).map(Enum::name)
            .collect(Collectors.joining(", "));

    // Attributes
    private Action action;
    private String name;
    private final Map<String, String> fields = new HashMap<>();


    // Constructor
    public CardCMD(String i, Inventory inv, Customers c) {
        super(i, inv, c);
    }


    /**
     * Description: Takes the input and parses through it, determining the action and the provided fields
     * Pre-Condition: Input should be a card command string, if not will throw an exception related to where the issue was
     * Post-Condition: The input is parsed, action is decided, the fields are updated with their values
     * @throws IllegalArgumentException if the CARD command is malformed
     */
    @Override
    public void parse() throws IllegalArgumentException {
        // Grabbing the line, removing the semicolon, splitting at spaces.
        String line = input.replace(";", "").trim();
        String[] tokens = line.split("\\s+"); // Tokens being the words in the input
        boolean found = false; // Used for validating if a key was found for the update validation

        // Error, shouldn't get here but
        if (!tokens[0].equals("CARD")) {
            throw new IllegalArgumentException("Error, CARD command must start with CARD!");
        }

        // Card name to run command on
        name = tokens[1];

        // Action being done with the card
        try {
            action = Action.valueOf(tokens[2]);
        }
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(String.format("Error, %s is not a valid CARD command action! " +
                    "Expected CREATE, UPDATE, or DELETE.", tokens[2]));
        }

        // Checking their fields
        if (action == Action.CREATE || action == Action.UPDATE) {
            for (int i = 3; i < tokens.length; i += 2) {
                if (i + 1 >= tokens.length) {
                    throw new IllegalArgumentException(String.format("Error, command not formated correctly, " +
                            "missing value for field: %s", tokens[i]));
                }
                fields.put(tokens[i], tokens[i + 1]);
            }
        }

        // Validating given fields
        switch (action) {
            case CREATE -> {
                // Check that they have each of the keys
                for (CreateRequired cr : CreateRequired.values()) {
                    if (!fields.containsKey(cr.name())) {
                        throw new IllegalArgumentException(String.format("Error, command does not contain key: %s", cr.name()));
                    }
                }
                // Check that the numeric fields are numeric
                if (!(Utils.isNumeric(fields.get("price")) && Utils.isNumeric(fields.get("stock")))) {
                    throw new IllegalArgumentException("Error, numeric values must be provided for price and stock.");
                }
            } // CREATE
            case UPDATE -> {
                // Checking if the card is in the inventory
                if (!inventory.hasCard(name))
                    throw new IllegalArgumentException("Error, the card that is trying to be updated is not in" +
                            " the inventory.");

                // Checking if fields contains at least one of the fields to update
                for (CreateRequired cr : CreateRequired.values()) {
                    if (fields.containsKey(cr.name())) {
                        found = true;
                        break;
                    }
                }
                // Didn't find any
                if (!found) {
                    throw new IllegalArgumentException(String.format("CARD UPDATE command must contain at least one" +
                            " of the fields to update: %s", requiredFields));
                }
            } // UPDATE
            case DELETE -> {
                // Checking if the card is in the inventory
                if (!inventory.hasCard(name))
                    throw new IllegalArgumentException("Error, the card that is trying to be deleted is not in" +
                            " the inventory.");

                // Checking if any product groups contain the card
                if (customers.isProductInACustomer(name) || inventory.isProductInProductGroup(name))
                    throw new IllegalArgumentException("Error, cannot delete this card because it is already being" +
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
        switch (action) {
            case CREATE -> create();
            case UPDATE -> update();
            case DELETE -> delete();
            default -> output = "Error, this was not a valid CARD command.";
        }
    }

    /**
     * Description: Adds the card with the given fields to the inventory and sets the output
     * Pre-Condition: Parse has been called already on the command object
     * Post-Condition: The card has been added and the output has been set
     */
    private void create() {
        inventory.addProduct(new Card(name, fields.get("element"), fields.get("rarity"),
                Integer.parseInt(fields.get("price")), Integer.parseInt(fields.get("stock")))); // Marks dirty
        output = String.format("card %s added", name);
    }

    /**
     * Description: Updates any fields from the card that need to be updated and sets the output
     * Pre-Condition: Parse has been called already on the command object
     * Post-Condition: This card has been updated and the output has been set
     */
    private void update() {
        // Card
        Card c = inventory.getCardByName(name); // Should never throw since parse checks if it is in the inventory

        // Marking card dirty
        inventory.markDirty(c);

        // Updating any fields that need to be updated
        for (var entry : fields.entrySet()) {
            switch (entry.getKey()) {
                case "element" -> c.setElement(entry.getValue());
                case "rarity" -> c.setRarity(entry.getValue());
                case "price" -> c.setPrice(Integer.parseInt(entry.getValue())); // Numeric check happened in parse
                case "stock" -> c.setStock(Integer.parseInt(entry.getValue()));
            }
        }
        output = String.format("card %s updated", name);
    }

    /**
     * Description: Removes the card from the inventory and sets the output
     * Pre-Condition: Parse has been called already on this command object
     * Post-Condition: The card has been removed and the output has been set
     */
    private void delete() {
        customers.deleteProductFromAllCustomers(inventory.getCardByName(name)); // Failsafe, should never do anything
        inventory.deleteProductByName(name); // Marks deleted
        output = String.format("card %s deleted", name);
    }

}
