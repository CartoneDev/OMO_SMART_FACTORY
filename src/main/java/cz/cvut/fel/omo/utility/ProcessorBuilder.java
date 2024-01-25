package cz.cvut.fel.omo.utility;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import cz.cvut.fel.omo.core.event.WaybackMachine;
import cz.cvut.fel.omo.model.CostPH;
import cz.cvut.fel.omo.model.processor.*;
import cz.cvut.fel.omo.model.processor.states.Initial;
import lombok.SneakyThrows;

import java.io.IOException;

/**
 * Builder for processors, produces three descendant classes of Processor
 */
public class ProcessorBuilder {
    private final Processor result;
    private boolean noRef;

    /**
     * Constructor may throw an exception if the type is unknown
     * @param type type of processor
     */
    @SneakyThrows
    public ProcessorBuilder (String type){
        result = switch (type) {
            case "machine" -> new Machine();
            case "worker" -> new HumanResource();
            case "robot" -> new RoboticResource();
            default -> throw new IOException("Unknown processor type: " + type);
        };
        result.setType(type);
    }

    /**
     * Sets the name of the processor
     * @param name name of the processor
     * @return builder
     */
    public ProcessorBuilder name(String name){
        result.setName(name);
        return this;
    }


    /**
     * Builds the processor
     * Sets the wayback machine of the processor
     * @return builder
     */
    public Processor build() {
        if (!noRef) { // The initial state doesn't need an initial state
            result.setWaybackMachine(new WaybackMachine(result.copy()));
        }
        return result;
    }

    /**
     * Sets the damage of the processor
     * @return builder
     */
    public ProcessorBuilder damage(Double damage) {
        this.result.setDamage(damage);
        return this;
    }

    /**
     * Sets the amount of the processor
     * @return builder
     */
    public ProcessorBuilder amount(Integer amount) {
        this.result.setAmount(amount);
        return this;
    }

    /**
     * Sets the state of the processor to initial
     * @return builder
     */
    public ProcessorBuilder initState() {
        this.result.setState(new Initial());
        return this;
    }

    /**
     * Sets the cost of the processor
     * @return builder
     */
    public ProcessorBuilder cost(CostPH costPH) {
        this.result.setCost(costPH);
        return this;
    }

    /**
     * Sets not to create a reference to the processor
     * @return builder
     */
    public ProcessorBuilder noRef() {
        this.noRef = true;
        return this;
    }

    /**
     * Sets the id of the processor
     * @return builder
     */
    public ProcessorBuilder id(Integer id) {
        this.result.setId(id);
        return this;
    }
}
