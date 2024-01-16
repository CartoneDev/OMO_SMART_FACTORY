package cz.cvut.fel.omo.model.processor.states;

import cz.cvut.fel.omo.core.event.Event;
import cz.cvut.fel.omo.core.event.EventType;
import cz.cvut.fel.omo.model.processor.Processor;

import java.util.Objects;

public class Broken extends ProcessorState{
    @Override
    public ProcessorState consume(Processor processor, Event event) {
        if (Objects.requireNonNull(event.getType()) == EventType.PROCESSOR_START_REPAIR) {
            return new BeingRepaired();
        }
        return this;
    }
}
