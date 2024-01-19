package cz.cvut.fel.omo.core.event;

import cz.cvut.fel.omo.core.Clock;
import lombok.Getter;
import lombok.Setter;

@Getter
/**
 * Basic event class
 */
public class Event {
    private final EventSource source;
    private EventType type;
    private Object payload;
    @Setter
    private Object solver;

    private Clock timestamp;


    public Event(EventType type, Object data, EventSource source) {
        this.type = type;
        this.payload = data;
        timestamp = Clock.getTime();
        this.source = source;
    }

    public static Event getEmptyEvent() {
        return new Event(EventType.EMPTY, null, null);
    }
}
