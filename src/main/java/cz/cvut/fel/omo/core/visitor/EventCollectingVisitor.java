package cz.cvut.fel.omo.core.visitor;

import cz.cvut.fel.omo.core.SmartFactory;
import cz.cvut.fel.omo.core.event.Event;
import cz.cvut.fel.omo.model.ProductionChain;
import cz.cvut.fel.omo.model.processor.HumanResource;
import cz.cvut.fel.omo.model.processor.Machine;
import cz.cvut.fel.omo.model.processor.RoboticResource;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

/**
 * Helper visitor class for collecting all events from the factory
 */
@Getter
public class EventCollectingVisitor implements Visitor{
    private final Set<Event> events = new HashSet<Event>();

    /**
     * Visits the factory
     * @param smartFactory
     */
    @Override
    public void visit(SmartFactory smartFactory) {
        events.addAll(smartFactory.getProcessorPool().collectEvents());
        events.addAll(smartFactory.getMaintenanceEvents());
        smartFactory.getLinks().forEach(productionChain -> productionChain.accept(this));
    }

    /**
     * Visits the production chain
     * @param productionChain production chain to visit
     */
    @Override
    public void visit(ProductionChain productionChain) {
        events.addAll(productionChain.getWaybackMachine().getEvents());
        productionChain.getProcessors().forEach(processor -> processor.accept(this));

    }

    /**
     * Visits the human resource and collects all events
     * @param processor human resource to visit
     */
    @Override
    public void visit(HumanResource processor) {
        events.addAll(processor.getWaybackMachine().getEvents());
    }

    /**
     * Visits the machine and collects all events
     * @param processor machine to visit
     */
    @Override
    public void visit(Machine processor) {
        events.addAll(processor.getWaybackMachine().getEvents());
    }

    /**
     * Visits the robotic resource and collects all events
     * @param processor robotic resource to visit
     */
    @Override
    public void visit(RoboticResource processor) {
        events.addAll(processor.getWaybackMachine().getEvents());
    }

}
