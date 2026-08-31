package commands;

import databases.Customers;
import databases.Inventory;

import java.util.ArrayList;
import java.util.List;

/**
 * QueryCMD
 * Description: Query command to filter through cards in the inventory
 * Name: Nico Rotella
 * Date Created: August 29th, 2026
 * Last Edited: August 29th, 2026
 */

// Class
public class QueryCMD extends Command {

    // Attributes


    // Constructor
    public QueryCMD(String i, Inventory inv, Customers c) {
        super(i, inv, c);
    }

    /**
     * Description: Takes the input and parses through it, determining the action and the provided fields
     * Pre-Condition: Input should be a query command string, if not will throw an exception related to where the issue was
     * Post-Condition: The input is parsed, action is decided, the fields are updated with their values
     * @throws IllegalArgumentException if the QUERY command is malformed
     */
    @Override
    public void parse() throws IllegalArgumentException {
        String line = input.replace(";", "").trim();
        String[] tokens = line.split("\\s+");
        List<String[]> conditions = new ArrayList<>();
        List<String> operators = new ArrayList<>();
        boolean parsingConditions = true, hasSorting = false;
        int counter = 3;

        // Checking that this is a full query command
        if (!(tokens[0].equals("QUERY") && tokens[1].equals("CARD") && tokens[2].equals("WHERE"))) {
            throw new IllegalArgumentException("Error, QUERY CARD WHERE command must start with QUERY CARD WHERE!");
        }

        // Collecting their query conditions
        while (parsingConditions) {

            // Checking that there are enough tokens left to be conditions
            if (tokens.length < counter + 3) { // Malformed command
                throw new IllegalArgumentException("Error, command not formatted correctly, expected field operator value");
            }

            // Collecting conditions
            conditions.add(new String[] {tokens[counter], tokens[counter + 1], tokens[counter + 2]});

            // Moving the counter
            counter += 3;

            // Checking if there is an operator or if the command is done
            if (tokens.length == counter) { // Command is finished
                parsingConditions = false;
            }
            else if (tokens[counter + 1].equals("AND") || tokens[counter + 1].equals("OR")) { // Expecting more conditions and an operator was provided
                operators.add(tokens[counter + 1]);

                // Moving the counter
                counter++;
            }
            else if (tokens[counter + 1].equals("LIMIT") || // Checking if their command is ending with a valid finish
                     (tokens.length >= counter + 2 && // Ensuring there is a 2nd token after to call in condition without throwing error
                      tokens[counter + 1].equals("ORDER") && tokens[counter + 2].equals("BY"))) {

                hasSorting = true;
                parsingConditions = false;
            }
            else {
                throw new IllegalArgumentException(String.format("Error, unexpected arguments %s %s %s.",
                        conditions.getLast()[0], conditions.getLast()[1], conditions.getLast()[2]));
            }
        }

        // Sorting query
        if (hasSorting) {
            // I would go through the order by and limit stuff here to determine
        }


        // Validating query conditions
        for (String[] condition : conditions) {
            // This would validate that the first token is a field, the second is an operator, the value makes sense
            // for that field, and that the operator is able to be used on that type
        }

    }

    /**
     * Description: Actually runs the command, updating as required and setting output
     * Pre-Condition: Parse has been run on this command
     * Post-Condition: Output has been set and command has been run
     */
    @Override
    public void run() {
        // This will call inventory and customer repository .save
        // Then will create an SQL command based on the query and build an output as required
    }


}
