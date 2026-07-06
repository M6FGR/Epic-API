package M6FGR.epic_api.events.player;

import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.entity.eventlistener.AbstractPlayerEvent;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener.EventType;

public class HeavyAttackEvent extends AbstractPlayerEvent<ServerPlayerPatch> {

    public static final EventType<HeavyAttackEvent> TYPE = new EventType<>(null);

    public HeavyAttackEvent(ServerPlayerPatch playerPatch, boolean cancelable) {
        super(playerPatch, cancelable);
    }
    public HeavyAttackEvent(ServerPlayerPatch playerPatch) {
        super(playerPatch, true);
    }
}
