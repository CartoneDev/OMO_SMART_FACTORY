package cz.cvut.fel.omo.core.event;

import cz.cvut.fel.omo.core.event.Event;

import java.sql.Time;
import java.util.ArrayList;

public class WaybackMachine <T extends Copyable & Timed> {
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
        return clone;
    }

}
