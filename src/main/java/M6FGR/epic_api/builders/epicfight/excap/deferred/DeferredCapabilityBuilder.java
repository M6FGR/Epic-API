package M6FGR.epic_api.builders.epicfight.excap.deferred;

import M6FGR.epic_api.main.EpicAPI;
import com.google.common.collect.Maps;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import org.jetbrains.annotations.ApiStatus.Experimental;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.LivingMotion;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.collider.Collider;
import yesman.epicfight.api.collider.MultiOBBCollider;
import yesman.epicfight.api.ex_cap.data.Moveset;
import yesman.epicfight.registry.deferred.holders.DeferredConditional;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.guard.GuardSkill.BlockType;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.capabilities.item.Style;
import yesman.epicfight.world.capabilities.item.WeaponCapability;
import yesman.epicfight.world.capabilities.item.WeaponCategory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Experimental
public class DeferredCapabilityBuilder {
    // heavy attack maps
    private static final Map<WeaponCategory, Map<Style, List<AnimationAccessor<? extends AttackAnimation>>>> globalHeavyCombos = Maps.newHashMap();
    private final Map<Style, List<AnimationAccessor<? extends AttackAnimation>>> localHeavyComboMap = Maps.newHashMap();

    // counter-attack maps
    private static final Map<WeaponCategory, AnimationAccessor<? extends AttackAnimation>> globalNormalCounters = Maps.newHashMap();
    private static final Map<WeaponCategory, List<AnimationAccessor<? extends AttackAnimation>>> globalParryCounters = Maps.newHashMap();
    private final List<AnimationAccessor<? extends AttackAnimation>> localNormalCounters = new ArrayList<>();
    private final List<AnimationAccessor<? extends AttackAnimation>> localParryCounters = new ArrayList<>();

    protected final Moveset.Builder moveSetBuilder;
    protected final WeaponCapability.Builder weaponCapabilityBuilder;

    protected Collider currentCollider;
    protected WeaponCategory currentCategory;
    protected Holder<SoundEvent> currentSwingSound;
    protected Holder<SoundEvent> currentHitSound;
    protected @Nullable Holder<Skill> currentPassive;
    protected @Nullable Holder<Skill> currentInnate;
    protected boolean currentHoldableInOffHand;
    protected Object[] currentMotionsPair;
    protected AnimationAccessor<? extends AttackAnimation>[] currentCombo;
    protected Style currentStyle = CapabilityItem.Styles.COMMON;
    protected ResourceLocation currentMoveSetID;
    protected ResourceLocation mainMoveSetID;
    protected Object[] currentPresetProperties;

    private DeferredCapabilityBuilder() {
        this.moveSetBuilder = Moveset.builder();
        this.weaponCapabilityBuilder = WeaponCapability.builder();
    }

    public static DeferredCapabilityBuilder newBuilder() {
        return new DeferredCapabilityBuilder();
    }

    @SafeVarargs
    public final DeferredCapabilityBuilder newMoveSet(
            Style style, ResourceLocation moveSetID, WeaponCategory category, Collider collider,
            DeferredConditional condition, Holder<SoundEvent> swingSound, Holder<SoundEvent> hitSound,
            Holder<ParticleType<?>> hitParticleType, boolean holdableInOffHand,
            @Nullable Holder<Skill> passiveSkill,
            @Nullable Holder<Skill> innateSkill,
            AnimationAccessor<? extends AttackAnimation>... animations
    ) {
        this.currentCombo = animations;
        this.currentInnate = innateSkill;
        this.currentPassive = passiveSkill;
        this.currentStyle = style;
        this.currentCategory = category;
        this.currentSwingSound = swingSound;
        this.currentHitSound = hitSound;
        this.currentHoldableInOffHand = holdableInOffHand;
        this.currentCollider = collider;
        this.mainMoveSetID = moveSetID;
        this.currentMoveSetID = moveSetID;

        this.weaponCapabilityBuilder
                .addConditionals(condition)
                .category(category)
                .collider(collider)
                .swingSound(swingSound)
                .hitParticle(hitParticleType)
                .addMoveset(style, this.moveSetBuilder)
                .canBePlacedOffhand(holdableInOffHand);

        this.moveSetBuilder.addComboAttacks(animations);

        if (innateSkill != null) {
            this.moveSetBuilder.addInnateSkill((itemStack, playerPatch) -> innateSkill.value());
        }
        if (passiveSkill != null) {
            this.moveSetBuilder.setPassiveSkill(passiveSkill);
        }
        return this;
    }

