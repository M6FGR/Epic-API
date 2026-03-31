package M6FGR.epic_api.api.input;

import M6FGR.epic_api.gameassets.EpicAPIKeyMappings;
import net.minecraft.client.KeyMapping;
import org.jetbrains.annotations.NotNull;
import yesman.epicfight.api.client.input.action.InputAction;
import yesman.epicfight.api.client.input.controller.ControllerBinding;

import java.util.Optional;
public enum EpicAPIIntputAction implements InputAction {
    HEAVY_ATTACK;
    public @NotNull KeyMapping keyMapping() {
        return switch (this) {
            case HEAVY_ATTACK -> EpicAPIKeyMappings.HEAVY_ATTACK;
        };
    }

    private final int id;

    EpicAPIIntputAction() {
        this.id = InputAction.ENUM_MANAGER.assign(this);
    }

    @Override
    public @NotNull Optional<@NotNull ControllerBinding> controllerBinding() {
        return Optional.empty();
    }

    @Override
    public int universalOrdinal() {
        return this.id;
    }
}
