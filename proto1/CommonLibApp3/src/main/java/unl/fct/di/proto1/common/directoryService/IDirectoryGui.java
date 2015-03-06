package unl.fct.di.proto1.common.directoryService;

import akka.actor.ActorSystem;
import unl.fct.di.proto1.common.IConsole;
import unl.fct.di.proto1.common.lib.ActorNode;

import java.io.IOException;
import java.util.List;

/**
 * Created by AT DR on 20-02-2015.
 *
 */
public interface IDirectoryGui extends IConsole {

    ActorSystem createSystem(String systemName) throws IOException;

    List<ActorNode> getMasters();
    void updateViewMasters() ;
    void addMaster(ActorNode newNode);

    List<ActorNode> getWorkers();
    void updateViewWorkers() ;
    void addWorker(ActorNode newNode);

    void addActorNode(ActorNode an);
}
