package cz.cvut.fel.omo.core.event;

import cz.cvut.fel.omo.core.event.Event;
import cz.cvut.fel.omo.model.processor.Processor;
import lombok.Getter;

import java.sql.Time;
import java.util.ArrayList;

@Getter
public class WaybackMachine <T extends Copyable<T> & Timed> {
    private final T initialState;

    private ArrayList<Event> events;
    public WaybackMachine(T init) {
        initialState = init;
        events = new ArrayList<>();
    }

    public void eventHappened(Event event){
        events.add(event);
    }

    @SuppressWarnings("unchecked")
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
