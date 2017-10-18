package javapoc.framework.storage;

import javapoc.framework.Model;
import javapoc.framework.service.Service;

public interface StorageService<T extends Model> extends Storage<T>, Service {

    @Override
    default String getServiceType() {
        return StorageService.class.getTypeName();
    }
}
