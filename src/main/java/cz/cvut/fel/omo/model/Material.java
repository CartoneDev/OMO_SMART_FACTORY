package cz.cvut.fel.omo.model;

import cz.cvut.fel.omo.core.event.Copyable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Class representing a material
 */
@AllArgsConstructor
@Builder(toBuilder = true)
@Setter
@Getter
public class Material implements Copyable {
    private Integer id;
    private String name;
    private String type;
    private Integer amount;
    private BigDecimal value;

    /**
     * Changes value of the material to be the value of 1 unit of material
     */
    // Converts value of an amount of material to value of 1 unit of material
    public void unitize() {

        value = value.divide(BigDecimal.valueOf(amount), 2, RoundingMode.HALF_UP);
        amount = 1;
    }

    /**
     * Creates a copy of the material
     * @return copy of the material
     */
    @Override
    public Material copy() {
        return this.toBuilder().build();
    }
}