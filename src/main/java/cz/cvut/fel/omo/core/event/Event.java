package cz.cvut.fel.omo.core.event;

import lombok.Getter;

@Getter
/**
 * Basic event class
 */
public class Event {
    private EventType type;
    private Object payload;

    public Event(EventType type, Object data) {
        this.type = type;
        this.payload = data;
    }

}
