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
    MATERIAL_SPENT;

    public Event getEvent() {
        return new Event(this, null);
    }
    public static boolean isProcessorEvent(EventType type){
        return type == PROCESSOR_BROKEN  || type ==  PROCESSOR_HALTED     || type ==  PROCESSOR_ASSIGNED ||
               type == PROCESSOR_STARTED || type ==  PROCESSOR_UNASSIGNED || type ==  PROCESSOR_REPAIRED;
    }
}

