package hse.hw07.quicksort;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class QuicksortTest {

    private Quicksort q;

    @BeforeEach
    void setUp() {
        var array = new int[10];
        var rnd = new Random();
        for (int i = 0; i < 10; i++) {
            array[i] = rnd.nextInt();
        }
        q = new Quicksort(array, 0, 10);
    }

    @Test
    void oneThreadQuicksortShouldWOrkCorrectly() {
        q.quicksort();
        var array = q.getArray();
        for (int i = 0; i < 9; i++) {
            assertTrue(array[i] <= array[i + 1]);
        }
    }

    @Test
    void multiThreadQuicksortShouldWOrkCorrectly() {
        q.run();
        var array = q.getArray();
        for (int i = 0; i < 9; i++) {
            assertTrue(array[i] <= array[i + 1]);
        }
    }
}