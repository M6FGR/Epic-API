package dev.m6fgr.epic_api.events.dispatch;

import dev.m6fgr.epic_api.events.dispatch.abstracts.DispatchableEvent;
import org.jetbrains.annotations.ApiStatus.OverrideOnly;

/**
 * A lightweight event system hook that allows for dynamic event execution.
 */
@OverrideOnly
public interface IDispatchableEvent<T extends DispatchableEvent> {
    T post();

   default boolean isCanceledAfterPosting() {
       return post().isCanceled();
   }

   static void dispatchEvents(Runnable task) {
       task.run();
   }

}