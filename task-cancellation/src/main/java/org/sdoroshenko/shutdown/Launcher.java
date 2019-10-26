package org.sdoroshenko.shutdown;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * java -Dfile.encoding=UTF-8 -classpath E:\projects\idea_projects\concurrency\task-cancellation\target\classes;E:\projects\idea_projects\concurrency\network\target\classes org.sdoroshenko.shutdown.Launcher
 * java org.sdoroshenko.shutdown.Launcher systemTimeout=10 producerTimeout=2 consumerTimeout=2
 */
public class Launcher {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("Application started. args=" + Arrays.toString(args));
        Map<String, Integer> argsMap = parseArgs(args);

        Queue<Integer> queue = new LinkedList<>();
        Producer producer = new Producer(queue, argsMap.get("producerTimeout"));
        Consumer consumer = new Consumer(queue, argsMap.get("consumerTimeout"));

        Thread hook = new Thread(() -> {
            System.out.println("Shutdown hook");
            producer.interrupt();
            consumer.interrupt();
            try {
                producer.join();
                consumer.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        Runtime.getRuntime().addShutdownHook(hook);
        System.out.println("Shutdown hook added");

        producer.start();
        consumer.start();
        System.out.println("-----------------------------------");

        TimeUnit.SECONDS.sleep(argsMap.get("systemTimeout"));

        System.out.println("\nApplication completed.\n-----------------------------------");
        System.exit(0);
    }

    private static Map<String, Integer> parseArgs(String[] args) {
        return Arrays.stream(args)
                .map(arg -> arg.split("="))
                .collect(Collectors.toMap(arr -> arr[0], arr -> Integer.valueOf(arr[1])));
    }
}
