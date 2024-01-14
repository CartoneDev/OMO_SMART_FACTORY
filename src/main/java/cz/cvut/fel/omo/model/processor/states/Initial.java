package cz.cvut.fel.omo.model.processor.states;

import cz.cvut.fel.omo.core.event.Event;
import cz.cvut.fel.omo.core.event.EventType;
import cz.cvut.fel.omo.model.processor.Processor;

/**
 * Once processor is added to the production chain, it is in the initial state.
 */
public class Initial extends ProcessorState{
    @Override
    public Event process(Processor processor) {
        processor.setState(new Processing());
        return new Event(EventType.PROCESSOR_STARTED, processor);
    }
    @Override
    public boolean equals(ProcessorState other) {
        return this.getClass() == other.getClass();
    }
}
