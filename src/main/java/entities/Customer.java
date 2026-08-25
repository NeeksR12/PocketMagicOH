package entities;


import entities.products.Deck;

import java.util.*;

/**
 * Customer
 * Description: A customer object which can hold carts and decks
 * Name: Nico Rotella
 * Date Created: July 26th, 2026
 * Last Edited: August 24th, 2026
 */

// Class
public class Customer {

    // Attributes
    private final String name;
    private Cart cart = new Cart(); // Potentially make a set later
    private Map<String, Deck> decks = new HashMap<String, Deck>(); // name -> deck
    private Integer id;

    // Constructor
    public Customer(String n) {
        name = n;
    }

    /**
     * Description: Gives the string value of the customer, this is what is to be displayed in Customers
     * Pre-Condition: This Customer is initialized
     * Post-Condition: String is returned
     * @return the string value of the customer
     */
    @Override
    public String toString() {
        return name + cart + "\n";
    }

    /**
     * Description: Adds a deck to this customer's decks
     * Pre-Condition: Customer and deck are already initialized and the customer does not already have this deck
     * Post-Condition: The deck is added to this customer's decks
     * @param d The deck being added
     * @throws IllegalArgumentException if the customer already has this deck
     */
    public void addDeck(Deck d) {
        if (!hasDeck(d.getName())) {
            decks.put(d.getName(), d);
        }
        else
            throw new IllegalArgumentException("Error, this customer already has this deck therefore it cannot be added.");
    }

    /**
     * Description: Checks if a customer has a deck and returns a boolean
     * Pre-Condition: Customer is initialized
     * Post-Condition: Boolean is returned
     * @param name The name of the deck
     * @return The boolean if found
     */
    public boolean hasDeck(String name) {
        return decks.containsKey(name);
    }

    /**
     * Description: Looks for a deck in the customer by its name
     * Pre-Condition: Customer is initialized
     * Post-Condition: Deck is returned or exception is thrown
     * @param name The name of the deck being searched for
     * @return The deck if found
     * @throws NoSuchElementException if not found
     */
    public Deck getDeckByName(String name) {
        Deck d = decks.get(name);

        if (d == null)
            throw new NoSuchElementException("Error, this customer already has this deck."); // Not found
        else
            return d; // Found
    }

    // Getters
    public String getName() {
        return name;
    }

    public Cart getCart() {
        return cart;
    }

    public Integer getId() {
        return id;
    }

    // Setter
    public void setCart(Cart c) {
        cart = c;
    }

    public void setId(Integer i) {
        id = i;
    }

    public void setDecks(Set<Deck> d) {
        decks = d;
    }

}
