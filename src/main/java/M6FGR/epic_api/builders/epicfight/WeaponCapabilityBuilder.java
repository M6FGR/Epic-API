package M6FGR.epic_api.builders.epicfight;

import M6FGR.epic_api.main.EpicAPI;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import org.jetbrains.annotations.ApiStatus.Internal;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@SuppressWarnings({"removal", "deprecation"})
public class WeaponCapabilityBuilder {
    // 1. GLOBAL STORAGE FOR SKILLS
    private static final Map<WeaponCategory, Map<Style, List<AnimationAccessor<? extends AttackAnimation>>>> GLOBAL_HEAVY_COMBOS = Maps.newHashMap();
    private static final Map<WeaponCategory, Map<Style, AnimationAccessor<? extends AttackAnimation>>> GLOBAL_COUNTER_ATTACKS = Maps.newHashMap();
    private static final Map<WeaponCategory, Map<Style, List<AnimationAccessor<? extends AttackAnimation>>>> GLOBAL_PARRY_COUNTERS = Maps.newHashMap();

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
    private final Map<Style, AnimationAccessor<? extends AttackAnimation>> localCounterAttacksMap = Maps.newHashMap();
    private final Map<Style, List<AnimationAccessor<? extends AttackAnimation>>> localParryCountersMap = Maps.newHashMap();

    private WeaponCapabilityBuilder() {
        this.builder = WeaponCapability.builder();
    }

    public static WeaponCapabilityBuilder builder() {
        return new WeaponCapabilityBuilder();
    }

    // Static accessor for HeavyAttack skill
    @Internal
    public static @Nullable List<AnimationAccessor<? extends AttackAnimation>> getHeavyCombo(WeaponCategory category, Style style) {
        Map<Style, List<AnimationAccessor<? extends AttackAnimation>>> styleMap = GLOBAL_HEAVY_COMBOS.get(category);
        return styleMap != null ? styleMap.get(style) : null;
    }

    // Static accessor for CounterAttack skill - Now returns a single animation
    @Internal
    public static @Nullable AnimationAccessor<? extends AttackAnimation> getNormalCounter(WeaponCategory category, Style style) {
        Map<Style, AnimationAccessor<? extends AttackAnimation>> styleMap = GLOBAL_COUNTER_ATTACKS.get(category);
        return styleMap != null ? styleMap.get(style) : null;
    }

    // Static accessor for ParryCounter skill
    @Internal
    public static @Nullable List<AnimationAccessor<? extends AttackAnimation>> getParryCounters(WeaponCategory category, Style style) {
        Map<Style, List<AnimationAccessor<? extends AttackAnimation>>> styleMap = GLOBAL_PARRY_COUNTERS.get(category);
        return styleMap != null ? styleMap.get(style) : null;
    }

    // --- Heavy Combo Methods ---

    @SafeVarargs
    public final WeaponCapabilityBuilder withNewHeavyCombo(Style style, AnimationAccessor<? extends AttackAnimation>... heavyCombo) {
        this.localHeavyComboMap.put(style, List.of(heavyCombo));
        return this;
    }

    @SafeVarargs
    public final WeaponCapabilityBuilder withNewHeavyCombo(AnimationAccessor<? extends AttackAnimation>... heavyCombo) {
        this.localHeavyComboMap.put(this.currentStyle, List.of(heavyCombo));
        return this;
    }

    // --- Counter Attack Methods (Single Animation Constraints) ---

    public final WeaponCapabilityBuilder addCounterAttack(Style style, AnimationAccessor<? extends AttackAnimation> counterAttack) {
        this.localCounterAttacksMap.put(style, counterAttack);
        return this;
    }

    public final WeaponCapabilityBuilder addCounterAttack(AnimationAccessor<? extends AttackAnimation> counterAttack) {
        this.localCounterAttacksMap.put(this.currentStyle, counterAttack);
        return this;
    }

    // --- Parry Counter Methods ---

    @SafeVarargs
    public final WeaponCapabilityBuilder addParryCounterAttack(Style style, AnimationAccessor<? extends AttackAnimation>... parryCounter) {
        this.localParryCountersMap.put(style, List.of(parryCounter));
        return this;
    }

    @SafeVarargs
    public final WeaponCapabilityBuilder addParryCounterAttack(AnimationAccessor<? extends AttackAnimation>... parryCounter) {
        this.localParryCountersMap.put(this.currentStyle, List.of(parryCounter));
        return this;
    }

    // --- Data Serialization / Loading ---

