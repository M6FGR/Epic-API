package dev.m6fgr.epic_api.animation.client.camera;

import dev.m6fgr.epic_api.animation.client.CameraAnimatorAPI;
import dev.m6fgr.epic_api.events.epic_api.registry.CameraAnimationRegistryDispatchableEvent;
import dev.m6fgr.epic_api.main.EpicAPI;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;
import yesman.epicfight.api.client.event.EpicFightClientEventHooks;

@EventBusSubscriber(modid = EpicAPI.MOD_ID, value = Dist.CLIENT)
public class CameraAnimationManager {

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Pre ignoredEvent) {
        CameraAnimatorAPI.getInstance().tick();
    }

    @SubscribeEvent
    public static void computeFOV(ViewportEvent.ComputeFov event) {
        if (CameraAnimatorAPI.getInstance().isPlaying()) {

            CameraAnimatorAPI.CamAnim current = CameraAnimatorAPI.getInstance().getCurrentCamAnim();

            if (current != null) {
                event.setFOV(CameraFOVHelper.focalLengthToFOV(10.3923f));
            }

        }

    }

    @SubscribeEvent
    public static void onCameraBuild(FMLClientSetupEvent evt) {
        EpicFightClientEventHooks.Camera.BUILD_TRANSFORM_PRE.registerContextAwareEvent((event, eventContext) -> {
            if (!CameraAnimatorAPI.getInstance().isPlaying()) return;
            // Apply Camera animation
            CameraAnimatorAPI.getInstance().applyToCamera(
                    event.getCamera(),
                    event.getPartialTick()
            );
            // Take full control of camera during animation
            event.setVanillaCameraSetupCanceled(true);

        });
        evt.enqueueWork(() -> {
            new CameraAnimationRegistryDispatchableEvent().post();
        });

    }




    public static class CameraFOVHelper {

        public enum SensorSize {

            FULL_FRAME(36.0F, 24.0F),
            APS_C(23.6F, 15.7F),
            APS_H(28.7F, 19.1F),
            MICRO_FOUR_THIRDS(17.3F, 13.0F),
            MEDIUM_FORMAT(44.0F, 33.0F);

            public final float width;
            public final float height;

            SensorSize(float width, float height) {
                this.width = width;
                this.height = height;
            }
        }

        static Minecraft mc = Minecraft.getInstance();

        private static double focalLengthToHorizontalFOV(double focalLengthMM, SensorSize sensor) {
            if (focalLengthMM <= 0) {
                EpicAPI.err("focalLength is lower than or equal to 0 : {}", focalLengthMM);
                return 70.0d; // Default fallback
            }

            double fovRadians = 2.0d * Math.atan(sensor.width / (2.0d * focalLengthMM));
            return Math.toDegrees(fovRadians);
        }


        public static double horizontalToVerticalFOV(double hFovDegrees, double aspectRatio) {
            double hFovRad = Math.toRadians(hFovDegrees);
            double vFovRad = 2.0d * Math.atan(Math.tan(hFovRad / 2.0d) / aspectRatio);
            return Math.toDegrees(vFovRad);
        }

        public static double focalLengthToFOV(double focalLengthMM, SensorSize sensor) {
            double hFov = focalLengthToHorizontalFOV(focalLengthMM, sensor);
            double aspectRatio = mc.getWindow().getWidth() / (double) mc.getWindow().getHeight();
            return horizontalToVerticalFOV(hFov, aspectRatio);
        }


        public static double focalLengthToFOV(double focalLengthMM) {
            return focalLengthToFOV(focalLengthMM, SensorSize.FULL_FRAME);
        }

    }


}