package org.sdoroshenko.spacecounter;

import org.testng.annotations.Test;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ForkJoinPool;

import static org.testng.Assert.*;

public class SizeCalculationTaskTest {

    @Test
    public void compute() {
        String working = System.getProperty("user.dir");
        Path workingPath = Paths.get(working);
        File root = workingPath.toFile();

        ForkJoinPool pool = new ForkJoinPool();
        SizeCalculationTask task = new SizeCalculationTask(root);
        long size = pool.invoke(task);
        System.out.println(workingPath + " size: " + toKiloBytes(size) + " KB");
        assertEquals(toKiloBytes(size), 159);
    }

    private long toKiloBytes(long bytes) {
        return bytes / 1024;
    }

    private long toMegaBytes(long bytes) {
        return bytes / (1024 * 1024);
    }
}