package dev.m6fgr.epic_api.events.bus;

import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;

import java.util.function.Consumer;

public class NeoBus implements IEventBus {

    private NeoBus() {}

    public static NeoBus get() {
        return new NeoBus();
    }

    @Override
    public void register(Object target) {
        NeoForge.EVENT_BUS.register(target);
    }

    @Override
    public <T extends Event> void addListener(Consumer<T> consumer) {
        NeoForge.EVENT_BUS.addListener(consumer);
    }

    @Override
    public <T extends Event> void addListener(Class<T> eventType, Consumer<T> consumer) {
        NeoForge.EVENT_BUS.addListener(eventType, consumer);
    }

    @Override
    public <T extends Event> void addListener(EventPriority priority, Consumer<T> consumer) {
        NeoForge.EVENT_BUS.addListener(priority, consumer);
    }

    @Override
    public <T extends Event> void addListener(EventPriority priority, Class<T> eventType, Consumer<T> consumer) {
        NeoForge.EVENT_BUS.addListener(priority, eventType, consumer);
    }

    @Override
    public <T extends Event> void addListener(EventPriority priority, boolean receiveCanceled, Consumer<T> consumer) {
        NeoForge.EVENT_BUS.addListener(priority, receiveCanceled, consumer);
    }

    @Override
    public <T extends Event> void addListener(EventPriority priority, boolean receiveCanceled, Class<T> eventType, Consumer<T> consumer) {
        NeoForge.EVENT_BUS.addListener(priority, receiveCanceled, eventType, consumer);
    }

    @Override
    public <T extends Event> void addListener(boolean receiveCanceled, Consumer<T> consumer) {
        NeoForge.EVENT_BUS.addListener(receiveCanceled, consumer);
    }

    @Override
    public <T extends Event> void addListener(boolean receiveCanceled, Class<T> eventType, Consumer<T> consumer) {
        NeoForge.EVENT_BUS.addListener(receiveCanceled, eventType, consumer);
    }

    @Override
    public void unregister(Object object) {
        NeoForge.EVENT_BUS.unregister(object);
    }

    @Override
    public <T extends Event> T post(T event) {
        return NeoForge.EVENT_BUS.post(event);
    }

    @Override
    public <T extends Event> T post(EventPriority phase, T event) {
        return NeoForge.EVENT_BUS.post(phase, event);
    }

    @Override
    public void start() {
        NeoForge.EVENT_BUS.start();
    }
}