package hse.hw08.threadpool;

import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

/**
 * interface for tasks in thread pool
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
     * Apply given function to result after computation
     * @param function takes type of task's result and returns new result
     */
    <S> void thenApply(@NotNull Function<R, S> function);
}