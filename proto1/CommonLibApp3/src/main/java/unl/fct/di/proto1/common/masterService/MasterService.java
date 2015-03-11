package unl.fct.di.proto1.common.masterService;

import akka.actor.*;
import akka.japi.Creator;
import unl.fct.di.proto1.common.IConsole;
import unl.fct.di.proto1.common.lib.ActorNode;
import unl.fct.di.proto1.common.lib.ActorState;
import unl.fct.di.proto1.common.lib.ActorType;
import unl.fct.di.proto1.common.lib.core.master.*;
import unl.fct.di.proto1.common.lib.protocol.*;
import unl.fct.di.proto1.common.lib.protocol.DDInt.*;
import unl.fct.di.proto1.common.lib.protocol.DDObject.*;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;


public class MasterService {
    final String MASTER_SYSTEM_NAME = "MasterServiceSystem";
    final String MASTER_STATE_FILENAME = "master_state.dat";

//    final int TIMEOUT_FOR_REQUEST_TO_WORKER = 5;

//    final int TIMEOUT_MASTER_FOR_REQUEST = 10;

    List<ActorNode> workers, clients;
    List<MasterRequest> requests;
   // List<DDMaster> data;

    IMasterGui masterGui;
    IConsole console;
    ActorSystem system;

    String masterActorName;
    ActorRef directoryServiceActorRef = null;


    public MasterService(IMasterGui masterGui, String masterActorName)
            throws Exception {

        this.masterGui = masterGui;
        this.console = masterGui;
        this.masterActorName = masterActorName;

        workers = masterGui.getWorkers();
        clients = masterGui.getClients();
        requests = masterGui.getRequests();
       // data = masterGui.getData();


        system = masterGui.createSystem(MASTER_SYSTEM_NAME);
        console.println(MASTER_SYSTEM_NAME + " created...");


        // Connect to DirectoryService
        ActorNode dsActorNode = new ActorNode("akka.tcp", "DirectoryServiceSystem", "127.0.0.1", "58730",
                "ds", ActorType.Directory);
        directoryServiceActorRef = dsActorNode.generateActorRef(system);
        if (directoryServiceActorRef != null) {
            console.println("Directory actor found -> " + directoryServiceActorRef.path());
        } else
            console.println("Directory actor not found -> " + dsActorNode.toShortString());

        // create local actor - Master actor
        ActorRef masterActorRef = system.actorOf(MasterActor.newActor(this, console), masterActorName);
        console.println("Actor created -> " + masterActorRef.path());


        // init Master Global Manager
        GlManager.init(console, masterActorRef, this);
    }


    public ActorRef getDirectoryServiceActorRef() {
        return directoryServiceActorRef;
    }

//    public void addOrUpdateActorNode(ActorNode newNode) {
//        if (newNode.getType().equals(ActorType.Worker)) {
//            workers.add(newNode);
//            updateView(adapterWorkers, listViewWorkers);
//        } else if (newNode.getType().equals(ActorType.Client)) {
//            clients.add(newNode);
//            updateView(adapterClients, listViewClients);
//        } else MainActivity.console.printException(
//                new Exception("updateActorNode: Invalid Actor Type:" + newNode.getType()));
//    }


    /**
     * Called with a valid ActorRef
     */
    private ActorNode addOrUpdateActorNode(ActorRef actorRef, ActorType actorType, ActorState actorState) {
        // get container of worker
        List<ActorNode> container = actorType.equals(ActorType.Worker) ? workers : clients;
        // get actorNode if possible
        ActorNode actorToUpdate = getActorNode(container, actorRef.path().name());

        // not existent - create a new one
        if (actorToUpdate == null) {
            // add new node - with state received
            actorToUpdate = new ActorNode(actorRef, actorState, actorType);
            masterGui.addActorNode(actorToUpdate);
        } else {
            // found - update state and actorRef and ip, port, ...
            actorToUpdate.setState(actorState);
            actorToUpdate.setActorRef(actorRef);
            masterGui.updateViewOfActorNode(actorToUpdate);
        }

        return actorToUpdate;
    }

