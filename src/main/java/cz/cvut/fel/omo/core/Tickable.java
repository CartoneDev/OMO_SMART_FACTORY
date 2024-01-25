package cz.cvut.fel.omo.core;

import cz.cvut.fel.omo.core.event.Event;


/**
 * Interface for tickable objects
 * Such objects are objects that can be ticked and produce an event as a result
 */
public interface Tickable {
    Event tick();
}
