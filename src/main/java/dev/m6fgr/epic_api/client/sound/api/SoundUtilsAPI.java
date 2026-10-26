package dev.m6fgr.epic_api.client.sound.api;

import dev.m6fgr.epic_api.client.sound.api.impl.SoundUtilsAPIImpl;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

public interface SoundUtilsAPI {

    static SoundUtilsAPI getInstance() {
        return new SoundUtilsAPIImpl();
    }

    /**
     * Obtains or creates a named channel group (e.g., "bgm", "ambient_layers", "sfx").
     */
    ISoundChannel getChannel(String channelName);

    /**
     * Quick builder for standalone sounds outside of channel groups using a SoundEvent.
     */
    ISoundInstanceBuilder create(SoundEvent soundEvent);

    /**
     * Constructs a builder populated with properties loaded from a JSON data definition.
     *
     * @param dataId ResourceLocation pointing to the JSON entry (e.g., "modid:boss_theme")
     */
    ISoundInstanceBuilder createFromData(ResourceLocation dataId);

    /**
     * Constructs a builder populated with properties loaded from a JSON data definition.
     *
     * @param id an integer pointing to the JSON entry (e.g., 3)
     */
    ISoundInstanceBuilder createFromID(int id);

    /**
     * Immediately plays a sound defined in data/assets JSON without requiring manual builder calls.
     *
     * @param dataId ResourceLocation pointing to the JSON entry
     * @return Live handle to control or manipulate the playing audio instance
     */
    IConfigurableSound playFromData(ResourceLocation dataId);

    /**
     * Immediately plays a sound defined in data/assets JSON without requiring manual builder calls.
     *
     * @param dataId an integer pointing to the JSON entry
     * @return Live handle to control or manipulate the playing audio instance
     */
    IConfigurableSound playFromID(int dataId);
}