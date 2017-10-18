package javapoc.framework.service;

public interface Service {

    default String getServiceType() {
        throw new ServiceException("Method not implemented: getServiceType(");
    }
}
