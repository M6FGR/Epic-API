package M6FGR.epic_api.api.animation.types;

import net.minecraft.world.phys.Vec3;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.property.AnimationProperty;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.client.animation.Layer;
import yesman.epicfight.api.client.animation.property.ClientAnimationProperties;
import yesman.epicfight.api.client.animation.property.JointMaskEntry;
import yesman.epicfight.api.client.animation.property.TrailInfo;
import yesman.epicfight.api.collider.Collider;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.datastructure.ParameterizedHashMap;
import yesman.epicfight.api.utils.math.ValueModifier;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.registry.entries.EpicFightParticles;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.gamerule.EpicFightGameRules;

import javax.annotation.Nullable;
import java.util.*;

// An advanced version of ComboAttackAnimation-BasicAttackAnimation class, it alone can combine:
// DashAttackAnimation, AirAttackAnimation, AttackAnimation, enabling trail registry too without the need for .jsons for each animation
// It can calculate attack animations too by their Frame-Window, no need to time them in seconds.
public class SimpleAttackAnimation extends AttackAnimation {
    private final List<TrailDefinition> trailDefinitions = new ArrayList<>();
    // Seconds Calculation.
    public SimpleAttackAnimation(float transitionTime, float antic, float preDelay, float contact, float recovery, @Nullable Collider collider, Joint colliderJoint, AnimationManager.AnimationAccessor<? extends SimpleAttackAnimation> accessor, AssetAccessor<? extends Armature> armature) {
        super(transitionTime, antic, preDelay, contact, recovery, collider, colliderJoint, accessor, armature);
        this.addCommonProperties();
        this.addProperty(AnimationProperty.StaticAnimationProperty.POSE_MODIFIER, Animations.ReusableSources.COMBO_ATTACK_DIRECTION_MODIFIER);
    }

    // FPS Calculation.
    public SimpleAttackAnimation(float transitionTime, float antic, int preDelayFrame, int contactFrame, float recovery, @Nullable Collider collider, Joint colliderJoint, AnimationManager.AnimationAccessor<? extends SimpleAttackAnimation> accessor, AssetAccessor<? extends Armature> armature) {
        super(transitionTime, antic, (float) preDelayFrame / 60, (float) contactFrame / 60, recovery, collider, colliderJoint, accessor, armature);
        this.addCommonProperties();
        this.addProperty(AnimationProperty.StaticAnimationProperty.POSE_MODIFIER, Animations.ReusableSources.COMBO_ATTACK_DIRECTION_MODIFIER);
    }

    // shouldBend Should be disabled if the animation was a dash or an air slash,
    // Since it makes the chest rotate while attacking wherever the player is looking.

    public SimpleAttackAnimation(float transitionTime, float antic, float preDelay, float contact, float recovery, boolean shouldBend, @Nullable Collider collider, Joint colliderJoint, AnimationManager.AnimationAccessor<? extends SimpleAttackAnimation> accessor, AssetAccessor<? extends Armature> armature) {
        super(transitionTime, antic, preDelay, contact, recovery, collider, colliderJoint, accessor, armature);
        this.addCommonProperties();
        if (shouldBend) {
            this.addProperty(AnimationProperty.StaticAnimationProperty.POSE_MODIFIER, Animations.ReusableSources.COMBO_ATTACK_DIRECTION_MODIFIER);
        }
    }
    // You can modify the basis speed of the AttackAnimation using this constructor.
    public SimpleAttackAnimation(float transitionTime, float antic, float preDelay, float contact, float recovery, boolean shouldBend, float basisSpeed, @Nullable Collider collider, Joint colliderJoint, AnimationManager.AnimationAccessor<? extends SimpleAttackAnimation> accessor, AssetAccessor<? extends Armature> armature) {
        super(transitionTime, antic, preDelay, contact, recovery, collider, colliderJoint, accessor, armature);
        this.addCommonProperties();
        this.addProperty(AnimationProperty.AttackAnimationProperty.BASIS_ATTACK_SPEED, basisSpeed);
        if (shouldBend) {
            this.addProperty(AnimationProperty.StaticAnimationProperty.POSE_MODIFIER, Animations.ReusableSources.COMBO_ATTACK_DIRECTION_MODIFIER);
        }
    }

    // FPS Calculation.
    public SimpleAttackAnimation(float transitionTime, float antic, int preDelayFrame, int contactFrame, float recovery, String trailJoint, TrailColor trailColor, @Nullable Collider collider, Joint colliderJoint, AnimationManager.AnimationAccessor<? extends SimpleAttackAnimation> accessor, AssetAccessor<? extends Armature> armature) {
        super(transitionTime, antic, (float) preDelayFrame / 60, (float) contactFrame / 60, recovery, collider, colliderJoint, accessor, armature);
        this.addTrail(trailJoint, trailColor, (float) preDelayFrame / 60, (float) contactFrame / 60);
        this.addCommonProperties();
        this.addProperty(AnimationProperty.StaticAnimationProperty.POSE_MODIFIER, Animations.ReusableSources.COMBO_ATTACK_DIRECTION_MODIFIER);
    }

