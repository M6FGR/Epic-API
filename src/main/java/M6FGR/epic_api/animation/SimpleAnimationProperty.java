package M6FGR.epic_api.animation;

import com.mojang.serialization.Codec;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.api.animation.property.AnimationProperty.StaticAnimationProperty;

public class SimpleAnimationProperty<T> extends StaticAnimationProperty<T> {

    public static final SimpleAnimationProperty<Float> PLAY_SPEED = new SimpleAnimationProperty<>("play_speed", Codec.FLOAT);


    public SimpleAnimationProperty(String name, @Nullable Codec<T> codecs) {
        super(name, codecs);
    }

    public SimpleAnimationProperty(String name) {
        this(name, null);
    }
}
