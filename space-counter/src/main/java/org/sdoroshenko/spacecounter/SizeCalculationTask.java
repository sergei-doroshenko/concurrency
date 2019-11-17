package org.sdoroshenko.spacecounter;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import java.util.stream.Collectors;

/**
 * Directory size calculation task.
 */
public class SizeCalculationTask extends RecursiveTask<Long> {

    private final File root;

    public SizeCalculationTask(File root) {
        this.root = root;
    }

    @Override
    protected Long compute() {
        if (root.isDirectory()) {
            List<ForkJoinTask<Long>> tasks = Arrays.stream(Objects.requireNonNull(root.listFiles()))
                    .map(SizeCalculationTask::new)
                    .map(ForkJoinTask::fork)
                    .collect(Collectors.toList());

            List<Long> results = tasks.stream()
                    .map(ForkJoinTask::join)
                    .collect(Collectors.toList());

            return results.stream().mapToLong(r -> r.longValue()).sum();
        }

        return root.length();
    }
}