    public SimpleAttackAnimation(float transitionTime, AnimationManager.AnimationAccessor<? extends SimpleAttackAnimation> accessor, AssetAccessor<? extends Armature> armature, AttackAnimation.Phase... phases) {
        super(transitionTime, accessor, armature, phases);
        this.addCommonProperties();
    }

    public SimpleAttackAnimation addTrail(String joint, TrailColor color) {
        this.trailDefinitions.add(new TrailDefinition(joint, color, Arrays.stream(this.phases).iterator().next().preDelay, Arrays.stream(this.phases).iterator().next().contact, TrailPreset.SWORD));
        return this;
    }
    public SimpleAttackAnimation addTrail(String joint, TrailColor color, TrailPreset preset) {
        this.trailDefinitions.add(new TrailDefinition(joint, color, Arrays.stream(this.phases).iterator().next().preDelay, Arrays.stream(this.phases).iterator().next().contact, preset));
        return this;
    }
    public SimpleAttackAnimation addTrail(String joint, TrailColor color, float begin, float end, TrailPreset preset) {
        this.trailDefinitions.add(new TrailDefinition(joint, color, begin, end, preset));
        return this;
    }
    // FPS Calculation
    public SimpleAttackAnimation addTrail(String joint, TrailColor color, int beginFrame, int endFrane, TrailPreset preset) {
        this.trailDefinitions.add(new TrailDefinition(joint, color, (float) beginFrame / 60, (float) endFrane / 60, preset));
        return this;
    }

    public SimpleAttackAnimation addTrail(String joint, TrailColor color, float begin, float end) {
        this.trailDefinitions.add(new TrailDefinition(joint, color, begin, end, TrailPreset.SWORD));
        return this;
    }

