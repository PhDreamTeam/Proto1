package unl.fct.di.proto1.common.lib.core;

import unl.fct.di.proto1.common.lib.ActorNode;
import unl.fct.di.proto1.common.lib.core.master.MasterRequest;
import unl.fct.di.proto1.common.lib.protocol.MsgPartitionRequest;

/**
 * Created by AT DR on 18-02-2015.
 *
 */
public class SentMsgRequestTracker {
    ActorNode anDestiny;
    MsgPartitionRequest msg;
    MasterRequest masterRequest;

    public SentMsgRequestTracker(ActorNode anDestiny, MsgPartitionRequest msg, MasterRequest masterRequest) {
        this.anDestiny = anDestiny;
        this.msg = msg;
        this.masterRequest = masterRequest;
    }

    public ActorNode getActorNodeDestiny() {
        return anDestiny;
    }

    public MsgPartitionRequest getMsg() {
        return msg;
    }

    public MasterRequest getMasterRequest() {
        return masterRequest;
    }
}
