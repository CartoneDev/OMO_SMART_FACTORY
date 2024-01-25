package cz.cvut.fel.omo.core;

import cz.cvut.fel.omo.core.event.Event;
import cz.cvut.fel.omo.core.event.EventType;
import cz.cvut.fel.omo.core.event.PriorityEvent;
import cz.cvut.fel.omo.core.visitor.Director;
import cz.cvut.fel.omo.core.visitor.InspectorGadget;
import cz.cvut.fel.omo.core.visitor.Visitable;
import cz.cvut.fel.omo.core.visitor.Visitor;
import cz.cvut.fel.omo.model.Product;
import cz.cvut.fel.omo.model.ProductionChain;
import cz.cvut.fel.omo.core.visitor.EventCollectingVisitor;

import cz.cvut.fel.omo.utility.summator.ProductSummator;
import lombok.Getter;
import lombok.extern.slf4j.XSlf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;


/**
 *  SmartFactory is a singleton class representing the whole factory.
 */
@XSlf4j(topic = "FACTORY")
public class SmartFactory implements Visitable {
    private static SmartFactory instance;
    @Getter
    private final String name;
    @Getter
    ProcessorPool processorPool = new ProcessorPool(new HashMap<>());

    private Maintenance maintenance;
    @Getter
    ArrayList<ProductionChain> links = new ArrayList<>();
    /**
     * Private constructor for singleton
     */
    private SmartFactory() {
        this.name = "Empty factory";
        if (instance != null) {
            log.error("Trying to instantiate second singleton instance!");
            return;
        }
        instance = this;
    }

    /**
     * Private constructor for singleton
     * @param processorPool processor pool
     * @param links production chains
     * @param name  name of the factory
     */

    private SmartFactory(ProcessorPool processorPool, ArrayList<ProductionChain> links, String name) {
        this.name = name;
        if (instance != null) {
            log.error("Trying to instantiate second singleton instance!");
            return;
        }
        instance = this;
        this.maintenance = new Maintenance(processorPool);
        this.processorPool = processorPool;
        this.links = links;
    }

    /**
     * Returns the singleton instance of the factory
     * @return singleton instance of the factory
     */
    public static SmartFactory getInstance() {
        if (instance == null) {
            instance = new SmartFactory();
            log.debug("Empty factory has been initialized!");
        }
        return instance;
    }

    /**
     * Sets the singleton instance of the factory, used for factory initialization by builder
     * @param processorPool processor pool
     * @param links production chains
     * @param name name of the factory
     * @return singleton instance of the factory
     */
    public static SmartFactory setInstance(ProcessorPool processorPool, ArrayList<ProductionChain> links, String name) {
        if (instance == null) {
            instance = new SmartFactory(processorPool, links, name);
            log.info((name.isEmpty() ? name : name.substring(0, 1).toUpperCase() + name.substring(1) ) + " initialized!");
        }
        return instance;
    }

    /**
     * Resets the singleton instance of the factory
     */
    public static void reset() {
        if (instance != null) {
            instance = null;
            log.info("Reseting SmartFactory setup");
        }
    }

    /**
     * Updates state of the factory each tick
     */
    public void tick() {
        maintenance.tick();
        // each tick equals to 1 realtime hour
        links.forEach(ProductionChain::tick);
    }

    /**
     * Reports an incident to the maintenance
     * @param event incident event
     */
    public void incidentHappened(PriorityEvent event){
        maintenance.incidentReported(event);
    }

    /**
     * Prints the status of the factory at given time
     * @param time time
     */
    public void printStatus(Integer time) {
        for (ProductionChain link : links) {
            link.printStatus(time);
        }
    }

    /**
     * Accepts a visitor
     * @param visitor
     */
    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    /**
     * Inspects the factory
     */
    public void inspect() {
        this.accept(new InspectorGadget());
    }

    /**
     * Directs the factory
     */
    public void direct() {
        this.accept(new Director());
    }

    /**
     * Returns all maintenance events
     * @return maintenance events
     */
    public Set<Event> getMaintenanceEvents() {
        return maintenance.getEvents();
    }

    /**
     * Prints the total production of the factory at given time
     * @param time time to print the production at
     */
    public void printProductionTotal(int time) {
        EventCollectingVisitor visitor = new EventCollectingVisitor();
        this.accept(visitor);
        ProductSummator summator = new ProductSummator();
        visitor.getEvents().stream().filter(e -> e.getTimestamp().getTicks() <= time).filter(e -> e.getType() == EventType.PRODUCT_PRODUCED)
                .forEach(e -> summator.add((Product)e.getPayload()));
        log.info("Production total: ");
        Arrays.stream(summator.toString().split(";")).forEach(log::info);
    }
}
