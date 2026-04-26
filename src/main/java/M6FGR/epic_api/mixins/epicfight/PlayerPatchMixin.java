package M6FGR.epic_api.mixins.epicfight;

import M6FGR.epic_api.gameassets.EpicAPISkills;
import M6FGR.epic_api.skills.EpicAPISkillSlots;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.skill.CapabilitySkill;

@Mixin(value = PlayerPatch.class, remap = false)
public abstract class PlayerPatchMixin<T extends Player> extends LivingEntityPatch<T> {


    @Shadow public abstract CapabilitySkill getSkillCapability();

    @Inject(
            at = @At(value = "HEAD"),
            method = "onJoinWorld(Lnet/minecraft/world/entity/player/Player;Lnet/minecraftforge/event/entity/EntityJoinLevelEvent;)V",
            remap = false
    )
    public void onJoinWorld(T entity, EntityJoinLevelEvent event, CallbackInfo ci) {
        CapabilitySkill skillCapability = this.getSkillCapability();
        skillCapability.getSkillContainerFor(EpicAPISkillSlots.HEAVY_ATTACK).setSkill(EpicAPISkills.HEAVY_ATTACKS);
    }
}
