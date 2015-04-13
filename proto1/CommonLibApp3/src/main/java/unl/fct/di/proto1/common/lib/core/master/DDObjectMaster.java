package unl.fct.di.proto1.common.lib.core.master;


import unl.fct.di.proto1.common.lib.ActorNode;
import unl.fct.di.proto1.common.lib.protocol.DDObject.*;
import unl.fct.di.proto1.common.lib.protocol.Msg;
import unl.fct.di.proto1.common.lib.protocol.MsgPartitionReply;
import unl.fct.di.proto1.common.lib.tools.BaseActions.MapFunction;
import unl.fct.di.proto1.common.lib.tools.BaseActions.Predicate;

import java.util.Arrays;
import java.util.HashMap;


// TODO FILTER: CHECK THIS: each worker will have a partition that can have a smaller size (maybe empty), adjust it at reply (is this correct or what?)

public class DDObjectMaster<T> extends DDMaster {

    // save data ref for resend to failed or substitute workers
    T[] data;


    // create a new DDInt based on an array
    public DDObjectMaster(String DDUI, String createRequestId, T[] data, ActorNode ownerActorNode, Msg msgRequest) {
        // super data
        super(DDUI, data.length, null, ownerActorNode);

        // save data ref for resend to failed or substitute workers
        this.data = data;

        GlManager.getConsole().println("Creating DDObjectMaster: " + DDUI);

        // keep DDInt in the DDInt manager
        GlManager.getDDManager().putDD(this);

        // get workers for this DD
        ActorNode[] workers = GlManager.getMasterService().getWorkers(getDataElemSize(), getNDataElems());
        //GlManager.getConsole().println("Got " + workers.length + " workers");

        // create new MasterRequest an put in in container of requests
        MasterRequest req = createMasterRequest(createRequestId, ownerActorNode, msgRequest, false);
        // create and activate timeout
        req.createTimeout(null);

        // build partitionsDescriptors
        buildPartitions(data, workers, req);
    }

    public DDObjectMaster(String newDDUI, DDObjectMaster parentDD, ActorNode ownerActorNode) {
        // super data
        super(newDDUI, parentDD.getNDataElems(), parentDD, ownerActorNode);

        this.data = null;

        GlManager.getConsole().println("Creating DDObjectMaster: " + DDUI + " from parent: " + parentDD.getDDUI());

        // keep DDInt in the DDInt manager
        GlManager.getDDManager().putDD(this);

        // build partitionsDescriptors from parent - The same workers as the parent partitions
        copyPartitions(parentDD);
    }

    // register internal services DD
    public DDObjectMaster(String newDDUI) {
        // super data
        super(newDDUI, 0, null, null);

        this.data = null;

        GlManager.getConsole().println("Creating DDObjectMaster: " + DDUI + " from Worker internal service DD");

        // keep DDInt in the DDInt manager
        GlManager.getDDManager().putDD(this);
    }

    /**
     * @param an actor node
     * @return the partition ID for the worker
     */
    public int addWorkerInternalPartition(ActorNode an) {
        for (DDPartitionDescriptor pd : partitionsDescriptors) {
            if (pd.getWorkerNode().equals(an)) {
                // already exists, return the partition ID
                return pd.getPartitionId();
            }
        }

        // create new partition descriptor
        DDPartitionDescriptor partDesc = new DDPartitionDescriptor(DDUI, partitionsDescriptors.size(), an);
        // add partition to partitionsDescriptors collection
        partitionsDescriptors.add(partDesc);
        // return partition ID for the new partition
        return partDesc.getPartitionId();
    }


