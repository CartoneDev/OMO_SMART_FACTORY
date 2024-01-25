package cz.cvut.fel.omo.model;

import java.util.*;
import java.util.stream.Collectors;

import cz.cvut.fel.omo.core.ProcessorPool;
import cz.cvut.fel.omo.core.SmartFactory;
import cz.cvut.fel.omo.core.event.*;
import cz.cvut.fel.omo.core.visitor.Visitable;
import cz.cvut.fel.omo.core.visitor.Visitor;
import cz.cvut.fel.omo.model.processor.Processor;
import cz.cvut.fel.omo.utility.Config;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.XSlf4j;

/**
 * Represents a production chain.
 * Both actual and a blueprint(prototype).
 */
@Getter
@Setter
@XSlf4j (topic = "PROD_CHAIN")
public class ProductionChain implements Visitable, EventSource, Copyable<ProductionChain>, Timed {
    private Integer priority;
    private Integer id;
    private String name;
    private LinkedList<Processor> processors = new LinkedList<>();
    private Product product;
    private ProductionChain prototype ;
    private WaybackMachine<ProductionChain> waybackMachine;
    private boolean halted = false;
    private boolean linkReorganized = false;

    /**
     * Constructor for actual production chain
     * @param prototype
     * @param id
     */
    public ProductionChain(ProductionChain prototype, Integer id) {
        this.prototype = prototype;
        this.name = prototype.getName();
        this.product = prototype.getProduct();
        this.waybackMachine = new WaybackMachine<>((ProductionChain) this.copy());
    }

    /**
     * Constructor for prototype
     */
    public ProductionChain() {
        prototype = null; //prototypes does not have prototypes
    }

