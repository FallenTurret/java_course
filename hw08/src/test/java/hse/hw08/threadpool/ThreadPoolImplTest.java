package hse.hw08.threadpool;

import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

class ThreadPoolImplTest {

    private static final int N = 20;
    private ThreadPoolImpl pool;
    private final Integer[] n = {0};

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
    void poolShouldContainAtLeastNThreads() throws InterruptedException, LightExecutionException {
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

    private Supplier<Integer> getTask() {
        return () -> {
            int m = 0;
            for (int i = 0; i < 1000000; i++) m++;
            synchronized (n) {
                n[0]++;
            }
            return m;
        };
    }

    private Function<Integer, Long> triangleSum = m -> {
        long result = 0;
        for (int i = 0; i < m; i++) result += i;
        synchronized (n) {
            n[0]++;
        }
        return result;
    };

    @Test
    void manyTasksWithThenApply() throws InterruptedException, LightExecutionException {
        ArrayList<LightFuture<Integer>> list = new ArrayList<>();
        for (int i = 0; i < 10 * N; i++) {
            var task = getTask();
            var futureTask = pool.submit(task);
            list.add(futureTask);
            futureTask.thenApply(triangleSum);
        }
        Thread.sleep(1000);
        for (var task: list) {
            task.thenApply((m) -> {
                synchronized (n) {
                    n[0]++;
                }
                return m + 1;
            });
        }
        Thread.sleep(2000);
        assertEquals(30 * N, n[0].intValue());
    }

    @Test
    void fewTasks() throws InterruptedException, LightExecutionException {
        for (int i = 0; i < N; i++) {
            var task = getTask();
            pool.submit(task);
            Thread.sleep(100);
        }
        assertEquals(N, n[0].intValue());
    }

    @Test
    void oneThreadShouldOutputLightExecutionExceptionAndShutdown() throws
            InterruptedException, LightExecutionException {
        var task = new Supplier<Integer>() {
            @Override
            public Integer get() {
                int i = 0;
                while (i < 10000) i++;
                return i / 0;
            }
        };
        var pool = new ThreadPoolImpl(1);
        for (int i = 0; i < 10; i++) {
            pool.submit(task);
        }
        Thread.sleep(1000);
        assertThrows(LightExecutionException.class, () -> pool.submit(getTask()));
        Thread.sleep(1000);
        assertEquals(0, n[0].intValue());
    }

    @Test
    void LightFutureGetShouldWOrkCorrectly() throws LightExecutionException {
        var list = new ArrayList<Supplier<Integer>>();
        for (int i = 0; i < 1000; i++) {
            var newTask = getTask();
            list.add(newTask);
            var task = pool.submit(newTask);
            if (i % 2 == 0) {
                assertEquals(1000000, task.get().intValue());
            }
        };
        for (int i = 1; i < 1000; i += 2) {
            assertEquals(1000000, list.get(i).get().intValue());
        }
    }
}