package dev.m6fgr.epic_api.mixins.minecraft.client;

import dev.m6fgr.epic_api.utils.EnvironmentHelper;
import dev.m6fgr.epic_api.utils.EnvironmentHelper.ServerTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.server.Services;
import net.minecraft.server.WorldStem;
import net.minecraft.server.level.progress.ChunkProgressListenerFactory;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.world.level.storage.LevelStorageSource.LevelStorageAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = IntegratedServer.class, remap = false, priority = 1005)
public abstract class IntegratedServerMixin {

    @Shadow public abstract boolean isPublished();

    @Inject(
            at = @At("TAIL"),
            method = "<init>",
            remap = false
    )

    private void setIntegratedServerType(Thread p_235248_, Minecraft p_235249_, LevelStorageAccess p_235250_, PackRepository p_235251_, WorldStem p_235252_, Services p_235253_, ChunkProgressListenerFactory p_235254_, CallbackInfo ci) {
        EnvironmentHelper.setServerAndType((IntegratedServer) (Object) this, this.isPublished() ? ServerTypes.INTEGRATED : ServerTypes.SINGLE_PLAYER);
    }
}
