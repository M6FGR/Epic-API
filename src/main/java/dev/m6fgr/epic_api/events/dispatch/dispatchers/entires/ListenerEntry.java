package dev.m6fgr.epic_api.events.dispatch.dispatchers.entires;

import dev.m6fgr.epic_api.events.dispatch.abstracts.DispatchableEvent;
import dev.m6fgr.epic_api.events.dispatch.dispatchers.extras.EventDispatcher;
import dev.m6fgr.epic_api.events.dispatch.dispatchers.extras.EventPriority;

public record ListenerEntry<T extends DispatchableEvent>(EventDispatcher<T> dispatcher, EventPriority priority, int sequence) {}
