package M6FGR.epic_api.mixins.minecraft.client;

import M6FGR.epic_api.events.mc.client.WorldLoadEventHook;
import M6FGR.epic_api.events.mc.client.WorldLoadEventHook.Create;
import M6FGR.epic_api.events.mc.client.WorldLoadEventHook.Join;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.WorldOpenFlows;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.WorldStem;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.levelgen.WorldDimensions;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.storage.LevelStorageSource.LevelStorageAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Function;

@Mixin(WorldOpenFlows.class)
public abstract class WorldOpenFlowsMixin {

    @Inject(
            at = @At("HEAD"),
            method = "openWorldDoLoad",
            remap = false
    )

    public void injectEventHookJoin(LevelStorageAccess levelStorage, WorldStem worldStem, PackRepository packRepository, CallbackInfo ci) {
        WorldLoadEventHook.Join worldJoinEventHook = new WorldLoadEventHook.Join(levelStorage, packRepository, worldStem);
        worldJoinEventHook.post();
    }

    @Inject(
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;doWorldLoad(Lnet/minecraft/world/level/storage/LevelStorageSource$LevelStorageAccess;Lnet/minecraft/server/packs/repository/PackRepository;Lnet/minecraft/server/WorldStem;Z)V"),
            method = "createFreshLevel",
            remap = false
    )
    public void injectEventHookCreate(
            String levelName,
            LevelSettings levelSettings,
            WorldOptions worldOptions,
            Function<RegistryAccess, WorldDimensions> dimensionGetter,
            Screen lastScreen,
            CallbackInfo ci,
            @Local LevelStorageAccess levelstoragesource$levelstorageaccess,
            @Local PackRepository packrepository,
            @Local WorldStem worldstem
    ) {
        WorldLoadEventHook.Create worldCreateEventHook = new WorldLoadEventHook.Create(levelstoragesource$levelstorageaccess, packrepository, worldstem);
        worldCreateEventHook.post();
    }
}
