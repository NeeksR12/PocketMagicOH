package commands;

import databases.Inventory;

/**
 * InvalidCMD
 * Description: Command to give output for invalid commands
 * Name: Nico Rotella
 * Date Created: May 22nd, 2026
 * Last Edited: July 25th, 2026
 */

public class InvalidCMD extends Command {

    // Constructor
    public InvalidCMD(String i, Inventory inv) {
        super(i, inv);
        output = "invalid command";
    }

    @Override
    public void parse() {}

    @Override
    public void run() {}

}
