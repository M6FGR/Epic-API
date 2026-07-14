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
import M6FGR.epic_api.builders.epicfight.excap.deferred.DeferredCapabilityBuilder;
import M6FGR.epic_api.builders.minecraft.CommandsBuilder;
import M6FGR.epic_api.builders.minecraft.GameRulesBuilder;
import M6FGR.epic_api.builders.minecraft.GameRulesBuilder.EnumValue;
import M6FGR.epic_api.builders.minecraft.ItemsBuilder;
import M6FGR.epic_api.cls.Compatibility;
import M6FGR.epic_api.events.epic_api.EpicAPIEventHooks;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.GameRules.BooleanValue;
import net.minecraft.world.level.GameRules.Category;
import net.minecraft.world.level.GameRules.IntegerValue;
import net.minecraft.world.level.GameRules.Key;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.server.command.EnumArgument;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.client.animation.Layer;
import yesman.epicfight.api.client.animation.Layer.LayerType;
import yesman.epicfight.api.client.animation.Layer.Priority;
import yesman.epicfight.api.client.model.Meshes;
import yesman.epicfight.api.client.model.Meshes.MeshAccessor;
import yesman.epicfight.api.client.model.SkinnedMesh;
import yesman.epicfight.client.mesh.HumanoidMesh;
import yesman.epicfight.client.mesh.WitherMesh;
import yesman.epicfight.client.renderer.patched.entity.PHumanoidRenderer;
import yesman.epicfight.client.renderer.patched.entity.PIronGolemRenderer;
import yesman.epicfight.client.renderer.patched.entity.PPlayerRenderer;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.gameasset.Armatures.ArmatureAccessor;
import yesman.epicfight.gameasset.ColliderPreset;
import yesman.epicfight.model.armature.HumanoidArmature;
import yesman.epicfight.registry.deferred.ItemPresetRegister;
import yesman.epicfight.registry.deferred.MovesetRegister;
import yesman.epicfight.registry.entries.EpicFightParticles;
import yesman.epicfight.registry.entries.EpicFightProviderConditionals;
import yesman.epicfight.registry.entries.EpicFightSkills;
import yesman.epicfight.registry.entries.EpicFightSounds;
import yesman.epicfight.world.capabilities.entitypatch.mob.IronGolemPatch;
import yesman.epicfight.world.capabilities.entitypatch.mob.ZombiePatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem.Styles;
import yesman.epicfight.world.capabilities.item.CapabilityItem.WeaponCategories;

import java.util.Collection;
import java.util.function.Supplier;

// This class shows how this API is used, no more!
class EpicAPIPlaceHolders {
    static class Armature {
        // these methods can accept 3 types of paths:
        // mod-id:path,
        // mod-id, path,
        // ResourceLocation#fromNamespaceAndPath(mod-id, path)
        public static final ArmatureAccessor<HumanoidArmature> PLACEHOLDER_ARMATURE = ArmatureBuilder.newEntityArmature(EntityType.ZOMBIE, "epicfight:entity/biped", ArmatureType.HUMANOID_ARMATURE);
        // Needs to be posted via FMLCommonSetupEvent
        public static final ArmatureAccessor<HumanoidArmature> PLACEHOLDER_ARMATURE_NO_ENTITY = ArmatureBuilder.newArmature("epicfight:entity/biped", ArmatureType.HUMANOID_ARMATURE);
        // if you couldn't find what type you wish for, you could use the constructor as so:
        public static final ArmatureAccessor<HumanoidArmature> PLACEHOLDER_ARMATURE_CUSTOM_CONSTRUCTOR = ArmatureBuilder.newArmature("epicfight:entity/biped", ArmatureType.of(HumanoidArmature::new));

    }

    static class Mesh {
        // same thing as the armature path arguments, it accepts 3 arguments

        // MeshType is holding 4 types of mesh types: (SkinnedMesh, ClassicMesh, CompositeMesh, HumanoidMesh)
        public static final MeshAccessor<HumanoidMesh> PLACEHOLDER_MESH = MeshBuilder.newMesh("epicfight:entity/biped", MeshType.HUMANOID_MESH);
        // Or if you couldn't find what you wish for, you could use the constructor as so:
        public static final MeshAccessor<WitherMesh> PLACEHOLDER_MESH_CUSTOM_CONSTRUCTOR = MeshBuilder.newMesh("epicfight:entity/wither", MeshType.of(WitherMesh::new));
        // This automatically detects the mesh type inside the JSON file itself, check JsonAssetLoader#loadMesh()
        public static final MeshAccessor<SkinnedMesh> PLACEHOLDER_MESH_JSON_LOADER = MeshBuilder.newMesh("epicfight:layer/default_cape", MeshType.JSON_LOADER);
    }

    static class EntityPatch {

