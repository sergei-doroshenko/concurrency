package org.sdoroshenko.concurrency.utils;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryType;

public class MemoryObserver {

    public long getHeapUsed(MemoryUnit memoryUnit) {
        long usedMemory = 0;
        for (MemoryPoolMXBean mpBean: ManagementFactory.getMemoryPoolMXBeans()) {
            if (mpBean.getType() == MemoryType.HEAP) {
                usedMemory += mpBean.getUsage().getUsed();
            }
        }

        return usedMemory / memoryUnit.bytes();
    }

    public long getNonHeapUsed(MemoryUnit memoryUnit) {
        long usedMemory = 0;
        for (MemoryPoolMXBean mpBean: ManagementFactory.getMemoryPoolMXBeans()) {
            if (mpBean.getType() == MemoryType.NON_HEAP) {
                usedMemory += mpBean.getUsage().getUsed();
            }
        }

        return usedMemory / memoryUnit.bytes();
    }
}
