package M6FGR.epic_api.builders.epicfight;

import M6FGR.epic_api.models.armature.NonHumanoidArmature;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.ApiStatus.Internal;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.gameasset.Armatures.ArmatureAccessor;
import yesman.epicfight.gameasset.Armatures.ArmatureContructor;
import yesman.epicfight.model.armature.HumanoidArmature;

import javax.annotation.Nullable;
import java.util.Map;

public class ArmatureBuilder {

    private ArmatureBuilder() {}

    // Automatically puts the armature to the entity, no need for events, and can accept 3 arguments:
    public static <AR extends Armature> Armatures.ArmatureAccessor<AR> newEntityArmature(EntityType<?> entity, String parsedPath, Armatures.ArmatureContructor<AR> contructor) {
        ResourceLocation parsed = ResourceLocation.parse(parsedPath);
        return newEntityArmatureI(entity, parsed.getNamespace(), parsed.getPath(), contructor);
    }

    public static <AR extends Armature> Armatures.ArmatureAccessor<AR> newEntityArmature(EntityType<?> entity, ResourceLocation location, Armatures.ArmatureContructor<AR> contructor) {
        return newEntityArmatureI(entity, location.getNamespace(), location.getPath(), contructor);
    }

    public static <AR extends Armature> Armatures.ArmatureAccessor<AR> newEntityArmature(EntityType<?> entity, String modid, String path, Armatures.ArmatureContructor<AR> contructor) {
        return newEntityArmatureI(entity, modid, path, contructor);
    }

    public static <AR extends Armature> Armatures.ArmatureAccessor<AR> newEntityArmature(EntityType<?> entity, String parsedPath, ArmatureType type) {
        ResourceLocation parsed = ResourceLocation.parse(parsedPath);
        return newEntityArmatureI(entity, parsed.getNamespace(), parsed.getPath(), type::apply);
    }

    public static <AR extends Armature> Armatures.ArmatureAccessor<AR> newEntityArmature(EntityType<?> entity, ResourceLocation location, ArmatureType type) {
        return newEntityArmatureI(entity, location.getNamespace(), location.getPath(), type::apply);
    }

    public static <AR extends Armature> Armatures.ArmatureAccessor<AR> newEntityArmature(EntityType<?> entity, String modid, String path, ArmatureType type) {
        return newEntityArmatureI(entity, modid, path, type::apply);
    }

    // needs to be posted via an event (FMLCommonSetupEvent)

    public static <AR extends Armature> Armatures.ArmatureAccessor<AR> newArmature(String parsedPath, Armatures.ArmatureContructor<AR> contructor) {
        ResourceLocation parsed = ResourceLocation.parse(parsedPath);
        return newArmatureI(parsed.getNamespace(), parsed.getPath(), contructor);
    }

    public static <AR extends Armature> Armatures.ArmatureAccessor<AR> newArmature(ResourceLocation location, Armatures.ArmatureContructor<AR> contructor) {
        return newArmatureI(location.getNamespace(), location.getPath(), contructor);
    }

    public static <AR extends Armature> Armatures.ArmatureAccessor<AR> newArmature(String modid, String path, Armatures.ArmatureContructor<AR> contructor) {
        return newArmatureI(modid, path, contructor);
    }

    public static <AR extends Armature> Armatures.ArmatureAccessor<AR> newArmature(String parsedPath, ArmatureType type) {
        ResourceLocation parsed = ResourceLocation.parse(parsedPath);
        return newArmatureI(parsed.getNamespace(), parsed.getPath(), type::apply);
    }

    public static <AR extends Armature> Armatures.ArmatureAccessor<AR> newArmature(ResourceLocation location, ArmatureType type) {
        return newArmatureI(location.getNamespace(), location.getPath(), type::apply);
    }

    public static <AR extends Armature> Armatures.ArmatureAccessor<AR> newArmature(String modid, String path, ArmatureType type) {
        return newArmatureI(modid, path, type::apply);
    }

    @Internal
    private static <AR extends Armature> ArmatureAccessor<AR> newArmatureI(String modid, String path, ArmatureContructor<AR> contructor) {
        return ArmatureAccessor.create(modid, path, contructor);
    }

    @Internal
    private static <AR extends Armature> ArmatureAccessor<AR> newEntityArmatureI(EntityType<?> entity, String modid, String path, ArmatureContructor<AR> contructor) {
        ArmatureAccessor<AR> accessor = newArmatureI(modid, path, contructor);
        Armatures.registerEntityTypeArmature(entity, accessor);
        return accessor;
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
        <AR extends Armature> AR apply(String name, int jointNumber, Joint joint, Map<String, Joint> jointMap) {
            // We cast the result of the invocation to the generic type AR
            return (AR) this.constructor.invoke(name, jointNumber, joint, jointMap);
        }

        public static ArmatureType of(ArmatureContructor<? extends Armature> constructor) {
            NON_HUMANOID_ARMATURE.constructor = constructor;
            return NON_HUMANOID_ARMATURE;
        }
    }

}