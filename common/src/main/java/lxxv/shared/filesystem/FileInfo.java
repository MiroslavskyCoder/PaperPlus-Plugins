package lxxv.shared.filesystem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

public class FileInfo {
    public final String name;
    public final boolean directory;
    public final long size;
    public final long lastModified;

    public FileInfo(String name, boolean directory, long size, long lastModified) {
        this.name = name;
        this.directory = directory;
        this.size = size;
        this.lastModified = lastModified;
    }

    public static FileInfo fromPath(Path path) throws IOException {
        BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);
        return new FileInfo(
            path.getFileName().toString(),
            attrs.isDirectory(),
            attrs.size(),
            attrs.lastModifiedTime().toMillis()
        );
    }
}
