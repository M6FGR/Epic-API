package dev.m6fgr.epic_api.events.dispatch.dispatchers.extras;

import dev.m6fgr.epic_api.events.dispatch.abstracts.DispatchEvent;
import dev.m6fgr.epic_api.events.dispatch.dispatchers.EventDispatch;

/**
 * Different priorities for {@link DispatchEvent} listeners.
 * <p>
 * Descending order of an integer, with the number 6 overriding events that has the {@link #HIGHEST} priority.
 * <p>
 *
 * {@link #MIDDLE} is the default priority for a listener.
 *
 *
 * @see EventDispatch#dispatchEvent(EventDispatcher) 
 */
public enum EventPriority {
    OVERRIDE, // Overrides the event, even if it had the HIGHEST priority
    HIGHEST, // First to dispatch
    HIGH,
    MIDDLE,
    LOW,
    LOWEST; // Last to dispatch

    EventPriority() {}

    public boolean is(EventPriority otherPriority) {
        return this == otherPriority;
    }

}
