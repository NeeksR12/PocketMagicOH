package entities.products;

/**
 * Product
 * Description: Contains methods and general things that a product should have in order to be in the inventory and sold
 * Name: Nico Rotella
 * Date Created: July 28th, 2026
 * Last Edited: July 29th, 2026
 */

// Interface
public interface Product {

    String getName();
    int getPrice();
    int getStock();
    void removeStock(Integer quantity) throws IllegalArgumentException;
    String toString();

}
