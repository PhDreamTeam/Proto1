package unl.fct.di.proto1.common.lib.protocol;

import unl.fct.di.proto1.common.lib.ActorType;

import java.io.Serializable;

public class MsgRegister extends Msg implements Serializable {
    ActorType type;

    public MsgRegister(String requestId, ActorType type) {
        super(null, requestId);
        this.type = type;
    }

    public ActorType getType() {
        return type;
    }

    @Override
    public String toString() {
        return super.toString() + " " + getType();
    }
}
