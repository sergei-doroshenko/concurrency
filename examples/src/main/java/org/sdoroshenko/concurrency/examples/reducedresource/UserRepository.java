package org.sdoroshenko.concurrency.examples.reducedresource;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Assume that findUserByUsername and findUserByUserId are implemented.
 * findUserByUsername and findUserByUserId use a resource that can only be used by ONE thread at a time.
 * There is a limited pool of these resources that is much smaller than the number of threads used by the application.
 * e.g. database connection pool.
 *
 * Leverage the cache to optimise the performance of the method over time.
 *
 * Here we need to reduce applications threads, so we don't use one executor with a lot of threads.
 *
 * The example solution addresses the followup question
 * "How do you prevent two threads from performing findByUsername for the same input at the same time?".
 *
 * When I present this question in an interview, I say that the reason findById is fast is because
 * it connects to a distributed in-memory data store. The reason findByUsername is slow is because
 * it performs an HTTP call. In addition, the HTTP functionality is backed by a connection pool that is much smaller
 * than the number of threads in the application (e.g. if the web application has 200 threads,
 * there may only be 10 socket connections). So, how do you AVOID WASTING two socket connections for the SAME DATA?
 *
 * By the way, this may not be the most optimal solution although to be fair,
 * what is optimal may depend on usage patterns.
 * The best answer I've seen so far required Java 8-specific features (CompletableFuture).
 */
public class UserRepository {

    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    /**
     * The maximum size of the Map needs to be controlled,
     * ideally by using an LRU or LFQ (or similar) Map implementation whose size can be parameterised.
     * Map of usernames to User ids.
     */
    private final Map<String, Long> cache;

    private final Resource<User, Long> inMemoryStorage;

    private final Resource<User, String> userHttpService;

    private final List<ExecutorService> singleThreadExecutors;

    public UserRepository(Resource<User, Long> inMemoryStorage, Resource<User, String> userHttpService) {
        this.inMemoryStorage = inMemoryStorage;
        this.userHttpService = userHttpService;

        this.cache = new HashMap<>();
        // 4 executors
        this.singleThreadExecutors = Arrays.asList(
            Executors.newSingleThreadExecutor(),
            Executors.newSingleThreadExecutor(),
            Executors.newSingleThreadExecutor(),
            Executors.newSingleThreadExecutor()
        );
    }

    /**
     * The reason findUserByUserId is fast is because it connects to a distributed in-memory data store.
     * @param id a user id
     * @return a {@link User}
     */
    private User findByUserId(long id) {
        return inMemoryStorage.get(id);
    } // FAST

    /**
     * The reason findByUsername is slow is because it performs an HTTP call.
     * @param username a user's name
     * @return a {@link User}
     */
    private User findByUsername(String username) {
        return userHttpService.get(username);
    } // SLOW

    /**
     * Gets a {@link User} by username.
     * 1. Obtain read lock
     * 2. Check cache, if it doesn't contain requested data, UNLOCK read lock and LOCK write lock
     * 3. LOCK write lock, retrieve data and write to cache, UNLOCK write lock
     * 4. LOCK read lock
     * 5. Read data from cache and return it
     * 6. Finally UNLOCK read lock
     * @param username used as a KEY to store retrieved id in the {@link #cache}.
     * @return a {@link User}
     * @throws Exception
     */
    public User getUserByUsername(final String username) throws Exception {
        lock.readLock().lock();
        try {
            if (!cache.containsKey(username)) {
                lock.readLock().unlock(); // Why are we unlocking here? We need to unlock to obtain write access.
                // Read Access   	If no threads are writing, and no threads have requested write access.
                // Write Access   	If no threads are reading or writing.
                lock.writeLock().lock();
                // We need this check, because 2 thread can try to obtain write lock, so when first thread writes to the cache
                // and released writeLock second thread can obtain writeLock and make additional write.
                // Another option is to double check the cache.
                if (!cache.containsKey(username)) {
                    try {
                        System.out.println("Call http for: " + username);
                        final User user = shardedFind(username);
                        cache.put(username, user.getId()); // We put username argument, not user.getUsername()
                    } finally {
                        lock.writeLock().unlock();
                    }
                }

                lock.readLock().lock(); // Why do we need to re-obtain a read lock? Test for this. Because cache data can be cleaned.
            }
//            lock.readLock().lock(); // Why do we need to re-obtain a read lock? When commented - test fails 2 http calls instead 1
            // We either obtain it in first line or after cache put
            return findByUserId(cache.get(username));
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * 1. calculate shard number
     * 2. get single thread executor by shard number
     * 3. submit task to received executor
     * @param username
     * @return
     * @throws InterruptedException
     * @throws ExecutionException
     */
    protected User shardedFind(final String username) throws InterruptedException, ExecutionException {
        // why not use just one executor with many more threads?
        final int shard = username.hashCode() % singleThreadExecutors.size();
        final Callable<User> callable = new Callable<User>() {
            public User call() {
                return findByUsername(username);
            }
        };
        // why only one thread-per executor? Won't we get better performance with more threads?
        // because we need to reduce large number of application threads to a particular number of threads (e.g. 4)
        final ExecutorService singleThreadExecutor = singleThreadExecutors.get(shard);
        // this blocks, why isn't that a concern?
        // because it calls from multiple threads and each blocks on own future
        // and BTW we have an acquired lock above, why ???
        final Future<User> future = singleThreadExecutor.submit(callable);
        return future.get();
    }

    public void cleanCache() {
        lock.writeLock().lock();
        try {
            cache.clear();
        } finally {
            lock.writeLock().unlock();
        }
    }
}
