package unl.fct.di.proto1.common.lib.core.master;


import akka.actor.ActorRef;
import unl.fct.di.proto1.common.IConsole;
import unl.fct.di.proto1.common.lib.core.CommunicationHelper;
import unl.fct.di.proto1.common.masterService.MasterService;

// 0 is minimum priority


// TODO NEXT ACTION:

// TODO 4 fotos
// TODO PhotoGroups, registar os services activos e os Grupos activos, no ciente também
// TODO implementar Workers com suporte a photos




// registry ==============================================

// TODO 3 ms master die and live again:  keep original MasterRequest in new DDs
// TODO 3 ms reconstruct a lost DD by replaying saved operations
// TODO 3 ms masters save and load state, pending requests no need to be saved
// TODO 3 workers reconnect to masters after master termination

// TODO 2 ms Substitute client tells por CommunicationHelper.tell

// TODO 1 ms DDIntMaster e DDObjectMaster no constructor temos a Msg e outros parâmetros da msg, reduzir isto
// TODO 1 ms same as previous: also in methods: getData, forEach, ...


// DONE ===========================================================
// DONE 5 ms Substitute worker tells por CommunicationHelper.tell

// DONE masters works well with workers crashes

/**
 * Global manager
 */
public class GlManager {
    static DDManager ddManager = null;
    static ActorRef masterActor = null;
    static IConsole console = null;
    static MasterService masterService;
    static CommunicationHelper communicationHelper = new CommunicationHelper();


    public static void init(IConsole console,  ActorRef masterActor,
                            MasterService masterService) {
        GlManager.ddManager = new DDManager();
        setConsole(console);
        setMasterActor(masterActor);
        setMasterService(masterService);
    }

    public static DDManager getDDManager() {
        return ddManager;
    }

    public static ActorRef getMasterActor() {
        return masterActor;
    }

    public static void setMasterActor(ActorRef masterActor) {
        GlManager.masterActor = masterActor;
    }

    public static IConsole getConsole() {
        return console;
    }

    public static void setConsole(IConsole console) {
        GlManager.console = console;
    }

    public static void setMasterService(MasterService iMasterService) {
        GlManager.masterService = iMasterService;
    }

    public static MasterService getMasterService() {
        return masterService;
    }

    public static CommunicationHelper getCommunicationHelper() {
        return communicationHelper;
    }
}