    /**
     * Entry point for delta time pulse
     */
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
                Event event = new Event(EventType.PRODUCT_PRODUCED, prototype.product.copy(), this);
                addEvent(event);
            }
        }
    }

    /**
     * Reorganizes the link to match the prototype
     */
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
                newOrder.addAll(processors);
            } else {
                log.info("Since production chain #{} where assembled without those, they to be returned to pool", id);
                processors.forEach(p -> p.addEvent(new Event(EventType.PROCESSOR_UNASSIGNED, p, this)));
                SmartFactory.getInstance().getProcessorPool().returnProcessors(new ArrayList<>(processors));
            }
        }

        processors = newOrder;
        linkReorganized = false;
    }

    /**
     * Halts the processor
     * @param processor
     */
    private void haltProcessor(Processor processor) {
        if (processor.getState().toString().equals("Processing")){
            processor.addEvent(new Event(EventType.PROCESSOR_HALTED, this, this));
        }
    }

    /**
     * Returns true if production is halted
     * @return true if production is halted, false otherwise
     */
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

    /**
     * Adds processor to the production chain
     * @param processor
     */
    public void addProcessor(Processor processor) {
        processors.add(processor);
        processor.setProductionChain(this);
    }

    /**
     * Adds processors to the production chain
     * @param processors
     */
    public void addProcessors(ArrayList<Processor> processors) {
        processors.forEach(this::addProcessor);
    }

    /**
     * Prints the status of the production chain in the given time
     */
    public void printStatus(Integer time) {
        if (linkReorganized){
            arrangeLink();
        }
        log.info("  Production chain #{} " + name + " status:", id);
        processors.forEach(p -> p.printStatus(time));
    }

    /**
     * Registers added processors to the production chain
     */
    public void registerProcessors() {
        Iterator<Processor> iterator = processors.iterator();
        this.processors = new LinkedList<>();
        while (iterator.hasNext()) {
            Processor processor = iterator.next();
            registerProcessor(processor);
        }
    }

    /**
     * Unregisters processors from the production chain
     * @param p processor to be unregistered
     */
    private void registerProcessor(Processor p){
        Event e = new Event(EventType.PROCESSOR_ASSIGNED, p, this );
        p.addEvent(e);
        this.addEvent(e);
    }

    /**
     * Unregisters processor from the production chain
     * @param p processor to be unregistered
     */
    private void unregisterProcessor(Processor p){
        Event e = new Event(EventType.PROCESSOR_UNASSIGNED, p, this );
        p.addEvent(e);
        this.addEvent(e);
    }

    /**
     * Accepts the visitor
     * @param visitor
     */
    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    /**
     * Returns the report descriptor
     * @return report descriptor
     */
    @Override
    public String getReportDescriptor() {
        return "Production chain " + " (#" + id + ")" + " producing " + product.getName();
    }

    /**
     * Returns the copy of the production chain
     * @return copy of the production chain
     */
    @Override
    public ProductionChain copy() {
        ProductionChain copy = new ProductionChain();
        copy.setName(name);
        copy.setProduct(product);
        copy.setId(id);
        copy.setPriority(priority);
        copy.setPrototype(prototype);
        copy.setHalted(halted);
        return copy;
    }

    /**
     * Adds event to the production chain
     * @param event
     */
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
        if (event.getType() == EventType.PRODUCT_CHANGED) {
            Product product = (Product) event.getPayload();

            if (product == null || prototype == null || prototype.getProcessors().isEmpty()) {
                log.error("Invalid product or prototype configuration for production chain #{} " + name, id);
                return;
            }

            if (event.getSolver() == null) {
                rebuildChainTo(product, event);
            } else {
                setProduct(product);
                setName(product.getName());
                prototype = Config.getBlueprintFor(product.getName());
            }
        }
    }

    /**
     * Returns the production chain at the given time
     * @param timestamp
     * @return production chain at the given time
     */
    @Override
    public Timed onTime(Integer timestamp) {
        return waybackMachine.goBackTo(timestamp);
    }

    /**
     * Rebuilds the production chain to produce the given product
     * @param product
     * @param event
     */
    private void rebuildChainTo(Product product, Event event){
        log.info("Rebuilding production chain #{} " + name + " to produce " + product.getName(), id);
        Set<Map.Entry<String, Long>> current = processors.stream().collect(Collectors.groupingBy(Processor::getName, Collectors.counting())).entrySet();
        Set<Map.Entry<String, Long>> required = Config.getBlueprintFor(product.getName()).getProcessors().stream().map(p -> new AbstractMap.SimpleEntry<>(p.getName(), (long) p.getAmount())).collect(Collectors.toSet());
        Map<String, Long> currentMap = current.stream()
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, HashMap::new));
        Map<String, Long> requiredMap = required.stream()
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, HashMap::new));
        Map<String, Long> toRemove = new HashMap<>();

        for (Map.Entry<String, Long> entry : currentMap.entrySet()) {
            Integer currentCount = entry.getValue().intValue();
            Integer requiredCount = requiredMap.getOrDefault(entry.getKey(), 0L).intValue();
            long diff = requiredCount - currentCount;
            requiredMap.put(entry.getKey(), Math.max(0, diff));
            if (diff < 0) {
                toRemove.put(entry.getKey(), - diff);
            }
        }
        ArrayList<Processor> toAdd = reserveProcessor(requiredMap);
        if (toAdd == null) {
            log.error("Not enough processors in pool for " + product.getName());
            log.info("Production chain #{} " + name + " is still producing {}", id, this.product.getName());
            return;
        }
        for (Map.Entry<String, Long> entry : toRemove.entrySet()) {
            String name = entry.getKey();
            int count = entry.getValue().intValue();
            ArrayList<Processor> toRemoveProcessors = processors.stream().filter(p -> p.getName().equals(name)).limit(count).collect(Collectors.toCollection(ArrayList::new));
            toRemoveProcessors.forEach(this::unregisterProcessor);
            SmartFactory.getInstance().getProcessorPool().returnProcessors(toRemoveProcessors);
        }
        toAdd.forEach(processor -> registerProcessor(processor));
        linkReorganized = true;
        setProduct(product);
        prototype = Config.getBlueprintFor(product.getName());
        setName(product.getName());
        log.info("Production chain #{} " + name + " is now producing {}", id, this.product.getName());
        event.setSolver(this);
    }

    /**
     * Reserves processors for the production chain
     * @param requiredMap
     * @return reserved processors
     */
    private ArrayList<Processor> reserveProcessor(Map<String, Long> requiredMap) {
        ProcessorPool processorPool = SmartFactory.getInstance().getProcessorPool();
        ArrayList<Processor> toAdd = new ArrayList<>();
        for (Map.Entry<String, Long> entry : requiredMap.entrySet()) {
            String name = entry.getKey();
            Integer count = entry.getValue().intValue();
            ArrayList<Processor> processors = processorPool.getProcessors(name, count);
            if (processors == null) {
                log.error("Not enough processors in pool for " + name);
                processorPool.returnProcessors(toAdd);
                return null;
            }

            toAdd.addAll(processors);
        }
        return toAdd;
    }

    /**
     * Rebuilds the production chain to produce the given product
     * @param product product to be produced
     */
    public void rebuildTo(Product product){
        addEvent(new Event(EventType.PRODUCT_CHANGED, product, this));
    }

    /**
     * Returns the state of the production chain at the given time
     * @param timestamp
     * @return state of the production chain at the given time
     */

    public ProductionChain getStateAt(Integer timestamp) {
        ProductionChain backInMaDays = waybackMachine.goBackTo(timestamp);
        backInMaDays.arrangeLink();
        return backInMaDays;
    }

    /**
     * Sets the priority of the production chain
     * @param priority
     */
    public void setPriority(Integer priority) {
        this.priority = priority;
        if (waybackMachine!=null) {
            ((ProductionChain)waybackMachine.getInitialState()).setPriority(priority);
        }
    }

    /**
     * Sets the id of the production chain
     * @param id
     */
    public void setId(Integer id) {
        this.id = id;
        if (waybackMachine!=null) {
            ((ProductionChain)waybackMachine.getInitialState()).setId(id);
        }
    }
}
