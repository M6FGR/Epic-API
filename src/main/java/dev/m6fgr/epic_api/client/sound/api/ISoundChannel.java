package dev.m6fgr.epic_api.client.sound.api;

import net.minecraft.sounds.SoundEvent;

public interface ISoundChannel {
    String getName();

    ISoundInstanceBuilder builder(SoundEvent soundEvent);

    ISoundChannel crossfadeTo(SoundEvent nextSound, float transitionSeconds);
    ISoundChannel setChannelVolume(float volume);
    ISoundChannel pauseChannel();
    ISoundChannel resumeChannel();
    ISoundChannel stopAll(float fadeOutSeconds);
}