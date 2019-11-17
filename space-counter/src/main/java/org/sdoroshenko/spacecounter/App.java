package org.sdoroshenko.spacecounter;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ForkJoinPool;

public class App {

    public static void main(String[] args) {
        String home = System.getProperty("user.home");
        String working = System.getProperty("user.dir");

        Path workingPath = Paths.get(working);
        File root = workingPath.toFile();
        ForkJoinPool pool = new ForkJoinPool();
        SizeCalculationTask task = new SizeCalculationTask(root);
        long total = pool.invoke(task);

        System.out.println("Total: " + toMegaBytes(total) + " MB"); // 23.3 23.8
    }

    private static long toKiloBytes(long bytes) {
        return bytes / 1024;
    }

    private static long toMegaBytes(long bytes) {
        return bytes / (1024 * 1024);
    }
}
