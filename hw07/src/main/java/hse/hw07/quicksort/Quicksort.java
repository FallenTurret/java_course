package hse.hw07.quicksort;

import java.util.concurrent.ThreadLocalRandom;

public class Quicksort extends Thread {
    private int[] data;
    private int left, right;

    public Quicksort(int[] data, int left, int right) {
        this.data = data;
        this.left = left;
        this.right = right;
    }

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

    public void quicksort(int left, int right) {
        if (left + 1 < right) {
            int i = partition(left, right);
            quicksort(left, i);
            quicksort(i + 1, right);
        }
    }

    @Override
    public void run() {
        parallelQuicksort();
    }

    private void parallelQuicksort() {
        if (left + 1 < right) {
            int i = partition(left, right);
            var quicksort = new Quicksort(data, left, i);
            quicksort.start();
            left = i + 1;
            parallelQuicksort();
            try {
                quicksort.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}