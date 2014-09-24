package org.trimou.engine.cache;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.Test;
import org.trimou.AbstractEngineTest;

/**
 * All {@link ComputingCache} implementations should pass this naive concurrency test.
 *
 * @author Martin Kouba
 */
public class ComputingCacheConcurrencyTest extends AbstractEngineTest {

    @Test
    public void testConcurrentAccess() throws InterruptedException {

        final int threads = 20;
        final long actions = 10000;
        final AtomicLong computations = new AtomicLong();

        final ComputingCache<Long, String> cache = engine.getConfiguration()
                .getComputingCacheFactory()
                .create("my", new ComputingCache.Function<Long, String>() {
                    @Override
                    public String compute(Long key) {
                        computations.incrementAndGet();
                        return key + ":thread" + Thread.currentThread().getId();
                    }
                }, null, null, null);

        final ExecutorService executorService = Executors
                .newFixedThreadPool(threads);
        final List<Callable<Boolean>> tasks = new ArrayList<Callable<Boolean>>();

        for (int i = 0; i < threads; i++) {
            tasks.add(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    for (long i = 0; i < actions; i++) {
                        cache.get(i);
                    }
                    return true;
                }
            });
        }

        List<Future<Boolean>> results = executorService.invokeAll(tasks);
        while (!isFinished(results)) {
            Thread.sleep(10l);
        }

        assertEquals(actions, computations.get());
        assertEquals(actions, cache.getAllPresent().size());
    }

    private boolean isFinished(List<Future<Boolean>> results) {
        for (Future<Boolean> future : results) {
            if (!future.isDone()) {
                return false;
            }
        }
        return true;
    }

}
