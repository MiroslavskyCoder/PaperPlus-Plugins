package lxxv.shared.javascript.modules;

import lxxv.shared.filesystem.FileSystemManager;
import lxxv.shared.filesystem.FileInfo;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * FilesystemModule: Unified bridge for JS filesystem access (for requireNativeModule("filesystem"))
 */
public class FilesystemModule {
    private final FileSystemManager fs = FileSystemManager.getInstance();

    public byte[] read(String path) throws Exception {
        return fs.read(Paths.get(path));
    }

    public void write(String path, byte[] data) throws Exception {
        fs.write(Paths.get(path), data);
    }

    public boolean exists(String path) throws Exception {
        return fs.exists(Paths.get(path));
    }

    public void delete(String path) throws Exception {
        fs.delete(Paths.get(path));
    }

    public void mkdirs(String path) throws Exception {
        fs.mkdirs(Paths.get(path));
    }

    public List<FileInfo> list(String dir) throws Exception {
        return fs.list(Paths.get(dir));
    }

    public FileInfo stat(String path) throws Exception {
        return fs.stat(Paths.get(path));
    }
}
