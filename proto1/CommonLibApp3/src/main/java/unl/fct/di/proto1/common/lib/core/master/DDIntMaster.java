package unl.fct.di.proto1.common.lib.core.master;


import scala.collection.immutable.Stream;
import unl.fct.di.proto1.common.lib.ActorNode;
import unl.fct.di.proto1.common.lib.protocol.DDInt.*;
import unl.fct.di.proto1.common.lib.protocol.Msg;
import unl.fct.di.proto1.common.lib.tools.BaseActions.Function;
import unl.fct.di.proto1.common.lib.tools.BaseActions.Predicate;

import java.util.Arrays;

public class DDIntMaster extends DDMaster {

    // save data ref for resend to failed or substitute workers
    int[] data;


    // create a new DDInt based on an array
    public DDIntMaster(String DDUI, String createRequestId, int[] data, ActorNode ownerActorNode, Msg msgRequest) {
        // super data
        super(DDUI, data.length, null, ownerActorNode);

        // save data ref for resend to failed or substitute workers
        this.data = data;

        GlManager.getConsole().println("Creating DDIntMaster: " + DDUI);

        // keep DDInt in the DDInt manager
        GlManager.getDDManager().putDD(this);

        // get workers for this DD
        ActorNode[] workers = GlManager.getMasterService().getWorkers(getDataElemSize(), getNDataElems());
        //GlManager.getConsole().println("Got " + workers.length + " workers");

        // create new MasterRequest an put in in container of requests
        MasterRequest req = createMasterRequest(createRequestId, ownerActorNode, msgRequest);
        // create and activate timeout
        req.createTimeout();

        // build partitionsDescriptors
        buildPartitions(data, workers, req);
    }

    public DDIntMaster(String newDDUI, DDIntMaster parentDD, ActorNode ownerActorNode) {
        // super data
        super(newDDUI, parentDD.getNDataElems(), parentDD, ownerActorNode);

        this.data = null;

        GlManager.getConsole().println("Creating DDIntMaster: " + DDUI + " from parent: " + parentDD.getDDUI());

        // keep DDInt in the DDInt manager
        GlManager.getDDManager().putDD(this);

        // build partitionsDescriptors from parent - The same workers as the parent partitions
        copyPartitions(parentDD);
    }


    private void buildPartitions(int[] data, ActorNode[] workers, MasterRequest req) {
        int nPartitions = workers.length;
        int startIdx = 0, nElemsPerPartition = data.length / workers.length;

        for (int i = 0; i < nPartitions; i++) {
            int nElems = nElemsPerPartition + (i < data.length % workers.length ? 1 : 0);
            int[] partitionData = Arrays.copyOfRange(data, startIdx, startIdx + nElems);

            // create new partition descriptor
            DDPartitionDescriptor partDesc = new DDPartitionDescriptor(DDUI, i, workers[i]);
            // add partition to partitionsDescriptors collection
            partitionsDescriptors.add(partDesc);

            // send data to workers
            sendDataToWorker(partitionData, partDesc, req);

            // go for next section of elements
            startIdx += nElems;
        }
    }

    private void sendDataToWorker(int[] data, DDPartitionDescriptor pDescriptor, MasterRequest req) {
        // set state of partition
        pDescriptor.setState(DDPartitionDescriptor.PartitionState.WAITING_WORKER_CREATE_REPLY);

        // build message
        MsgPartitionCreateDDInt msgOut = new MsgPartitionCreateDDInt(pDescriptor.getDDUI(), req.getRequestId(),
                pDescriptor.getPartitionId(), data);

        // build request tracker and send message to worker if possible
        GlManager.getCommunicationHelper().tell(pDescriptor.getWorkerNode(), msgOut,
                GlManager.getMasterActor(), req);

        GlManager.getConsole().println("Sent: " + msgOut);
    }


    public void getData(String requestId, ActorNode requesterActorNode, Msg msgRequest) {
        // create new MasterRequest an put in in container of requests
        MasterRequest req = createMasterRequest(requestId, requesterActorNode, msgRequest);
        // create and activate timeout
        req.createTimeout();

        for (DDPartitionDescriptor partition : partitionsDescriptors) {
            // build message
            MsgPartitionGetDataDDInt msgOut = new MsgPartitionGetDataDDInt(getDDUI(), requestId,
                    partition.getPartitionId());

            // build request tracker and send message to worker if possible
            GlManager.getCommunicationHelper().tell(partition.getWorkerNode(), msgOut,
                    GlManager.getMasterActor(), req);
            GlManager.getConsole().println("Sent: " + msgOut);
        }
    }


