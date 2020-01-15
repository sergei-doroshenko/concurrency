package org.sdoroshenko.concurrency.examples.fjp;

import org.testng.annotations.Test;

import java.util.concurrent.ForkJoinPool;

import static org.testng.Assert.*;

public class FibonacciTaskTest {

    @Test
    public void fibonacci() {
        int fibEnd = 20;
        ForkJoinPool pool = new ForkJoinPool();

        System.out.println("Parallelism: " + pool.getParallelism());
        System.out.println("Pool size: " + pool.getPoolSize());
        System.out.println("# task in queue: " + pool.getQueuedTaskCount());

        FibonacciTask task = new FibonacciTask(fibEnd);
        Integer fibNum = pool.invoke(task);
        System.out.println("FibNumber: " + fibNum + " for: " + fibEnd);
        System.out.println("Task count: " + FibonacciTask.getTaskCounter());

        assertEquals(fibNum.intValue(), 6765);
    }

}