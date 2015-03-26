package unl.fct.di.proto1.common.lib.core.client;

import unl.fct.di.proto1.common.lib.protocol.DDObject.*;
import unl.fct.di.proto1.common.lib.protocol.Msg;
import unl.fct.di.proto1.common.lib.protocol.MsgReply;
import unl.fct.di.proto1.common.lib.tools.BaseActions.Function;
import unl.fct.di.proto1.common.lib.tools.BaseActions.Predicate;
import unl.fct.di.proto1.common.lib.tools.BaseActions.Reduction;

import java.util.UUID;


public class DDObject<T> extends DD {

    // create a new DD based on an array
    public DDObject(T[] data) {
        // create super DD with a new UUDI and with no parent and number of elems, save DD
        super(null, data.length);
        String requestId = UUID.randomUUID().toString();
        ClientManager.putDD(this);

        // create Create DD Msg, keep it in sentMsgsHashMap, send it to master and show it on screen
        MsgCreateDDObject<T> msg = new MsgCreateDDObject<>(DDUI, requestId, data);
        sentMsgsHashMap.put(requestId, msg);
        ClientManager.getMasterActor().tell(msg, ClientManager.getClientActor());
        ClientManager.getConsole().println("Sent: " + msg);

        // wait for completion of create DD
        try {
            //noinspection SynchronizationOnLocalVariableOrMethodParameter
            synchronized (msg) {
                msg.wait();
            }
        } catch (InterruptedException e) {
            ClientManager.getConsole().printException(e);
        }

        // process reply and return result
        MsgReply replyMsg = receivedMsgsHashMap.get(requestId);
        if (!replyMsg.isSuccess()) {
            // remove this DD - and throw exception
            ClientManager.removeDD(this.getDDUI());
            throw new RuntimeException("Error Creating DD: " + replyMsg.getFailureReason());
        }

        // clean up original and reply messages
        sentMsgsHashMap.remove(msg.getRequestId());
        receivedMsgsHashMap.remove(replyMsg.getRequestId());
    }

    /**
     * To open a connection to a DD already existent in master
     */
    private DDObject(String DDUI) {
        // create new DDObject to store the results, create RequestId
        super(DDUI);
        String requestId = UUID.randomUUID().toString();
        ClientManager.putDD(this);

        // create Open msg, keep it in sentMsgsHashMap, send it to master and show it on screen
        MsgOpenDDObject msg = new MsgOpenDDObject(DDUI, requestId);
        sentMsgsHashMap.put(requestId, msg);
        ClientManager.getMasterActor().tell(msg, ClientManager.getClientActor());
        ClientManager.getConsole().println("Sent: " + msg);

        // wait for completion of create DD on sent msg
        try {
            //noinspection SynchronizationOnLocalVariableOrMethodParameter
            synchronized (msg) {
                msg.wait();
            }
        } catch (InterruptedException e) {
            ClientManager.getConsole().printException(e);
        }

        // process reply and return result
        MsgOpenDDObjectReply replyMsg = (MsgOpenDDObjectReply) (receivedMsgsHashMap.get(requestId));
        if (!replyMsg.isSuccess()) {
            // remove this DD - and throw exception
            ClientManager.removeDD(this.getDDUI());
            throw new RuntimeException("Error Opening DD: " + replyMsg.getFailureReason());
        } else {
            // save number of elems
            nDataElems = replyMsg.getnDataElemsDD();
        }

        // clean up original and reply messages
        sentMsgsHashMap.remove(msg.getRequestId());
        receivedMsgsHashMap.remove(replyMsg.getRequestId());
    }

    // Must be private - client should not call this constructor
    private DDObject(DDObject parentDD) {
        // create super DD with a new UUDI and with the parent and number of elems
        super(parentDD, parentDD.getNDataElems());

        // save this DDInt in the Manager
        ClientManager.putDD(this);
    }

    // create a DD access object from the DDUI of a DD already in system
    // TODO generic ??
    static public DDObject openDDObject(String DDUI) throws Exception {
        DD dd = ClientManager.getDD(DDUI);
        if (dd != null) {
            if (dd instanceof DDObject) {
                //open an existing local DD
                return (DDObject) dd;
            } else {
                throw new Exception("DDUI of an unexpected type. Found " +
                        dd.getClass().getSimpleName() + " with the same DDUI. Should be a DDObject");
            }
        } else {
            // open a remote DDUI
            return new DDObject(DDUI);
        }
    }

