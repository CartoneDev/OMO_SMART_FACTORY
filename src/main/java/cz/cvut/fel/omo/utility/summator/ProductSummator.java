package cz.cvut.fel.omo.utility.summator;

import cz.cvut.fel.omo.model.CostPH;
import cz.cvut.fel.omo.model.Material;
import cz.cvut.fel.omo.model.Product;
import cz.cvut.fel.omo.model.ProductionChain;
import cz.cvut.fel.omo.utility.Config;

import java.math.BigDecimal;
import java.util.HashMap;

/**
 * Summator for products
 */
public class ProductSummator {
    private final HashMap<String, Product> productHashMap = new HashMap<>();

    public void add(Product payload) {
        if (productHashMap.containsKey(payload.getName())) {
            Product p = productHashMap.get(payload.getName());
            p.setAmount(p.getAmount() + payload.getAmount());
        } else {
            Product p = payload.copy();
            productHashMap.put(payload.getName(), p);
        }
    }
    public @Override String toString() {
        StringBuilder sb = new StringBuilder();
        Integer total = 0;
        for (Product p : productHashMap.values()) {
            total += p.getAmount();
            sb.append(p.getName()).append(" x").append(p.getAmount()).append(" [")
                    .append(p.getType()).append("] ").append("\"")
                    .append(p.getDescription()).append("\"").append(";");
        }
        sb.append("With total amount of ").append(total).append(" products");
        return sb.toString();
    }
}
