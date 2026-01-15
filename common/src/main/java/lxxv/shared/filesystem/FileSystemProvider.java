package lxxv.shared.filesystem;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface FileSystemProvider {
    byte[] read(Path path) throws IOException;
    void write(Path path, byte[] data) throws IOException;
    boolean exists(Path path) throws IOException;
    void delete(Path path) throws IOException;
    void mkdirs(Path path) throws IOException;
    List<FileInfo> list(Path dir) throws IOException;
    FileInfo stat(Path path) throws IOException;
}