        public static EntityPatchBuilder HUMANOID_PATCH = EntityPatchBuilder.newEntityPatch(EntityType.ZOMBIE, ZombiePatch::new, (context, type) -> new PHumanoidRenderer<>(Meshes.BIPED ,context, type));
        public static EntityPatchBuilder NON_HUMANOID_PATCH = EntityPatchBuilder.newEntityPatch(EntityType.IRON_GOLEM, IronGolemPatch::new, PIronGolemRenderer::new);
        public static EntityPatchBuilder PLAYER_PATCH = EntityPatchBuilder.newEntityPatchUnsafe(EntityType.PLAYER, player -> new ServerPlayerPatch((ServerPlayer) player), PPlayerRenderer::new);


        // you can either use ILoadableClass or call this in the modCommonEvents in your main class:
        public static void registerPatches() {
            // NEVER initialize them outside of this method if you're going to use ILoadableClass
            // use Epic-API's event hooks to work, not epic fight's!
            EpicAPIEventHooks.Registry.ENTITY_PATCH.registerEvent(event -> {
                event.registerFrom(HUMANOID_PATCH);
                event.registerFrom(NON_HUMANOID_PATCH);
                event.registerFrom(PLAYER_PATCH);
            });
        }

    }


    static class Items {
        // will throw an IllegalArgumentException if you use DeferredRegister.create()
        private static final DeferredRegister<Item> REGISTRY = ItemsBuilder.buildRegistry("modid");

        // you don't need to use the REGISTRY above, newItem() already uses it in ItemsBuilder
        public static final DeferredHolder<Item, Item> PLACEHOLDER_ITEM = ItemsBuilder.newItem("item1", Item::new);

        public static final DeferredHolder<Item, Item> PLACEHOLDER_ITEM_PROPS = ItemsBuilder.newItem("item2", Item::new, properties -> properties
                .durability(1990)
                .rarity(Rarity.EPIC)
                .attributes(ItemAttributeModifiers.builder().build()));

    }


    static class GameRules {
        // as following, this is how simple it is to register gamerules:

        // non-synchronized is a gamerule applies to the client only (executor-only), e.g -> /gamerule chatDebug true
        public static final Key<EnumValue<ChatFont>> PLACEHOLDER_ENUM = GameRulesBuilder.newEnum("enum", Category.CHAT, ChatFont.BOLD);

        // synchronized is basically a gamerule that applies to all (every player in the world), e.g. -> /gamerule switchableCamera false (now they can't switch camera types by pressing F5)
        public static final Key<EnumValue<ChatFont>> PLACEHOLDER_ENUM_SYNC = GameRulesBuilder.newEnum("enumSynced", Category.CHAT, ChatFont.BOLD, true);
        // the same thing applies to all the gamerules below, just a different key type
        public static final Key<IntegerValue> PLACEHOLDER_INT = GameRulesBuilder.newInteger("int", Category.CHAT, 1);

        public static final Key<IntegerValue> PLACEHOLDER_INT_SYNC = GameRulesBuilder.newInteger("intSynced", Category.CHAT, 1, true);

        public static final Key<BooleanValue> PLACEHOLDER_BOOL = GameRulesBuilder.newBoolean("bool", Category.CHAT, true);

        public static final Key<BooleanValue> PLACEHOLDER_BOOL_SYNC = GameRulesBuilder.newBoolean("boolSynced", Category.CHAT, true, true);


        enum ChatFont {
            BOLD,
            UNDERLINE,
            DEFAULT;
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
                            // repeatable
                            true,
                            accessor,
                            BIPED
                    )
                    .withLayer(Layer.LayerType.COMPOSITE_LAYER)
                    .withPriority(Layer.Priority.LOW)
                    .withJointMask(JointMasks.BIPED_ROOT_UPPER_JOINTS)
            );

            PLACEHOLDER_WALK = builder.nextAccessor("path/walk", accessor ->
                    new SimpleMovementAnimation(
                            // repeatable
                            true,
                            // movement speed (starts at 1.0F)
                            1.16F,
                            accessor,
                            BIPED
                    )
                    .withLayer(LayerType.COMPOSITE_LAYER)
                    .withPriority(Priority.MIDDLE)
                    .withJointMask(JointMasks.BIPED_RIGHT_ARM)
            );

