package M6FGR.epic_api.events.item;

import M6FGR.epic_api.events.EpicAPIEventHooks;
import M6FGR.epic_api.events.IEventHook;
import M6FGR.epic_api.builders.epicfight.MoveSetBuilder;
import net.minecraft.resources.ResourceLocation;
import yesman.epicfight.api.event.EpicFightEventHooks;
import yesman.epicfight.api.event.Event;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ExCapCapabilityRegistryEventHook extends Event implements IEventHook {
    private final Map<ResourceLocation, MoveSetBuilder> registryMap = new HashMap<>();
    private ResourceLocation id;


    public void register(ResourceLocation id, MoveSetBuilder registry) {
        registry.build(id);
        this.id = id;
        this.registryMap.put(id, registry);
    }

    public Map<ResourceLocation, MoveSetBuilder> getRegistryMap() {
        return Collections.unmodifiableMap(this.registryMap);
    }

    @Override
    public void post() {
        EpicAPIEventHooks.Registry.MOVE_SET_CAPABILITY.post(this);

        EpicFightEventHooks.Registry.EX_CAP_DATA_CREATION.registerEvent(event ->
                this.registryMap.values().forEach(reg -> event.addData(reg.getDataEntry())));

        EpicFightEventHooks.Registry.EX_CAP_MOVESET_REGISTRY.registerEvent(event ->
                this.registryMap.values().forEach(reg -> event.addMoveSet(reg.build(this.id))));

        EpicFightEventHooks.Registry.EX_CAP_BUILDER_CREATION.registerEvent(event ->
                this.registryMap.values().forEach(reg -> event.addBuilder(reg.getBuilderEntry())));

        EpicFightEventHooks.Registry.EX_CAP_DATA_POPULATION.registerEvent(event ->
                this.registryMap.values().forEach(reg ->
                event.registerData(reg.getBuilderEntry().id(), reg.getDataEntry().id())));

        EpicFightEventHooks.Registry.WEAPON_CAPABILITY_PRESET.registerEvent(event ->
                this.registryMap.forEach((id, reg) -> {
            // We use the ID passed during registration (e.g., "epic_api:bokken")
            // and map it to a provider that builds the WeaponCapability
            event.getTypeEntry().put(id, item -> reg.getWeaponCapability());
        }));
    }
}