    private void addCommonProperties() {
        this.addProperty(AnimationProperty.ActionAnimationProperty.CANCELABLE_MOVE, true);
        this.addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, false);
        this.addProperty(AnimationProperty.AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.1F);
    }

    protected void bindPhaseState(AttackAnimation.Phase phase) {;
        this.stateSpectrumBlueprint
                .newTimePair(phase.start, phase.preDelay)
                .addState(EntityState.PHASE_LEVEL, 1)

                .newTimePair(phase.start, phase.contact)
                .addState(EntityState.SKILL_EXECUTABLE, false)

                .newTimePair(phase.start, phase.recovery)
                .addState(EntityState.MOVEMENT_LOCKED, true)
                .addState(EntityState.UPDATE_LIVING_MOTION, false)
                .addState(EntityState.COMBO_ATTACKS_DOABLE, false)

                .newTimePair(phase.start, phase.end)
                .addState(EntityState.INACTION, true)

                .newTimePair(phase.preDelay, phase.contact)
                .addState(EntityState.ATTACKING, true)
                .addState(EntityState.PHASE_LEVEL, 2)

                .newTimePair(phase.contact, phase.end)
                .addState(EntityState.PHASE_LEVEL, 3)
                .addState(EntityState.TURNING_LOCKED, true);
    }

    @Override
    public void loadAnimation() {
        super.loadAnimation();

        if (!this.properties.containsKey(AnimationProperty.AttackAnimationProperty.BASIS_ATTACK_SPEED)) {
            float basisSpeed = Float.parseFloat(String.format(Locale.US, "%.2f", 1.0F / this.getTotalTime()));
            this.addProperty(AnimationProperty.AttackAnimationProperty.BASIS_ATTACK_SPEED, basisSpeed);
        }
        if (!this.properties.containsKey(ClientAnimationProperties.TRAIL_EFFECT)) {
            List<TrailInfo> activeTrails = new ArrayList<>();
            for (TrailDefinition def : this.trailDefinitions) {
                activeTrails.add(TrailInfo.builder()
                        .time(def.startDelay, def.endDelay)
                        .joint(def.joint())
                        .r(def.color().r).g(def.color().g).b(def.color().b)
                        .startPos(new Vec3(0, 0, 0))
                        .endPos(new Vec3(0, 0, -1.2))
                        .texture(TrailInfo.GENERIC_TRAIL_TEXTURE)
                        .type(EpicFightParticles.SWING_TRAIL.get())
                        .lifetime(4)
                        .interpolations(4)
                        .create());
                if (def.preset != null) {
                    activeTrails.add(TrailInfo.builder()
                            .time(def.startDelay, def.endDelay)
                            .joint(def.joint())
                            .r(def.color().r).g(def.color().g).b(def.color().b)
                            .startPos(def.preset.start)
                            .endPos(def.preset.end)
                            .texture(TrailInfo.GENERIC_TRAIL_TEXTURE)
                            .type(EpicFightParticles.SWING_TRAIL.get())
                            .lifetime(def.preset.lifetime)
                            .interpolations(def.preset.interpolates)
                            .create());
                }
            }
            if (!activeTrails.isEmpty()) {
                this.addProperty(ClientAnimationProperties.TRAIL_EFFECT, activeTrails);
            }
        }
    }

    public ParameterizedHashMap<EntityState.StateFactor<?>> getStatesMap(LivingEntityPatch<?> entitypatch, float time) {
        ParameterizedHashMap<EntityState.StateFactor<?>> stateMap = super.getStatesMap(entitypatch, time);
        if (!EpicFightGameRules.STIFF_COMBO_ATTACKS.getRuleValue(entitypatch.getOriginal().level())) {
            stateMap.put(EntityState.MOVEMENT_LOCKED, Optional.of(false));
            stateMap.put(EntityState.UPDATE_LIVING_MOTION, Optional.of(true));
        }

        return stateMap;
    }

    public Optional<JointMaskEntry> getJointMaskEntry(LivingEntityPatch<?> entitypatch, boolean useCurrentMotion) {
        return entitypatch.isLogicalClient() && entitypatch.getClientAnimator().getPriorityFor(this.getAccessor()) == Layer.Priority.HIGHEST ? Optional.of(JointMaskEntry.COMBO_ATTACK_MASK) : super.getJointMaskEntry(entitypatch, useCurrentMotion);
    }


    public SimpleAttackAnimation multiplyDamage(float multiplier) {
        this.addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(multiplier));
        return this;
    }
    public SimpleAttackAnimation multiplyImpact(float multiplier) {
        this.addProperty(AnimationProperty.AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(multiplier));
        return this;
    }

    record TrailDefinition(String joint, TrailColor color, float startDelay, float endDelay, TrailPreset preset) {}

    public enum TrailColor {
        WOOD(0.55f, 0.41f, 0.29f),
        STONE(0.6f, 0.6f, 0.6f),
        IRON(0.85f, 0.85f, 0.85f),
        GOLD(0.992f, 1.0f, 0.463f),
        DIAMOND(0.2f, 0.9f, 0.7f),
        NETHERITE(0.3f, 0.25f, 0.3f),
        RED(1.0F, 0.2F, 0.2F),
        GREEN(0.2F, 1.0F, 0.2F),
        BLUE(0.2F, 0.2F, 1.0F),
        WHITE(1.0f, 1.0f, 1.0f),
        EMPTY(0, 0, 0);

        float r, g, b;
        TrailColor(float r, float g, float b) {
            this.r = r;
            this.g = g;
            this.b = b;
        }

        // we target EMPTY instance
        public static TrailColor newColor(float r, float g, float b) {
            EMPTY.r = Math.min(r, 1);
            EMPTY.g = Math.min(g, 1);
            EMPTY.b = Math.min(b, 1);
            return EMPTY;
        }
    }
    public enum TrailPreset {
        AXE(new Vec3(0.0, -0.15, -0.35), new Vec3(0.0, -0.15, -0.7), 4, 6),
        DAGGER(new Vec3(0, 0, 0), new Vec3(0, 0, -0.6), 4, 4),
        SWORD(new Vec3(0, 0, 0), new Vec3(0, 0, -1.2), 4, 6),
        GREATSWORD(new Vec3(0, 0, -0.2), new Vec3(0, -0.24, -1.8), 6, 4),
        LONGSWORD(new Vec3(0, 0, 0.2), new Vec3(0, 0, -1.7), 4, 4),
        TACHI(new Vec3(0, 0, -0.2), new Vec3(0, 0.4, -1.75), 6, 4),
        SPEAR(new Vec3(0.0, 0.2, -0.9), new Vec3(0.0, 0.25, -2.0), 6, 4),
        EMPTY(null, null, 0, 0);

        Vec3 start, end;
        int lifetime, interpolates;
        TrailPreset(Vec3 start, Vec3 end, int lifetime, int interpolateCount) {
            this.start = start;
            this.end = end;
            this.lifetime = lifetime;
            this.interpolates = interpolateCount;
        }


        public static TrailPreset newPreset(Vec3 beginPos, Vec3 endPos, int lifetime, int interpolates) {
            // we target EMPTY instance too
            EMPTY.start = beginPos;
            EMPTY.end = endPos;
            EMPTY.lifetime = lifetime;
            EMPTY.interpolates = interpolates;
            return EMPTY;
        }
    }
}