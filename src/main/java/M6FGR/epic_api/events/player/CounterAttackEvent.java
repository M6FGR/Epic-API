package M6FGR.epic_api.events.player;

import M6FGR.epic_api.events.EpicAPIEventHooks;
import M6FGR.epic_api.events.IEventHook;
import yesman.epicfight.api.event.LivingEntityPatchEvent;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

public class CounterAttackEvent extends LivingEntityPatchEvent implements IEventHook<CounterAttackEvent> {
    public CounterAttackEvent(ServerPlayerPatch entityPatch) {
        super(entityPatch);
    }

    public ServerPlayerPatch getPlayerPatch() {
        return (ServerPlayerPatch) this.getEntityPatch();
    }

    @Override
    public CounterAttackEvent post() {
       return EpicAPIEventHooks.Player.COUNTER_ATTACK.post(this);
    }
}
