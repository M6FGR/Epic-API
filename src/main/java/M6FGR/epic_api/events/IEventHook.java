package M6FGR.epic_api.events;

import org.jetbrains.annotations.ApiStatus.Experimental;
import yesman.epicfight.api.event.Event;


/**
 * A lightweight event system hook that allows for dynamic event execution.
 * Check EpicAPI#commonEvents, for example
 */
@Experimental
public interface IEventHook<T extends Event> {
    T post();
}