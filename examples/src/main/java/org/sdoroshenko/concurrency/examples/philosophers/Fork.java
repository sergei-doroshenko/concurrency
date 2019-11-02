package org.sdoroshenko.concurrency.examples.philosophers;

public class Fork {
    private final int id;
    private volatile boolean free = true;

    public Fork(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public boolean isFree() {
        return this.free;
    }

    public void setFree(boolean free) {
        this.free = free;
    }
}
