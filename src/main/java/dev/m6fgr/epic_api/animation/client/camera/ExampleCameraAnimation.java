package dev.m6fgr.epic_api.animation.client.camera;

import dev.m6fgr.epic_api.animation.client.CameraAnimatorAPI;
import dev.m6fgr.epic_api.events.epic_api.registry.CameraAnimationRegistryDispatchableEvent;
import dev.m6fgr.epic_api.main.EpicAPI;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber(value = Dist.CLIENT, modid = EpicAPI.MOD_ID)
public final class ExampleCameraAnimation {

    public static final List<CameraAnimatorAPI.CamAnim> CAM_ANIM_LIST = new ArrayList<>();

    public static CameraAnimatorAPI.CamAnim register(String name, ResourceLocation path) {

        CameraAnimatorAPI.CamAnim anim = new CameraAnimatorAPI.CamAnim(
                ResourceLocation.fromNamespaceAndPath(EpicAPI.MOD_ID, name),
                path);

        for (CameraAnimatorAPI.CamAnim existing : CAM_ANIM_LIST) {
            if (existing.animationName().equals(anim.animationName())) {
                throw new IllegalStateException("CamAnimation named " + anim.animationName() + " already registered.");
            }
        }

        CAM_ANIM_LIST.add(anim);

        return anim;
    }

    public static final CameraAnimatorAPI.CamAnim TEST2 = register(
            "test2",
            ResourceLocation.fromNamespaceAndPath(EpicAPI.MOD_ID, "camera/testcamera.json"));

    public static final CameraAnimatorAPI.CamAnim TEST = new CameraAnimatorAPI.CamAnim(ResourceLocation.fromNamespaceAndPath(EpicAPI.MOD_ID,"testcamera_2"));


    @SubscribeEvent
    public static void registerExampleCamAnim(CameraAnimationRegistryDispatchableEvent event){
        CAM_ANIM_LIST.forEach(camAnim -> event.register(TEST2));
        event.register(TEST);
    }

}
