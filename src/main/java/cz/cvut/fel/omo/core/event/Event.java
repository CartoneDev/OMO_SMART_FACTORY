package cz.cvut.fel.omo.core.event;

import cz.cvut.fel.omo.core.Clock;
import lombok.Getter;
import lombok.Setter;

/**
 * Basic event class
 */
@Getter
public class Event {
    private final EventSource source;
    private EventType type;
    private Object payload;
    @Setter
    private Object solver;

    private Clock timestamp;


    /**
     * Constructor for event, automatically sets timestamp to current time
     * @param type  type of event
     * @param data payload
     * @param source source of event
     */
    public Event(EventType type, Object data, EventSource source) {
        this.type = type;
        this.payload = data;
        timestamp = Clock.getTime();
        this.source = source;
    }

    /**
     * Constructor for event, automatically sets timestamp to current time
     * @return empty event
     */
    public static Event getEmptyEvent() {
        return new Event(EventType.EMPTY, null, null);
    }
}

