package M6FGR.epic_api.api.registry;

import M6FGR.epic_api.main.EpicAPI;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import org.jetbrains.annotations.ApiStatus.Experimental;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.LivingMotion;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.collider.Collider;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.particle.HitParticleType;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.capabilities.item.Style;
import yesman.epicfight.world.capabilities.item.WeaponCapability;
import yesman.epicfight.world.capabilities.item.WeaponCategory;
import yesman.epicfight.world.capabilities.provider.ExtraEntryProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Experimental
public class WeaponCapabilityRegistry {
    // 1. GLOBAL STORAGE FOR SKILLS
    private static final Map<WeaponCategory, Map<Style, List<AnimationAccessor<? extends AttackAnimation>>>> GLOBAL_HEAVY_COMBOS = Maps.newHashMap();

    private final WeaponCapability.Builder builder;
    protected Collider currentCollider;
    protected WeaponCategory currentCategory;
    protected SoundEvent currentSwingSound;
    protected SoundEvent currentHitSound;
    protected @Nullable Skill currentPassive;
    protected @Nullable Skill currentInnate;
    protected boolean currentHoldableInOffHand;
    protected Object[] currentMotionsPair;
    protected AnimationAccessor<? extends AttackAnimation>[] currentCombo;
    private Style currentStyle = CapabilityItem.Styles.COMMON;

    // 2. LOCAL STORAGE FOR BUILDER
    private final Map<Style, List<AnimationAccessor<? extends AttackAnimation>>> localHeavyComboMap = Maps.newHashMap();

    private WeaponCapabilityRegistry() {
        this.builder = WeaponCapability.builder();
    }

    public static WeaponCapabilityRegistry builder() {
        return new WeaponCapabilityRegistry();
    }

    // Static accessor for HeavyAttack skill
    public static @Nullable List<AnimationAccessor<? extends AttackAnimation>> getHeavyCombo(WeaponCategory category, Style style) {
        Map<Style, List<AnimationAccessor<? extends AttackAnimation>>> styleMap = GLOBAL_HEAVY_COMBOS.get(category);
        return styleMap != null ? styleMap.get(style) : null;
    }

    // --- Heavy Combo Methods ---

    @SafeVarargs
    public final WeaponCapabilityRegistry withNewHeavyCombo(Style style, AnimationAccessor<? extends AttackAnimation>... heavyCombo) {
        this.localHeavyComboMap.put(style, List.of(heavyCombo));
        return this;
    }

    @SafeVarargs
    public final WeaponCapabilityRegistry withNewHeavyCombo(AnimationAccessor<? extends AttackAnimation>... heavyCombo) {
        this.localHeavyComboMap.put(this.currentStyle, List.of(heavyCombo));
        return this;
    }

    public WeaponCapabilityRegistry registerHeavyComboFromTag(ResourceLocation rl, CompoundTag rootTag, @Nullable ExtraEntryProvider extraEntryProvider) {
        String categoryStr = rootTag.getString("category");
        if (categoryStr.isEmpty()) return this;

        WeaponCategory category = WeaponCategory.ENUM_MANAGER.getOrThrow(categoryStr);

        if (rootTag.contains("heavy_combos")) {
            CompoundTag heavyCombosTag = rootTag.getCompound("heavy_combos");

            for (String styleKey : heavyCombosTag.getAllKeys()) {
                Style style = Style.ENUM_MANAGER.getOrThrow(styleKey);

                ListTag animList = heavyCombosTag.getList(styleKey, 8); // 8 is String type
                List<AnimationAccessor<? extends AttackAnimation>> anims = new ArrayList<>();

                for (int i = 0; i < animList.size(); ++i) {
                    String animId = animList.getString(i);
                    AnimationAccessor<? extends AttackAnimation> animation = (extraEntryProvider == null)
                            ? AnimationManager.byKey(animId)
                            : extraEntryProvider.getExtraOrBuiltInAnimation(animId);

                    if (animation != null) {
                        anims.add(animation);
                    } else {
                        EpicAPI.LOGGER.warn("Missing animation {} in {}", animId, rl);
                    }
                }

                if (!anims.isEmpty()) {
                    GLOBAL_HEAVY_COMBOS
                            .computeIfAbsent(category, k -> Maps.newHashMap())
                            .put(style, anims);
                }
            }
        }

        return this;
    }

