package M6FGR.epic_api.api.events.hooks;

import M6FGR.epic_api.api.events.ExCapCapabilityRegistryEvent;
import yesman.epicfight.api.event.EventHook;

public class EpicAPIEventHooks {
    public static class Registry {
        public static final EventHook<ExCapCapabilityRegistryEvent> EX_CAP_CAPABILITY_REGISTRY = EventHook.createEventHook();
    }
}
