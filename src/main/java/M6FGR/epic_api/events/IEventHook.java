package M6FGR.epic_api.events;

import M6FGR.epic_api.main.EpicAPI;
import org.jetbrains.annotations.ApiStatus.Experimental;

import java.lang.reflect.Constructor;


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
    void postClient();


    /**
     * Dynamically instantiates and fires one or more event classes.
     * * @param eventClasses The classes to be fired. Each class MUST have
     * a public, no-argument constructor.
     */
    @SafeVarargs
    static void fire(Class<? extends IEventHook>... eventClasses) {
        for (Class<? extends IEventHook> eventCls : eventClasses) {
            try {
                Constructor<? extends IEventHook> constructor = eventCls.getDeclaredConstructor();
                constructor.setAccessible(true);
                IEventHook event = constructor.newInstance();
                event.post();
                if (EpicAPI.isClient()) {
                    event.postClient();
                }
                EpicAPI.debug("Fired [{}]", eventCls.getSimpleName());
            } catch (NoSuchMethodException e) {
                EpicAPI.err("Failed to fire event [{}], no public constructor found!", eventCls.getSimpleName());
            } catch (Exception e) {
                EpicAPI.err("Failed to fire event [{}]: {}", eventCls.getSimpleName(), e.getMessage());
            }
        }
    }

}