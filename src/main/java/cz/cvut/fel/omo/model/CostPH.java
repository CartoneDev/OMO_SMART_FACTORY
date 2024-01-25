package cz.cvut.fel.omo.model;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
@Getter
public class CostPH {
    List<Material> materials = new ArrayList<>();

    public void addCost(Material m) {
        materials.add(m);
    }
}


//stroj cost total = E{ hours work * cost }