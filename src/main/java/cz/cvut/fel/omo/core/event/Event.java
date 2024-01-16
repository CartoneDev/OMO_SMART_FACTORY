package cz.cvut.fel.omo.core.event;

import cz.cvut.fel.omo.core.Clock;
import lombok.Getter;
import lombok.Setter;

@Getter
/**
 * Basic event class
 */
public class Event {
    private EventType type;
    private Object payload;
    @Setter
    private Object solver;

    private Clock timestamp;


    public Event(EventType type, Object data) {
        this.type = type;
        this.payload = data;
        timestamp = Clock.getTime();
    }

    public static Event getEvent(EventType type, Object data) {
        return new Event(type,data);
    }

    public static Event getEvent(EventType type) {
        return new Event(type, null);
    }

    public static Event getEmptyEvent() {
        return new Event(EventType.EMPTY, null);
    }
}
