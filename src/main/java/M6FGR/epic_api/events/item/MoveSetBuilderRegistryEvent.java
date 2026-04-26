package M6FGR.epic_api.events.item;

import M6FGR.epic_api.builders.epicfight.MoveSetBuilder;
import M6FGR.epic_api.cls.ILoadableClass;
import M6FGR.epic_api.main.EpicAPI;
import com.google.common.collect.Maps;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.IModBusEvent;
import yesman.epicfight.api.ex_cap.core.events.ExCapBuilderCreationEvent;
import yesman.epicfight.api.ex_cap.core.events.ExCapDataRegistrationEvent;
import yesman.epicfight.api.ex_cap.core.events.ExCapMovesetRegistryEvent;
import yesman.epicfight.api.ex_cap.core.events.ExCapabilityBuilderPopulationEvent;
import yesman.epicfight.api.forgeevent.WeaponCapabilityPresetRegistryEvent;

import java.util.Map;
public class MoveSetBuilderRegistryEvent extends Event implements IModBusEvent, ILoadableClass {
    private static final Map<ResourceLocation, MoveSetBuilder> registryMap = Maps.newHashMap();


    public void register(ResourceLocation id, MoveSetBuilder registry) {
        if (registryMap.containsKey(id)) {
            throw new IllegalArgumentException("MoveSet ID: " + id + "is already registered!");
        }
        registry.build(id);
        registryMap.put(id, registry);
    }

    private void registerWeaponCapability(WeaponCapabilityPresetRegistryEvent event) {
        registryMap.forEach((rl, moveSet) -> event.getTypeEntry().put(rl, item -> moveSet.getWeaponCapability()));
    }
    private void registerExCapData(ExCapDataRegistrationEvent event) {
        registryMap.values().forEach(moveSet -> event.addData(moveSet.getDataEntry()));
    }
    private void registerExCapMoveSet(ExCapMovesetRegistryEvent event) {
        registryMap.forEach((rl, moveSet) -> event.addMoveSet(moveSet.build(rl)));
    }
    private void registerExCapCreation(ExCapBuilderCreationEvent event) {
        registryMap.forEach((rl, moveSet) -> event.addBuilder(moveSet.getBuilderEntry()));
    }
    private void registerExCapBuilderPopulate(ExCapabilityBuilderPopulationEvent event) {
        registryMap.forEach((rl, moveSet) -> event.registerData(moveSet.getBuilderEntry().id(), moveSet.getDataEntry().id()));
    }

    @Override
    public void onModConstructor(IEventBus modBus) {
        modBus.addListener(this::registerWeaponCapability);
        modBus.addListener(this::registerExCapData);
        modBus.addListener(this::registerExCapMoveSet);
        modBus.addListener(this::registerExCapCreation);
        modBus.addListener(this::registerExCapBuilderPopulate);
    }
}