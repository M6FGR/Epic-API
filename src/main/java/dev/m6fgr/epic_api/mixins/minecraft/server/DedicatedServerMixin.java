package dev.m6fgr.epic_api.mixins.minecraft.server;

import dev.m6fgr.epic_api.utils.EnvironmentHelper;
import dev.m6fgr.epic_api.utils.EnvironmentHelper.ServerTypes;
import com.mojang.datafixers.DataFixer;
import net.minecraft.server.Services;
import net.minecraft.server.WorldStem;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.dedicated.DedicatedServerSettings;
import net.minecraft.server.level.progress.ChunkProgressListenerFactory;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.world.level.storage.LevelStorageSource.LevelStorageAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = DedicatedServer.class, remap = false, priority = 1005)
public class DedicatedServerMixin {
    @Inject(
            at = @At("TAIL"),
            method = "<init>",
            remap = false
    )
    private void injectServerType(Thread serverThread, LevelStorageAccess storageSource, PackRepository packRepository, WorldStem worldStem, DedicatedServerSettings settings, DataFixer fixerUpper, Services services, ChunkProgressListenerFactory p_214796_, CallbackInfo ci) {
        EnvironmentHelper.setServerAndType((DedicatedServer) (Object) this, ServerTypes.DEDICATED);
    }
}
