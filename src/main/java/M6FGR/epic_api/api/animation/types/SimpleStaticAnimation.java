package M6FGR.epic_api.api.animation.types;

import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.client.animation.Layer;
import yesman.epicfight.api.client.animation.property.ClientAnimationProperties;
import yesman.epicfight.api.model.Armature;

public class SimpleStaticAnimation extends StaticAnimation {
    public SimpleStaticAnimation(float transitionTime, boolean repeatable, AnimationManager.AnimationAccessor<SimpleStaticAnimation> animation, AssetAccessor<? extends Armature> armature) {
        super(transitionTime, repeatable, animation, armature);
    }
    public SimpleStaticAnimation(boolean repeatable, AnimationManager.AnimationAccessor<SimpleStaticAnimation> animation, AssetAccessor<? extends Armature> armature) {
        super(repeatable, animation, armature);
    }
    public SimpleStaticAnimation(AnimationManager.AnimationAccessor<SimpleStaticAnimation> animation, AssetAccessor<? extends Armature> armature) {
        super(true, animation, armature);
    }

    public SimpleStaticAnimation withPriority(Layer.Priority priority) {
        this.addProperty(ClientAnimationProperties.PRIORITY, priority);
        return this;
    }

    public SimpleStaticAnimation withLayer(Layer.LayerType layer) {
        this.addProperty(ClientAnimationProperties.LAYER_TYPE, layer);
        return this;
    }
}
