package dev.m6fgr.epic_api.cls.loadable;

import net.neoforged.bus.api.IEventBus;

public interface ILoadableClass {

    /** Primary method to register Items, Blocks, Entities, etc... Called Directly. */
    default void onModConstruct(IEventBus modBus) {}

    /** Use to register Client-only listeners to the Mod Bus. Called Directly. */
    default void onModClientConstruct(IEventBus modBus) {}

    /** Use to register listeners to the global NeoForge.EVENT_BUS. Called Directly. */
    default void onGameConstruct(IEventBus neoBus) {}

    /** Use to register listeners to the client side of NeoForge.EVENT_BUS. Called Directly. */
    default void onGameClientConstruct(IEventBus neoBus) {}

    /** Overridden so the class does not log/throws an exception that it wasn't loaded */
    default boolean ignoreNotLoadedWarning() {
        return false;
    }

}