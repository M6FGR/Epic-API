package dev.m6fgr.epic_api.main;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import dev.m6fgr.epic_api.animation.types.SimpleAttackAnimation;
import dev.m6fgr.epic_api.animation.types.SimpleAttackAnimation.TrailColor;
import dev.m6fgr.epic_api.animation.types.SimpleAttackAnimation.TrailPreset;
import dev.m6fgr.epic_api.animation.types.SimpleMovementAnimation;
import dev.m6fgr.epic_api.animation.types.SimpleStaticAnimation;
import dev.m6fgr.epic_api.animation.types.joints.JointMasks;
import dev.m6fgr.epic_api.builders.epicfight.ArmatureBuilder;
import dev.m6fgr.epic_api.builders.epicfight.ArmatureBuilder.ArmatureTypes;
import dev.m6fgr.epic_api.builders.epicfight.EntityPatchBuilder;
import dev.m6fgr.epic_api.builders.epicfight.MeshBuilder;
import dev.m6fgr.epic_api.builders.epicfight.MeshBuilder.MeshTypes;
import dev.m6fgr.epic_api.builders.epicfight.excap.deferred.DeferredCapabilityBuilder;
import dev.m6fgr.epic_api.builders.minecraft.CommandsBuilder;
import dev.m6fgr.epic_api.builders.minecraft.GameRulesBuilder;
import dev.m6fgr.epic_api.builders.minecraft.GameRulesBuilder.EnumValue;
import dev.m6fgr.epic_api.builders.minecraft.GameRulesBuilder.SynchronizedGameRule;
import dev.m6fgr.epic_api.builders.minecraft.ItemsBuilder;
import dev.m6fgr.epic_api.cls.loadable.ILoadableClass;
import dev.m6fgr.epic_api.cls.loadable.enums.LoadableEnum;
import dev.m6fgr.epic_api.cls.loadable.marks.Compatibility;
import dev.m6fgr.epic_api.compat.plugin.CompatibilityPlugin;
import dev.m6fgr.epic_api.events.dispatch.dispatchers.extras.EventPriority;
import dev.m6fgr.epic_api.events.epic_api.EpicAPIDispatchableEvents;
import dev.m6fgr.epic_api.events.epic_api.dispatch.DispatchEvent;
import dev.m6fgr.epic_api.events.mc.MinecraftDispatchableEvents;
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
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
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
class EpicAPIExamples {
    static class Armature {
        // these methods can accept 3 types of paths:
        // mod-id:path,
        // mod-id, path,
        // ResourceLocation#fromNamespaceAndPath(mod-id, path)
        public static final ArmatureAccessor<HumanoidArmature> PLACEHOLDER_ARMATURE = ArmatureBuilder.newEntityArmature(EntityType.ZOMBIE, "epicfight:entity/biped", ArmatureTypes.HUMANOID);
        // Needs to be posted via FMLCommonSetupEvent
        public static final ArmatureAccessor<HumanoidArmature> PLACEHOLDER_ARMATURE_NO_ENTITY = ArmatureBuilder.newArmature("epicfight:entity/biped", ArmatureTypes.HUMANOID);
        // if you couldn't find what type you wish for, you could use the constructor as so:
        public static final ArmatureAccessor<HumanoidArmature> PLACEHOLDER_ARMATURE_CUSTOM_CONSTRUCTOR = ArmatureBuilder.newArmature("epicfight:entity/biped", ArmatureTypes.of(HumanoidArmature::new));

    }

    static class Mesh {
        // same thing as the armature path arguments, it accepts 3 arguments

