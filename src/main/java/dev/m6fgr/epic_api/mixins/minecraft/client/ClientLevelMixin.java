package dev.m6fgr.epic_api.mixins.minecraft.client;

import dev.m6fgr.epic_api.events.mc.entity.EntitySummonDispatchableEvent;
import dev.m6fgr.epic_api.events.mc.player.PlayerJoinDispatchableEvent;
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
            PlayerJoinDispatchableEvent.Client playerJoinClientEH = new PlayerJoinDispatchableEvent.Client(player, (ClientLevel) (Object) this);
            playerJoinClientEH.post();
        } else {
            EntitySummonDispatchableEvent.Client entitySummonClientEH = new EntitySummonDispatchableEvent.Client(entity, (ClientLevel) (Object) this);
            entitySummonClientEH.post();
        }
    }
}
