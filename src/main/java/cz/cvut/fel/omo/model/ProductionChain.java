package cz.cvut.fel.omo.model;

import java.util.LinkedList;
import cz.cvut.fel.omo.model.processor.Processor;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ProductionChain {
    private Integer priority;
    private Integer id;
    private String name;
    private LinkedList<Processor> processors;
    private Product product;
    public void tick() {
        // TODO: implement
    }

    public void addProcessor(Processor processor) {
    }
}
