package dev.m6fgr.epic_api.cls.loadable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(
        ElementType.CONSTRUCTOR
)
@Retention(RetentionPolicy.RUNTIME)
public @interface Invoke {}
