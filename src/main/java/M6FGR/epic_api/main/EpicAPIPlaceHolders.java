package M6FGR.epic_api.main;

import M6FGR.epic_api.animation.types.SimpleAttackAnimation;
import M6FGR.epic_api.animation.types.SimpleAttackAnimation.TrailColor;
import M6FGR.epic_api.animation.types.SimpleAttackAnimation.TrailPreset;
import M6FGR.epic_api.animation.types.SimpleMovementAnimation;
import M6FGR.epic_api.animation.types.SimpleStaticAnimation;
import M6FGR.epic_api.animation.types.SimpleStaticAnimation.JointMasks;
import M6FGR.epic_api.builders.epicfight.ArmatureRegistrar;
import M6FGR.epic_api.builders.epicfight.ArmatureRegistrar.ArmatureType;
import M6FGR.epic_api.builders.epicfight.EntityPatchRegistrar;
import M6FGR.epic_api.builders.epicfight.MeshBuilder;
import M6FGR.epic_api.builders.epicfight.MeshBuilder.MeshType;
import M6FGR.epic_api.builders.epicfight.MoveSetBuilder;
import M6FGR.epic_api.builders.epicfight.WeaponCapabilityBuilder;
import M6FGR.epic_api.builders.minecraft.GameRulesBuilder;
import M6FGR.epic_api.builders.minecraft.GameRulesBuilder.EnumValue;
import M6FGR.epic_api.builders.minecraft.ItemsBuilder;
import M6FGR.epic_api.events.EpicAPIEventHooks;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.GameRules.BooleanValue;
import net.minecraft.world.level.GameRules.Category;
import net.minecraft.world.level.GameRules.IntegerValue;
import net.minecraft.world.level.GameRules.Key;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import yesman.epicfight.EpicFight;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.client.animation.Layer;
import yesman.epicfight.api.client.model.Meshes;
import yesman.epicfight.api.event.EpicFightEventHooks;
import yesman.epicfight.api.ex_cap.modules.assets.MainConditionals;
import yesman.epicfight.client.mesh.HumanoidMesh;
import yesman.epicfight.client.mesh.WitherMesh;
import yesman.epicfight.client.renderer.patched.entity.PHumanoidRenderer;
import yesman.epicfight.client.renderer.patched.entity.PIronGolemRenderer;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.gameasset.Armatures.ArmatureAccessor;
import yesman.epicfight.gameasset.ColliderPreset;
import yesman.epicfight.model.armature.HumanoidArmature;
import yesman.epicfight.model.armature.PiglinArmature;
import yesman.epicfight.registry.entries.EpicFightParticles;
import yesman.epicfight.registry.entries.EpicFightSkills;
import yesman.epicfight.registry.entries.EpicFightSounds;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.entitypatch.mob.IronGolemPatch;
import yesman.epicfight.world.capabilities.entitypatch.mob.ZombiePatch;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.capabilities.item.WeaponCapability;

import java.util.function.Function;
// This class shows how this API is used, no more!
class EpicAPIPlaceHolders {
    public static class Armature {
        // First is parsedPath(modid:path)
        public static final ArmatureAccessor<HumanoidArmature> PLACEHOLDER_ARMATURE_PP = ArmatureRegistrar.newEntityArmature(EntityType.ZOMBIE, "epicfight:entity/biped", HumanoidArmature::new);
        // Second is normal path(modid, path)
        public static final ArmatureAccessor<HumanoidArmature> PLACEHOLDER_ARMATURE_NP = ArmatureRegistrar.newEntityArmature(EntityType.ZOMBIE, "epicfight", "entity/biped", HumanoidArmature::new);
        // Third is ResourceLocation (ResourceLocation.fromNamespaceAndPath(modid, path)
        public static final ArmatureAccessor<HumanoidArmature> PLACEHOLDER_ARMATURE_RL = ArmatureRegistrar.newEntityArmature(EntityType.ZOMBIE, EpicFight.identifier("entity/biped"), HumanoidArmature::new);
        // Needs to be posted via FMLCommonSetupEvent
        public static final ArmatureAccessor<HumanoidArmature> PLACEHOLDER_ARMATURE_NO_ENTITY = ArmatureRegistrar.newArmature("epicfight:entity/biped", HumanoidArmature::new);
        // or if you want to, you could use ArmatureType for easier registry, as so:
        public static final ArmatureAccessor<HumanoidArmature> PLACEHOLDER_ARMATURE_NE_EHUMANOID = ArmatureRegistrar.newArmature("epicfight:entity/biped", ArmatureType.HUMANOID_ARMATURE);

