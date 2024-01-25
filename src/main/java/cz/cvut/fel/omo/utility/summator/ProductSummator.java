package cz.cvut.fel.omo.utility.summator;

import cz.cvut.fel.omo.model.CostPH;
import cz.cvut.fel.omo.model.Material;
import cz.cvut.fel.omo.utility.Config;

import java.math.BigDecimal;
import java.util.HashMap;

/**
 * Summator for products
 */
public class ProductSummator {


//    private final HashMap<String, Material> materials = new HashMap<>();
//
//    public void addMaterial(Material material, Integer amount) {
//        if (materials.containsKey(material.getName())) {
//            Material m = materials.get(material.getName());
//            m.setAmount(m.getAmount() + amount);
//        } else {
//            Material m = material.copy();
//            m.setAmount(amount);
//            materials.put(material.getName(), m);
//        }
//    }
//
//
//    public void add(CostPH cost, double v) {
//        for (Material m : cost.getMaterials()) {
//            addMaterial(m, (int) Math.ceil(m.getAmount() * v));
//        }
//    }
//
//    public String toString() {
//        StringBuilder sb = new StringBuilder();
//        BigDecimal total = BigDecimal.ZERO;
//        for (Material m : materials.values()) {
//            BigDecimal current = m.getValue().multiply(BigDecimal.valueOf(m.getAmount()));
//            total = total.add(current);
//            sb.append(m.getAmount()).append("x ").append(m.getName()).append("(").append(current).append(",-").append(Config.getCurrency()).append(")").append("; ");
//        }
//        sb.append("With total value of ").append(total).append(",-").append(Config.getCurrency());
//        return sb.toString();
//    }
//
//    public void add(cz.cvut.fel.omo.utility.summator.MaterialSummator processorSummator) {
//        for (Material m : processorSummator.materials.values()) {
//            addMaterial(m, m.getAmount());
//        }
//    }

}
