package dev.m6fgr.epic_api.utils.code;

import java.lang.reflect.Member;

public enum AccessType {
    PUBLIC(Member.PUBLIC),
    DECLARED(Member.DECLARED);

    private final int access;

    AccessType(int access) {
        this.access = access;
    }

    public int getAccess() {
        return this.access;
    }
}