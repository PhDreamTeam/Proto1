package unl.fct.di.proto1.common.lib.core.master;


import unl.fct.di.proto1.common.lib.ActorNode;

import java.io.Serializable;

public class DDPartitionDescriptor implements Serializable {
    String DDUI;
    int partitionIdx;
    ActorNode workerNode;

    enum PartitionState {INIT, WAITING_WORKER_CREATE_REPLY, DEPLOYED, DEPLOYED_FAILED};

    PartitionState state = PartitionState.INIT;

    public DDPartitionDescriptor(String DDUI, int partitionIdx, ActorNode workerNode) {
        this.DDUI = DDUI;
        this.partitionIdx = partitionIdx;
        this.workerNode = workerNode;
        GlManager.getConsole().println("New " + this);
    }

    public DDPartitionDescriptor(String DDUI, DDPartitionDescriptor ddPartitionDescriptorIntParent) {
        this(DDUI, ddPartitionDescriptorIntParent.getPartitionId(),
                ddPartitionDescriptorIntParent.getWorkerNode());
    }

    public PartitionState getState() {
        return state;
    }

    public void setState(PartitionState newState) {
        state = newState;
    }


    public String getDDUI() {
        return DDUI;
    }

    public void setDDUI(String DDUI) {
        this.DDUI = DDUI;
    }

    public int getPartitionId() {
        return partitionIdx;
    }

    public void setPartitionIdx(int partitionIdx) {
        this.partitionIdx = partitionIdx;
    }

    public ActorNode getWorkerNode() {
        return workerNode;
    }

    @Override
    public String toString() {
        return "partition: " + DDUI + " - " + partitionIdx + " - " + workerNode;
    }

    public void setWorkerNode(ActorNode workerNode) {
        this.workerNode = workerNode;
    }

}
