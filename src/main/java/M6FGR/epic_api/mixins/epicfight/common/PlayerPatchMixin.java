package M6FGR.epic_api.mixins.epicfight.common;

import M6FGR.epic_api.events.epic_api.player.AddPlayerMotionsEventHook;
import M6FGR.epic_api.gameassets.EpicAPISkills;
import M6FGR.epic_api.skills.EpicAPISkillSlots;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yesman.epicfight.api.animation.Animator;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.skill.PlayerSkills;

@Mixin(value = PlayerPatch.class, remap = false)
public abstract class PlayerPatchMixin<T extends Player> extends LivingEntityPatch<T> {
    public PlayerPatchMixin(T entity) {
        super(entity);
    }

    @Shadow
    public abstract PlayerSkills getPlayerSkills();


    @Inject(
            at = @At(value = "HEAD"),
            method = "onJoinWorld(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/level/Level;Z)V",
            remap = false
    )
    public void epicAPI$onJoinWorld(T entity, Level level, boolean worldgenSpawn, CallbackInfo ci) {
        // add HeavyAttack and CounterAttack as a default skill
        PlayerSkills skillCapability = this.getPlayerSkills();
        skillCapability.getSkillContainerFor(EpicAPISkillSlots.HEAVY_ATTACK).setSkill(EpicAPISkills.HEAVY_ATTACKS.get());
        skillCapability.getSkillContainerFor(EpicAPISkillSlots.COUNTER_ATTACK).setSkill(EpicAPISkills.COUNTER_ATTACK.get());
    }

    @Inject(
            at = @At("TAIL"),
            method = "initAnimator",
            remap = false
    )

    public void injectAddMotionsEH(Animator animator, CallbackInfo ci) {
        AddPlayerMotionsEventHook addPlayerMotionsEH = new AddPlayerMotionsEventHook(animator, (PlayerPatch<?>) (Object) this);
        addPlayerMotionsEH.post();
    }
}
