package cz.cvut.fel.omo.model.processor.states;

import cz.cvut.fel.omo.core.event.Event;
import cz.cvut.fel.omo.core.event.EventType;
import cz.cvut.fel.omo.model.processor.Processor;

import java.util.Objects;

/**
 * Broken state of a processor
 */
public class BeingRepaired extends ProcessorState{
    /**
     * Consumes an event altering the state of the processor
     * @param processor processor to consume the event
     * @param event event to be consumed (has to be of type PROCESSOR_REPAIRED)
     * @return new state
     */
    @Override
    public ProcessorState consume(Processor processor, Event event) {
        if (Objects.requireNonNull(event.getType()) == EventType.PROCESSOR_REPAIRED) {
            if (processor.isAssigned()) {
                return new Processing();
            } else {
                return new Initial();
            }
        }
        return this;
    }
}
