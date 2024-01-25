package cz.cvut.fel.omo.model.processor;

import cz.cvut.fel.omo.core.Clock;
import cz.cvut.fel.omo.core.event.*;
import cz.cvut.fel.omo.core.event.WaybackMachine;
import cz.cvut.fel.omo.core.visitor.Visitable;
import cz.cvut.fel.omo.model.CostPH;
import cz.cvut.fel.omo.model.Product;
import cz.cvut.fel.omo.model.ProductionChain;
import cz.cvut.fel.omo.model.processor.states.ProcessorState;
import cz.cvut.fel.omo.utility.Config;
import cz.cvut.fel.omo.utility.ProcessorBuilder;

import java.util.Objects;
import java.util.Random;
import lombok.Getter;
import lombok.Setter;

/**
 * Class representing abstract processor, which is a part of a production chain
 */
@Getter
@Setter
public abstract class Processor implements Timed, Copyable<Processor>, Visitable, EventSource {
    private Integer id;
    private String name;
    private String type;
    private ProcessorState state;
    private Integer amount;
    private Double damage;

    private ProductionChain productionChain = null;

    private CostPH cost;

    private WaybackMachine<Processor> waybackMachine;

    /**
     * Mechanism of changing processor in time, produces events
     * @return Event of EventType.Empty or EventType.ProcessorBroken
     */
    public Event tick() {
        if (isBroken()) {
            Integer priority = productionChain!=null?productionChain.getPriority():0;
            Event e = new PriorityEvent(EventType.PROCESSOR_BROKEN, this, priority, this);
            addEvent(e);
            return e;
        }
        Config.getDecayModel().decay(this);
	    return Event.getEmptyEvent();
    }

    /**
     * Converts the processor to a builder
     * @return ProcessorBuilder
     */

    public ProcessorBuilder toBuilder() {
        return new ProcessorBuilder(type)
                        .name(name)
                        .initState()
                        .amount(amount)
                        .damage(damage)
                        .cost(cost).id(id);

    }

    /**
     * Deals damage to the processor
     * @param damage amount of damage to be dealt
     */
    public void dealDamage(Double damage) {
        this.damage += damage;
    }

    /**
     * Copies the processor
     * @return copy of the processor
     */
    public Processor copy(){
        return this.toBuilder().noRef().build();
    }

    /**
     * Adds an event to the processor, which state is altered by the event
     * @param event
     */
    public void addEvent(Event event) {
        if (waybackMachine!=null) waybackMachine.eventHappened(event);
        state=state.consume(this, event);
    }

    /**
     * Checks if the processor is assigned to a production chain
     * @return true if the processor is assigned to a production chain, false otherwise
     */
    public boolean isAssigned() {
        return productionChain != null;
    }

    /**
     * Prints the status of the processor at a given time
     * @param time time at which the status is to be printed
     */
    public void printStatus(Integer time) {

        String toPrint;
        if (Objects.equals(time, Clock.getTime().getTicks())){
            toPrint = getStatus();
        }else{
            toPrint = getStatusAt(time);
        }
        System.out.println(toPrint);
    }

    /**
     * Returns the status of the processor
     * @return status of the processor
     */
    protected String getStatus(){
        String formattedDamage = String.format("%.2f", damage * 100);
        return (id) + "Processor " + name + " is " + state + " and has " + formattedDamage + "% wear off";
    }

    /**
     * Returns the status of the processor at a given time
     * @param time
     * @return status of the processor at a given time
     */
    public String getStatusAt(Integer time){
        return ((Processor)waybackMachine.goBackTo(time)).getStatus().split(" and has ")[0];
    }

    /**
     * Checks if the processor is broken
     * @return true if the processor is broken, false otherwise
     */
    public boolean isBroken() {
        return  ((this.getDamage() > 0.8) && (new Random().nextDouble() > 0.85)) ||
                ((this.getDamage() > 0.6) && (new Random().nextDouble() > 0.99));
    }

    /**
     * Returns the processor in human-readable form
     * @return the processor in human-readable form
     */
    @Override
    public String toString() {
        return String.format("%s %s %s %.2f%% damaged %s",
                state, name, type, damage * 100,
                (isAssigned()) ? String.format(" assigned to %s[%d]",
                        productionChain.getProduct().getName(), productionChain.getId()) : "");
    }

    /**
     * Returns the processor in human-readable form
     * @return the processor in human-readable form
     */
    @Override
    public String getReportDescriptor() {
        return "Processor " + name + " " + type + " #" + id + (!name.equals("repairman")?"[" + state + "]":"");
    }

    /**
     * Returns the processor in human-readable form
     * @return the processor in human-readable form
     */
    public void setId(Integer id) {
        if (waybackMachine!=null) waybackMachine.getInitialState().setId(id);
        this.id = id;
    }

    /**
     * Returns the processor in time
     * @param timestamp
     * @return the processor in time
     */
    @Override
    public Timed onTime(Integer timestamp) {
        return waybackMachine.goBackTo(timestamp);
    }
}
