package dev.m6fgr.epic_api.client.sound.soundtypes;

import dev.m6fgr.epic_api.client.sound.api.IConfigurableSound;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.lwjgl.openal.AL11;

import java.util.function.Supplier;

public class ConfigurableSoundInstance extends AbstractTickableSoundInstance implements IConfigurableSound {

    private Supplier<Vec3> positionSupplier;
    private Supplier<Boolean> stopCondition;

    private float baseVolume = 1.0f;
    private float basePitch = 1.0f;
    private float speedMultiplier = 1.0f;

    private boolean isPaused = false;
    private float fadeSpeed = 0.05f;
    private boolean isFadingIn = false;
    private boolean isFadingOut = false;

    private double currentTimestamp = 0.0;
    private double loopStart = 0.0;
    private double loopEnd = -1.0;
    private boolean customLoop = false;

    private SoundSource category;
    private int openALSourceId = -1;

    public ConfigurableSoundInstance(SoundEvent sound, SoundSource category) {
        super(sound, category, SoundInstance.createUnseededRandom());
        this.looping = true;
        this.category = category;
        this.delay = 0;
    }

    @Override
    public void tick() {
        if (this.isPaused) return;

        if (this.stopCondition != null && stopCondition.get()) {
            this.stop(0.1f);
        }

        if (this.positionSupplier != null) {
            Vec3 pos = positionSupplier.get();
            if (pos != null) {
                this.x = pos.x;
                this.y = pos.y;
                this.z = pos.z;
            }
        }

        this.currentTimestamp += 0.05 * (double) this.pitch;

        if (this.customLoop && this.loopEnd > 0.0 && this.currentTimestamp >= this.loopEnd) {
            this.seekTo(this.loopStart);
        }

        if (this.isFadingOut) {
            this.volume -= fadeSpeed;
            if (this.volume <= 0.0f) {
                this.volume = 0.0f;
                this.stop();
            }
        } else if (this.isFadingIn) {
            this.volume += fadeSpeed;
            if (this.volume >= baseVolume) {
                this.volume = baseVolume;
                this.isFadingIn = false;
            }
        }
    }

    @Override
    public ConfigurableSoundInstance seekTo(double seconds) {
        this.currentTimestamp = Math.max(0.0, seconds);
        if (this.openALSourceId > 0) {
            AL11.alSourcef(this.openALSourceId, AL11.AL_SEC_OFFSET, (float) this.currentTimestamp);
        }
        return this;
    }

    @Override
    public ConfigurableSoundInstance pause() {
        this.isPaused = true;
        return this;
    }

    @Override
    public ConfigurableSoundInstance resume() {
        this.isPaused = false;
        return this;
    }

    @Override
    public ConfigurableSoundInstance stop(float fadeOutSeconds) {
        if (fadeOutSeconds <= 0.0f) {
            this.stop();
        } else {
            this.fadeSpeed = 1.0f / (fadeOutSeconds * 20.0f);
            this.isFadingOut = true;
            this.isFadingIn = false;
        }
        return this;
    }

    @Override
    public ConfigurableSoundInstance setVolume(float volume) {
        this.baseVolume = Math.max(0.0f, volume);
        if (!this.isFadingIn && !this.isFadingOut) this.volume = this.baseVolume;
        return this;
    }

    @Override
    public ConfigurableSoundInstance setPitch(float pitch) {
        this.basePitch = Math.max(0.1f, pitch);
        this.pitch = this.basePitch * this.speedMultiplier;
        return this;
    }

    @Override
    public ConfigurableSoundInstance setSpeed(float speed) {
        this.speedMultiplier = Math.max(0.1f, speed);
        this.pitch = this.basePitch * this.speedMultiplier;
        return this;
    }

    public ConfigurableSoundInstance enableFadeIn(float fadeSpeed) {
        this.fadeSpeed = fadeSpeed;
        this.isFadingIn = true;
        this.isFadingOut = false;
        this.volume = 0.0f;
        return this;
    }

    public ConfigurableSoundInstance setCategory(SoundSource category) {
        this.category = category;
        return this;
    }

    @Override
    public ConfigurableSoundInstance updateLoopBounds(double start, double end) {
        this.loopStart = Math.max(0.0, start);
        this.loopEnd = end;
        this.customLoop = true;
        return this;
    }

    public ConfigurableSoundInstance attachToPosition(Supplier<Vec3> positionSupplier) {
        this.positionSupplier = positionSupplier;
        return this;
    }

    public ConfigurableSoundInstance attachToEntity(Entity entity) {
        this.positionSupplier = entity::position;
        this.stopCondition = () -> entity == null || entity.isRemoved();
        return this;
    }

    public ConfigurableSoundInstance setStopCondition(Supplier<Boolean> stopCondition) {
        this.stopCondition = stopCondition;
        return this;
    }

    public ConfigurableSoundInstance setOpenALSourceId(int id) {
        this.openALSourceId = id;
        return this;
    }

    @Override public boolean isPlaying() { return !this.isStopped(); }
    @Override public boolean isPaused() { return this.isPaused; }
    @Override public double getCurrentTimestamp() { return this.currentTimestamp; }

    public SoundSource getCategory() { return this.category; }
    public double getLoopEnd() { return this.loopEnd; }
    public double getLoopStart() { return this.loopStart; }
    public float getBasePitch() { return this.basePitch; }
    public float getBaseVolume() { return this.baseVolume; }
    public float getFadeSpeed() { return this.fadeSpeed; }
    public float getSpeedMultiplier() { return this.speedMultiplier; }
    public boolean areStopConditionsMet() { return this.stopCondition.get(); }
    public Supplier<Vec3> getPositionSupplier() { return this.positionSupplier; }
}