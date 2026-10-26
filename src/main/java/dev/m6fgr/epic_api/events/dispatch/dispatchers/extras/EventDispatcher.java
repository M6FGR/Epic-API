package dev.m6fgr.epic_api.events.dispatch.dispatchers.extras;

import dev.m6fgr.epic_api.events.dispatch.abstracts.DispatchableEvent;

@FunctionalInterface
public interface EventDispatcher<T extends DispatchableEvent> {
    void dispatch(T event);
}
