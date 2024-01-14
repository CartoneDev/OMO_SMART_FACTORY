package cz.cvut.fel.omo.model.processor.states;

import cz.cvut.fel.omo.core.event.Event;
import cz.cvut.fel.omo.model.processor.Processor;

public abstract class ProcessorState {
    public Event process(Processor processor) {
        return null;
    }

    public boolean equals(ProcessorState other){
        return this.getClass() == other.getClass();
    }
}
