package lxxv.shared.javascript.util;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Utility to execute callables with timeout and cancellation.
 */
public class SafeExecutor {
    private final ExecutorService executor;

    public SafeExecutor(ExecutorService executor) {
        this.executor = executor;
    }

    public <T> T run(Callable<T> task, long timeoutMs) throws Exception {
        Future<T> f = executor.submit(task);
        try {
            if (timeoutMs > 0) {
                return f.get(timeoutMs, TimeUnit.MILLISECONDS);
            }
            return f.get();
        } catch (TimeoutException te) {
            f.cancel(true);
            throw te;
        } catch (ExecutionException ee) {
            if (ee.getCause() instanceof Exception ex) {
                throw ex;
            }
            throw ee;
        }
    }
}
