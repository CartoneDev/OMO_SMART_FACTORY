package cz.cvut.fel.omo.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@AllArgsConstructor
@Builder(toBuilder = true)
@Setter
@Getter
public class Material {
    private Integer id;
    private String name;
    private String type;
    private Integer amount;
    private BigDecimal value;

    // Converts value of an amount of material to value of 1 unit of material
    public void unitize() {
        value = value.divide(BigDecimal.valueOf(amount));
        amount = 1;
    }
}