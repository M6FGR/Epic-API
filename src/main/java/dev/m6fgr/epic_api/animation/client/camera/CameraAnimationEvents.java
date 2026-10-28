package dev.m6fgr.epic_api.animation.client.camera;

import dev.m6fgr.epic_api.animation.client.CameraAnimatorAPI;
import dev.m6fgr.epic_api.animation.client.CameraAnimatorAPI.CameraFOVHelper;
import dev.m6fgr.epic_api.events.epic_api.registry.CameraAnimationRegistryDispatchableEvent;
import dev.m6fgr.epic_api.main.EpicAPIMod;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;
import yesman.epicfight.api.client.event.EpicFightClientEventHooks;

@EventBusSubscriber(modid = EpicAPIMod.MOD_ID, value = Dist.CLIENT)
public class CameraAnimationEvents {

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Pre ignoredEvent) {
        CameraAnimatorAPI.getInstance().tick();
    }

    //@SubscribeEvent
    public static void computeFOV(ViewportEvent.ComputeFov event) {
        if (CameraAnimatorAPI.getInstance().isPlaying()) {
            CameraAnimatorAPI.CamAnim current = CameraAnimatorAPI.getInstance().getCurrentCamAnim();

            //FIXME: This is calculated Default FOV from blender, do you guys really think it should be default
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


}