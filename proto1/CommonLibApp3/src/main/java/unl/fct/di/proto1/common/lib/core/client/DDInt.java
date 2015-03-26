package unl.fct.di.proto1.common.lib.core.client;

import unl.fct.di.proto1.common.lib.protocol.DDInt.*;
import unl.fct.di.proto1.common.lib.tools.BaseActions.Function;
import unl.fct.di.proto1.common.lib.tools.BaseActions.Predicate;

import java.util.UUID;


public class DDInt extends DD {

    // space to be used only with getData
    int[] data = null;

    // create a new DDInt based on an array
    public DDInt(int[] data) {
        // create super DD with a new UUDI and with no parent and number of elems
        super(null, data.length);

        // save this DDInt in the Manager
        ClientManager.putDD(this);

        // send msg to create DD in master
        String requestId = UUID.randomUUID().toString();
        MsgCreateDDInt msg = new MsgCreateDDInt(DDUI, requestId, data);
        ClientManager.getMasterActor().tell(msg, ClientManager.getClientActor());
        // show it in screen
        ClientManager.getConsole().println("Sent: " + msg);

        // wait for completion of create DDInt
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
    static public DDInt openDDInt(String DDUI) throws Exception {
        DD dd = ClientManager.getDD(DDUI);
        if (dd != null) {
            if (dd instanceof DDInt) {
                //open an existing local DD
                return (DDInt) dd;
            } else {
                throw new Exception("DDUI of an unexpected type. Found " +
                        dd.getClass().getSimpleName() + " with the same DDUI. Should be a DDInt");
            }
        } else {
            // open a remote DDUI
            return new DDInt(DDUI);
        }
    }

    /**
     * To open a connection to a DD already existent in master
     */
    private DDInt(String DDUI) {
        super(DDUI);

        // save this DD in the Manager
        ClientManager.putDD(this);

        // send msg to open DD in master
        String requestId = UUID.randomUUID().toString();
        MsgOpenDDInt msg = new MsgOpenDDInt(DDUI, requestId);
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
    private DDInt(DDInt parentDD) {
        // create super DD with a new UUDI and with the parent and number of elems
        super(parentDD, parentDD.getNDataElems());

        // save this DDInt in the Manager
        ClientManager.putDD(this);
    }

    public int[] getData() {
        // send MsgGetData to master
        String requestId = UUID.randomUUID().toString();
        MsgGetDataDDInt msg = new MsgGetDataDDInt(DDUI, requestId);
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
    public DDInt forEach(Function<Integer, Integer> action) {
        // create new DDInt to store the results
        DDInt newDD = new DDInt(this);

        // send msg to master
        String requestId = UUID.randomUUID().toString();
        MsgApplyFunctionDDInt msg = new MsgApplyFunctionDDInt(DDUI, requestId, newDD.getDDUI(), action);
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

    // Filter objects that match a Predicate object
    public DDInt filter(Predicate<Integer> predicate) {
        // create new DDInt to store the results
        DDInt newDD = new DDInt(this);


        // send msg to master
        String requestId = UUID.randomUUID().toString();
        MsgApplyFilterDDInt msg = new MsgApplyFilterDDInt(DDUI, requestId, newDD.getDDUI(), predicate);
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
        return "DDInt " + super.toString();
    }

    public void fireMsgCreateDDIntReply(MsgCreateDDIntReply msg) {
        if (msg.isSuccess()) {
            // success: nothing to do
            lastOperationError = null;
        } else {
            // failure:
            lastOperationError = "Error creating DDInt: " + msg.getFailureReason();

            // remove previously created DD - this DD does not exist in Master
            ClientManager.removeDD(msg.getDDUI());
        }

        // wake up client thread that asked to create DDInt
        synchronized (this) {
            this.notify();
        }
    }

    public void fireMsgGetDataDDIntReply(MsgGetDataDDIntReply msg) {
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

    public void fireMsgApplyFunctionDDIntReply(MsgApplyFunctionDDIntReply msg) {
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

    public void fireMsgApplyFilterDDIntReply(MsgApplyFilterDDIntReply msg) {
        if (msg.isSuccess()) {
            // success: nothing to do
            lastOperationError = null;

            // save number of elems
            nDataElems = msg.getnDataElemsDD();

        } else {
            // failure:
            lastOperationError = "Error applying function: " + msg.getFailureReason();
        }

        // wake up client thread that asked to create DDInt
        synchronized (this) {
            this.notify();
        }
    }

}

