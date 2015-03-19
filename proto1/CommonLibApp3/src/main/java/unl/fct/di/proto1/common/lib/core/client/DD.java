package unl.fct.di.proto1.common.lib.core.client;

import unl.fct.di.proto1.common.lib.protocol.Msg;

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
    HashMap<String, Msg> receivedMsgsHashMap = new HashMap<>();

    public DD(DD parentDD, int nDataElems) {
        DDUI = UUID.randomUUID().toString();
        this.parent = parentDD;
        this.nDataElems = nDataElems;
    }

    public DD(String DDUI) {
        this.DDUI = DDUI;
    }

//    public abstract int[] getData();

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

//    public abstract DD forEach(Function<Integer, Integer> action);

    // Map objects to another DD as specified by a Function object
//    public abstract <R> Stream<R> map(Function<Integer, ? extends R> mapper);

    // Filter objects that match a Predicate object
//    public abstract DD filter(Predicate<Integer> predicate);

    @Override
    public String toString() {
        return getDDUI() + " " + getNDataElems();
    }


//    public abstract void fireMsgCreateDDIntReply(MsgCreateDDIntReply msg);

//    public abstract void fireMsgGetDataDDIntReply(MsgGetDataDDIntReply msg);

//    public abstract void fireMsgApplyFunctionDDIntReply(MsgApplyFunctionDDIntReply msg);

//    public abstract void fireMsgApplyFilterDDIntReply(MsgApplyFilterDDIntReply msg);
}
