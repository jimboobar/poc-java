package javapoc.storage.filesystem;

import javapoc.framework.Model;
import javapoc.framework.coder.Coder;
import javapoc.framework.storage.StorageException;
import javapoc.framework.storage.StorageService;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.stream.Collectors;

public class FilesystemService<T extends Model> implements StorageService<T> {


    private final FileHandler fileHandler;
    private final Coder<T> coder;


    public FilesystemService(Path rootPath, Coder<T> coder, String fileExtension) throws StorageException {
        if (coder == null)
            throw new StorageException("Invalid <coder>");

        this.fileHandler = new FileHandler(rootPath, fileExtension);
        this.coder = coder;
    }


    public FilesystemService(Path rootPath, Coder<T> coder) throws StorageException {
        this(rootPath, coder, "");
    }


    public Path getPath() {
        return fileHandler.rootPath;
    }


    @Override
    public Class<T> getClassType() {
        return coder.getClassType();
    }


    @Override
    public T create(T model) throws StorageException {
        Path path = fileHandler.resolve(model.getIdentifier());
        if (Files.exists(path))
            throw new StorageException(String.format(
                    "File already exists: %s", path.toString()));

        try (OutputStream output = Files.newOutputStream(path)) {
            coder.encode(output, model);
            return model;
        } catch (IOException e) {
            throw new StorageException(e);
        }
    }

    @Override
    public T read(String identifier) throws StorageException {
        Path path = fileHandler.resolve(identifier);
        if (!Files.isRegularFile(path))
            throw new StorageException(String.format(
                    "File does not exist: %s", path.toString()));

        try (InputStream input = Files.newInputStream(path)) {
            return coder.decode(input);
        } catch (IOException e) {
            throw new StorageException(e);
        }
    }

    @Override
    public T update(T model) throws StorageException {
        Path path = fileHandler.resolve(model.getIdentifier());
        if (!Files.isRegularFile(path))
            throw new StorageException(String.format(
                    "File does not exist: %s", path.toString()));

        try (OutputStream output = Files.newOutputStream(path)) {
            coder.encode(output, model);
            return model;
        } catch (IOException e) {
            throw new StorageException(e);
        }
    }


    @Override
    public Collection<String> keys() throws StorageException {
        try {
            return fileHandler.keys();
        } catch (IOException e) {
            throw new StorageException(e);
        }
    }


    @Override
    public T delete(String identifier) throws StorageException {
        try {
            T model = read(identifier);

            Files.delete(fileHandler.resolve(model.getIdentifier()));
            return model;
        } catch (IOException e) {
            throw new StorageException(e);
        }
    }


    private static class FileHandler {

        private final Path rootPath;
        private final String fileExtension;

        private FileHandler(Path rootPath, String fileExtension) throws StorageException {
            if (rootPath == null)
                throw new StorageException("Invalid <rootPath>");

            if (!Files.isDirectory(rootPath))
                throw new StorageException("Not a directory <rootPath>");

            if (!Files.exists(rootPath))
                throw new StorageException("Directory does not exist <rootPath>");

            this.rootPath = rootPath;
            this.fileExtension = (fileExtension == null)
                    ? ""
                    : fileExtension;
        }

        private Path resolve(String identifier) {
            if (identifier == null || identifier.isEmpty())
                throw new StorageException("Invalid <identifier>");

            return rootPath.resolve((fileExtension.isEmpty())
                    ? identifier
                    : String.format("%s%s", identifier, fileExtension));
        }

        private Collection<String> keys() throws IOException {
            return Files.list(rootPath)
                    //.filter(path -> path.endsWith(fileExtension))
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .map(fileName -> (fileExtension.isEmpty()
                            ? fileName
                            : fileName.substring(0, fileName.length() - fileExtension.length())))
                    .collect(Collectors.toList());
        }
    }
}
