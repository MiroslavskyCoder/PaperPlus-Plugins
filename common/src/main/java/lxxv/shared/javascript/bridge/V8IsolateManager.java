package lxxv.shared.javascript.bridge;

import java.util.concurrent.ConcurrentLinkedQueue;

import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.interop.engine.IJavetEngine;
import com.caoccao.javet.interop.engine.JavetEnginePool;

/**
 * Lightweight isolate manager around Javet engine pool.
 */
public class V8IsolateManager {
    private final JavetEnginePool<V8Runtime> pool;
    private final ConcurrentLinkedQueue<IJavetEngine<V8Runtime>> borrowed = new ConcurrentLinkedQueue<>();

    public V8IsolateManager(JavetEnginePool<V8Runtime> pool) {
        this.pool = pool;
    }

    public IJavetEngine<V8Runtime> acquire() throws Exception {
        IJavetEngine<V8Runtime> engine = pool.getEngine();
        borrowed.add(engine);
        return engine;
    }

    public void release(IJavetEngine<V8Runtime> engine) {
        if (engine != null) {
            borrowed.remove(engine);
            pool.releaseEngine(engine);
        }
    }

    public void closeAll() {
        borrowed.forEach(pool::releaseEngine);
        borrowed.clear();
    }
}
