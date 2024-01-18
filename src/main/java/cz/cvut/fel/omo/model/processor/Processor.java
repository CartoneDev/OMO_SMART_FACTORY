package cz.cvut.fel.omo.model.processor;

import cz.cvut.fel.omo.core.event.*;
import cz.cvut.fel.omo.core.event.WaybackMachine;
import cz.cvut.fel.omo.core.visitor.Visitable;
import cz.cvut.fel.omo.model.CostPH;
import cz.cvut.fel.omo.model.ProductionChain;
import cz.cvut.fel.omo.model.processor.states.ProcessorState;
import cz.cvut.fel.omo.utility.Config;
import cz.cvut.fel.omo.utility.ProcessorBuilder;
import java.util.Random;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class Processor implements Timed, Copyable, Visitable {
    private Integer id;
    private String name;
    private String type;
    private ProcessorState state;
    private Integer amount;
    private Double damage;

    private ProductionChain productionChain = null;

    private CostPH cost;

    private Processor initialState = null;

    private WaybackMachine waybackMachine;

    public Event tick() {
        if (isBroken()) {
            Integer priority = productionChain!=null?productionChain.getPriority():0;
            Event e = new PriorityEvent(EventType.PROCESSOR_BROKEN, this, priority);
            addEvent(e);
            return e;
        }
        Config.getDecayModel().decay(this);
	    return Event.getEmptyEvent();
    }

    public ProcessorBuilder toBuilder() {
        return new ProcessorBuilder(type)
                        .name(name)
                        .initState()
                        .amount(amount)
                        .damage(damage)
                        .cost(cost);
    }

    public void dealDamage(Double damage) {
        this.damage += damage;
    }

    public Processor copy(){
        return this.toBuilder().noRef().build();
    }

    public void addEvent(Event event) {
        waybackMachine.eventHappened(event);
        state=state.consume(this, event);
    }

    public boolean isAssigned() {
        return productionChain != null;
    }

    public void printStatus() {
        String formatedDamage = String.format("%.2f", damage * 100);
        System.out.println("Processor " + name + " is " + state + " and has " + formatedDamage + "% wear off");
    }

    public boolean isBroken() {
        return  ((this.getDamage() > 0.8) && (new Random().nextDouble() > 0.85)) ||
                ((this.getDamage() > 0.6) && (new Random().nextDouble() > 0.99));
    }

    @Override
    public String toString() {
        return String.format("%s %s %s %.2f%% damaged %s",
                state, name, type, damage * 100,
                (isAssigned()) ? String.format(" assigned to %s[%d]",
                        productionChain.getProduct().getName(), productionChain.getId()) : "");
    }
}
