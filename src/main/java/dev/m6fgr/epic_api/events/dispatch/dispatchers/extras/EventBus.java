package dev.m6fgr.epic_api.events.dispatch.dispatchers.extras;

import dev.m6fgr.epic_api.events.dispatch.abstracts.DispatchableEvent;
import dev.m6fgr.epic_api.events.dispatch.dispatchers.EventDispatch;

/**
 Different bus types for {@link EventDispatch}.
 <p>
 {@link #MOD}: The mod's event bus.
 <p>
 {@link #GAME}: NeoForge's event bus.
 <p>
 {@link #BOTH}: Both NeoForge and The mod's event buses.

 @see EventDispatch#postEvent(DispatchableEvent)
*/
public enum EventBus {
    MOD,
    GAME,
    BOTH
}
