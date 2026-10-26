package dev.m6fgr.epic_api.client.sound.data;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;

import javax.annotation.Nullable;

public record SoundDataDefinition(
        ResourceLocation soundEvent,
        ResourceLocation location,
        @Nullable String category,
        float volume,
        float pitch,
        float speed,
        float fadeInSpeed,
        double loopStart,
        double loopEnd
) {
    public SoundSource getSoundSource() {
        if (category == null) return SoundSource.MASTER;
        try {
            return SoundSource.valueOf(this.category.toUpperCase());
        } catch (IllegalArgumentException e) {
            return SoundSource.MASTER;
        }
    }

    public ResourceLocation getLocation() {
        return this.location;
    }
}