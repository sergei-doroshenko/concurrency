package org.sdoroshenko.concurrency.examples.reducedresource;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @author Sergei_Doroshenko
 * @see UserRepository
 * @since 1.0
 * @version 1.0
 */
public class UserRepositoryTest {

    /**
     * Checks that two threads don't perform findByUsername for the same input at the same time.
     * @throws Exception
     */
    @Test
    public void getUserByUsernameReducedApplicationThreadsToArbitraryNumber() throws Exception {

        // create a test data source
        Map<Long, User> userStorage = new ConcurrentHashMap<>();
        userStorage.put(1L, new User(1, "peter_parker", "Peter Parker")); // we need to get this
        userStorage.put(2L, new User(2, "john_dow", "John Jow"));
        userStorage.put(3L, new User(3, "tony_stark", "Tony Stark"));

        CallCountable<User, Long> inMemoryStorage = new CallCountable<>(new FkInMemoryStorage(userStorage));
        CallCountable<User, String> userHttpClient = new CallCountable<>(new FkUserHttpClient());
        UserRepository userRepository = new UserRepository(inMemoryStorage, userHttpClient);

        // create a lot of threads
        List<Future> results = new ArrayList<>();
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < 10; i++) {
            // call
            Future<User> result = executorService.submit(() -> userRepository.getUserByUsername("http"));
            results.add(result);
        }

        for (Future<User> userResult : results) {
            User user = userResult.get();
            System.out.println(user);
            System.out.println(user.hashCode());
            Assert.assertEquals(user, new User(1, "peter_parker", "Peter Parker"));
        }
        // make some assertions to be sure that only arbitrary number of thread accessed the data storage
        // track number of simultaneous calls
        // check that we AVOID WASTING two socket connections for the SAME DATA

        System.out.println("InMemoryStorage call count: " + inMemoryStorage.getCallCount());
        System.out.println("UserHttpClient call count: " + userHttpClient.getCallCount());
        Assert.assertEquals(userHttpClient.getCallCount(), 1);
        Assert.assertEquals(inMemoryStorage.getCallCount(), 10);

        // check that time is not more than 2 min
    }

    @Test
    public void readFromCacheNeedsLock() throws Exception {

        // create a test data source
        Map<Long, User> userStorage = new ConcurrentHashMap<>();
        userStorage.put(1L, new User(1, "peter_parker", "Peter Parker")); // we need to get this
        userStorage.put(2L, new User(2, "john_dow", "John Jow"));
        userStorage.put(3L, new User(3, "tony_stark", "Tony Stark"));

        CallCountable<User, Long> inMemoryStorage = new CallCountable<>(new FkInMemoryStorage(userStorage));
        CallCountable<User, String> userHttpClient = new CallCountable<>(new FkUserHttpClient());
        UserRepository userRepository = new UserRepository(inMemoryStorage, userHttpClient);

        // create a lot of threads
        List<Future> results = new ArrayList<>();
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < 10; i++) {
            String username = i % 2 == 0 ? "Test" : "Hello";
            // call
            Future<User> result = executorService.submit(() -> userRepository.getUserByUsername(username));
            results.add(result);
        }

        ScheduledExecutorService scheduled = Executors.newSingleThreadScheduledExecutor();
        scheduled.schedule(() -> userRepository.cleanCache(), 31, TimeUnit.SECONDS);

        for (Future<User> userResult : results) {
            User user = userResult.get();
            System.out.println(user);
            System.out.println(user.hashCode());
//            Assert.assertEquals(user, new User(1, "peter_parker", "Peter Parker"));
        }
        // make some assertions to be sure that only arbitrary number of thread accessed the data storage
        // track number of simultaneous calls
        // check that we AVOID WASTING two socket connections for the SAME DATA

        System.out.println("InMemoryStorage call count: " + inMemoryStorage.getCallCount());
        System.out.println("UserHttpClient call count: " + userHttpClient.getCallCount());
        Assert.assertEquals(userHttpClient.getCallCount(), 2);
        Assert.assertEquals(inMemoryStorage.getCallCount(), 10);

        // check that time is not more than 2 min
    }
}