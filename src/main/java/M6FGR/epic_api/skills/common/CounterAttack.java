package M6FGR.epic_api.skills.common;

import M6FGR.epic_api.animation.EpicAPIAnimationStates;
import M6FGR.epic_api.builders.epicfight.excap.deferred.DeferredCapabilityBuilder;
import M6FGR.epic_api.events.player.CounterAttackEvent;
import M6FGR.epic_api.gameassets.EpicAPISkillDataKeys;
import M6FGR.epic_api.network.EpicAPINetworkManager;
import M6FGR.epic_api.skills.EpicAPISkillCategories;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.event.types.player.SkillConsumeEvent;
import yesman.epicfight.network.EntityPairingPacketTypes;
import yesman.epicfight.registry.entries.EpicFightSkills;
import yesman.epicfight.registry.entries.EpicFightSounds;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillBuilder;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataManager;
import yesman.epicfight.skill.guard.GuardSkill;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.capabilities.item.WeaponCategory;

import java.util.List;

public class CounterAttack extends Skill {
    private float counterConsumption;

    public static SkillBuilder<?> createCounterAttackBuilder() {
        return new SkillBuilder<>(CounterAttack::new)
                .setCategory(EpicAPISkillCategories.COUNTER_ATTACK)
                .setResource(Resource.STAMINA);
    }

    public CounterAttack(SkillBuilder<?> builder) {
        super(builder);
    }

    @Override
    public void loadDatapackParameters(CompoundTag params) {
        this.counterConsumption = params.getFloat("stamina_consumption");
    }

    @Override
    public void executeOnServer(SkillContainer container, CompoundTag tag) {
        PlayerPatch<?> executor = container.getExecutor();
        ServerPlayerPatch serverExecutor = container.getServerExecutor();
        CapabilityItem mainHand = executor.getHoldingItemCapability(InteractionHand.MAIN_HAND);

        boolean canParry = EpicFightSkills.PARRYING.get().isHoldingWeaponAvailable(executor, mainHand, GuardSkill.BlockType.ADVANCED_GUARD);
        CounterTypes counterType = canParry ? CounterTypes.PARRY : CounterTypes.NORMAL;

        // Resolve the exact animation using static getters
        AnimationManager.AnimationAccessor<? extends StaticAnimation> animation = this.getCounterMotion(container, mainHand, counterType);
        if (animation == null) {
            return;
        }

        CounterAttackEvent counterAttackEvent = new CounterAttackEvent(container);

        if (!counterAttackEvent.post().isCanceled()) {
            SkillConsumeEvent event = new SkillConsumeEvent(executor, this, this.resource, null);
            if (!event.isCanceled()) {
                event.getResourceType().consumer.consume(container, serverExecutor, this.counterConsumption);
            }

            container.getExecutor().playSound(EpicFightSounds.HYPERVITALITY.get(), 0.5F, 0, 0);

            EpicAPINetworkManager.sendPairingPacket(
                    serverExecutor.getOriginal(),
                    EntityPairingPacketTypes.FLASH_WHITE,
                    // arguments
                    4, 15, 8, false
            );
            executor.playAnimationSynchronized(animation, 0.0F);
        }
    }

    protected AnimationManager.AnimationAccessor<? extends StaticAnimation> getCounterMotion(SkillContainer container, CapabilityItem itemCapability, CounterTypes counterType) {
        WeaponCategory category = itemCapability.getWeaponCategory();

        if (counterType == CounterTypes.PARRY) {
            // Fetch list via getParryingCounterAttacks getter
            List<AnimationManager.AnimationAccessor<? extends AttackAnimation>> parryList = DeferredCapabilityBuilder.getParryingCounterAttacks(category);

            if (parryList != null && !parryList.isEmpty()) {
                SkillDataManager dataManager = container.getDataManager();
                int motionCounter = dataManager.getDataValue(EpicAPISkillDataKeys.MOTION_COUNTER);
                dataManager.setDataF(EpicAPISkillDataKeys.MOTION_COUNTER, (v) -> v + 1);

                return parryList.get(Math.abs(motionCounter) % parryList.size());
            }
        }

        // Fall back or default to normal counter if not parrying / no parry list is configured
        return DeferredCapabilityBuilder.getNormalCounterAttack(category);
    }

    @Override
    public boolean isExecutableState(PlayerPatch<?> executor) {
        boolean baseExecutable = super.isExecutableState(executor);
        boolean hasStamina = executor.getStamina() >= this.counterConsumption;
        boolean canCounter = executor.getEntityState().getState(EpicAPIAnimationStates.CAN_COUNTER);

        return baseExecutable && hasStamina && canCounter;
    }


    public enum CounterTypes {
        NORMAL,
        PARRY
    }
}