package M6FGR.epic_api.events.player;

import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.entity.eventlistener.AbstractPlayerEvent;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener.EventType;

public class CounterAttackEvent extends AbstractPlayerEvent<ServerPlayerPatch> {

    public static final EventType<CounterAttackEvent> TYPE = new EventType<>(null);

    public CounterAttackEvent(ServerPlayerPatch playerPatch, boolean cancelable) {
        super(playerPatch, cancelable);
    }
    public CounterAttackEvent(ServerPlayerPatch playerPatch) {
        super(playerPatch, true);
    }
}
