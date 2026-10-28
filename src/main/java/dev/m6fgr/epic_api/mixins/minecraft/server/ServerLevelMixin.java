package dev.m6fgr.epic_api.mixins.minecraft.server;

import dev.m6fgr.epic_api.events.mc.entity.EntitySummonDispatchEvent;
import dev.m6fgr.epic_api.events.mc.player.PlayerJoinDispatchEvent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerLevel.class)
public class ServerLevelMixin {
    // every event here is only posted if it was in a dedicated server
    @Shadow @Final private MinecraftServer server;

    @Inject(
            at = @At("HEAD"),
            method = "addEntity",
            remap = false
    )
    public void injectSummonEventHook(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (!this.server.isDedicatedServer()) return;
        EntitySummonDispatchEvent.Server serverSummonEH = new EntitySummonDispatchEvent.Server(entity, (ServerLevel) (Object) this);
        serverSummonEH.post();
    }

    @Inject(
            at = @At("HEAD"),
            method = "addPlayer",
            remap = false
    )
    public void injectPlayerJoinEventHook(ServerPlayer player, CallbackInfo ci) {
        if (!this.server.isDedicatedServer()) return;
        PlayerJoinDispatchEvent.Server serverPlayerJoinEH = new PlayerJoinDispatchEvent.Server(player, (ServerLevel) (Object) this);
        serverPlayerJoinEH.post();
    }

}
