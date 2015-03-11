package unl.fct.di.proto1.common.lib.core.master;

import akka.actor.ActorRef;
import unl.fct.di.proto1.common.lib.ActorNode;
import unl.fct.di.proto1.common.lib.protocol.DDObject.MsgOpenDDObject;
import unl.fct.di.proto1.common.lib.protocol.DDObject.MsgOpenDDObjectReply;
import unl.fct.di.proto1.common.lib.protocol.Msg;
import unl.fct.di.proto1.common.masterService.MasterService;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by AT DR on 12-02-2015.
 *
 */
public class DDMaster implements Serializable {
    // DD Unique Identifier
    String DDUI;
    // number of elements in this DD
    int nDataElems;
    // partitionsDescriptors
    ArrayList<DDPartitionDescriptor> partitionsDescriptors = new ArrayList<>();
    // DD parent
    DDMaster parentDD = null;
    // DDIntMaster owner
    ActorNode clientOwnerActorNode = null;

    //TODO consider storing the pending and finished request in a backup
    //NOTE: consider removing finished request vs maintaining history
     HashMap<String, MasterRequest> requests = new HashMap<>();


    public DDMaster(String DDUI, int nDataElems, DDMaster parentDD, ActorNode clientOwnerActorNode) {
        this.DDUI = DDUI;
        this.nDataElems = nDataElems;
        this.parentDD = parentDD;
        this.clientOwnerActorNode = clientOwnerActorNode;
    }

    public MasterRequest createMasterRequest(String requestId, ActorNode requestOwner, Msg msgRequest){
        MasterRequest req = new MasterRequest(requestId, requestOwner, this, msgRequest);
        requests.put(req.getRequestId(), req);
        return req;
    }


    protected void copyPartitions(DDMaster parentDD) {
        for (int i = 0, nParts = parentDD.getNPartitions(); i < nParts; i++) {
            // create new partition descriptor
            DDPartitionDescriptor partDesc = new DDPartitionDescriptor(DDUI,
                    parentDD.partitionsDescriptors.get(i));
            // add partition to partitionsDescriptors collection
            partitionsDescriptors.add(partDesc);
        }
    }

    void resetNDataElems() {
        nDataElems = 0;
//        for (int i = 0, nPartitions = partitionsDescriptors.size(); i < nPartitions; i++) {
//            partitionsDescriptors.get(i).setNElems(0);
//        }
    }

    public String getDDUI() {
        return DDUI;
    }

    public int getNDataElems() {
        return nDataElems;
    }

    public int getNPartitions () {
        return partitionsDescriptors.size();
    }

    public void updateReferences(MasterService ms) {

        // merge clientOwnerActorNode with previously stored state
        ActorNode resultActorNode = ms.addOrUpdateActorNode(clientOwnerActorNode);
        if(clientOwnerActorNode != resultActorNode)
            clientOwnerActorNode = resultActorNode;

        // merge partition descriptors worker nodes
        for(DDPartitionDescriptor part: partitionsDescriptors) {
            ActorNode an = part.getWorkerNode();
            resultActorNode = ms.addOrUpdateActorNode(an);
            // if new node, set new node as worker node
            if(an != resultActorNode)
                part.setWorkerNode(resultActorNode);
        }

        // merge request requesters owner nodes
        for(String key : requests.keySet()) {
            MasterRequest req = requests.get(key);

            ActorNode an = req.getRequestOwnerActorNode();
            resultActorNode = ms.addOrUpdateActorNode(an);
            // if new node, set new node as updated client requester node
            if(an != resultActorNode)
                req.setRequestOwnerActorNode(resultActorNode);
        }
    }

    public static void OpenDDMaster(DDMaster dd, ActorNode actorNodeRequester, ActorRef masterActorRef,
                                        MsgOpenDDObject msg, Class DDMasterTypeClass) {
        Msg msgOut;
        if((dd != null) && (DDMasterTypeClass.isInstance(dd))) {
            // send success: the DD exist
            msgOut =  new MsgOpenDDObjectReply(msg.getDDUI(), msg.getRequestId(), dd.getNDataElems(), true, null);
        } else {
            // not a DDObject: send error to client
            msgOut =  msg.getFailureReplyMessage("DDUI not found or doesn't have the required type");
        }
        actorNodeRequester.getActorRef().tell(msgOut, masterActorRef);
        GlManager.getConsole().println("Sent: " + msgOut);
    }

    public DDMASTERSTATE getState() {
        boolean success = true;
        for (DDPartitionDescriptor p : partitionsDescriptors) {
            if (p.getState().equals(DDPartitionDescriptor.PartitionState.WAITING_WORKER_CREATE_REPLY))
                return DDMASTERSTATE.WAITING_WORKER_CREATE;
            if (p.getState().equals(DDPartitionDescriptor.PartitionState.DEPLOYED_FAILED))
                success = false;
        }
        if (success) {
            return DDMASTERSTATE.DEPLOYED;
        }
        return DDMASTERSTATE.DEPLOYED_FAILED;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName().substring(2, getClass().getSimpleName().length() - 6) +
                " " + getDDUI() + " nelems: " + getNDataElems() +
                " nparts: " + partitionsDescriptors.size() + " " + getState();
    }
}
