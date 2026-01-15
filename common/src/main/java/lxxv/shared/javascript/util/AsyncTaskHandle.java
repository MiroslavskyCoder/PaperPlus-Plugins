package lxxv.shared.javascript.util;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Lightweight handle around a Future.
 */
public class AsyncTaskHandle<T> {
    private final Future<T> future;

    public AsyncTaskHandle(Future<T> future) {
        this.future = future;
    }

    public boolean isDone() {
        return future.isDone();
    }

    public T get() throws Exception {
        return future.get();
    }

    public T get(long timeout, TimeUnit unit) throws Exception {
        return future.get(timeout, unit);
    }

    public boolean cancel() {
        return future.cancel(true);
    }
}
