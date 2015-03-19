package pt.unl.fct.di.proto1.client;

import akka.actor.ActorSystem;
import com.typesafe.config.ConfigFactory;
import pt.unl.fct.di.proto1.common.ProportionalLayout;
import unl.fct.di.proto1.common.IConsole;
import unl.fct.di.proto1.common.client.Client;
import unl.fct.di.proto1.common.client.IClientGui;
import unl.fct.di.proto1.common.lib.ActorNode;

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

        if(args.length == 1)
            clientID = args[0];

//        DefaultCaret caret = (DefaultCaret)consoleTextArea.getCaret();
//        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        setTitle("..: Client " + clientID +  " :..");
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
        JButton printlnButton = new JButton("Println");
        JButton mergeTestButton = new JButton("Merge Test");
        JButton reduceTestButton = new JButton("Reduce Test");
        JButton piReduceTestButton = new JButton("Pi Reduce Test");

        JPanel panelControl = new JPanel();
        panelControl.setBorder(BorderFactory.createTitledBorder("Control"));
        panelControl.add(createDDIntButton);
        panelControl.add(createDDObjectButton);
        panelControl.add(openDDObjectButton);
        panelControl.add(photosButton);
        panelControl.add(mergeTestButton);
        panelControl.add(reduceTestButton);
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
