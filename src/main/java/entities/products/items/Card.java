package entities.products.items;


import entities.products.Product;

/**
 * Card
 * Description: Class for card objects. Each card contains its own attributes such as element, rarity, etc.
 * Name: Nico Rotella
 * Date Created: May 5th, 2026
 * Last Edited: August 19th, 2026
 */

// Class
public class Card implements Item {

    // Attributes
    private final String name;
    private String element, rarity;
    private int price, stock;
    private Integer id;

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

    /**
     * Description: Removes a quantity of stock from the card
     * Pre-Condition: Quantity should be less than stock
     * Post-Condition: Stock is removed or error is thrown
     * @param quantity The amount of stock to be removed
     * @throws IllegalArgumentException if quantity is greater stock
     */
    @Override
    public void removeStock(Integer quantity) throws IllegalArgumentException {
        if (quantity > stock)
            throw new IllegalArgumentException("Error, cannot remove more stock than there is.");
        else
            stock -= quantity;
    }

    // Getters
    @Override
    public String getName() {
        return name;
    }

    public String getElement() {
        return element;
    }

    public String getRarity() {
        return rarity;
    }

    @Override
    public int getPrice() {
        return price;
    }

    @Override
    public int getStock() {
        return stock;
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public String getType() {
        return "CARD";
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

    @Override
    public void setId(Integer i) {
        id = i;
    }

} // Class
