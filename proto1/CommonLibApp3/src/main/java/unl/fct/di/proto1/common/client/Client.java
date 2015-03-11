package unl.fct.di.proto1.common.client;

import akka.actor.*;
import akka.japi.Creator;
import pt.unl.fct.di.proto1.services.photos.Photo;
import unl.fct.di.proto1.common.IConsole;
import unl.fct.di.proto1.common.lib.ActorNode;
import unl.fct.di.proto1.common.lib.ActorState;
import unl.fct.di.proto1.common.lib.ActorType;
import unl.fct.di.proto1.common.lib.core.client.ClientManager;
import unl.fct.di.proto1.common.lib.core.client.DDInt;
import unl.fct.di.proto1.common.lib.core.client.DDObject;
import unl.fct.di.proto1.common.lib.protocol.DDInt.MsgApplyFilterDDIntReply;
import unl.fct.di.proto1.common.lib.protocol.DDInt.MsgApplyFunctionDDIntReply;
import unl.fct.di.proto1.common.lib.protocol.DDInt.MsgCreateDDIntReply;
import unl.fct.di.proto1.common.lib.protocol.DDInt.MsgGetDataDDIntReply;
import unl.fct.di.proto1.common.lib.protocol.DDObject.*;
import unl.fct.di.proto1.common.lib.protocol.MsgGetMaster;
import unl.fct.di.proto1.common.lib.protocol.MsgGetMasterReply;
import unl.fct.di.proto1.common.lib.protocol.MsgRegister;
import unl.fct.di.proto1.common.lib.protocol.MsgRegisterReply;
import unl.fct.di.proto1.common.remoteActions.*;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.UUID;


public class Client {

    final String CLIENT_SYSTEM_NAME = "ClientSystem";

    IConsole console;

    IClientGui clientGui;

    ActorSystem system;

    ActorNode currentMaster = null;

    ActorRef directoryServiceActorRef = null;


    public Client(IClientGui clientGui, String clientActorName) throws Exception {
        // keep args
        this.console = clientGui;
        this.clientGui = clientGui;

        ClientManager.setConsole(console);

        system = clientGui.createSystem(CLIENT_SYSTEM_NAME);
        console.println(CLIENT_SYSTEM_NAME + " created...");


        // Connect to DirectoryService
        ActorNode dsActorNode = new ActorNode("akka.tcp", "DirectoryServiceSystem", "127.0.0.1", "58730", "ds", ActorType.Directory);
        //console.println("Trying to connect to Directory actorNode -> " + dsActorNode.getPath());
        directoryServiceActorRef = dsActorNode.generateActorRef(system);
        if (directoryServiceActorRef != null) {
            console.println("Directory actor found -> " + dsActorNode.toShortString());
            clientGui.addService(dsActorNode);
        } else
            console.println("Directory actor not found -> " + dsActorNode.toShortString());

        // create local actor - client actor
        ActorRef myActor = system.actorOf(ClientActor.newActor(this), clientActorName);
        console.println("Actor created -> " + myActor.path());
        ClientManager.setClientActor(myActor);
        console.println();
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


    // ------------------------------------------------------------
    public void workWithInts() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                // CREAT DDInt
                console.println("Creating DDint...");
                DDInt d1 = new DDInt(new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9});
                console.println("DDInt created:" + d1);
                console.println();

                // GET DDInt data
                console.println("Getting data from DDint...");
                int[] result = d1.getData();
                console.println("DDInt received:" + Arrays.toString(result));
                console.println();

                // Apply function to DDInt
                console.println("Applying Function to DDint -> DDInt2");
                DDInt d2 = d1.forEach(new DDIntFunctionAdd_1());
                console.println("End of Function to DDint -> DDInt2.");
                console.println();

                // Get data from DDInt2
                console.println("Getting data from DDint2...");
                int[] result2 = d2.getData();
                console.println("DDInt2 received:" + Arrays.toString(result2));
                console.println();

                // APPLY FILTER TEST 1

                // Apply filter to DDInt2
                console.println("Applying filter to DDint2 -> DDInt3");
                DDInt d3 = d2.filter(new DDIntFilterEvenNumbers_1());
                console.println("End of Function to DDint2 -> DDInt3.");
                console.println();

