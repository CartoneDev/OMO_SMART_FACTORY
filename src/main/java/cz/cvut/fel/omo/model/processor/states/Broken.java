package cz.cvut.fel.omo.model.processor.states;

import cz.cvut.fel.omo.core.event.Event;
import cz.cvut.fel.omo.core.event.EventType;
import cz.cvut.fel.omo.model.processor.Processor;

public class Broken extends ProcessorState{
    @Override
    public Event process(Processor processor) {
        return new Event(EventType.PROCESSOR_HALTED, processor);
    }
}
