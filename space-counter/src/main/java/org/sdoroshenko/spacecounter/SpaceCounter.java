package org.sdoroshenko.spacecounter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SpaceCounter {

    public static void main(String[] args) {
        String home = System.getProperty("user.home");
        String working = System.getProperty("user.dir");

        Path workingPath = Paths.get(working);
        File root = workingPath.toFile();
        System.out.println("Root: " + root);

        SpaceCounter counter = new SpaceCounter();
        long total = counter.calculate(root);
        System.out.println("Total: " + (total / (1024 * 1024)) + " MB"); // 23.3 23.8
        counter.ttt();
    }

    private long calculate(File root) {
        System.out.println("Calculating for: " + root);
        if (root.isDirectory()) {
            return Arrays.stream(Objects.requireNonNull(root.listFiles()))
                    .mapToLong(this::calculate).sum();
        }
        long totalSpace = root.length();
        System.out.println("File " + root + " has [" + totalSpace + ']');
        return totalSpace;
    }

    private void ttt() {
        String working = System.getProperty("user.dir");
        try (Stream<Path> walk = Files.walk(Paths.get(working))) {
            List<String> result = walk.filter(Files::isDirectory)
                    .map(x -> x.toString())
                    .collect(Collectors.toList());
            System.out.println(result);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
