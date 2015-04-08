package unl.fct.di.proto1.workerservice;

import akka.actor.ActorSystem;
import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.typesafe.config.ConfigFactory;
import pt.unl.fct.di.proto1.services.photos.PhotoWorker;
import unl.fct.di.proto1.common.lib.ActorNode;
import unl.fct.di.proto1.common.lib.core.services.photo.IPhotoWorker;
import unl.fct.di.proto1.common.lib.core.worker.DDPartition;
import unl.fct.di.proto1.common.workerService.IWorkerGui;
import unl.fct.di.proto1.common.workerService.WorkerRequest;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by AT DR on 20-02-2015.
 *
 */
public class WorkerGuiAndroid implements IWorkerGui {
    Activity context;
    TextView console;

    ArrayList<ActorNode> services = new ArrayList<>();
    ArrayAdapter<ActorNode> adapterServices;
    ListView listViewServices;

    ArrayList<DDPartition> DDPartitions = new ArrayList<>();
    ArrayAdapter<DDPartition> adapterDDPartitions;
    ListView listViewDDPartitions;

    ArrayList<WorkerRequest> requests = new ArrayList<>();
    ArrayAdapter<WorkerRequest> adapterRequests;
    ListView listViewRequests;



    public WorkerGuiAndroid(Activity ctx) {
        this.context = ctx;

        // create console
        this.console = (TextView) context.findViewById(R.id.TextViewConsole);

        // ADAPTER AND LIST VIEW MASTERS ------------------------------
        adapterServices = new ArrayAdapter<> (
                context, R.layout.mytextlistview, services);
        listViewServices = (ListView) context.findViewById(R.id.ListViewServices);
        listViewServices.setAdapter(adapterServices);

        // ADAPTER AND LIST VIEW DDPartition ------------------------------
        adapterDDPartitions = new ArrayAdapter<> (
                context, R.layout.mytextlistview, getDDPartitions());
        listViewDDPartitions = (ListView) context.findViewById(R.id.ListViewDDPartitions);
        listViewDDPartitions.setAdapter(adapterDDPartitions);

        // ADAPTER AND LIST VIEW REQUESTS ------------------------------
        adapterRequests = new ArrayAdapter<> (
                context, R.layout.mytextlistview, getRequests());
        listViewRequests = (ListView) context.findViewById(R.id.ListViewRequests);
        listViewRequests.setAdapter(adapterRequests);


    }

    public ActorSystem createSystem(String systemName) throws IOException {
        InputStream ir = context.getResources().getAssets().open("application.conf");
        InputStreamReader is = new InputStreamReader(ir);

        final ActorSystem system = ActorSystem.create(systemName,
                ConfigFactory.parseReader(is).resolve());

        is.close();
        return system;
    }

   // services
   @Override
   public List<ActorNode> getServices() {
       return Collections.unmodifiableList(services);
   }

    @Override
    public void updateViewServices() {
        updateView(adapterServices, listViewServices);
    }

    @Override
    public void addService(ActorNode newNode) {
        services.add(newNode);
        updateViewServices();
    }


    // DDPartitions

    @Override
    public List<DDPartition> getDDPartitions() {
        return Collections.unmodifiableList(DDPartitions);
    }

    @Override
    public void updateViewDDPartitions() {
        updateView(adapterDDPartitions, listViewDDPartitions);
    }

    @Override
    public void addDDPartition(DDPartition newPartition) {
        DDPartitions.add(newPartition);
        updateViewDDPartitions();
    }

    public void clearPartitions() {
        DDPartitions.clear();
        updateViewDDPartitions();
    }


    // requests

    @Override
    public List<WorkerRequest> getRequests() {
        return Collections.unmodifiableList(requests);
    }

    @Override
    public void updateViewRequests() {
        updateView(adapterRequests, listViewRequests);
    }

    @Override
    public void addRequest(WorkerRequest req) {
        requests.add(req);
        updateViewRequests();
    }



    private void updateView(final ArrayAdapter<?> adapter, View view) {
        if (adapter != null) {
            view.post(new Runnable() {
                public void run() {
                    adapter.notifyDataSetChanged();
                }
            });
        }
    }



    public FileOutputStream openFileOutput(String fileName) throws FileNotFoundException {
        return context.openFileOutput(fileName, Context.MODE_PRIVATE);
    }

    public FileInputStream openFileInput(String fileName) throws FileNotFoundException {
        return context.openFileInput(fileName);
    }

    @Override
    public void serializeDDPartitions(ObjectOutputStream oos) throws IOException {
        oos.writeObject(DDPartitions);
    }

    public void deleteFile(String fileName) {
        context.deleteFile(fileName);
    }

    @Override
    public String[] getFileList() {
        return context.fileList();
    }

    @Override
    public IPhotoWorker createPhotoWorker(String uuid, String pathFileName, ActorNode workerActorNode) {
        return new PhotoWorker(uuid, pathFileName, workerActorNode);
    }

    public void println(final String msg) {
        console.post(new Runnable() {
            @Override
            public void run() {
                //  console.append(">" + msg + "\n");
                CharSequence txt = console.getText();
                console.setText(">" + msg + (txt.equals("") ? "" : "\n" + txt));
            }
        });
    }

    public void println() {
        println("");
    }

    public void printException(Exception ex) {
        println("Exception: " + ex.getMessage());
        ex.printStackTrace();
    }
}
