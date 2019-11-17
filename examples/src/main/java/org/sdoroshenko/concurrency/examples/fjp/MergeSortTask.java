package org.sdoroshenko.concurrency.examples.fjp;

import java.util.concurrent.RecursiveTask;

public class MergeSortTask extends RecursiveTask<int[]> {

    private final int[] arr;
    private final int low;
    private final int high;

    public MergeSortTask(int[] arr, int low, int high) {
        this.arr = arr;
        this.low = low;
        this.high = high;
    }

    @Override
    protected int[] compute() {

        if (low < high) {
            // Get the index of the element which is in the middle
            int middle = low + (high - low) / 2;

            // Sort the left side of the array
            MergeSortTask task1 = new MergeSortTask(arr, low, middle);
            task1.fork();
            // Sort the right side of the array
            MergeSortTask task2 = new MergeSortTask(arr, middle + 1, high);
            task2.fork();

            task1.join();
            task2.join();

            // Combine them both
            merge(arr, low, middle+1, high);
        }

        return arr;
    }

    void merge(int[] arr, int low, int middle, int high) {
        int[] temp = new int[high - low + 1];
        int i = low, j = middle, k = 0;

        high += 1;

        while(i < middle && j < high) {
            if (arr[i] < arr[j]) {
                temp[k++] = arr[i++];
            } else {
                temp[k++] = arr[j++];
            }
        }

        while(i < middle) {
            temp[k++] = arr[i++];
        }

        while(j < high) {
            temp[k++] = arr[j++];
        }

        for(int n = low, m = 0; n < high; n++, m++) {
            arr[n] = temp[m];
        }
    }

    int[] merge2(int[] arr1, int[] arr2) {
        int n = arr1.length;
        int m = arr2.length;
        int[] temp = new int[n + m];
        int i = 0, j = 0, k = 0;

        // merge
        while(i < n && j < m) {
            if (arr1[i] < arr2[j]) {
                temp[k++] = arr1[i++];
            } else {
                temp[k++] = arr2[j++];
            }
        }

        // copy rest of arr1
        while(i < n) {
            temp[k++] = arr1[i++];
        }

        // copy the rest of arr2
        while(j < m) {
            temp[k++] = arr2[j++];
        }

        return temp;
    }
}
