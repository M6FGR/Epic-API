package dev.m6fgr.epic_api.events.mc;

import dev.m6fgr.epic_api.cls.lists.ArraysList;
import dev.m6fgr.epic_api.events.dispatch.abstracts.DispatchableEvent;
import dev.m6fgr.epic_api.events.dispatch.dispatchers.EventDispatch;
import dev.m6fgr.epic_api.events.dispatch.side.EventSide;
import dev.m6fgr.epic_api.events.mc.client.ClientInitializeDispatchableEvent;
import dev.m6fgr.epic_api.events.mc.client.ClientTickDispatchableEvent;
import dev.m6fgr.epic_api.events.mc.client.WorldLoadDispatchableEvent;
import dev.m6fgr.epic_api.events.mc.entity.EntitySummonDispatchableEvent;
import dev.m6fgr.epic_api.events.mc.player.PlayerJoinDispatchableEvent;
import dev.m6fgr.epic_api.events.mc.player.PlayerTickDispatchableEvent;
import dev.m6fgr.epic_api.events.mc.server.ServerInitializeDispatchableEvent;
import dev.m6fgr.epic_api.events.mc.server.ServerStopDispatchableEvent;
import dev.m6fgr.epic_api.events.mc.server.ServerTickDispatchableEvent;
import org.jetbrains.annotations.ApiStatus.Experimental;

@Experimental
public class MinecraftDispatchableEvents {
    private static final ArraysList<EventDispatch<?>> EVENTS = new ArraysList<>();

    public static class Server {
        // fired when the server is loading/starting
        public static final EventDispatch<ServerInitializeDispatchableEvent.Pre> SERVER_START_PRE = EventDispatch.createDispatchable(EventSide.SERVER);
        // fired when the server is started and running
        public static final EventDispatch<ServerInitializeDispatchableEvent.Post> SERVER_START_POST = EventDispatch.createDispatchable(EventSide.SERVER);
        // fired when the server shuts off (maybe when it crashes too
        public static final EventDispatch<ServerStopDispatchableEvent> SERVER_STOP = EventDispatch.createDispatchable(EventSide.SERVER);
        // fired when the server is online and ticking
        public static final EventDispatch<ServerTickDispatchableEvent> SERVER_TICK = EventDispatch.createDispatchable(EventSide.SERVER);
    }

    public static class Player {
        // both fired when the player is ticking in the world
        public static final EventDispatch<PlayerTickDispatchableEvent.Pre> TICK_PRE = EventDispatch.createDispatchable();
        public static final EventDispatch<PlayerTickDispatchableEvent.Post> TICK_POST = EventDispatch.createDispatchable();
        // fired when the player joins a client-side world (single-player/integrated server)
        public static final EventDispatch<PlayerJoinDispatchableEvent.Client> JOIN_CLIENT = EventDispatch.createDispatchable(EventSide.CLIENT);
        // fired when the player joins on a public server
        public static final EventDispatch<PlayerJoinDispatchableEvent.Server> JOIN_SERVER = EventDispatch.createDispatchable(EventSide.SERVER);

    }

    public static class Client {
        // fired when the world is being initialized
        public static final EventDispatch<WorldLoadDispatchableEvent.Initialize> WORLD_INIT = EventDispatch.createDispatchable(EventSide.CLIENT);
        // fired when the player joins an already existing world
        public static final EventDispatch<WorldLoadDispatchableEvent.Join> WORLD_JOIN = EventDispatch.createDispatchable(EventSide.CLIENT);
        // fired when the player creates and joins a world
        public static final EventDispatch<WorldLoadDispatchableEvent.Create> WORLD_CREATE = EventDispatch.createDispatchable(EventSide.CLIENT);
        // both fired when the player initializes the client
        public static final EventDispatch<ClientInitializeDispatchableEvent.Pre> CLIENT_START_PRE = EventDispatch.createDispatchable(EventSide.CLIENT);
        public static final EventDispatch<ClientInitializeDispatchableEvent.Post> CLIENT_START_POST = EventDispatch.createDispatchable(EventSide.CLIENT);
        // fired when the client is initialized and ticking
        public static final EventDispatch<ClientTickDispatchableEvent> CLIENT_TICK = EventDispatch.createDispatchable(EventSide.CLIENT);
    }

    public static class Entity {
        // fired when an entity is summoned in the client world
        public static final EventDispatch<EntitySummonDispatchableEvent.Client> SUMMON_CLIENT = EventDispatch.createDispatchable(EventSide.CLIENT);
        // fired when an entity is summoned in the server's world
        public static final EventDispatch<EntitySummonDispatchableEvent.Server> SUMMON_SERVER = EventDispatch.createDispatchable(EventSide.SERVER);
    }

    public static ArraysList<EventDispatch<? extends DispatchableEvent>> getEventsAsList() {
        return EVENTS;
    }

    static {
        EVENTS.addAsArray(
                Player.JOIN_CLIENT,
                Player.JOIN_SERVER
                //Player.TICK_POST,
                //Player.TICK_PRE
        );
        EVENTS.addAsArray(
                Client.CLIENT_START_POST,
                Client.CLIENT_START_PRE,
                Client.WORLD_CREATE,
                Client.WORLD_INIT,
                Client.WORLD_JOIN
        );
        EVENTS.addAsArray(
                Server.SERVER_START_POST,
                Server.SERVER_START_PRE,
                Server.SERVER_STOP
                //Server.SERVER_TICK
        );
        EVENTS.addAsArray(
                Entity.SUMMON_CLIENT,
                Entity.SUMMON_SERVER
        );
    }
    
}
