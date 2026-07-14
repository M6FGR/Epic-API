package M6FGR.epic_api.mixins.minecraft.server;

import M6FGR.epic_api.events.mc.server.ServerTickEventHook;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {
    @Inject(
            at = @At(value = "INVOKE", target = "Lnet/neoforged/neoforge/event/EventHooks;fireServerTickPre(Ljava/util/function/BooleanSupplier;Lnet/minecraft/server/MinecraftServer;)V"),
            method = "tickServer",
            remap = false
    )

    public void injectTickEventHook(BooleanSupplier hasTimeLeft, CallbackInfo ci) {
        ServerTickEventHook tickEventHook = new ServerTickEventHook((MinecraftServer) (Object) this, hasTimeLeft);
        tickEventHook.post();
    }
}
