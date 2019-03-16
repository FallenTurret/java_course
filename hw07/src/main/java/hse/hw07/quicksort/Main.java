package hse.hw07.quicksort;

import java.util.Random;

/**
 * Class, which is used to search array size, which is more efficient to sort by multi-thread quick-sort
 */
public class Main {
    public static void main(String[] args) {
        int leftBound = 0;
        int rightBound = (int)5e6;
        while (leftBound + 1 < rightBound) {
            int n = (leftBound + rightBound) / 2;
            if (isEfficient(n)) {
                rightBound = n;
            } else {
                leftBound = n;
            }
        }
        System.out.println("MultiThread is more efficient on arrays of size more than " + rightBound);
    }

    private static boolean isEfficient(int n) {
        var array = new int[n];
        var array2 = new int[n];
        var rnd = new Random();
        for (int i = 0; i < n; i++) {
            array[i] = rnd.nextInt(n);
            array2[i] = array[i];
        }
        var quicksort = new Quicksort(array, 0, n);
        var start = System.currentTimeMillis();
        quicksort.run();
        var multiThreadTime = System.currentTimeMillis() - start;
        quicksort = new Quicksort(array2, 0, n);
        start = System.currentTimeMillis();
        quicksort.quicksort();
        var singleThreadTime = System.currentTimeMillis() - start;
        System.out.println("Size of array: " + n);
        System.out.println("Single: " + singleThreadTime + "ms");
        System.out.println("Multi: " + multiThreadTime + "ms");
        System.out.println();
        return multiThreadTime < singleThreadTime;
    }
}