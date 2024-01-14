package cz.cvut.fel.omo.model.processor;

import cz.cvut.fel.omo.core.event.Event;
import cz.cvut.fel.omo.model.CostPH;
import cz.cvut.fel.omo.core.Tickable;
import cz.cvut.fel.omo.model.processor.states.ProcessorState;
import cz.cvut.fel.omo.utility.ProcessorBuilder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class Processor implements Tickable {
    private Integer id;
    private String name;
    private String type;
    private ProcessorState state;
    private Integer amount;
    private Double damage;

    private CostPH cost;
    public Event tick() {
        return state.process(this);
    }

    public ProcessorBuilder toBuilder() {
        return new ProcessorBuilder(type)
                        .name(name)
                        .initState()
                        .amount(amount)
                        .damage(damage)
                        .cost(cost);
    }
}
