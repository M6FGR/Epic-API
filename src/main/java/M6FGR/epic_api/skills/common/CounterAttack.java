package M6FGR.epic_api.skills.common;

import M6FGR.epic_api.animation.EpicAPIAnimationStates;
import M6FGR.epic_api.builders.epicfight.WeaponCapabilityBuilder;
import M6FGR.epic_api.events.player.CounterAttackEvent;
import M6FGR.epic_api.gameassets.EpicAPISkillDataKeys;
import M6FGR.epic_api.skills.EpicAPISkillCategories;
import M6FGR.epic_api.utils.EnvironmentHelper;
import M6FGR.epic_api.utils.EnvironmentHelper.Environments;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.gameasset.EpicFightSkills;
import yesman.epicfight.gameasset.EpicFightSounds;
import yesman.epicfight.network.EntityPairingPacketTypes;
import yesman.epicfight.network.EpicFightNetworkManager;
import yesman.epicfight.network.server.SPEntityPairingPacket;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillBuilder;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataKey;
import yesman.epicfight.skill.SkillDataKeys;
import yesman.epicfight.skill.SkillDataManager;
import yesman.epicfight.skill.guard.GuardSkill.BlockType;
import yesman.epicfight.skill.guard.ParryingSkill;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.capabilities.item.Style;
import yesman.epicfight.world.capabilities.item.WeaponCategory;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener.EventType;
import yesman.epicfight.world.entity.eventlistener.SkillConsumeEvent;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

@SuppressWarnings("unchecked")
public class CounterAttack extends Skill {
    protected float counterConsumption;

    public CounterAttack(SkillBuilder<?> builder) {
        super(builder);
    }

    public static SkillBuilder<CounterAttack> createCounterAttackBuilder() {
        return new SkillBuilder()
                .setCategory(EpicAPISkillCategories.COUNTER_ATTACK)
                .setActivateType(ActivateType.ONE_SHOT)
                .setResource(Resource.STAMINA);
    }

    @Override
    public void setParams(CompoundTag params) {
        this.counterConsumption = params.getFloat("stamina_consumption");
    }

    @Override
    public void executeOnServer(SkillContainer container, FriendlyByteBuf args) {
        ParryingSkill skill = (ParryingSkill) EpicFightSkills.PARRYING;
        PlayerPatch<?> playerPatch = container.getExecutor();
        ServerPlayerPatch serverPlayerPatch = container.getServerExecutor();
        ServerPlayer serverPlayer = serverPlayerPatch.getOriginal();
        SPEntityPairingPacket packet = new SPEntityPairingPacket(serverPlayer.getId(), EntityPairingPacketTypes.FLASH_WHITE);
             packet.getBuffer().writeInt(4);
             packet.getBuffer().writeInt(15);
             packet.getBuffer().writeInt(8);
             packet.getBuffer().writeBoolean(false);
        boolean canParry = skill.isHoldingWeaponAvailable(playerPatch, playerPatch.getHoldingItemCapability(InteractionHand.MAIN_HAND), BlockType.ADVANCED_GUARD);
        CounterType counterType = canParry ? CounterType.PARRY : CounterType.NORMAL;
        AnimationAccessor<? extends AttackAnimation> animation = this.getCounterMotion(container, playerPatch.getHoldingItemCapability(InteractionHand.MAIN_HAND), counterType);
        SkillConsumeEvent skillConsumeEvent = new SkillConsumeEvent(playerPatch, this, this.resource, args);
        CounterAttackEvent counterAttackEvent = new CounterAttackEvent(serverPlayerPatch);
        playerPatch.getEventListener().triggerEvents(EventType.SKILL_CONSUME_EVENT, skillConsumeEvent);
        if (!skillConsumeEvent.isCanceled()) {
            skillConsumeEvent.getResourceType().consumer.consume(container, serverPlayerPatch, this.counterConsumption);
        }
        playerPatch.getEventListener().triggerEvents(CounterAttackEvent.TYPE, counterAttackEvent);
        if (!counterAttackEvent.isCanceled()) {
            container.getExecutor().playSound(EpicFightSounds.HYPERVITALITY.get(), 0.5F, 0, 0);
            EpicFightNetworkManager.sendToAllPlayerTrackingThisEntityWithSelf(packet, serverPlayer);
            playerPatch.playAnimationSynchronized(animation, 0.0F);
        }
    }


    protected AnimationManager.AnimationAccessor<? extends AttackAnimation> getCounterMotion(SkillContainer container, CapabilityItem itemCapability, CounterType counterType) {
        WeaponCategory category = itemCapability.getWeaponCategory();
        Style style = itemCapability.getStyle(container.getExecutor());
        if (counterType == CounterType.PARRY) {
            List<AnimationManager.AnimationAccessor<? extends AttackAnimation>> parryList = WeaponCapabilityBuilder.getParryCounters(category, style);
            if (parryList != null && !parryList.isEmpty()) {
                SkillDataManager dataManager = container.getDataManager();
                int motionCounter = dataManager.getDataValue(EpicAPISkillDataKeys.COUNTER_MOTION.get());
                dataManager.setDataF(EpicAPISkillDataKeys.COUNTER_MOTION.get(), v -> v + 1);

                return parryList.get(Math.abs(motionCounter) % parryList.size());
            }
        }
        return WeaponCapabilityBuilder.getNormalCounter(category, style);
    }

    @Override
    public boolean isExecutableState(PlayerPatch<?> executor) {
        boolean baseExecutable = super.isExecutableState(executor);
        boolean hasStamina = executor.getStamina() >= this.counterConsumption;
        boolean canCounter = executor.getEntityState().getState(EpicAPIAnimationStates.CAN_COUNTER);
        return baseExecutable && hasStamina && canCounter;
    }


    public enum CounterType {
        NORMAL,
        PARRY
    }
}
