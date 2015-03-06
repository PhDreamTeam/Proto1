package pt.unl.fct.di.proto1.directory;

import akka.actor.ActorSystem;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import pt.unl.fct.di.proto1.common.ProportionalLayout;
import unl.fct.di.proto1.common.IConsole;
import unl.fct.di.proto1.common.directoryService.DirectoryService;
import unl.fct.di.proto1.common.directoryService.IDirectoryGui;
import unl.fct.di.proto1.common.lib.ActorNode;
import unl.fct.di.proto1.common.lib.ActorType;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by AT DR on 25-02-2015.
 *
 * TODO tolerância ao master desparecer e outros
 *
 */
public class Directory_Gui extends JFrame implements IDirectoryGui {

    // consoleTextArea area
    JTextArea consoleTextArea = new JTextArea();

    // masters area
    ArrayList<ActorNode> masters = new ArrayList<>();
    DefaultListModel<ActorNode> listModelMasters = new DefaultListModel<>();
    JList<ActorNode> listMasters = new JList<>(listModelMasters);

    // workers area
    ArrayList<ActorNode> workers = new ArrayList<>();
    DefaultListModel<ActorNode> listModelWorkers = new DefaultListModel<>();
    JList<ActorNode> listWorkers = new JList<>(listModelWorkers);

    DirectoryService directoryService;
    IConsole console;


    // methods ===============================================

    public void init() {

        console = this;

//        DefaultCaret caret = (DefaultCaret)consoleTextArea.getCaret();
//        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        setTitle("..: Directory Service :..");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(500, 600);
        setLocationRelativeTo(null);

        setLayout(new ProportionalLayout(0, 0.4f, 0, 0));

        JPanel panelViewer = new JPanel(new GridLayout(0, 1));
        add(panelViewer, ProportionalLayout.CENTER);

        // masters
        listMasters.setBorder(BorderFactory.createTitledBorder("Masters"));
        panelViewer.add(new JScrollPane(listMasters));
        listMasters.setBackground(new Color(0xffffdb64));
        listMasters.setOpaque(true);

        // workers
        listWorkers.setBorder(BorderFactory.createTitledBorder("Workers"));
        panelViewer.add(new JScrollPane(listWorkers));
        listWorkers.setBackground(new Color(0xffc8ff8a));
        listWorkers.setOpaque(true);


        // consoleTextArea
        JScrollPane jsConsole = new JScrollPane(consoleTextArea);
        jsConsole.setBorder(BorderFactory.createTitledBorder("Console"));
        add(jsConsole, ProportionalLayout.SOUTH);
        jsConsole.setBackground(new Color(0xF3F2EF));
        jsConsole.setOpaque(true);
        consoleTextArea.setBackground(new Color(0xF3F2EF));
        consoleTextArea.setOpaque(true);


        // show frame
        setVisible(true);

        // create clientService
        try {
            directoryService = new DirectoryService(this, "ds");
        } catch (Exception e) {
            printException(e);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Directory_Gui clientGui = new Directory_Gui();
                clientGui.init();
            }
        });
    }


    //
    public ActorSystem createSystem(String systemName)  {
        Config conf = ConfigFactory.load("conf/applicationDirectoryService.conf");
        final ActorSystem system = ActorSystem.create(systemName, conf);
        return system;
    }

    /* TODO DEBUG APAGARRR???*/
    @Override
    public List<ActorNode> getMasters() {
        return Collections.unmodifiableList(masters);
    }

    @Override
    public void updateViewMasters() {
        // nothing to do

    }


    @Override
    public void addMaster(ActorNode newNode) {
        masters.add(newNode);
        listModelMasters.addElement(newNode);
        updateViewMasters();
    }

    @Override
    public List<ActorNode> getWorkers() {
        return Collections.unmodifiableList(masters);
    }

    @Override
    public void updateViewWorkers() {
        // nothing to do
    }


    @Override
    public void addWorker(ActorNode newNode) {
        workers.add(newNode);
        listModelWorkers.addElement(newNode);
        updateViewWorkers();
    }

    @Override
    public void addActorNode(ActorNode an) {
        if (an.getType().equals(ActorType.Master)) {
            addMaster(an);
        } else if (an.getType().equals(ActorType.Worker)) {
            addWorker(an);
        }
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
