package javapoc.framework.storage;

public class StorageException extends RuntimeException {

    public StorageException(String message) {
        super(message);
    }

    public StorageException(Throwable t) {
        super(t);
    }
}
