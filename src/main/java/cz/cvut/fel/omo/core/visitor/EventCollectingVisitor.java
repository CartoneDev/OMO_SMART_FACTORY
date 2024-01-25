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

@Getter
public class EventCollectingVisitor implements Visitor{
    private final Set<Event> events = new HashSet<Event>();
    @Override
    public void visit(SmartFactory smartFactory) {
        events.addAll(smartFactory.getProcessorPool().collectEvents());
        events.addAll(smartFactory.getMaintenanceEvents());
        smartFactory.getLinks().forEach(productionChain -> productionChain.accept(this));
    }

    @Override
    public void visit(ProductionChain productionChain) {
        events.addAll(productionChain.getWaybackMachine().getEvents());
        productionChain.getProcessors().forEach(processor -> processor.accept(this));

    }

    @Override
    public void visit(HumanResource processor) {
        events.addAll(processor.getWaybackMachine().getEvents());
    }

    @Override
    public void visit(Machine processor) {
        events.addAll(processor.getWaybackMachine().getEvents());
    }

    @Override
    public void visit(RoboticResource processor) {
        events.addAll(processor.getWaybackMachine().getEvents());
    }

}
