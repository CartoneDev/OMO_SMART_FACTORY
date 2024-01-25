package cz.cvut.fel.omo.core.event;


/**
 * Interface for entities which exist in time and consume events
 */
public interface Timed {
    /**
     * Consumes an event
     * @param event event to be consumed
     */
    void addEvent(Event event);

    /**
     * Returnes timed entity on a given timestamp
     * @param timestamp timestamp to return the entity on
     * @return timed entity on a given timestamp
     */
    Timed onTime(Integer timestamp);
}
