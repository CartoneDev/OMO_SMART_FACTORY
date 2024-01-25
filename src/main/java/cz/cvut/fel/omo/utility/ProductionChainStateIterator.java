package cz.cvut.fel.omo.utility;

import cz.cvut.fel.omo.core.event.Event;
import cz.cvut.fel.omo.core.event.EventType;
import cz.cvut.fel.omo.core.event.WaybackMachine;
import cz.cvut.fel.omo.model.ProductionChain;

import java.util.ArrayList;
import java.util.List;

/**
 * Iterator for production chain states, each state doesn't contain events from their predecessors
 */
public class ProductionChainStateIterator {

    private ArrayList<Event> events;
    private ProductionChain productionChain;
    private Integer timestamp;
    private Integer previousTimestamp = 0;
    private Integer dueTimestamp;
    public ProductionChainStateIterator(ProductionChain productionChain, Integer dueTimestamp) {
        this.productionChain = productionChain;
        this.timestamp = 0;
        this.dueTimestamp = dueTimestamp;
        events = this.productionChain.getWaybackMachine()
                .getEvents()
                .stream()
                .filter(e -> e.getTimestamp().getTicks() < dueTimestamp)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }
    public boolean hasNext() {
        return !events.isEmpty();
    }

    public ProductionChain next() {
        previousTimestamp = timestamp;
        ProductionChain result = (ProductionChain) productionChain.getWaybackMachine().getInitialState().copy();
        result.setWaybackMachine(new WaybackMachine<ProductionChain>(result));

        int i = 0;

        while (isSetupChainType(events.get(i).getType())){
            if (events.get(i).getType() != EventType.PROCESSOR_UNASSIGNED){
                result.addEvent(events.get(i));
            }
            i++;
        }

        for (; i < events.size(); i++) {
            if (events.get(i).getType() == EventType.PRODUCT_CHANGED){
                break; // find production chain bending point
            }
        }
        List<Event> eventsToApply = events.subList(0, i);
        for (Event event : eventsToApply) {
            result.addEvent(event);
            timestamp = Math.max(timestamp, event.getTimestamp().getTicks());
        }
        events = new ArrayList<>(events.subList(i, events.size()));

        return result;
    }

    private boolean isSetupChainType(EventType type) {
        return type == EventType.PRODUCT_CHANGED
                || type == EventType.PROCESSOR_ASSIGNED
                || type == EventType.PROCESSOR_UNASSIGNED;
    }
}
