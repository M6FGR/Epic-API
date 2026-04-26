package M6FGR.epic_api.main;

import M6FGR.epic_api.animation.types.SimpleAttackAnimation;
import M6FGR.epic_api.animation.types.SimpleAttackAnimation.TrailColor;
import M6FGR.epic_api.animation.types.SimpleAttackAnimation.TrailPreset;
import M6FGR.epic_api.animation.types.SimpleMovementAnimation;
import M6FGR.epic_api.animation.types.SimpleStaticAnimation;
import M6FGR.epic_api.animation.types.SimpleStaticAnimation.JointMasks;
import M6FGR.epic_api.builders.epicfight.ArmatureBuilder;
import M6FGR.epic_api.builders.epicfight.ArmatureBuilder.ArmatureType;
import M6FGR.epic_api.builders.epicfight.EntityPatchBuilder;
import M6FGR.epic_api.builders.epicfight.MeshBuilder;
import M6FGR.epic_api.builders.epicfight.MeshBuilder.MeshType;
import M6FGR.epic_api.builders.epicfight.MoveSetBuilder;
import M6FGR.epic_api.builders.epicfight.WeaponCapabilityBuilder;
import M6FGR.epic_api.builders.minecraft.GameRulesBuilder;
import M6FGR.epic_api.builders.minecraft.GameRulesBuilder.EnumValue;
import M6FGR.epic_api.builders.minecraft.ItemsBuilder;
import M6FGR.epic_api.cls.Compatibility;
import M6FGR.epic_api.cls.ILoadableClass;
import M6FGR.epic_api.events.entity.EntityPatchBuilderRegistryEvent;
import M6FGR.epic_api.events.item.MoveSetBuilderRegistryEvent;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.GameRules.BooleanValue;
import net.minecraft.world.level.GameRules.Category;
import net.minecraft.world.level.GameRules.IntegerValue;
import net.minecraft.world.level.GameRules.Key;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.client.animation.Layer;
import yesman.epicfight.api.client.model.Meshes;
import yesman.epicfight.api.client.model.Meshes.MeshAccessor;
import yesman.epicfight.api.client.model.SkinnedMesh;
import yesman.epicfight.api.forgeevent.WeaponCapabilityPresetRegistryEvent;
import yesman.epicfight.client.mesh.HumanoidMesh;
import yesman.epicfight.client.mesh.WitherMesh;
import yesman.epicfight.client.renderer.patched.entity.PHumanoidRenderer;
import yesman.epicfight.client.renderer.patched.entity.PIronGolemRenderer;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.gameasset.Armatures.ArmatureAccessor;
import yesman.epicfight.gameasset.ColliderPreset;
import yesman.epicfight.gameasset.EpicFightSkills;
import yesman.epicfight.gameasset.EpicFightSounds;
import yesman.epicfight.gameasset.ex_cap.MainConditionals;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.model.armature.HumanoidArmature;
import yesman.epicfight.model.armature.PiglinArmature;
import yesman.epicfight.particle.EpicFightParticles;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.entitypatch.mob.IronGolemPatch;
import yesman.epicfight.world.capabilities.entitypatch.mob.ZombiePatch;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.capabilities.item.WeaponCapability;

import java.util.function.Function;
// This class shows how this API is used, no more!
class EpicAPIPlaceHolders {
    
    private static class Armature {
        // First is parsedPath(modid:path)
        public static final ArmatureAccessor<HumanoidArmature> PLACEHOLDER_ARMATURE_PP = ArmatureBuilder.newEntityArmature(EntityType.ZOMBIE, "epicfight:entity/biped", HumanoidArmature::new);
        // Second is normal path(modid, path)
        public static final ArmatureAccessor<HumanoidArmature> PLACEHOLDER_ARMATURE_NP = ArmatureBuilder.newEntityArmature(EntityType.ZOMBIE, "epicfight", "entity/biped", HumanoidArmature::new);
        // Third is ResourceLocation (ResourceLocation.fromNamespaceAndPath(modid, path)
        public static final ArmatureAccessor<HumanoidArmature> PLACEHOLDER_ARMATURE_RL = ArmatureBuilder.newEntityArmature(EntityType.ZOMBIE, EpicFightMod.identifier("entity/biped"), HumanoidArmature::new);
        // Needs to be posted via FMLCommonSetupEvent
        public static final ArmatureAccessor<HumanoidArmature> PLACEHOLDER_ARMATURE_NO_ENTITY = ArmatureBuilder.newArmature("epicfight:entity/biped", HumanoidArmature::new);
        // or if you want to, you could use ArmatureType for easier registry, as so:
        public static final ArmatureAccessor<HumanoidArmature> PLACEHOLDER_ARMATURE_NE_EHUMANOID = ArmatureBuilder.newArmature("epicfight:entity/biped", ArmatureType.HUMANOID_ARMATURE);

        public static final ArmatureAccessor<HumanoidArmature> PLACEHOLDER_ARMATURE_NE_NON_HUMANOID = ArmatureBuilder.newArmature("epicfight:entity/wither", ArmatureType.NON_HUMANOID_ARMATURE);

        public static final ArmatureAccessor<HumanoidArmature> PLACEHOLDER_ARMATURE_NE_CUSTOM = ArmatureBuilder.newArmature("epicfight:entity/piglin", ArmatureType.of(PiglinArmature::new));
    }

    private static class Mesh {
        // Same thing as the armature path arguments, it accepts 3 arguments
        // It accepts either MeshType or MeshConstructors as exampled:

