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
import unl.fct.di.proto1.common.lib.core.services.photo.IPhotoRemote;

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

    @Override
    public void displayThumbnails(IPhotoRemote[] photos) {
        //TODO To be implemented
        throw new IllegalStateException("Not implemented. To Do!");
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


    public void displayThumbnails(Object[] photos) {
//        // TODO this code should run in swing EDT
//
//        JFrame jf = new JFrame();
//        jf.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
//
//        JPanel jp = new JPanel();
//        JScrollPane scrollPane = new JScrollPane(jp);
//        jf.add(scrollPane);
//
//        for (int i = 0; i < photos.length; i++) {
//            ImageIcon t = new ImageIcon(((Photo) photos[i]).getThumbnail());
//            jp.add(new JLabel(t));
//        }
//        jf.setSize(300, 400);
//        jf.setLocationRelativeTo(null);
//
//        jf.setVisible(true);
    }

    public void displayPhotos(Object[] photos) {
//        // TODO this code should run in swing EDT
//
//        JFrame jf = new JFrame();
//        jf.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
//
//
//        JPanel jp = new JPanel();
//        JScrollPane scrollPane = new JScrollPane(jp);
//        jf.add(scrollPane);
//
//
//        for (int i = 0; i < photos.length; i++) {
//            Photo p = ((Photo) photos[i]);
//            console.println("Loading photo: " + p.getPhotoUuid());
//            ImageIcon t = null;
//            try {
//                t = new ImageIcon(p.getPhotoInBytes());
//                jp.add(new JLabel(t));
//            } catch (Exception e) {
//                console.println("Error loading photo " + p.getPhotoUuid());
//            }
//        }
//        jf.pack();
//        jf.setLocationRelativeTo(null);
//
//        jf.setVisible(true);
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
