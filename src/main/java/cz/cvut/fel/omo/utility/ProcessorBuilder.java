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

public class ProcessorBuilder {
    private final Processor result;
    private boolean noRef;
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
    public ProcessorBuilder name(String name){
        result.setName(name);
        return this;
    }

    public ProcessorBuilder cost(JsonNode node, JsonParser jp, DeserializationContext ctxt) {
//        result.setCost(new CostPH(node.get("cost").asInt()));
        return this;
    }

    public Processor build() {
        if (!noRef) { // The initial state doesn't need an initial state
            result.setWaybackMachine(new WaybackMachine(result.copy()));
        }
        return result;
    }

    public ProcessorBuilder damage(Double damage) {
        this.result.setDamage(damage);
        return this;
    }
    public ProcessorBuilder amount(Integer amount) {
        this.result.setAmount(amount);
        return this;
    }

    public ProcessorBuilder initState() {
        this.result.setState(new Initial());
        return this;
    }

    public ProcessorBuilder cost(CostPH costPH) {
        this.result.setCost(costPH);
        return this;
    }

    public ProcessorBuilder noRef() {
        this.noRef = true;
        return this;
    }

    public ProcessorBuilder id(Integer id) {
        this.result.setId(id);
        return this;
    }
}
