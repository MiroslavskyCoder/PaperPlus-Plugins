package lxxv.shared.filesystem;

import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FileSystemManager {
    private static final FileSystemManager INSTANCE = new FileSystemManager();
    private final Map<String, FileSystemProvider> providers = new ConcurrentHashMap<>();
    private FileSystemProvider defaultProvider;

    private FileSystemManager() {
        this.defaultProvider = new LocalFileSystemProvider();
        providers.put("local", defaultProvider);
    }

    public static FileSystemManager getInstance() {
        return INSTANCE;
    }

    public void registerProvider(String name, FileSystemProvider provider) {
        providers.put(name, provider);
    }

    public FileSystemProvider getProvider(String name) {
        return providers.getOrDefault(name, defaultProvider);
    }

    public FileSystemProvider getDefaultProvider() {
        return defaultProvider;
    }

    public void setDefaultProvider(FileSystemProvider provider) {
        this.defaultProvider = provider;
    }

    // Convenience methods
    public byte[] read(Path path) throws Exception {
        return defaultProvider.read(path);
    }
    public void write(Path path, byte[] data) throws Exception {
        defaultProvider.write(path, data);
    }
    public boolean exists(Path path) throws Exception {
        return defaultProvider.exists(path);
    }
    public void delete(Path path) throws Exception {
        defaultProvider.delete(path);
    }
    public void mkdirs(Path path) throws Exception {
        defaultProvider.mkdirs(path);
    }
    public java.util.List<FileInfo> list(Path dir) throws Exception {
        return defaultProvider.list(dir);
    }
    public FileInfo stat(Path path) throws Exception {
        return defaultProvider.stat(path);
    }
}
