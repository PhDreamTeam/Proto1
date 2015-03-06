package unl.fct.di.proto1.directoryservice;

import akka.actor.ActorSystem;
import android.app.Activity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.typesafe.config.ConfigFactory;
import unl.fct.di.proto1.common.directoryService.IDirectoryGui;
import unl.fct.di.proto1.common.lib.ActorNode;
import unl.fct.di.proto1.common.lib.ActorType;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by AT DR on 20-02-2015.
 */
public class DirectoryGuiAndroid implements IDirectoryGui {
    Activity context;
    TextView console;

    // container of Masters
    ArrayList<ActorNode> masters = new ArrayList<>();
    ArrayAdapter<ActorNode> adapterMasters = null;
    ListView listViewMasters = null;

    // container of workers
    ArrayList<ActorNode> workers = new ArrayList<>();
    ArrayAdapter<ActorNode> adapterWorkers = null;
    ListView listViewWorkers = null;


    public DirectoryGuiAndroid(Activity context) {
        this.context = context;

        // create console
        this.console = (TextView) context.findViewById(R.id.TextViewConsole);


        // ADAPTER AND LIST VIEW SERVICES ------------------------------
        adapterMasters = new ArrayAdapter<>(
                context, R.layout.mytextlistview, masters);
        listViewMasters = (ListView) context.findViewById(R.id.listViewMasters);
        listViewMasters.setAdapter(adapterMasters);
    }

    public ActorSystem createSystem(String systemName) throws IOException {
        InputStream ir = context.getResources().getAssets().open("application.conf");
        InputStreamReader is = new InputStreamReader(ir);

        final ActorSystem system = ActorSystem.create(systemName,
                ConfigFactory.parseReader(is).resolve());

        is.close();
        return system;
    }


    // masters

    @Override
    public List<ActorNode> getMasters() {
        return Collections.unmodifiableList(masters);
    }

    @Override
    public void updateViewMasters() {
        updateView(adapterMasters, listViewMasters);
    }

    @Override
    public void addMaster(ActorNode newNode) {
        masters.add(newNode);
        updateViewMasters();
    }


    // workers

    @Override
    public List<ActorNode> getWorkers() {
        return Collections.unmodifiableList(workers);
    }

    @Override
    public void updateViewWorkers() {
        updateView(adapterWorkers, listViewWorkers);
    }

    @Override
    public void addWorker(ActorNode newNode) {
        workers.add(newNode);
        updateViewWorkers();
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

    @Override
    public void addActorNode(ActorNode an) {
        if (an.getType().equals(ActorType.Master))
            addMaster(an);
        else if (an.getType().equals(ActorType.Worker))
            addWorker(an);
        else
            println("Error adding actor of unexpected type!!");
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
