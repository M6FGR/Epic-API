package dev.m6fgr.epic_api.events.dispatch.dispatchers;

import dev.m6fgr.epic_api.events.dispatch.abstracts.DispatchableEvent;
import dev.m6fgr.epic_api.events.dispatch.dispatchers.entires.CancelableListenerEntry;
import dev.m6fgr.epic_api.events.dispatch.dispatchers.extras.EventDispatcher;
import dev.m6fgr.epic_api.events.dispatch.dispatchers.extras.EventPriority;
import dev.m6fgr.epic_api.events.dispatch.marks.HybridEvent;
import dev.m6fgr.epic_api.events.dispatch.side.EventSide;
import dev.m6fgr.epic_api.utils.code.CodeUtils;
import net.neoforged.fml.ModLoader;
import net.neoforged.neoforge.common.NeoForge;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class CancelableEventDispatch<T extends DispatchableEvent> extends EventDispatch<T> {

    protected final EventSide side;
    protected T event;
    protected final List<CancelableListenerEntry<T>> cancelableEntries = new CopyOnWriteArrayList<>();
    protected final AtomicInteger sequenceGenerator = new AtomicInteger();

    protected CancelableEventDispatch(EventSide side) {
        this.side = side;
    }

    protected CancelableEventDispatch() {
        this(EventSide.BOTH);
    }

    public static <T extends DispatchableEvent> CancelableEventDispatch<T> createDispatchable() {
        return new CancelableEventDispatch<>();
    }

    public static <T extends DispatchableEvent> CancelableEventDispatch<T> createDispatchable(EventSide side) {
        return new CancelableEventDispatch<>(side);
    }

    @Override
    public CancelableEventDispatch<T> dispatchEvent(EventDispatcher<T> dispatcher) {
        return this.dispatchEvent(dispatcher, EventPriority.MIDDLE, false);
    }

    @Override
    public CancelableEventDispatch<T> dispatchEvent(EventDispatcher<T> dispatcher, EventPriority eventPriority) {
        return this.dispatchEvent(dispatcher, eventPriority, false);
    }

    public CancelableEventDispatch<T> dispatchEvent(EventDispatcher<T> dispatcher, EventPriority priority, boolean receiveCanceled) {
        if (priority == EventPriority.OVERRIDE) {
            this.cancelableEntries.removeIf(entry -> entry.priority() != priority && this.event.canBeOverridden());
        }

        int sequence = this.sequenceGenerator.incrementAndGet();
        this.addEntry(new CancelableListenerEntry<>(dispatcher, priority,  sequence, receiveCanceled));

        this.cancelableEntries.sort(
                Comparator.<CancelableListenerEntry<T>>comparingInt(entry -> entry.priority().ordinal())
                        .reversed()
                        .thenComparingInt(CancelableListenerEntry::sequence)
        );

        return this;
    }

    @Override
    public void removeEvent(EventDispatcher<T> listener) {
        this.cancelableEntries.removeIf(entry -> entry.dispatcher().equals(listener));
    }

    public void clearEntries() {
        this.cancelableEntries.clear();
    }

    public void addEntry(CancelableListenerEntry<T> entry) {
        this.cancelableEntries.add(entry);
    }

    public void forEachEntry(Consumer<CancelableListenerEntry<T>> each) {
        this.cancelableEntries.forEach(each);
    }

    @Override
    public T postEvent(T dispatchableEvent) {
        if (dispatchableEvent == null) return null;
        this.event = dispatchableEvent;

        Class<?> eventClass = dispatchableEvent.getClass();

        boolean hasHybrid = CodeUtils.hasAnnotationSuper(eventClass, HybridEvent.class);

        if (hasHybrid && CodeUtils.getSuperClass(eventClass) != DispatchableEvent.class) {
            HybridEvent hybridEvent = eventClass.getAnnotation(HybridEvent.class);
            if (hybridEvent != null) {
                switch (hybridEvent.value()) {
                    case MOD -> ModLoader.postEvent(dispatchableEvent);
                    case GAME -> NeoForge.EVENT_BUS.post(dispatchableEvent);
                    case BOTH -> this.postToAllBuses(dispatchableEvent);
                }
            }
        }

        if (!this.cancelableEntries.isEmpty() && this.side.shouldDispatch()) {
            for (CancelableListenerEntry<T> cancelableEntry : this.cancelableEntries) {
                if (!dispatchableEvent.isCanceled() || cancelableEntry.receiveCanceled()) {
                    cancelableEntry.dispatcher().dispatch(dispatchableEvent);
                }
            }
        }

        return dispatchableEvent;
    }

    @Override
    public EventSide getSide() {
        return this.side;
    }

    @Override
    public boolean hasListeners() {
        return !this.cancelableEntries.isEmpty();
    }
}