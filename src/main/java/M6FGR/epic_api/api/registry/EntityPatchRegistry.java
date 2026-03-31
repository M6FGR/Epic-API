package M6FGR.epic_api.api.registry;

import M6FGR.epic_api.api.cls.ILoadableClass;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import yesman.epicfight.api.client.event.EpicFightClientEventHooks;
import yesman.epicfight.api.event.EpicFightEventHooks;
import yesman.epicfight.api.event.types.registry.EntityPatchRegistryEvent;
import yesman.epicfight.client.renderer.patched.entity.PatchedEntityRenderer;
import yesman.epicfight.world.capabilities.entitypatch.EntityPatch;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class EntityPatchRegistry implements ILoadableClass {
    private static final List<FullPatchEntry<?>> REGISTRY_ENTRIES = new ArrayList<>();

    public static <T extends Entity> void newEntityPatch(
            EntityType<T> type,
            Function<T, EntityPatch<T>> patch,
            RendererFactory renderer
    ) {
        REGISTRY_ENTRIES.add(new FullPatchEntry<>(type, patch, renderer));
    }

    private void registerEntityPatches() {
        EpicFightEventHooks.Registry.ENTITY_PATCH.registerEvent(event -> {
            for (FullPatchEntry<?> entry : REGISTRY_ENTRIES) {
                doPatchRegistration(event, entry);
            }
        });
    }

    private <T extends Entity> void doPatchRegistration(EntityPatchRegistryEvent event, FullPatchEntry<T> entry) {
        event.registerEntityPatch(entry.type(), entry.patchConstructor());
    }

    private void registerRenderers() {
        EpicFightClientEventHooks.Registry.ADD_PATCHED_ENTITY.registerEvent(event -> {
            for (FullPatchEntry<?> entry : REGISTRY_ENTRIES) {
                event.addPatchedEntityRenderer(entry.type(), (entityType) ->
                        entry.rendererFactory().create(event.getContext(), entityType)
                );
            }
        });
    }

    private record FullPatchEntry<T extends Entity>(
            EntityType<T> type,
            Function<T, EntityPatch<T>> patchConstructor,
            RendererFactory rendererFactory
    ) {}

    @FunctionalInterface
    public interface RendererFactory {
        PatchedEntityRenderer<?, ?, ?, ?> create(EntityRendererProvider.Context context, EntityType<?> type);
    }

    // patches are always common, unlike renderers, client only
    @Override
    public void onModCommonEvents(FMLCommonSetupEvent event) {
        this.registerEntityPatches();
    }

    @Override
    public void onModClientEvents(FMLClientSetupEvent event) {
        this.registerRenderers();
    }
}