        // MeshType is holding 4 types of mesh types: (SkinnedMesh, ClassicMesh, CompositeMesh, HumanoidMesh)
        public static final MeshAccessor<HumanoidMesh> PLACEHOLDER_MESH = MeshBuilder.newMesh("epicfight:entity/biped", MeshTypes.HUMANOID);
        // Or if you couldn't find what you wish for, you could use the constructor as so:
        public static final MeshAccessor<WitherMesh> PLACEHOLDER_MESH_CUSTOM_CONSTRUCTOR = MeshBuilder.newMesh("epicfight:entity/wither", MeshTypes.of(WitherMesh::new));
        // This automatically detects the mesh type inside the JSON file itself, check JsonAssetLoader#loadMesh()
        public static final MeshAccessor<SkinnedMesh> PLACEHOLDER_MESH_JSON_LOADER = MeshBuilder.newMesh("epicfight:layer/default_cape", MeshTypes.JSON_LOADER);
    }

    static class EntityPatch {

        public static final EntityPatchBuilder HUMANOID_PATCH = EntityPatchBuilder.newEntityPatch(EntityType.ZOMBIE, ZombiePatch::new, (context, type) -> new PHumanoidRenderer<>(Meshes.BIPED ,context, type));
        public static final EntityPatchBuilder NON_HUMANOID_PATCH = EntityPatchBuilder.newEntityPatch(EntityType.IRON_GOLEM, IronGolemPatch::new, PIronGolemRenderer::new);
        public static final EntityPatchBuilder PLAYER_PATCH = EntityPatchBuilder.newEntityPatchUnsafe(EntityType.PLAYER, player -> new ServerPlayerPatch((ServerPlayer) player), PPlayerRenderer::new);


        // you can either use ILoadableClass or call this in the modCommonEvents in your main class:
        public static void registerPatches() {
            // NEVER initialize them outside of this method if you're going to use ILoadableClass
            // use Epic-API's event hooks to work, not epic fight's!
            EpicAPIDispatchableEvents.Registry.ENTITY_PATCH.dispatchEvent(event -> {
                event.registerFrom(HUMANOID_PATCH);
                event.registerFrom(NON_HUMANOID_PATCH);
                event.registerFrom(PLAYER_PATCH);
            });
        }

    }

    static class Items {
        private static final ItemsBuilder REGISTRY = ItemsBuilder.createRegistry("modid");

        public static final DeferredHolder<Item, Item> PLACEHOLDER_ITEM = REGISTRY.newItem("item1", Item::new);

        public static final DeferredHolder<Item, Item> PLACEHOLDER_ITEM_PROPS = REGISTRY.newItem("item2", Item::new, properties -> properties
                .durability(1990)
                .rarity(Rarity.EPIC)
                .attributes(ItemAttributeModifiers.builder().build()));


    }

    static class GameRules {
        // as following, this is how simple it is to register gamerules:

        // non-synchronized is a gamerule applies to the client only (executor-only), e.g -> /gamerule chatDebug true
        public static final SynchronizedGameRule<EnumValue<ChatFont>> PLACEHOLDER_ENUM = GameRulesBuilder.newEnum("enum", Category.CHAT, ChatFont.BOLD);

        // synchronized is basically a gamerule that applies to all (every player in the world), e.g. -> /gamerule switchableCamera false (now they can't switch camera types by pressing F5)
        public static final SynchronizedGameRule<EnumValue<ChatFont>> PLACEHOLDER_ENUM_SYNC = GameRulesBuilder.newEnum("enumSynced", Category.CHAT, ChatFont.BOLD, true);
        // the same thing applies to all the gamerules below, just a different key type
        public static final SynchronizedGameRule<IntegerValue> PLACEHOLDER_INT = GameRulesBuilder.newInteger("int", Category.CHAT, 1);

        public static final SynchronizedGameRule<IntegerValue> PLACEHOLDER_INT_SYNC = GameRulesBuilder.newInteger("intSynced", Category.CHAT, 1, true);

        public static final SynchronizedGameRule<BooleanValue> PLACEHOLDER_BOOL = GameRulesBuilder.newBoolean("bool", Category.CHAT, true);

