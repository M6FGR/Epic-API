package M6FGR.epic_api.mixins.minecraft.client;

import M6FGR.epic_api.events.mc.client.WorldLoadEventHook;
import net.minecraft.client.Minecraft;
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

    public void injectCWEventHook(LevelStorageAccess levelStorage, PackRepository packRepository, WorldStem worldStem, boolean newWorld, CallbackInfo ci) {
        WorldLoadEventHook.Initialize worldInitEventHook = new WorldLoadEventHook.Initialize(levelStorage, packRepository, worldStem, newWorld);
        worldInitEventHook.post();
    }


}
