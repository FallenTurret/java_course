package hse.hw08.threadpool;

import java.util.function.Function;

public interface LightFuture<R> {

    boolean isReady();

    R get() throws LightExecutionException;

    void thenApply(Function<R, LightFuture<?>> function);
}