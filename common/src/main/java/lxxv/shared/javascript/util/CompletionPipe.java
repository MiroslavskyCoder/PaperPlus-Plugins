package lxxv.shared.javascript.util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * Simple pipeline helper to chain async stages.
 */
public class CompletionPipe<T> {
    private CompletableFuture<T> future;

    public CompletionPipe(CompletableFuture<T> seed) {
        this.future = seed;
    }

    public <U> CompletionPipe<U> then(Function<T, U> fn) {
        CompletableFuture<U> next = future.thenApply(fn);
        return new CompletionPipe<>(next);
    }

    public CompletionPipe<T> onError(Function<Throwable, T> fn) {
        future = future.exceptionally(fn);
        return this;
    }

    public CompletableFuture<T> joinAll(List<CompletableFuture<T>> futures) {
        List<CompletableFuture<T>> copy = new ArrayList<>(futures);
        return CompletableFuture.allOf(copy.toArray(new CompletableFuture[0]))
                .thenApply(v -> future.join());
    }

    public CompletableFuture<T> asFuture() {
        return future;
    }
}
