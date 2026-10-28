package dev.m6fgr.epic_api.events.dispatch.dispatchers.extras;

import dev.m6fgr.epic_api.events.dispatch.abstracts.DispatchEvent;

@FunctionalInterface
public interface EventDispatcher<T extends DispatchEvent> {
    void dispatch(T event);
}
