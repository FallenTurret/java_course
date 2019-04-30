package hse.hw08.threadpool;

import org.jetbrains.annotations.NotNull;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * interface for tasks in thread pool
 * @param <R> type of task result
 */
public interface LightFuture<R> {

    /**
     * Check whether task is done or not
     * @return true if result has been computed, false otherwise
     */
    boolean isReady();

    /**
     * Get task's result, wait until it's computation if needed
     * @return result of task
     * @throws LightExecutionException exception, containing information about exception in task
     */
    R get() throws LightExecutionException;

    /**
     * add new task in thread pool, which based on current task's result
     * @param function takes type of task's result and returns new task represented by supplier
     */
    void thenApply(@NotNull Function<R, Supplier<?>> function);
}