package databases;


import entities.Card;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.NoSuchElementException;

/**
 * Inventory
 * Description: Contains the stores inventory of cards and related methods to manipulate it
 * Name: Nico Rotella
 * Date Created: May 25th, 2026
 * Last Edited: July 25th, 2026
 */
public class Inventory {

    // Attributes
    private ArrayList<Card> inv = new ArrayList<Card>();

    // Constructors
    /**
     * Default constructor
     */
    public Inventory() {}

    /**
     * Param constructor, takes an array list and sets it to the store's inv
     * @param s The array list
     */
    public Inventory(ArrayList<Card> s) {
        inv = s;
    }

    /**
     * Param constructor, takes multiple card objects and fills the store's inv
     * @param cards As many cards as being added to the inventory
     */
    public Inventory(Card ... cards) {
        inv.addAll(Arrays.asList(cards));
    }

    /**
     * Description: Checks if a card is in the inventory and returns a boolean
     * Pre-Condition: Inventory is initialized
     * Post-Condition: Boolean is returned
     * @param name The name of the card
     * @return The boolean if found
     */
    public Boolean isCardInInventory(String name) {
        for (Card c : inv) {
            if (c.getName().equals(name))
                return true;
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
        for (Card c : inv) {
            if (c.getName().equals(name))
                return c; // Found
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
    public String getElementByName(String name) throws NoSuchElementException {
        for (Card c : inv) {
            if (c.getName().equals(name))
                return c.getElement();
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
    public String getRarityByName(String name) throws NoSuchElementException {
        for (Card c : inv) {
            if (c.getName().equals(name))
                return c.getRarity();
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
        for (Card c : inv) {
            if (c.getName().equals(name))
                return c.getPrice();
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
        for (Card c : inv) {
            if (c.getName().equals(name))
                return c.getStock();
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
     * Description: Adds a card to inventory
     * Pre-Condition: Inventory and Card are initialized and card is not already in the inventory
     * Post-Condition: Card is added or exception is thrown
     * @param c The card
     * @throws IllegalArgumentException if the card is already in the inventory
     */
    public void addCard(Card c) {
        if (!isCardInInventory(c.getName())) 
            inv.add(c);
        else 
            throw new IllegalArgumentException("Error, this card is already in the inventory therefore cannot be added.");
    }

    /**
     * Description: Removes a card from the inventory
     * Pre-Condition: Inventory is initialized
     * Post-Condition: The card is removed from the inventory
     * @param name The name of the card being removed
     * @throws IllegalArgumentException if the card is not in the inventory
     */
    public void removeCardByName(String name) throws IllegalArgumentException{
        if (isCardInInventory(name)) {
            for (Card c : inv) {
                if (c.getName().equals(name)) {
                    inv.remove(c);
                    break;
                }
            }
        }
        else {
            throw new IllegalArgumentException("Error, this card is not in the inventory therefore cannot be removed");
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

        for (Card c : inv) {
            sb.append(c.toString());
            sb.append("\n");
        }

        return sb.toString();
    }

    // Getter
    public ArrayList<Card> getInv() {
        return inv;
    }

}
