package unl.fct.di.proto1.common.lib.protocol;

import unl.fct.di.proto1.common.lib.ActorType;

import java.io.Serializable;
import java.util.List;

public class MsgRegisterWorker extends MsgRegister implements Serializable {
    List<String> internalDDUIs;

    public MsgRegisterWorker(String requestId, List<String> internalDDUIs) {
        super(requestId, ActorType.Worker);
        this.internalDDUIs = internalDDUIs;
    }

    public List<String> getInternalDDUIs() {
        return internalDDUIs;
    }

    @Override
    public String toString() {
        return super.toString() + " " + getInternalDDUIs();
    }
}
