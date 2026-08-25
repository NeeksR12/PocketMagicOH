package entities.products;

import entities.products.items.Card;

import java.util.HashMap;
import java.util.Map;

/**
 * Deck
 * Description: A collection of cards that are intended to be played together in the PMO Game
 * Name: Nico Rotella
 * Date Created: August 20th, 2026
 * Last Edited: August 24th, 2026
 */

// Class
public class Deck implements ProductGroup<Card, Card> {

    // Attributes
    private final Map<Card, Integer> cards = new HashMap<>();
    private final String name;
    private Integer id;


    // Constructors
    public Deck(String n) {
        name = n;
    }


    /**
     * Description: Adds or updates a deck card
     * Pre-Condition: Params are of correct types
     * Post-Condition: The deck is updated to reflect the new cards
     * @param card The card being added to the deck
     * @param quantity The quantity of the card being added to the deck
     */
    @Override
    public void add(Card card, Integer quantity) {
        cards.merge(card, quantity, Integer::sum);
    }

    /**
     * Description: Removes and/or deletes a deck card
     * Pre-Condition: Params are of correct types
     * Post-Condition: The deck is updated to reflect the removed cards
     * @param card The card being removed from the deck
     * @param quantity The quantity of the card being removed from the deck
     */
    @Override
    public void remove(Card card, Integer quantity) {
        // Taking that card out of their deck
        cards.computeIfPresent(card, (k, v) -> v - quantity);
        // If the card has no more quantity, removing it from their deck
        if (cards.get(card) <= 0)
            cards.remove(card);
    }

    /**
     * Description: Deletes a card from the deck entirely
     * Pre-Condition: Param is a card
     * Post-Condition: The deck has this card deleted from it
     * @param card The card being deleted
     */
    @Override
    public void delete(Card card) {
        cards.remove(card);
    }

    /**
     * Description: Deletes a card from the deck entirely, by name
     * Pre-Condition: Param is a string
     * Post-Condition: The deck has this card deleted from it
     * @param name The name of the card being deleted
     */
    @Override
    public void deleteByName(String name) {
        for (Card c : cards.keySet()) {
            if (c.getName().equals(name))
                cards.remove(c);
        }
    }

    /**
     * Description: Empties the deck
     * Pre-Condition: None
     * Post-Condition: The deck is cleared and has zero cards
     */
    @Override
    public void empty() {
        cards.clear();
    }

    /**
     * Description: Checks if a card is in the deck and returns a boolean
     * Pre-Condition: Inventory is initialized
     * Post-Condition: Boolean is returned
     * @param name The name of the card
     * @return The boolean if found
     */
    @Override
    public boolean hasProduct(String name) {
        for (Card c : cards.keySet()) {
            if (c.getName().equals(name))
                return true;
        }
        return false;
    }

    /**
     * Description: Gives the quantity of a desired card by its name
     * Pre-Condition: card with the given name should be in the deck
     * Post-Condition: Quantity of card in deck is returned
     * @param name The name of the card
     * @return The quantity of the card in the deck
     * @throws IllegalArgumentException if there is no card with that name in the deck
     */
    @Override
    public Integer quantityOf(String name) throws IllegalArgumentException {
        for (var entry : cards.entrySet()) {
            if (entry.getKey().getName().equals(name))
                return entry.getValue();
        }
        throw new IllegalArgumentException(String.format("Error, %s is not a card in deck.", name));
    }

    /**
     * Definition: Checks if a deck is empty
     * Pre-Condition: cards is initialized
     * Post-Condition: A boolean is returned
     * @return The boolean if the deck is empty
     */
    @Override
    public boolean isEmpty() {
        return cards.isEmpty();
    }

    /**
     * Description: Returns the total amount of cards in the deck
     * Pre-Condition: None
     * Post-Condition: Total amount of cards is returned
     * @return The total amount of cards (Integer, not int)
     */
    private Integer totalCards() {
        Integer totalCards = 0;
        for (Integer i : cards.values())
            totalCards += i;
        return totalCards;
    }

    /**
     * Description: Checks if a deck is valid
     * Pre-Condition: None
     * Post-Condition: Boolean if deck is valid is returned
     * @return The boolean if deck is valid
     */
    public boolean isValid() {
        // Checking that there is at least 10 cards
        if (totalCards() < 10)
            return false; // Less than 10

        // Checking that there is no card in the deck more than 4 times
        for (Integer i : cards.values()) {
            if (i > 4)
                return false; // More than 4
        }

        // Deck is valid, no card is allowed in deck that is not in inventory, this is checked in DeckCMD
        return true;
    }

    // Getter
    /**
     * Description: Functions entirely as getCards(), this method is a getter
     * Pre-Condition: This object is declared and initialized
     * Post-Condition: The map of cards to quantities is returned
     * @return The map of cards to quantities (HashMap)
     */
    @Override
    public Map<Card, Integer> toItems() {
        return cards;
    }

    public String getName() {
        return name;
    }

    // Setter
    public void setId(Integer i) {
        id = i;
    }

}
