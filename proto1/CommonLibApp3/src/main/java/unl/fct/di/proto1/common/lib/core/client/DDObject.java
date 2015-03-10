package unl.fct.di.proto1.common.lib.core.client;

import scala.collection.immutable.Stream;
import unl.fct.di.proto1.common.lib.protocol.DDObject.*;
import unl.fct.di.proto1.common.lib.tools.BaseActions.Function;
import unl.fct.di.proto1.common.lib.tools.BaseActions.Predicate;

import java.util.UUID;


public class DDObject extends DD {

    // space to be used only with getData
    Object[] data = null;

    // create a new DD based on an array
    public DDObject(Object[] data) {
        // create super DD with a new UUDI and with no parent and number of elems
        super(null, data.length);

        // save this DD in the Manager
        ClientManager.putDD(this);

        // send msg to create DD in master
        String requestId = UUID.randomUUID().toString();
        MsgCreateDDObject msg = new MsgCreateDDObject(DDUI, requestId, data);
        ClientManager.getMasterActor().tell(msg, ClientManager.getClientActor());
        // show it in screen
        ClientManager.getConsole().println("Sent: " + msg);

        // wait for completion of create DD
        try {
            synchronized (this) {
                this.wait();
            }
        } catch (InterruptedException e) {
            ClientManager.getConsole().printException(e);
        }

        if (lastOperationError != null) {
            throw new RuntimeException(lastOperationError);
        }
    }

    // create a DD access object from the DDUI of a DD already in system
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

    /**
     * To open a connection to a DD already existent in master
     */
    private DDObject(String DDUI) {
        super(DDUI);

        // save this DD in the Manager
        ClientManager.putDD(this);

        // send msg to open DD in master
        String requestId = UUID.randomUUID().toString();
        MsgOpenDDObject msg = new MsgOpenDDObject(DDUI, requestId);
        ClientManager.getMasterActor().tell(msg, ClientManager.getClientActor());
        // show it in screen
        ClientManager.getConsole().println("Sent: " + msg);

        // wait for completion of create DD
        try {
            synchronized (this) {
                this.wait();
            }
        } catch (InterruptedException e) {
            ClientManager.getConsole().printException(e);
        }

        if (lastOperationError != null) {
            throw new RuntimeException(lastOperationError);
        }
    }

    // Must be private - client should not call this constructor
    private DDObject(DDObject parentDD) {
        // create super DD with a new UUDI and with the parent and number of elems
        super(parentDD, parentDD.getNDataElems());

        // save this DDInt in the Manager
        ClientManager.putDD(this);
    }


    public Object[] getData() {
        // send MsgGetData to master
        String requestId = UUID.randomUUID().toString();
        MsgGetDataDDObject msg = new MsgGetDataDDObject(DDUI, requestId);
        ClientManager.getMasterActor().tell(msg, ClientManager.getClientActor());

        // show it in screen
        ClientManager.getConsole().println("Sent: " + msg);

        // wait for data of DDInt
        try {
            synchronized (this) {
                this.wait();
            }
        } catch (InterruptedException e) {
            ClientManager.getConsole().printException(e);
        }

        if (lastOperationError != null) {
            throw new RuntimeException(lastOperationError);
        }

        return data;
    }


    // aggregate operations

    /**
     * Perform an action as specified by a Consumer object
     * This method is executed by a independent thread
     * This method should be blocking
     *
     * @param action to be performed in the DD elements,
     *               should produce aa new element of the same type
     * @return the new DD
     */
    public DDObject forEach(Function<Object, Object> action) {
        // create new DDInt to store the results
        DDObject newDD = new DDObject(this);


        // send msg to master
        String requestId = UUID.randomUUID().toString();
        MsgApplyFunctionDDObject msg = new MsgApplyFunctionDDObject(DDUI, requestId, newDD.getDDUI(), action);
        ClientManager.getMasterActor().tell(msg, ClientManager.getClientActor());

        // show it in screen
        ClientManager.getConsole().println("Sent: " + msg);

        // wait for operation conclusion in new DDInt
        try {
            synchronized (newDD) {
                newDD.wait();
            }
        } catch (InterruptedException e) {
            ClientManager.getConsole().printException(e);
        }

        // get result from new DDInt
        if (newDD.getLastOperationError() != null) {
            throw new RuntimeException(newDD.getLastOperationError());
        }

        return newDD;
    }

