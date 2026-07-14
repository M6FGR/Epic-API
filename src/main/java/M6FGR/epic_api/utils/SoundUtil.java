package M6FGR.epic_api.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.neoforged.fml.loading.FMLLoader;

public class SoundUtil {
    private static final SoundUtil INSTANCE = new SoundUtil();

    private SoundUtil() {}

    public static SoundUtil getInstance() {
        return INSTANCE;
    }

    public SoundManager getSoundManager() {
        return Minecraft.getInstance().getSoundManager();
    }

    public SoundEvent createTickableSound(SoundEvent soundEvent, float sVolume, float sPitch, Runnable task) {
        if (!FMLLoader.getDist().isClient()) return soundEvent;
        AbstractTickableSoundInstance tickableSound = new AbstractTickableSoundInstance(soundEvent, SoundSource.AMBIENT, RandomSource.create()) {
            @Override
            public float getVolume() {
                return sVolume;
            }

            @Override
            public float getPitch() {
                return sPitch;
            }

            @Override
            public void tick() {
                task.run();
            }
        };
        this.playSoundInstance(tickableSound);
        return soundEvent;
    }

    public void playSound(SoundEvent sound, float volume, float pitch) {
        if (!FMLLoader.getDist().isClient()) return;
        this.getSoundManager().play(SimpleSoundInstance.forLocalAmbience(sound, pitch, volume));
    }

    public void playSoundInstance(SoundInstance sound) {
        if (!FMLLoader.getDist().isClient()) return;
        this.getSoundManager().play(sound);
    }

    public void playSound(SoundEvent sound, float volume) {
        if (!FMLLoader.getDist().isClient()) return;
        if (sound == null) throw new NullPointerException("sound is null!");
        this.getSoundManager().play(SimpleSoundInstance.forUI(sound, volume));
    }

    public void playSoundFrom(Entity entity, SoundEvent sound, float volume, float pitch, Level level) {
        level.playSound(null, entity, sound, SoundSource.AMBIENT, volume, pitch);
    }

    public void playSound(SoundEvent sound) {
        if (!FMLLoader.getDist().isClient()) return;
        this.getSoundManager().play(SimpleSoundInstance.forUI(sound, 1.0F, 1.0F));
    }

    public void stopSound(SoundEvent sound) {
        if (!FMLLoader.getDist().isClient()) return;
        this.getSoundManager().stop(sound.getLocation(), null);
    }

    public void pauseSounds() {
        if (!FMLLoader.getDist().isClient()) return;
        this.getSoundManager().pause();
    }

    public void stopAllSounds() {
        if (!FMLLoader.getDist().isClient()) return;
        this.getSoundManager().stop();
    }
}