    public int getDataElemSize() {
        return Integer.SIZE / 8;
    }

    // aggregate operations

    /**
     * Perform an action as specified by a Consumer object
     */
    public DDIntMaster forEach(String newDDUI, Function<Integer, Integer> action, ActorNode requesterActorNode,
                               String requestId, Msg msgRequest) {
        // create a new local DDIntMaster
        DDIntMaster newDD = new DDIntMaster(newDDUI, this, requesterActorNode);

        // create new MasterRequest an put in in container of requests
        MasterRequest req = createMasterRequest(requestId, requesterActorNode, msgRequest);
        // create and activate timeout
        req.createTimeout();


        // send apply function to all partitions (their workers)
        for (DDPartitionDescriptor partition : newDD.partitionsDescriptors) {
            // set partition state
            partition.setState(DDPartitionDescriptor.PartitionState.WAITING_WORKER_CREATE_REPLY);

            // build message
            MsgPartitionApplyFunctionDDInt msgOut = new
                    MsgPartitionApplyFunctionDDInt(this.DDUI, requestId, partition.getPartitionId(),
                    newDDUI, action);

            // build request tracker and send message to worker if possible
            GlManager.getCommunicationHelper().tell(partition.getWorkerNode(), msgOut,
                    GlManager.getMasterActor(), req);

            GlManager.getConsole().println("Sent: " + msgOut);
        }
        return newDD;
    }

    // Map objects to another DD as specified by a Function object
    public <R> Stream<R> map(Function<Integer, ? extends R> mapper) {
        //for (DDPartitionInt partition : partitionsDescriptors) {
        //    partition.map(mapper);
        //}
        // TODO do MAP
        return null;
    }


    // Filter objects that match a Predicate object
    public DDIntMaster filter(String newDDUI, Predicate<Integer> filter, ActorNode requesterActorNode,
                              String requestId, Msg msgRequest) {
        // create a new local DDIntMaster
        DDIntMaster newDD = new DDIntMaster(newDDUI, this, requesterActorNode);

        // set nElems to 0
        newDD.resetNDataElems();

        // create new MasterRequest an put in in container of requests
        MasterRequest req = createMasterRequest(requestId, requesterActorNode, msgRequest);
        // create and activate timeout
        req.createTimeout();

        // send apply function to all partitions (their workers)
        for (DDPartitionDescriptor partition : newDD.partitionsDescriptors) {
            // set partition state
            partition.setState(DDPartitionDescriptor.PartitionState.WAITING_WORKER_CREATE_REPLY);

            // build and send message
            MsgPartitionApplyFilterDDInt msgOut = new MsgPartitionApplyFilterDDInt(this.DDUI, requestId,
                    partition.getPartitionId(), newDDUI, filter);

            // build request tracker and send message to worker if possible
            GlManager.getCommunicationHelper().tell(partition.getWorkerNode(), msgOut,
                    GlManager.getMasterActor(), req);

            GlManager.getConsole().println("Sent: " + msgOut);
        }
        return newDD;
    }


    // fire procedures ================================================================

    public void fireMsgCreatePartitionDDIntReply(MsgPartitionCreateDDIntReply msg) {
        MasterRequest req = requests.get(msg.getRequestId());

        // register the state
        DDPartitionDescriptor pd = partitionsDescriptors.get(msg.getPartId());
        pd.setState(msg.isSuccess() ? DDPartitionDescriptor.PartitionState.DEPLOYED :
                DDPartitionDescriptor.PartitionState.DEPLOYED_FAILED);

        // add answer to request
        req.addAnswer(msg);

        // check if request is finished
        if (req.getState() != MasterRequest.REQUEST_STATE.WAITING) {
            if (req.getState() == MasterRequest.REQUEST_STATE.SUCCESS) {
                // SUCCESS - nothing to do, just send msg to client
                MsgCreateDDIntReply msgOut = new MsgCreateDDIntReply(getDDUI(), msg.getRequestId(), true, null);
                req.getRequestOwner().tell(msgOut, GlManager.getMasterActor());
                GlManager.getConsole().println("Sent: " + msgOut);
            } else {
                // FAILED
                Msg msgOut = req.getMsgRequest().getFailureReplyMessage("failed!!!");
                req.getRequestOwner().tell(msgOut, GlManager.getMasterActor());
                GlManager.getConsole().println("Sent: " + msgOut);
            }
            GlManager.getConsole().println();
        }
    }

