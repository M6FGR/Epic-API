package M6FGR.epic_api.mixins.minecraft.client;

import M6FGR.epic_api.events.mc.entity.EntitySummonEventHook;
import M6FGR.epic_api.events.mc.player.PlayerJoinEventHook;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientLevel.class)
public abstract class ClientLevelMixin {

    @Inject(
            at = @At("HEAD"),
            method = "addEntity",
            remap = false
    )
    public void injectJoinEventHook(Entity entity, CallbackInfo ci) {
        // we put it in an "if-else" block, so we avoid firing both events if the entity was an instance of a player
        if (entity instanceof Player player) {
            PlayerJoinEventHook.Client playerJoinClientEH = new PlayerJoinEventHook.Client(player, (ClientLevel) (Object) this);
            playerJoinClientEH.post();
        } else {
            EntitySummonEventHook.Client entityClientJoinEH = new EntitySummonEventHook.Client(entity, (ClientLevel) (Object) this);
            entityClientJoinEH.post();
        }
    }
}
