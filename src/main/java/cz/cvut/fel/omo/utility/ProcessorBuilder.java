package cz.cvut.fel.omo.utility;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import cz.cvut.fel.omo.model.CostPH;
import cz.cvut.fel.omo.model.processor.*;
import lombok.SneakyThrows;

import java.io.IOException;

public class ProcessorBuilder {
    Processor result;
    @SneakyThrows
    public ProcessorBuilder (String type){
        result = switch (type) {
            case "machine" -> new Machine();
            case "worker" -> new HumanResource();
            case "robot" -> new RoboticResource();
            default -> throw new IOException("Unknown processor type: " + type);
        };
    }
    public ProcessorBuilder name(String name){
        result.setName(name);
        return this;
    }

    public ProcessorBuilder cost(JsonNode node, JsonParser jp, DeserializationContext ctxt) {
//        result.setCost(new CostPH(node.get("cost").asInt()));
        return this;
    }

    public Processor build() { return result; }

    public ProcessorBuilder damage(Double damage) {
        this.result.setDamage(damage);
        return this;
    }
    public ProcessorBuilder amount(Integer amount) {
        this.result.setAmount(amount);
        return this;
    }

    public ProcessorBuilder initState() {
        this.result.setState(new ProcessorState());
        return this;
    }

    public ProcessorBuilder cost(CostPH costPH) {
        this.result.setCost(costPH);
        return this;
    }
}
