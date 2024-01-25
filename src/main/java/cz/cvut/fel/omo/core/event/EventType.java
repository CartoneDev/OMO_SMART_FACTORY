package cz.cvut.fel.omo.core.event;

/**
 * Enum for types of events
 */
public enum EventType {
    EMPTY,
    PRODUCT_PRODUCED,
    PROCESSOR_HALTED,
    PROCESSOR_BROKEN,
    PROCESSOR_STARTED,
    PROCESSOR_REPAIRED,
    PROCESSOR_ASSIGNED,
    PROCESSOR_UNASSIGNED,
    PROCESSOR_START_REPAIR,
    PRODUCT_CHANGED;
}

