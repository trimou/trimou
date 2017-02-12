package org.trimou.engine.cache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trimou.AbstractEngineTest;

/**
 * All {@link ComputingCache} implementations should pass this naive concurrency
 * test.
 *
 * @author Martin Kouba
 */
public class ComputingCacheTest extends AbstractEngineTest {

    protected static final Logger LOGGER = LoggerFactory
            .getLogger(ComputingCacheTest.class);

    @Test
    public void testConcurrentAccess() throws InterruptedException {
        long actions = 10000;
        int threads = 20;
        Result result = runComputations(threads, actions, "my", null, null);
        assertEquals(actions, result.getComputations().get());
        assertEquals(actions, result.getCache().getAllPresent().size());
    }

    protected Result runComputations(final int threads, final long actions,
            String consumerId, Long expirationTimeout, Long maxSize)
            throws InterruptedException {

        final CountDownLatch startSignal = new CountDownLatch(threads);
        final AtomicLong computations = new AtomicLong();

        final ComputingCache<Long, String> cache = engine
                .getConfiguration()
                .getComputingCacheFactory()
                .create(consumerId,
                        key -> {
                            // logger.debug("Loading {}", key);
                            computations.incrementAndGet();
                            return key + ":"
                                    + Thread.currentThread().getId();
                        }, expirationTimeout, maxSize, null);

        final ExecutorService executorService = Executors
                .newFixedThreadPool(threads);
        final List<Callable<Boolean>> tasks = new ArrayList<>();

        for (int i = 0; i < threads; i++) {
            tasks.add(() -> {
                startSignal.countDown();
                startSignal.await();
                // Thread thread = Thread.currentThread();
                // logger.debug("{}/{} started", thread.getId(),
                // thread.getName());
                for (long j = 0; j < actions; j++) {
                    cache.get(j);
                }
                // logger.debug("{}/{} finished", thread.getId(),
                // thread.getName());
                return true;
            });
        }
        List<Future<Boolean>> results = executorService.invokeAll(tasks);
        while (!isFinished(results)) {
            Thread.sleep(10L);
        }
        return new Result(cache, computations);
    }

    private boolean isFinished(List<Future<Boolean>> results) {
        for (Future<Boolean> future : results) {
            if (!future.isDone()) {
                return false;
            }
        }
        return true;
    }

    protected static class Result {

        private final ComputingCache<Long, String> cache;

        private final AtomicLong computations;

        public Result(ComputingCache<Long, String> cache,
                AtomicLong computations) {
            super();
            this.cache = cache;
            this.computations = computations;
        }

        public ComputingCache<Long, String> getCache() {
            return cache;
        }

        public AtomicLong getComputations() {
            return computations;
        }

    }

    @Test
    public void testInvalidate() {

        final ComputingCache<Long, String> cache = engine.getConfiguration()
                .getComputingCacheFactory()
                .create("test", key -> "" + key, null, null, null);

        for (long i = 0; i < 100; i++) {
            cache.get(i);
        }

        cache.invalidate(key -> key % 2 == 0);
        assertEquals(50, cache.size());
    }

    @Test
    public void testEviction() throws InterruptedException {
        long actions = 1000L;
        int threads = 10;
        Result result = runComputations(threads, actions, "test", null, 500L);
        assertTrue(result.getComputations().get() >= actions);
        assertTrue(result.getCache().size() < actions);
    }

}
