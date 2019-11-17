package org.sdoroshenko.concurrency.examples.fjp;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;

import static org.testng.Assert.*;

public class MergeSortTaskTest {

    @Test(dataProvider = "sortData")
    public void sort(int[] arr, int[] expected) {
        MergeSortTask task = new MergeSortTask(arr, 0, arr.length - 1);
        ForkJoinPool pool = new ForkJoinPool();
        int[] result = pool.invoke(task);
        System.out.println(Arrays.toString(result));
        assertEquals(result, expected);
    }

    @DataProvider(name = "sortData")
    public static Object[][] sortData() {
        return new Object[][]{
                {
                        new int[]{4, 1, 7, 12, 2, 9, 5},
                        new int[]{1, 2, 4, 5, 7, 9, 12},
                },
                {// sorted
                        new int[]{1, 2, 4, 5, 7, 9, 12},
                        new int[]{1, 2, 4, 5, 7, 9, 12},
                },
                {// reversed
                        new int[]{12, 9, 7, 5, 4, 2, 1},
                        new int[]{1, 2, 4, 5, 7, 9, 12},
                },
                {
                        new int[]{12, 1},
                        new int[]{1, 12},
                },
                {
                        new int[]{1},
                        new int[]{1},
                },
                {
                        new int[0],
                        new int[0],
                }
        };
    }

    @Test(dataProvider = "mergeData")
    public void merge(int[] arr, int low, int middle, int high, int[] expected) {
        MergeSortTask task = new MergeSortTask(null, 0, 0);
        task.merge(arr, low, middle, high);
        System.out.println(Arrays.toString(arr));
        assertEquals(arr, expected);
    }

    @DataProvider(name = "mergeData")
    public Object[][] mergeData() {
        return new Object[][]{
                {
                        new int[]{1, 4, 7, 2, 5, 9, 12},
                        0, 3 /*middle index of '2'*/, 6 /*last element (12) index + 1 or arr length*/,
                        new int[]{1, 2, 4, 5, 7, 9, 12},
                },
                {
                        new int[]{12, 1, 2, 4, 5, 7, 9},
                        0, 1, 6,
                        new int[]{1, 2, 4, 5, 7, 9, 12},
                },
                {
                        new int[]{2, 4, 5, 7, 9, 12, 1},
                        0, 6, 6,
                        new int[]{1, 2, 4, 5, 7, 9, 12},
                },
                {// sorted
                        new int[]{1, 2, 4, 5, 7, 9, 12},
                        0, 0, 6,
                        new int[]{1, 2, 4, 5, 7, 9, 12},
                }
        };
    }

    @Test(dataProvider = "merge2Data")
    public void merge2(int[] arr1, int[] arr2, int[] expected) {
        MergeSortTask task = new MergeSortTask(null, 0, 0);
        int[] result = task.merge2(arr1, arr2);
        System.out.println(Arrays.toString(result));
        assertEquals(result, expected);
    }

    @DataProvider(name = "merge2Data")
    public Object[][] merge2Data() {
        return new Object[][]{
                {
                        new int[0],
                        new int[0],
                        new int[0]
                },
                {
                        new int[]{2},
                        new int[]{1},
                        new int[]{1, 2}
                },
                {
                        new int[0],
                        new int[]{1},
                        new int[]{1}
                },
                {
                        new int[]{2},
                        new int[0],
                        new int[]{2}
                },
                {
                        new int[]{1, 4, 7},
                        new int[]{2, 5, 9, 12},
                        new int[]{1, 2, 4, 5, 7, 9, 12}
                },
                {
                        new int[]{2, 5, 9, 12},
                        new int[]{1, 4, 7},
                        new int[]{1, 2, 4, 5, 7, 9, 12}
                }
        };
    }
}