        public static final SynchronizedGameRule<BooleanValue> PLACEHOLDER_BOOL_SYNC = GameRulesBuilder.newBoolean("boolSynced", Category.CHAT, true, true);


        enum ChatFont {
            BOLD,
            UNDERLINE,
            DEFAULT;
        }
    }

    static class Animation {
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
    static class CompatibilityClass {// implements ILoadableClass
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

                            .newFloat("setFlt", 0.1F, 30.5F)
                            .executes(context -> rotateBall(context.getSource(), FloatArgumentType.getFloat(context, "setFlt")))

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

    static class CapabilityPreset implements ILoadableClass {
        // first off, we do 2 registries as so:
        static final ItemPresetRegister ITEM_REGISTRY = ItemPresetRegister.create(EpicAPIMod.MOD_ID);
        static final MovesetRegister MOVESET_REGISTRY = MovesetRegister.create(EpicAPIMod.MOD_ID);

        // then we build a capability
        // (it has to be a supplier because this runs before epic fight's animation registry?, odd):
        static final Supplier<DeferredCapabilityBuilder> BOKKEN_2H = () -> DeferredCapabilityBuilder.newBuilder()
                .newMoveSet(
                        Styles.TWO_HAND,
                        EpicAPI.getInstance().identifier("bokken_2h"),
                        WeaponCategories.SWORD,
                        ColliderPreset.SWORD,
                        EpicFightProviderConditionals.DEFAULT_2H_WIELD_STYLE,
                        EpicFightSounds.WHOOSH,
                        EpicFightSounds.BLADE_HIT,
                        EpicFightParticles.HIT_BLADE,
                        // holdableInOffHand
                        false,
                        // passiveSkill
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
        @Override
        public void onModConstruct(IEventBus modBus) {
            ITEM_REGISTRY.register(modBus);
            MOVESET_REGISTRY.register(modBus);
        }

        @Override
        public boolean ignoreNotLoadedWarning() {
            return true;
        }
    }

    @EventBusSubscriber(modid = EpicAPIMod.MOD_ID)
    static class DispatchableEvents {
        // a dispatchable event is almost like an event hook, but more flexible, as so:
        private static void onDispatch() {
            MinecraftDispatchableEvents.Player.JOIN_CLIENT.dispatchEvent(event -> EpicAPIMod.debugIfDevSide("Hello from [{}]", event.getEventClsName()));
        }
        // the method above can be called anywhere, but it's best to call it in a life cycle event, or the mod's constructor
        // or if needed, and the event is hybrid (having the @HybridEvent annotation), you can use neo's event subscribers
        @SubscribeEvent
        // This event is empty, used to dispatch other events, posted in the mod's constructor
        public static void onDispatch(DispatchEvent event) {
            MinecraftDispatchableEvents.Client.CLIENT_START_PRE.dispatchEvent(joinEvent -> EpicAPIMod.debugIfDevSide("Hello from [{}] subscribed!", event.getEventClsName()));
        }
        // IF the event is not hybrid and used in a subscriber, it'd do nothing
        // An event with the @HybridEvent annotation is automatically posted to neo's event bus API (based on the event bus passed in the annotation)
        // and since the annotation is not present, the event is never posted anywhere, resulting in an event that's not doing anything

        // now, we cover the priority system
        private static void onClientStart() {
            // By default, the priority is always middle if left empty
            MinecraftDispatchableEvents.Client.CLIENT_START_PRE.dispatchEvent(event ->
                    EpicAPIMod.debugIfDevSide("Hello from client start with the {} priority", event.getPriority().toString().toLowerCase())
            );
            // But here, we make it the highest, so it dispatches before the event above
            MinecraftDispatchableEvents.Client.CLIENT_START_PRE.dispatchEvent(event ->
                    EpicAPIMod.debugIfDevSide("Hello from client start with the {} priority", event.getPriority().toString().toLowerCase())
            , EventPriority.HIGHEST
            );
            // But here, if we put the override priority, it cancels the 2 above and overrides them, completely changing the result of the event itself
            // Note that it can be only overridden if the boolean DispatchableEvent#canBeOverridden is true
            // The only un-overridable event is DispatchEvent used above
            MinecraftDispatchableEvents.Client.CLIENT_START_PRE.dispatchEvent(event ->
                     EpicAPIMod.debugIfDevSide("Hello from client start with the {} priority", event.getPriority().toString().toLowerCase())
            , EventPriority.OVERRIDE
            );
        }
    }

