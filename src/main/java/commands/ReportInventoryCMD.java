package commands;

import entities.Card;
import databases.Inventory;

/**
 * ReportInventoryCMD
 * Description: Command for reporting the inventory
 * Name: Nico Rotella
 * Date Created: May 22nd, 2026
 * Last Edited: July 25th, 2026
 */
public class ReportInventoryCMD extends Command{

    // Static final string builder
    private static final StringBuilder sb = new StringBuilder();


    // Constructor
    /**
     * Parameter Constructor
     * @param i The input string (Should be "REPORT INVENTORY;")
     * @param inv The inventory being the list of cards at the store
     */
    public ReportInventoryCMD(String i, Inventory inv) {
        super(i, inv);
    }

    /**
     * Description: Checking that the received command is actually "REPORT INVENTORY;"
     * Pre-Condition: Input should be "REPORT INVENTORY;" or else throws IllegalArgumentException
     * Post-Condition: Command is validated
     * @throws IllegalArgumentException if the input is not correct
     */
    @Override
    public void parse() throws IllegalArgumentException {
        // Making sure this is actually the right command (Shouldn't get here)
        if (!input.equals("REPORT INVENTORY;"))
            throw new IllegalArgumentException("REPORT INVENTORY command must be exactly \"REPORT INVENTORY;\"");
    }

    /**
     * Description: Actually runs the command and creates the output
     * Pre-Condition: Parse should have been run and inventory is actually filled with cards
     * Post-Condition: Output is set with the inventory
     */
    @Override
    public void run() {
        if (inventory.hasStock()) {
            for (Card c : inventory.getInv()) {
                sb.append(String.format("%s %d\n", c.getName(), c.getStock()));
                // NOTE: Less resource intensive than +=, += creates a new string builder each iteration
            }
            output = sb.toString();
        }
        else {
            output = "Out of stock";
        }
    }

}
