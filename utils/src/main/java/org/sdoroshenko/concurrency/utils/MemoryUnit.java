package org.sdoroshenko.concurrency.utils;

/**
 * Describes a common memory units.
 */
public enum MemoryUnit {
    B(1), KB(1024), MB(1024 * 1024), GB(1024 * 1024 * 1024);

    private final int bytes;

    MemoryUnit(int bytes) {
        this.bytes = bytes;
    }

    public int bytes() {
        return bytes;
    }
}
