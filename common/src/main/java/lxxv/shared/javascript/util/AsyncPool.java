package lxxv.shared.javascript.util;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Simple async pool for running background JS-related tasks.
 */
public class AsyncPool {
    private final ExecutorService executor;

    public AsyncPool(int threads, String namePrefix) {
        int size = Math.max(1, threads);
        this.executor = Executors.newFixedThreadPool(size, new NamedFactory(namePrefix));
    }

    public <T> AsyncTaskHandle<T> submit(Callable<T> task) {
        return new AsyncTaskHandle<>(executor.submit(task));
    }

    public AsyncTaskHandle<?> submit(Runnable task) {
        return new AsyncTaskHandle<>(executor.submit(task, null));
    }

    public void shutdown() {
        executor.shutdownNow();
    }

    private static class NamedFactory implements ThreadFactory {
        private final String prefix;
        private final AtomicInteger idx = new AtomicInteger(0);

        NamedFactory(String prefix) {
            this.prefix = prefix == null ? "async-pool" : prefix;
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r);
            t.setName(prefix + "-" + idx.incrementAndGet());
            t.setDaemon(true);
            return t;
        }
    }
}
