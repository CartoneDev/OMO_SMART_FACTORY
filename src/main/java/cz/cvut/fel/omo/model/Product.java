package cz.cvut.fel.omo.model;

import cz.cvut.fel.omo.core.event.Copyable;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Class representing a product
 */
@Getter
@Setter
@Builder(toBuilder = true)
public class Product implements Copyable<Product> {
    private String name;
    private String type;
    private String description;
    private Integer amount;
    private CostPH costPH;

    /**
     * Creates a copy of the product
     * @return
     */
    public Product copy() {
        return this.toBuilder().build();
    }

    /**
     * Returns the product in human-readable form
     * @return the product in human-readable form
     */
    public String toString(){
        return name + " " + type + " \"" + description + "\" (" + amount + "x)";
    }
}