        public static final ArmatureAccessor<HumanoidArmature> PLACEHOLDER_ARMATURE_NE_NON_HUMANOID = ArmatureRegistrar.newArmature("epicfight:entity/wither", ArmatureType.NON_HUMANOID_ARMATURE);

        public static final ArmatureAccessor<HumanoidArmature> PLACEHOLDER_ARMATURE_NE_CUSTOM = ArmatureRegistrar.newArmature("epicfight:entity/piglin", ArmatureType.of(PiglinArmature::new));
    }

    public static class Mesh {
        // Same thing as the armature path arguments, it accepts 3 arguments
        // It accepts either MeshType or MeshConstructors as exampled:

        // MeshType is holding 4 types of mesh types: (SkinnedMesh, ClassicMesh, CompositeMesh, HumanoidMesh)
        public static final Meshes.MeshAccessor<HumanoidMesh> PLACEHOLDER_MESH_E = MeshBuilder.newMesh("epicfight:entity/biped", MeshType.HUMANOID_MESH);
        // Or if you couldn't find what you wish for, you could use the constructor as so:
        public static final Meshes.MeshAccessor<HumanoidMesh> PLACEHOLDER_MESH = MeshBuilder.newMesh("epicfight:entity/wither", MeshType.of(loader -> loader.loadSkinnedMesh(WitherMesh::new)));
    }

    public static class EntityPatch  {
        public static EntityPatchRegistrar<Zombie> HUMANOID_PATCH;
        public static EntityPatchRegistrar<IronGolem> NON_HUMANOID_PATCH;


        // you can either use ILoadableClass or call this in the modCommonEvents in your main class:
        public void registerPatches() {
            // NEVER initialize them outside if you're going to use ILoadableClass
            HUMANOID_PATCH = EntityPatchRegistrar.get().newEntityPatch(EntityType.ZOMBIE, ZombiePatch::new, (context, type) -> new PHumanoidRenderer<>(Mesh.PLACEHOLDER_MESH_E, context, type));
            NON_HUMANOID_PATCH = EntityPatchRegistrar.get().newEntityPatch(EntityType.IRON_GOLEM, IronGolemPatch::new, PIronGolemRenderer::new);
            // critical!, use Epic-API's event hooks to work, not epic fight's!
            EpicAPIEventHooks.Registry.ENTITY_PATCH.registerEvent(event -> {
                event.registerFrom(HUMANOID_PATCH);
                event.registerFrom(NON_HUMANOID_PATCH);
            });
        }

    }


    public static class Items {
        private static final DeferredRegister<Item> ITEM_REGISTRY = DeferredRegister.create(Registries.ITEM, "modid");

        public static final DeferredHolder<Item, Item> PLACEHOLDER_ITEM = ItemsBuilder.newItem("item", Item::new, ITEM_REGISTRY);

        public static final DeferredHolder<Item, Item> PLACEHOLDER_ITEM_PROPERTIES = ItemsBuilder.newItem("item", Item::new, ITEM_REGISTRY, properties -> properties.durability(1000));
    }


    public static class GameRules {
        // as following, this is how simple it is to register gamerules:

        // non-synchronized is a gamerule applies to the client only (executor-only), e.g -> /gamerule setResolution Resolution.FULL_HD
        public static final Key<EnumValue<PlaceHolders>> PLACEHOLDER_ENUM = GameRulesBuilder.newEnum("enum", Category.CHAT, PlaceHolders.FIRST);

        // synchronized is basically a gamerule that applies to all (every player in the world), e.g. -> /gamerule setCameraType THIRD_PERSON_BACK (net.minecraft.client.CameraType)
        public static final Key<EnumValue<PlaceHolders>> PLACEHOLDER_ENUM_SYNC = GameRulesBuilder.newEnum("enumSynced", Category.CHAT, PlaceHolders.FIRST, true);
        // the same thing applies to all the gamerules below, just a different key type
        public static final Key<IntegerValue> PLACEHOLDER_INT = GameRulesBuilder.newInteger("int", Category.CHAT, 1);

        public static final Key<IntegerValue> PLACEHOLDER_INT_SYNC = GameRulesBuilder.newInteger("intSynced", Category.CHAT, 1, true);

        public static final Key<BooleanValue> PLACEHOLDER_BOOL = GameRulesBuilder.newBoolean("bool", Category.CHAT, true);

