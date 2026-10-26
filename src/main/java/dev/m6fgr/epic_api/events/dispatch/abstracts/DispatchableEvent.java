package dev.m6fgr.epic_api.events.dispatch.abstracts;

import dev.m6fgr.epic_api.events.dispatch.dispatchers.extras.EventPriority;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;
import org.jetbrains.annotations.ApiStatus.Internal;

import java.lang.reflect.Modifier;

public abstract class DispatchableEvent extends Event implements IModBusEvent {

    protected boolean canceled;
    protected EventPriority currentPriority;

    protected DispatchableEvent() {
        super();
    }

    public boolean isCanceled() {
        return this.canceled;
    }

    public boolean canBeOverridden() {
        return true;
    }

    public void setCanceled(boolean canceled) {
        this.canceled = canceled;
    }

    public EventPriority getPriority() {
        return this.currentPriority;
    }

    @Internal
    public void setPriority(EventPriority currentPriority) {
        this.currentPriority = currentPriority;
    }

    public String getEventClsName() {
        Class<?> cls = this.getClass();
        Class<?> superclass = cls.getSuperclass();

        if (superclass != null && superclass != Object.class && superclass != DispatchableEvent.class && Modifier.isAbstract(superclass.getModifiers())) {
            return superclass.getSimpleName() + "#" + cls.getSimpleName();
        }

        return cls.getSimpleName();
    }
}
