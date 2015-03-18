package unl.fct.di.proto1.common.lib.core.master;


import akka.actor.ActorRef;
import unl.fct.di.proto1.common.IConsole;
import unl.fct.di.proto1.common.lib.core.CommunicationHelper;
import unl.fct.di.proto1.common.masterService.MasterService;

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
