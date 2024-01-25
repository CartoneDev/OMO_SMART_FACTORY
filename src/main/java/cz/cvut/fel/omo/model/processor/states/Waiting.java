package cz.cvut.fel.omo.model.processor.states;

import cz.cvut.fel.omo.core.event.Event;
import cz.cvut.fel.omo.model.processor.Processor;

import java.util.Objects;

import static cz.cvut.fel.omo.core.event.EventType.PROCESSOR_STARTED;

/**
 * Waiting state of a processor
 */
public class Waiting extends ProcessorState{
    @Override
    public ProcessorState consume(Processor processor, Event event) {
        if (Objects.requireNonNull(event.getType()) == PROCESSOR_STARTED) {
            return new Processing();
        }
        return this;
    }
}
