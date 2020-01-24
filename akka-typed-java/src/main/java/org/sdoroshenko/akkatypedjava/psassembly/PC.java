package org.sdoroshenko.akkatypedjava.psassembly;

import java.util.StringJoiner;

public class PC {
    public boolean cpu;

    public PC copy() {
        PC result = new PC();
        result.cpu = this.cpu;
        return result;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", PC.class.getSimpleName() + "[", "]")
            .add("cpu=" + cpu)
            .toString();
    }
}
