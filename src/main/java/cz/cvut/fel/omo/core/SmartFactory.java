package cz.cvut.fel.omo.core;

import cz.cvut.fel.omo.core.event.Event;
import cz.cvut.fel.omo.core.event.PriorityEvent;
import cz.cvut.fel.omo.core.visitor.Director;
import cz.cvut.fel.omo.core.visitor.InspectorGadget;
import cz.cvut.fel.omo.core.visitor.Visitable;
import cz.cvut.fel.omo.core.visitor.Visitor;
import cz.cvut.fel.omo.model.ProductionChain;
import lombok.Getter;
import lombok.extern.slf4j.XSlf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;


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
    private SmartFactory() {
        this.name = "Empty factory";
        if (instance != null) {
            log.error("Trying to instantiate second singleton instance!");
            return;
        }
        instance = this;
    }


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

    public static SmartFactory getInstance() {
        if (instance == null) {
            instance = new SmartFactory();
            log.debug("Empty factory has been initialized!");
        }
        return instance;
    }
    public static SmartFactory setInstance(ProcessorPool processorPool, ArrayList<ProductionChain> links, String name) {
        if (instance == null) {
            instance = new SmartFactory(processorPool, links, name);
            log.info((name.isEmpty() ? name : name.substring(0, 1).toUpperCase() + name.substring(1) ) + " initialized!");
        }
        return instance;
    }

    public static void reset() {
        if (instance != null) {
            instance = null;
            log.info("Reseting SmartFactory setup");
        }
    }


    public void tick() {
        maintenance.tick();
        // each tick equals to 1 realtime hour
        links.forEach(ProductionChain::tick);
    }

    public void incidentHappened(PriorityEvent event){
        maintenance.incidentReported(event);
    }

    public void printStatus(Integer time) {
        for (ProductionChain link : links) {
            link.printStatus(time);
        }
    }

    public void productProduced(Event event) {

    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void inspect() {
        this.accept(new InspectorGadget());
    }

    public void direct() {
        this.accept(new Director());
    }

    public Set<Event> getMaintenanceEvents() {
        return maintenance.getEvents();
    }
}
