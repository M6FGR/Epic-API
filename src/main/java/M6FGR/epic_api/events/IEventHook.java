package M6FGR.epic_api.events;

import M6FGR.epic_api.main.EpicAPI;
import org.jetbrains.annotations.ApiStatus.Experimental;

import java.util.NoSuchElementException;

/**
 * A lightweight event system hook that allows for dynamic event execution.
 * Any class implementing this interface must provide a parameterless
 * constructor for the firing system to instantiate it via reflection.
 */
@Experimental
public interface IEventHook {

    /**
     * Executes the main logic of the event.
     * This method is called automatically after the event class is instantiated.
     */
    void post();

    /**
     * Dynamically instantiates and fires one or more event classes.
     * * @param eventClasses The classes to be fired. Each class MUST have
     * a public, no-argument constructor.
     */
    @SafeVarargs
    static void fire(Class<? extends IEventHook>... eventClasses) {
        for (Class<? extends IEventHook> eventCls : eventClasses) {
            try {
                // Uses reflection to create a new instance of the event class
                // ensuring that events are isolated and fresh each time they fire.
                IEventHook event = eventCls.getDeclaredConstructor().newInstance();
                // Executes the defined event logic
                event.post();
            } catch (NoSuchElementException noCons) {
                EpicAPI.LOGGER.error("Failed to fire event [{}], it doesn't have a public constructor!", eventCls.getSimpleName());
            } catch (Exception e) {
                // Catches instantiation errors (e.g., missing constructor)
                // to prevent the API from crashing the game when an event fails.
                EpicAPI.LOGGER.error("Failed to fire event [{}], {}", eventCls.getSimpleName(), e.getMessage());
            }
        }
    }
}