package unl.fct.di.proto1.masterservice;

import akka.actor.ActorSystem;
import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.typesafe.config.ConfigFactory;
import unl.fct.di.proto1.common.lib.ActorNode;
import unl.fct.di.proto1.common.lib.core.master.DDMaster;
import unl.fct.di.proto1.common.lib.core.master.MasterRequest;
import unl.fct.di.proto1.common.masterService.IMasterGui;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by AT DR on 20-02-2015.
 *
 */
public class MasterGuiAndroid implements IMasterGui {
    Activity context;
    TextView console;

    // container of workers
    ArrayList<ActorNode> workers = new ArrayList<>();
    ArrayAdapter<ActorNode> adapterWorkers = null;
    ListView listViewWorkers = null;

    // container of clients
    ArrayList<ActorNode> clients = new ArrayList<>();
    ArrayAdapter<ActorNode> adapterClients = null;
    ListView listViewClients = null;

    // container of Requests
    ArrayList<MasterRequest> requests =  new ArrayList<>();
    ArrayAdapter<MasterRequest> adapterRequests = null;
    ListView listViewRequests = null;

    // TODO create a FinishedRequests list (and for screen) to distinguish from pending requests

    // container of data
    ArrayList<DDMaster> data =  new ArrayList<>();
    ArrayAdapter<DDMaster> adapterData = null;
    ListView listViewData = null;


    public MasterGuiAndroid(Activity context) {
        this.context = context;
        this.console = (TextView) context.findViewById(R.id.TextViewConsole);

        // set Workers list view adapter
        adapterWorkers = new ArrayAdapter<>(
                context, R.layout.mytextlistview, workers);
        listViewWorkers = (ListView) context.findViewById(R.id.ListViewWorkers);
        listViewWorkers.setAdapter(adapterWorkers);

        // set Clients list view adapter
        adapterClients = new ArrayAdapter<>(
                context, R.layout.mytextlistview, clients);
        listViewClients = (ListView) context.findViewById(R.id.ListViewClients);
        listViewClients.setAdapter(adapterClients);

        // set Requests list view adapter
        adapterRequests = new ArrayAdapter<>(
                context, R.layout.mytextlistview, requests);
        listViewRequests = (ListView) context.findViewById(R.id.ListViewRequests);
        listViewRequests.setAdapter(adapterRequests);

        // set Data list view adapter
        adapterData = new ArrayAdapter<>(
                context, R.layout.mytextlistview, data);
        listViewData = (ListView) context.findViewById(R.id.ListViewData);
        listViewData.setAdapter(adapterData);
    }

    public ActorSystem createSystem(String systemName) throws IOException {
        InputStream ir = context.getResources().getAssets().open("application.conf");
        InputStreamReader is = new InputStreamReader(ir);

        final ActorSystem system = ActorSystem.create(systemName,
                ConfigFactory.parseReader(is).resolve());

        is.close();
        return system;
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


    // getters

    @Override
    public List<ActorNode> getWorkers() {
        return Collections.unmodifiableList(workers);
    }

    @Override
    public List<ActorNode> getClients() {
        return Collections.unmodifiableList(clients);
    }

    @Override
    public List<MasterRequest> getRequests() {
        return Collections.unmodifiableList(requests);
    }

    @Override
    public List<DDMaster> getData() {
        return Collections.unmodifiableList(data);
    }


    // update Views

    @Override
    public void updateViewWorkers() {
        updateView(adapterWorkers, listViewWorkers);
    }

    @Override
    public void updateViewClients() {
        updateView(adapterClients, listViewClients);
    }

    @Override
    public void updateViewRequests() {
        updateView(adapterRequests, listViewRequests);
    }

    @Override
    public void updateViewData() {
        updateView(adapterData, listViewData);
    }


    // add something

    @Override
    public void addWorker(ActorNode an) {
        workers.add(an);
        updateViewWorkers();
    }

    @Override
    public void addClient(ActorNode an) {
        clients.add(an);
        updateViewClients();
    }

    @Override
    public void addRequest(MasterRequest req) {
        requests.add(req);
        updateViewRequests();
    }

    @Override
    public void addData(DDMaster dd) {
        data.add(dd);
        updateViewData();
    }

    @Override
    public FileOutputStream openFileOutput(String fileName) throws FileNotFoundException {
        return context.openFileOutput(fileName, Context.MODE_PRIVATE);
    }

    @Override
    public FileInputStream openFileInput(String fileName) throws FileNotFoundException {
        return context.openFileInput(fileName);
    }

    public void deleteFile(String fileName) {
        context.deleteFile(fileName);
    }

    @Override
    public void addActorNode(ActorNode actorToUpdate) {

    }

    @Override
    public void updateViewOfActorNode(ActorNode actorToUpdate) {

    }

    @Override
    public String[] getFileList() {
        return context.fileList();
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