        public static final Key<BooleanValue> PLACEHOLDER_BOOL_SYNC = GameRulesBuilder.newBoolean("boolSynced", Category.CHAT, true, true);


        private enum PlaceHolders {
            FIRST,
            SECOND,
            THIRD;
        }
    }

    public static class Animation {
        public static AnimationManager.AnimationAccessor<SimpleStaticAnimation> PLACEHOLDER_IDLE;
        public static AnimationManager.AnimationAccessor<SimpleMovementAnimation> PLACEHOLDER_WALK;
        public static AnimationManager.AnimationAccessor<SimpleAttackAnimation> PLACEHOLDER_ATTACK;
        private void registerAnimations(AnimationManager.AnimationRegistryEvent event) {
            event.newBuilder("example", Animation::build);
        }

        private static void build(AnimationManager.AnimationBuilder builder) {
            Joint toolR = Armatures.BIPED.get().toolR;
            ArmatureAccessor<HumanoidArmature> BIPED = Armatures.BIPED;
                    PLACEHOLDER_IDLE = builder.nextAccessor("path/idle", accessor ->
                    new SimpleStaticAnimation(
                            true,
                            accessor,
                            BIPED
                    )
                    .withLayer(Layer.LayerType.COMPOSITE_LAYER)
                    .withPriority(Layer.Priority.LOW)
                    .withJointMask(JointMasks.ROOT_UPPER_JOINTS)
            );

            PLACEHOLDER_WALK = builder.nextAccessor("path/walk", accessor ->
                    new SimpleMovementAnimation(
                            true,
                            1.16F,
                            accessor,
                            BIPED
                    )
                    .withLayer(Layer.LayerType.COMPOSITE_LAYER)
                    .withPriority(Layer.Priority.MIDDLE)
                    .withJointMask(JointMasks.RIGHT_ARM)
            );

            PLACEHOLDER_ATTACK = builder.nextAccessor("path/attack", accessor ->
                    new SimpleAttackAnimation(
                       0.1F,
                       0.2F,
                       11,
                       16,
                       0.8F,
                       null,
                       toolR,
                       accessor,
                       BIPED
               )
               .addTrail(
                       "Tool_R",
                       TrailColor.newColor(0.2F, 0.2F, 0.2F),
                       TrailPreset.SWORD
               )
               .multiplyDamage(
                       0.3F
               )
               .multiplyImpact(
                       0.6F
               )
            );
        }
    }

    public static class CapabilityPresets {

        public static final Function<Item, WeaponCapability.Builder> EXAMPLE_EFM = item ->
                WeaponCapabilityBuilder.builder()
                        .withStyleConditions(entityPatch -> {
                            if (entityPatch.getHoldingItemCapability(InteractionHand.OFF_HAND).getWeaponCategory() == CapabilityItem.WeaponCategories.SHIELD) {
                                return CapabilityItem.Styles.ONE_HAND;
                            } else if (entityPatch instanceof PlayerPatch<?> playerPatch) {
                                if (playerPatch.getSkill(SkillSlots.WEAPON_INNATE).isActivated()) {
                                    return CapabilityItem.Styles.OCHS;
                                }
                            }
                            return CapabilityItem.Styles.TWO_HAND;
                        })
                        // Two-handed preset
                        .newPreset(
                                CapabilityItem.Styles.TWO_HAND,
                                CapabilityItem.WeaponCategories.LONGSWORD,
                                ColliderPreset.LONGSWORD,
                                EpicFightSounds.WHOOSH.get(),
                                EpicFightSounds.BLADE_HIT.get(),
                                EpicFightParticles.HIT_BLADE.get(),
                                false,
                                null,
                                EpicFightSkills.LIECHTENAUER.get(),
                                Animations.LONGSWORD_AUTO1,
                                Animations.LONGSWORD_AUTO2,
                                Animations.LONGSWORD_AUTO3,
                                Animations.LONGSWORD_DASH,
                                Animations.LONGSWORD_AIR_SLASH
                        )
                        .withNewHeavyCombo(
                                Animations.LONGSWORD_LIECHTENAUER_AUTO1,
                                Animations.LONGSWORD_LIECHTENAUER_AUTO2,
                                Animations.LONGSWORD_LIECHTENAUER_AUTO3,
                                Animations.LONGSWORD_DASH,
                                Animations.LONGSWORD_AIR_SLASH
                        )
                        .forEachMotion(
                                LivingMotions.IDLE, Animations.BIPED_HOLD_LONGSWORD,
                                LivingMotions.WALK, Animations.BIPED_WALK_LONGSWORD,
                                LivingMotions.RUN, Animations.BIPED_RUN_LONGSWORD,
                                LivingMotions.BLOCK, Animations.LONGSWORD_GUARD
                        )
                        // One-handed preset
                        .secondaryStyle(
                                CapabilityItem.Styles.ONE_HAND,
                                null,
                                EpicFightSkills.SHARP_STAB.get()
                        )
                        // Innate skill preset (Ochs)
                        .secondaryPreset(
                                CapabilityItem.Styles.OCHS,
                                null,
                                EpicFightSkills.LIECHTENAUER.get(),
                                Animations.LONGSWORD_LIECHTENAUER_AUTO1,
                                Animations.LONGSWORD_LIECHTENAUER_AUTO2,
                                Animations.LONGSWORD_LIECHTENAUER_AUTO3,
                                Animations.LONGSWORD_DASH,
                                Animations.LONGSWORD_AIR_SLASH
                        )
                        .forEachMotion(
                                CapabilityItem.Styles.OCHS,
                                LivingMotions.IDLE, Animations.BIPED_HOLD_LIECHTENAUER,
                                LivingMotions.WALK, Animations.BIPED_WALK_LIECHTENAUER,
                                LivingMotions.ALL, Animations.BIPED_HOLD_LIECHTENAUER
                        )
                        .build();
        // post in FMLCommonSetupEvent
        private void registerCapability() {
            EpicFightEventHooks.Registry.WEAPON_CAPABILITY_PRESET.registerEvent(event -> event.getTypeEntry().put(ResourceLocation.fromNamespaceAndPath("example", "weapon"), EXAMPLE_EFM));
        }