    @Internal
    public static void registerHeavyComboFromTag(ResourceLocation rl, CompoundTag rootTag) {
        String categoryStr = rootTag.getString("category");
        if (categoryStr.isEmpty()) return;

        WeaponCategory category = WeaponCategory.ENUM_MANAGER.getOrThrow(categoryStr);

        if (rootTag.contains("heavy_combos")) {
            CompoundTag heavyCombosTag = rootTag.getCompound("heavy_combos");

            for (String styleKey : heavyCombosTag.getAllKeys()) {
                Style style = Style.ENUM_MANAGER.getOrThrow(styleKey);

                ListTag animList = heavyCombosTag.getList(styleKey, 8);
                List<AnimationAccessor<? extends AttackAnimation>> anims = new ArrayList<>();

                for (int i = 0; i < animList.size(); ++i) {
                    String animId = animList.getString(i);
                    AnimationAccessor<? extends AttackAnimation> animation = AnimationManager.byKey(animId);

                    if (animation != null) {
                        anims.add(animation);
                    } else {
                        EpicAPI.err("Missing animation {} in {}", animId, rl);
                    }
                }

                if (!anims.isEmpty()) {
                    GLOBAL_HEAVY_COMBOS
                            .computeIfAbsent(category, k -> Maps.newHashMap())
                            .put(style, anims);
                }
            }
        }
    }

    @Internal
    public static void registerCounterAttackFromTag(ResourceLocation rl, CompoundTag rootTag) {
        String categoryStr = rootTag.getString("category");
        if (categoryStr.isEmpty()) return;

        WeaponCategory category = WeaponCategory.ENUM_MANAGER.getOrThrow(categoryStr);

        if (rootTag.contains("counter_attack")) {
            CompoundTag counterTag = rootTag.getCompound("counter_attack");

            for (String styleKey : counterTag.getAllKeys()) {
                Style style = Style.ENUM_MANAGER.getOrThrow(styleKey);

                if (counterTag.contains(styleKey, 8)) {
                    String animId = counterTag.getString(styleKey);
                    AnimationAccessor<? extends AttackAnimation> animation = AnimationManager.byKey(animId);

                    if (animation != null) {
                        GLOBAL_COUNTER_ATTACKS
                                .computeIfAbsent(category, k -> Maps.newHashMap())
                                .put(style, animation);
                    } else {
                        EpicAPI.warn("Missing counter attack animation {} in {}", animId, rl);
                    }
                }
            }
        }
    }

    @Internal
    public static void registerParryCounterFromTag(ResourceLocation rl, CompoundTag rootTag) {
        String categoryStr = rootTag.getString("category");
        if (categoryStr.isEmpty()) return;

        WeaponCategory category = WeaponCategory.ENUM_MANAGER.getOrThrow(categoryStr);

        if (rootTag.contains("parry_counters")) {
            CompoundTag parryTag = rootTag.getCompound("parry_counters");

            for (String styleKey : parryTag.getAllKeys()) {
                Style style = Style.ENUM_MANAGER.getOrThrow(styleKey);
                ListTag animList = parryTag.getList(styleKey, 8);
                List<AnimationAccessor<? extends AttackAnimation>> anims = new ArrayList<>();

                for (int i = 0; i < animList.size(); ++i) {
                    String animId = animList.getString(i);
                    AnimationAccessor<? extends AttackAnimation> animation = AnimationManager.byKey(animId);

                    if (animation != null) {
                        anims.add(animation);
                    } else {
                        EpicAPI.warn("Missing parry counter animation {} in {}", animId, rl);
                    }
                }

                if (!anims.isEmpty()) {
                    GLOBAL_PARRY_COUNTERS
                            .computeIfAbsent(category, k -> Maps.newHashMap())
                            .put(style, anims);
                }
            }
        }
    }

    // --- Preset & Style Methods ---

