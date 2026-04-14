package M6FGR.epic_api.builders.epicfight;

import M6FGR.epic_api.models.armature.NonHumanoidArmature;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.ApiStatus.Experimental;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.gameasset.Armatures.ArmatureContructor;
import yesman.epicfight.model.armature.HumanoidArmature;

import javax.annotation.Nullable;
import java.util.Map;

@Experimental
public class ArmatureRegistrar {
    private record ArmatureMap(EntityType<?> entityType, AssetAccessor<? extends Armature> armature) {}


    private ArmatureRegistrar() {}

    // Automatically puts the armature to the entity, no need for events, and can accept 3 arguments:
    public static <AR extends Armature> Armatures.ArmatureAccessor<AR> newEntityArmature(EntityType<?> entity, String parsedPath, Armatures.ArmatureContructor<AR> contructor) {
        Armatures.ArmatureAccessor<AR> accessor = newArmature(parsedPath, contructor);
        Armatures.registerEntityTypeArmature(entity, accessor);
        return accessor;
    }

    public static <AR extends Armature> Armatures.ArmatureAccessor<AR> newEntityArmature(EntityType<?> entity, ResourceLocation location, Armatures.ArmatureContructor<AR> contructor) {
        Armatures.ArmatureAccessor<AR> accessor = newArmature(location, contructor);
        Armatures.registerEntityTypeArmature(entity, accessor);
        return accessor;
    }

    public static <AR extends Armature> Armatures.ArmatureAccessor<AR> newEntityArmature(EntityType<?> entity, String modid, String path, Armatures.ArmatureContructor<AR> contructor) {
        Armatures.ArmatureAccessor<AR> accessor = newArmature(modid, path, contructor);
        Armatures.registerEntityTypeArmature(entity, accessor);
        return accessor;
    }

    public static <AR extends Armature> Armatures.ArmatureAccessor<AR> newEntityArmature(EntityType<?> entity, String parsedPath, ArmatureType type) {
        Armatures.ArmatureAccessor<AR> accessor = newArmature(parsedPath, type::apply);
        Armatures.registerEntityTypeArmature(entity, accessor);
        return accessor;
    }

    public static <AR extends Armature> Armatures.ArmatureAccessor<AR> newEntityArmature(EntityType<?> entity, ResourceLocation location, ArmatureType type) {
        Armatures.ArmatureAccessor<AR> accessor = newArmature(location, type::apply);
        Armatures.registerEntityTypeArmature(entity, accessor);
        return accessor;
    }

    public static <AR extends Armature> Armatures.ArmatureAccessor<AR> newEntityArmature(EntityType<?> entity, String modid, String path, ArmatureType type) {
        Armatures.ArmatureAccessor<AR> accessor = newArmature(modid, path, type::apply);
        Armatures.registerEntityTypeArmature(entity, accessor);
        return accessor;
    }

    // needs to be posted via an event (FMLCommonSetupEvent)

    public static <AR extends Armature> Armatures.ArmatureAccessor<AR> newArmature(String parsedPath, Armatures.ArmatureContructor<AR> contructor) {
        ResourceLocation path = ResourceLocation.parse(parsedPath);
        return Armatures.ArmatureAccessor.create(path.getNamespace(), path.getPath(), contructor);
    }

    public static <AR extends Armature> Armatures.ArmatureAccessor<AR> newArmature(ResourceLocation location, Armatures.ArmatureContructor<AR> contructor) {
        return Armatures.ArmatureAccessor.create(location.getNamespace(), location.getPath(), contructor);
    }

    public static <AR extends Armature> Armatures.ArmatureAccessor<AR> newArmature(String modid, String path, Armatures.ArmatureContructor<AR> contructor) {
        return Armatures.ArmatureAccessor.create(modid, path, contructor);
    }

    public static <AR extends Armature> Armatures.ArmatureAccessor<AR> newArmature(String parsedPath, ArmatureType type) {
        ResourceLocation path = ResourceLocation.parse(parsedPath);
        return Armatures.ArmatureAccessor.create(path.getNamespace(), path.getPath(), type::apply);
    }

    public static <AR extends Armature> Armatures.ArmatureAccessor<AR> newArmature(ResourceLocation location, ArmatureType type) {
        return Armatures.ArmatureAccessor.create(location.getNamespace(), location.getPath(), type::apply);
    }

    public static <AR extends Armature> Armatures.ArmatureAccessor<AR> newArmature(String modid, String path, ArmatureType type) {
        return Armatures.ArmatureAccessor.create(modid, path, type::apply);
    }

    public enum ArmatureType {
        HUMANOID_ARMATURE(HumanoidArmature::new),
        NON_HUMANOID_ARMATURE(NonHumanoidArmature::new);

        // Use a specific constructor reference
        private @Nullable ArmatureContructor<? extends Armature> constructor;

        ArmatureType(@Nullable ArmatureContructor<? extends Armature> constructor) {
            this.constructor = constructor;
        }

        @SuppressWarnings("unchecked")
        public <AR extends Armature> AR apply(String name, int jointNumber, Joint joint, Map<String, Joint> jointMap) {
            // We cast the result of the invocation to the generic type AR
            return (AR) this.constructor.invoke(name, jointNumber, joint, jointMap);
        }

        public static ArmatureType of(ArmatureContructor<? extends Armature> constructor) {
            NON_HUMANOID_ARMATURE.constructor = constructor;
            return NON_HUMANOID_ARMATURE;
        }
    }

}