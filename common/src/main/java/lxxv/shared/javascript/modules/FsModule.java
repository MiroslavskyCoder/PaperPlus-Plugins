package lxxv.shared.javascript.modules;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import lxxv.shared.javascript.JavaScriptEngine;

/**
 * Simple filesystem helpers for JS native module `fs`.
 */
public class FsModule {
    private final JavaScriptEngine engine;

    public FsModule(JavaScriptEngine engine) {
        this.engine = Objects.requireNonNull(engine, "engine");
    }

    public void register() {
        engine.registerFunction("readText", args -> Files.readString(resolve(args, 0), StandardCharsets.UTF_8));

        engine.registerFunction("writeText", args -> {
            Path path = resolve(args, 0);
            String content = args.length > 1 && args[1] != null ? args[1].toString() : "";
            boolean append = args.length > 2 && Boolean.parseBoolean(String.valueOf(args[2]));
            if (append) {
                Files.writeString(path, content, StandardCharsets.UTF_8, java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND);
            } else {
                Files.writeString(path, content, StandardCharsets.UTF_8, java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.TRUNCATE_EXISTING);
            }
            return true;
        });

        engine.registerFunction("exists", args -> Files.exists(resolve(args, 0)));

        engine.registerFunction("delete", args -> Files.deleteIfExists(resolve(args, 0)));

        engine.registerFunction("mkdirs", args -> {
            Files.createDirectories(resolve(args, 0));
            return true;
        });

        engine.registerFunction("list", args -> {
            List<String> entries = Files.list(resolve(args, 0)).map(p -> p.getFileName().toString()).collect(Collectors.toList());
            return entries.toArray(new String[0]);
        });
    }

    private Path resolve(Object[] args, int idx) {
        if (args.length <= idx || args[idx] == null) {
            throw new IllegalArgumentException("path is required");
        }
        return Paths.get(args[idx].toString());
    }
}
