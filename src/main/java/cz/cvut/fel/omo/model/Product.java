package cz.cvut.fel.omo.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder(toBuilder = true)
public class Product {
    private String name;
    private String type;
    private String description;
    private Integer amount;
    private CostPH costPH;
}
