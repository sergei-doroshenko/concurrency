package org.sdoroshenko.concurrency.jcip.buffer;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class SemaphoreBoundedBufferPutTakeTest {
    protected ExecutorService pool;
    protected CyclicBarrier barrier;
    protected SemaphoreBoundedBuffer<Integer> bb;
    protected int nTrials, nPairs;
    protected AtomicInteger putSum = new AtomicInteger(0);
    protected AtomicInteger takeSum = new AtomicInteger(0);

    @BeforeMethod
    public void setUp() {
        pool = Executors.newCachedThreadPool();
        bb = new SemaphoreBoundedBuffer<>(10);
        nTrials = 1000;
        nPairs = 10;
        barrier = new CyclicBarrier(nPairs * 2 + 1);
    }

    @AfterMethod
    public void tearDown() {
        pool.shutdown();
    }

    @Test
    public void test() {
        try {
            for (int i = 0; i < nPairs; i++) {
                pool.execute(new Producer());
                pool.execute(new Consumer());
            }
            barrier.await(); // wait for all threads to be ready
            barrier.await(); // wait for all threads to finish
            Assert.assertEquals(putSum.get(), takeSum.get());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static int xorShift(int y) {
        y ^= (y << 6);
        y ^= (y >>> 21);
        y ^= (y << 7);
        return y;
    }

    class Producer implements Runnable {
        public void run() {
            try {
                int seed = (this.hashCode() ^ (int) System.nanoTime());
                int sum = 0;
                barrier.await();
                for (int i = nTrials; i > 0; --i) {
                    bb.put(seed);
                    sum += seed;
                    seed = xorShift(seed);
                }
                putSum.getAndAdd(sum);
                barrier.await();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    class Consumer implements Runnable {
        public void run() {
            try {
                barrier.await();
                int sum = 0;
                for (int i = nTrials; i > 0; --i) {
                    sum += bb.take();
                }
                takeSum.getAndAdd(sum);
                barrier.await();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
