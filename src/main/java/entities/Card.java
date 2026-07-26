package entities;


/**
 * Card
 * Description: Class for card objects. Each card contains its own attributes such as element, rarity, etc.
 * Name: Nico Rotella
 * Date Created: May 5th, 2026
 * Last Edited: July 25th, 2026
 */

// Class
public class Card {

    // Attributes
    private final String name;
    private String element, rarity;
    private int price, stock;

    // Constructors
    /**
     * Create a card
     * @param n The name
     * @param e The element
     * @param r The rarity
     * @param p The price
     * @param s The stock
     */
    public Card(String n, String e, String r, int p, int s) {
        name = n;
        element = e;
        rarity = r;
        price = p;
        stock = s;
    }

    /**
     * Description: Gives the string value of the card, this is what is to be displayed in inventory
     * Pre-Condition: This card is initialized
     * Post-Condition: String is returned
     * @return The string value of the card
     */
    @Override
    public String toString() {
        return String.format("""
                CARD
                Name: %s
                Element: %s
                Rarity: %s
                Price: %d
                Stock: %d
                """, name, element, rarity, price, stock);
    }


    // Getters
    public String getName() {
        return name;
    }

    public String getElement() {
        return element;
    }

    public String getRarity() {
        return rarity;
    }

    public int getPrice() {
        return price;
    }

    public int getStock() {
        return stock;
    }

    // Setters
    public void setElement(String e) {
        element = e;
    }

    public void setRarity(String r) {
        rarity = r;
    }

    public void setPrice(int p) {
        price = p;
    }

    public void setStock(int s) {
        stock = s;
    }
} // Class
