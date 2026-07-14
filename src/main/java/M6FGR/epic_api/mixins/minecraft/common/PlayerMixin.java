package M6FGR.epic_api.mixins.minecraft.common;

import M6FGR.epic_api.events.mc.player.PlayerTickEventHook;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerMixin {
    @Inject(
            method = "tick",
            at = @At("HEAD"),
            remap = false
    )
    private void injectEventHookPre(CallbackInfo ci) {
        PlayerTickEventHook.Pre tickPre = new PlayerTickEventHook.Pre((Player) (Object) this);
        tickPre.post();
    }

    @Inject(
            at = @At(value = "INVOKE", target = "Lnet/neoforged/neoforge/event/EventHooks;firePlayerTickPost(Lnet/minecraft/world/entity/player/Player;)V"),
            method = "tick",
            remap = false
    )

    private void injectEventHookPost(CallbackInfo ci) {
        PlayerTickEventHook.Post tickPost = new PlayerTickEventHook.Post((Player) (Object) this);
        tickPost.post();
    }
}
