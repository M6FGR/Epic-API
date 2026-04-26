package M6FGR.epic_api.events.entity;

import M6FGR.epic_api.builders.epicfight.EntityPatchBuilder;
import M6FGR.epic_api.builders.epicfight.EntityPatchBuilder.FullPatchEntry;
import M6FGR.epic_api.builders.epicfight.EntityPatchBuilder.PRendererConstructor;
import M6FGR.epic_api.cls.ILoadableClass;
import M6FGR.epic_api.main.EpicAPI;
import com.google.common.collect.Maps;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.IModBusEvent;
import yesman.epicfight.api.client.forgeevent.PatchedRenderersEvent;
import yesman.epicfight.api.forgeevent.EntityPatchRegistryEvent;
import yesman.epicfight.world.capabilities.entitypatch.EntityPatch;

import java.util.Map;
import java.util.function.Supplier;


public class EntityPatchBuilderRegistryEvent extends Event implements IModBusEvent, ILoadableClass {
    private static final Map<FullPatchEntry<?>, PRendererConstructor> ENTITY_PATCH_MAP = Maps.newHashMap();

    public <E extends Entity> void registerFrom(EntityPatchBuilder<E> registrar) {
        for (FullPatchEntry<?> entry : registrar.getEntries()) {
            ENTITY_PATCH_MAP.put(entry, entry.pRendererConstructor());
        }
    }

    private void onEntityPatchRegistry(EntityPatchRegistryEvent event) {
        ENTITY_PATCH_MAP.keySet().forEach(entry -> registerSingle(event, entry));
    }

    private void onPatchedRenderers(PatchedRenderersEvent.Add event) {
        ENTITY_PATCH_MAP.keySet().forEach(entry -> addSingleRenderer(event, entry));
    }

    @SuppressWarnings("unchecked")
    private static <E extends Entity> void registerSingle(EntityPatchRegistryEvent event, FullPatchEntry<?> entry) {
        FullPatchEntry<E> castedEntry = (FullPatchEntry<E>) entry;
        event.getTypeEntry().put(castedEntry.type(), entityType -> (Supplier<EntityPatch<?>>) castedEntry.patchConstructor().get());
    }

    @SuppressWarnings("unchecked")
    private static <E extends Entity> void addSingleRenderer(PatchedRenderersEvent.Add event, FullPatchEntry<?> entry) {
        FullPatchEntry<E> castedEntry = (FullPatchEntry<E>) entry;

        event.addPatchedEntityRenderer(castedEntry.type(), (entityType) ->
                castedEntry.pRendererConstructor().create(event.getContext(), entityType)
        );
    }

    @Override
    public void onModConstructor(IEventBus modBus) {
        modBus.addListener(this::onEntityPatchRegistry);
    }

    @Override
    public void onModClientConstructor(IEventBus modBus) {
        modBus.addListener(this::onEntityPatchRegistry);
    }
}