                // Get data from DDInt3
                console.println("Getting data from DDint3...");
                int[] result3 = d3.getData();
                console.println("DDInt3 received:" + Arrays.toString(result3));
                console.println();

                // APPLY FILTER TEST 2

                // Apply filter to DDInt3
                console.println("Applying filter to DDint3 -> DDInt4");
                DDInt d4 = d3.filter(new DDIntFilterGreaterThen_1(14));
                console.println("End of Function to DDint3 -> DDInt4.");
                console.println();

                // Get data from DDInt4
                console.println("Getting data from DDint4...");
                int[] result4 = d4.getData();
                console.println("DDInt4 received:" + Arrays.toString(result4));
                console.println();
            }
        });
        t.start();
    }


    // ------------------------------------------------------------
    public void workWithObjects() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                // CREATE DD1
                console.println("Creating DD1 DDObject...");
                DDObject d1 = new DDObject(new Object[]{"um", "dois", "três", "quatro", "cinco", "seis"});
                console.println("DD1 created:" + d1);
                console.println();

                // GET DD1 data
                console.println("Getting data from DD1...");
                Object[] result = d1.getData();
                console.println("DD1 received:" + Arrays.toString(result));
                console.println();

                // Apply function to DD2
                console.println("Applying Function to DD1 -> DD2");
                DDObject d2 = d1.forEach(new DDObjectFunctionAddLenght_1());
                console.println("End of Function to DD1 -> DD2.");
                console.println();

                // Get data from DD2
                console.println("Getting data from DD2...");
                Object[] result2 = d2.getData();
                console.println("DD2 received:" + Arrays.toString(result2));
                console.println();

                // APPLY FILTER TEST 1

                // Apply filter to DD2
                console.println("Applying filter to DD2 -> DD3");
                DDObject d3 = d2.filter(new DDObjectFilterContainsString_1("S"));
                console.println("End of Function to DD2 -> DD3.");
                console.println();

                // Get data from DD3
                console.println("Getting data from DD3...");
                Object[] result3 = d3.getData();
                console.println("DD3 received:" + Arrays.toString(result3));
                console.println();

                // APPLY FILTER TEST 2

                // Apply filter to DD3
                console.println("Applying filter to DD3 -> DD4");
                DDObject d4 = d3.filter(new DDObjectFilterContainsString_1("I"));
                console.println("End of Function to DD3 -> DD4.");
                console.println();

                // Get data from DD4
                console.println("Getting data from DD4...");
                Object[] result4 = d4.getData();
                console.println("DD4 received:" + Arrays.toString(result4));
                console.println();

            }
        });
        t.start();
    }

    // ------------------------------------------------------------
    public void workWithExistingObjects(final String DDUI) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    // OPEN DD1
                    console.println("Opening DD1 DDObject: " + DDUI);
                    DDObject d1 = DDObject.openDDObject(DDUI);
                    console.println("DD1 opened:" + d1);
                    console.println();

                    // GET DDX1 data
                    console.println("Getting data from DD1...");
                    Object[] result = d1.getData();
                    console.println("DD1 received:" + Arrays.toString(result));
                    console.println();

                    // OPEN DDX1
                    console.println("Opening DDX1 DDObject with Error...");
                    DDObject dx1 = DDObject.openDDObject(DDUI + "_ERROR");
                    console.println("DDX1 opened:" + dx1);
                    console.println();

                } catch (Exception e) {
                    console.printException(e);
                }
            }
        });
        t.start();
    }

    // ------------------------------------------------------------
    // work with existing Internal Photos
    public void workWithPhotos() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    String DDUI = "photoDD1";

                    // OPEN IPHOTO DD1
                    console.println("Opening IPHOTOS DDUI: " + DDUI);
                    DDObject d1 = DDObject.openDDObject(DDUI);
                    console.println("Opened: " + d1);
                    console.println();

                    // GET IPHOTO DD1 data
                    console.println("Getting data from IPHOTOS DDUI: " + DDUI);
                    Photo[] photos = (Photo[])d1.getData();
                    console.println("Received photos:" + Arrays.toString(photos));
                    console.println();

                    // display thumbnails
                    displayThumbnails(photos);


                } catch (Exception e) {
                    console.printException(e);
                }
            }
        });
        t.start();
    }

    private void displayThumbnails(Photo[] photos) {
        JFrame jf = new JFrame();
        jf.setLayout(new FlowLayout());

        for (int i = 0; i < photos.length; i++) {
            ImageIcon t = new ImageIcon(photos[i].getThumbnail());
            jf.add(new JLabel(t));
        }
        jf.pack();
        jf.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        jf.setVisible(true);
    }

    // ------------------------------------------------------------
    // work with existing Internal Photos
    // DEBUG verificar....
    public void workWithImageTest() {
//        System.out.println("Vou começar o teste");
//        console.println("Vou começar o teste");
//        PhotoWorker pw = new PhotoWorker("123456765r4e", "C:/PhD/code/Proto1/photos/kk1.jpg");
//        PhotoWorker pw2 = new PhotoWorker("123456765r5e", "C:/PhD/code/Proto1/photos/kk2.jpg");
//        System.out.println("Foto carregada");
//        console.println("Foto carregada");
//        JFrame jf = new JFrame();
//        jf.setLayout(new FlowLayout());
//
//
//        try {
//            // thumbnail
//            ImageIcon t = new ImageIcon();
//            t.setImage(pw.getPhoto().getScaledInstance(100, 100, Image.SCALE_FAST));
//            jf.add(new JLabel(t));
//            console.println("Thumbnail adicionado");
//
//            // thumbnail 2
//            ImageIcon t2 = new ImageIcon(pw.getThumbnail());
//            jf.add(new JLabel(t2));
//            console.println("Thumbnail adicionado");
//
//            // thumbnail 3
//            ImageIcon t3 = new ImageIcon();
//            t3.setImage(pw2.getPhoto().getScaledInstance(100, 100, Image.SCALE_FAST));
//            jf.add(new JLabel(t3));
//            console.println("Thumbnail adicionado");
//
//            // thumbnail 4
//            ImageIcon t4 = new ImageIcon(pw2.getThumbnail());
//            jf.add(new JLabel(t4));
//            console.println("Thumbnail adicionado");
//
//            // original image
//            ImageIcon i = new ImageIcon();
//            i.setImage(pw.getPhoto());
//            jf.add(new JLabel(i));
//            console.println("Imagem adicionada");
//
//
//        } catch (IOException e) {
//            e.printStackTrace();
//            System.out.println("EXCEÇÃO: " + e.getMessage());
//        }
//        jf.pack();
//        jf.setVisible(true);
//        console.println("ImageTest terminado...");
    }

    // ------------------------------------------------------------
    public ActorNode getActorNode(ActorRef actorRef) {
        for (ActorNode an : clientGui.getServices()) {
            if (an.getActorRef() != null && an.getActorRef().equals(actorRef))
                return an;
        }
        return null;
    }


    // ------------------------------------------------------------

    /**
     * Class Client Actor
     */
    public static class ClientActor extends UntypedActor {

        Client client;

        static final ActorType type = ActorType.Client;

        IConsole console;

        // constructor
        public ClientActor(Client client) {
            this.client = client;
            console = client.console;

            ActorRef actorRefDirectoryService = this.client.getDirectoryServiceActorRef();

            if (actorRefDirectoryService != null) {
                // watch directory service system messages
                this.getContext().watch(actorRefDirectoryService);

                // get my master
                String requestId = UUID.randomUUID().toString();
                actorRefDirectoryService.tell(new MsgGetMaster(requestId), getSelf());
            }
        }

        public static Props newActor(final Client client) {
            return Props.create(new Creator<ClientActor>() {
                public ClientActor create() throws Exception {
                    return new ClientActor(client);
                }
            });
        }

        @Override
        public void onReceive(Object message) {
            console.println("Received: " + message);

            // DDObject ==================================

            if (message instanceof MsgOpenDDObjectReply) {
                MsgOpenDDObjectReply msg = (MsgOpenDDObjectReply) message;
                DDObject dd = (DDObject) ClientManager.getDD(msg.getDDUI());
                dd.fireMsgOpenDDObjectReply(msg);
            } //

            else if (message instanceof MsgApplyFilterDDObjectReply) {
                MsgApplyFilterDDObjectReply msg = (MsgApplyFilterDDObjectReply) message;
                DDObject dd = (DDObject) ClientManager.getDD(msg.getNewDDUI());
                dd.fireMsgApplyFilterDDObjectReply(msg);
            } //

            else if (message instanceof MsgApplyFunctionDDObjectReply) {
                MsgApplyFunctionDDObjectReply msg = (MsgApplyFunctionDDObjectReply) message;
                DDObject dd = (DDObject) ClientManager.getDD(msg.getNewDDUI());
                dd.fireMsgApplyFunctionDDObjectReply(msg);
            } //

            else if (message instanceof MsgGetDataDDObjectReply) {
                MsgGetDataDDObjectReply msg = (MsgGetDataDDObjectReply) message;
                DDObject dd = (DDObject) ClientManager.getDD(msg.getDDUI());
                dd.fireMsgGetDataDDObjectReply(msg);
            } //

            else if (message instanceof MsgCreateDDObjectReply) {
                MsgCreateDDObjectReply msg = (MsgCreateDDObjectReply) message;
                DDObject dd = (DDObject) ClientManager.getDD(msg.getDDUI());
                dd.fireMsgCreateDDObjectReply(msg);
            } //


            // DDInt =================================

            else if (message instanceof MsgApplyFilterDDIntReply) {
                MsgApplyFilterDDIntReply msg = (MsgApplyFilterDDIntReply) message;
                DDInt dd = (DDInt) ClientManager.getDD(msg.getNewDDUI());
                dd.fireMsgApplyFilterDDIntReply(msg);
            } //

            else if (message instanceof MsgApplyFunctionDDIntReply) {
                MsgApplyFunctionDDIntReply msg = (MsgApplyFunctionDDIntReply) message;
                DDInt dd = (DDInt) ClientManager.getDD(msg.getNewDDUI());
                dd.fireMsgApplyFunctionDDIntReply(msg);
            } //

            else if (message instanceof MsgGetDataDDIntReply) {
                MsgGetDataDDIntReply msg = (MsgGetDataDDIntReply) message;
                DDInt dd = (DDInt) ClientManager.getDD(msg.getDDUI());
                dd.fireMsgGetDataDDIntReply(msg);
            } //

            else if (message instanceof MsgCreateDDIntReply) {
                MsgCreateDDIntReply msg = (MsgCreateDDIntReply) message;
                DDInt dd = (DDInt) ClientManager.getDD(msg.getDDUI());
                dd.fireMsgCreateDDIntReply(msg);
            } //

            // Master and Directory messages ==============

            else if (message instanceof MsgRegisterReply) {
                console.println();

                // start doing the work with a separate thread

                // now: doing nothing: waiting for menu selection
                //client.workWithInts();
                //client.workWithObjects();
            } //

            else if (message instanceof MsgGetMasterReply) {
                ActorNode masterActorNode = ((MsgGetMasterReply) message).getActorNode();
                ActorRef masterActorRef = null;
                // generate Master actorRef
                try {
                    masterActorRef = masterActorNode.generateActorRef(getContext());
                    console.println("Master actor found -> " + masterActorRef.path());
                    // TODO replace with setMAsterActorNode to keep ActorNode
                    ClientManager.setMasterActor(masterActorRef);

                    // put master on screen
                    client.clientGui.addService(masterActorNode);
                    // watch platform events for master actor
                    this.getContext().watch(masterActorRef);
                    // register in master
                    String requestId = UUID.randomUUID().toString();
                    masterActorRef.tell(new MsgRegister(requestId, ActorType.Client), getSelf());
                    console.println("Sent MsgRegister to master -> " + masterActorRef.path());
                } catch (Exception e) {
                    console.printException(e);
                }
            } //

            else if (message instanceof Terminated) {
                ActorRef aref = getSender();
                console.println("Node terminated -> " + aref.path());

                ActorNode snd = client.getActorNode(aref);
                if (snd != null) {
                    snd.setState(ActorState.DISCONNECTED);
                    client.clientGui.updateViewServices();
                } else console.println("Actor not found by getActorNode ...");

                console.println("Msg Terminated: finished!");
            } //

            else {
                console.println("Unhandled message: " + message);
                //unhandled(message);
            }
        }
    }

    public class Request {
        int id;

        public Request(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String toString() {
            return "Request{" + "id=" + id + '}';
        }
    }

}