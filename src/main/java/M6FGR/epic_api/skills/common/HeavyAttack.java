package M6FGR.epic_api.skills.common;

import M6FGR.epic_api.builders.epicfight.WeaponCapabilityBuilder;
import M6FGR.epic_api.gameassets.EpicAPISkillDataKeys;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
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
import yesman.epicfight.api.exception.AssetLoadingException;
import yesman.epicfight.api.utils.math.ValueModifier;
import yesman.epicfight.network.EpicFightNetworkManager;
import yesman.epicfight.network.common.AnimatorControlPacket.Action;
import yesman.epicfight.network.common.AnimatorControlPacket.Layer;
import yesman.epicfight.network.common.AnimatorControlPacket.Priority;
import yesman.epicfight.network.server.SPAnimatorControl;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillBuilder;
import yesman.epicfight.skill.SkillCategories;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataKey;
import yesman.epicfight.skill.SkillDataKeys;
import yesman.epicfight.skill.SkillDataManager;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.capabilities.item.Style;
import yesman.epicfight.world.capabilities.item.WeaponCategory;
import yesman.epicfight.world.entity.eventlistener.BasicAttackEvent;
import yesman.epicfight.world.entity.eventlistener.ComboCounterHandleEvent;
import yesman.epicfight.world.entity.eventlistener.ComboCounterHandleEvent.Causal;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener.EventType;
import yesman.epicfight.world.entity.eventlistener.SkillConsumeEvent;
import yesman.epicfight.world.gamerule.EpicFightGameRules;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiFunction;

public class HeavyAttack extends Skill {
    public double MIN_ATTACK_Y = 0.1;
    private final Map<WeaponCategory, Map<Style, BiFunction<CapabilityItem, PlayerPatch<?>, List<AnimationManager.AnimationAccessor<? extends AttackAnimation>>>>> heavyMotionMap = new HashMap<>();
    public static final AnimationVariables.IndependentAnimationVariableKey<Boolean> HEAVY_COMBO = AnimationVariables.independent((animator) -> false, false);
    private static UUID EVENT_UUID = UUID.fromString("7d2a9b1c-4e8f-43b2-a5d6-c9e0f1a2b3c4");
    protected float heavyAttackConsumption;
    protected float heavyDashAttackConsumption;
    protected float heavyAirAttackConsumption;

    // Scaling Parameters
    protected float damageMultiplier;
    protected float impactMultiplier;
    protected float armorNegation;



    @SuppressWarnings("unchecked")
    public static SkillBuilder<HeavyAttack> createHeavyAttackBuilder() {
        return new SkillBuilder()
                .setCategory(SkillCategories.BASIC_ATTACK)
                .setActivateType(ActivateType.ONE_SHOT)
                .setResource(Resource.NONE);
    }

    public HeavyAttack(SkillBuilder<?> builder) {
        super(builder);
    }

    @Override
    public void setParams(CompoundTag parameters) {
        // Attack consumptions
        this.heavyAttackConsumption = parameters.getFloat("heavy_attack_consumption");
        this.heavyDashAttackConsumption = parameters.getFloat("heavy_dash_consumption");
        this.heavyAirAttackConsumption = parameters.getFloat("heavy_airslash_consumption");
        // Attributes multipliers
        this.damageMultiplier = parameters.contains("damage_multiplier") ? parameters.getFloat("damage_multiplier") : 0.5f;
        this.impactMultiplier = parameters.contains("impact_multiplier") ? parameters.getFloat("impact_multiplier") : 1.0f;
        this.armorNegation = parameters.contains("armor_negation") ? parameters.getFloat("armor_negation") : 15.0f;
    }

