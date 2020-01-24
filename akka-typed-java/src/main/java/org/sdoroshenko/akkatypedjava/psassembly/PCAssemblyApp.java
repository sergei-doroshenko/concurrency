package org.sdoroshenko.akkatypedjava.psassembly;

import java.util.concurrent.CompletionStage;

/**
 * install CPU -> priming processor -> affixing cooler
 * seating memory
 * preparing enclosure
 * SSD installation
 * add an optical drive
 *
 * inserting the motherboard
 * install graphic card
 * moar power
 *
 * Plugging peripherals
 *
 * Firmware tweaks
 * Installing the OS and drivers
 */
public class PCAssemblyApp {
    public static void main(String[] args) {
        AssemblyService service = new AssemblyService();

        PC pc = new PC();
        System.out.println(pc);
        CompletionStage<PC> result = service.installCPU(pc);
        result.whenComplete((reply, failure) -> System.out.println("Reply: " + reply + ", " + "Failure: " + failure));

        service.shutdown();
    }
}
