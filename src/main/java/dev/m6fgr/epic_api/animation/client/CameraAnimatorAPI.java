package dev.m6fgr.epic_api.animation.client;

import com.google.gson.JsonObject;
import dev.m6fgr.epic_api.cls.maps.BasicHashMap;
import dev.m6fgr.epic_api.cls.maps.BasicMap;
import dev.m6fgr.epic_api.config.EpicAPIConfigs;
import dev.m6fgr.epic_api.main.EpicAPIMod;
import dev.m6fgr.epic_api.mixins.minecraft.client.CameraAccessor;
import net.minecraft.client.Camera;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import yesman.epicfight.api.animation.JointTransform;
import yesman.epicfight.api.animation.TransformSheet;
import yesman.epicfight.api.asset.JsonAssetLoader;
import yesman.epicfight.api.client.event.EpicFightClientEventHooks;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

import javax.annotation.Nullable;

public class CameraAnimatorAPI {
    private static final Logger LOGGER = LogManager.getLogger(CameraAnimatorAPI.class.getSimpleName());

    private static final CameraAnimatorAPI INSTANCE = new CameraAnimatorAPI();

    public static CameraAnimatorAPI getInstance() {
        return INSTANCE;
    }

    private final BasicMap<ResourceLocation, CameraAnimation> animations = new BasicHashMap<>();

    @Nullable
    private CameraAnimation currentAnimation;
    private ResourceLocation currentAnimationName;
    private CamAnim currentCamAnim;
    private Entity originEntity;

    private float currentTime;
    private boolean playing;
    private boolean looping;
    private boolean isMirrored;
    private boolean worldSpace;
    private boolean hasEventRegistered;
    private boolean paused;

    private boolean lockMousePanning;
    private float lockedYaw;

    private CameraAnimatorAPI() {
        this.currentTime = 0.0f;
        this.playing = false;
        this.looping = false;
        this.worldSpace = false;
    }

    public CamAnim getCurrentCamAnim() {
        return this.currentCamAnim;
    }

    public record CamAnim(ResourceLocation id, ResourceLocation jsonLocation) {
        /** derive JsonLocation From ResourceLocation.
         * <p>
         ** example: new CamAnim(Resourcelocation.parse(Mod_id:camera/camera_json))
         *
         **/
        public CamAnim(ResourceLocation id) {
            this(id, ResourceLocation.fromNamespaceAndPath(
                    id.getNamespace(), "camera/" + id.getPath() + ".json"));
        }

        public String animationName() {
            return id.getPath();
        }
    }

    public void registerAnimation(CamAnim camAnim) {
        try {
            Minecraft mc = Minecraft.getInstance();
            JsonAssetLoader loader = new JsonAssetLoader(mc.getResourceManager(), camAnim.jsonLocation());
            CameraAnimation animation = this.loadAnimation(loader);
            ResourceLocation name = camAnim.id();
            animations.put(name, animation);
            LOGGER.info("Registered camera animation: {}, ({}s) ", name, animation.getDuration());
        } catch (Exception e) {
            EpicAPIMod.err("Failed to load camera animation '" + camAnim.animationName() + "': " + e.getMessage());
        }
    }

    private CameraAnimation loadAnimation(JsonAssetLoader loader) {
        JsonObject rootJson = loader.getRootJson();
        JsonObject cameraObject = rootJson.getAsJsonObject("camera");
        if (cameraObject == null) throw new IllegalArgumentException("JSON must contain 'camera' object");

        JsonObject convertedJson = new JsonObject();
        convertedJson.add("time", cameraObject.get("time"));
        convertedJson.add("transform", cameraObject.get("transform"));
        convertedJson.addProperty("name", "camera");

        TransformSheet sheet = JsonAssetLoader.getTransformSheet(
                convertedJson, null, false, JsonAssetLoader.TransformFormat.ATTRIBUTES
        );
        return new CameraAnimation(sheet);
    }

    public void play(CamAnim anim, boolean loop, boolean lockMouse, boolean useWorldSpace, @Nullable Vec3 WorldSpaceOrigin){
        play(anim, loop,lockMouse,useWorldSpace, WorldSpaceOrigin,null, false);
    }

