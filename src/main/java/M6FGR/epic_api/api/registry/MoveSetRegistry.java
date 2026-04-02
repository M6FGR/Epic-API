package M6FGR.epic_api.api.registry;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.LivingMotion;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.collider.Collider;
import yesman.epicfight.api.ex_cap.modules.core.data.*;
import yesman.epicfight.api.ex_cap.modules.core.provider.ProviderConditional;
import yesman.epicfight.particle.HitParticleType;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.world.capabilities.item.Style;
import yesman.epicfight.world.capabilities.item.WeaponCapability;
import yesman.epicfight.world.capabilities.item.WeaponCategory;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MoveSetRegistry {
    protected MoveSetEntry moveSetEntry;
    protected BuilderEntry builderEntry;
    protected ExCapDataEntry dataEntry;

    protected ResourceLocation motherID;
    protected MoveSet.MoveSetBuilder motherBuilder;
    protected ExCapData.Builder exCapData;
    protected WeaponCapability.Builder weaponCapability;

    protected MoveSet.MoveSetBuilder lastActiveBuilder;
    protected Style lastActiveStyle;
    protected ResourceLocation lastActiveID;

    // This stores all motions defined so far so children can inherit them
    protected final Map<LivingMotion, AnimationManager.AnimationAccessor<? extends StaticAnimation>> persistentMotions = new HashMap<>();

    protected WeaponCategory currentCategory;
    protected AnimationManager.AnimationAccessor<? extends AttackAnimation>[] currentCombo;
    protected Skill currentPassive;

    private MoveSetRegistry() {
        this.motherBuilder = MoveSet.builder();
        this.exCapData = ExCapData.builder();
        this.weaponCapability = WeaponCapability.builder();
    }

    public static MoveSetRegistry builder() {
        return new MoveSetRegistry();
    }

    private void injectConditional(ConditionalEntry condition) {
        try {
            ProviderConditional built = condition.builder().build();
            Field field = exCapData.getClass().getDeclaredField("conditionals");
            field.setAccessible(true);
            @SuppressWarnings("unchecked")
            List<ProviderConditional> list = (List<ProviderConditional>) field.get(exCapData);
            list.add(built);
        } catch (Exception e) {
            try { this.exCapData.addConditional(condition.id()); } catch (Exception ignored) {}
        }
    }

    private void finalizePreviousStyle() {
        if (lastActiveBuilder != null && lastActiveStyle != null && lastActiveID != null) {
            this.weaponCapability.addMoveSet(lastActiveStyle, lastActiveBuilder);
            this.exCapData.addMoveset(lastActiveStyle, lastActiveID);
        }
    }

    private void applyInheritance(MoveSet.MoveSetBuilder builder, Style style) {
        persistentMotions.forEach((motion, anim) -> {
            builder.addLivingMotionModifier(motion, anim);
            this.weaponCapability.livingMotionModifier(style, motion, anim);
        });
    }

    @SafeVarargs
    public final MoveSetRegistry newMoveSet(Style style, ResourceLocation id, WeaponCategory category, Collider collider, SoundEvent swingSound, SoundEvent hitSound, HitParticleType hitParticleType, boolean offhand, @Nullable Skill pass, @Nullable Skill innate, ConditionalEntry cond, AnimationManager.AnimationAccessor<? extends AttackAnimation>... anims) {
        this.motherID = id;
        this.currentCombo = anims;
        this.currentCategory = category;
        this.currentPassive = pass;

        this.motherBuilder.identifier(id).addComboAttacks(anims).setPassiveSkill(pass).addInnateSkill((stack, patch) -> innate);
        this.weaponCapability.category(category).canBePlacedOffhand(offhand).collider(collider).swingSound(swingSound).hitSound(hitSound).hitParticle(hitParticleType).addConditionals(cond.builder().build());

        injectConditional(cond);
        this.lastActiveStyle = style;
        this.lastActiveBuilder = this.motherBuilder;
        this.lastActiveID = id;
        return this;
    }

    public MoveSetRegistry withChild(Style style, ResourceLocation id, @Nullable Skill innate, ConditionalEntry cond) {
        finalizePreviousStyle();

        MoveSet.MoveSetBuilder child = MoveSet.builder().identifier(id).parent(this.motherID).addComboAttacks(this.currentCombo).setPassiveSkill(this.currentPassive).addInnateSkill((stack, patch) -> innate);

        this.applyInheritance(child, style);

        this.weaponCapability.addConditionals(cond.builder().build());
        injectConditional(cond);

        this.lastActiveStyle = style;
        this.lastActiveBuilder = child;
        this.lastActiveID = id;
        return this;
    }

    @SafeVarargs
    public final MoveSetRegistry withChildMoveSet(Style style, ResourceLocation id, @Nullable Skill innate, @Nullable Skill pass, ConditionalEntry cond, AnimationManager.AnimationAccessor<? extends AttackAnimation>... combo) {
        finalizePreviousStyle();

        MoveSet.MoveSetBuilder child = MoveSet.builder().identifier(id).parent(this.motherID).addInnateSkill((stack, patch) -> innate).setPassiveSkill(pass).addComboAttacks(combo);

        this.applyInheritance(child, style);

        this.weaponCapability.addConditionals(cond.builder().build());
        injectConditional(cond);

        this.lastActiveStyle = style;
        this.lastActiveBuilder = child;
        this.lastActiveID = id;
        return this;
    }

    public MoveSetRegistry forEachMotion(Object... pairs) {
        if (pairs.length % 2 != 0) throw new IllegalArgumentException("Pairs required!");
        for (int i = 0; i < pairs.length; i += 2) {
            LivingMotion motion = (LivingMotion) pairs[i];
            @SuppressWarnings("unchecked")
            AnimationManager.AnimationAccessor<? extends StaticAnimation> anim = (AnimationManager.AnimationAccessor<? extends StaticAnimation>) pairs[i + 1];

            this.lastActiveBuilder.addLivingMotionModifier(motion, anim);
            this.weaponCapability.livingMotionModifier(this.lastActiveStyle, motion, anim);

            this.persistentMotions.put(motion, anim);
        }
        return this;
    }

    @SafeVarargs
    public final MoveSetRegistry withHeavyCombo(AnimationManager.AnimationAccessor<? extends AttackAnimation>... heavy) {
        if (this.lastActiveStyle != null && this.currentCategory != null) {
            WeaponCapabilityRegistry.registerHeavyCombo(this.currentCategory, this.lastActiveStyle, heavy);
        }
        return this;
    }
    @SafeVarargs
    public final MoveSetRegistry withHeavyCombo(Style style, AnimationManager.AnimationAccessor<? extends AttackAnimation>... heavy) {
        if (this.lastActiveStyle != null && this.currentCategory != null) {
            WeaponCapabilityRegistry.registerHeavyCombo(this.currentCategory, style, heavy);
        }
        return this;
    }

    public MoveSetEntry build(ResourceLocation registryID) {
        finalizePreviousStyle();
        this.dataEntry = new ExCapDataEntry(registryID, this.exCapData);
        this.builderEntry = new BuilderEntry(registryID, this.weaponCapability);
        this.moveSetEntry = new MoveSetEntry(this.motherID, this.motherBuilder);
        return this.moveSetEntry;
    }

    public WeaponCapability.Builder getWeaponCapability() { return this.weaponCapability; }
    public ExCapDataEntry getDataEntry() { return this.dataEntry; }
    public BuilderEntry getBuilderEntry() { return this.builderEntry; }
}