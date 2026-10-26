package dev.m6fgr.epic_api.mixins.minecraft.client;

import dev.m6fgr.epic_api.events.mc.client.ClientInitializeDispatchableEvent;
import dev.m6fgr.epic_api.events.mc.client.ClientInitializeDispatchableEvent.Post;
import dev.m6fgr.epic_api.events.mc.client.ClientInitializeDispatchableEvent.Pre;
import dev.m6fgr.epic_api.events.mc.client.ClientTickDispatchableEvent;
import dev.m6fgr.epic_api.events.mc.client.WorldLoadDispatchableEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.main.GameConfig;
import net.minecraft.server.WorldStem;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.world.level.storage.LevelStorageSource.LevelStorageAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
    @Inject(
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/GameProfileCache;setUsesAuthentication(Z)V"),
            method = "doWorldLoad",
            remap = false
    )

    public void injectCWDE(LevelStorageAccess levelStorage, PackRepository packRepository, WorldStem worldStem, boolean newWorld, CallbackInfo ci) {
        WorldLoadDispatchableEvent.Initialize worldInitEventHook = new WorldLoadDispatchableEvent.Initialize(levelStorage, packRepository, worldStem, newWorld);
        worldInitEventHook.post();
    }

    @Inject(
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/packs/repository/PackRepository;reload()V"),
            method = "<init>",
            remap = false
    )

    public void injectCIDEPre(GameConfig gameConfig, CallbackInfo ci) {
        ClientInitializeDispatchableEvent.Pre clientInitDEPre = new Pre((Minecraft) (Object) this);
        clientInitDEPre.post();
    }

    @Inject(
            at = @At("TAIL"),
            method = "<init>",
            remap = false
    )

    public void injectCIDEPost(GameConfig gameConfig, CallbackInfo ci) {
        ClientInitializeDispatchableEvent.Post clientInitDEPost = new Post((Minecraft) (Object) this);
        clientInitDEPost.post();
    }

    @Inject(
            at = @At(value = "INVOKE", target = "Lnet/neoforged/neoforge/client/ClientHooks;fireClientTickPre()V"),
            method = "tick",
            remap = false
    )

    public void injectCTDE(CallbackInfo ci) {
        ClientTickDispatchableEvent clientTickDE = new ClientTickDispatchableEvent((Minecraft) (Object) this);
        clientTickDE.post();
    }


}