    // Remembered a photon bug where particle movements were going haywire cause of world-space local space conflict, figured
    // I need to implement a world space in this camera animator to fix big bug when it goes psycho after massive player tp in local space
    public void play(CamAnim anim, boolean loop, boolean lockMouse, boolean useWorldSpace, @Nullable Vec3 WorldSpaceOrigin, @Nullable Entity OriginEntity, boolean isMirrored) {

        if (this.playing && this.originEntity != null
                && OriginEntity != null && this.originEntity != OriginEntity) {
            LOGGER.warn("play() for entity {} ignored - already playing '{}' for entity {}",
                    OriginEntity, this.currentAnimationName, this.originEntity);
            return;
        }


        CameraType cameraType = Minecraft.getInstance().options.getCameraType();
        if (cameraType.isFirstPerson() || cameraType.isMirrored() || !EpicAPIConfigs.Common.shouldClipToHead()) return;

        if (!this.hasEventRegistered) {
            EpicFightClientEventHooks.Camera.BUILD_TRANSFORM_POST.registerEvent(event -> {
                try {
                    if (event.getCameraApi().isLockingOnTarget() && this.isPlaying()) {
                        event.getCameraApi().toggleLockOn();
                    }
                } catch (Exception e) {
                    LOGGER.error("LockOnError! : ", e);
                }
            }, "cameraAnimator", -100);
            this.hasEventRegistered = true;
        }


        CameraAnimation animation = animations.get(anim.id);
        if (animation == null) return;

        Minecraft mc = Minecraft.getInstance();

        this.originEntity = OriginEntity != null ? OriginEntity : mc.player;

        if (this.originEntity != null) {
            LivingEntityPatch<?> originPatch = EpicFightCapabilities.getEntityPatch(originEntity, LivingEntityPatch.class);
            if (originEntity != mc.player) {
                this.lockedYaw = originPatch.getYRot();
            }
            this.lockedYaw = originEntity.getYRot();
        }

        this.currentAnimation = animation;
        this.currentAnimationName = anim.id();
        this.currentCamAnim = anim;
        this.currentTime = 0.0f;
        this.playing = true;
        this.looping = loop;
        this.lockMousePanning = lockMouse;
        this.isMirrored = isMirrored;

        this.worldSpace = useWorldSpace;

        if (useWorldSpace && worldSpaceOrigin == null && originEntity == null) {
            LOGGER.warn("[CameraAnimator] useWorldSpace=true but BOTH origin and originEntity are null — " +
                    "falling back to local player. This is a bug at the call site.");
        }

        if (useWorldSpace && WorldSpaceOrigin == null && mc.player != null) {
            this.worldSpaceOrigin = originEntity.getEyePosition(0.0f);
        } else {
            this.worldSpaceOrigin = WorldSpaceOrigin;
        }

    }

    /// WorldSpace only ## It says mirrored but its actually works as a reverse ##
    public void playMirrored(CamAnim camAnim, boolean loop, Entity originEntity) {
        Vec3 worldSpaceOrigin = originEntity.getEyePosition();
        //Only Derive Origin Point from Origin but Drive rest of animation using localPlayer for worldSpace
        this.play(camAnim, loop, true, true, worldSpaceOrigin, Minecraft.getInstance().player, true);
    }


    public void stop() {
        this.playing = false;
        this.currentTime = 0.0f;
        this.currentAnimation = null;
        this.currentAnimationName = null;
        this.currentCamAnim = null;
        this.worldSpaceOrigin = null;
        this.originEntity = null;
    }


    public Iterable<ResourceLocation> getAnimationNames() {
        return this.animations.keySet();
    }


    public CameraAnimation getAnimationByName(ResourceLocation id) {
        return this.animations.get(id);
    }

    private boolean gameHasBeenPaused;

    public void tick() {
        if (!this.playing || this.currentAnimation == null) return;

        // pause handle when singleplayer
        Minecraft mc = Minecraft.getInstance();
        boolean gamePaused = mc.isSingleplayer()
                && mc.screen != null
                && mc.screen.isPauseScreen();

        if (gamePaused || this.paused) {
            this.gameHasBeenPaused = true;
            return;
        } else if (this.gameHasBeenPaused) {
            this.gameHasBeenPaused = false;
        }

        this.currentTime += 0.05f;

        if (this.currentTime >= this.currentAnimation.getDuration()) {
            if (this.looping) {
                this.currentTime = this.currentTime % this.currentAnimation.getDuration();
            } else {
                this.playing = false;
                this.currentTime = this.currentAnimation.getDuration();
            }
        }
    }

