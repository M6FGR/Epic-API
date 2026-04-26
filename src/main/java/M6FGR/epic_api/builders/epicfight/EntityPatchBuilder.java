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
public class EntityPatchBuilder<T extends Entity>  {
    private static EntityPatchBuilder<?> INS = new EntityPatchBuilder<>();
    private final List<FullPatchEntry<?>> entries = new ArrayList<>();

    private EntityPatchBuilder() {
        INS = this;
    }

    public static <E extends Entity> EntityPatchBuilder<E> get() {
        return (EntityPatchBuilder<E>) INS;
    }

    public <E extends T> EntityPatchBuilder<E> newEntityPatch(
            EntityType<E> type,
            Supplier<EntityPatch<E>> patchConstructor,
            PRendererConstructor pRendererConstructor
    ) {
        this.entries.add(new FullPatchEntry<>(type, patchConstructor, pRendererConstructor));
        return (EntityPatchBuilder<E>) this;
    }

    @Internal
    public List<FullPatchEntry<?>> getEntries() {
        return this.entries;
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