package org.sdoroshenko.utils;

public class Await {

    public static void await(long timeout) {
        try {
            Thread.sleep(timeout);
        } catch (InterruptedException e) {
            throw LaunderThrowable.launderThrowable(e.getCause());
        }
    }

    public static void await1(long timeout) {
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() < start + timeout) {
        }
    }

}
