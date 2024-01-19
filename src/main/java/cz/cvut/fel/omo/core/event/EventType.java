package cz.cvut.fel.omo.core.event;

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
    MATERIAL_SPENT;

    public Event getEvent(Object data, EventSource source) {
        return new Event(this, data, source);
    }
}

