package dev.m6fgr.epic_api.events.subscribes;

import dev.m6fgr.epic_api.client.gui.InteractionAPI;
import dev.m6fgr.epic_api.main.EpicAPIMod;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderFrameEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

public class EpicAPISubscribeEvents {
    @EventBusSubscriber(modid = EpicAPIMod.MOD_ID, value = Dist.CLIENT)
    public static class Client {

        private static long lastFrameNanos = 0L;

        @SubscribeEvent
        public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
            if (event.getHand() == InteractionHand.MAIN_HAND && event.getLevel().isClientSide()) {
                InteractionAPI.getInstance().interactWithEntity(event.getEntity(), event.getTarget());
                event.setCanceled(true);
            }
        }

        @SubscribeEvent
        public static void onRenderFrame(RenderFrameEvent.Pre event) {
            InteractionAPI api = InteractionAPI.getInstance();
            if (!api.isInteracting()) {
                lastFrameNanos = 0L;
                return;
            }

            long now = System.nanoTime();
            if (lastFrameNanos == 0L) {
                lastFrameNanos = now;
                return;
            }

            float deltaSeconds = (float) ((now - lastFrameNanos) / 1_000_000_000.0);
            lastFrameNanos = now;

            api.updateInteractionLock(deltaSeconds);
        }

        @SubscribeEvent
        public static void onCameraAngles(ViewportEvent.ComputeCameraAngles event) {
            InteractionAPI api = InteractionAPI.getInstance();
            if (!api.isInteracting()) return;

            Entity target = api.getCurrentTarget();
            Player player = event.getRenderer().getMainCamera().getEntity() instanceof Player p ? p : null;
            if (player == null || target == null || !target.isAlive()) return;

            float partialTick = (float) event.getPartialTick();
            float interpolatedYaw = Mth.rotLerp(partialTick, player.yRotO, player.getYRot());
            float interpolatedPitch = Mth.lerp(partialTick, player.xRotO, player.getXRot());

            event.setYaw(interpolatedYaw);
            event.setPitch(interpolatedPitch);
        }
    }
}