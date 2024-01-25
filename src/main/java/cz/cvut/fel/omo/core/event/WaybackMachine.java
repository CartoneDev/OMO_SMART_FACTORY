package cz.cvut.fel.omo.core.event;

import cz.cvut.fel.omo.core.event.Event;
import cz.cvut.fel.omo.model.processor.Processor;
import lombok.Getter;

import java.sql.Time;
import java.util.ArrayList;

/**
 * Class for storing events and keeping partial persistence partially persistent
 * @param <T> type of the object
 */
@Getter
public class WaybackMachine <T extends Copyable<T> & Timed> {
    private final T initialState;

    private ArrayList<Event> events;

    /**
     * Constructor
     * @param init initial state of the object
     */
    public WaybackMachine(T init) {
        initialState = init;
        events = new ArrayList<>();
    }

    /**
     * Adds event to the list of events
     * @param event event to be added
     */
    public void eventHappened(Event event){
        events.add(event);
    }

    /**
     * Goes back to given timestamp
     * @param timestamp timestamp to go back to
     * @return object at given timestamp
     */
    public T goBackTo(Integer timestamp){
        T clone = (T) (initialState).copy();

        for (Event e : events){
            if (e.getTimestamp().getTicks() < timestamp){
                clone.addEvent(e);
            }
        }
        return clone;
    }

}
