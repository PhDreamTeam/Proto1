package unl.fct.di.proto1.common.masterService;

import akka.actor.ActorSystem;
import unl.fct.di.proto1.common.IConsole;
import unl.fct.di.proto1.common.lib.ActorNode;
import unl.fct.di.proto1.common.lib.core.master.DDMaster;
import unl.fct.di.proto1.common.lib.core.master.MasterRequest;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by AT DR on 20-02-2015.
 *
 */
public interface IMasterGui extends IConsole{

    ActorSystem createSystem(String systemName) throws IOException;

    List<ActorNode> getWorkers();

    List<ActorNode> getClients();

    List<MasterRequest> getRequests();

    List<DDMaster> getData();


    void updateViewWorkers() ;

    void updateViewClients() ;

    void updateViewRequests() ;

    void updateViewData() ;


    void addWorker(ActorNode an);

    void addClient(ActorNode an);

    void addRequest(MasterRequest req);

    void addData(DDMaster dd);


    FileOutputStream openFileOutput(String fileName) throws FileNotFoundException ;

    FileInputStream openFileInput(String fileName) throws FileNotFoundException;

    void deleteFile(String fileName);

    /* Do not supply the path name. Either Android or Java standalone already know the proper path */
    String[] getFileList();

    void addActorNode(ActorNode actorToUpdate);

    void updateViewOfActorNode(ActorNode actorToUpdate);
}