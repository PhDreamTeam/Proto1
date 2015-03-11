package unl.fct.di.proto1.common.workerService;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.japi.Creator;
import unl.fct.di.proto1.common.IConsole;
import unl.fct.di.proto1.common.lib.ActorNode;
import unl.fct.di.proto1.common.lib.ActorState;
import unl.fct.di.proto1.common.lib.ActorType;
import unl.fct.di.proto1.common.lib.core.worker.DDPartition;
import unl.fct.di.proto1.common.lib.core.worker.DDPartitionInt;
import unl.fct.di.proto1.common.lib.core.worker.DDPartitionObject;
import unl.fct.di.proto1.common.lib.core.worker.DDPartitionPhotoInternal;
import unl.fct.di.proto1.common.lib.protocol.DDInt.*;
import unl.fct.di.proto1.common.lib.protocol.DDObject.*;
import unl.fct.di.proto1.common.lib.protocol.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class WorkerService {

    final String WORKER_SYSTEM_NAME = "WorkerServiceSystem";
    final String PARTITIONSFILENAME = "partitions.dat";

    IWorkerGui wg;
    IConsole console = null;
    ActorNode currentMaster = null;
    ActorRef directoryServiceActorRef = null;

    // service Manager - keep the existing services TODO
    ServiceManager serviceManager = new ServiceManager();

    // photo manager
    PhotoManager photoManager;


    public WorkerService(IWorkerGui wg, String workerServiceActorName) throws Exception {
        this.wg = wg;
        console = wg;

        ActorSystem system = wg.createSystem(WORKER_SYSTEM_NAME);
        console.println(WORKER_SYSTEM_NAME + " created...");

        // add photo service to service manager
        // TODO review where we should add the service
        serviceManager.addService("photo");
        serviceManager.addInternalDDUI("photoDD1");

        // create PhotoManager
        String photoMangerPathName = "data/" + workerServiceActorName;
        console.println("Creating PhotoManager in pathName -> " + photoMangerPathName);
        photoManager = new PhotoManager(photoMangerPathName);
        photoManager.addActivePhotoGroup("DD1");


        // Connect to DirectoryService
        ActorNode dsActorNode = new ActorNode("akka.tcp", "DirectoryServiceSystem", "127.0.0.1", "58730", "ds", ActorType.Directory);
        console.println("Trying to connect to Directory actorNode -> " + dsActorNode.getPath());
        directoryServiceActorRef = dsActorNode.generateActorRef(system);
        if(directoryServiceActorRef != null)
            console.println("Directory actor found -> " + directoryServiceActorRef.path());
        else
            console.println("Directory actor not found -> " + dsActorNode.toShortString());


        // create local actor - Worker actor
        ActorRef myActor = system.actorOf(WorkerActor.newActor(this), workerServiceActorName);
        console.println("Actor created -> " + myActor.path());

        // load partitions from disk
        loadPartitionsFromDisk();
    }


    private void writePartitionsToDisk() {
        try {
            FileOutputStream os = wg.openFileOutput(PARTITIONSFILENAME);
            ObjectOutputStream oos = new ObjectOutputStream(os);

            // TODO choose better place to put the file,
            // TODO put one partition in one file
            wg.serializeDDPartitions(oos);
            console.println("Partitions saved");

            oos.close();
        } catch (Exception e) {
            console.printException(e);
        }

    }

    public void showFiles() {
        String[] files = wg.getFileList();
        console.println("File listing...");
        for (String file : files) {
            console.println("  " + file);
        }
        console.println("End listing...");

    }

    public boolean loadPartitionsFromDisk() {
        try {
            FileInputStream is = wg.openFileInput(PARTITIONSFILENAME);
            ObjectInputStream ois = new ObjectInputStream(is);

            // read partitions to collection
            ArrayList<DDPartition> DDPartitionsAux = null;
            Object obj = ois.readObject();
            if (obj instanceof ArrayList)
                DDPartitionsAux = (ArrayList<DDPartition>) obj;
            else
                throw new Exception("Error reading partitions object from disk. Object type unexpected!!!");
            // insert them in ArrayList connected to adapter
            for (DDPartition part : DDPartitionsAux) {
                wg.addDDPartition(part);
            }

            console.println("Partitions loaded -> " + DDPartitionsAux.size());
            console.println("Partitions -> " + wg.getDDPartitions());

            ois.close();
        } catch (Exception e) {
            console.println(e.getMessage());
            return false;
        }
        return true;
    }

    public void clearPartitions() {
        wg.deleteFile(PARTITIONSFILENAME);
        wg.clearPartitions();
        console.println("All Partitions cleared: files and memory");
    }


    public List<DDPartition> getArrayDDPartitions() {
        return wg.getDDPartitions();
    }

    public ActorRef getDirectoryServiceActorRef() {
        return directoryServiceActorRef;
    }

    public ActorNode getCurrentMaster() {
        return currentMaster;
    }

    public void setCurrentMaster(ActorNode currentMaster) {
        this.currentMaster = currentMaster;
    }

    public void addService(ActorNode newNode) {
        wg.addService(newNode);
    }

    public void addDDPartition(DDPartition newPartition) {
        wg.addDDPartition(newPartition);
        //Log.d("WorkerService", "Partition added: " + newPartition.getDDUI());
        writePartitionsToDisk();
    }

    public DDPartition getPartition(String DDUI, int partID) {
        // if internal DD
        if(serviceManager.getInternalDDUIs().contains(DDUI)) {
            DDPartitionPhotoInternal dd = new DDPartitionPhotoInternal(DDUI, this);
            return dd;
        }

        // other DD
        List<DDPartition> ddparts = getArrayDDPartitions();

        for (int i = 0, size = ddparts.size(); i < size; i++) {
            DDPartition p = ddparts.get(i);
            if (p.getDDUI().equals(DDUI) && p.getPartId() == partID)
                return p;
        }
        return null;
    }

    public IWorkerGui getWorkerGui() {
        return wg;
    }

    public PhotoManager getPhotoManager() {
        return photoManager;
    }

    public static class WorkerActor extends UntypedActor {

        static final ActorType type = ActorType.Worker;
        WorkerService ws;

        // constructorWorkerService ws;
        public WorkerActor(WorkerService workerService) {
            this.ws = workerService;

            ActorRef actorRefDirectoryService = ws.getDirectoryServiceActorRef();
            if (actorRefDirectoryService != null) {
                // register in Directory
                Msg msgOut = new MsgRegister(UUID.randomUUID().toString(), type);
                ws.getDirectoryServiceActorRef().tell(msgOut, getSelf());
                ws.console.println("Sent: " + msgOut + " to:" + ws.getDirectoryServiceActorRef().path().name());
            }
        }

        public static Props newActor(final WorkerService ws) {
            return Props.create(new Creator<WorkerActor>() {
                public WorkerActor create() throws Exception {
                    return new WorkerActor(ws);
                }
            });
        }

        @Override
        public void onReceive(Object message) {
            ws.console.println("Received: " + message + " from: " + getSender().path());

            // TODO In each request, check if the request is already processed, if so just reply success

            // DDObject =====================================

            if (message instanceof MsgPartitionApplyFilterDDObject) {
                MsgPartitionApplyFilterDDObject msg = (MsgPartitionApplyFilterDDObject) message;
                DDPartitionObject parentDDPartition = (DDPartitionObject) ws.getPartition(msg.getDDUI(),
                        msg.getPartId());

                // apply function
                DDPartitionObject newDDPartition = parentDDPartition.filter(msg.getFilter(), msg.getNewDDUI());

                // keep new partition in array of partitions and show it in screen
                ws.addDDPartition(newDDPartition);
                ws.console.println("Apply filter result: " + newDDPartition);

                // send msg to master - create message
                MsgPartitionApplyFilterDDObjectReply msgOut = new MsgPartitionApplyFilterDDObjectReply(
                        msg.getDDUI(), msg.getRequestId(), msg.getPartId(), msg.getNewDDUI(),
                        newDDPartition.getData().length, true, null);
                // send reply
                getSender().tell(msgOut, getSelf());
                ws.console.println("Sent: " + msgOut + " to: " + getSender().path().name());
                ws.console.println("");
            } //

            else if (message instanceof MsgPartitionApplyFunctionDDObject) {
                MsgPartitionApplyFunctionDDObject msg = (MsgPartitionApplyFunctionDDObject) message;
                DDPartitionObject parentDDPartition = (DDPartitionObject) ws.getPartition(msg.getDDUI(),
                        msg.getPartId());

                // apply function
                DDPartitionObject newDDPartition = parentDDPartition.forEach(msg.getAction(), msg.getNewDDUI());

                // keep new partition in array of partitions and show it in screen
                ws.addDDPartition(newDDPartition);
                ws.console.println("Apply function result: " + newDDPartition);

                // send msg to master - create message
                MsgPartitionApplyFunctionDDObjectReply msgOut = new MsgPartitionApplyFunctionDDObjectReply(
                        msg.getDDUI(), msg.getRequestId(), msg.getPartId(), msg.getNewDDUI(), true, null);
                // send reply
                getSender().tell(msgOut, getSelf());
                ws.console.println("Sent: " + msgOut + " to: " + getSender().path().name());
                ws.console.println("");
            } //

            else if (message instanceof MsgPartitionGetDataDDObject) {
                MsgPartitionGetDataDDObject msg = (MsgPartitionGetDataDDObject) message;
                DDPartitionObject p = (DDPartitionObject) ws.getPartition(msg.getDDUI(), msg.getPartId());

                MsgPartitionGetDataDDObjectReply msgOut;
                if (p != null) {
                    msgOut = new MsgPartitionGetDataDDObjectReply(
                            p.getDDUI(), msg.getRequestId(), p.getPartId(), p.getData(), true, null);
                } else {
                    msgOut = msg.getFailureReplyMessage("partition not found");
                }
                // send reply and show it on screen
                getSender().tell(msgOut, getSelf());
                ws.console.println("Sent: " + msgOut + " to: " + getSender().path().name());
                ws.console.println("");
            } //

            else if (message instanceof MsgPartitionCreateDDObject) {
                MsgPartitionCreateDDObject msgPartition = (MsgPartitionCreateDDObject) message;

                // TODO if DDUI is from a internal service, throw error

                DDPartitionObject partition = new DDPartitionObject(msgPartition.getDDUI(),
                        msgPartition.getPartId(), msgPartition.getData());
                // show in screen
                ws.addDDPartition(partition);
                ws.console.println("MsgCreatePartitionDDObject: processed");

                // send reply
                Msg msgOut = new MsgPartitionCreateDDObjectReply(msgPartition.getDDUI(),
                        msgPartition.getRequestId(), msgPartition.getPartId(), true, null);
                getSender().tell(msgOut, getSelf());
                ws.console.println("Sent: " + msgOut + " to: " + getSender().path().name());
                ws.console.println("");
            } //


            // DDInt =====================================

            else if (message instanceof MsgPartitionApplyFilterDDInt) {
                MsgPartitionApplyFilterDDInt msg = (MsgPartitionApplyFilterDDInt) message;
                DDPartitionInt parentDDPartition = (DDPartitionInt) ws.getPartition(msg.getDDUI(), msg.getPartId());

                // apply function
                DDPartitionInt newDDPartition = parentDDPartition.filter(msg.getFilter(), msg.getNewDDUI());

                // keep new partition in array of partitions and show it in screen
                ws.addDDPartition(newDDPartition);
                ws.console.println("Apply filter result: " + newDDPartition);

                // send msg to master - create message
                MsgPartitionApplyFilterDDIntReply msgOut = new MsgPartitionApplyFilterDDIntReply(
                        msg.getDDUI(), msg.getRequestId(), msg.getPartId(), msg.getNewDDUI(),
                        newDDPartition.getData().length, true, null);
                // send reply
                getSender().tell(msgOut, getSelf());
                ws.console.println("Sent: " + msgOut + " to: " + getSender().path().name());
                ws.console.println("");
            } //

            else if (message instanceof MsgPartitionApplyFunctionDDInt) {
                MsgPartitionApplyFunctionDDInt msg = (MsgPartitionApplyFunctionDDInt) message;
                DDPartitionInt parentDDPartition = (DDPartitionInt) ws.getPartition(msg.getDDUI(), msg.getPartId());

                // apply function
                DDPartitionInt newDDPartition = parentDDPartition.forEach(msg.getAction(), msg.getNewDDUI());

                // keep new partition in array of partitions and show it in screen
                ws.addDDPartition(newDDPartition);
                ws.console.println("Apply function result: " + newDDPartition);

                // send msg to master - create message
                MsgPartitionApplyFunctionDDIntReply msgOut = new MsgPartitionApplyFunctionDDIntReply(
                        msg.getDDUI(), msg.getRequestId(), msg.getPartId(), msg.getNewDDUI(), true, null);
                // send reply
                getSender().tell(msgOut, getSelf());
                ws.console.println("Sent: " + msgOut + " to: " + getSender().path().name());
                ws.console.println("");
            } //

            else if (message instanceof MsgPartitionGetDataDDInt) {
                MsgPartitionGetDataDDInt msg = (MsgPartitionGetDataDDInt) message;
                DDPartitionInt p = (DDPartitionInt) ws.getPartition(msg.getDDUI(), msg.getPartId());

                MsgPartitionGetDataDDIntReply msgOut;
                if (p != null) {
                    msgOut = new MsgPartitionGetDataDDIntReply(
                            p.getDDUI(), msg.getRequestId(), p.getPartId(), p.getData(), true, null);
                } else {
                    msgOut = msg.getFailureReplyMessage("partition not found");
                }
                // send reply
                getSender().tell(msgOut, getSelf());
                ws.console.println("Sent: " + msgOut + " to: " + getSender().path().name());
                ws.console.println("");
            } //

            else if (message instanceof MsgPartitionCreateDDInt) {
                MsgPartitionCreateDDInt msgPartition = (MsgPartitionCreateDDInt) message;
                ActorNode masterActorNode = new ActorNode(getSender(), ActorState.ACTIVE, ActorType.Master);

                // create new partition
                DDPartitionInt partition = new DDPartitionInt(msgPartition.getDDUI(),
                        msgPartition.getPartId(), msgPartition.getData());
                // show in screen
                ws.addDDPartition(partition);
                ws.console.println("MsgCreatePartitionDDInt: processed");

                // send reply
                Msg msgOut = new MsgPartitionCreateDDIntReply(
                        msgPartition.getDDUI(), msgPartition.getRequestId(), msgPartition.getPartId(), true, null);
                getSender().tell(msgOut, getSelf());
                ws.console.println("Sent: " + msgOut + " to: " + getSender().path().name());
                ws.console.println("");
            } //


            // SETUP =====================================

            else if (message instanceof MsgRegisterWorkerReply){
                // receive register confirmation - nothing to do.
                // we cannot remove this, because it will run MsgRegisterReply by default
            } //

            else if (message instanceof MsgGetMasterReply) {
                // receive this from Directory service
                ActorNode masterActorNode = ((MsgGetMasterReply) message).getActorNode();
                ws.setCurrentMaster(masterActorNode);

                try {
                    // generate Master actorRef
                    ActorRef masterActorRef = masterActorNode.generateActorRef(getContext());
                    ws.console.println("Master actor found -> " + masterActorRef.path());

                    // put master on screen
                    ws.addService(masterActorNode);
                    ws.console.println("");

                    // register in master - just to be nice
                    Msg msgOut = new MsgRegisterWorker(UUID.randomUUID().toString(),
                            ws.serviceManager.getInternalDDUIs());
                    masterActorRef.tell(msgOut, getSelf());
                    ws.console.println("Sent: " + msgOut + " to: " + getSender().path().name());
                } catch (Exception e) {
                    ws.console.printException(e);
                }
            } //

            else if (message instanceof MsgRegisterReply) {
                // Directory service answers register from worker
                // get master node from Directory service
                Msg msgOut = new MsgGetMaster(UUID.randomUUID().toString());
                sender().tell(msgOut, getSelf());
                ws.console.println("Sent: " + msgOut + " to: " + getSender().path().name());
            } //

            // TODO watch for terminations

            else {
                ws.console.println("Unhandled message: " + message);
                //unhandled(message);
            }
        }
    }
}

