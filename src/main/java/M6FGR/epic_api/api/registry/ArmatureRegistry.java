package M6FGR.epic_api.api.registry;

import M6FGR.epic_api.api.cls.ILoadableClass;
import M6FGR.epic_api.main.EpicAPI;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.gameasset.Armatures;

import java.util.ArrayList;
import java.util.List;

public class ArmatureRegistry implements ILoadableClass {
    record ArmatureBinding(EntityType<?> entityType, AssetAccessor<? extends Armature> armature) {}
    static final List<ArmatureBinding> BINDINGS = new ArrayList<>();


    public static <A extends Armature> Armatures.ArmatureAccessor<A> newEntityArmature(EntityType<?> entity, String parsedPath, Armatures.ArmatureContructor<A> contructor) {
        Armatures.ArmatureAccessor<A> accessor = newArmature(parsedPath, contructor);
        putForEntity(entity, accessor);
        return accessor;
    }

    public static <A extends Armature> Armatures.ArmatureAccessor<A> newEntityArmature(EntityType<?> entity, ResourceLocation location, Armatures.ArmatureContructor<A> contructor) {
        Armatures.ArmatureAccessor<A> accessor = newArmature(location, contructor);
        putForEntity(entity, accessor);
        return accessor;
    }

    public static <A extends Armature> Armatures.ArmatureAccessor<A> newEntityArmature(EntityType<?> entity, String modid, String path, Armatures.ArmatureContructor<A> contructor) {
        Armatures.ArmatureAccessor<A> accessor = newArmature(modid, path, contructor);
        putForEntity(entity, accessor);
        return accessor;
    }


    public static <A extends Armature> Armatures.ArmatureAccessor<A> newArmature(String parsedPath, Armatures.ArmatureContructor<A> contructor) {
        ResourceLocation path = ResourceLocation.parse(parsedPath);
        return Armatures.ArmatureAccessor.create(path.getNamespace(), path.getPath(), contructor);
    }

    public static <A extends Armature> Armatures.ArmatureAccessor<A> newArmature(ResourceLocation location, Armatures.ArmatureContructor<A> contructor) {
        return Armatures.ArmatureAccessor.create(location.getNamespace(), location.getPath(), contructor);
    }

    public static <A extends Armature> Armatures.ArmatureAccessor<A> newArmature(String modid, String path, Armatures.ArmatureContructor<A> contructor) {
        return Armatures.ArmatureAccessor.create(modid, path, contructor);
    }

    public static void putForEntity(EntityType<?> entityType, AssetAccessor<? extends Armature> armatureAccessor) {
        BINDINGS.add(new ArmatureBinding(entityType, armatureAccessor));
    }

    private void registerArmatures() {
        if (BINDINGS.isEmpty()) {
            EpicAPI.LOGGER.warn("No armature bindings were found!");
            return;
        }

        for (ArmatureBinding binding : BINDINGS) {
            ResourceLocation path = binding.armature() != null ? binding.armature().registryName() : null;
            if (binding.entityType() == null) {
                EpicAPI.LOGGER.error("Cannot register the armature: {}, entity put for it is null!", path);
                return;
            }
            if (binding.armature() == null) {
                EpicAPI.LOGGER.error("Cannot register the armature for entity: {}, armature is null!", binding.entityType().toString());
                return;
            }

            EpicAPI.LOGGER.info("Registering armature: {} for {}",
                    path,
                    binding.entityType().toShortString()
            );

            try {
                Armatures.registerEntityTypeArmature(binding.entityType(), binding.armature());
                EpicAPI.LOGGER.info("Registered armature: {} for: {}", path, binding.entityType().toString());
            } catch (Exception e) {
                EpicAPI.LOGGER.error("Failed to apply armature: {} for: {}, {}",
                        path, binding.entityType().toShortString(), e.getMessage());
            }
        }
    }

    @Override
    public void onModCommonEvents(FMLCommonSetupEvent event) {
        this.registerArmatures();
    }
}