    @SafeVarargs
    public final DeferredCapabilityBuilder newHeavyCombo(Style style, AnimationAccessor<? extends AttackAnimation>... heavyCombo) {
        this.localHeavyComboMap.put(style, List.of(heavyCombo));
        return this;
    }

    @SafeVarargs
    public final DeferredCapabilityBuilder newHeavyCombo(AnimationAccessor<? extends AttackAnimation>... heavyCombo) {
        this.localHeavyComboMap.put(this.currentStyle, List.of(heavyCombo));
        return this;
    }

    public DeferredCapabilityBuilder addTag(ResourceLocation id) {
        this.weaponCapabilityBuilder.addTag(id);
        return this;
    }

    public DeferredCapabilityBuilder addLivingMotion(LivingMotion motion, AnimationAccessor<? extends StaticAnimation> animation) {
        this.moveSetBuilder.addLivingMotionModifier(motion, animation);
        return this;
    }

    public DeferredCapabilityBuilder addLivingMotionPairs(Object... pairs) {
        for (int i = 0; i < pairs.length; i += 2) {
            LivingMotion motion = (LivingMotion) pairs[i];
            @SuppressWarnings("unchecked")
            AnimationAccessor<? extends StaticAnimation> animation = (AnimationAccessor<? extends StaticAnimation>) pairs[i + 1];
            if (animation != null) {
                this.addLivingMotion(motion, animation);
                EpicAPI.debugIfDevSide("Registered animation pairs of: [{}, {}] for moveset ID: [{}]", motion.toString(), animation.registryName().toString(), this.currentMoveSetID.toString());
            } else {
                EpicAPI.errIfDevSide("Animation for motion [{}] is not found for moveset: [{}]!, animation slot in pair: {}", motion.toString(), this.currentMoveSetID.toString(), i + 1);
            }
        }
        this.currentMotionsPair = pairs;
        return this;
    }

    public DeferredCapabilityBuilder addGuardAnimation(AnimationAccessor<? extends StaticAnimation> animation) {
        this.addLivingMotionPairs(LivingMotions.BLOCK, animation);
        return this;
    }

    public DeferredCapabilityBuilder addGuardHitAnimation(AnimationAccessor<? extends StaticAnimation> animation) {
        this.moveSetBuilder.addGuardAnimations(BlockType.GUARD, animation);
        return this;
    }

    public DeferredCapabilityBuilder addGuardBreakAnimation(AnimationAccessor<? extends StaticAnimation> animation) {
        this.moveSetBuilder.addGuardAnimations(BlockType.GUARD_BREAK, animation);
        return this;
    }

    @SafeVarargs
    public final DeferredCapabilityBuilder addCounterAttack(AnimationAccessor<? extends AttackAnimation>... animation) {
        this.localNormalCounters.addAll(Arrays.asList(animation));
        return this;
    }

    @SafeVarargs
    public final DeferredCapabilityBuilder addParryCounterAttack(AnimationAccessor<? extends AttackAnimation>... animation) {
        this.localParryCounters.addAll(Arrays.asList(animation));
        return this;
    }

    @SafeVarargs
    public final DeferredCapabilityBuilder addParryingAnimations(AnimationAccessor<? extends StaticAnimation>... animation) {
        this.moveSetBuilder.addGuardAnimations(BlockType.ADVANCED_GUARD, animation);
        return this;
    }

    @Internal
    public static @Nullable List<AnimationAccessor<? extends AttackAnimation>> getHeavyCombo(WeaponCategory category, Style style) {
        Map<Style, List<AnimationAccessor<? extends AttackAnimation>>> styleMap = globalHeavyCombos.get(category);
        return styleMap != null ? styleMap.get(style) : null;
    }

