package javapoc.framework.storage;

import javapoc.framework.Model;

import java.util.Collection;

public interface Storage<T extends Model> {


    Class<T> getClassType();


    T create(T model) throws StorageException;


    T read(String identifier) throws StorageException;


    T update(T model) throws StorageException;


    T delete(String identifier) throws StorageException;


    default Collection<String> keys() throws StorageException {
        throw new StorageException("Method not implemented: keys()");
    }


    default Integer size() throws StorageException {
        throw new StorageException("Method not implemented: size()");
    }
}
