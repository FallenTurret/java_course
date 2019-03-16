package hse.hw07.quicksort;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class QuicksortTest {

    private Quicksort q;
    private static final int N = (int)1e5;

    @BeforeEach
    void setUp() {
        var array = new int[N];
        var rnd = new Random();
        for (int i = 0; i < N; i++) {
            array[i] = rnd.nextInt();
        }
        q = new Quicksort(array, 0, N);
    }

    @Test
    void oneThreadQuicksortShouldWOrkCorrectly() {
        q.quicksort();
        var array = q.getArray();
        for (int i = 0; i + 1 < N; i++) {
            assertTrue(array[i] <= array[i + 1]);
        }
    }

    @Test
    void multiThreadQuicksortShouldWOrkCorrectly() {
        q.run();
        var array = q.getArray();
        for (int i = 0; i + 1 < N; i++) {
            assertTrue(array[i] <= array[i + 1]);
        }
    }
}