    // Map objects to another DD as specified by a Function object
    public <R> Stream<R> map(Function<Integer, ? extends R> mapper) {
        //for (DDPartitionInt partition : partitionsDescriptors) {
        //    partition.map(mapper);
        //}
        // TODO
        return null;
    }

    // Filter objects that match a Predicate object
    public DDObject filter(Predicate<Object> predicate) {
        // create new DDInt to store the results
        DDObject newDD = new DDObject(this);


        // send msg to master
        String requestId = UUID.randomUUID().toString();
        MsgApplyFilterDDObject msg = new MsgApplyFilterDDObject(DDUI, requestId, newDD.getDDUI(),
                predicate);
        ClientManager.getMasterActor().tell(msg, ClientManager.getClientActor());

        // show it in screen
        ClientManager.getConsole().println("Sent: " + msg);

        // wait for operation conclusion in new DDInt - to enable more operations in old DD
        try {
            synchronized (newDD) {
                newDD.wait();
            }
        } catch (InterruptedException e) {
            ClientManager.getConsole().printException(e);
        }

        // get result from new DDInt
        if (newDD.getLastOperationError() != null) {
            throw new RuntimeException(newDD.getLastOperationError());
        }
        return newDD;
    }


    @Override
    public String toString() {
        return "DDObject " + super.toString();
    }

    public void fireMsgCreateDDObjectReply(MsgCreateDDObjectReply msg) {
        if (msg.isSuccess()) {
            // success: nothing to do
            lastOperationError = null;
        } else {
            // failure:
            lastOperationError = "Error creating DDObject: " + msg.getFailureReason();

            // remove previously created DD - this DD does not exist in Master
            ClientManager.removeDD(msg.getDDUI());
        }

        // wake up client thread that asked to create DDInt
        synchronized (this) {
            this.notify();
        }
    }

    public void fireMsgOpenDDObjectReply(MsgOpenDDObjectReply msg) {
        if (msg.isSuccess()) {
            // success: nothing to do
            lastOperationError = null;

            // save number of elems
            nDataElems = msg.getnDataElemsDD();

        } else {
            // failure:
            lastOperationError = "Error opening DDObject: " + DDUI + ", DDUI not recognized!: " + msg.getFailureReason();

            // remove previously created DD - this DD does not exist in Master
            ClientManager.removeDD(msg.getDDUI());
        }

        // wake up client thread that asked to create DDInt
        synchronized (this) {
            this.notify();
        }
    }


    public void fireMsgGetDataDDObjectReply(MsgGetDataDDObjectReply msg) {
        if (msg.isSuccess()) {
            // get data reference
            data = msg.getData();
            lastOperationError = null;
        } else {
            // no data
            data = null;
            lastOperationError = "Error in get data: " + msg.getFailureReason();
        }

        // wake up client thread that asked to create DDInt
        synchronized (this) {
            this.notify();
        }
    }

    public void fireMsgApplyFunctionDDObjectReply(MsgApplyFunctionDDObjectReply msg) {
        if (msg.isSuccess()) {
            // success: nothing to do
            lastOperationError = null;
        } else {
            // failure:
            lastOperationError = "Error applying function: " + msg.getFailureReason();
        }

        // wake up client thread that asked to create DDInt
        synchronized (this) {
            this.notify();
        }
    }

    public void fireMsgApplyFilterDDObjectReply(MsgApplyFilterDDObjectReply msg) {
        if (msg.isSuccess()) {
            // success: nothing to do
            lastOperationError = null;

            // save number of elems
            nDataElems = msg.getnDataElemsDD();

        } else {
            // failure:
            lastOperationError = "Error applying filter: " + msg.getFailureReason();
        }

        // wake up client thread that asked to create DDInt
        synchronized (this) {
            this.notify();
        }
    }
}
