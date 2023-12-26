package cz.cvut.fel.omo.model;

import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;

@AllArgsConstructor
@Builder
public class Material {
    private Integer id;
    private String name;
    private Integer amount;
    private BigDecimal price;
}