    // --- Preset & Style Methods ---

    @SafeVarargs
    public final WeaponCapabilityRegistry newPreset(Style style, WeaponCategory category, Collider collider, SoundEvent swingSound, SoundEvent hitSound, HitParticleType hitParticleType, boolean holdableInOffHand, @Nullable Skill passiveSkill, @Nullable Skill innateSkill, AnimationAccessor<? extends AttackAnimation>... animations) {
        this.currentCombo = animations;
        this.currentInnate = innateSkill;
        this.currentPassive = passiveSkill;
        this.currentStyle = style;
        this.currentCategory = category;
        this.currentSwingSound = swingSound;
        this.currentHitSound = hitSound;
        this.currentHoldableInOffHand = holdableInOffHand;
        this.currentCollider = collider;
        this.builder
                .category(category)
                .collider(collider)
                .hitSound(hitSound)
                .swingSound(swingSound)
                .hitParticle(hitParticleType)
                .canBePlacedOffhand(holdableInOffHand)
                .passiveSkill(passiveSkill)
                .innateSkill(style, itemStack -> innateSkill)
                .weaponCombinationPredicator(entityPatch -> {
                    CapabilityItem cap = entityPatch.getHoldingItemCapability(InteractionHand.MAIN_HAND);
                    if (cap instanceof WeaponCapability weaponCap) {
                        return weaponCap.getStyle(entityPatch) == CapabilityItem.Styles.ONE_HAND;
                    }
                    return false;
                })
                .newStyleCombo(style, animations);
        return this;
    }

    @SafeVarargs
    public final WeaponCapabilityRegistry secondaryPreset(Style style, @Nullable Skill passiveSkill, @Nullable Skill innateSkill, AnimationAccessor<? extends AttackAnimation>... animations) {
        this.currentCombo = animations;
        this.currentInnate = innateSkill;
        this.currentPassive = passiveSkill;
        this.currentStyle = style;
        this.builder
                .category(currentCategory)
                .collider(currentCollider)
                .hitSound(currentHitSound)
                .swingSound(currentSwingSound)
                .canBePlacedOffhand(currentHoldableInOffHand)
                .passiveSkill(passiveSkill)
                .weaponCombinationPredicator(entityPatch -> {
                    CapabilityItem cap = entityPatch.getHoldingItemCapability(InteractionHand.MAIN_HAND);
                    if (cap instanceof WeaponCapability weaponCap) {
                        return weaponCap.getStyle(entityPatch) == CapabilityItem.Styles.ONE_HAND;
                    }
                    return false;
                })
                .innateSkill(style, itemStack -> innateSkill)
                .newStyleCombo(style, animations);
        return this;
    }

    public final WeaponCapabilityRegistry secondaryStyle(Style style, @Nullable Skill passiveSkill, @Nullable Skill innateSkill) {
        this.currentStyle = style;
        this.builder
                .category(currentCategory)
                .collider(currentCollider)
                .hitSound(currentHitSound)
                .swingSound(currentSwingSound)
                .canBePlacedOffhand(currentHoldableInOffHand)
                .passiveSkill(passiveSkill)
                .weaponCombinationPredicator(entityPatch -> {
                    CapabilityItem cap = entityPatch.getHoldingItemCapability(InteractionHand.MAIN_HAND);
                    if (cap instanceof WeaponCapability weaponCap) {
                        return weaponCap.getStyle(entityPatch) == CapabilityItem.Styles.ONE_HAND;
                    }
                    return false;
                })                .innateSkill(style, itemStack -> innateSkill)
                .newStyleCombo(style, currentCombo);
        this.forEachMotion(currentMotionsPair);
        return this;
    }

    public final WeaponCapabilityRegistry secondaryStyle(Style style, @Nullable Skill innateSkill) {
        this.currentStyle = style;
        this.builder.category(currentCategory).collider(currentCollider).hitSound(currentHitSound).swingSound(currentSwingSound)
                .canBePlacedOffhand(currentHoldableInOffHand).passiveSkill(currentPassive)
                .weaponCombinationPredicator(entityPatch -> currentHoldableInOffHand)
                .innateSkill(style, itemStack -> innateSkill)
                .newStyleCombo(style, currentCombo);
        return this;
    }

