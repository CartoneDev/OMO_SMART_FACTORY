package cz.cvut.fel.omo.model;

import java.util.ArrayList;
import java.util.LinkedList;

import cz.cvut.fel.omo.core.SmartFactory;
import cz.cvut.fel.omo.core.event.*;
import cz.cvut.fel.omo.core.visitor.Visitable;
import cz.cvut.fel.omo.core.visitor.Visitor;
import cz.cvut.fel.omo.model.processor.Processor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.XSlf4j;

@Getter
@Setter
@XSlf4j (topic = "PROD_CHAIN")
public class ProductionChain implements Visitable, EventSource, Copyable, Timed {
    private Integer priority;
    private Integer id;
    private String name;
    private LinkedList<Processor> processors = new LinkedList<>();
    private Product product;
    private final ProductionChain prototype ;
    private WaybackMachine<ProductionChain> waybackMachine;
    private boolean halted = false;
    private boolean linkReorganized = false;

    public ProductionChain(ProductionChain prototype, Integer id) {
        this.prototype = prototype;
        this.name = prototype.getName();
        this.product = prototype.getProduct();
        this.waybackMachine = new WaybackMachine<>((ProductionChain) this.copy());
    }

    public ProductionChain() {
        prototype = null; //prototypes does not have prototypes
    }

    public void tick() {
        Event e = null;

        if (linkReorganized){
            arrangeLink();
        }

        if (halted){
            log.info("Production chain #{} " + name + " is halted", id);
            return;
        }

        for (Processor processor : processors) {
            if (getProductionHalted()){
                haltProcessor(processor);
            }else{
                if(processor.getState().toString().equals("Waiting")){
                    processor.addEvent(new Event(EventType.PROCESSOR_STARTED, this, this));
                }
                e = processor.tick();
                if (e.getType() == EventType.PROCESSOR_BROKEN){
                    log.info("Processor " + processor.getName() + " is broken");
                    break;
                } else if (e.getType() == EventType.PROCESSOR_HALTED){
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
                SmartFactory.getInstance().productProduced(new Event(EventType.PRODUCT_PRODUCED, product.getName(), this));
            }
        }
    }

    private void arrangeLink() {
        LinkedList<Processor> newOrder = new LinkedList<>();

        for (Processor procType : prototype.getProcessors()) {
            Integer count = procType.getAmount();
            LinkedList<Processor> toAdd = processors.stream().filter(p -> p.getType().equals(procType.getType()))
                    .filter(p -> p.getName().equals(procType.getName())).limit(count)
                    .collect(java.util.stream.Collectors.toCollection(LinkedList::new));
            if (toAdd.size() < count){
                log.error("Production chain #{} " + name + " is missing processors of type " + procType.getName(), id);
                log.info("Production chain #{} " + name + " is halted", id);
                halted = true;
            }
            processors.removeAll(toAdd);
            newOrder.addAll(toAdd);
        }
        if (!processors.isEmpty()){
            log.error("Production chain #{} " + name + " has processors that are not in prototype", id);
            if (halted){
                log.info("Production chain #{} " + name + " is halted", id);
            } else {
                log.info("Since production chain #{} where assembled without those, they to be returned to pool", id);
                processors.forEach(p -> p.addEvent(new Event(EventType.PROCESSOR_UNASSIGNED, p, this)));
                SmartFactory.getInstance().getProcessorPool().returnProcessors(new ArrayList<>(processors));
            }
        }

        processors = newOrder;
    }

    private void haltProcessor(Processor processor) {
        if (processor.getState().toString().equals("Processing")){
            processor.addEvent(new Event(EventType.PROCESSOR_HALTED, this, this));
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

    public void printStatus(Integer time) {
        log.info("  Production chain #{} " + name + " status:", id);
        processors.forEach(p -> p.printStatus(time));
    }

    public void registerProcessors() {
        for (Processor processor : processors) registerProcessor(processor);
    }
    public void registerProcessor(Processor p){
        Event e = new Event(EventType.PROCESSOR_ASSIGNED, p, this );
        p.addEvent(e);
        this.addEvent(e);
    }
    public void unregisterProcessor(Processor p){
        Event e = new Event(EventType.PROCESSOR_UNASSIGNED, p, this );
        p.addEvent(e);
        this.addEvent(e);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String getReportDescriptor() {
        return "Production chain " + name + " (#" + id + ")" + " producing " + product.getName();
    }

    @Override
    public Copyable copy() {
        ProductionChain copy = new ProductionChain();
        copy.setName(name);
        copy.setProduct(product);
        copy.setId(id);
        copy.setPriority(priority);
        copy.setHalted(halted);
        return copy;
    }

    @Override
    public void addEvent(Event event) {
        if (waybackMachine!=null) waybackMachine.eventHappened(event);
        if (event.getType() == EventType.PROCESSOR_ASSIGNED) {
            processors.add((Processor) event.getPayload());
            linkReorganized =true;
        }
        if (event.getType() == EventType.PROCESSOR_UNASSIGNED) {
            processors.remove((Processor) event.getPayload());
            linkReorganized =true;
        }
    }
}
