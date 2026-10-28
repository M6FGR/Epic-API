package dev.m6fgr.epic_api.events.dispatch.marks;

import dev.m6fgr.epic_api.events.dispatch.dispatchers.extras.EventBusType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// marked on the events that can be
// either posted by a @SubscribeEvent annotation or dispatched by the EventDispatcher
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface HybridEvent {
     EventBusType value() default EventBusType.BOTH;
}
