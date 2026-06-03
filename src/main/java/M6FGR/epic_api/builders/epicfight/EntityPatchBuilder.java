package M6FGR.epic_api.builders.epicfight;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.ApiStatus.Internal;
import yesman.epicfight.client.renderer.patched.entity.PatchedEntityRenderer;
import yesman.epicfight.world.capabilities.entitypatch.EntityPatch;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * A Singleton-style utility builder used to register Epic Fight entity patches
 * and their corresponding patched renderers cleanly.
 */
@SuppressWarnings("unchecked")
public class EntityPatchBuilder {

    // Global registry storage holding all registered entity patch data entries
    private static final List<FullPatchEntry<?>> entries = new ArrayList<>();

    // The internal singleton instance returned by the factory methods to allow chaining
    private static final EntityPatchBuilder instance = new EntityPatchBuilder();

    // Private constructor enforces the static utility pattern, preventing external instantiation
    private EntityPatchBuilder() {
    }

    /**
     * Statically registers a standard entity patch and its custom renderer to the global pipeline.
     *
     * @param type The vanilla Minecraft EntityType target (e.g., EntityType.ZOMBIE)
     * @param patchConstructor A method reference or lambda creating the patch (e.g., ZombiePatch::new)
     * @param pRendererConstructor A functional interface constructing the patched renderer instance
     * @return The global EntityPatchBuilder instance for continuous fluent chaining
     */
    public static <E extends LivingEntity> EntityPatchBuilder newEntityPatch(
            EntityType<E> type,
            Function<E, ? extends EntityPatch<E>> patchConstructor,
            PRendererConstructor pRendererConstructor
    ) {
        // Erasing generic structures through a raw Function assignment eliminates compilation conflicts
        Function rawConstructor = patchConstructor;
        entries.add(new FullPatchEntry<>(type, (Function<Entity, ? extends EntityPatch<Entity>>) rawConstructor, pRendererConstructor));
        return instance;
    }

    /**
     * Unsafe registration variant using looser variance bounds.
     * Use this when handling entities with complex inheritance structures or non-standard class hierarchies.
     */
    public static <E extends LivingEntity> EntityPatchBuilder newEntityPatchUnsafe(
            EntityType<E> type,
            Function<? super E, ? extends EntityPatch<? extends E>> patchConstructor,
            PRendererConstructor pRendererConstructor
    ) {
        // Erasing the wildcard captures (? super E, ? extends E) through raw assignment fixes the line 66 compiler error
        Function rawConstructor = patchConstructor;
        entries.add(new FullPatchEntry<>(type, (Function<Entity, ? extends EntityPatch<Entity>>) rawConstructor, pRendererConstructor));
        return instance;
    }

    /**
     * Exposes the internal registered entries.
     * Used by the mod's main setup events to cycle through and submit definitions to Epic Fight.
     */
    @Internal
    public List<FullPatchEntry<?>> getEntries() {
        return entries;
    }

    /**
     * Data holder representing a complete definition package for a modded Epic Fight entity tracking system.
     */
    public record FullPatchEntry<E extends Entity> (
            EntityType<E> type,
            // Uses base Entity bounds internally, so it can store both safe and unsafe variants uniformly
            Function<Entity, ? extends EntityPatch<Entity>> patchConstructor,
            PRendererConstructor pRendererConstructor
    ) {}

    /**
     * Functional bridge providing the vanilla rendering context required to instantiate
     * patched Epic Fight renderers on the client distribution.
     */
    @FunctionalInterface
    public interface PRendererConstructor {
        PatchedEntityRenderer<?, ?, ?, ?> create(EntityRendererProvider.Context context, EntityType<?> type);
    }
}