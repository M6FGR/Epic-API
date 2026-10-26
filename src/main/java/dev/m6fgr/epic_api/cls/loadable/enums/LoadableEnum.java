package dev.m6fgr.epic_api.cls.loadable.enums;

import dev.m6fgr.epic_api.cls.loadable.ILoadableClass;

public interface LoadableEnum {
    Class<? extends ILoadableClass> getLoadableClass();

    boolean isClientOnly();

}
