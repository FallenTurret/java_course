package hse.hw08.threadpool;

import org.jetbrains.annotations.NotNull;

import java.util.function.Function;
import java.util.function.Supplier;

public interface LightFuture<R> {

    boolean isReady();

    R get() throws LightExecutionException;

    void thenApply(@NotNull Function<R, Supplier<?>> function);
}