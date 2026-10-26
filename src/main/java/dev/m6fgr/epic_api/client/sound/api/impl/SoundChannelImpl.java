package dev.m6fgr.epic_api.client.sound.api.impl;

import dev.m6fgr.epic_api.client.sound.api.IConfigurableSound;
import dev.m6fgr.epic_api.client.sound.api.ISoundChannel;
import dev.m6fgr.epic_api.client.sound.api.ISoundInstanceBuilder;
import net.minecraft.sounds.SoundEvent;

import java.util.ArrayList;
import java.util.List;

public class SoundChannelImpl implements ISoundChannel {

    private final String name;
    private final List<IConfigurableSound> activeSounds = new ArrayList<>();
    private float channelVolume = 1.0f;
    private IConfigurableSound currentActiveSound;

    public SoundChannelImpl(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public ISoundInstanceBuilder builder(SoundEvent soundEvent) {
        return new SoundInstanceBuilderImpl(soundEvent) {
            @Override
            public IConfigurableSound play() {
                IConfigurableSound sound = super.play();
                sound.setVolume(channelVolume);
                activeSounds.add(sound);
                currentActiveSound = sound;
                return sound;
            }
        };
    }

    @Override
    public SoundChannelImpl crossfadeTo(SoundEvent nextSound, float transitionSeconds) {
        IConfigurableSound oldSound = this.currentActiveSound;

        if (oldSound != null && oldSound.isPlaying()) {
            oldSound.stop(transitionSeconds);
        }

        float fadeSpeed = transitionSeconds > 0 ? 1.0f / (transitionSeconds * 20.0f) : 0.05f;
        this.currentActiveSound = builder(nextSound)
                .enableFadeIn(fadeSpeed)
                .play();
        return this;
    }

    @Override
    public SoundChannelImpl setChannelVolume(float volume) {
        this.channelVolume = Math.max(0.0f, volume);
        this.activeSounds.removeIf(s -> !s.isPlaying());
        this.activeSounds.forEach(s -> s.setVolume(this.channelVolume));
        return this;
    }

    @Override
    public SoundChannelImpl pauseChannel() {
        this.activeSounds.forEach(IConfigurableSound::pause);
        return this;
    }

    @Override
    public SoundChannelImpl resumeChannel() {
        this.activeSounds.forEach(IConfigurableSound::resume);
        return this;
    }

    @Override
    public SoundChannelImpl stopAll(float fadeOutSeconds) {
        this.activeSounds.forEach(configurableSound -> configurableSound.stop(fadeOutSeconds));
        this.activeSounds.clear();
        this.currentActiveSound = null;
        return this;
    }
}