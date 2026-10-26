package dev.m6fgr.epic_api.client.sound.api.impl;

import dev.m6fgr.epic_api.client.sound.api.IConfigurableSound;
import dev.m6fgr.epic_api.client.sound.api.ISoundChannel;
import dev.m6fgr.epic_api.client.sound.api.ISoundInstanceBuilder;
import dev.m6fgr.epic_api.client.sound.api.SoundUtilsAPI;
import dev.m6fgr.epic_api.client.sound.data.SoundDataDefinition;
import dev.m6fgr.epic_api.client.sound.data.EpicAPISoundDataManager;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SoundUtilsAPIImpl implements SoundUtilsAPI {

    private final Map<String, ISoundChannel> channels = new ConcurrentHashMap<>();

    public SoundUtilsAPIImpl() {}

    @Override
    public ISoundChannel getChannel(String channelName) {
        return this.channels.computeIfAbsent(channelName, SoundChannelImpl::new);
    }

    @Override
    public ISoundInstanceBuilder create(SoundEvent soundEvent) {
        return new SoundInstanceBuilderImpl(soundEvent);
    }

    @Override
    public ISoundInstanceBuilder createFromData(ResourceLocation dataId) {
        SoundDataDefinition def = EpicAPISoundDataManager.byName(dataId);
        if (def == null) {
            throw new IllegalArgumentException("SoundUtils API: No JSON definition found for data ID: " + dataId);
        }

        SoundEvent soundEvent = BuiltInRegistries.SOUND_EVENT.get(def.soundEvent());
        if (soundEvent == null) {
            throw new IllegalArgumentException("SoundUtils API: SoundEvent not found: " + def.soundEvent());
        }

        return this.create(soundEvent)
                .setCategory(def.getSoundSource())
                .setVolume(def.volume())
                .setPitch(def.pitch())
                .setSpeed(def.speed())
                .enableFadeIn(def.fadeInSpeed())
                .setLoopBounds(def.loopStart(), def.loopEnd());
    }


    @Override
    public ISoundInstanceBuilder createFromID(int id) {
        SoundDataDefinition def = EpicAPISoundDataManager.byID(id);
        if (def == null) {
            throw new IllegalArgumentException("SoundUtils API: No JSON definition found for ID: " + id);
        }

        SoundEvent soundEvent = BuiltInRegistries.SOUND_EVENT.get(def.soundEvent());
        if (soundEvent == null) {
            throw new IllegalArgumentException("SoundUtils API: SoundEvent not found: " + def.soundEvent());
        }

        return this.create(soundEvent)
                .setCategory(def.getSoundSource())
                .setVolume(def.volume())
                .setPitch(def.pitch())
                .setSpeed(def.speed())
                .enableFadeIn(def.fadeInSpeed())
                .setLoopBounds(def.loopStart(), def.loopEnd());
    }

    @Override
    public IConfigurableSound playFromData(ResourceLocation dataId) {
        return this.createFromData(dataId).play();
    }

    @Override
    public IConfigurableSound playFromID(int dataId) {
        return this.createFromID(dataId).play();
    }
}