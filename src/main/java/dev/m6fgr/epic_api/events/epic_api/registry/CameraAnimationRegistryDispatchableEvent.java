package dev.m6fgr.epic_api.events.epic_api.registry;

import dev.m6fgr.epic_api.animation.client.CameraAnimatorAPI;
import dev.m6fgr.epic_api.events.dispatch.IDispatchableEvent;
import dev.m6fgr.epic_api.events.dispatch.abstracts.DispatchableEvent;
import dev.m6fgr.epic_api.events.epic_api.EpicAPIDispatchableEvents;
import dev.m6fgr.epic_api.main.EpicAPI;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class CameraAnimationRegistryDispatchableEvent extends DispatchableEvent implements IDispatchableEvent<CameraAnimationRegistryDispatchableEvent> {
    public void register(CameraAnimatorAPI.CamAnim camAnim) {
        CameraAnimatorAPI.getInstance().registerAnimation(camAnim);
    }

    public void registerFromClass(Class<?> cls) {
        // Check if the field is static and matches or extends CamAnim
        for (Field field : cls.getDeclaredFields())
            if (Modifier.isStatic(field.getModifiers()) &&
                    CameraAnimatorAPI.CamAnim.class.isAssignableFrom(field.getType())) {

                try {
                    field.setAccessible(true);
                    Object value = field.get(null); // null because it's static

                    if (value instanceof CameraAnimatorAPI.CamAnim camAnim) {
                        this.register(camAnim);
                    }
                } catch (Exception e) {
                    EpicAPI.err("Failed to register animation from field [{}] in class [{}]: {}",
                            field.getName(), cls.getName(), e.getMessage());
                    e.printStackTrace();
                }
            }
    }

    @Override
    public CameraAnimationRegistryDispatchableEvent post() {
        return EpicAPIDispatchableEvents.Registry.CAMERA_ANIMATIONS.postEvent(this);
    }
}
