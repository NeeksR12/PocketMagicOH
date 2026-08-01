package databases;


import entities.Cart;
import entities.products.Card;
import entities.products.Product;

import java.util.ArrayList;
import java.util.NoSuchElementException;

/**
 * Inventory
 * Description: Contains the stores inventory of cards and related methods to manipulate it
 * Name: Nico Rotella
 * Date Created: May 25th, 2026
 * Last Edited: July 29th, 2026
 */
public class Inventory {

    // Attributes
    private final ArrayList<Product> inv = new ArrayList<Product>();

    // Constructor
    public Inventory() {}


    // General Product Operations
    /**
     * Description: Checks if a product is in the inventory and returns a boolean
     * Pre-Condition: Inventory is initialized
     * Post-Condition: Boolean is returned
     * @param name The name of the product
     * @return The boolean if found
     */
    public boolean hasProduct(String name) {
        for (Product p : inv) {
            if (p.getName().equals(name))
                return true;
        }
        return false;
    }

    /**
     * Description: Looks for a product in the inventory by its name
     * Pre-Condition: Inventory is initialized
     * Post-Condition: Product is returned or exception is thrown
     * @param name The name of the product being searched for
     * @return The product if found
     * @throws NoSuchElementException if not found
     */
    public Product getProductByName(String name) throws NoSuchElementException {
        for (Product p : inv) {
            if (p.getName().equals(name))
                return p; // Found
        }
        throw new NoSuchElementException("Error, this product is not in the inventory."); // Not found
    }

    /**
     * Description: Adds a product to inventory
     * Pre-Condition: Inventory and Product are initialized and product is not already in the inventory
     * Post-Condition: Product is added or exception is thrown
     * @param p The product
     * @throws IllegalArgumentException if the card is already in the inventory
     */
    public void addProduct(Product p) {
        if (!hasProduct(p.getName()))
            inv.add(p);
        else
            throw new IllegalArgumentException("Error, this product is already in the inventory therefore cannot be added.");
    }

    /**
     * Description: Removes a product from the inventory
     * Pre-Condition: Inventory is initialized
     * Post-Condition: The product is removed from the inventory
     * @param name The name of the product being removed
     * @throws IllegalArgumentException if the product is not in the inventory
     */
    public void removeProductByName(String name) throws IllegalArgumentException {
        if (hasProduct(name))
            inv.remove(getProductByName(name));
        else
            throw new IllegalArgumentException("Error, this product is not in the inventory therefore cannot be removed");
    }


    // Card Specific Operations
    /**
     * Description: Checks if a card is in the inventory and returns a boolean
     * Pre-Condition: Inventory is initialized
     * Post-Condition: Boolean is returned
     * @param name The name of the card
     * @return The boolean if found
     */
    public Boolean hasCard(String name) {
        for (Product p : inv) {
            if (p instanceof Card c) { // NOTE: This is called pattern notation, no need to declare and instantiate later
                if (c.getName().equals(name))
                    return true;
            }
        }
        return false;
    }

    /**
     * Description: Looks for a card in the inventory by its name
     * Pre-Condition: Inventory is initialized
     * Post-Condition: Card is returned or exception is thrown
     * @param name The name of the card being searched for
     * @return The card if found
     * @throws NoSuchElementException if not found
     */
    public Card getCardByName(String name) throws NoSuchElementException{
        for (Product p : inv) {
            if (p instanceof Card c) {
                if (c.getName().equals(name))
                    return c; // Found
            }
        }
        throw new NoSuchElementException("Error, this card is not in the inventory."); // Not found
    }

    /**
     * Description: Returns the element of a card in inventory
     * Pre-Condition: Inventory is initialized
     * Post-Condition: Stock is returned or error is thrown
     * @param name The name of the card
     * @return The element of the card if found
     * @throws NoSuchElementException if the card is not in inventory
     */
    public String getCardElementByName(String name) throws NoSuchElementException {
        for (Product p : inv) {
            if (p instanceof Card c) {
                if (c.getName().equals(name))
                    return c.getElement();
            }
        }
        throw new NoSuchElementException("Error, this card is not in the inventory.");
    }

    /**
     * Description: Returns the rarity of a card in inventory
     * Pre-Condition: Inventory is initialized
     * Post-Condition: Stock is returned or error is thrown
     * @param name The name of the card
     * @return The rarity of the card if found
     * @throws NoSuchElementException if the card is not in inventory
     */
    public String getCardRarityByName(String name) throws NoSuchElementException {
        for (Product p : inv) {
            if (p instanceof Card c) {
                if (c.getName().equals(name))
                    return c.getRarity();
            }
        }
        throw new NoSuchElementException("Error, this card is not in the inventory.");
    }

    /**
     * Description: Returns the price of a card in inventory
     * Pre-Condition: Inventory is initialized
     * Post-Condition: Stock is returned or error is thrown
     * @param name The name of the card
     * @return The price of the card if found
     * @throws NoSuchElementException if the card is not in inventory
     */
    public int getPriceByName(String name) throws NoSuchElementException {
        for (Product p : inv) {
            if (p instanceof Card c) {
                if (c.getName().equals(name))
                    return c.getPrice();
            }
        }
        throw new NoSuchElementException("Error, this card is not in the inventory.");
    }

    /**
     * Description: Returns the stock of a card in inventory
     * Pre-Condition: Inventory is initialized
     * Post-Condition: Stock is returned or error is thrown
     * @param name The name of the card
     * @return The stock of the card if found
     * @throws NoSuchElementException if the card is not in inventory
     */
    public int getStockByName(String name) throws NoSuchElementException {
        for (Product p : inv) {
            if (p instanceof Card c) {
                if (c.getName().equals(name))
                    return c.getStock();
            }
        }
        throw new NoSuchElementException("Error, this card is not in the inventory.");
    }

    /**
     * Description: Checks if there is stock in the inventory
     * Pre-Condition: The inventory object is initialized
     * Post-Condition: A boolean is returned true if there is stock and false if not
     * @return The boolean if there is stock
     */
    public boolean hasStock() {
        return !inv.isEmpty();
    }


    // General Class Operations
    /**
     * Description: Removes the stock from the inventory from the items in the given cart
     * Pre-Condition: Desired quantities in cart should be greater than the stock of the product
     * Post-Condition: Stock is removed from inventory or exception is thrown
     * @param c The cart
     * @throws IllegalArgumentException if the cart desired more of a product than it is stocked
     */
    public void purchaseCart(Cart c) throws IllegalArgumentException {
        // Checking stock availability
        for (var entry : c.getItems().entrySet()) {
            Product product = entry.getKey();
            Integer quantity = entry.getValue();

            // Checking if there is less stock than desired
            if (product.getStock() < quantity) {
                throw new IllegalArgumentException("Error, checkout could not be completed due to stock.");
            }
        }

        // Sufficient stock, removing stock from inventory
        for (var entry : c.getItems().entrySet()) {
            entry.getKey().removeStock(entry.getValue()); // Shouldn't throw since checked above
        }
    }

    /**
     * Description: Gives the string value of the inventory, this is what should be in the text file
     * Pre-Condition: This inventory is initialized
     * Post-Condition: String is returned
     * @return The string value of the inventory
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (Product p : inv) {
            sb.append(p.toString());
            sb.append("\n");
        }

        return sb.toString();
    }

    // Getter
    public ArrayList<Product> getInv() {
        return inv;
    }

}
