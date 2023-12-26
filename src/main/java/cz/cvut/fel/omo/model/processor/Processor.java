package cz.cvut.fel.omo.model.processor;

import cz.cvut.fel.omo.model.CostPH;

public abstract class Processor {
    private Integer id;
    private String name;
    private String type;
    private ProcessorState state;

    private CostPH cost;
    public void tick() {
        state.tick();
    }
}
