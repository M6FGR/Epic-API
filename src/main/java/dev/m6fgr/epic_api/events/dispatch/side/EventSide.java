package dev.m6fgr.epic_api.events.dispatch.side;

import dev.m6fgr.epic_api.utils.EnvironmentHelper;

import java.util.function.BooleanSupplier;

public enum EventSide {
    CLIENT(EnvironmentHelper::isLogicalClient),
    SERVER(EnvironmentHelper::isLogicalServer),
    BOTH(true);

    private final BooleanSupplier shouldDispatch;

    EventSide(BooleanSupplier shouldDispatch) {
        this.shouldDispatch = shouldDispatch;
    }

    EventSide(boolean shouldDispatch) {
        this(() -> shouldDispatch);
    }

    public boolean shouldDispatch() {
        return this.shouldDispatch.getAsBoolean();
    }

    public boolean is(EventSide matchingSide) {
        return this == matchingSide;
    }
}