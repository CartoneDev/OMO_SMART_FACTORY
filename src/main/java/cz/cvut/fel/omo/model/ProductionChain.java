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
    private LinkedList<Processor> processors = new LinkedList<>();
    private Product product;
    private final ProductionChain prototype ;

    public ProductionChain(ProductionChain prototype, Integer id) {
        this.prototype = prototype;
        this.name = prototype.getName();
        this.product = prototype.getProduct();
    }

    public ProductionChain() {
        prototype = null; //prototypes does not have prototypes
    }

    public void tick() {
        // TODO: implement
    }

    public void addProcessor(Processor processor) {
        processors.add(processor);
    }
}
