package hse.hw07.quicksort;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Class, which implements single and multiple thread quick-sort
 */
public class Quicksort extends Thread {
    private static final int THREADS = 10;
    private static volatile int threads = 0;
    private static final Object lock = new Object();
    private int[] data;
    private int left, right;

    /**
     * Constructor, which accepts array and borders. Only array elements within borders are sorted
     * @param data array of numbers
     * @param left left border, inclusively
     * @param right right border, exclusively
     */
    public Quicksort(int[] data, int left, int right) {
        this.data = data;
        this.left = left;
        this.right = right;
    }

    /**
     * Getter for data array
     * @return given in constructor array if quick-sort was not called, sorted array otherwise
     */
    public int[] getArray() {
        return data;
    }

    private int partition(int left, int right) {
        int median = data[ThreadLocalRandom.current().nextInt(left, right)];
        while (left < right) {
            while (data[left] < median) {
                left++;
            }
            while (data[right - 1] > median) {
                right--;
            }
            if (left + 1 >= right) {
                break;
            }
            var tmp = data[right - 1];
            data[right - 1] = data[left];
            data[left] = tmp;
            left++;
            right--;
        }
        return right;
    }

    public void quicksort() {
        quicksort(left, right);
    }

    private void quicksort(int left, int right) {
        if (left + 1 < right) {
            int i = partition(left, right);
            quicksort(left, i);
            quicksort(i, right);
        }
    }

    /**
     * Method, which calls multi-thread quick-sort
     */
    @Override
    public void run() {
        parallelQuicksort();
    }

    private void parallelQuicksort() {
        if (left + 1 < right) {
            int i = partition(left, right);
            Quicksort quicksort = null;
            synchronized (lock) {
                if (threads < THREADS) {
                    threads++;
                    quicksort = new Quicksort(data, left, i);
                }
            }
            if (quicksort != null) {
                quicksort.start();
                left = i;
                parallelQuicksort();
                try {
                    quicksort.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    synchronized (lock) {
                        threads--;
                    }
                }
            } else {
                var tmp = right;
                right = i;
                parallelQuicksort();
                left = i;
                right = tmp;
                parallelQuicksort();
            }
        }
    }
}