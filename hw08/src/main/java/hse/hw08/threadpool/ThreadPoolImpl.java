package hse.hw08.threadpool;

import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Thread pool with fixed number of working threads
 */
public class ThreadPoolImpl {

    private static class SupplierFromFunction<R, S> implements Supplier<S> {

        private Function<R, S> function;
        private R result;

        public SupplierFromFunction(Function<R, S> function, R result) {
            this.function = function;
            this.result = result;
        }

        @Override
        public S get() {
            return function.apply(result);
        }
    }

    private class LightFutureImpl<R> implements LightFuture<R> {

        private volatile boolean ready = false;
        private volatile boolean inProgress = false;
        private Supplier<R> task;
        private R result;
        private LinkedList<Function<R, ?>> toApply = new LinkedList<>();

        public LightFutureImpl(Supplier<R> task) {
            this.task = task;
        }

        @Override
        public boolean isReady() {
            return ready;
        }

        @Override
        public R get() throws LightExecutionException {
            try {
                if (!inProgress) {
                    synchronized (this) {
                        while (!isReady()) wait();
                    }
                } else {
                    inProgress = false;
                    result = task.get();
                    task = null;
                    ready = true;
                }
            } catch (Exception e) {
                throw new LightExecutionException(e.getMessage(), e.getCause());
            }
            applyAll();
            return result;
        }

        @Override
        public <S> void thenApply(@NotNull Function<R, S> function) {
            if (isReady()) {
                var task = new SupplierFromFunction<>(function, result);
                synchronized (tasks) {
                    tasks.add(new LightFutureImpl<>(task));
                    tasks.notifyAll();
                }
            } else {
                synchronized (toApply) {
                    toApply.add(function);
                }
            }
        }

        private void applyAll() {
            synchronized (toApply) {
                while (toApply.size() > 0) {
                    var function = toApply.poll();
                    var task = new SupplierFromFunction<>(function, result);
                    synchronized (tasks) {
                        tasks.add(new LightFutureImpl<>(task));
                        tasks.notifyAll();
                    }
                }
            }
        }
    }

    private Thread[] threads;
    private LinkedList<LightFutureImpl<?>> tasks = new LinkedList<>();
    private LightExecutionException exception = null;

    /**
     * Constructs thread pool, runs threads, which take tasks from queue
     * @param n number of working threads
     */
    public ThreadPoolImpl(int n) {
        threads = new Thread[n];
        for (int i = 0; i < n; i++) {
            threads[i] = new Thread(() -> {
                while (true) {
                    LightFutureImpl<?> curTask;
                    synchronized (tasks) {
                        while (tasks.size() == 0) {
                            try {
                                tasks.wait();
                            } catch (InterruptedException e) {
                                return;
                            }
                        }
                        curTask = tasks.poll();
                    }
                    try {
                        curTask.inProgress = true;
                        curTask.get();
                        synchronized (curTask) {
                            curTask.notifyAll();
                        }
                    } catch (LightExecutionException e) {
                        exception = e;
                        shutdown();
                    }
                }
            });
            threads[i].start();
        }
    }

    /**
     * adds new task in queue
     * @param task task to execute
     * @param <R> type of task's result
     * @return LightFuture, which represents given tasks
     * @throws LightExecutionException if one of the previous tasks finished with exception
     */
    public <R> LightFuture<R> submit(@NotNull Supplier<R> task) throws LightExecutionException {
        if (exception != null) {
            throw exception;
        }
        var futureTask = new LightFutureImpl<>(task);
        synchronized (tasks) {
            tasks.add(futureTask);
            tasks.notifyAll();
        }
        return futureTask;
    }

    /**
     * Stops all threads immediately, doesn't wait until current tasks are finished
     */
    public void shutdown() {
        for (int i = 0; i < threads.length; i++) {
            threads[i].interrupt();
        }
    }
}