package org.sdoroshenko.akkatypedjava.psassembly;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * install CPU -> priming processor -> affixing cooler
 * seating memory
 * preparing enclosure
 * SSD installation
 * add an optical drive
 * <p>
 * inserting the motherboard
 * install graphic card
 * moar power
 * <p>
 * Plugging peripherals
 * <p>
 * Firmware tweaks
 * Installing the OS and drivers
 *
 * Throughput of assembly line should be more than 100 000 assembled PCs per second
 */
public class PCAssemblyApp {
    public static void main(String[] args) throws InterruptedException {
        AssemblyService service = new AssemblyService();
        AtomicInteger completed = new AtomicInteger();
        AtomicLong start = new AtomicLong(System.currentTimeMillis());
        AtomicLong finish = new AtomicLong();
        int pcNumber = 10;

        for (int i = 1; i <= pcNumber; i++) {
            System.out.println("Start assembling: " + i);
            PC pc = new PC(i);
            service.installCPU(pc)
                    .thenCompose(service::installMemory) // flatMap
                    .thenCompose(service::installSSD)
                    .thenCompose(service::installEnclosure)
                    .thenAccept(reply -> {
                        if (completed.incrementAndGet() == pcNumber)
                            finish.set(System.currentTimeMillis());
                    });
        }

        while (completed.get() < pcNumber) {
            Thread.sleep(100);
        }

        System.out.println("Time: " + (finish.get() - start.get()) + " ms");

        service.shutdown();
    }
}
