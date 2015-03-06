package unl.fct.di.proto1.common.lib.protocol;

import unl.fct.di.proto1.common.lib.ActorNode;

import java.io.Serializable;


public class MsgGetMasterReply extends Msg implements Serializable {
    ActorNode an;


    public MsgGetMasterReply(String requestId, ActorNode an) {
        super(null, requestId);
        this.an = an;
    }


    public ActorNode getActorNode() {
        return an;
    }

    @Override
    public String toString() {
        return super.toString() + " " + an;
    }
}
