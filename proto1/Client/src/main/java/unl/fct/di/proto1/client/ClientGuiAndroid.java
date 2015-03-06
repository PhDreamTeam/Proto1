package unl.fct.di.proto1.client;

import akka.actor.ActorSystem;
import android.app.Activity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.typesafe.config.ConfigFactory;
import unl.fct.di.proto1.common.client.IClientGui;
import unl.fct.di.proto1.common.lib.ActorNode;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by AT DR on 20-02-2015.
 *
 */
public class ClientGuiAndroid implements IClientGui {
    Activity context;
    TextView console;

    ArrayList<ActorNode> services = new ArrayList<>();
    ArrayAdapter<ActorNode> adapterServices;
    ListView listViewServices;


    public ClientGuiAndroid(Activity context) {
        this.context = context;

        // create console
        this.console = (TextView) context.findViewById(R.id.TextViewConsole);


        // ADAPTER AND LIST VIEW SERVICES ------------------------------
        adapterServices = new ArrayAdapter<> (
                context, R.layout.mytextlistview, services);
        listViewServices = (ListView) context.findViewById(R.id.ListViewServices);
        listViewServices.setAdapter(adapterServices);
    }

    public ActorSystem createSystem(String systemName) throws IOException {
        InputStream ir = context.getResources().getAssets().open("application.conf");
        InputStreamReader is = new InputStreamReader(ir);

        final ActorSystem system = ActorSystem.create(systemName,
                ConfigFactory.parseReader(is).resolve());

        is.close();
        return system;
    }


    public List<ActorNode> getServices() {
        return Collections.unmodifiableList(services);
    }

    public void updateViewServices() {
        updateView(adapterServices, listViewServices);
    }

    public void addService(ActorNode newNode) {
        services.add(newNode);
        updateView(adapterServices, listViewServices);
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
