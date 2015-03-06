package unl.fct.di.proto1.common.lib.core;

import akka.actor.ActorRef;
import unl.fct.di.proto1.common.lib.ActorNode;
import unl.fct.di.proto1.common.lib.ActorState;
import unl.fct.di.proto1.common.lib.core.master.GlManager;
import unl.fct.di.proto1.common.lib.core.master.MasterRequest;
import unl.fct.di.proto1.common.lib.protocol.MsgPartitionReply;
import unl.fct.di.proto1.common.lib.protocol.MsgPartitionRequest;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by AT DR on 18-02-2015.
 */
public class CommunicationHelper {

    ArrayList<SentMsgRequestTracker> pendingMsgs = new ArrayList<>();


    public void tell(ActorNode anDestiny, MsgPartitionRequest msg, ActorRef anSender, MasterRequest masterRequest) {
        // create a tracker and keep it in tracker container and in its masterRequest
        SentMsgRequestTracker reqTrk = new SentMsgRequestTracker(anDestiny, msg, masterRequest);
        pendingMsgs.add(reqTrk);
        masterRequest.addReqTrk(reqTrk);

        if (anDestiny.getState().equals(ActorState.ACTIVE)) {
            anDestiny.getActorRef().tell(msg, anSender);
        }
    }

    public void removePendingTrackers(MasterRequest masterRequest) {
        for (Iterator<SentMsgRequestTracker> it = pendingMsgs.iterator(); it.hasNext(); ) {
            if (it.next().getMasterRequest().equals(masterRequest))
                it.remove();
        }
    }


    public SentMsgRequestTracker getMessage(MsgPartitionReply msgReceived) {
        for (SentMsgRequestTracker rt : pendingMsgs)
            if (rt.getMsg().getDDUI().equals(msgReceived.getDDUI()) &&
                    rt.getMsg().getRequestId().equals(msgReceived.getRequestId()) &&
                    rt.getMsg().getPartId() == msgReceived.getPartId())
                return rt;
        return null;
    }

    /**
     * Get and if found remove the message that have the same RequestID of the msgReceived message (in argument)
     *
     * @param msgReceived
     */
    public SentMsgRequestTracker getAndRemoveOriginalPendingMessage(MsgPartitionReply msgReceived) {
        for (Iterator<SentMsgRequestTracker> it = pendingMsgs.iterator(); it.hasNext(); ) {
            SentMsgRequestTracker rt = it.next();
            if (rt.getMsg().getDDUI().equals(msgReceived.getDDUI()) &&
                    rt.getMsg().getRequestId().equals(msgReceived.getRequestId()) &&
                    rt.getMsg().getPartId() == msgReceived.getPartId()) {
                it.remove();
                return rt;
            }
        }
        return null;
    }

    /**
     * Check if exists pending messages for this actor and resend them
     * @param an the actor node
     */
    public void doPendingActions(ActorNode an) {
        for (SentMsgRequestTracker rt : pendingMsgs)
            if (rt.getActorNodeDestiny().equals(an)) {
                // resend message
                an.getActorRef().tell(rt.getMsg(), GlManager.getMasterActor());
                // TODO: solve this: GlManager only can be used in master
                GlManager.getConsole().println("Sent pending msg: " + rt.getMsg() + " to " + an.getActorName());
            }
    }
}


