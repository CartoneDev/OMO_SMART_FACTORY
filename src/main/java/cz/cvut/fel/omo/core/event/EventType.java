package cz.cvut.fel.omo.core.event;

public enum EventType {
    EMPTY,
    PRODUCT_PRODUCED,
    PROCESSOR_BROKEN,
    PROCESSOR_REPAIRED,
    PROCESSOR_HALTED,
    PROCESSOR_ASSIGNED,
    PROCESSOR_UNASSIGNED,
    MATERIAL_SPENT;

    public Event getEvent() {
        return new Event(this, null);
    }

}

