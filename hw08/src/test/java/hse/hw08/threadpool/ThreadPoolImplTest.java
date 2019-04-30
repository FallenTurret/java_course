package hse.hw08.threadpool;

import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

class ThreadPoolImplTest {

    private static final int N = 20;
    private ThreadPoolImpl pool;
    final Integer[] n = {0};

    @BeforeEach
    void setUp() {
        pool = new ThreadPoolImpl(N);
        n[0] = 0;
    }

    @AfterEach
    void tearDown() {
        pool.shutdown();
    }

    @Test
    void poolShouldContainAtLeastNThreads() throws InterruptedException {
        var task = new Supplier<Boolean>() {
            @Override
            public Boolean get() {
                synchronized (n[0]) {
                    n[0]++;
                }
                while (this != null) {}
                return true;
            }
        };
        for (int i = 0; i < N; i++) {
            pool.submit(task);
        }
        Thread.sleep(100);
        assertEquals(N, n[0].intValue());
    }

    Supplier<Integer> getTask() {
        return () -> {
            int m = 0;
            for (int i = 0; i < 1000000; i++) m++;
            synchronized (n) {
                n[0]++;
            }
            return m;
        };
    }

    @Test
    void manyTasksWithThenApply() throws InterruptedException {
        ArrayList<LightFuture<Integer>> list = new ArrayList<>();
        for (int i = 0; i < 10 * N; i++) {
            var task = getTask();
            var futureTask = pool.submit(task);
            list.add(futureTask);
            futureTask.thenApply((n) -> task);
        }
        Thread.sleep(1000);
        for (var task: list) {
            task.thenApply((n) -> getTask());
        }
        Thread.sleep(2000);
        assertEquals(30 * N, n[0].intValue());
    }

    @Test
    void fewTasks() throws InterruptedException {
        for (int i = 0; i < N; i++) {
            var task = getTask();
            pool.submit(task);
            Thread.sleep(100);
        }
        assertEquals(N, n[0].intValue());
    }

    @Test
    void oneThreadShouldOutputLightExecutionExceptionAndShutdown() throws InterruptedException {
        var task = new Supplier<Integer>() {
            @Override
            public Integer get() {
                return 1 / 0;
            }
        };
        var pool = new ThreadPoolImpl(1);
        for (int i = 0; i < 10; i++) {
            pool.submit(task);
        }
        Thread.sleep(100);
        pool.submit(getTask());
        Thread.sleep(1000);
        assertEquals(0, n[0].intValue());
    }
}