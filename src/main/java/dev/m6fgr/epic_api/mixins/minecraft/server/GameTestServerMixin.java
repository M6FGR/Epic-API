package dev.m6fgr.epic_api.mixins.minecraft.server;

import dev.m6fgr.epic_api.utils.EnvironmentHelper;
import dev.m6fgr.epic_api.utils.EnvironmentHelper.ServerTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestServer;
import net.minecraft.server.WorldStem;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.world.level.storage.LevelStorageSource.LevelStorageAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;

@Mixin(value = GameTestServer.class, remap = false, priority = 1005)
public class GameTestServerMixin {

    @Inject(
            at = @At("TAIL"),
            method = "<init>",
            remap = false
    )

    private void setGameTestServerType(Thread p_206597_, LevelStorageAccess p_206598_, PackRepository p_206599_, WorldStem p_206600_, Collection p_206601_, BlockPos p_206602_, CallbackInfo ci) {
        EnvironmentHelper.setServerAndType((GameTestServer) (Object) this, ServerTypes.GAME_TEST);
    }
}