    @Override
    public void executeOnServer(SkillContainer skillContainer, FriendlyByteBuf buf) {
        ServerPlayerPatch executor = skillContainer.getServerExecutor();
        SkillConsumeEvent event = new SkillConsumeEvent(executor, this, this.resource, buf);
        BasicAttackEvent attackEvent = new BasicAttackEvent(executor);
        executor.getEventListener().triggerEvents(EventType.SKILL_CONSUME_EVENT, event);
        if (!event.isCanceled()) {
            event.getResourceType().consumer.consume(skillContainer, executor, event.getAmount());
        }

        if (!attackEvent.isCanceled()) {
            CapabilityItem cap = executor.getHoldingItemCapability(InteractionHand.MAIN_HAND);
            AnimationManager.AnimationAccessor<? extends AttackAnimation> attackMotion;
            ServerPlayer player = executor.getOriginal();
            SkillDataManager dataManager = skillContainer.getDataManager();
            int comboCounter = dataManager.getDataValue(EpicAPISkillDataKeys.HEAVY_COUNTER.get());
            Vec3 blocksToDelta = this.deltaToBlocks(player.getDeltaMovement());
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
                this.applyWeaponScaling(attackMotion.get());

                setHeavyCounter(Causal.ANOTHER_ACTION_ANIMATION, executor, skillContainer, attackMotion, comboCounter);
                executor.getAnimator().playAnimation(attackMotion, 0.0F);
                executor.getAnimator().getVariables().put(HEAVY_COMBO, attackMotion, true);

                boolean stiffAttack = EpicFightGameRules.STIFF_COMBO_ATTACKS.getRuleValue(player.level());
                SPAnimatorControl animatorControlPacket = getAnimatorControl(skillContainer, stiffAttack, attackMotion);
                EpicFightNetworkManager.sendToAllPlayerTrackingThisEntityWithSelf(animatorControlPacket, player);
            }
            executor.updateEntityState();
        }
    }


    private Vec3 deltaToBlocks(Vec3 delta) {
        double dx = delta.x * 2.12453;
        double dz = delta.z * 2.12453;
        double dy = 0;
        if (delta.y > 0) {
            dy = Math.sqrt(delta.y * 0.16) + 0.02;
        } else if (delta.y < 0) {
            dy = delta.y * 0.1;
        }
        return new Vec3(dx, dy, dz);
    }


    private void applyWeaponScaling(AttackAnimation animation) {
        for (AttackAnimation.Phase phase : animation.phases) {
            // if the multiply number is below 1, we add, so it doesn't decrease the damage nor the impact
            phase.addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, this.damageMultiplier < 1 ? ValueModifier.adder(this.damageMultiplier) : ValueModifier.multiplier(this.damageMultiplier));
            phase.addProperty(AnimationProperty.AttackPhaseProperty.IMPACT_MODIFIER, this.impactMultiplier < 1 ? ValueModifier.adder(this.impactMultiplier) : ValueModifier.multiplier(this.impactMultiplier));
            phase.addProperty(AnimationProperty.AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.adder(this.armorNegation));
        }
    }

    @Override
    public void onInitiate(SkillContainer container) {
        container.getExecutor().getEventListener().addEventListener(EventType.ACTION_EVENT_SERVER, EVENT_UUID, (event) -> {
            int comboCounter = (Integer)container.getDataManager().getDataValue((SkillDataKey) SkillDataKeys.COMBO_COUNTER.get());
            setHeavyCounter(Causal.ANOTHER_ACTION_ANIMATION, event.getPlayerPatch(), container, event.getAnimation(), comboCounter);
        });
    }

    public void onRemoved(SkillContainer container) {
        container.getExecutor().getEventListener().removeListener(EventType.ACTION_EVENT_SERVER, EVENT_UUID);
    }

    @Nullable
    private List<AnimationManager.AnimationAccessor<? extends AttackAnimation>> applyMotionsForMaps(PlayerPatch<?> playerpatch, CapabilityItem itemCapability) {
        WeaponCategory currentCategory = itemCapability.getWeaponCategory();
        Style currentStyle = itemCapability.getStyle(playerpatch);

        List<AnimationManager.AnimationAccessor<? extends AttackAnimation>> dynamicCombo =
                WeaponCapabilityBuilder.getHeavyCombo(currentCategory, currentStyle);

        if (dynamicCombo != null) return dynamicCombo;

        Map<Style, BiFunction<CapabilityItem, PlayerPatch<?>, List<AnimationManager.AnimationAccessor<? extends AttackAnimation>>>> styleMap = this.heavyMotionMap.get(currentCategory);
        if (styleMap != null) {
            BiFunction<CapabilityItem, PlayerPatch<?>, List<AnimationManager.AnimationAccessor<? extends AttackAnimation>>> comboProvider = styleMap.get(currentStyle);
            if (comboProvider != null) return comboProvider.apply(itemCapability, playerpatch);
        }
        return null;
    }


    private static void setHeavyCounter(Causal reason, ServerPlayerPatch playerpatch, SkillContainer container, AnimationManager.AnimationAccessor<? extends StaticAnimation> causalAnimation, int value) {
        int prevValue = container.getDataManager().getDataValue(EpicAPISkillDataKeys.HEAVY_COUNTER.get());
        ComboCounterHandleEvent comboResetEvent = new ComboCounterHandleEvent(reason, playerpatch, causalAnimation, prevValue, value);
        container.getExecutor().getEventListener().triggerEvents(EventType.COMBO_COUNTER_HANDLE_EVENT, comboResetEvent);
        container.getDataManager().setData(EpicAPISkillDataKeys.HEAVY_COUNTER.get(), comboResetEvent.getNextValue());
    }

    public boolean checkConsumption(PlayerPatch<?> playerPatch, boolean dash, boolean air) {
        float consumption = air ? this.heavyAirAttackConsumption : (dash ? this.heavyDashAttackConsumption : this.heavyAttackConsumption);
        return playerPatch.consumeForSkill(this, this.resource, consumption);
    }

    private static @NotNull SPAnimatorControl getAnimatorControl(SkillContainer skillContainer, boolean stiffAttack, AnimationManager.AnimationAccessor<? extends AttackAnimation> attackMotion) {
        return stiffAttack
                ? new SPAnimatorControl(Action.PLAY, attackMotion, 0.0F, skillContainer.getExecutor())
                : new SPAnimatorControl(Action.PLAY_CLIENT, attackMotion,0.0F, skillContainer.getExecutor(), Layer.COMPOSITE_LAYER, Priority.HIGHEST);
    }

}