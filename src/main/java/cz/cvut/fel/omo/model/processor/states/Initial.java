package cz.cvut.fel.omo.model.processor.states;

import cz.cvut.fel.omo.core.event.Event;
import cz.cvut.fel.omo.core.event.EventType;
import cz.cvut.fel.omo.model.ProductionChain;
import cz.cvut.fel.omo.model.processor.Processor;

import java.util.Objects;

/**
 * Initial state of a processor
 * Once processor is added to the production chain, it is in the initial state.
 */
public class Initial extends ProcessorState{
    /**
     * Consumes an event altering the state of the processor
     * @param processor processor to consume the event
     * @param event event to be consumed (has to be of type PROCESSOR_ASSIGNED)
     * @return new state
     */
    @Override
    public ProcessorState consume(Processor processor, Event event) {
        if (Objects.requireNonNull(event.getType()) == EventType.PROCESSOR_ASSIGNED) {
            return handleAssigned(processor, event);
        }
        return this;
    }
}
