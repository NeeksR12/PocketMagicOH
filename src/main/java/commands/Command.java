package commands;

import entities.Inventory;

/**
 * Command
 * Description: Parent class for commands, contains input and output as well as constructor and abstract methods
 * Name: Nico Rotella
 * Date Created: May 6th, 2026
 * Last Edited: July 25th, 2026
 */
public abstract class Command {

    // Attributes
    protected String input, output;
    protected Inventory inventory;

    // Constructor
    public Command(String i, Inventory inv) {
        input = i;
        inventory = inv;
    }

    // Abstract methods
    public abstract void parse() throws InterruptedException;
    public abstract void run();

    // Getters
    public String getOutput() {
        return output;
    }
}
