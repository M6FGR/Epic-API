package dev.m6fgr.epic_api.mixins.minecraft.client;

import dev.m6fgr.epic_api.events.mc.client.ClientInitializeDispatchEvent;
import dev.m6fgr.epic_api.events.mc.client.ClientInitializeDispatchEvent.Post;
import dev.m6fgr.epic_api.events.mc.client.ClientInitializeDispatchEvent.Pre;
import dev.m6fgr.epic_api.events.mc.client.ClientTickDispatchEvent;
import dev.m6fgr.epic_api.events.mc.client.WorldLoadDispatchEvent;
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
        WorldLoadDispatchEvent.Initialize worldInitEventHook = new WorldLoadDispatchEvent.Initialize(levelStorage, packRepository, worldStem, newWorld);
        worldInitEventHook.post();
    }

    @Inject(
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/packs/repository/PackRepository;reload()V"),
            method = "<init>",
            remap = false
    )

    public void injectCIDEPre(GameConfig gameConfig, CallbackInfo ci) {
        ClientInitializeDispatchEvent.Pre clientInitDEPre = new Pre((Minecraft) (Object) this);
        clientInitDEPre.post();
    }

    @Inject(
            at = @At("TAIL"),
            method = "<init>",
            remap = false
    )

    public void injectCIDEPost(GameConfig gameConfig, CallbackInfo ci) {
        ClientInitializeDispatchEvent.Post clientInitDEPost = new Post((Minecraft) (Object) this);
        clientInitDEPost.post();
    }

    @Inject(
            at = @At(value = "INVOKE", target = "Lnet/neoforged/neoforge/client/ClientHooks;fireClientTickPre()V"),
            method = "tick",
            remap = false
    )

    public void injectCTDE(CallbackInfo ci) {
        ClientTickDispatchEvent clientTickDE = new ClientTickDispatchEvent((Minecraft) (Object) this);
        clientTickDE.post();
    }


}
