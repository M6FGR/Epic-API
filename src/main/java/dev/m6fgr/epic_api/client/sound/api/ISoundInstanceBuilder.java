package dev.m6fgr.epic_api.client.sound.api;

import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import java.util.function.Supplier;

public interface ISoundInstanceBuilder {
    ISoundInstanceBuilder setCategory(SoundSource category);
    ISoundInstanceBuilder setVolume(float volume);
    ISoundInstanceBuilder setPitch(float pitch);
    ISoundInstanceBuilder setSpeed(float speedMultiplier);

    // Positioning & Tracking
    ISoundInstanceBuilder attachTo(Entity entity);
    ISoundInstanceBuilder attachTo(Supplier<Vec3> positionSupplier);
    ISoundInstanceBuilder setStopCondition(Supplier<Boolean> stopCondition);

    // Timestamps & Looping
    ISoundInstanceBuilder setTimestamp(double seconds);
    ISoundInstanceBuilder setLoopBounds(double startSeconds, double endSeconds);

    // Transitions
    ISoundInstanceBuilder enableFadeIn(float fadeSpeed);

    IConfigurableSound play();
}