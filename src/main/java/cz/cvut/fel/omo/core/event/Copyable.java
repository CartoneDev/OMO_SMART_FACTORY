package cz.cvut.fel.omo.core.event;

/**
 * Interface for copyable objects
 * @param <T> type of the object
 */
public interface Copyable<T> {
    T copy();
}
