import java.sql.*;
import java.util.*;

import commands.*;
import databases.*;
import utils.*;

/**
 * StoreFront
 * Description: Main class for Pocket Magic Oh. Contains the inventory system
 * Name: Nico Rotella
 * Date Created: May 5th, 2026
 * Last Edited: August 28th, 2026
 */

// Class
public final class StoreFront {

    // Static fields
    static Inventory inventory = new Inventory(); // Initialized in open store method
    static Customers customers = new Customers();
    static List<Command> commands = new ArrayList<>();
    static List<String> incomingCommands = new ArrayList<>();

    // main
    public static void main(String[] args) {

        // Objects and variables
        Scanner s = new Scanner(System.in);
        StringBuilder sb = new StringBuilder();
        String input, command, output;


        // Establishing DB connection
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:PocketMagicOH.db")) {

            // Running program
            try {
                conn.setAutoCommit(false); // In case of error, no partial override occurs

                // Filling the inventory array list with cards
                openStore(conn);

                // Taking input from the user
                do {
                    sb.append(s.nextLine());
                    input = sb.toString();
                } while (!input.contains("REPORT INVENTORY;"));

                // Empty String builder
                sb.setLength(0);

                // Gathering their commands
                for (int i = 0; i < input.length(); i++) {

                    // Getting their command
                    sb.append(input.charAt(i));
                    command = sb.toString();

                    // Seeing if the command is complete
                    if (command.contains(";")) {
                        incomingCommands.add(command.trim());
                        sb.setLength(0);
                    }
                }

                // Creating their commands
                for (String incomingCommand : incomingCommands) {
                    createCommand(incomingCommand);
                }

                // Updates and outputs
                for (Command c : commands) {
                    try {
                        c.parse();
                        c.run();
                        sb.append(c.getOutput()).append("\n"); // Adding result of command output to actual output
                    } catch (IllegalArgumentException e) {
                        sb.append(e.getMessage()).append("\n");
                    }
                }

                // Assigning actual output
                output = sb.toString();

                // Display output
                System.out.println(output);

                // Updating the text files
                closeStore(conn);
                conn.commit(); // Committing changes

            }
            catch(SQLException e){ // Error with DB updates, rollback
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) { // Error with rollback
                    System.out.println("Error with rollback: " + rollbackEx.getMessage());
                }
                System.out.println("Save failed, changes were rolled back: " + e.getMessage());
            }
            finally{
                try {
                    conn.setAutoCommit(true);
                }
                catch (SQLException ignored) {}
            } // Running program
        } // Establishing connection
        catch (SQLException e) {
            System.out.printf("""
                    Error connecting to the database.
                    Error message: %s
                    """, e.getMessage());
        }
    } // Main

    /**
     * Description: Takes all the info stored in the databases and saves them to their objects
     * Pre-Condition: None
     * Post-Condition: The objects are set up
     */
    private static void openStore(Connection conn) {
        // If they don't already exist, creating the tables in the database
        Utils.createTables(conn);

        InventoryRepository.fillInventory(conn, inventory);
        CustomersRepository.fillCustomers(conn, customers, inventory);
    } // openStore

    /**
     * Description: Updates the DB with all the updates
     * Pre-Condition: Connection is set up properly
     * Post-Condition: DB is updated
     * @param conn The connection to the DB
     * @throws SQLException if there is an SQL issue
     */
    private static void closeStore(Connection conn) throws SQLException{
        // Updating DB
        InventoryRepository.save(conn, inventory);
        CustomersRepository.save(conn, customers);
    }

    /**
     * Description: Creates a command object for the type of command run and adds it to the command list
     * Pre-Condition: Param is a string
     * Post-Condition: Command is selected and created
     * @param input The command from start to semicolon
     */
    private static void createCommand(String input) {

        // Variables
        String keyword = input.split(" ")[0];

        // Report inventory command
        if (input.equals("REPORT INVENTORY;")) {
            commands.add(new ReportInventoryCMD(input, inventory, customers)); // Bookmark
        }
        else {
            try {
                CommandType type = CommandType.valueOf(keyword); // Enum value check for keyword
                commands.add(type.create(input, inventory, customers));
            }
            catch (IllegalArgumentException e) {
                commands.add(new InvalidCMD(input, inventory, customers));
            }
        }
    }

}
