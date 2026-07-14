package M6FGR.epic_api.mixins.neoforge.server;

import M6FGR.epic_api.events.mc.server.ServerStartEventHook;
import M6FGR.epic_api.events.mc.server.ServerStopEventHook;
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
        ServerStartEventHook.Pre serverStartEvent = new ServerStartEventHook.Pre(server);
        serverStartEvent.post();
    }
    @Inject(
            at = @At(value = "HEAD"),
            method = "handleServerStarted",
            remap = false
    )
    private static void injectEventHookPost(MinecraftServer server, CallbackInfo ci) {
        ServerStartEventHook.Post serverStartPost = new ServerStartEventHook.Post(server);
        serverStartPost.post();
    }

    @Inject(
            at = @At("HEAD"),
            method = "handleServerStopped",
            remap = false
    )
    private static void injectEventHookStop(MinecraftServer server, CallbackInfo callbackInfo) {
        ServerStopEventHook stopEvent = new ServerStopEventHook(server);
        stopEvent.post();
    }

}
