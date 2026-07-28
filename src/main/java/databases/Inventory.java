package databases;


import entities.products.Card;
import entities.products.Product;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.NoSuchElementException;

/**
 * Inventory
 * Description: Contains the stores inventory of cards and related methods to manipulate it
 * Name: Nico Rotella
 * Date Created: May 25th, 2026
 * Last Edited: July 28th, 2026
 */
public class Inventory {

    // Attributes
    private ArrayList<Product> inv = new ArrayList<Product>();

    // Constructors
    /**
     * Default constructor
     */
    public Inventory() {}

    /**
     * Param constructor, takes an array list and sets it to the store's inv
     * @param i The array list
     */
    public Inventory(ArrayList<Product> i) {
        inv = i;
    }

    /**
     * Param constructor, takes multiple card objects and fills the store's inv
     * @param products As many products as being added to the inventory
     */
    public Inventory(Product ... products) {
        inv.addAll(Arrays.asList(products));
    }

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
            throw new IllegalArgumentException("Error, this card is already in the inventory therefore cannot be added.");
    }

    /**
     * Description: Removes a product from the inventory
     * Pre-Condition: Inventory is initialized
     * Post-Condition: The product is removed from the inventory
     * @param name The name of the product being removed
     * @throws IllegalArgumentException if the product is not in the inventory
     */
    public void removeProductByName(String name) throws IllegalArgumentException{
        if (hasCard(name)) {
            for (Product p : inv) {
                if (p.getName().equals(name)) {
                    inv.remove(p);
                    break;
                }
            }
        }
        else {
            throw new IllegalArgumentException("Error, this card is not in the inventory therefore cannot be removed");
        }
    }

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
