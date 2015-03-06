package unl.fct.di.proto1.common.workerService;

import unl.fct.di.proto1.common.lib.core.worker.DDPartitionInt;


public class WorkerRequestNewPartition extends WorkerRequest {

    DDPartitionInt partition;

    public WorkerRequestNewPartition(DDPartitionInt partition) {
        super(nextId++);
        this.partition = partition;
    }

    public DDPartitionInt getPartition() {
        return partition;
    }

    @Override
    public String toString() {
        return id + " new DDInt: " + partition.toString();
    }
}