    public void fireMsgPartitionGetDataDDIntReply(MsgPartitionGetDataDDIntReply msg) {
       MasterRequest req = requests.get(msg.getRequestId());

        // add answer to request
        req.addAnswer(msg);

        // check if request is finished
        if (req.getState() != MasterRequest.REQUEST_STATE.WAITING) {
            if (req.getState() == MasterRequest.REQUEST_STATE.SUCCESS) {
                // SUCCESS - get data and return it to request owner
                int[] dataReply = getDataInternal(req);

                // send data to client requester
                MsgGetDataDDIntReply msgOut = new MsgGetDataDDIntReply(getDDUI(), msg.getRequestId(), dataReply,
                        true, null);
                req.getRequestOwner().tell(msgOut, GlManager.getMasterActor());
            } else {
                // FAILED
                Msg msgOut = req.getMsgRequest().getFailureReplyMessage("failed!!!");
                req.getRequestOwner().tell(msgOut, GlManager.getMasterActor());
            }
            GlManager.getConsole().println();

            // TODO We must delete the requests somewhere in time
            // PS: only when we don't need it for sure
        }
        // registar a msg e verificar se já terminou e se sim dar a resposta ao cliente
    }

    private int[] getDataInternal(MasterRequest req) {
        // reserve space to data
        int[] dataReply = new int[getNDataElems()];

        // get all data
        for (int i = 0, idx = 0, size = partitionsDescriptors.size(); i < size; i++) {
            int[] dPart = ((MsgPartitionGetDataDDIntReply) req.answers.get(i)).getData();
            // copy data
            System.arraycopy(dPart, 0, dataReply, idx, dPart.length);
            idx += dPart.length;
        }
        return dataReply;
    }

    /*
     * This method runs in the parent, but should update childDDUI (newDDUI) partitions state
     */
    public void fireMsgPartitionApplyFunctionDDIntReply(MsgPartitionApplyFunctionDDIntReply msg) {
        MasterRequest req = requests.get(msg.getRequestId());

        // add answer to request
        req.addAnswer(msg);

        // update partition state
        DDMaster newDDInt = GlManager.getDDManager().getDD(msg.getNewDDUI());
        newDDInt.partitionsDescriptors.get(msg.getPartId()).setState(msg.isSuccess() ?
                DDPartitionDescriptor.PartitionState.DEPLOYED :
                DDPartitionDescriptor.PartitionState.DEPLOYED_FAILED);

        // check if request is finished
        if (req.getState() != MasterRequest.REQUEST_STATE.WAITING) {
            if (req.getState() == MasterRequest.REQUEST_STATE.SUCCESS) {
                // success - send success to client requester
                MsgApplyFunctionDDIntReply msgOut = new
                        MsgApplyFunctionDDIntReply(getDDUI(), msg.getRequestId(), msg.getNewDDUI(), true, null);
                req.getRequestOwner().tell(msgOut, GlManager.getMasterActor());
            } else {
                // failure - send failure to client requester
                Msg msgOut = req.getMsgRequest().getFailureReplyMessage("failed!!!");
                req.getRequestOwner().tell(msgOut, GlManager.getMasterActor());
            }
            GlManager.getConsole().println();
        }
    }

    public void fireMsgPartitionApplyFilterDDIntReply(MsgPartitionApplyFilterDDIntReply msg) {
        MasterRequest req = requests.get(msg.getRequestId());

        // count with data elements in new partition
        // add answer to request
        req.addAnswer(msg);

        // update partition state
        DDMaster newDDInt = GlManager.getDDManager().getDD(msg.getNewDDUI());
        newDDInt.partitionsDescriptors.get(msg.getPartId()).setState(msg.isSuccess() ?
                DDPartitionDescriptor.PartitionState.DEPLOYED :
                DDPartitionDescriptor.PartitionState.DEPLOYED_FAILED);

        newDDInt.nDataElems += msg.getNElems();

        // check if request is finished
        if (req.getState() != MasterRequest.REQUEST_STATE.WAITING) {
            if (req.getState() == MasterRequest.REQUEST_STATE.SUCCESS) {
                // success - send success to client requester
                MsgApplyFilterDDIntReply msgOut = new
                        MsgApplyFilterDDIntReply(getDDUI(), msg.getRequestId(), msg.getNewDDUI(),
                        newDDInt.nDataElems, true, null);
                req.getRequestOwner().tell(msgOut, GlManager.getMasterActor());
            } else {
                // failure - send failure to client requester
                Msg msgOut = req.getMsgRequest().getFailureReplyMessage("failed!!!");
                req.getRequestOwner().tell(msgOut, GlManager.getMasterActor());
            }
            GlManager.getConsole().println();
        }
    }

}

