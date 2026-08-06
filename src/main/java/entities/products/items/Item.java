package entities.products.items;

import entities.products.Product;

/**
 * Item
 * Description: Contains methods and general things that an actual item will have related to its stock
 * Name: Nico Rotella
 * Date Created: August 5th, 2026
 * Last Edited: August 5th, 2026
 */

// Interface
public interface Item extends Product {

    int getStock();

    // Each item handles knowing its own stock, the inventory handles knowing the items itself
    void removeStock(Integer quantity) throws IllegalArgumentException;

}
