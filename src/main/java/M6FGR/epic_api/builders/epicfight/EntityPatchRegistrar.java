package M6FGR.epic_api.builders.epicfight;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.ApiStatus.Experimental;
import yesman.epicfight.client.renderer.patched.entity.PatchedEntityRenderer;
import yesman.epicfight.world.capabilities.entitypatch.EntityPatch;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
@SuppressWarnings("unchecked")
@Experimental
public class EntityPatchRegistrar<T extends Entity>  {
    private static final EntityPatchRegistrar<Entity> INS = new EntityPatchRegistrar<>();
    private final List<FullPatchEntry<?>> entries = new ArrayList<>();

    private EntityPatchRegistrar() {}

    public static <E extends Entity> EntityPatchRegistrar<E> get() {
        return (EntityPatchRegistrar<E>) INS;
    }

    public <E extends T> EntityPatchRegistrar<E> newEntityPatch(
            EntityType<E> type,
            Function<E, EntityPatch<E>> patch,
            RendererFactory renderer
    ) {
        this.entries.add(new FullPatchEntry<>(type, patch, renderer));
        return (EntityPatchRegistrar<E>) this;
    }

    public List<FullPatchEntry<?>> getEntries() {
        return this.entries;
    }

    public record FullPatchEntry<E extends Entity>(
            EntityType<E> type,
            Function<E, EntityPatch<E>> patchConstructor,
            RendererFactory rendererFactory
    ) {}

    @FunctionalInterface
    public interface RendererFactory {
        PatchedEntityRenderer<?, ?, ?, ?> create(EntityRendererProvider.Context context, EntityType<?> type);
    }

}