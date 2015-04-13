package unl.fct.di.proto1.common.lib.core.master;

import akka.actor.ActorRef;
import akka.actor.Cancellable;
import scala.concurrent.duration.Duration;
import unl.fct.di.proto1.common.lib.ActorNode;
import unl.fct.di.proto1.common.lib.core.SentMsgRequestTracker;
import unl.fct.di.proto1.common.lib.protocol.Msg;
import unl.fct.di.proto1.common.lib.protocol.MsgPartitionReply;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

// TODO We must delete the requests somewhere in time
// PS: only when we don't need it for sure

public class MasterRequest implements Serializable {
    enum REQUEST_STATE {WAITING, FAILED, SUCCESS}

    private HashMap<Integer, MsgPartitionReply> answers = new HashMap<>();
    private ArrayList<SentMsgRequestTracker> pendingRequests = new ArrayList<>();

    private String requestId;

    private ActorNode requestOwnerActorNode;

    private DDMaster dd;

    private boolean failed = false;

    private boolean receivedAtLeastOneSuccessMessage = false;

    private REQUEST_STATE state = REQUEST_STATE.WAITING;

    private Cancellable timeout = null;

    private Msg msgRequest;

    private String failureReason = "";

    // to count the number the received elements in getdata
    private int nElemsReceived = 0;

    private boolean allowIncompleteResults;


    /*
     *
     */
    public MasterRequest(String requestId, ActorNode requestOwner, DDMaster dd, Msg msgRequest, boolean allowIncompleteResults) {
        this.requestId = requestId;
        this.dd = dd;
        requestOwnerActorNode = requestOwner;
        this.msgRequest = msgRequest;
        this.allowIncompleteResults = allowIncompleteResults;
    }

    public void addReqTrk(SentMsgRequestTracker reqTrk) {
        pendingRequests.add(reqTrk);
    }

    public String getRequestId() {
        return requestId;
    }

    public ActorRef getRequestOwner() {
        return requestOwnerActorNode.getActorRef();
    }

    public REQUEST_STATE getState() {
        return state;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public Msg getMsgRequest() {
        return msgRequest;
    }

    public int getNElemsReceived() {
        return nElemsReceived;
    }
    public void addElemsReceived(int nElems) {
        nElemsReceived += nElems;
    }

    public boolean allowIncompleteResults() {
        return allowIncompleteResults;
    }

    public boolean receivedAtLeastOneSuccessMessage() {
        return receivedAtLeastOneSuccessMessage;
    }

    public void addAnswer(MsgPartitionReply msgReply) {
        if (!deletePendingMsgTrkRequest(msgReply)) {
            GlManager.getConsole().println("Received unexpected msgPartitionReply: " + msgReply);
            return;
        }

        // checking for failure
        if (!msgReply.isSuccess()) {
            failed = true;
            if (failureReason.length() != 0)
                failureReason += ", ";
            failureReason += "partId: " + msgReply.getPartId() + " " + msgReply.getFailureReason();
        } else {
            receivedAtLeastOneSuccessMessage = true;
        }

        // add answer
        answers.put(msgReply.getPartId(), msgReply);

        // set request state
        if (dd.getNPartitions() == answers.size()) {
            state = failed ? REQUEST_STATE.FAILED : REQUEST_STATE.SUCCESS;
            // Cancel timeout actions
            // avoid unnecessary firing of timeout
            timeout.cancel();
        }
    }

    public HashMap<Integer, MsgPartitionReply> getAnswers() {
        return answers;
    }

    private void deleteAllPendingMsgTrkRequests() {
        pendingRequests.clear();
    }

    private boolean deletePendingMsgTrkRequest(MsgPartitionReply msgReply) {
        for (Iterator<SentMsgRequestTracker> it = pendingRequests.iterator(); it.hasNext(); ) {
            SentMsgRequestTracker rt = it.next();
            if (rt.getMsg().getDDUI().equals(msgReply.getDDUI()) &&
                    rt.getMsg().getRequestId().equals(msgReply.getRequestId()) &&
                    rt.getMsg().getPartId() == msgReply.getPartId()) {
                it.remove();
                return true;
            }
        }
        return false;
    }

    public ActorNode getRequestOwnerActorNode() {
        return requestOwnerActorNode;
    }

    public void setRequestOwnerActorNode(ActorNode requestOwnerActorNode) {
        this.requestOwnerActorNode = requestOwnerActorNode;
    }

    public Cancellable getTimeout() {
        return timeout;
    }

    public Cancellable createTimeout(final Runnable timeoutCustomProcedure) {
        // create timeout
        timeout = GlManager.getMasterService().getSystem().scheduler().scheduleOnce(
                Duration.create(30, TimeUnit.SECONDS), new Runnable() {
                    @Override
                    public void run() {
                        // fire timeout
                        fireTimeout(timeoutCustomProcedure);
                    }
                }, GlManager.getMasterService().getSystem().dispatcher());
        return timeout;
    }

    public void fireTimeout(Runnable timeoutCustomProcedure) {
        if (getState().equals(REQUEST_STATE.WAITING)) {
            // cancel request and return TIMEOUT or incomplete results
            state = REQUEST_STATE.FAILED;
            failed = true;
            failureReason = "TIMEOUT " + (!failureReason.equals("") ? "with: " + failureReason : "");

            GlManager.getConsole().println("Timeout fired: " + this.getMsgRequest());

            // checking incomplete results
            if(allowIncompleteResults && (timeoutCustomProcedure != null)) {
                // run timeout custom procedure
               timeoutCustomProcedure.run();
            }
            else {
                // return failure
                getRequestOwner().tell(msgRequest.getFailureReplyMessage(failureReason), GlManager.getMasterActor());
            }

            // remove all pending trkReqs from this MasterRequest in CommunicationHelper List
            GlManager.getCommunicationHelper().removePendingTrackers(this);

            // remove pending trkReqs in this MasterRequest
            deleteAllPendingMsgTrkRequests();
        }
        // else request already completed, nothing to do
    }
}
