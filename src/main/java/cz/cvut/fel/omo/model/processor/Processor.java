package cz.cvut.fel.omo.model.processor;

import cz.cvut.fel.omo.model.CostPH;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class Processor {
    private Integer id;
    private String name;
    private String type;
    private ProcessorState state;
    private Integer amount;
    private Double damage;

    private CostPH cost;
    public void tick() {
        state.tick();
    }
}
