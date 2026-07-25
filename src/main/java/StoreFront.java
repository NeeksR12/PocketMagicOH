import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import commands.*;
import entities.*;
import utils.*;

/**
 * StoreFront
 * Description: Main class for Pocket Magic Oh. Contains the inventory system
 * Name: Nico Rotella
 * Date Created: May 5th, 2026
 * Last Edited: July 25th 2026
 */

// Class
public class StoreFront {

    static Inventory inventory = new Inventory(); // Initialized in open store method
    static ArrayList<Command> commands = new ArrayList<Command>();
    static ArrayList<String> incomingCommands = new ArrayList<String>();

    // main
    public static void main(String[] args) {

        // Objects and variables
        Scanner s = new Scanner(System.in);
        StringBuilder sb = new StringBuilder();
        String input = "";
        String command = "";
        String output = "";


        // Filling the inventory array list with cards
        openStore();

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
            }
            catch (IllegalArgumentException e) {
                sb.append(e.getMessage()).append("\n");
            }
        }

        // Assigning actual output
        output = sb.toString();

        updateInventoryFile(); // Make a general update/complete all commands, this method can be called in that

        // Display output
        System.out.println(output);

    } // Main

    /**
     * Description: Fills the inventory with what the store has
     * Pre-Condition: Text file is accessible
     * Post-Condition: Inventory is set up
     */
    public static void openStore() {

        // Objects and Variables
        FileReader fr = null;
        Scanner sfr = null;
        boolean success = false;
        String line = "";

        // Card fields
        Map<String, String> fields = new HashMap<>();
        int price, stock; // Need to convert the string from the map to an int later

        try {
            fr = new FileReader("inventory.txt");
            sfr = new Scanner(fr);

            success = true;
        }
        catch (FileNotFoundException error) {
            System.out.println("Issue finding the file.");
        }

        if (success) { // Low security and won't work if file for some reason was not formatted right
            while (sfr.hasNextLine()) {
                line = sfr.nextLine();

                if (line.contains(": ")) {
                    String[] parts = line.split(": ", 2);
                    fields.put(parts[0], parts[1]);
                }
                else if (line.trim().isEmpty()){

                    price = Utils.isNumeric(fields.get("Price")) // Checks if numeric and then gives int value
                            ? Integer.parseInt(fields.get("Price"))
                            : 0;

                    stock = Utils.isNumeric(fields.get("Stock"))
                            ? Integer.parseInt(fields.get("Stock"))
                            : 0;

                    if (!(fields.get("Name") == null || fields.get("Element") == null || fields.get("Rarity") == null))
                        inventory.addCard(new Card(
                                fields.get("Name"),
                                fields.get("Element"),
                                fields.get("Rarity"),
                                price,
                                stock
                        ));

                    fields.clear();
                }
            } // While parsing
        } // Successful file reader
    } // openStore

    /**
     * Description: Updates the text file containing the inventory
     * Pre-Condition: Inventory object is declared and initialized
     * Post-Condition: The inventory is updated
     */
    public static void updateInventoryFile() {

        // Variables and Objects
        FileWriter fw = null;
        PrintWriter pw = null;
        boolean success = false;

        try {
            fw = new FileWriter("inventory.txt");
            pw = new PrintWriter(fw);

            success = true;
        }
        catch (IOException e) {
            System.out.println("Issue updating the inventory, could not access the file.");
        }

        if (success) {
            pw.println(inventory.toString());
            pw.close();
        }

    }

    /**
     * Description: Creates a command object for the type of command run and adds it to the command list
     * Pre-Condition: Param is a string
     * Post-Condition: Command is selected and created
     * @param input The command from start to semicolon
     */
    public static void createCommand(String input) {

        // Variables
        String keyword = input.split(" ")[0];

        // Report inventory command
        if (input.equals("REPORT INVENTORY;")) {
            commands.add(new ReportInventoryCMD(input, inventory));
        }
        else {
            try {
                CommandType type = CommandType.valueOf(keyword); // Enum value check for keyword
                commands.add(type.create(input, inventory));
            } catch (IllegalArgumentException e) {
                commands.add(new InvalidCMD(input, inventory));
            }
        }
    }

}
