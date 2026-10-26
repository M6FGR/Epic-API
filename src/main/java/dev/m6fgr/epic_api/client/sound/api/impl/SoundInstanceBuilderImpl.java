package dev.m6fgr.epic_api.client.sound.api.impl;

import dev.m6fgr.epic_api.client.sound.api.IConfigurableSound;
import dev.m6fgr.epic_api.client.sound.api.ISoundInstanceBuilder;
import dev.m6fgr.epic_api.client.sound.soundtypes.ConfigurableSoundInstance;
import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import java.util.function.Supplier;

public class SoundInstanceBuilderImpl implements ISoundInstanceBuilder {

    private final ConfigurableSoundInstance instance;

    public SoundInstanceBuilderImpl(SoundEvent soundEvent) {
        this.instance = new ConfigurableSoundInstance(soundEvent, SoundSource.MASTER);
    }

    @Override
    public ISoundInstanceBuilder setCategory(SoundSource category) {
        this.instance.setCategory(category);
        return this;
    }

    @Override
    public ISoundInstanceBuilder setVolume(float volume) {
        this.instance.setVolume(volume);
        return this;
    }

    @Override
    public ISoundInstanceBuilder setPitch(float pitch) {
        this.instance.setPitch(pitch);
        return this;
    }

    @Override
    public ISoundInstanceBuilder setSpeed(float speedMultiplier) {
        this.instance.setSpeed(speedMultiplier);
        return this;
    }

    @Override
    public ISoundInstanceBuilder attachTo(Entity entity) {
        this.instance.attachToEntity(entity);
        return this;
    }

    @Override
    public ISoundInstanceBuilder attachTo(Supplier<Vec3> positionSupplier) {
        this.instance.attachToPosition(positionSupplier);
        return this;
    }

    @Override
    public ISoundInstanceBuilder setStopCondition(Supplier<Boolean> stopCondition) {
        this.instance.setStopCondition(stopCondition);
        return this;
    }

    @Override
    public ISoundInstanceBuilder setTimestamp(double seconds) {
        this.instance.seekTo(seconds);
        return this;
    }

    @Override
    public ISoundInstanceBuilder setLoopBounds(double startSeconds, double endSeconds) {
        this.instance.updateLoopBounds(startSeconds, endSeconds);
        return this;
    }

    @Override
    public ISoundInstanceBuilder enableFadeIn(float fadeSpeed) {
        this.instance.enableFadeIn(fadeSpeed);
        return this;
    }

    @Override
    public IConfigurableSound play() {
        Minecraft.getInstance().getSoundManager().play(this.instance);
        return this.instance;
    }
}