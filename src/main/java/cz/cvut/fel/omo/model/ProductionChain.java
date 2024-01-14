package cz.cvut.fel.omo.model;

import java.util.LinkedList;

import cz.cvut.fel.omo.core.SmartFactory;
import cz.cvut.fel.omo.core.event.Event;
import cz.cvut.fel.omo.core.event.EventType;
import cz.cvut.fel.omo.model.processor.Processor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.XSlf4j;

@Getter
@Setter
@XSlf4j (topic = "ProductionChain")
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
            Event e = null;
            for (Processor processor : processors) {

                e = processor.tick();
                if (e.getType() == EventType.PROCESSOR_BROKEN){
                    log.info("Processor " + processor.getName() + " is broken");

                    break;
                }
            }
            if (e != null) {
              if (e.getType() == EventType.PROCESSOR_BROKEN){
                  log.info("Production chain {}" + name + " is broken", id);
                  log.info("Incident will be reported");
                  SmartFactory.getInstance().incidentHappend(e);
              }else if (e.getType() == EventType.PROCESSOR_HALTED){
                  // Broken processor be halted till repaired
              }
            }
        }


    public void addProcessor(Processor processor) {
        processors.add(processor);
    }
}
