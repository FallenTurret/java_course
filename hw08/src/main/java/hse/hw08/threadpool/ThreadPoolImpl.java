package hse.hw08.threadpool;

import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Thread pool with fixed number of working threads
 */
public class ThreadPoolImpl {

    private class LightFutureImpl<R> implements LightFuture<R> {

        private volatile boolean ready = false;
        private Supplier<R> task;
        private R result;
        private LinkedList<Function<R, Supplier<?>>> toApply = new LinkedList<>();

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
                result = task.get();
                ready = true;
            } catch(Exception e) {
                throw new LightExecutionException(e.getMessage(), e.getCause());
            }
            applyAll();
            return result;
        }

        @Override
        public void thenApply(@NotNull Function<R, Supplier<?>> function) {
            synchronized (toApply) {
                if (isReady()) {
                    var task = function.apply(result);
                    synchronized (tasks) {
                        tasks.add(new LightFutureImpl<>(task));
                        tasks.notifyAll();
                    }
                } else {
                    toApply.add(function);
                }
            }
        }

        private void applyAll() {
            synchronized (toApply) {
                while (toApply.size() > 0) {
                    var task = toApply.poll().apply(result);
                    synchronized (tasks) {
                        tasks.add(new LightFutureImpl<>(task));
                        tasks.notifyAll();
                    }
                }
            }
        }
    }

    private Thread[] threads;
    private LinkedList<LightFuture<?>> tasks = new LinkedList<>();

    /**
     * Constructs thread pool, runs threads, which take tasks from queue
     * @param n number of working threads
     */
    public ThreadPoolImpl(int n) {
        threads = new Thread[n];
        for (int i = 0; i < n; i++) {
            threads[i] = new Thread(() -> {
                while (true) {
                    LightFuture<?> curTask;
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
                        curTask.get();
                    } catch (LightExecutionException e) {
                        e.printStackTrace();
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
     */
    public <R> LightFuture<R> submit(@NotNull Supplier<R> task) {
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