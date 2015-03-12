package unl.fct.di.proto1.common.lib;


import akka.actor.ActorNotFound;
import akka.actor.ActorRef;
import akka.actor.ActorRefFactory;
import akka.actor.ActorSelection;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;


public class ActorNode implements Serializable {
    // must be transient to avoid being serialized
    transient ActorRef actorRef = null;

    String protocol;
    String ip;
    String port;
    String systemName;
    String actorName;
    ActorState state = ActorState.INIT;

    // methods

    public ActorType getType() {
        return type;
    }

    public void setType(ActorType type) {
        this.type = type;
    }

    ActorType type;


    public ActorNode(ActorRef actorRef, ActorState state, ActorType type) {
        setActorRef(actorRef);
        this.state = state;
        this.type = type;
    }

    public ActorNode(String protocol, String systemName, String ip, String port,
                     String actorName, ActorType type) {

        setProtocol(protocol);
        setIp(ip);
        setPort(port);
        setSystemName(systemName);
        setActorName(actorName);
        setType(type);
        // state = UNKNOWN
        // actorRef = null
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

    public String getActorName() {
        return actorName;
    }

    public void setActorName(String actorName) {
        this.actorName = actorName;
    }

    public void setActorRef(ActorRef actorRef) {
        this.actorRef = actorRef;
        setProtocol(actorRef.path().address().protocol());
        setIp(actorRef.path().address().host().get());
        setPort(actorRef.path().address().port().get().toString());
        setSystemName(actorRef.path().address().system());
        setActorName(actorRef.path().name());
    }

    public ActorRef getActorRef() {
        // May be a good idea to auto-generate actorRef when null
        return actorRef;
    }

    public ActorState getState() {
        return state;
    }

    public void setState(ActorState state) {
        this.state = state;
    }


    public boolean isStateActive() {
        return getState().equals(ActorState.ACTIVE);
    }

    public ActorRef generateActorRef(ActorRefFactory arf) throws Exception {
        // if DISCONNECTED return null
        if(state.equals(ActorState.DISCONNECTED))
            return null;

       // Log.d("GenerateActorRef", "toString actorRef -> " + getPath());
        ActorSelection mstActor = arf.actorSelection(getPath());
        // TODO blocking code SOLVE THIS IN A ASYNCHRONOUS MODE
        try {
            Future<ActorRef> f = mstActor.resolveOne(new FiniteDuration(10, TimeUnit.SECONDS));
            actorRef = Await.result(f, Duration.Inf());
            setState(ActorState.ACTIVE);
        } catch(ActorNotFound e){
            actorRef = null;
        }
        return actorRef;
    }

    public String getPath() {
        return getProtocol() + "://" + getSystemName() + "@" + getIp() + ":" + getPort() + "/user/" + getActorName();
    }

    public String toShortString() {
        return getActorName() + " " + getIp() + ":" + getPort();
    }

    @Override
    public String toString() {
        // return  getPath() + " - " + state;
        return toShortString() + " - " + state.toString().substring(0, 4);
    }
}