    public void applyToCamera(Camera camera, float partialTick) {
        // we stop if the config is set to false
        if (!this.playing || this.currentAnimation == null || !EpicAPIConfigs.Common.isCamAnimationsEnabled()) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        float animTime = currentTime + (partialTick / 20.0f);
        CameraTransform anim = getTransformAtTime(animTime);

        float baseYaw = this.lockMousePanning ? this.lockedYaw : this.originEntity.getViewYRot(partialTick);

        // Inverse X and Z positions to account for Blender to MC conversion
        Vector3f animOffset = new Vector3f(-anim.location.x, anim.location.y, -anim.location.z);

        if (this.isMirrored) { //Reverse Positions for Mirrored mode
            animOffset.x = -animOffset.x;
            animOffset.z = -animOffset.z;
        }

        // Rotate offset by base yaw
        Quaternionf yawRot = new Quaternionf().rotateY((float) Math.toRadians(-baseYaw));
        animOffset = yawRot.transform(animOffset);

        // get Desired Local/World Space params
        Vector3f desiredPos = this.getDesiredPos(partialTick, mc, animOffset);

        Quaternionf rotation = anim.rotation;

        // Convert rotation to Euler angles
        Vector3f euler = rotation.getEulerAnglesYXZ(new Vector3f());
        float animYaw = (float) Math.toDegrees(euler.y);
        float animPitch = (float) Math.toDegrees(euler.x);
        float animRoll = (float) Math.toDegrees(euler.z);

        if (this.isMirrored) {
            animYaw += 180f;
        }

        // NaN safety if something bad might happen
        if (Float.isNaN(animYaw) || Float.isNaN(animPitch) || Float.isNaN(animRoll)) {
            animYaw = this.lastAppliedYaw;
            animPitch = this.lastAppliedPitch;
            animRoll = this.lastAppliedRoll;
        } else {
            this.lastAppliedYaw = animYaw;
            this.lastAppliedPitch = animPitch;
            this.lastAppliedRoll = animRoll;
        }

        float finalYaw = baseYaw + animYaw;

        // Apply to camera
        this.applyCameraTransform(camera, desiredPos, finalYaw, animPitch, animRoll);

    }

    private @NotNull Vector3f getDesiredPos(float partialTick, Minecraft mc, Vector3f animOffset) {
        Vec3 basePos;
        if (worldSpace) {
            if (worldSpaceOrigin != null) {
                basePos = worldSpaceOrigin;
            } else {
                assert mc.player != null;
                basePos = mc.player.getEyePosition(0.01f);
            }
        } else {
            assert mc.player != null;
            basePos = mc.player.getEyePosition(partialTick);
        }

        return new Vector3f(
                (float) basePos.x + animOffset.x,
                (float) basePos.y + animOffset.y,
                (float) basePos.z + animOffset.z
        );
    }

    private float lastAppliedYaw = 0f, lastAppliedPitch = 0f, lastAppliedRoll = 0f;
    private Vec3 worldSpaceOrigin = null; // Important - Set a god reference world Space point before launch or else fallbacks

    private void applyCameraTransform(Camera camera, Vector3f position, float yaw, float pitch, float roll) {
        CameraAccessor accessor = (CameraAccessor) camera;
        accessor.invokeSetPosition(position.x, position.y, position.z);
        accessor.invokeSetRotation(yaw, pitch, roll);
    }

    private CameraTransform getTransformAtTime(float time) {
        if (currentAnimation == null) return new CameraTransform(new Vector3f(), new Quaternionf());
        JointTransform transform = currentAnimation.sheet.getInterpolatedTransform(time);
        Vec3f efTranslation = transform.translation();
        Quaternionf efRotation = transform.rotation();
        return new CameraTransform(
                new Vector3f(efTranslation.x, efTranslation.y, efTranslation.z),
                new Quaternionf(efRotation.x, efRotation.y, efRotation.z, efRotation.w)
        );
    }

    // Getters
    public void pause() {
        this.paused = true;
    }

    public void resume() {
        this.paused = false;
    }

    public boolean isPlaying() {
        return this.playing;
    }

    public boolean isPaused() {
        return this.paused;
    }


    @Nullable public ResourceLocation getCurrentAnimationName() {
        return this.currentAnimationName;
    }

    public boolean isMirrored() {
        return this.isMirrored;
    }

    public float getCurrentTime() {
        return this.currentTime;
    }

    public boolean hasAnimation(ResourceLocation name) {
        return animations.containsKey(name); }

    public boolean isMousePanningLocked() {
        return lockMousePanning;
    }

    public void setLockMousePanning(boolean lock) {
        this.lockMousePanning = lock;
    }

    private static class CameraAnimation {
        private final TransformSheet sheet;
        private final float duration;
        public CameraAnimation(TransformSheet sheet) {
            this.sheet = sheet;
            this.duration = sheet.maxFrameTime();
        }
        public float getDuration() {
            return this.duration;
        }
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
                EpicAPIMod.err("focalLength is lower than or equal to 0 : {}", focalLengthMM);
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



    public record CameraTransform(Vector3f location, Quaternionf rotation) {}
}