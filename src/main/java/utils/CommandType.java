package utils;

import commands.*;
import entities.Inventory;

/**
 * CommandType
 * ENUM that contains a way to create a command object for each type of command
 * Name: Nico Rotella
 * Date Created: May 22nd, 2026
 * Last Edited: July 25th, 2026
 */
public enum CommandType {

    CARD {
        @Override
        public Command create(String input, Inventory inventory) {
            return new CardCMD(input, inventory);
        }
    }; // , between different enum inheritors

    public abstract Command create(String input, Inventory inventory);
}
