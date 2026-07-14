package M6FGR.epic_api.events.mc;

import M6FGR.epic_api.events.mc.client.WorldLoadEventHook;
import M6FGR.epic_api.events.mc.entity.EntitySummonEventHook;
import M6FGR.epic_api.events.mc.player.PlayerJoinEventHook;
import M6FGR.epic_api.events.mc.player.PlayerTickEventHook;
import M6FGR.epic_api.events.mc.server.ServerStartEventHook;
import M6FGR.epic_api.events.mc.server.ServerStopEventHook;
import M6FGR.epic_api.events.mc.server.ServerTickEventHook;
import jdk.jfr.Experimental;
import yesman.epicfight.api.event.EventHook;
import yesman.epicfight.api.utils.side.LogicalSide;

@Experimental
public class MinecraftEventHooks {

    public static class Server {
        public static final EventHook<ServerStartEventHook.Pre> SERVER_START_PRE = EventHook.createSidedEventHook(LogicalSide.SERVER);
        public static final EventHook<ServerStartEventHook.Post> SERVER_START_POST = EventHook.createSidedEventHook(LogicalSide.SERVER);
        public static final EventHook<ServerStopEventHook> SERVER_STOP = EventHook.createSidedEventHook(LogicalSide.SERVER);
        public static final EventHook<ServerTickEventHook> SERVER_TICK = EventHook.createSidedEventHook(LogicalSide.SERVER);
    }

    public static class Player {
        public static final EventHook<PlayerTickEventHook.Pre> TICK_PRE = EventHook.createEventHook();
        public static final EventHook<PlayerTickEventHook.Post> TICK_POST = EventHook.createEventHook();
        public static final EventHook<PlayerJoinEventHook.Client> JOIN_CLIENT = EventHook.createSidedEventHook(LogicalSide.CLIENT);
        public static final EventHook<PlayerJoinEventHook.Server> JOIN_SERVER = EventHook.createSidedEventHook(LogicalSide.SERVER);
    }

    public static class Client {
        public static final EventHook<WorldLoadEventHook.Initialize> WORLD_INIT = EventHook.createEventHook();
        public static final EventHook<WorldLoadEventHook.Join> WORLD_JOIN = EventHook.createEventHook();
        public static final EventHook<WorldLoadEventHook.Create> WORLD_CREATE = EventHook.createEventHook();
    }

    public static class Entity {
        public static final EventHook<EntitySummonEventHook.Client> SUMMON_CLIENT = EventHook.createSidedEventHook(LogicalSide.CLIENT);
        public static final EventHook<EntitySummonEventHook.Server> SUMMON_SERVER = EventHook.createSidedEventHook(LogicalSide.SERVER);
    }
    
}
