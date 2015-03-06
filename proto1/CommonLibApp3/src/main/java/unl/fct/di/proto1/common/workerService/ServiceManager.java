package unl.fct.di.proto1.common.workerService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Service Manager
 */
public class ServiceManager {
    ArrayList<String> services = new ArrayList<>();

    public void addService(String serviceName) {
        services.add(serviceName);
    }

    public List<String> getServices() {
        return Collections.unmodifiableList(services);
    }
}
