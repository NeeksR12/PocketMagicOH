package commands;

import databases.*;

/**
 * InvalidCMD
 * Description: Command to give output for invalid commands
 * Name: Nico Rotella
 * Date Created: May 22nd, 2026
 * Last Edited: July 28th, 2026
 */

public class InvalidCMD extends Command {

    // Constructor
    public InvalidCMD(String i, Inventory inv, Customers c) {
        super(i, inv, c);
        output = "invalid command";
    }

    @Override
    public void parse() {}

    @Override
    public void run() {}

}
