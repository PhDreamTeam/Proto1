package unl.fct.di.proto1.common.lib.core.client;

import unl.fct.di.proto1.common.lib.protocol.Msg;
import unl.fct.di.proto1.common.lib.protocol.MsgReply;

import java.util.HashMap;
import java.util.UUID;


public abstract class DD {
    // DD Unique Identifier
    String DDUI = null;

    // number of elements in this DD
    int nDataElems = 0;

    // parent DD
    DD parent = null;

    // error result from last operation
    String lastOperationError = null;

    //hashmaps to handle sent and received msgs (e.g. allow access to the result of multiple reduce operations)
    HashMap<String, Msg> sentMsgsHashMap = new HashMap<>();
    HashMap<String, MsgReply> receivedMsgsHashMap = new HashMap<>();

    public DD(DD parentDD, int nDataElems) {
        DDUI = UUID.randomUUID().toString();
        this.parent = parentDD;
        this.nDataElems = nDataElems;
    }

    public DD(String DDUI) {
        this.DDUI = DDUI;
    }

    public String getDDUI() {
        return DDUI;
    }

    public int getNDataElems() {
        return nDataElems;
    }

    public String getLastOperationError() {
        return lastOperationError;
    }

    public void setLastOperationError(String lastOperationError) {
        this.lastOperationError = lastOperationError;
    }

    @Override
    public String toString() {
        return getDDUI() + " " + getNDataElems();
    }



}