        // MeshType is holding 4 types of mesh types: (SkinnedMesh, ClassicMesh, CompositeMesh, HumanoidMesh)
        public static final Meshes.MeshAccessor<HumanoidMesh> PLACEHOLDER_MESH_E = MeshBuilder.newMesh("epicfight:entity/biped", MeshType.HUMANOID_MESH);
        // Or if you couldn't find what you wish for, you could use the constructor as so:
        public static final Meshes.MeshAccessor<WitherMesh> PLACEHOLDER_MESH = MeshBuilder.newMesh("epicfight:entity/wither", MeshType.of(WitherMesh::new));
        // This automatically detects the mesh type inside the JSON file itself, check JsonAssetLoader#loadMesh()
        public static final MeshAccessor<SkinnedMesh> PLACEHOLDER_MESH_JSON_LOADER = MeshBuilder.newMesh("epicfight:layer/default_cape", MeshType.MESH_LOADER);
    }

    private static class EntityPatch {
        public static EntityPatchBuilder<Zombie> HUMANOID_PATCH = EntityPatchBuilder.get().newEntityPatch(EntityType.ZOMBIE, ZombiePatch::new, (context, type) -> new PHumanoidRenderer<>(Mesh.PLACEHOLDER_MESH_E, context, type));

        public static EntityPatchBuilder<IronGolem> NON_HUMANOID_PATCH = EntityPatchBuilder.get().newEntityPatch(EntityType.IRON_GOLEM, IronGolemPatch::new, PIronGolemRenderer::new);


        public static void registerPatches(EntityPatchBuilderRegistryEvent event) {
            event.registerFrom(HUMANOID_PATCH);
            event.registerFrom(NON_HUMANOID_PATCH);
        }

    }


    private static class Items {
        private static final DeferredRegister<Item> ITEM_REGISTRY = DeferredRegister.create(Registries.ITEM, "modid");

        public static final RegistryObject<Item> PLACEHOLDER_ITEM = ItemsBuilder.newItem("item", Item::new, ITEM_REGISTRY);

        public static final RegistryObject<Item> PLACEHOLDER_ITEM_PROPERTIES = ItemsBuilder.newItem("item", Item::new, ITEM_REGISTRY, properties -> properties.durability(1000));
    }


    private static class GameRules {
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

    private static class Animation {
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

    @Compatibility(modid = "example_mod", clientSide = true, printWarns = true)
    // MUST implement ILoadableClass and loaded in the mod constructor!
    private static class CompatibilityClass implements ILoadableClass {
        /* You here do the compatibility code, Based on the params in the annotation:
         it will load if the mod-id was found via ModList#isLoaded
         it will load if the environment was client sided
         it will print warning if the target mod wasn't loaded, or the mod is loaded on a dedicated server

        Also, param#printWarns is optional, it's mostly for debugs and when the class was loaded
        */


        // not loading the class, it's a placeholder
        @Override
        public boolean shouldLoad() {
            return false;
        }
    }

    private static class CapabilityPresets {

        public static final Function<Item, CapabilityItem.Builder> EXAMPLE_EFM = item ->
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
                                EpicFightSkills.LIECHTENAUER,
                                Animations.LONGSWORD_AUTO1,
                                Animations.LONGSWORD_AUTO2,
                                Animations.LONGSWORD_AUTO3,
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
                                EpicFightSkills.SHARP_STAB
                        )
                        // Innate skill preset (Ochs)
                        .secondaryPreset(
                                CapabilityItem.Styles.OCHS,
                                null,
                                EpicFightSkills.LIECHTENAUER,
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
        public static void registerCapability(WeaponCapabilityPresetRegistryEvent event) {
            event.getTypeEntry().put(EpicAPI.identifier("example_preset"), EXAMPLE_EFM);
        }


        // making is static final will cause a NullPointerException!, initialize it inside the registry method!
        private static MoveSetBuilder EXAMPLE_EXCAP;


        // post in FMLCommonSetupEvent
        public static void registerMoveSet(MoveSetBuilderRegistryEvent event) {
            EXAMPLE_EXCAP = MoveSetBuilder.builder()
                    .newMoveSet(
                            CapabilityItem.Styles.TWO_HAND,
                            EpicAPI.identifier("example_2h"),
                            CapabilityItem.WeaponCategories.LONGSWORD,
                            ColliderPreset.LONGSWORD,
                            EpicFightSounds.WHOOSH_ROD.get(),
                            EpicFightSounds.BLUNT_HIT_HARD.get(),
                            EpicFightParticles.HIT_BLUNT.get(),
                            false,
                            null,
                            EpicFightSkills.SWEEPING_EDGE,
                            MainConditionals.DEFAULT_2H_WIELD_STYLE,
                            Animations.SWORD_AUTO1,
                            Animations.SWORD_AUTO2,
                            Animations.SWORD_DASH,
                            Animations.SWORD_AIR_SLASH
                    )
                    .forEachMotion(
                            LivingMotions.IDLE, yesman.epicfight.gameasset.Animations.BIPED_IDLE,
                            LivingMotions.WALK, yesman.epicfight.gameasset.Animations.BIPED_WALK,
                            LivingMotions.RUN, yesman.epicfight.gameasset.Animations.BIPED_RUN_LONGSWORD
                    )
                    // child gets the motions from above
                    .withChildMoveSet(
                            CapabilityItem.Styles.ONE_HAND,
                            EpicAPI.identifier("example_1h"),
                            EpicFightSkills.SWEEPING_EDGE,
                            null,
                            MainConditionals.SHIELD_OFFHAND,
                            Animations.SWORD_AUTO1,
                            Animations.SWORD_AUTO2,
                            Animations.SWORD_AUTO3,
                            Animations.SWORD_DASH,
                            Animations.SWORD_AIR_SLASH
                    ).withDefaultBipedMotions();
            event.register(EpicAPI.identifier("example_moveset_capability"), EXAMPLE_EXCAP);
        }

    }
}