        // making is static final will cause a NullPointerException!, initialize it inside the registry method!
        private MoveSetBuilder EXAMPLE_EXCAP;


        // post in FMLCommonSetupEvent
        private void registerMoveSet() {
            this.EXAMPLE_EXCAP = MoveSetBuilder.builder()
                    .newMoveSet(
                            CapabilityItem.Styles.TWO_HAND,
                            EpicFight.identifier("example_2h"),
                            CapabilityItem.WeaponCategories.LONGSWORD,
                            ColliderPreset.LONGSWORD,
                            EpicFightSounds.WHOOSH_ROD.get(),
                            EpicFightSounds.BLUNT_HIT_HARD.get(),
                            EpicFightParticles.HIT_BLUNT.get(),
                            false,
                            null,
                            EpicFightSkills.SWEEPING_EDGE.get(),
                            MainConditionals.DEFAULT_2H_WIELD_STYLE,
                            Animations.SWORD_AUTO1,
                            Animations.SWORD_AUTO2,
                            Animations.SWORD_DASH,
                            Animations.SWORD_AIR_SLASH
                    )
                    .withHeavyCombo(
                            CapabilityItem.Styles.TWO_HAND,
                            Animations.LONGSWORD_LIECHTENAUER_AUTO1,
                            Animations.LONGSWORD_LIECHTENAUER_AUTO2,
                            Animations.LONGSWORD_LIECHTENAUER_AUTO3,
                            Animations.LONGSWORD_DASH,
                            Animations.LONGSWORD_AIR_SLASH
                    )
                    .forEachMotion(
                            LivingMotions.IDLE, yesman.epicfight.gameasset.Animations.BIPED_IDLE,
                            LivingMotions.WALK, yesman.epicfight.gameasset.Animations.BIPED_WALK,
                            LivingMotions.RUN, yesman.epicfight.gameasset.Animations.BIPED_RUN_LONGSWORD
                    )
                    // child gets the motions from above
                    .withChildMoveSet(
                            CapabilityItem.Styles.ONE_HAND,
                            EpicFight.identifier("example_1h"),
                            EpicFightSkills.SWEEPING_EDGE.get(),
                            null,
                            MainConditionals.SHIELD_OFFHAND,
                            Animations.SWORD_AUTO1,
                            Animations.SWORD_AUTO2,
                            Animations.SWORD_AUTO3,
                            Animations.SWORD_DASH,
                            Animations.SWORD_AIR_SLASH
                    ).withDefaultBipedMotions();
            EpicAPIEventHooks.Registry.MOVE_SET_CAPABILITY.registerEvent(event -> {
                event.register(ResourceLocation.fromNamespaceAndPath("epic_api", "example_excap"), this.EXAMPLE_EXCAP);
            });
        }
    }
}
