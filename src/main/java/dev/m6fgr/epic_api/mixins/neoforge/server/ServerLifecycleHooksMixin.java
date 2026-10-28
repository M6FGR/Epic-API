package dev.m6fgr.epic_api.mixins.neoforge.server;

import dev.m6fgr.epic_api.events.mc.server.ServerInitializeDispatchEvent;
import dev.m6fgr.epic_api.events.mc.server.ServerStopDispatchEvent;
import net.minecraft.server.MinecraftServer;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLifecycleHooks.class)
public class ServerLifecycleHooksMixin {

    @Inject(
            at = @At(value = "INVOKE", target = "Lnet/neoforged/neoforge/common/util/LogicalSidedProvider;setServer(Ljava/util/function/Supplier;)V"),
            method = "handleServerAboutToStart",
            remap = false
    )
    private static void injectEventHookPre(MinecraftServer server, CallbackInfo ci) {
        ServerInitializeDispatchEvent.Pre serverInitPre = new ServerInitializeDispatchEvent.Pre(server);
        serverInitPre.post();
    }
    @Inject(
            at = @At(value = "HEAD"),
            method = "handleServerStarted",
            remap = false
    )
    private static void injectEventHookPost(MinecraftServer server, CallbackInfo ci) {
        ServerInitializeDispatchEvent.Post serverInitPost = new ServerInitializeDispatchEvent.Post(server);
        serverInitPost.post();
    }

    @Inject(
            at = @At("HEAD"),
            method = "handleServerStopped",
            remap = false
    )
    private static void injectEventHookStop(MinecraftServer server, CallbackInfo callbackInfo) {
        ServerStopDispatchEvent stopEvent = new ServerStopDispatchEvent(server);
        stopEvent.post();
    }

}
