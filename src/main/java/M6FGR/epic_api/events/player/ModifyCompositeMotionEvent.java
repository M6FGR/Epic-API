package M6FGR.epic_api.events.player;

import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.event.IModBusEvent;
import yesman.epicfight.api.animation.LivingMotion;
import yesman.epicfight.client.world.capabilites.entitypatch.player.AbstractClientPlayerPatch;
import yesman.epicfight.world.entity.eventlistener.DetachablePlayerEvent;
import yesman.epicfight.world.entity.eventlistener.EventTrigger;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener.EventType;

public class ModifyCompositeMotionEvent extends Event implements IModBusEvent, DetachablePlayerEvent<AbstractClientPlayerPatch<?>> {

    public static final EventType<ModifyCompositeMotionEvent> TYPE = new EventType<>(LogicalSide.CLIENT);

    private final AbstractClientPlayerPatch<?> acpp;
    private LivingMotion compositeMotion;

    public ModifyCompositeMotionEvent(AbstractClientPlayerPatch<?> acpp, LivingMotion compositeMotion) {
        this.acpp = acpp;
        this.compositeMotion = compositeMotion;
    }


    @Override
    public AbstractClientPlayerPatch<?> getPlayerPatch() {
        return this.acpp;
    }

    public LivingMotion getCompositeMotion() {
        return this.compositeMotion;
    }

    public void setCompositeMotion(LivingMotion compositeMotion) {
        this.compositeMotion = compositeMotion;
    }
}
