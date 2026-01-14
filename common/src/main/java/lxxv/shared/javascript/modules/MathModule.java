package lxxv.shared.javascript.modules;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

import lxxv.shared.javascript.JavaScriptEngine;

/**
 * Adds math helper functions directly to the JS runtime via registered functions.
 */
public class MathModule {
    private final JavaScriptEngine engine;

    public MathModule(JavaScriptEngine engine) {
        this.engine = Objects.requireNonNull(engine, "engine");
    }

    public void register() {
        engine.registerFunction("mathClamp", args -> {
            double v = toDouble(args, 0, 0.0);
            double min = toDouble(args, 1, v);
            double max = toDouble(args, 2, v);
            return Math.min(Math.max(v, min), max);
        });

        engine.registerFunction("mathRandomRange", args -> {
            double min = toDouble(args, 0, 0.0);
            double max = toDouble(args, 1, 1.0);
            return ThreadLocalRandom.current().nextDouble(min, max);
        });

        engine.registerFunction("mathRandomInt", args -> {
            int min = (int) Math.floor(toDouble(args, 0, 0.0));
            int max = (int) Math.floor(toDouble(args, 1, 1.0));
            return ThreadLocalRandom.current().nextInt(min, max + 1);
        });

        engine.registerFunction("mathChance", args -> {
            double p = toDouble(args, 0, 0.0);
            return ThreadLocalRandom.current().nextDouble() < p;
        });

        engine.registerFunction("mathLerp", args -> {
            double a = toDouble(args, 0, 0.0);
            double b = toDouble(args, 1, 0.0);
            double t = toDouble(args, 2, 0.0);
            return a + (b - a) * t;
        });
    }

    private double toDouble(Object[] args, int idx, double def) {
        if (args.length <= idx || args[idx] == null) return def;
        try {
            return Double.parseDouble(args[idx].toString());
        } catch (NumberFormatException e) {
            return def;
        }
    }
}
