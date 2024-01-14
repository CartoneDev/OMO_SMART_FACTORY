package cz.cvut.fel.omo.model.processor;

import cz.cvut.fel.omo.core.event.Event;

import java.util.ArrayList;

public class WaybackMachine {
    private final Processor initialState;

    private ArrayList<Event> events;
    public WaybackMachine(Processor init) {
        initialState = init;
        events = new ArrayList<>();
    }

    public void eventHappened(Event event){
        events.add(event);
    }




}
