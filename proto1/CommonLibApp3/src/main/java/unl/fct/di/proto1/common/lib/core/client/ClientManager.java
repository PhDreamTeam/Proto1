package unl.fct.di.proto1.common.lib.core.client;


import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import pt.unl.fct.di.proto1.services.photos.Photo;
import unl.fct.di.proto1.common.IConsole;

import java.util.HashMap;

/**
 * Global manager
 */
public class ClientManager {
    static ActorRef clientActor = null;
    static IConsole console = null;
    static ActorRef masterActor = null;
    static ActorSystem clientSystem = null;

    static HashMap<String, DD> DDMap =  new HashMap<>();

    static HashMap<String, Photo> photoMap = new HashMap<>();

    public static void putPhotoInPhotoMap(Photo p){
        photoMap.put(p.getPhotoUuid(), p);
    }

    public static Photo getPhotoInPhotoMap(String photoUuid){
        return photoMap.get(photoUuid);
    }

    public static ActorRef getClientActor() {
        return clientActor;
    }

    public static void setClientActor(ActorRef clientActor) {
        ClientManager.clientActor = clientActor;
    }

    public static IConsole getConsole() {
        return console;
    }

    public static void setConsole(IConsole console) {
        ClientManager.console = console;
    }

    public static void setMasterActor(ActorRef masterActor) {
        ClientManager.masterActor = masterActor;
    }

    public static ActorRef getMasterActor() {
        return masterActor;
    }

    public static DD getDD(String DDUI) {
        return DDMap.get(DDUI);
    }

    public static DD putDD(DD dd) {
        return DDMap.put(dd.getDDUI(), dd);
    }

    public static DD removeDD(String DDUI) {
        return DDMap.remove(DDUI);
    }

    public static ActorSystem getClientSystem() {
        return clientSystem;
    }

    public static void setClientSystem(ActorSystem clientSystem) {
        ClientManager.clientSystem = clientSystem;
    }
}