    @SafeVarargs
    public final WeaponCapabilityBuilder newPreset(Style style, WeaponCategory category, Collider collider, SoundEvent swingSound, SoundEvent hitSound, HitParticleType hitParticleType, boolean holdableInOffHand, @Nullable Skill passiveSkill, @Nullable Skill innateSkill, AnimationAccessor<? extends AttackAnimation>... animations) {
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
    public final WeaponCapabilityBuilder secondaryPreset(Style style, @Nullable Skill passiveSkill, @Nullable Skill innateSkill, AnimationAccessor<? extends AttackAnimation>... animations) {
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

    public WeaponCapabilityBuilder secondaryStyle(Style style, @Nullable Skill passiveSkill, @Nullable Skill innateSkill) {
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

    public WeaponCapabilityBuilder secondaryStyle(Style style, @Nullable Skill innateSkill) {
        this.currentStyle = style;
        this.builder.category(currentCategory).collider(currentCollider).hitSound(currentHitSound).swingSound(currentSwingSound)
                .canBePlacedOffhand(currentHoldableInOffHand).passiveSkill(currentPassive)
                .weaponCombinationPredicator(entityPatch -> currentHoldableInOffHand)
                .innateSkill(style, itemStack -> innateSkill)
                .newStyleCombo(style, currentCombo);
        return this;
    }

    // --- Shield Methods ---

    public WeaponCapabilityBuilder newShieldPreset(WeaponCategory category) {
        this.builder.constructor(BasicShieldCapability::new);
        this.builder.category(category);
        return this;
    }

    public WeaponCapabilityBuilder newShieldPreset(WeaponCategory category, Function<CapabilityItem.Builder, CapabilityItem> constructor) {
        this.builder.constructor(constructor);
        this.builder.category(category);
        return this;
    }

    public WeaponCapabilityBuilder withShieldBlockAnimation(AnimationAccessor<? extends StaticAnimation> animation) {
        this.builder.constructor(builder -> {
            BasicShieldCapability shield = new BasicShieldCapability(builder);
            shield.animation = animation;
            return shield;
        });
        return this;
    }

    public WeaponCapabilityBuilder withStyleConditions(Function<LivingEntityPatch<?>, Style> styleProvider) {
        this.builder.styleProvider(styleProvider);
        return this;
    }

    public WeaponCapabilityBuilder withOffHandPredict(Function<LivingEntityPatch<?>, Boolean> predicator) {
        this.builder.weaponCombinationPredicator(predicator);
        return this;
    }

    public WeaponCapabilityBuilder withLivingMotion(LivingMotion livingMotion, AnimationAccessor<? extends StaticAnimation> animation) {
        this.builder.livingMotionModifier(this.currentStyle, livingMotion, animation);
        return this;
    }

    public WeaponCapabilityBuilder withLivingMotion(Style style, LivingMotion livingMotion, AnimationAccessor<? extends StaticAnimation> animation) {
        this.builder.livingMotionModifier(style, livingMotion, animation);
        return this;
    }

    public WeaponCapabilityBuilder forEachMotion(Object... pairs) {
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
    public WeaponCapabilityBuilder forEachMotion(Style style, Object... pairs) {
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

    public WeaponCapabilityBuilder withReach(float reach) {
        this.builder.reach(reach);
        return this;
    }

    // --- The Build Method ---

    public WeaponCapability.Builder build() {
        if (this.currentCategory != null) {
            // Heavy Combos
            if (!this.localHeavyComboMap.isEmpty()) {
                Map<Style, List<AnimationAccessor<? extends AttackAnimation>>> heavyMap =
                        GLOBAL_HEAVY_COMBOS.computeIfAbsent(this.currentCategory, k -> Maps.newHashMap());
                heavyMap.putAll(this.localHeavyComboMap);
            }

            // Counter Attacks (Flattens values strictly to single animation states)
            if (!this.localCounterAttacksMap.isEmpty()) {
                Map<Style, AnimationAccessor<? extends AttackAnimation>> counterMap =
                        GLOBAL_COUNTER_ATTACKS.computeIfAbsent(this.currentCategory, k -> Maps.newHashMap());
                counterMap.putAll(this.localCounterAttacksMap);
            }

            // Parry Counters
            if (!this.localParryCountersMap.isEmpty()) {
                Map<Style, List<AnimationAccessor<? extends AttackAnimation>>> parryMap =
                        GLOBAL_PARRY_COUNTERS.computeIfAbsent(this.currentCategory, k -> Maps.newHashMap());
                parryMap.putAll(this.localParryCountersMap);
            }
        }
        return this.builder;
    }

    // --- Shield Capability Class ---

    static class BasicShieldCapability extends CapabilityItem {
        private AnimationAccessor<? extends StaticAnimation> animation = Animations.BIPED_BLOCK;

        public BasicShieldCapability(Builder builder) {
            super(builder);
        }

        @Override
        public Map<LivingMotion, AnimationAccessor<? extends StaticAnimation>> getLivingMotionModifier(LivingEntityPatch<?> playerpatch, InteractionHand hand) {
            return ImmutableMap.of(LivingMotions.BLOCK_SHIELD, this.animation);
        }
    }
}