package M6FGR.epic_api.mixins.epicfight.client;

import M6FGR.epic_api.events.player.ModifyCompositeMotionEvent;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yesman.epicfight.client.world.capabilites.entitypatch.player.AbstractClientPlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

@Mixin(value = AbstractClientPlayerPatch.class, remap = false, priority = 1001)
public abstract class AbstractClientPlayerPatchMixin<T extends AbstractClientPlayer> extends PlayerPatch<T> {


    @Inject(
            method = "updateMotion",
            at = @At(
                    value = "INVOKE",
                    target = "Lyesman/epicfight/world/entity/eventlistener/PlayerEventListener;triggerEvents(Lyesman/epicfight/world/entity/eventlistener/PlayerEventListener$EventType;Lyesman/epicfight/world/entity/eventlistener/DetachablePlayerEvent;)Z",
                    // target the second method for composite motions, not base
                    ordinal = 1
            ),
            remap = false
    )
    public void injectModifyMotionModBusEvent(boolean considerInaction, CallbackInfo ci) {
        ModifyCompositeMotionEvent modifyCompositeMotionEvent = new ModifyCompositeMotionEvent((AbstractClientPlayerPatch<?>) (Object) this, this.currentCompositeMotion);
        this.eventListeners.triggerEvents(ModifyCompositeMotionEvent.TYPE, modifyCompositeMotionEvent);
        MinecraftForge.EVENT_BUS.post(modifyCompositeMotionEvent);
    }
}
