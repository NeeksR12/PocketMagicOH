package commands;

import databases.*;
import entities.products.Product;
import entities.products.items.Item;

import java.util.ArrayList;
import java.util.List;

/**
 * ReportInventoryCMD
 * Description: Command for reporting the inventory
 * Name: Nico Rotella
 * Date Created: May 22nd, 2026
 * Last Edited: August 19th, 2026
 */
public class ReportInventoryCMD extends Command{

    // Constructor
    public ReportInventoryCMD(String i, Inventory inv, Customers c) {
        super(i, inv, c);
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

        // Variables and objects
        StringBuilder sb = new StringBuilder();
        List<Product> nonItems = new ArrayList<>();

        sb.append("\nINVENTORY\n");
        if (inventory.hasStock()) {
            for (Product p : inventory.getProducts().values()) {
                if (p instanceof Item i)
                    sb.append(String.format("%s %d\n", i.getName(), i.getStock()));
                    // NOTE: Less resource intensive than +=, += creates a new string builder each iteration
                else
                    nonItems.add(p);
            }
            for (Product p : nonItems) {
                sb.append(String.format("%s\n", p.getName()));
            }
            output = sb.toString();
        }
        else {
            output = "Out of stock";
        }
    }

}
