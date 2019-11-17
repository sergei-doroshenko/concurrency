package org.sdoroshenko.concurrency.examples.fjp;

import org.testng.annotations.Test;

import java.util.concurrent.ForkJoinPool;

import static org.testng.Assert.*;

public class FibonacciTaskTest {

    @Test
    public void fibonacci() {
        int fibEnd = 20;
        ForkJoinPool pool = new ForkJoinPool();

        FibonacciTask task = new FibonacciTask(fibEnd);
        Integer fibNum = pool.invoke(task);
        System.out.println("FibNumber: " + fibNum + " for: " + fibEnd);
        System.out.println("Task count: " + FibonacciTask.getTaskCounter());

        assertEquals(fibNum.intValue(), 6765);
    }

}