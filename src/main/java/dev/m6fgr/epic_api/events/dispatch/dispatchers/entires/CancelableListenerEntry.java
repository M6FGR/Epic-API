package dev.m6fgr.epic_api.events.dispatch.dispatchers.entires;

import dev.m6fgr.epic_api.events.dispatch.abstracts.DispatchEvent;
import dev.m6fgr.epic_api.events.dispatch.dispatchers.extras.EventDispatcher;
import dev.m6fgr.epic_api.events.dispatch.dispatchers.extras.EventPriority;

public record CancelableListenerEntry<T extends DispatchEvent>(EventDispatcher<T> dispatcher, EventPriority priority, int sequence, boolean receiveCanceled) {}