package unl.fct.di.proto1.common.lib.core.master;


import scala.collection.immutable.Stream;
import unl.fct.di.proto1.common.lib.ActorNode;
import unl.fct.di.proto1.common.lib.protocol.DDObject.*;
import unl.fct.di.proto1.common.lib.protocol.Msg;
import unl.fct.di.proto1.common.lib.protocol.MsgPartitionReply;
import unl.fct.di.proto1.common.lib.tools.BaseActions.Function;
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
        MasterRequest req = createMasterRequest(createRequestId, ownerActorNode, msgRequest);
        // create and activate timeout
        req.createTimeout();

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
     *
     * @param an
     * @return the partition ID for the worker
     */
    public int addWorkerInternalPartition(ActorNode an){
        for (DDPartitionDescriptor pd : partitionsDescriptors) {
            if(pd.getWorkerNode().equals(an)) {
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
        MsgPartitionCreateDDObject msgOut = new MsgPartitionCreateDDObject(pDescriptor.getDDUI(),
                req.getRequestId(), pDescriptor.getPartitionId(), data);

        // build request tracker and send message to worker if possible
        GlManager.getCommunicationHelper().tell(pDescriptor.getWorkerNode(), msgOut,
                GlManager.getMasterActor(), req);

        GlManager.getConsole().println("Sent: " + msgOut);
    }


    public void getData(String requestId, ActorNode senderNode, Msg msg) {
        // create new MasterRequest an put in in container of requests
        final MasterRequest req = createMasterRequest(requestId, senderNode, msg);
        // create and activate timeout
        req.createTimeout();

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
    public <R> DDObjectMaster<R> forEach(String newDDUI, Function<T, R> action, ActorNode requesterActorNode,
                                  String requestId, MsgApplyFunctionDDObject<T, R> msgRequest) {
        // create a new local DDIntMaster
        DDObjectMaster<R> newDD = new DDObjectMaster<>(newDDUI, this, requesterActorNode);

        // create new MasterRequest an put in in container of requests
        MasterRequest req = createMasterRequest(requestId, requesterActorNode, msgRequest);
        // create and activate timeout
        req.createTimeout();

        // send apply function to all partitions (their workers)
        for (DDPartitionDescriptor partition : newDD.partitionsDescriptors) {
            // set partition state
            partition.setState(DDPartitionDescriptor.PartitionState.WAITING_WORKER_CREATE_REPLY);

            // build message
            MsgPartitionApplyFunctionDDObject<T, R> msgOut = new
                    MsgPartitionApplyFunctionDDObject<>(this.DDUI, requestId, partition.getPartitionId(),
                    newDDUI, action, msgRequest.getArrayRType());

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
    public DDObjectMaster<T> filter(String newDDUI, Predicate<T> filter, ActorNode requesterActorNode,
                                 String requestId, Msg msgRequest) {
        // create a new local DDObjectMaster
        DDObjectMaster newDD = new DDObjectMaster(newDDUI, this, requesterActorNode);

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
    public <T> DDObjectMaster<T> merge(String ddToMergeDDUI, String newDDUI, ActorNode requesterActorNode,
                                String requestId, MsgApplyMergeDDObject msgRequest) {

        // create a new local DDObjectMaster
        DDObjectMaster<T> newDD = new DDObjectMaster<>(newDDUI, this, requesterActorNode);

        // add partitions descriptors from ddToMerge to newDD
        addPartitionDescriptorsFromDdToMergeToNewDD(ddToMergeDDUI, newDD);

        // set nElems to 0
        newDD.resetNDataElems();

        // create new MasterRequest an put in in container of requests
        MasterRequest req = createMasterRequest(requestId, requesterActorNode, msgRequest, newDD);
        // create and activate timeout
        req.createTimeout();

        // send apply function to all partitions (their workers)
        int nPartsFirstDD = partitionsDescriptors.size();
        for (DDPartitionDescriptor partition : newDD.partitionsDescriptors) {
            // set partition state
            partition.setState(DDPartitionDescriptor.PartitionState.WAITING_WORKER_CREATE_REPLY);

            // build message
            String ddToDuplicate =  partition.getPartitionId() < nPartsFirstDD ? this.DDUI : ddToMergeDDUI;
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
    public void doReduce(ActorNode requesterActorNode, String requestId, MsgApplyReduceDDObject msgRequest) {
        // create new MasterRequest an put in in container of requests
        MasterRequest req = createMasterRequest(requestId, requesterActorNode, msgRequest);
        // create and activate timeout
        req.createTimeout();

        // send apply function to all partitions (their workers)
        for (DDPartitionDescriptor partition : partitionsDescriptors) {
            // build message
            MsgPartitionApplyReduceDDObject msgOut = new MsgPartitionApplyReduceDDObject(this.DDUI, requestId,
                    partition.getPartitionId(), msgRequest.getReduction());

            // build request tracker and send message to worker if possible
            GlManager.getCommunicationHelper().tell(partition.getWorkerNode(), msgOut,
                    GlManager.getMasterActor(), req);

            GlManager.getConsole().println("Sent: " + msgOut);
        }
    }

    public void doCount(ActorNode requesterActorNode, String requestId, MsgGetCountDDObject msgRequest) {
        // create new MasterRequest an put in in container of requests
        final MasterRequest req = createMasterRequest(requestId, requesterActorNode, msgRequest);
        // create and activate timeout
        req.createTimeout();

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
        DDObjectMaster ddToMerge = (DDObjectMaster)GlManager.getDDManager().getDD(ddToMergeDDUI);

        int nPartitions = partitionsDescriptors.size();

        // add clone of each ddToMerge partition
        for (DDPartitionDescriptor pd: ddToMerge.partitionsDescriptors) {
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
                Msg msgOut = ((MsgCreateDDObject)req.getMsgRequest()).getSuccessReplyMessage();
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
        if(msg.isSuccess())
            req.addElemsReceived(msg.getData().length);

        // checking if request is finished
        if (req.getState() != MasterRequest.REQUEST_STATE.WAITING) {
            if (req.getState() == MasterRequest.REQUEST_STATE.SUCCESS) {
                // SUCCESS - get data and return it to request owner
                T[] dataReply = getDataInternal(req);

                // send data to client requester
                @SuppressWarnings("unchecked")
                MsgGetDataDDObject<T> msgGetData = (MsgGetDataDDObject<T>)req.getMsgRequest();
                MsgGetDataDDObjectReply<T> msgOut = msgGetData.getSuccessReplyMessage(dataReply);
                req.getRequestOwner().tell(msgOut, GlManager.getMasterActor());
            } else {
                // FAILED
                Msg msgOut = req.getMsgRequest().getFailureReplyMessage("Failure in partitions: " + req.getFailureReason());
                req.getRequestOwner().tell(msgOut, GlManager.getMasterActor());
            }
            GlManager.getConsole().println();

            // TODO We must delete the requests somewhere in time
            // PS: only when we don't need it for sure
        }
        // registar a msg e verificar se já terminou e se sim dar a resposta ao cliente
    }

    private T[] getDataInternal(MasterRequest req) {
        // TODO Work with partial request replies, consider that some responses may not come and the idx logic will not work.
        // reserve space to data
        T[] dataReply = null;

        // get all data
        for (int i = 0, idx = 0, size = partitionsDescriptors.size(); i < size; i++) {
            @SuppressWarnings("unchecked")
            MsgPartitionGetDataDDObjectReply<T> msgReply = (MsgPartitionGetDataDDObjectReply<T>) req.answers.get(i);
            T[] partitionData = msgReply.getData();
            if(dataReply == null) {
                // build result array from array from first message
                dataReply = Arrays.copyOf(partitionData, req.getNElemsReceived());
            }
            else {
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
    public void fireMsgPartitionApplyFunctionDDObjectReply(MsgPartitionApplyFunctionDDObjectReply msg) {
        MasterRequest req = requests.get(msg.getRequestId());

        // add answer to request
        req.addAnswer(msg);

        // update partition state
        DDMaster newDD = GlManager.getDDManager().getDD(msg.getNewDDUI());
        newDD.partitionsDescriptors.get(msg.getPartId()).setState(msg.isSuccess() ?
                DDPartitionDescriptor.PartitionState.DEPLOYED :
                DDPartitionDescriptor.PartitionState.DEPLOYED_FAILED);

        // checking if request is finished
        if (req.getState() != MasterRequest.REQUEST_STATE.WAITING) {
            if (req.getState() == MasterRequest.REQUEST_STATE.SUCCESS) {
                // success - send success to client requester
                Msg msgOut = ((MsgApplyFunctionDDObject)req.getMsgRequest()).getSuccessReplyMessage();
                req.getRequestOwner().tell(msgOut, GlManager.getMasterActor());
            } else {
                // failure - send failure to client requester
                Msg msgOut = req.getMsgRequest().getFailureReplyMessage("Failure in partitions: " + req.getFailureReason());
                req.getRequestOwner().tell(msgOut, GlManager.getMasterActor());
            }
            GlManager.getConsole().println();
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

        // checking if request is finished
        if (req.getState() != MasterRequest.REQUEST_STATE.WAITING) {
            if (req.getState() == MasterRequest.REQUEST_STATE.SUCCESS) {
                // success - send success to client requester

                Msg msgOut = ((MsgApplyFilterDDObject)req.getMsgRequest()).getSuccessReplyMessage(newDD.nDataElems);
                req.getRequestOwner().tell(msgOut, GlManager.getMasterActor());
            } else {
                // failure - send failure to client requester
                Msg msgOut = req.getMsgRequest().getFailureReplyMessage("Failure in partitions: " + req.getFailureReason());
                req.getRequestOwner().tell(msgOut, GlManager.getMasterActor());
            }
            GlManager.getConsole().println();
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
                Msg msgOut = ((MsgApplyMergeDDObject)req.getMsgRequest()).getSuccessReplyMessage(nDataElems);
                req.getRequestOwner().tell(msgOut, GlManager.getMasterActor());

            } else {
                // failure - send failure to client requester
                Msg msgOut = req.getMsgRequest().getFailureReplyMessage("Failure in partitions: " + req.getFailureReason());
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
                Msg msgOut = ((MsgApplyReduceDDObject<T>)req.getMsgRequest()).getSuccessReplyMessage(result);
                req.getRequestOwner().tell(msgOut, GlManager.getMasterActor());
            } else {
                // failure - send failure to client requester
                Msg msgOut = req.getMsgRequest().getFailureReplyMessage("Failure in partitions: " + req.getFailureReason());
                req.getRequestOwner().tell(msgOut, GlManager.getMasterActor());
            }
            GlManager.getConsole().println();
        }
    }

    public void fireMsgPartitionGetCountDDObjectReply(MsgPartitionGetCountDDObjectReply msg) {
        // get request and add msg to it
        MasterRequest req = requests.get(msg.getRequestId());
        req.addAnswer(msg);

        // checking if request is finished
        if (req.getState() != MasterRequest.REQUEST_STATE.WAITING) {
            if (req.getState() == MasterRequest.REQUEST_STATE.SUCCESS) {

                //count
                int count = 0;
                for (int partIdx : req.getAnswers().keySet()) {
                    count += ((MsgPartitionGetCountDDObjectReply) (req.getAnswers().get(partIdx))).getCount();
                }

                // send result to client requester
                Msg msgOut = ((MsgGetCountDDObject)req.getMsgRequest()).getSuccessReplyMessage(count);
                req.getRequestOwner().tell(msgOut, GlManager.getMasterActor());
            } else {
                // failure - send failure to client requester
                Msg msgOut = req.getMsgRequest().getFailureReplyMessage("Failure in partitions: " + req.getFailureReason());
                req.getRequestOwner().tell(msgOut, GlManager.getMasterActor());
            }
            GlManager.getConsole().println();
        }
    }

    private T calculateReduceResults(MasterRequest req) {
        T result = null;
        HashMap<Integer, MsgPartitionReply> replies = req.getAnswers();
        for ( int partIdx :replies.keySet()) {
            T partResult = ((MsgPartitionApplyReduceDDObjectReply<T>) (replies.get(partIdx))).getResult();
            if (result == null)
                result = partResult;
            else {
                @SuppressWarnings("unchecked")
                MsgApplyReduceDDObject<T> m = (MsgApplyReduceDDObject<T>) (req.getMsgRequest());
                result = m.getReduction().reduce(partResult, result);
            }
        }
        return result;
    }

}

