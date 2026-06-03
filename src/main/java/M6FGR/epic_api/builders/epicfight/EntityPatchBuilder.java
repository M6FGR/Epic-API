package M6FGR.epic_api.builders.epicfight;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.ApiStatus.Internal;
import yesman.epicfight.client.renderer.patched.entity.PatchedEntityRenderer;
import yesman.epicfight.world.capabilities.entitypatch.EntityPatch;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings("unchecked")
public class EntityPatchBuilder  {
    private static final List<FullPatchEntry<?>> entries = new ArrayList<>();
    private static final EntityPatchBuilder instance = new EntityPatchBuilder();


    public static <E extends Entity> EntityPatchBuilder newEntityPatch(
            EntityType<E> type,
            Supplier<EntityPatch<E>> patchConstructor,
            PRendererConstructor pRendererConstructor
    ) {
        entries.add(new FullPatchEntry<>(type, patchConstructor, pRendererConstructor));
        return instance;
    }

    @Internal
    public List<FullPatchEntry<?>> getEntries() {
        return entries;
    }

    public record FullPatchEntry<E extends Entity>(
            EntityType<E> type,
            Supplier<EntityPatch<E>> patchConstructor,
            PRendererConstructor pRendererConstructor
    ) {}

    @FunctionalInterface
    public interface PRendererConstructor {
        PatchedEntityRenderer<?, ?, ?, ?> create(EntityRendererProvider.Context context, EntityType<?> type);
    }

}