    static class LoadableEnumsAndClasses implements ILoadableClass {
        // Loadable enums and classes API is an API focusing on initializing the class/enum in a simple way
        // Here's how it works:

        // in this method, it passes the mod bus directly from the constructor, and running the method as well
        @Override
        public void onModConstruct(IEventBus modBus) {
            ILoadableClass.super.onModConstruct(modBus);
        }

        // in this method, it also passed a bus, but it's Neo's bus instead, same running logic
        @Override
        public void onGameConstruct(IEventBus neoBus) {
            ILoadableClass.super.onGameConstruct(neoBus);
        }

        // in this one though, it works the same way as ILoadableClass#onModConstruct, but it only runs on the client side
        // will not be invoked in the server side
        @Override
        public void onModClientConstruct(IEventBus modBus) {
            ILoadableClass.super.onModClientConstruct(modBus);
        }

        // same logic here, except it passes Neo's event bus
        @Override
        public void onGameClientConstruct(IEventBus neoBus) {
            ILoadableClass.super.onGameClientConstruct(neoBus);
        }

        @Override
        public boolean ignoreNotLoadedWarning() {
            return true;
        }

        // To load this class, you call LoadableClassManager#loadClass(IEventBus, Class<? extends ILoadableClass>[]) in your mod's constructor
        // each class loaded will automatically invoke and run these methods, easily registering and running code instead of calling everything in the mod's constructor

        // Now, we move to enums
        // In here, we pass the loadable class inside the enum itself, and then loading them without calling anything
        // Check EpicAPILoadableEnums for example
        public enum ExampleLoadableEnum implements LoadableEnum {
            LOADABLE_ENUM(LoadableEnumsAndClasses.class);

            private final Class<? extends ILoadableClass> loadableClass;
            private final boolean clientOnly;

            ExampleLoadableEnum(Class<? extends ILoadableClass> loadableClass, boolean clientOnly) {
                this.loadableClass = loadableClass;
                this.clientOnly = clientOnly;
            }

            ExampleLoadableEnum(Class<? extends ILoadableClass> loadableClass) {
                this(loadableClass, false);
            }

            // The main class, passed and loaded via this enum
            @Override
            public Class<? extends ILoadableClass> getLoadableClass() {
                return this.loadableClass;
            }

            // a filter for client loadable classes
            @Override
            public boolean isClientOnly() {
                return this.clientOnly;
            }
        }
        // To load this enum, you call LoadableClassManager#loadEnum(IEventBus, Enum.values()); in your mod's constructor
    }

    static class Plugins {
        // The API also cover plugins, known for compatibility between mods
        // Here's how the plugins work here:
        public static class ExamplePlugin implements CompatibilityPlugin {
            // The compatibility mod for this plugin, if empty or not present, this plugin will not be initialized
            @Override
            public String getTargetModID() {
                return EpicAPI.MOD_ID;
            }
            // Checks if this plugin should only be a client side plugin
            @Override
            public boolean isClientOnly() {
                return false;
            }
            // The initialization of this plugin, it passes the mod's bus and Neo's bus, they're optional
            @Override
            public void onInitialize(IEventBus neoBus, IEventBus modBus) {}

            // And to load this plugin, you call this in your mod's constructor:
            // CompatibilityPluginsManager.registerPlugins(null, ExamplePlugin.class);

        }
    }

}