            PLACEHOLDER_ATTACK = builder.nextAccessor("path/attack", accessor ->
                    new SimpleAttackAnimation(
                       // transitionTime
                       0.1F,
                       // antic
                       0.2F,
                       // preDelay (FPS)
                       11,
                       // contact (FPS)
                       16,
                       // recovery
                       0.8F,
                       // collider
                       null,
                       // collider joint
                       toolR,
                       // accessor
                       accessor,
                       // armature
                       BIPED
               )
               .addTrail(
                       BIPED.get().toolR,
                       TrailColor.IRON,
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

    @Compatibility(
            modid = {"example_mod", "example_mod2"},
            clientSide = false
    )
    // MUST implement ILoadableClass and loaded in the mod constructor!
    private static class CompatibilityClass {// implements ILoadableClass
        /*
         You here do the compatibility code, Based on the params in the annotation:
         it will load if the mods were found via ModList#isLoaded
         it will load if the environment was client sided
         if @param#debug is true, it will print warning if the target mod wasn't loaded, or the mod is loaded on a dedicated server
        */
    }


    static class Commands {
        static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
            dispatcher.register(
                    CommandsBuilder.newRoot("example")
                            // /example enums <targets> <setEnum> ===
                            .newLiteral("enums")
                            .fork(root -> root.newArgument("targets", EntityArgument.entities())
                                        .newEnum("setEnum", BloodType.class)
                                        .executes(context -> setBloodType(context.getSource(), EnumArgument.enumArgument(BloodType.class))))

                            // === BRANCH 2: /example <setFlt> ===
                            .newFloat("setFlt", 0.1F, 30.5F)
                            .executes(context -> rotateBall(context.getSource(), FloatArgumentType.getFloat(context, "setFlt")))

                            // === BRANCH 3: /example <setInt> ===
                            .newInt("setInt", 0, 30)
                            .executes(context -> setTemp(context.getSource(), IntegerArgumentType.getInteger(context, "setInt")))

                            .build()
            );

        }

        private void registerCommand(RegisterCommandsEvent event) {
            Commands.register(event.getDispatcher());
        }

        private static int hateAllPlayers(CommandSourceStack source, Collection<ServerPlayer> players) {
            return 1;
        }

        private static int setTemp(CommandSourceStack sourceStack, int temp) {
            return 1;
        }

        private static int rotateBall(CommandSourceStack sourceStack, float temp) {
            return 1;
        }

        private static int setBloodType(CommandSourceStack source, EnumArgument<BloodType> enumArgument) {
            return 1;
        }

        private enum BloodType {
            A_PLUS,
            B_PLUS,
            O_PLUS,
            AB_PLUS,
            A_MINUS,
            B_MINUS,
            O_MINUS,
            AB_MINUS
        }

    }

    static class CapabilityPreset {
        // first off, we do 2 registries as so:
        static final ItemPresetRegister ITEM_REGISTRY = ItemPresetRegister.create(EpicAPI.MOD_ID);
        static final MovesetRegister MOVESET_REGISTRY = MovesetRegister.create(EpicAPI.MOD_ID);

        // then we build a capability
        // (it has to be a supplier because this runs before epic fight's animation registry?, odd):
        static Supplier<DeferredCapabilityBuilder> BOKKEN_2H = () -> DeferredCapabilityBuilder.newBuilder()
                .newMoveSet(
                        Styles.TWO_HAND,
                        EpicAPI.identifier("bokken_2h"),
                        WeaponCategories.SWORD,
                        ColliderPreset.SWORD,
                        EpicFightProviderConditionals.DEFAULT_2H_WIELD_STYLE,
                        EpicFightSounds.WHOOSH,
                        EpicFightSounds.BLADE_HIT,
                        EpicFightParticles.HIT_BLADE,
                        false,
                        null,
                        EpicFightSkills.SWEEPING_EDGE,
                        Animations.LONGSWORD_AUTO1,
                        Animations.LONGSWORD_AUTO2,
                        Animations.LONGSWORD_AUTO3,
                        Animations.SWORD_DASH,
                        Animations.SWORD_AIR_SLASH
                )
                .newHeavyCombo(
                        Animations.LONGSWORD_LIECHTENAUER_AUTO1,
                        Animations.LONGSWORD_LIECHTENAUER_AUTO2,
                        Animations.LONGSWORD_LIECHTENAUER_AUTO3,
                        Animations.LONGSWORD_DASH,
                        Animations.GREATSWORD_AIR_SLASH
                )
                .addLivingMotionPairs(
                        LivingMotions.IDLE, Animations.BIPED_HOLD_LONGSWORD,
                        LivingMotions.WALK, Animations.BIPED_WALK_LONGSWORD,
                        LivingMotions.RUN, Animations.BIPED_RUN_LONGSWORD
                )
                .addGuardAnimation(Animations.LONGSWORD_GUARD)
                .addGuardHitAnimation(Animations.LONGSWORD_GUARD_HIT)
                .addParryingAnimations(Animations.LONGSWORD_GUARD_ACTIVE_HIT1, Animations.LONGSWORD_GUARD_ACTIVE_HIT2)
                .addCounterAttack(Animations.SWEEPING_EDGE)
                // these will play in order from first to last
                .addParryCounterAttacks(Animations.DANCING_EDGE, Animations.STEEL_WHIRLWIND);
        // if you want another style, you can call newMoveSet() again, or for cleaner code, you can build another capability the same way

       // you can do a static-block, or you can define a DeferredWeapon and DeferredMoveset separately
       static {
            ITEM_REGISTRY.registerWeapon("bokken", () -> BOKKEN_2H.get().buildWeapon());
            MOVESET_REGISTRY.registerMoveset("bokken_2h", () -> BOKKEN_2H.get().buildMoveSet());
       }

        // now you register the bus either by using ILoadableClass or calling the registries in the mod constructor
        public void onModConstructor(IEventBus modBus) {
            ITEM_REGISTRY.register(modBus);
            MOVESET_REGISTRY.register(modBus);
        }
    }

}