    /**
     * Called with an ActorNode from persistent state - DISCONNECTED
     *
     * @return the ActorNode to be used
     */
    public ActorNode addOrUpdateActorNode(ActorNode an) {
        // get container of ActorNodes
        List<ActorNode> container = an.getType().equals(ActorType.Worker) ? workers : clients;
        // get actorNode if possible
        ActorNode actorToUpdate = getActorNode(container, an.getActorName());

        // if actor already exists, nothing to do, only return it
        if (actorToUpdate == null) {
            // not existent - create a new one, add it with DISCONNECTED state
            an.setState(ActorState.DISCONNECTED);
            container.add(an);
            actorToUpdate = an;

            if (an.getType().equals(ActorType.Worker))
                updateViewWorkers();
            else
                updateViewClients();
        }

        return actorToUpdate;
    }


    private ActorNode updateActorNode(ActorRef actorRef, ActorState actorState) {
        String actorName = actorRef.path().name();

        // check in workers
        ActorNode actorToUpdate = getActorNode(workers, actorName);
        // check in clients
        if (actorToUpdate == null)
            actorToUpdate = getActorNode(clients, actorName);

        // error
        if (actorToUpdate == null) {
            console.println("Error: Updating an inexistent actorNode to " + actorState);
            return null;
        }

        // if an outdated termination message arrives ignore it...
        if (actorState.equals(ActorState.DISCONNECTED) &&
                !actorToUpdate.getActorRef().equals(actorRef)) {
            console.println("Updating " + actorRef +
                    " with state " + actorState + ": ignored");
            return actorToUpdate;
        }

        // found - update state, actorRef (and ip,port, ...) and gui
        actorToUpdate.setState(actorState);
        actorToUpdate.setActorRef(actorRef);

        if (actorToUpdate.getType().equals(ActorType.Worker))
            updateViewWorkers();
        else
            updateViewClients();

        return actorToUpdate;
    }

    public ActorNode getClientActorNode(String actorName) {
        return getActorNode(clients, actorName);
    }


    public ActorNode getWorkerActorNode(String actorName) {
        return getActorNode(workers, actorName);
    }

    private ActorNode getActorNode(List<ActorNode> nodes, String actorName) {
        for (ActorNode an : nodes)
            if (an.getActorName().equals(actorName))
                return an;
        return null;
    }

    public ActorSystem getSystem() {
        return system;
    }


    public void addData(DDMaster dd) {
        masterGui.addData(dd);
    }

    public void updateViewWorkers() {
        masterGui.updateViewWorkers();
    }

    private void updateViewClients() {
        masterGui.updateViewClients();
    }

    private void updateViewData() {
        masterGui.updateViewData();
    }

    private void updateViewRequests() {
        masterGui.updateViewRequests();
    }


    /**
     * tries to get nWorkers active workers
     *
     * @return array filled with workers
     */
    private ActorNode[] getWorkers(int nWorkers) {
        if (nWorkers < 1) return new ActorNode[0];

        ActorNode[] w = new ActorNode[nWorkers];
        int nWorkersFound = 0;

        GlManager.getConsole().println("GetWorkers nWorkers -> " + workers.size());
        for (ActorNode actor : workers) {
            if (actor.isStateActive()) {
                w[nWorkersFound++] = actor;
                if (nWorkersFound == nWorkers)
                    return w;
            }
        }

        return Arrays.copyOfRange(w, 0, nWorkersFound);
    }


    /**
     * Gets the number of workers that the system assign to DD
     * @return The workers assigned to the DD
     */
    public ActorNode[] getWorkers(int dataElemSize, int nDataElems) {
        // TODO
        return getWorkers(2);
    }


