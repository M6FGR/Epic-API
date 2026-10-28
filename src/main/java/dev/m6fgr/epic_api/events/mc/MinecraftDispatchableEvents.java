package dev.m6fgr.epic_api.events.mc;

import dev.m6fgr.epic_api.cls.lists.ArraysList;
import dev.m6fgr.epic_api.events.dispatch.dispatchers.EventDispatch;
import dev.m6fgr.epic_api.events.dispatch.side.EventSide;
import dev.m6fgr.epic_api.events.mc.client.ClientInitializeDispatchEvent;
import dev.m6fgr.epic_api.events.mc.client.ClientTickDispatchEvent;
import dev.m6fgr.epic_api.events.mc.client.WorldLoadDispatchEvent;
import dev.m6fgr.epic_api.events.mc.entity.EntitySummonDispatchEvent;
import dev.m6fgr.epic_api.events.mc.player.PlayerJoinDispatchEvent;
import dev.m6fgr.epic_api.events.mc.player.PlayerTickDispatchEvent;
import dev.m6fgr.epic_api.events.mc.server.ServerInitializeDispatchEvent;
import dev.m6fgr.epic_api.events.mc.server.ServerStopDispatchEvent;
import dev.m6fgr.epic_api.events.mc.server.ServerTickDispatchEvent;
import org.jetbrains.annotations.ApiStatus.Experimental;

@Experimental
public class MinecraftDispatchableEvents {
    private static final ArraysList<EventDispatch<?>> EVENTS = new ArraysList<>();

    public static class Server {
        // fired when the server is loading/starting
        public static final EventDispatch<ServerInitializeDispatchEvent.Pre> SERVER_START_PRE = EventDispatch.createDispatchable(EventSide.SERVER);
        // fired when the server is started and running
        public static final EventDispatch<ServerInitializeDispatchEvent.Post> SERVER_START_POST = EventDispatch.createDispatchable(EventSide.SERVER);
        // fired when the server shuts off (maybe when it crashes too
        public static final EventDispatch<ServerStopDispatchEvent> SERVER_STOP = EventDispatch.createDispatchable(EventSide.SERVER);
        // fired when the server is online and ticking
        public static final EventDispatch<ServerTickDispatchEvent> SERVER_TICK = EventDispatch.createDispatchable(EventSide.SERVER);
    }

    public static class Player {
        // both fired when the player is ticking in the world
        public static final EventDispatch<PlayerTickDispatchEvent.Pre> TICK_PRE = EventDispatch.createDispatchable();
        public static final EventDispatch<PlayerTickDispatchEvent.Post> TICK_POST = EventDispatch.createDispatchable();
        // fired when the player joins a client-side world (single-player/integrated server)
        public static final EventDispatch<PlayerJoinDispatchEvent.Client> JOIN_CLIENT = EventDispatch.createDispatchable(EventSide.CLIENT);
        // fired when the player joins on a public server
        public static final EventDispatch<PlayerJoinDispatchEvent.Server> JOIN_SERVER = EventDispatch.createDispatchable(EventSide.SERVER);

    }

    public static class Client {
        // fired when the world is being initialized
        public static final EventDispatch<WorldLoadDispatchEvent.Initialize> WORLD_INIT = EventDispatch.createDispatchable(EventSide.CLIENT);
        // fired when the player joins an already existing world
        public static final EventDispatch<WorldLoadDispatchEvent.Join> WORLD_JOIN = EventDispatch.createDispatchable(EventSide.CLIENT);
        // fired when the player creates and joins a world
        public static final EventDispatch<WorldLoadDispatchEvent.Create> WORLD_CREATE = EventDispatch.createDispatchable(EventSide.CLIENT);
        // both fired when the player initializes the client
        public static final EventDispatch<ClientInitializeDispatchEvent.Pre> CLIENT_START_PRE = EventDispatch.createDispatchable(EventSide.CLIENT);
        public static final EventDispatch<ClientInitializeDispatchEvent.Post> CLIENT_START_POST = EventDispatch.createDispatchable(EventSide.CLIENT);
        // fired when the client is initialized and ticking
        public static final EventDispatch<ClientTickDispatchEvent> CLIENT_TICK = EventDispatch.createDispatchable(EventSide.CLIENT);
    }

    public static class Entity {
        // fired when an entity is summoned in the client world
        public static final EventDispatch<EntitySummonDispatchEvent.Client> SUMMON_CLIENT = EventDispatch.createDispatchable(EventSide.CLIENT);
        // fired when an entity is summoned in the server's world
        public static final EventDispatch<EntitySummonDispatchEvent.Server> SUMMON_SERVER = EventDispatch.createDispatchable(EventSide.SERVER);
    }
}