package cz.cvut.fel.omo.model.processor.states;

import cz.cvut.fel.omo.core.event.Event;
import cz.cvut.fel.omo.core.event.EventType;
import cz.cvut.fel.omo.model.processor.Processor;
import cz.cvut.fel.omo.utility.Config;

import java.util.Random;

/**
 * Processing state of a processor
 */
public class Processing extends ProcessorState{
    /**
     * Consumes an event altering the state of the processor
     * @param processor processor to consume the event
     * @param event event to be consumed
     * @return new state of the processor
     */
    @Override
    public ProcessorState consume(Processor processor, Event event) {
        return switch (event.getType()) {
            case PROCESSOR_HALTED -> new Waiting();
            case PROCESSOR_BROKEN -> new Broken();
            case PROCESSOR_START_REPAIR -> new BeingRepaired();
            case PROCESSOR_UNASSIGNED -> new Initial();
            default -> this;
        };
    }
}
