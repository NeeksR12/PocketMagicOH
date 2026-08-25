package entities.products;

import entities.Persistable;

/**
 * Product
 * Description: Contains methods and general things that a product should have in order to be in the inventory and sold
 * Name: Nico Rotella
 * Date Created: July 28th, 2026
 * Last Edited: August 25th, 2026
 */

// Interface
public interface Product extends Persistable {

    int getPrice();
    String getType();
    String toString();

}