    // TODO: save master state
    public void saveState() {
        console.println("Save state...");
        try {
            FileOutputStream os = masterGui.openFileOutput(MASTER_STATE_FILENAME);
            ObjectOutputStream oos = new ObjectOutputStream(os);

            // write state to disk
            // TODO choose better place to put the file,
            // TODO put one DD in one file
            oos.writeObject(GlManager.getDDManager().getDDs());

            oos.close();
        } catch (Exception e) {
            console.printException(e);
        }
        console.println("End of save state...");
    }

    public boolean loadState() {
        console.println("Load state...");
        try {
            FileInputStream is = masterGui.openFileInput(MASTER_STATE_FILENAME);
            ObjectInputStream ois = new ObjectInputStream(is);

            // read DDs from disk
            Object obj = ois.readObject();
            HashMap<String, DDMaster> DDsMapAux = null;
            if (obj instanceof HashMap)
                DDsMapAux = (HashMap<String, DDMaster>) obj;
            else
                throw new Exception("Error reading master state Map DDs object from disk. Object type unexpected!!!");

            // insert them in ArrayList connected to adapter
            for (String key : DDsMapAux.keySet()) {
                DDMaster dd = DDsMapAux.get(key);
                // update worker and actorRef references, add nodes to WorkerManager
                dd.updateReferences(this);
                GlManager.getDDManager().putDD(dd);
                addData(dd);
                console.println("DD loaded from disk: " + dd.getDDUI());
            }

            console.println("DDs loaded -> " + DDsMapAux.keySet().size());
            console.println("DDs -> " + GlManager.getDDManager().getDDs());
            ois.close();
        } catch (Exception e) {
            console.println(e.getMessage());
            return false;
        }

        console.println("End of load state...");
        return true;
    }

    public void clearPersistentState() {
        console.println("Clear persistent state...");
        masterGui.deleteFile(MASTER_STATE_FILENAME);
        console.println("End of clear persistent state...");
    }

    public void showFiles() {
        console.println("File listing...");
        String[] files = masterGui.getFileList();
        for (String file : files) {
            console.println("  " + file);
        }
        console.println("End listing...");
    }


    public static class MasterActor extends UntypedActor {

        MasterService ms;

        IConsole console;

        // constructor
        public MasterActor(MasterService ms, IConsole console) {
            this.ms = ms;
            this.console = console;

            ActorRef actorRefDirectoryService = ms.getDirectoryServiceActorRef();
            if (actorRefDirectoryService != null) {
                // register in Directory
                String requestId = UUID.randomUUID().toString();
                Msg msg = new MsgRegister(requestId, ActorType.Master);
                actorRefDirectoryService.tell(msg, getSelf());
                console.println("Sent:... " + msg);
            }
        }

        public static Props newActor(final MasterService ms, final IConsole console) {
            return Props.create(new Creator<MasterActor>() {

                public MasterActor create() throws Exception {
                    return new MasterActor(ms, console);
                }
            });
        }

