package cz.cvut.fel.omo.model;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * Class representing a bundle of materials
 */
@Getter
public class CostPH {
    List<Material> materials = new ArrayList<>();

    /**
     * Adds a material to the bundle
     * @param m material to be added
     */
    public void addCost(Material m) {
        materials.add(m);
    }
}


//stroj cost total = E{ hours work * cost }