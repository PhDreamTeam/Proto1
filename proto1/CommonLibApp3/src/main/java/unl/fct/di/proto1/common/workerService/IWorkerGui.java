package unl.fct.di.proto1.common.workerService;

import akka.actor.ActorSystem;
import unl.fct.di.proto1.common.IConsole;
import unl.fct.di.proto1.common.lib.ActorNode;
import unl.fct.di.proto1.common.lib.core.worker.DDPartition;

import java.io.*;
import java.util.List;

/**
 * Created by AT DR on 20-02-2015.
 *
 */
public interface IWorkerGui extends IConsole{

    public ActorSystem createSystem(String systemName) throws IOException;






    // Interface methods
    List<ActorNode> getServices();

    void updateViewServices();

    void addService(ActorNode newNode);


    List<WorkerRequest> getRequests();

    void updateViewRequests();

    void addRequest(WorkerRequest req);


    List<DDPartition> getDDPartitions();

    void updateViewDDPartitions();

    public void addDDPartition(DDPartition newPartition);



    void clearPartitions();

    FileOutputStream openFileOutput(String fileName) throws FileNotFoundException ;

    FileInputStream openFileInput(String fileName) throws FileNotFoundException ;

    void serializeDDPartitions(ObjectOutputStream oos) throws IOException;

    void deleteFile(String fileName);

    String[] getFileList();


}
