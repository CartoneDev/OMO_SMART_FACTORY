package cz.cvut.fel.omo.model.processor.states;

import cz.cvut.fel.omo.core.event.Event;
import cz.cvut.fel.omo.model.ProductionChain;
import cz.cvut.fel.omo.model.processor.Processor;

public abstract class ProcessorState {
    public ProcessorState consume(Processor processor, Event event) {
	return this;
    }

    public ProcessorState handleUnassigned(Processor processor, Event event) {
        processor.setProductionChain(null);
        return new Initial();
    }
    public ProcessorState handleAssigned(Processor processor, Event event) {
        processor.setProductionChain((ProductionChain) event.getSource());
        return new Waiting();
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