    private void buildPartitions(T[] data, ActorNode[] workers, MasterRequest req) {
        int nPartitions = workers.length;
        int startIdx = 0, nElemsPerPartition = data.length / workers.length;

        for (int i = 0; i < nPartitions; i++) {
            int nElems = nElemsPerPartition + (i < data.length % workers.length ? 1 : 0);
            T[] partitionData = Arrays.copyOfRange(data, startIdx, startIdx + nElems);

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

    private void sendDataToWorker(T[] data, DDPartitionDescriptor pDescriptor, MasterRequest req) {
        // set state of partition
        pDescriptor.setState(DDPartitionDescriptor.PartitionState.WAITING_WORKER_CREATE_REPLY);

        // build message
        MsgPartitionCreateDDObject<T> msgOut = new MsgPartitionCreateDDObject<>(pDescriptor.getDDUI(),
                req.getRequestId(), pDescriptor.getPartitionId(), data);

        // build request tracker and send message to worker if possible
        GlManager.getCommunicationHelper().tell(pDescriptor.getWorkerNode(), msgOut,
                GlManager.getMasterActor(), req);

        GlManager.getConsole().println("Sent: " + msgOut);
    }


    public void getData(String requestId, ActorNode senderNode, MsgGetDataDDObject<T> msg) {
        // create new MasterRequest an put in in container of requests
        final MasterRequest req = createMasterRequest(requestId, senderNode, msg, msg.allowIncompleteResults());
        // create and activate timeout
        req.createTimeout(new Runnable() {
            @Override
            public void run() {
                getDataReturnWithFailure(req);
            }
        });

        for (DDPartitionDescriptor partition : partitionsDescriptors) {
            // build message
            MsgPartitionGetDataDDObject msgOut = new MsgPartitionGetDataDDObject(getDDUI(), requestId,
                    partition.getPartitionId());

            // build request tracker and send message to worker if possible
            GlManager.getCommunicationHelper().tell(partition.getWorkerNode(), msgOut,
                    GlManager.getMasterActor(), req);
            //partition.getWorkerNode().getActorRef().tell(msg, GlManager.getMasterActor());
            GlManager.getConsole().println("Sent: " + msgOut);
        }
    }

    public int getDataElemSize() {
        // TODO maybe delete this method
        return 0;
    }

    // aggregate operations

    /**
     * Perform an action as specified by a Consumer object
     */
    public <R> DDObjectMaster<R> map(String newDDUI, MapFunction<T, R> mapFunction, ActorNode requesterActorNode,
                                     String requestId, final MsgApplyMapDDObject<T, R> msgRequest) {
        // create a new local DDIntMaster
        DDObjectMaster<R> newDD = new DDObjectMaster<>(newDDUI, this, requesterActorNode);
        // lets see how many elements we get for the map, if we allow incomplete results
        newDD.resetNDataElems();

        // create new MasterRequest an put in in container of requests
        final MasterRequest req = createMasterRequest(requestId, requesterActorNode, msgRequest,
                msgRequest.allowIncompleteResults());

        // create and activate timeout
        req.createTimeout(new Runnable() {
            @Override
            public void run() {
                mapReturnWithFailure(req, msgRequest);
            }
        });

        // send apply function to all partitions (their workers)
        for (DDPartitionDescriptor partition : newDD.partitionsDescriptors) {
            // set partition state
            partition.setState(DDPartitionDescriptor.PartitionState.WAITING_WORKER_CREATE_REPLY);

            // build message
            MsgPartitionApplyMapDDObject<T, R> msgOut = new
                    MsgPartitionApplyMapDDObject<>(this.DDUI, requestId, partition.getPartitionId(),
                    newDDUI, mapFunction, msgRequest.getArrayRType());

            // build request tracker and send message to worker if possible
            GlManager.getCommunicationHelper().tell(partition.getWorkerNode(), msgOut,
                    GlManager.getMasterActor(), req);
            GlManager.getConsole().println("Sent: " + msgOut);
        }
        return newDD;
    }


    // Filter objects that match a Predicate object
    public DDObjectMaster<T> filter(String newDDUI, Predicate<T> filter, ActorNode requesterActorNode,
                                    String requestId, MsgApplyFilterDDObject<T> msgRequest) {
        // create a new local DDObjectMaster
        DDObjectMaster<T> newDD = new DDObjectMaster<>(newDDUI, this, requesterActorNode);

        // set nElems to 0
        newDD.resetNDataElems();

        // create new MasterRequest an put in in container of requests
        final MasterRequest req = createMasterRequest(requestId, requesterActorNode, msgRequest,
                msgRequest.allowIncompleteResults());
        // create and activate timeout
        req.createTimeout(new Runnable() {
            @Override
            public void run() {
                filterReturnWithFailure(req);
            }
        });

        // send apply function to all partitions (their workers)
        for (DDPartitionDescriptor partition : newDD.partitionsDescriptors) {
            // set partition state
            partition.setState(DDPartitionDescriptor.PartitionState.WAITING_WORKER_CREATE_REPLY);

            // build message
            MsgPartitionApplyFilterDDObject<T> msgOut = new MsgPartitionApplyFilterDDObject<>(this.DDUI, requestId,
                    partition.getPartitionId(), newDDUI, filter);

            // build request tracker and send message to worker if possible
            GlManager.getCommunicationHelper().tell(partition.getWorkerNode(), msgOut,
                    GlManager.getMasterActor(), req);

            GlManager.getConsole().println("Sent: " + msgOut);
        }
        return newDD;
    }

    /**
     *
     */
    public DDObjectMaster<T> merge(String ddToMergeDDUI, String newDDUI, ActorNode requesterActorNode,
                                   String requestId, MsgApplyMergeDDObject msgRequest) {

        // create a new local DDObjectMaster
        DDObjectMaster<T> newDD = new DDObjectMaster<>(newDDUI, this, requesterActorNode);

        // add partitions descriptors from ddToMerge to newDD
        addPartitionDescriptorsFromDdToMergeToNewDD(ddToMergeDDUI, newDD);

        // set nElems to 0
        newDD.resetNDataElems();

        // create new MasterRequest an put in in container of requests
        MasterRequest req = createMasterRequest(requestId, requesterActorNode, msgRequest, newDD, false);
        // create and activate timeout
        req.createTimeout(null);

        // send apply function to all partitions (their workers)
        int nPartsFirstDD = partitionsDescriptors.size();
        for (DDPartitionDescriptor partition : newDD.partitionsDescriptors) {
            // set partition state
            partition.setState(DDPartitionDescriptor.PartitionState.WAITING_WORKER_CREATE_REPLY);

            // build message
            String ddToDuplicate = partition.getPartitionId() < nPartsFirstDD ? this.DDUI : ddToMergeDDUI;
            int partIdFormDdToDuplicate = partition.getPartitionId() < nPartsFirstDD ?
                    partition.getPartitionId() : partition.getPartitionId() - nPartsFirstDD;

            MsgPartitionApplyMergeDDObject msgOut = new MsgPartitionApplyMergeDDObject(newDDUI, requestId,
                    partition.getPartitionId(), ddToDuplicate, partIdFormDdToDuplicate);

            // build request tracker and send message to worker if possible
            GlManager.getCommunicationHelper().tell(partition.getWorkerNode(), msgOut,
                    GlManager.getMasterActor(), req);

            GlManager.getConsole().println("Sent: " + msgOut);
        }
        return newDD;
    }

    /**
     *
     */
    public void doReduce(ActorNode requesterActorNode, String requestId, MsgApplyReduceDDObject<T> msgRequest) {
        // create new MasterRequest an put in in container of requests
        final MasterRequest req = createMasterRequest(requestId, requesterActorNode, msgRequest, msgRequest.allowIncompleteResults());
        // create and activate timeout
        req.createTimeout(new Runnable() {
            @Override
            public void run() {
                reduceReturnWithFailure(req);
            }
        });

        // send apply function to all partitions (their workers)
        for (DDPartitionDescriptor partition : partitionsDescriptors) {
            // build message
            MsgPartitionApplyReduceDDObject<T> msgOut = new MsgPartitionApplyReduceDDObject<>(this.DDUI, requestId,
                    partition.getPartitionId(), msgRequest.getReduction());

            // build request tracker and send message to worker if possible
            GlManager.getCommunicationHelper().tell(partition.getWorkerNode(), msgOut,
                    GlManager.getMasterActor(), req);

            GlManager.getConsole().println("Sent: " + msgOut);
        }
    }

    public void doCount(ActorNode requesterActorNode, String requestId, MsgGetCountDDObject msgRequest) {
        // create new MasterRequest an put in in container of requests
        final MasterRequest req = createMasterRequest(requestId, requesterActorNode, msgRequest,
                msgRequest.allowIncompleteResults());
        // create and activate timeout
        req.createTimeout(new Runnable() {
            @Override
            public void run() {
                getCountReturnWithFailure(req);
            }
        });

        for (DDPartitionDescriptor partition : partitionsDescriptors) {
            // build message
            MsgPartitionGetCountDDObject msgOut = new MsgPartitionGetCountDDObject(getDDUI(), requestId,
                    partition.getPartitionId());

            // build request tracker and send message to worker if possible
            GlManager.getCommunicationHelper().tell(partition.getWorkerNode(), msgOut,
                    GlManager.getMasterActor(), req);
            //partition.getWorkerNode().getActorRef().tell(msg, GlManager.getMasterActor());
            GlManager.getConsole().println("Sent: " + msgOut);
        }
    }

    private void addPartitionDescriptorsFromDdToMergeToNewDD(String ddToMergeDDUI, DDObjectMaster newDD) {
        DDObjectMaster ddToMerge = (DDObjectMaster) GlManager.getDDManager().getDD(ddToMergeDDUI);

        int nPartitions = partitionsDescriptors.size();

        // add doClone of each ddToMerge partition
        for (DDPartitionDescriptor pd : ddToMerge.partitionsDescriptors) {
            // duplicate ddToMerge partition with partitionIdx corrected
            DDPartitionDescriptor newPart = new DDPartitionDescriptor(newDD.getDDUI(),
                    pd.partitionIdx + nPartitions, pd.getWorkerNode());
            // add duplicate partition to newDD
            newDD.partitionsDescriptors.add(newPart);
        }
    }


    // fire procedures ================================================================

    public void fireMsgCreatePartitionDDObjectReply(MsgPartitionCreateDDObjectReply msg) {
        MasterRequest req = requests.get(msg.getRequestId());

        // register the state
        DDPartitionDescriptor pd = partitionsDescriptors.get(msg.getPartId());
        pd.setState(msg.isSuccess() ? DDPartitionDescriptor.PartitionState.DEPLOYED :
                DDPartitionDescriptor.PartitionState.DEPLOYED_FAILED);

        // add answer to request
        req.addAnswer(msg);

        // checking if request is finished
        if (req.getState() != MasterRequest.REQUEST_STATE.WAITING) {
            if (req.getState() == MasterRequest.REQUEST_STATE.SUCCESS) {
                // SUCCESS - nothing to do, just send msg to client
                Msg msgOut = ((MsgCreateDDObject) req.getMsgRequest()).getSuccessReplyMessage();
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

    public void fireMsgPartitionGetDataDDObjectReply(MsgPartitionGetDataDDObjectReply<T> msg) {
        MasterRequest req = requests.get(msg.getRequestId());

        // add answer to request
        req.addAnswer(msg);

        // add nelems to nElemsReceived in case of success
        if (msg.isSuccess())
            req.addElemsReceived(msg.getData().length);

        // checking if request is finished
        if (req.getState() != MasterRequest.REQUEST_STATE.WAITING) {
            @SuppressWarnings("unchecked")
            MsgGetDataDDObject<T> msgGetData = (MsgGetDataDDObject<T>) req.getMsgRequest();
            if (req.getState() == MasterRequest.REQUEST_STATE.SUCCESS) {
                // SUCCESS - get data and return it to request owner
                T[] dataReply = getDataInternal(req);

                // send data to client requester
                MsgGetDataDDObjectReply<T> msgOut = msgGetData.getSuccessReplyMessage(dataReply);
                req.getRequestOwner().tell(msgOut, GlManager.getMasterActor());
            } else {
                // FAILED
                getDataReturnWithFailure(req);
            }
            GlManager.getConsole().println();

            // TODO We must delete the requests somewhere in time
            // PS: only when we don't need it for sure
        }
        // registar a msg e verificar se já terminou e se sim dar a resposta ao cliente
    }

    public void getDataReturnWithFailure(MasterRequest req) {
        @SuppressWarnings("unchecked")
        MsgGetDataDDObject<T> msgGetData = (MsgGetDataDDObject<T>) req.getMsgRequest();

        if (msgGetData.allowIncompleteResults() && req.receivedAtLeastOneSuccessMessage()) {
            // allow incomplete results - get incomplete data and return it to request owner
            T[] dataReply = getDataInternal(req);
            MsgGetDataDDObjectReply<T> msgOut = msgGetData.getSuccessReplyMessage(dataReply);
            msgOut.setIncompleteResults(req.getFailureReason());
            req.getRequestOwner().tell(msgOut, GlManager.getMasterActor());
        } else {
            // failure
            Msg msgOut = req.getMsgRequest().getFailureReplyMessage(
                    "Failure in partitions: " + req.getFailureReason());
            req.getRequestOwner().tell(msgOut, GlManager.getMasterActor());
        }
    }

    private T[] getDataInternal(MasterRequest req) {
        // reserve space to data
        T[] dataReply = null;

        // get all data
        for (int i = 0, idx = 0, size = partitionsDescriptors.size(); i < size; i++) {
            @SuppressWarnings("unchecked")
            MsgPartitionGetDataDDObjectReply<T> msgReply = (MsgPartitionGetDataDDObjectReply<T>) req.getAnswers().get(
                    i);

            // ignore faulty or timed out partition data
            if (msgReply == null || !msgReply.isSuccess())
                continue;

            T[] partitionData = msgReply.getData();
            if (dataReply == null) {
                // build result array from array from first message
                dataReply = Arrays.copyOf(partitionData, req.getNElemsReceived());
            } else {
                // copy data
                System.arraycopy(partitionData, 0, dataReply, idx, partitionData.length);
            }
            idx += partitionData.length;
        }
        return dataReply;
    }

    /*
     * This method runs in the parent, but should update childDDUI (newDDUI) partitions state
     */
    public void fireMsgPartitionApplyMapDDObjectReply(MsgPartitionApplyMapDDObjectReply msg) {
        MasterRequest req = requests.get(msg.getRequestId());

        // add answer to request
        req.addAnswer(msg);

        // update partition state
        DDMaster newDD = GlManager.getDDManager().getDD(msg.getNewDDUI());
        newDD.partitionsDescriptors.get(msg.getPartId()).setState(msg.isSuccess() ?
                DDPartitionDescriptor.PartitionState.DEPLOYED :
                DDPartitionDescriptor.PartitionState.DEPLOYED_FAILED);

        newDD.nDataElems += msg.getNElems();
        req.addElemsReceived(msg.getNElems());

        // checking if request is finished
        if (req.getState() != MasterRequest.REQUEST_STATE.WAITING) {
            if (req.getState() == MasterRequest.REQUEST_STATE.SUCCESS) {
                // success - send success to client requester
                Msg msgOut = ((MsgApplyMapDDObject) req.getMsgRequest()).getSuccessReplyMessage(req.getNElemsReceived());
                req.getRequestOwner().tell(msgOut, GlManager.getMasterActor());
            } else {
                @SuppressWarnings("unchecked")
                MsgApplyMapDDObject<T, ?> msgRequest = ((MsgApplyMapDDObject<T, ?>) req.getMsgRequest());
                mapReturnWithFailure(req, msgRequest);
            }
            GlManager.getConsole().println();
        }
    }

    public <R> void mapReturnWithFailure(MasterRequest req, MsgApplyMapDDObject<T, R> msgApplyMap) {
        if (msgApplyMap.allowIncompleteResults() && req.receivedAtLeastOneSuccessMessage()) {
            // allow incomplete results - get incomplete data and return it to request owner
            MsgApplyMapDDObjectReply msgOut = msgApplyMap.getSuccessReplyMessage(req.getNElemsReceived());
            msgOut.setIncompleteResults(req.getFailureReason());
            req.getRequestOwner().tell(msgOut, GlManager.getMasterActor());
        } else {
            // failure
            Msg msgOut = req.getMsgRequest().getFailureReplyMessage("Failure in partitions: " + req.getFailureReason());
            req.getRequestOwner().tell(msgOut, GlManager.getMasterActor());
        }
    }

    public void fireMsgPartitionApplyFilterDDObjectReply(MsgPartitionApplyFilterDDObjectReply msg) {
        MasterRequest req = requests.get(msg.getRequestId());

        // count with data elements in new partition
        // add answer to request
        req.addAnswer(msg);

        // update partition state
        DDMaster newDD = GlManager.getDDManager().getDD(msg.getNewDDUI());
        newDD.partitionsDescriptors.get(msg.getPartId()).setState(msg.isSuccess() ?
                DDPartitionDescriptor.PartitionState.DEPLOYED :
                DDPartitionDescriptor.PartitionState.DEPLOYED_FAILED);

        newDD.nDataElems += msg.getNElems();
        req.addElemsReceived(msg.getNElems());

        // checking if request is finished
        if (req.getState() != MasterRequest.REQUEST_STATE.WAITING) {
            if (req.getState() == MasterRequest.REQUEST_STATE.SUCCESS) {
                // success - send success to client requester
                Msg msgOut = ((MsgApplyFilterDDObject) req.getMsgRequest()).getSuccessReplyMessage(newDD.nDataElems);
                req.getRequestOwner().tell(msgOut, GlManager.getMasterActor());
            } else {
                // failure - checking for incomplete results
                filterReturnWithFailure(req);
            }
            GlManager.getConsole().println();
        }
    }

    public void filterReturnWithFailure(MasterRequest req) {
        @SuppressWarnings("unchecked")
        MsgApplyFilterDDObject<T> msgApplyFilter = (MsgApplyFilterDDObject<T>) req.getMsgRequest();

        if (msgApplyFilter.allowIncompleteResults() && req.receivedAtLeastOneSuccessMessage()) {
            // allow incomplete results - get incomplete data and return it to request owner
            MsgApplyFilterDDObjectReply msgOut = msgApplyFilter.getSuccessReplyMessage(req.getNElemsReceived());
            msgOut.setIncompleteResults(req.getFailureReason());
            req.getRequestOwner().tell(msgOut, GlManager.getMasterActor());
        } else {
            // failure
            Msg msgOut = req.getMsgRequest().getFailureReplyMessage("Failure in partitions: " + req.getFailureReason());
            req.getRequestOwner().tell(msgOut, GlManager.getMasterActor());
        }
    }

    public void fireMsgPartitionApplyMergeDDObjectReply(MsgPartitionApplyMergeDDObjectReply msg) {
        MasterRequest req = requests.get(msg.getRequestId());

        // count with data elements in new partition
        // add answer to request
        req.addAnswer(msg);

        // update partition state
        partitionsDescriptors.get(msg.getPartId()).setState(msg.isSuccess() ?
                DDPartitionDescriptor.PartitionState.DEPLOYED :
                DDPartitionDescriptor.PartitionState.DEPLOYED_FAILED);

        nDataElems += msg.getNElems();

        // checking if request is finished
        if (req.getState() != MasterRequest.REQUEST_STATE.WAITING) {
            if (req.getState() == MasterRequest.REQUEST_STATE.SUCCESS) {

                // success - send success to client requester
                Msg msgOut = ((MsgApplyMergeDDObject) req.getMsgRequest()).getSuccessReplyMessage(nDataElems);
                req.getRequestOwner().tell(msgOut, GlManager.getMasterActor());

            } else {
                // failure - send failure to client requester
                Msg msgOut = req.getMsgRequest().getFailureReplyMessage(
                        "Failure in partitions: " + req.getFailureReason());
                req.getRequestOwner().tell(msgOut, GlManager.getMasterActor());
            }

            GlManager.getConsole().println();
        }
    }

    public void fireMsgPartitionApplyReduceDDObjectReply(MsgPartitionApplyReduceDDObjectReply msg) {
        // get request and add msg to it
        MasterRequest req = requests.get(msg.getRequestId());
        req.addAnswer(msg);

        // checking if request is finished
        if (req.getState() != MasterRequest.REQUEST_STATE.WAITING) {
            if (req.getState() == MasterRequest.REQUEST_STATE.SUCCESS) {

                // success - calculate reduce results from all partitions result
                T result = calculateReduceResults(req);

                // send result to client requester
                @SuppressWarnings("unchecked")
                Msg msgOut = ((MsgApplyReduceDDObject<T>) req.getMsgRequest()).getSuccessReplyMessage(result);
                req.getRequestOwner().tell(msgOut, GlManager.getMasterActor());
            } else {
                // failure - checking for incomplete results
                reduceReturnWithFailure(req);
            }
            GlManager.getConsole().println();
        }
    }

    public void reduceReturnWithFailure(MasterRequest req) {
        @SuppressWarnings("unchecked")
        MsgApplyReduceDDObject<T> msgApplyReduce = (MsgApplyReduceDDObject<T>) req.getMsgRequest();

        if (msgApplyReduce.allowIncompleteResults() && req.receivedAtLeastOneSuccessMessage()) {
            // allow incomplete results - get incomplete data and return it to request owner
            T result = calculateReduceResults(req);

            MsgApplyReduceDDObjectReply<T> msgOut = msgApplyReduce.getSuccessReplyMessage(result);
            msgOut.setIncompleteResults(req.getFailureReason());
            req.getRequestOwner().tell(msgOut, GlManager.getMasterActor());
        } else {
            // failure - checking for incomplete results
            Msg msgOut = req.getMsgRequest().getFailureReplyMessage(
                    "Failure in partitions: " + req.getFailureReason());
            req.getRequestOwner().tell(msgOut, GlManager.getMasterActor());
        }
    }

    public void fireMsgPartitionGetCountDDObjectReply(MsgPartitionGetCountDDObjectReply msg) {
        // get request and add msg to it
        MasterRequest req = requests.get(msg.getRequestId());
        req.addAnswer(msg);

        req.addElemsReceived(msg.getCount());

        // checking if request is finished
        if (req.getState() != MasterRequest.REQUEST_STATE.WAITING) {
            if (req.getState() == MasterRequest.REQUEST_STATE.SUCCESS) {
                // send result to client requester
                Msg msgOut = ((MsgGetCountDDObject) req.getMsgRequest()).getSuccessReplyMessage(req.getNElemsReceived());
                req.getRequestOwner().tell(msgOut, GlManager.getMasterActor());
            } else {
                getCountReturnWithFailure(req);
            }
            GlManager.getConsole().println();
        }
    }

    public void getCountReturnWithFailure(MasterRequest req) {
        @SuppressWarnings("unchecked")
        MsgGetCountDDObject msgGetCount = (MsgGetCountDDObject) req.getMsgRequest();

        if (msgGetCount.allowIncompleteResults() && req.receivedAtLeastOneSuccessMessage()) {
            // allow incomplete results - get incomplete data and return it to request owner
            MsgGetCountDDObjectReply msgOut = msgGetCount.getSuccessReplyMessage(req.getNElemsReceived());
            msgOut.setIncompleteResults(req.getFailureReason());
            req.getRequestOwner().tell(msgOut, GlManager.getMasterActor());
        } else {
            // failure
            Msg msgOut = req.getMsgRequest().getFailureReplyMessage(
                    "Failure in partitions: " + req.getFailureReason());
            req.getRequestOwner().tell(msgOut, GlManager.getMasterActor());
        }
    }



    private T calculateReduceResults(MasterRequest req) {
        T result = null;
        HashMap<Integer, MsgPartitionReply> replies = req.getAnswers();
        for (int partIdx : replies.keySet()) {
            @SuppressWarnings("unchecked")
            MsgPartitionApplyReduceDDObjectReply<T> msgReply = (MsgPartitionApplyReduceDDObjectReply<T>) (replies.get(partIdx));

            if(msgReply.isSuccess()) {
                T partResult = msgReply.getResult();
                if (result == null)
                    result = partResult;
                else {
                    @SuppressWarnings("unchecked")
                    MsgApplyReduceDDObject<T> m = (MsgApplyReduceDDObject<T>) (req.getMsgRequest());
                    result = m.getReduction().reduce(partResult, result);
                }
            }
        }
        return result;
    }

}

