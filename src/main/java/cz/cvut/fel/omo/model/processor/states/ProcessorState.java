package cz.cvut.fel.omo.model.processor.states;

import cz.cvut.fel.omo.core.event.Event;
import cz.cvut.fel.omo.model.ProductionChain;
import cz.cvut.fel.omo.model.processor.Processor;

/**
 * Abstract class representing a state of a processor
 */
public abstract class ProcessorState {
    /**
     * Consumes an event
     * @param processor processor
     * @param event event
     * @return new state
     */
    public ProcessorState consume(Processor processor, Event event) {
	return this;
    }

    /**
     * Handles an assigned event
     * @param processor assigned processor
     * @param event assigning event
     * @return new state
     */
    public ProcessorState handleAssigned(Processor processor, Event event) {
        processor.setProductionChain((ProductionChain) event.getSource());
        return new Waiting();
    }

    /**
     * Returns a string representation of the state
     * @return string representation of the state
     */
    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
