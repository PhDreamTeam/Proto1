package unl.fct.di.proto1.common.client;

import akka.actor.ActorSystem;
import unl.fct.di.proto1.common.IConsole;
import unl.fct.di.proto1.common.lib.ActorNode;
import unl.fct.di.proto1.common.lib.core.services.photo.IPhotoRemote;

import java.io.IOException;
import java.util.List;

/**
 * Created by AT DR on 20-02-2015.
 *
 */
public interface IClientGui extends IConsole {

    ActorSystem createSystem(String systemName) throws IOException;

    List<ActorNode> getServices();

    void updateViewServices() ;

    void addService(ActorNode newNode);

    void displayThumbnails(IPhotoRemote[] photos);

    void displayPhotos(Object[] photos);
}
