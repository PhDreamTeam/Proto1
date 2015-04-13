package pt.unl.fct.di.proto1.client;

import akka.actor.ActorSystem;
import com.typesafe.config.ConfigFactory;
import pt.unl.fct.di.proto1.common.ProportionalLayout;
import pt.unl.fct.di.proto1.services.photos.Photo;
import unl.fct.di.proto1.common.IConsole;
import unl.fct.di.proto1.common.client.Client;
import unl.fct.di.proto1.common.client.IClientGui;
import unl.fct.di.proto1.common.lib.ActorNode;
import unl.fct.di.proto1.common.lib.core.services.photo.IPhotoRemote;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by AT DR on 25-02-2015.
 *
 */
public class Client_Gui extends JFrame implements IClientGui {

    // consoleTextArea area
    JTextArea consoleTextArea = new JTextArea();

    // services area
    ArrayList<ActorNode> services = new ArrayList<>();
    DefaultListModel<ActorNode> listModelServices = new DefaultListModel<>();
    JList<ActorNode> listServices = new JList<>(listModelServices);

    // control area
    private JTextField textFieldUUID;

    String clientID = "c1";

    Client client;

    // console alias
    IConsole console;


    // methods ===============================================

    public Client_Gui(String[] args) {
        // console alias
        console = this;

        if (args.length == 1)
            clientID = args[0];

//        DefaultCaret caret = (DefaultCaret)consoleTextArea.getCaret();
//        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        setTitle("..: Client " + clientID + " :..");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(500, 600);
        setLocationRelativeTo(null);

        setLayout(new ProportionalLayout(0, 0.4f, 0, 0));

        JPanel panelViewer = new JPanel(new GridLayout(0, 1));
        add(panelViewer, ProportionalLayout.CENTER);

        // services
        listServices.setBorder(BorderFactory.createTitledBorder("Services"));
        panelViewer.add(new JScrollPane(listServices));
        listServices.setBackground(new Color(0xCC376A));
        listServices.setOpaque(true);

        // control
        // create DDInt, create DDObject, Open DDObject
        JButton createDDIntButton = new JButton("Create DDInt");
        JButton createDDObjectButton = new JButton("Create DDObject");
        JButton openDDObjectButton = new JButton("Open DDObject");
        JButton photosButton = new JButton("INTERNAL PHOTOS");
        JButton mergeTestButton = new JButton("Merge Test");
        JButton reduceTestButton = new JButton("Reduce Test");
        JButton photosIncompleteButton = new JButton("Incomplete PHOTOS");
        JButton piReduceTestButton = new JButton("Pi Reduce Test");
        JButton printlnButton = new JButton("Println");

        JPanel panelControl = new JPanel();
        panelControl.setBorder(BorderFactory.createTitledBorder("Control"));
        panelControl.add(createDDIntButton);
        panelControl.add(createDDObjectButton);
        panelControl.add(openDDObjectButton);
        panelControl.add(photosButton);
        panelControl.add(mergeTestButton);
        panelControl.add(reduceTestButton);
        panelControl.add(photosIncompleteButton);
        panelControl.add(piReduceTestButton);
        panelControl.add(printlnButton);
        textFieldUUID = new JTextField(30);
        panelControl.add(textFieldUUID);
        panelViewer.add(panelControl);
        panelControl.setBackground(new Color(0xCC99A0));
        panelControl.setOpaque(true);


        // consoleTextArea
        JScrollPane jsConsole = new JScrollPane(consoleTextArea);
        jsConsole.setBorder(BorderFactory.createTitledBorder("Console"));
        add(jsConsole, ProportionalLayout.SOUTH);
        jsConsole.setBackground(new Color(0xCCB5BA));
        jsConsole.setOpaque(true);
        consoleTextArea.setBackground(new Color(0xCCB5BA));
        consoleTextArea.setOpaque(true);


        // action listeners ------------------------------------------

        printlnButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                console.println("");
            }
        });

        createDDIntButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                client.workWithInts();
            }
        });

        createDDObjectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                client.workWithObjects();
            }
        });

        openDDObjectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                client.workWithExistingObjects(textFieldUUID.getText());
            }
        });

        photosButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                client.workWithPhotos();
            }
        });

        mergeTestButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                client.workWithMergingPhotos();
            }
        });

        reduceTestButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                client.workWithReduceOnPhotos();
            }
        });

        piReduceTestButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                client.PiReduceExample();
            }
        });

        photosIncompleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                client.workWithIncompletePhotos();
            }
        });


        // show frame
        setVisible(true);

        // create clientService
        try {
            client = new Client(this, clientID);
        } catch (Exception e) {
            printException(e);
        }
    }

    public static void main(final String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Client_Gui(args);
            }
        });
    }


    public ActorSystem createSystem(String systemName) throws IOException {
        return ActorSystem.create(systemName,
                ConfigFactory.load("conf/applicationClient.conf"));
    }

    @Override
    public List<ActorNode> getServices() {
        return Collections.unmodifiableList(services);
    }

    @Override
    public void updateViewServices() {
        // nothing to do
    }


    @Override
    public void addService(ActorNode newNode) {
        services.add(newNode);
        listModelServices.addElement(newNode);
        updateViewServices();
    }


    public void displayThumbnails(final IPhotoRemote[] photos) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame jf = new JFrame();
                jf.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

                JPanel jp = new JPanel();
                JScrollPane scrollPane = new JScrollPane(jp);
                jf.add(scrollPane);


                for (int i = 0; i < photos.length; i++) {
                    ImageIcon t = null;
                    try {
                        t = new ImageIcon(photos[i].getThumbnail());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    jp.add(new JLabel(t));
                }
                jf.setSize(300, 400);
                jf.setLocationRelativeTo(null);

                jf.setVisible(true);
            }
        });
    }


    public void displayPhotos(final Object[] photos) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame jf = new JFrame();
                jf.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);


                JPanel jp = new JPanel();
                JScrollPane scrollPane = new JScrollPane(jp);
                jf.add(scrollPane);


                for (int i = 0; i < photos.length; i++) {
                    Photo p = ((Photo) photos[i]);
                    console.println("Loading photo: " + p.getPhotoUuid());
                    ImageIcon t = null;
                    try {
                        t = new ImageIcon(p.getPhotoInBytes());
                        jp.add(new JLabel(t));
                    } catch (Exception e) {
                        console.println("Error loading photo " + p.getPhotoUuid());
                    }
                }
                jf.pack();
                jf.setLocationRelativeTo(null);

                jf.setVisible(true);
            }
        });
    }


     @Override
     public void println(String msg) {
         consoleTextArea.append(msg + "\n");
         consoleTextArea.setCaretPosition(consoleTextArea.getDocument().getLength());
     }

     @Override
     public void println() {
         consoleTextArea.append("\n");
         consoleTextArea.setCaretPosition(consoleTextArea.getDocument().getLength());
     }

     @Override
     public void printException(Exception ex) {
         println(ex.getMessage());
         ex.printStackTrace();
     }
 }
