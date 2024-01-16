package cz.cvut.fel.omo.model;

import java.util.ArrayList;
import java.util.LinkedList;

import cz.cvut.fel.omo.core.SmartFactory;
import cz.cvut.fel.omo.core.event.Event;
import cz.cvut.fel.omo.core.event.EventType;
import cz.cvut.fel.omo.core.event.PriorityEvent;
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

    private boolean halted = false;
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
                if (getProductionHalted()){
                    haltProcessor(processor);
                }else{
                    if(processor.getState().toString().equals("Waiting")){
                        processor.addEvent(new Event(EventType.PROCESSOR_STARTED, this));
                    }
                    e = processor.tick();
                    if (e.getType() == EventType.PROCESSOR_BROKEN){
                        log.info("Processor " + processor.getName() + " is broken");
                        break;
                    } else if (e.getType() == EventType.PROCESSOR_HALTED){
                        halted=true;
                        break;
                    }
            }
            }
            if (e != null) {
                if (e.getType() == EventType.PROCESSOR_BROKEN){
                    log.info("Production chain #{} " + name + " is broken", id);
                    log.info("Incident on ({}h) will be reported", e.getTimestamp());
                    SmartFactory.getInstance().incidentHappened((PriorityEvent) e);
                }else if (e.getType() == EventType.PROCESSOR_HALTED){
                    log.info("Production chain #{}" + name + " is halted, awaiting repair crew", id);
                } else if (e.getType() == EventType.EMPTY){
                    SmartFactory.getInstance().productProduced(new Event(EventType.PRODUCT_PRODUCED, this));
                }
            }
        }

    private void haltProcessor(Processor processor) {
        if (processor.getState().toString().equals("Processing")){
            processor.addEvent(new Event(EventType.PROCESSOR_HALTED, this));
        }
    }

    private boolean getProductionHalted() {
        for (Processor processor : processors) {
            if (processor.getState().toString().equals("Broken")){
                return true;
            }
            if (processor.getState().toString().equals("BeingRepaired")){
                return true;
            }
        }
        return false;
    }


    public void addProcessor(Processor processor) {
        processors.add(processor);
        processor.setProductionChain(this);
    }

    public void addProcessors(ArrayList<Processor> processors) {
        processors.forEach(this::addProcessor);
    }

    public void printStatus() {
        log.info("  Production chain #{} " + name + " status:", id);
        processors.forEach(Processor::printStatus);
    }

    public void registerProcessors() {
        for (Processor processor : processors) registerProcessor(processor);
    }
    public void registerProcessor(Processor p){
        Event e = new Event(EventType.PROCESSOR_ASSIGNED, this);
        p.addEvent(e);
    }
    public void unregisterProcessor(Processor p){
        Event e = new Event(EventType.PROCESSOR_UNASSIGNED, this);
        p.addEvent(e);
    }
}
