package commands;

import databases.*;

/**
 * Command
 * Description: Parent class for commands, contains input and output as well as constructor and abstract methods
 * Name: Nico Rotella
 * Date Created: May 6th, 2026
 * Last Edited: July 28th, 2026
 */
public abstract class Command {

    // Attributes
    protected String input, output;
    protected Inventory inventory;
    protected Customers customers;

    // Constructor
    public Command(String i, Inventory inv, Customers c) {
        input = i;
        inventory = inv;
        customers = c;
    }

    // Abstract methods
    public abstract void parse() throws IllegalArgumentException;
    public abstract void run();

    // Getters
    public String getOutput() {
        return output;
    }
}
