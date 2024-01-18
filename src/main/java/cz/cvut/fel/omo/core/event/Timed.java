package cz.cvut.fel.omo.core.event;


/**
 * Interface for entities which exist in time and consume events
 */
public interface Timed {
    void addEvent(Event event);
}
