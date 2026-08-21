package utils;

import commands.*;
import databases.*;

/**
 * CommandType
 * ENUM that contains a way to create a command object for each type of command
 * Name: Nico Rotella
 * Date Created: May 22nd, 2026
 * Last Edited: August 20th, 2026
 */

// Enum
public enum CommandType {

    CARD {
        @Override
        public Command create(String input, Inventory inventory, Customers customers) {
            return new CardCMD(input, inventory, customers);
        }
    },
    CART {
        @Override
        public Command create(String input, Inventory inventory, Customers customers) {
            return new CartCMD(input, inventory, customers);
        }
    },
    CUSTOMER {
        @Override
        public Command create(String input, Inventory inventory, Customers customers) {
            return new CustomerCMD(input, inventory, customers);
        }
    },
    CHECKOUT {
        @Override
        public Command create(String input, Inventory inventory, Customers customers) {
            return new CheckoutCMD(input, inventory, customers);
        }
    },
    BUNDLE {
        @Override
        public Command create(String input, Inventory inventory, Customers customers) {
            return new BundleCMD(input, inventory, customers);
        }
    }; // , between different enum inheritors

    // Method to create a command
    public abstract Command create(String input, Inventory inventory, Customers customers);

}