    // get Data from the remote workers (client<->master<->worker)
    public T[] getData() {
        // create new DDObject to store the results, create RequestId
        String requestId = UUID.randomUUID().toString();

        // create getData msg, keep it in sentMsgsHashMap, send it to master and show it on screen
        MsgGetDataDDObject msg = new MsgGetDataDDObject(DDUI, requestId);
        sentMsgsHashMap.put(requestId, msg);
        ClientManager.getMasterActor().tell(msg, ClientManager.getClientActor());
        ClientManager.getConsole().println("Sent: " + msg);

        // wait for data of DDObject
        try {
            //noinspection SynchronizationOnLocalVariableOrMethodParameter
            synchronized (msg) {
                msg.wait();
            }
        } catch (InterruptedException e) {
            ClientManager.getConsole().printException(e);
        }

        // process reply and return result
        @SuppressWarnings("unchecked")
        MsgGetDataDDObjectReply<T> replyMsg = (MsgGetDataDDObjectReply<T> ) (receivedMsgsHashMap.get(requestId));
        if (!replyMsg.isSuccess()) {
            throw new RuntimeException("Error Getting Data: " + replyMsg.getFailureReason());
        }

        T[] data = replyMsg.getData();

        // clean up original and reply messages
        sentMsgsHashMap.remove(msg.getRequestId());
        receivedMsgsHashMap.remove(replyMsg.getRequestId());

        return data;
    }

    /**************************************/
    // aggregate operations
    /**************************************/

    /**
     * Perform an action as specified by a Consumer object
     * This method is executed by a independent thread
     * This method should be blocking
     *
     * @param action to be performed in the DD elements,
     *               should produce aa new element of the same type
     * @return the new DD
     */
    public <R> DDObject<R> forEach(Function<T, R> action, R[] arrayRType) {
        // create new DDObject to store the results, create RequestId
        DDObject<R> newDD = new DDObject<>(this);
        String requestId = UUID.randomUUID().toString();

        // create forEach msg, keep it in sentMsgsHashMap, send it to master and show it on screen
        MsgApplyFunctionDDObject<T, R> msg = new MsgApplyFunctionDDObject<>(DDUI, requestId, newDD.getDDUI(), action, arrayRType);
        sentMsgsHashMap.put(requestId, msg);
        ClientManager.getMasterActor().tell(msg, ClientManager.getClientActor());
        ClientManager.getConsole().println("Sent: " + msg);

        // wait for operation conclusion in the sent Msg
        try {
            //noinspection SynchronizationOnLocalVariableOrMethodParameter
            synchronized (msg) {
                msg.wait();
            }
        } catch (InterruptedException e) {
            ClientManager.getConsole().printException(e);
        }

        // get result from new DDObject
        MsgReply replyMsg = receivedMsgsHashMap.get(requestId);
        if (!replyMsg.isSuccess()) {
            // remove newDD - and throw exception
            ClientManager.removeDD(msg.getNewDDUI());
            throw new RuntimeException("Error applying Function: " + replyMsg.getFailureReason());
        }

        // clean up original and reply messages
        sentMsgsHashMap.remove(msg.getRequestId());
        receivedMsgsHashMap.remove(replyMsg.getRequestId());

        return newDD;
    }

    public DDObject<T> merge(DDObject<T> ddToMerge) {
        // create new DDInt to store the results, create RequestId
        DDObject<T> newDD = new DDObject<>(this);
        String requestId = UUID.randomUUID().toString();

        // create merge msg, keep it in sentMsgsHashMap, send it to master and show it on screen
        MsgApplyMergeDDObject msg = new MsgApplyMergeDDObject(DDUI, requestId, ddToMerge.getDDUI(),
                newDD.getDDUI());
        sentMsgsHashMap.put(requestId, msg);
        ClientManager.getMasterActor().tell(msg, ClientManager.getClientActor());
        ClientManager.getConsole().println("Sent: " + msg);

        // wait for operation conclusion in original msg
        try {
            //noinspection SynchronizationOnLocalVariableOrMethodParameter
            synchronized (msg) {
                msg.wait();
            }
        } catch (InterruptedException e) {
            ClientManager.getConsole().printException(e);
        }

        // process reply and return result
        MsgApplyMergeDDObjectReply replyMsg = (MsgApplyMergeDDObjectReply) (receivedMsgsHashMap.get(requestId));
        if (!replyMsg.isSuccess()) {
            // remove newDD - and throw exception
            ClientManager.removeDD(msg.getNewDDUI());
            throw new RuntimeException("Error applying Merge: " + replyMsg.getFailureReason());
        } else {
            // save number of elems
            newDD.nDataElems = replyMsg.getnDataElemsDD();
        }

        // clean up original and reply messages
        sentMsgsHashMap.remove(msg.getRequestId());
        receivedMsgsHashMap.remove(replyMsg.getRequestId());

        return newDD;
    }

