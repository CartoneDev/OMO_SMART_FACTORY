package cz.cvut.fel.omo.model.processor.states;

import cz.cvut.fel.omo.core.event.Event;
import cz.cvut.fel.omo.core.event.EventType;
import cz.cvut.fel.omo.model.processor.Processor;
import cz.cvut.fel.omo.utility.Config;

import java.util.Random;

public class Processing extends ProcessorState{
    @Override
    public Event process(Processor processor) {
        Config.getDecayModel().decay(processor);
        boolean isBroken = new Random().nextDouble() > 0.7 || processor.getDamage() > 0.3 && new Random().nextDouble() > 0.7;
        if (isBroken) {
            processor.setState(new Broken());
            return new Event(EventType.PROCESSOR_BROKEN, processor);
        }
        return Event.getEmptyEvent();
    }

}
