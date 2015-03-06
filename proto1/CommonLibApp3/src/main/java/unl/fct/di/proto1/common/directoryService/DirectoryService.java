package unl.fct.di.proto1.common.directoryService;

import akka.actor.*;
import akka.japi.Creator;
import unl.fct.di.proto1.common.IConsole;
import unl.fct.di.proto1.common.lib.ActorNode;
import unl.fct.di.proto1.common.lib.ActorState;
import unl.fct.di.proto1.common.lib.ActorType;
import unl.fct.di.proto1.common.lib.protocol.*;

import java.io.IOException;
import java.util.List;


public class DirectoryService {
    final String DIRECTORY_SYSTEM_NAME = "DirectoryServiceSystem";

    IDirectoryGui directoryGui;
    IConsole console;
    ActorSystem system;


    public DirectoryService(IDirectoryGui directoryGui, String directoryActorName) throws IOException {
        this.directoryGui = directoryGui;
        console = directoryGui;
        try {
            system = directoryGui.createSystem(DIRECTORY_SYSTEM_NAME);
            console.println(DIRECTORY_SYSTEM_NAME + " created...");

            final ActorRef myActor = system.actorOf(DirServActor.props(this), directoryActorName);
            console.println("Actor created -> " + myActor.path());

        } catch (Exception e) {
            console.printException(e);
        }
    }

    public IDirectoryGui getIDirectoryGui() {
        return directoryGui;
    }


    public ActorNode getMaster(ActorRef actorRef) {
        // TODO can be a WORKER or a CLIENT
        // TODO implement a better get master from worker
        for (ActorNode mst : directoryGui.getMasters()) {
            if (mst.getState().equals(ActorState.ACTIVE))
                return mst;
        }
        return null;
    }


    public ActorNode getActorNode(ActorRef actorRef) {
        for (ActorNode an : directoryGui.getMasters()) {
            if ((an.getActorRef() != null) && (an.getActorRef().equals(actorRef)))
                return an;
        }
        for (ActorNode an : directoryGui.getWorkers()) {
            if (an.getActorRef() != null && an.getActorRef().equals(actorRef))
                return an;
        }
        return null;
    }


    public static class DirServActor extends UntypedActor {
        DirectoryService ds;
        IDirectoryGui directoryGui;
        IConsole console;

        public DirServActor(DirectoryService ds) {
            this.ds = ds;
            this.directoryGui = ds.getIDirectoryGui();
            console = this.directoryGui;
        }

        public static Props props(final DirectoryService ds) {
            return Props.create(new Creator<DirServActor>() {
                //                private static final long serialVersionUID = 1L;
                public DirServActor create() throws Exception {
                    return new DirServActor(ds);
                }
            });
        }

        @Override
        public void onReceive(Object message) {
            console.println("Received: " + message + " from: " + getSender().path());

            if (message instanceof MsgGetMaster) {
                MsgGetMaster msg = (MsgGetMaster) message;
                ActorNode mst = ds.getMaster(getSender());

                // send GetMasterReply to sender
                MsgGetMasterReply msgOut = new MsgGetMasterReply(msg.getRequestId(), mst);
                getSender().tell(msgOut, getSelf());
                console.println("Sent " + msgOut);
            } //

            else if (message instanceof MsgRegister) {
                MsgRegister msg = (MsgRegister) message;

                ActorNode an = new ActorNode(getSender(), ActorState.ACTIVE, msg.getType());
                console.println("New " + an.getType() + " -> " + an);
                directoryGui.addActorNode(an);

                // watch actor system messages
                this.getContext().watch(sender());



                // send RegisterReply to sender
                Msg msgOut = new MsgRegisterReply(msg.getRequestId(), true, null);
                getSender().tell(msgOut, getSelf());
                console.println("Sent " + msgOut);
            } //

            else if (message instanceof Terminated) {
                ActorRef aref = getSender();
                console.println("Node terminated -> " + aref.path());
                ActorNode snd = ds.getActorNode(aref);

                if (snd != null) {
                    snd.setState(ActorState.DISCONNECTED);
                    if (snd.getType().equals(ActorType.Master))
                        directoryGui.updateViewMasters();
                    else if (snd.getType().equals(ActorType.Worker))
                        directoryGui.updateViewWorkers();
                    else console.println("Actor type not accepted ...");
                } else console.println("Actor not found by getActorNode ...");
                console.println("Msg Terminated: finished!");
            } //

            else {
                unhandled(message);
            }

        }

        @Override
        public String toString() {
            return "s- ";
        }
    }

}