    // Filter objects that match a Predicate object
    public DDObject<T> filter(Predicate<T> predicate) {
        // create new DDObject to store the results, create RequestId
        DDObject<T> newDD = new DDObject<>(this);
        String requestId = UUID.randomUUID().toString();

        // create merge msg, keep it in sentMsgsHashMap, send it to master and show it on screen
        MsgApplyFilterDDObject<T> msg = new MsgApplyFilterDDObject<>(DDUI, requestId, newDD.getDDUI(),
                predicate);
        sentMsgsHashMap.put(requestId, msg);
        ClientManager.getMasterActor().tell(msg, ClientManager.getClientActor());
        ClientManager.getConsole().println("Sent: " + msg);

        // wait for operation conclusion in original msg
        try {
            //noinspection SynchronizationOnLocalVariableOrMethodParameter
            synchronized (msg) {
                msg.wait();
            }
        } catch (InterruptedException e) {
            ClientManager.getConsole().printException(e);
        }

        // process reply and return result
        MsgApplyFilterDDObjectReply replyMsg = (MsgApplyFilterDDObjectReply) (receivedMsgsHashMap.get(requestId));
        if (!replyMsg.isSuccess()) {
            // remove newDD - and throw exception
            ClientManager.removeDD(msg.getNewDDUI());
            throw new RuntimeException("Error applying Filter: " + replyMsg.getFailureReason());
        } else {
            // save number of elems
            newDD.nDataElems = replyMsg.getnDataElemsDD();
        }

        // clean up original and reply messages
        sentMsgsHashMap.remove(msg.getRequestId());
        receivedMsgsHashMap.remove(replyMsg.getRequestId());

        return newDD;
    }

    public T reduce(Reduction<T> reduceFunction) {
        // create reqID, create msg, keep msg, send master and show it on screen
        String requestId = UUID.randomUUID().toString();
        MsgApplyReduceDDObject<T> msg = new MsgApplyReduceDDObject<>(DDUI, requestId, reduceFunction);
        sentMsgsHashMap.put(requestId, msg);
        ClientManager.getMasterActor().tell(msg, ClientManager.getClientActor());
        ClientManager.getConsole().println("Sent: " + msg);

        // wait for operation conclusion in new DDObject - to enable more operations in old DD
        try {
            //noinspection SynchronizationOnLocalVariableOrMethodParameter
            synchronized (msg) {
                msg.wait();
            }
        } catch (InterruptedException e) {
            ClientManager.getConsole().printException(e);
        }

        @SuppressWarnings("unchecked")
        MsgApplyReduceDDObjectReply<T> replyMsg = (MsgApplyReduceDDObjectReply<T>) (receivedMsgsHashMap.get(requestId));

        // get result from new DDInt
        if (!replyMsg.isSuccess()) {
            throw new RuntimeException("Error applying Reduce: " + replyMsg.getFailureReason());
        }

        // clean up original and reply messages
        sentMsgsHashMap.remove(msg.getRequestId());
        receivedMsgsHashMap.remove(replyMsg.getRequestId());

        return replyMsg.getResult();
    }

    public int count() {
        // create reqID, create msg, keep msg, send master and show it on screen
        String requestId = UUID.randomUUID().toString();
        MsgGetCountDDObject msg = new MsgGetCountDDObject(DDUI, requestId);
        sentMsgsHashMap.put(requestId, msg);
        ClientManager.getMasterActor().tell(msg, ClientManager.getClientActor());
        ClientManager.getConsole().println("Sent: " + msg);

        // wait for operation conclusion in new DDObject - to enable more operations in old DD
        try {
            //noinspection SynchronizationOnLocalVariableOrMethodParameter
            synchronized (msg) {
                msg.wait();
            }
        } catch (InterruptedException e) {
            ClientManager.getConsole().printException(e);
        }

        MsgGetCountDDObjectReply replyMsg = (MsgGetCountDDObjectReply) (receivedMsgsHashMap.get(requestId));
        if (!replyMsg.isSuccess()) {
            throw new RuntimeException("Error applying Count: " + replyMsg.getFailureReason());
        }

        // clean up original and reply messages
        sentMsgsHashMap.remove(msg.getRequestId());
        receivedMsgsHashMap.remove(replyMsg.getRequestId());

        return replyMsg.getCount();
    }

    @Override
    public String toString() {
        return "DDObject " + super.toString();
    }


    /**************************************/
    // Fire functions =====================
    /**************************************/

    public void fireMsgReply(MsgReply msgReply) {
        receivedMsgsHashMap.put(msgReply.getRequestId(), msgReply);
        Msg origSentMsg = sentMsgsHashMap.get(msgReply.getRequestId());

        // wake up client thread that asked to get Count
        //noinspection SynchronizationOnLocalVariableOrMethodParameter
        synchronized (origSentMsg) {
            origSentMsg.notify();
        }
    }
}

