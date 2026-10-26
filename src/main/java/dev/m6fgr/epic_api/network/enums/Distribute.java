package dev.m6fgr.epic_api.network.enums;

public enum Distribute {
    SERVER,
    ALL_PLAYERS,
    PLAYER,
    WORLD_PLAYERS,
    PTE,
    PTEAS;

    public boolean is(Distribute... matches) {
        for (Distribute match : matches) {
            return this == match;
        }
        return false;
    }
}