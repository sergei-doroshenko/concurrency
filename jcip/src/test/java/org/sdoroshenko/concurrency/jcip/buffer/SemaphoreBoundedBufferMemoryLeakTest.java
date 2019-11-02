package org.sdoroshenko.concurrency.jcip.buffer;

import org.sdoroshenko.concurrency.utils.MemoryObserver;
import org.sdoroshenko.concurrency.utils.MemoryUnit;
import org.testng.Assert;
import org.testng.annotations.Test;

public class SemaphoreBoundedBufferMemoryLeakTest {

    private static final int CAPACITY  = 10000;
    private static final int THRESHOLD = 10000;
    private final SemaphoreBoundedBuffer<Big> bb = new SemaphoreBoundedBuffer<>(CAPACITY);

    class Big {
        double[] data = new double[10000];
    }

    @Test
    public void testLeak() throws InterruptedException {
        long heapSize1 = snapshotHeap();

        for (int i = 0; i < CAPACITY; i++)
            bb.put(new Big());

        for (int i = 0; i < CAPACITY; i++)
            bb.take();

        long heapSize2 = snapshotHeap();
        long heapDiff = Math.abs(heapSize1 - heapSize2);

        System.out.println(
                String.format(
                        "Math.abs(heapSize1 (%d%4$s) - heapSize2 (%d%4$s) ) = %d%4$s",
                        heapSize1,
                        heapSize2,
                        heapDiff,
                        MemoryUnit.KB.name()
                )
        );

        Assert.assertTrue(
                heapDiff < THRESHOLD,
                String.format(
                        "Math.abs(heapSize1 (%d) - heapSize2 (%d) ) should be less then %d, but %d",
                        heapSize1,
                        heapSize2,
                        THRESHOLD,
                        heapDiff
                )
        );
    }

    private long snapshotHeap() {
        /* Snapshot heap and return heap size */
        System.gc();
        return new MemoryObserver().getHeapUsed(MemoryUnit.KB);
    }
}