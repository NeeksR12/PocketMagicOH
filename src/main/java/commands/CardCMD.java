package commands;

import entities.Card;
import entities.Inventory;
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
 * Last Edited: July 25th, 2026
 */
public class CardCMD extends Command {

    // Enums
    public enum Action {CREATE, UPDATE, DELETE}
    public enum CreateRequired {element, rarity, price, stock}

    // Required fields as a string
    private final static String requiredFields = Arrays.stream(CreateRequired.values()).map(Enum::name)
            .collect(Collectors.joining(", "));

    // Attributes
    private Action action;
    private String name;
    private final Map<String, String> fields = new HashMap<>();

    // Card
    private Card c;

    // Constructor
    public CardCMD(String i, Inventory inv) {
        super(i, inv);
    }


    /**
     * Description: Takes the input and parses through it, determining the action and the provided fields
     * Pre-Condition: Input should be a card command string, if not will throw an exception related to where the issue was
     * Post-Condition: The input is parsed, action is decided, the fields are updated with their values
     * @throws IllegalArgumentException if the first token isn't CARD, the action token isn't an action, or is malformed
     */
    @Override
    public void parse() throws IllegalArgumentException {
        // Grabbing the line, removing the semicolon, splitting at spaces.
        String line = input.replace(";", "").trim();
        String[] tokens = line.split("\\s+"); // Tokens being the words in the input
        boolean found = false; // Used for validating if a key was found for the update validation

        // Error, shouldn't get here but
        if (!tokens[0].equals("CARD")) {
            throw new IllegalArgumentException("CARD command must start with CARD!");
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
                    throw new IllegalArgumentException(String.format("Command not formated correctly, " +
                            "missing value for field: %s", tokens[i]));
                }
                fields.put(tokens[i], tokens[i + 1]);
            }
        }

        // Validating given fields
        switch (action) {
            case CREATE:
                // Check that they have each of the keys
                for (CreateRequired cr : CreateRequired.values()) {
                    if (!fields.containsKey(cr.name())) {
                        throw new IllegalArgumentException(String.format("Command does not contain key: %s", cr.name()));
                    }
                }
                // Check that the numeric fields are numeric
                if (!(Utils.isNumeric(fields.get("price")) && Utils.isNumeric(fields.get("stock")))) {
                    throw new IllegalArgumentException("Numeric values must be provided for price and stock.");
                }
                break;
            case UPDATE:
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
                break;
            // Don't need a case for delete, no arguments after action

        } // parse

    }

    /**
     * Description: Actually runs the command, setting the card and output
     * Pre-Condition: Parse has been run on this command
     * Post-Condition: Output and card object for this command are set
     */
    @Override
    public void run() {
        switch (action) {

            /*
            case CREATE -> create();
            case UPDATE -> update();
            case DELETE -> delete();
            //case null, default -> invalid command or something?

            // Need access to the inventory and just do the CUD directly to the inv instead of making new objects

             */
        }
    }

    /**
     * Description: Initializes and instantiates this card and output for this command object with CREATING card state
     * Pre-Condition: Parse has been called already on the command object
     * Post-Condition: This card and output is initialized and instantiated
     */
    /*
    private void create() {
        c = new Card(name, fields.get("element"), fields.get("rarity"),
                Integer.parseInt(fields.get("price")), Integer.parseInt(fields.get("stock")), Card.State.CREATING);
        output = String.format("card %s added", name);
    }

     */

    /**
     * Description: Initializes and instantiates this card and output for this command object with UPDATING state
     * Pre-Condition: Parse has been called already on the command object
     * Post-Condition: This card and output is initialized and instantiated
     */
    /*
    private void update() {
        c = new Card(name, fields.get("element"), fields.get("rarity"),
                Integer.parseInt(fields.get("price")), Integer.parseInt(fields.get("stock")), Card.State.UPDATING);
        output = String.format("card %s updated", name);
    }

     */

    /**
     * Description: Initializes and instantiates this card and output for this command object with the name,
     * DELETING state, and all null
     * Pre-Condition: Parse has been called already on this command object
     * Post-Condition: This card and output is initialized and instantiated
     */
    /*
    private void delete() {
        c = new Card(name);
        output = String.format("card %s deleted", name);
    }

     */

}