    // Cleaned up duplicate methods to provide exactly one distinct getter per type
    @Internal
    public static @Nullable AnimationAccessor<? extends AttackAnimation> getNormalCounterAttack(WeaponCategory category) {
        return globalNormalCounters.get(category);
    }

    @Internal
    public static @Nullable List<AnimationAccessor<? extends AttackAnimation>> getParryingCounterAttacks(WeaponCategory category) {
        return globalParryCounters.get(category);
    }

    @Internal
    public static void registerHeavyComboFromTag(ResourceLocation rl, CompoundTag rootTag) {
        String categoryStr = rootTag.getString("category");
        if (categoryStr.isEmpty()) {
            throw new IllegalArgumentException("Can't find weapon category for the heavy combo in: " + rl);
        }

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
                    globalHeavyCombos
                            .computeIfAbsent(category, k -> Maps.newHashMap())
                            .put(style, anims);
                }
            }
        }
    }

    /**
     * Parses the data configuration using the singular "normal_counter" layout.
     */
    @Internal
    public static void registerCounterFromTag(ResourceLocation rl, CompoundTag rootTag) {
        // grab the weapon category from the file first
        String categoryStr = rootTag.getString("category");
        if (categoryStr.isEmpty()) {
            throw new IllegalArgumentException("Can't find weapon category for the counter attack in: " + rl);
        }

        WeaponCategory category = WeaponCategory.ENUM_MANAGER.getOrThrow(categoryStr);

        if (rootTag.contains("normal_counter", 8)) { // 8 = StringTag
            String animId = rootTag.getString("normal_counter");
            AnimationAccessor<? extends AttackAnimation> animation = AnimationManager.byKey(animId);

            if (animation != null) {
                globalNormalCounters.put(category, animation);
            } else {
                EpicAPI.err("Missing normal counter animation [{}] for category [{}] in file [{}]", animId, categoryStr, rl);
            }
        }

        if (rootTag.contains("parry_counters", 9)) { // 9 = ListTag
            ListTag animList = rootTag.getList("parry_counters", 8); // 8 = String elements
            List<AnimationAccessor<? extends AttackAnimation>> counters = new ArrayList<>();

            for (int i = 0; i < animList.size(); ++i) {
                String animId = animList.getString(i);
                AnimationAccessor<? extends AttackAnimation> animation = AnimationManager.byKey(animId);

                if (animation != null) {
                    counters.add(animation);
                } else {
                    EpicAPI.err("Missing parry counter animation sequence index [{}] -> [{}] for category [{}] in file [{}]", i, animId, categoryStr, rl);
                }
            }

            if (!counters.isEmpty()) {
                globalParryCounters.put(category, counters);
            }
        }
    }

    public Moveset.Builder buildMoveSet() {
        if (this.currentCategory != null && !this.localHeavyComboMap.isEmpty()) {
            Map<Style, List<AnimationAccessor<? extends AttackAnimation>>> categoryMap =
                    globalHeavyCombos.computeIfAbsent(this.currentCategory, k -> Maps.newHashMap());

            categoryMap.putAll(this.localHeavyComboMap);
        }

        if (this.currentCategory != null) {
            if (!this.localNormalCounters.isEmpty()) {
                // If populated via standard code registration builder, save the first entry element
                globalNormalCounters.put(this.currentCategory, this.localNormalCounters.getFirst());
            }
            if (!this.localParryCounters.isEmpty()) {
                globalParryCounters.put(this.currentCategory, new ArrayList<>(this.localParryCounters));
            }
        }

        EpicAPI.debugIfDevSide("Registered moveset: [{}], with combo: {}", this.currentMoveSetID, this.currentCombo);
        return this.moveSetBuilder;
    }

    public WeaponCapability.Builder buildWeapon() {
        this.currentPresetProperties = new Object[]{
                this.currentCategory,
                this.currentCollider instanceof MultiOBBCollider multiOBBCollider ? multiOBBCollider.toString() : this.currentCollider.toString(),
                this.currentInnate,
                this.currentPassive,
        };
        EpicAPI.debugIfDevSide("Registered preset with properties: {}", this.currentPresetProperties);
        return this.weaponCapabilityBuilder;
    }
}