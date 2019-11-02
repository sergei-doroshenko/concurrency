package org.sdoroshenko.concurrency.jcip.buffer;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;

public class SemaphoreBoundedBufferTest {
    private static final long LOCKUP_DETECT_TIMEOUT = 1000;
    private static final int CAPACITY  = 10;
    private SemaphoreBoundedBuffer<Integer> bb;

    @BeforeMethod
    public void setUp() {
        bb = new SemaphoreBoundedBuffer<>(CAPACITY);
    }

    public void testIsEmptyWhenConstructed() {
        Assert.assertTrue(bb.isEmpty());
        Assert.assertFalse(bb.isFull());
    }

    public void testIsFullAfterPuts() throws InterruptedException {
        for (int i = 0; i < 10; i++)
            bb.put(i);
        Assert.assertTrue(bb.isFull());
        Assert.assertFalse(bb.isEmpty());
    }

    public void testTakeBlocksWhenEmpty() {
        Assert.assertTrue(bb.isEmpty());
        Thread taker = new Thread(() -> {
            try {
                int unused = bb.take();
                Assert.fail(); // if we get here, it's an error
            } catch (InterruptedException success) {
            }
        });

        try {
            taker.start();
            Thread.sleep(LOCKUP_DETECT_TIMEOUT);
            taker.interrupt();
            taker.join(LOCKUP_DETECT_TIMEOUT);
            Assert.assertFalse(taker.isAlive());
        } catch (Exception unexpected) {
            Assert.fail();
        }
    }
}