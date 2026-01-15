package lxxv.shared.filesystem;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class LocalFileSystemProvider implements FileSystemProvider {
    @Override
    public byte[] read(Path path) throws IOException {
        return Files.readAllBytes(path);
    }

    @Override
    public void write(Path path, byte[] data) throws IOException {
        Files.createDirectories(path.getParent());
        Files.write(path, data);
    }

    @Override
    public boolean exists(Path path) throws IOException {
        return Files.exists(path);
    }

    @Override
    public void delete(Path path) throws IOException {
        Files.deleteIfExists(path);
    }

    @Override
    public void mkdirs(Path path) throws IOException {
        Files.createDirectories(path);
    }

    @Override
    public List<FileInfo> list(Path dir) throws IOException {
        List<FileInfo> files = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
            for (Path entry : stream) {
                files.add(stat(entry));
            }
        }
        return files;
    }

    @Override
    public FileInfo stat(Path path) throws IOException {
        return FileInfo.fromPath(path);
    }
}
