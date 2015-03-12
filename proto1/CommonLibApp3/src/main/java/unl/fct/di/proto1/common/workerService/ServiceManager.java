package unl.fct.di.proto1.common.workerService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Service Manager
 */
public class ServiceManager {
    ArrayList<String> services = new ArrayList<>();
    ArrayList<String> internalDDUIs = new ArrayList<>();
    // will keep the partition IDs, received from master after register
    // CHECK delete partitionsIDs- we don't need them in workers
    HashMap<String, Integer> internalDDUIsPartitionIDs = new HashMap<>();

    public void addService(String serviceName) {
        services.add(serviceName);
    }
    public List<String> getServices() {
        return Collections.unmodifiableList(services);
    }

    public void addInternalDDUI(String internalDDUI) {
        internalDDUIs.add(internalDDUI);
    }
    public List<String> getInternalDDUIs() {
        return Collections.unmodifiableList(internalDDUIs);
    }

    public int getInternalDDUIsPartitionID(String DDUI) {
        return internalDDUIsPartitionIDs.get(DDUI);
    }

    public void addInternalDDUIPartitionID(int[] internalDDUIsPartitionIDsReceived) {
        for (int i = 0, size = internalDDUIs.size(); i < size; i++) {
            internalDDUIsPartitionIDs.put(internalDDUIs.get(i), internalDDUIsPartitionIDsReceived[i]);
        }

    }
}
