package commands;

import entities.Inventory;

/**
 * InvalidCMD
 * Description: Command to give output for invalid commands
 * Name: Nico Rotella
 * Date Created: May 22nd, 2026
 * Last Edited: May 22nd, 2026
 */

public class InvalidCMD extends Command {

    // Constructor
    public InvalidCMD(String i, Inventory inv) {
        super(i, inv);
        output = "invalid command\n";
    }

    @Override
    public void parse() {}

    @Override
    public void run() {}

}
