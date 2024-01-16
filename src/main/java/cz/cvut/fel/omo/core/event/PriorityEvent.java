package cz.cvut.fel.omo.core.event;

public class PriorityEvent extends Event implements Comparable<PriorityEvent> {
    Integer priority;
    public PriorityEvent(EventType type, Object data, Integer priority){
        super(type, data);
        this.priority=priority;
    }

    @Override
    public int compareTo(PriorityEvent o) {
        return this.priority.compareTo(o.priority);
    }
}
