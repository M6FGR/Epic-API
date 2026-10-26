package dev.m6fgr.epic_api.events.dispatch.dispatchers;

import dev.m6fgr.epic_api.events.dispatch.abstracts.DispatchableEvent;
import dev.m6fgr.epic_api.events.dispatch.dispatchers.entires.ListenerEntry;
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

public class EventDispatch<T extends DispatchableEvent> {

    protected final EventSide side;
    protected T event;
    protected final List<ListenerEntry<T>> entries = new CopyOnWriteArrayList<>();
    private final AtomicInteger sequenceGenerator = new AtomicInteger();

    protected EventDispatch(EventSide side) {
        this.side = side;
    }

    protected EventDispatch() {
        this(EventSide.BOTH);
    }

    public static <T extends DispatchableEvent> EventDispatch<T> createDispatchable() {
        return new EventDispatch<>();
    }

    public static <T extends DispatchableEvent> EventDispatch<T> createDispatchable(EventSide side) {
        return new EventDispatch<>(side);
    }

    public EventDispatch<T> dispatchEvent(EventDispatcher<T> listener) {
        return this.dispatchEvent(listener, EventPriority.MIDDLE);
    }

    public EventDispatch<T> dispatchEvent(EventDispatcher<T> listener, EventPriority priority) {
        if (priority == EventPriority.OVERRIDE && this.event != null) {
            this.entries.removeIf(entry -> entry.priority() != priority && this.event.canBeOverridden());
        }

        int sequence = this.sequenceGenerator.incrementAndGet();
        this.addListener(new ListenerEntry<>(listener, priority, sequence));

        this.entries.sort(
                Comparator.<ListenerEntry<T>>comparingInt(value -> value.priority().ordinal())
                        .reversed()
                        .thenComparingInt(ListenerEntry::sequence)
        );
        return this;
    }

    public void removeEvent(EventDispatcher<T> listener) {
        this.entries.removeIf(entry -> entry.dispatcher().equals(listener));
    }

    public void forEachListener(Consumer<ListenerEntry<T>> eachListener) {
        this.entries.forEach(eachListener);
    }

    public void clearListeners() {
        this.entries.clear();
    }

    public void addListener(ListenerEntry<T> entry) {
        this.entries.add(entry);
    }


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

        if (!this.entries.isEmpty() && this.side.shouldDispatch()) {
            for (ListenerEntry<T> entry : this.entries) {
                dispatchableEvent.setPriority(entry.priority());
                entry.dispatcher().dispatch(dispatchableEvent);
            }
        }
        return dispatchableEvent;
    }

    public void postToAllBuses(T dispatchableEvent) {
        ModLoader.postEvent(dispatchableEvent);
        NeoForge.EVENT_BUS.post(dispatchableEvent);
    }

    public boolean postAndCheckCanceled(T dispatchableEvent) {
        return this.postEvent(dispatchableEvent).isCanceled();
    }

    public void postNoReturn(T dispatchableEvent) {
        this.postEvent(dispatchableEvent);
    }

    public EventSide getSide() {
        return this.side;
    }
    public boolean hasListeners() {
        return !this.entries.isEmpty();
    }
}