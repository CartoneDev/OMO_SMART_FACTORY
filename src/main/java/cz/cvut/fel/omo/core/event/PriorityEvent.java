package cz.cvut.fel.omo.core.event;

public class PriorityEvent extends Event implements Comparable<PriorityEvent> {
    Integer priority;
    public PriorityEvent(EventType type, Object data, Integer priority, EventSource source){
        super(type, data, source);
        this.priority=priority;
    }

    @Override
    public int compareTo(PriorityEvent o) {
        int priorityComparison = this.priority.compareTo(o.priority);

        if (priorityComparison == 0) {
            // If priorities are the same, compare timestamp.ticks (inverted)
            return Long.compare(o.getTimestamp().getTicks(), this.getTimestamp().getTicks());
        }

        return priorityComparison;
    }
}
