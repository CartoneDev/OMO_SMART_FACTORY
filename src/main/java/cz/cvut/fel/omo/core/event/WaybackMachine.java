package cz.cvut.fel.omo.core.event;

import cz.cvut.fel.omo.core.event.Event;

import java.util.ArrayList;

public class WaybackMachine <T> {
    private final T initialState;

    private ArrayList<Event> events;
    public WaybackMachine(T init) {
        initialState = init;
        events = new ArrayList<>();
    }

    public void eventHappened(Event event){
        events.add(event);
    }




}
