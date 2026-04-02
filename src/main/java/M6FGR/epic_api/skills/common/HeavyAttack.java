package M6FGR.epic_api.skills.common;

import M6FGR.epic_api.api.math.MathUtil;
import M6FGR.epic_api.api.registry.WeaponCapabilityRegistry;
import M6FGR.epic_api.gameassets.EpicAPISkillDataKeys;
import M6FGR.epic_api.skills.EpicAPISkillCategories;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.AnimationVariables;
import yesman.epicfight.api.animation.property.AnimationProperty;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.event.EpicFightEventHooks;
import yesman.epicfight.api.event.types.player.ComboAttackEvent;
import yesman.epicfight.api.event.types.player.ModifyComboCounter;
import yesman.epicfight.api.event.types.player.SkillConsumeEvent;
import yesman.epicfight.api.exception.AssetLoadingException;
import yesman.epicfight.api.utils.math.ValueModifier;
import yesman.epicfight.network.EpicFightNetworkManager;
import yesman.epicfight.network.common.AbstractAnimatorControl;
import yesman.epicfight.network.server.SPAnimatorControl;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillBuilder;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataManager;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.capabilities.item.Style;
import yesman.epicfight.world.capabilities.item.WeaponCategory;
import yesman.epicfight.world.gamerule.EpicFightGameRules;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public class HeavyAttack extends Skill {
    public double MIN_ATTACK_Y = 0.1;
    private final Map<WeaponCategory, Map<Style, BiFunction<CapabilityItem, PlayerPatch<?>, List<AnimationManager.AnimationAccessor<? extends AttackAnimation>>>>> heavyMotionMap = new HashMap<>();
    public static final AnimationVariables.IndependentVariableKey<Boolean> HEAVY_COMBO = AnimationVariables.unsyncIndependent((animator) -> false, false);

    protected float heavyAttackConsumption;
    protected float heavyDashAttackConsumption;
    protected float heavyAirAttackConsumption;

    // Scaling Parameters
    protected float damageMultiplier;
    protected float impactMultiplier;
    protected float armorNegation;

    /**
     * Standard Builder Creator
     */
    public static SkillBuilder<?> createHeavyAttackBuilder() {
        return new SkillBuilder<>(HeavyAttack::new)
                .setCategory(EpicAPISkillCategories.HEAVY_ATTACK)
                .setActivateType(ActivateType.ONE_SHOT)
                .setResource(Resource.STAMINA);
    }

    public HeavyAttack(SkillBuilder<?> builder) {
        super(builder);
    }

    @Override
    public void loadDatapackParameters(CompoundTag parameters) {
        this.heavyAttackConsumption = parameters.getFloat("heavy_attack_consumption");
        this.heavyDashAttackConsumption = parameters.getFloat("heavy_dash_consumption");
        this.heavyAirAttackConsumption = parameters.getFloat("heavy_airslash_consumption");

        this.damageMultiplier = parameters.contains("damage_multiplier") ? parameters.getFloat("damage_multiplier") : 0.5f;
        this.impactMultiplier = parameters.contains("impact_multiplier") ? parameters.getFloat("impact_multiplier") : 1.0f;
        this.armorNegation = parameters.contains("armor_negation") ? parameters.getFloat("armor_negation") : 15.0f;
    }

    @Override
    public void executeOnServer(SkillContainer skillContainer, CompoundTag args) {
        ServerPlayerPatch executor = skillContainer.getServerExecutor();
        SkillConsumeEvent event = new SkillConsumeEvent(executor, this, this.resource, null);

        if (!executor.getEntityState().canBasicAttack()) {
            return;
        }

        if (!EpicFightEventHooks.Player.CONSUME_SKILL.postWithListener(event, event.getEntityPatch().getEventListener()).isCanceled()) {
            event.getResourceType().consumer.consume(skillContainer, executor, event.getAmount());
        }

        if (!EpicFightEventHooks.Player.COMBO_ATTACK.post(new ComboAttackEvent(executor)).isCanceled()) {
            CapabilityItem cap = executor.getHoldingItemCapability(InteractionHand.MAIN_HAND);
            AnimationManager.AnimationAccessor<? extends AttackAnimation> attackMotion = null;
            ServerPlayer player = executor.getOriginal();
            SkillDataManager dataManager = skillContainer.getDataManager();
            int comboCounter = dataManager.getDataValue(EpicAPISkillDataKeys.HEAVY_COUNTER);

            Vec3 blocksToDelta = MathUtil.cubicVec3ToDelta(player.getDeltaMovement());
            boolean dashAttack = player.isSprinting();
            boolean airAttack = !player.isInWater() && !player.onGround() && blocksToDelta.y() > this.MIN_ATTACK_Y && !player.getBlockStateOn().is(Block.byItem(Items.DIRT_PATH));

            List<AnimationManager.AnimationAccessor<? extends AttackAnimation>> combo = this.applyMotionsForMaps(executor, cap);

            if (combo == null || combo.isEmpty()) return;

            int comboSize = combo.size();
            if (comboSize < 3) {
                throw new AssetLoadingException("Heavy combo for " + cap.getWeaponCategory() + " needs at least 3 animations (Regular, Dash, Air).");
            }

            if (airAttack) {
                attackMotion = combo.get(comboSize - 1);
            } else if (dashAttack) {
                attackMotion = combo.get(comboSize - 2);
            } else {
                int maxRegularIdx = comboSize - 3;
                attackMotion = combo.get(Math.min(comboCounter, maxRegularIdx));
                comboCounter = (comboCounter + 1) % (maxRegularIdx + 1);
            }

            if (attackMotion != null && this.checkConsumption(executor, dashAttack, airAttack)) {
                // Apply scaling to the animation before playing
                if (attackMotion.get() instanceof AttackAnimation attackAnim) {
                    this.applyWeaponScaling(attackAnim);
                }

                setHeavyCounter(ModifyComboCounter.Causal.ANOTHER_ACTION_ANIMATION, executor, skillContainer, attackMotion, comboCounter);
                executor.getAnimator().playAnimation(attackMotion, 0.0F);
                executor.getAnimator().getVariables().put(HEAVY_COMBO, attackMotion, true);

                boolean stiffAttack = EpicFightGameRules.STIFF_COMBO_ATTACKS.getRuleValue(player.level());
                SPAnimatorControl animatorControlPacket = getAnimatorControl(skillContainer, stiffAttack, attackMotion);
                EpicFightNetworkManager.sendToAllPlayerTrackingThisEntityWithSelf(animatorControlPacket, player);
            }
            executor.updateEntityState();
        }
    }


    private void applyWeaponScaling(AttackAnimation animation) {
        for (AttackAnimation.Phase phase : animation.phases) {
            // in case of the multiply number is below 1, we add, so it doesn't decrease the damage nor the impact
            phase.addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, this.damageMultiplier < 1 ? ValueModifier.adder(this.damageMultiplier) : ValueModifier.multiplier(this.damageMultiplier));
            phase.addProperty(AnimationProperty.AttackPhaseProperty.IMPACT_MODIFIER, this.impactMultiplier < 1 ? ValueModifier.adder(this.impactMultiplier) : ValueModifier.multiplier(this.damageMultiplier));
            phase.addProperty(AnimationProperty.AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.adder(this.armorNegation));
        }
    }

    @Override
    public void updateContainer(SkillContainer container) {
        container.runOnServer((serverPlayerPatch) -> {
            if (container.getExecutor().getTickSinceLastAction() > 16 && container.getDataManager().getDataValue(EpicAPISkillDataKeys.HEAVY_COUNTER) > 0) {
                setHeavyCounter(ModifyComboCounter.Causal.TIME_EXPIRED, serverPlayerPatch, container, null, 0);
            }
        });
    }

    @Nullable
    public List<AnimationManager.AnimationAccessor<? extends AttackAnimation>> applyMotionsForMaps(PlayerPatch<?> playerpatch, CapabilityItem itemCapability) {
        WeaponCategory currentCategory = itemCapability.getWeaponCategory();
        Style currentStyle = itemCapability.getStyle(playerpatch);

        List<AnimationManager.AnimationAccessor<? extends AttackAnimation>> dynamicCombo =
                WeaponCapabilityRegistry.getHeavyCombo(currentCategory, currentStyle);

        if (dynamicCombo != null) return dynamicCombo;

        Map<Style, BiFunction<CapabilityItem, PlayerPatch<?>, List<AnimationManager.AnimationAccessor<? extends AttackAnimation>>>> styleMap = this.heavyMotionMap.get(currentCategory);
        if (styleMap != null) {
            BiFunction<CapabilityItem, PlayerPatch<?>, List<AnimationManager.AnimationAccessor<? extends AttackAnimation>>> comboProvider = styleMap.get(currentStyle);
            if (comboProvider != null) return comboProvider.apply(itemCapability, playerpatch);
        }
        return null;
    }

    private static void setHeavyCounter(ModifyComboCounter.Causal reason, ServerPlayerPatch playerpatch, SkillContainer container, AnimationManager.AnimationAccessor<? extends StaticAnimation> causalAnimation, int value) {
        int prevValue = container.getDataManager().getDataValue(EpicAPISkillDataKeys.HEAVY_COUNTER);
        ModifyComboCounter comboResetEvent = new ModifyComboCounter(reason, playerpatch, causalAnimation, prevValue, value);
        EpicFightEventHooks.Player.MODIFY_COMBO_COUNTER.post(comboResetEvent);
        container.getDataManager().setData(EpicAPISkillDataKeys.HEAVY_COUNTER, comboResetEvent.getNextValue());
    }

    public boolean checkConsumption(PlayerPatch<?> playerPatch, boolean dash, boolean air) {
        float consumption = air ? this.heavyAirAttackConsumption : (dash ? this.heavyDashAttackConsumption : this.heavyAttackConsumption);
        return playerPatch.consumeForSkill(this, this.resource, consumption);
    }

    private static @NotNull SPAnimatorControl getAnimatorControl(SkillContainer skillContainer, boolean stiffAttack, AnimationManager.AnimationAccessor<? extends AttackAnimation> attackMotion) {
        return stiffAttack
                ? new SPAnimatorControl(AbstractAnimatorControl.Action.PLAY, attackMotion, skillContainer.getExecutor(), 0.0F)
                : new SPAnimatorControl(AbstractAnimatorControl.Action.PLAY_CLIENT, attackMotion, skillContainer.getExecutor(), 0.0F, AbstractAnimatorControl.Layer.COMPOSITE_LAYER, AbstractAnimatorControl.Priority.HIGHEST);
    }
}