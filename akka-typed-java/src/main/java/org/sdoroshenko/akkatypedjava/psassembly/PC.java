package org.sdoroshenko.akkatypedjava.psassembly;

import java.util.ArrayList;
import java.util.List;

public class PC {
    public final int serialNumber;
    public final List<String> components;

    public PC(int serialNumber) {
        this.serialNumber = serialNumber;
        this.components = new ArrayList<>();
    }

    public PC(int serialNumber, List<String> components) {
        this.serialNumber = serialNumber;
        this.components = components;
    }

    public PC copy() {
        return new PC(serialNumber, components);
    }

    @Override
    public String toString() {
        return "PC{" +
                "serialNumber=" + serialNumber +
                ", components=" + components +
                '}';
    }
}