    // --- Shield Methods ---

    public WeaponCapabilityRegistry newShieldPreset(WeaponCategory category) {
        this.builder.constructor(BasicShieldCapability::new);
        this.builder.category(category);
        return this;
    }

    public WeaponCapabilityRegistry newShieldPreset(WeaponCategory category, Function<WeaponCapability.Builder, CapabilityItem> constructor) {
        this.builder.constructor(constructor);
        this.builder.category(category);
        return this;
    }

    public WeaponCapabilityRegistry withShieldBlockAnimation(AnimationAccessor<? extends StaticAnimation> animation) {
        this.builder.constructor(builder -> {
            BasicShieldCapability shield = new BasicShieldCapability(builder);
            shield.animation = animation;
            return shield;
        });
        return this;
    }


    public WeaponCapabilityRegistry withStyleConditions(Function<LivingEntityPatch<?>, Style> styleProvider) {
        this.builder.styleProvider(styleProvider);
        return this;
    }

    public WeaponCapabilityRegistry withOffHandPredict(Function<LivingEntityPatch<?>, Boolean> predicator) {
        this.builder.weaponCombinationPredicator(predicator);
        return this;
    }

    public WeaponCapabilityRegistry withLivingMotion(LivingMotion livingMotion, AnimationAccessor<? extends StaticAnimation> animation) {
        this.builder.livingMotionModifier(this.currentStyle, livingMotion, animation);
        return this;
    }

    public WeaponCapabilityRegistry withLivingMotion(Style style, LivingMotion livingMotion, AnimationAccessor<? extends StaticAnimation> animation) {
        this.builder.livingMotionModifier(style, livingMotion, animation);
        return this;
    }

    public WeaponCapabilityRegistry forEachMotion(Object... pairs) {
        if (pairs.length % 2 != 0) {
            throw new IllegalArgumentException("forEachMotion must have an even number of arguments (Motion/Animation pairs)!");
        }
        for (int i = 0; i < pairs.length; i += 2) {
            LivingMotion motion = (LivingMotion) pairs[i];
            @SuppressWarnings("unchecked")
            AnimationAccessor<? extends StaticAnimation> animation =
                    (AnimationAccessor<? extends StaticAnimation>) pairs[i + 1];

            this.withLivingMotion(motion, animation);
        }
        this.currentMotionsPair = pairs;
        return this;
    }
    public WeaponCapabilityRegistry forEachMotion(Style style, Object... pairs) {
        if (pairs.length % 2 != 0) {
            throw new IllegalArgumentException("forEachMotion must have an even number of arguments (Motion/Animation pairs)!");
        }
        for (int i = 0; i < pairs.length; i += 2) {
            LivingMotion motion = (LivingMotion) pairs[i];
            @SuppressWarnings("unchecked")
            AnimationAccessor<? extends StaticAnimation> animation =
                    (AnimationAccessor<? extends StaticAnimation>) pairs[i + 1];

            this.withLivingMotion(style, motion, animation);
        }
        this.currentMotionsPair = pairs;
        return this;
    }


    public WeaponCapabilityRegistry withReach(float reach) {
        this.builder.reach(reach);
        return this;
    }

    // --- The Build Method ---

    public WeaponCapability.Builder build() {
        if (this.currentCategory != null && !this.localHeavyComboMap.isEmpty()) {
            Map<Style, List<AnimationAccessor<? extends AttackAnimation>>> categoryMap =
                    GLOBAL_HEAVY_COMBOS.computeIfAbsent(this.currentCategory, k -> Maps.newHashMap());

            categoryMap.putAll(this.localHeavyComboMap);
        }
        return this.builder;
    }

    // --- Shield Capability Class ---

    static class BasicShieldCapability extends CapabilityItem {
        private AnimationAccessor<? extends StaticAnimation> animation = Animations.BIPED_BLOCK;

        public BasicShieldCapability(Builder<?> builder) {
            super(builder);
        }

        @Override
        public Map<LivingMotion, AnimationAccessor<? extends StaticAnimation>> getLivingMotionModifier(LivingEntityPatch<?> playerpatch, InteractionHand hand) {
            return ImmutableMap.of(LivingMotions.BLOCK_SHIELD, this.animation);
        }
    }
}