        @Override
        public void onReceive(Object message) {
            console.println("Received: " + message + " from: " + getSender());

            // DDObject  ========================================================

            if (message instanceof MsgPartitionApplyFilterDDObjectReply) {
                MsgPartitionApplyFilterDDObjectReply msg = (MsgPartitionApplyFilterDDObjectReply) message;
                DDObjectMaster parentDD = (DDObjectMaster) GlManager.getDDManager().getDD(msg.getDDUI());
                if (GlManager.getCommunicationHelper().getAndRemoveOriginalPendingMessage(msg) != null) {
                    // update partition state
                    parentDD.fireMsgPartitionApplyFilterDDObjectReply(msg);
                    // update screen - DD state could have changed
                    ms.updateViewData();
                } else {
                    // message received after timeout or request finished
                    console.println("Received message not recognized by MessageTracker: " + msg);
                }
            } //

            else if (message instanceof MsgApplyFilterDDObject) {
                MsgApplyFilterDDObject msg = (MsgApplyFilterDDObject) message;
                DDObjectMaster parentDDObject = (DDObjectMaster) GlManager.getDDManager().getDD(msg.getDDUI());
                ActorNode clientActorNode = ms.getClientActorNode(getSender().path().name());
                // apply filter
                DDObjectMaster newDDObject = parentDDObject.filter(msg.getNewDDUI(), msg.getFilter(),
                        clientActorNode, msg.getRequestId(), msg);
                // add to screen
                ms.addData(newDDObject);
            } //

            else if (message instanceof MsgPartitionApplyFunctionDDObjectReply) {
                MsgPartitionApplyFunctionDDObjectReply msg = (MsgPartitionApplyFunctionDDObjectReply) message;
                DDObjectMaster parentDD = (DDObjectMaster) GlManager.getDDManager().getDD(msg.getDDUI());
                if (GlManager.getCommunicationHelper().getAndRemoveOriginalPendingMessage(msg) != null) {
                    // update partition state
                    parentDD.fireMsgPartitionApplyFunctionDDObjectReply(msg);
                    // update screen - DD state could have changed
                    ms.updateViewData();
                } else {
                    // message received after timeout or request finished
                    console.println("Received message not recognized by MessageTracker: " + msg);
                }
            } //

            else if (message instanceof MsgApplyFunctionDDObject) {
                MsgApplyFunctionDDObject msg = (MsgApplyFunctionDDObject) message;
                DDObjectMaster parentDDObject = (DDObjectMaster) GlManager.getDDManager().getDD(msg.getDDUI());
                ActorNode clientActorNode = ms.getClientActorNode(getSender().path().name());
                // for each
                DDObjectMaster newDDObject = parentDDObject.forEach(msg.getNewDDUI(), msg.getAction(),
                        clientActorNode, msg.getRequestId(), msg);
                // add to screen
                ms.addData(newDDObject);
            } //

            else if (message instanceof MsgPartitionGetDataDDObjectReply) {
                MsgPartitionGetDataDDObjectReply msg = (MsgPartitionGetDataDDObjectReply) message;
                DDObjectMaster dd = (DDObjectMaster) GlManager.getDDManager().getDD(msg.getDDUI());
                if (GlManager.getCommunicationHelper().getAndRemoveOriginalPendingMessage(msg) != null) {
                    // do it
                    dd.fireMsgPartitionGetDataDDObjectReply(msg);
                    // update screen - DD state could have changed
                    ms.updateViewData();
                } else {
                    // message received after timeout or request finished
                    console.println("Received message not recognized by MessageTracker: " + msg);
                }
            } //

            else if (message instanceof MsgGetDataDDObject) {
                MsgGetDataDDObject msg = (MsgGetDataDDObject) message;
                DDObjectMaster dd = (DDObjectMaster) GlManager.getDDManager().getDD(msg.getDDUI());
                ActorNode clientActorNode = ms.getClientActorNode(getSender().path().name());
                // get data
                dd.getData(msg.getRequestId(), clientActorNode, msg);
            } //

            else if (message instanceof MsgOpenDDObject) {
                MsgOpenDDObject msg = (MsgOpenDDObject) message;
                // check if DD exists in DDManager
                DDMaster dd = GlManager.getDDManager().getDD(msg.getDDUI());
                ActorNode clientActorNode = ms.getClientActorNode(getSender().path().name());
                // open DD
                DDMaster.OpenDDMaster(dd, clientActorNode, getSelf(), msg, DDObjectMaster.class);
            } //

            else if (message instanceof MsgPartitionCreateDDObjectReply) {
                MsgPartitionCreateDDObjectReply msg = (MsgPartitionCreateDDObjectReply) message;
                DDObjectMaster dd = (DDObjectMaster) GlManager.getDDManager().getDD(msg.getDDUI());
                if (GlManager.getCommunicationHelper().getAndRemoveOriginalPendingMessage(msg) != null) {
                    dd.fireMsgCreatePartitionDDObjectReply(msg);
                    // update screen - DD state could have changed
                    ms.updateViewData();
                } else {
                    // message received after timeout or request finished
                    console.println("Received message not recognized by MessageTracker: " + msg);
                }
            } //

            else if (message instanceof MsgCreateDDObject) {
                MsgCreateDDObject msg = (MsgCreateDDObject) message;
                ActorNode clientActorNode = ms.getClientActorNode(getSender().path().name());
                // create new DDObject
                DDObjectMaster d = new DDObjectMaster(msg.getDDUI(), msg.getRequestId(), msg.getData(),
                        clientActorNode, msg);
                // add to screen
                ms.addData(d);
            }

            // DDInt ========================================================

            else if (message instanceof MsgPartitionApplyFilterDDIntReply) {
                MsgPartitionApplyFilterDDIntReply msg = (MsgPartitionApplyFilterDDIntReply) message;
                DDIntMaster parentDD = (DDIntMaster) GlManager.getDDManager().getDD(msg.getDDUI());
                if (GlManager.getCommunicationHelper().getAndRemoveOriginalPendingMessage(msg) != null) {
                    // update partition state
                    parentDD.fireMsgPartitionApplyFilterDDIntReply(msg);
                    // update screen - DD state could have changed
                    ms.updateViewData();
                } else {
                    // message received after timeout or request finished
                    console.println("Received message not recognized by MessageTracker: " + msg);
                }
            } //

            else if (message instanceof MsgApplyFilterDDInt) {
                MsgApplyFilterDDInt msg = (MsgApplyFilterDDInt) message;
                DDIntMaster parentDDInt = (DDIntMaster) GlManager.getDDManager().getDD(msg.getDDUI());
                ActorNode clientActorNode = ms.getClientActorNode(getSender().path().name());
                // filter
                DDIntMaster newDDInt = parentDDInt.filter(msg.getNewDDUI(), msg.getFilter(),
                        clientActorNode, msg.getRequestId(), msg);
                // add to screen
                ms.addData(newDDInt);
            } //

            else if (message instanceof MsgPartitionApplyFunctionDDIntReply) {
                MsgPartitionApplyFunctionDDIntReply msg = (MsgPartitionApplyFunctionDDIntReply) message;
                DDIntMaster parentDD = (DDIntMaster) GlManager.getDDManager().getDD(msg.getDDUI());
                if (GlManager.getCommunicationHelper().getAndRemoveOriginalPendingMessage(msg) != null) {
                    // update partition state
                    parentDD.fireMsgPartitionApplyFunctionDDIntReply(msg);
                    // update screen - DD state could have changed
                    ms.updateViewData();
                } else {
                    // message received after timeout or request finished
                    console.println("Received message not recognized by MessageTracker: " + msg);
                }
            } //

            else if (message instanceof MsgApplyFunctionDDInt) {
                MsgApplyFunctionDDInt msg = (MsgApplyFunctionDDInt) message;
                DDIntMaster parentDDInt = (DDIntMaster) GlManager.getDDManager().getDD(msg.getDDUI());
                ActorNode clientActorNode = ms.getClientActorNode(getSender().path().name());
                // forEach
                DDIntMaster newDDInt = parentDDInt.forEach(msg.getNewDDUI(), msg.getAction(),
                        clientActorNode, msg.getRequestId(), msg);
                // add to screen
                ms.addData(newDDInt);
            } //

            else if (message instanceof MsgPartitionGetDataDDIntReply) {
                MsgPartitionGetDataDDIntReply msg = (MsgPartitionGetDataDDIntReply) message;
                DDIntMaster dd = (DDIntMaster) GlManager.getDDManager().getDD(msg.getDDUI());
                if (GlManager.getCommunicationHelper().getAndRemoveOriginalPendingMessage(msg) != null) {
                    // do it
                    dd.fireMsgPartitionGetDataDDIntReply(msg);
                    // update screen - DD state could have changed
                    ms.updateViewData();
                } else {
                    // message received after timeout or request finished
                    console.println("Received message not recognized by MessageTracker: " + msg);
                }
            } //

            else if (message instanceof MsgGetDataDDInt) {
                MsgGetDataDDInt msg = (MsgGetDataDDInt) message;
                DDIntMaster dd = (DDIntMaster) GlManager.getDDManager().getDD(msg.getDDUI());
                ActorNode clientActorNode = ms.getClientActorNode(getSender().path().name());
                // get data
                dd.getData(msg.getRequestId(), clientActorNode, msg);
            } //

            else if (message instanceof MsgPartitionCreateDDIntReply) {
                MsgPartitionCreateDDIntReply msg = (MsgPartitionCreateDDIntReply) message;
                DDIntMaster dd = (DDIntMaster) GlManager.getDDManager().getDD(msg.getDDUI());
                if (GlManager.getCommunicationHelper().getAndRemoveOriginalPendingMessage(msg) != null) {
                    dd.fireMsgCreatePartitionDDIntReply(msg);
                    // update screen - DD state could have changed
                    ms.updateViewData();
                } else {
                    // message received after timeout or request finished
                    console.println("Received message not recognized by MessageTracker: " + msg);
                }
            } //

            else if (message instanceof MsgCreateDDInt) {
                MsgCreateDDInt msg = (MsgCreateDDInt) message;
                ActorNode clientActorNode = ms.getClientActorNode(getSender().path().name());
                DDIntMaster d = new DDIntMaster(msg.getDDUI(), msg.getRequestId(), msg.getData(), clientActorNode, msg);
                // add to screen
                ms.addData(d);
            } //

            // extra messages ======================================

            else if (message instanceof MsgRegisterReply) {
                // directory answers register ok - nothing to do
            } //

            else if (message instanceof MsgRegister) {
                MsgRegister msg = (MsgRegister) message;
                if (msg.getType().equals(ActorType.Worker) ||
                        msg.getType().equals(ActorType.Client)) {

                    // save or update actor and put it on screen
                    ActorNode an = ms.addOrUpdateActorNode(getSender(), msg.getType(), ActorState.ACTIVE);

                    // watch actor system messages
                    this.getContext().watch(sender());


                    Msg msgOut;

                    if (msg.getType().equals(ActorType.Worker)){
                        // register worker internal services
                        ms.registerWorkerInternalServices(an, ((MsgRegisterWorker)msg).getInternalDDUIs());
                        // reply...
                        msgOut = new MsgRegisterWorkerReply(msg.getRequestId(), true, null);
                    }else
                        msgOut = new MsgRegisterReply(msg.getRequestId(), true, null);

                    getSender().tell(msgOut, getSelf());
                    console.println("Sent: " + msgOut + " to " + getSender().path().name());

                    // do pending processing for this actor
                    GlManager.getCommunicationHelper().doPendingActions(an);
                } else
                    console.println("Message with unexpected actor type: " + msg);
            } //

            else if (message instanceof Terminated) {
                ActorRef aref = getSender();
                console.println("Node terminated -> " + aref.path());
                console.println("Node terminated name -> " + aref.path().name());

                // save or update actor and put it on screen
                ms.updateActorNode(aref, ActorState.DISCONNECTED);
            } //

            else {
                //unhandled(message);
                console.println("Unhandled message: " + message);
            }
        }
    }

    /**
     * Register  worker internal internalDDUIs
     * @param an Actor node of the registering worker
     * @param internalDDUIs
     */
    private void registerWorkerInternalServices(ActorNode an, List<String> internalDDUIs) {
        for (int i = 0; i < internalDDUIs.size(); i++) {
            String ddui = internalDDUIs.get(i);
            // check if DD exists - create if not
            DDMaster dd = GlManager.getDDManager().getDD(ddui);
            if(dd == null){
                // add new internal service DD
                dd = new DDObjectMaster(ddui);
                // add to gui
                addData(dd);
            } else {
                if(!(dd instanceof DDObjectMaster))
                    throw new RuntimeException("Internal worker service DD (" + ddui +
                            ") should be a DDOBjectMaster, found -> " + dd.getClass().getSimpleName());
            }
            DDObjectMaster ddo = (DDObjectMaster)dd;
            // add this worker as a worker with a partition in that dd
            ddo.addWorkerInternalPartition(an);

        }
    }
}