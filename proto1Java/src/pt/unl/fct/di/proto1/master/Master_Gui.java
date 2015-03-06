package pt.unl.fct.di.proto1.master;

import akka.actor.ActorSystem;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import pt.unl.fct.di.proto1.common.ProportionalLayout;
import unl.fct.di.proto1.common.IConsole;
import unl.fct.di.proto1.common.lib.ActorNode;
import unl.fct.di.proto1.common.lib.ActorType;
import unl.fct.di.proto1.common.lib.core.master.DDMaster;
import unl.fct.di.proto1.common.lib.core.master.MasterRequest;
import unl.fct.di.proto1.common.masterService.IMasterGui;
import unl.fct.di.proto1.common.masterService.MasterService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by AT DR on 25-02-2015.
 *
 */
public class Master_Gui extends JFrame implements IMasterGui {

    // consoleTextArea area
    JTextArea consoleTextArea = new JTextArea();

    // workers area
    ArrayList<ActorNode> workers = new ArrayList<>();
    DefaultListModel<ActorNode> listModelWorkers = new DefaultListModel<>();
    JList<ActorNode> listWorkers = new JList<>(listModelWorkers);

    // Clients area
    ArrayList<ActorNode> clients = new ArrayList<>();
    DefaultListModel<ActorNode> listModelClients = new DefaultListModel<>();
    JList<ActorNode> listClients = new JList<>(listModelClients);

    // Requests area
    ArrayList<MasterRequest> requests = new ArrayList<>();
    DefaultListModel<MasterRequest> listModelRequests = new DefaultListModel<>();
    JList<MasterRequest> listRequests = new JList<>(listModelRequests);

    // Datasets area
    ArrayList<DDMaster> data = new ArrayList<>();
    DefaultListModel<DDMaster> listModelData = new DefaultListModel<>();
    JList<DDMaster> listData = new JList<>(listModelData);

    MasterService masterService;

    String masterID = "ms";
    String workingPathName;

    IConsole console;


    // methods ===============================================

    public Master_Gui(String[] args) {
        // set console alias
        console = this;

        if (args.length == 1)
            masterID = args[0];

//        DefaultCaret caret = (DefaultCaret)consoleTextArea.getCaret();
//        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        workingPathName = "data/" + masterID;

        prepareWorkingDir();

        setTitle("..: Master Service :..");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(500, 600);
        setLocationRelativeTo(null);

        setLayout(new ProportionalLayout(0, 0.4f, 0, 0));

        JPanel panelViewer = new JPanel(new GridLayout(0, 1, 1, 1));
        add(panelViewer, ProportionalLayout.CENTER);

        // Actors panel
        JPanel panelActors = new JPanel(new GridLayout(1, 0));

        // workers
        listWorkers.setBorder(BorderFactory.createTitledBorder("Workers"));
        panelActors.add(new JScrollPane(listWorkers));
        listWorkers.setBackground(new Color(0x8bcc3f));
        listWorkers.setOpaque(true);

        // clients
        listClients.setBorder(BorderFactory.createTitledBorder("Clients"));
        panelActors.add(new JScrollPane(listClients));
        listClients.setBackground(new Color(0xffc7cc3f));
        listClients.setOpaque(true);

        panelViewer.add(panelActors);

        // requests
        listRequests.setBorder(BorderFactory.createTitledBorder("Requests"));
        panelViewer.add(new JScrollPane(listRequests));
        listRequests.setBackground(new Color(0xffccc827));
        listRequests.setOpaque(true);

        // Data
        listData.setBorder(BorderFactory.createTitledBorder("Data"));
        panelViewer.add(new JScrollPane(listData));
        listData.setBackground(new Color(0xffabff7c));
        listData.setOpaque(true);

        // consoleTextArea
        JScrollPane jsConsole = new JScrollPane(consoleTextArea);
        jsConsole.setBorder(BorderFactory.createTitledBorder("Console"));
        add(jsConsole, ProportionalLayout.SOUTH);
        jsConsole.setBackground(new Color(0xffd7ffc5));
        jsConsole.setOpaque(true);
        consoleTextArea.setBackground(new Color(0xffd7ffc5));
        consoleTextArea.setOpaque(true);


        setJMenuBar(getMenuBarMaster());

        // show frame
        setVisible(true);

        // create masterService
        try {
            masterService = new MasterService(this, masterID);
        } catch (Exception e) {
            console.printException(e);
        }
    }

    private JMenuBar getMenuBarMaster() {
        JMenuBar menuBar = new JMenuBar();

        JMenu menu = new JMenu("Actions");
        menuBar.add(menu);

        // List files item
        JMenuItem listFilesMenuItem = new JMenuItem("List files");
        listFilesMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                masterService.showFiles();
            }
        });
        menu.add(listFilesMenuItem);

        // Load state item
        JMenuItem loadStateMenuItem = new JMenuItem("Load state");
        loadStateMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                masterService.loadState();
            }
        });
        menu.add(loadStateMenuItem);

        // Save state item
        JMenuItem saveStateMenuItem = new JMenuItem("Save state");
        saveStateMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                masterService.saveState();
            }
        });
        menu.add(saveStateMenuItem);

        // Save state item
        JMenuItem clearPersistentStateMenuItem = new JMenuItem("Clear persistent state");
        clearPersistentStateMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                masterService.clearPersistentState();
            }
        });
        menu.add(clearPersistentStateMenuItem);

        return menuBar;
    }

    public static void main(final String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Master_Gui(args);
            }
        });
    }

    private void prepareWorkingDir() {
        File wd = new File(workingPathName);
        if (!wd.exists()) {
            wd.mkdirs();
        }
    }


    //
    public ActorSystem createSystem(String systemName) {
        Config conf = ConfigFactory.load("conf/applicationMasterService.conf");
        final ActorSystem system = ActorSystem.create(systemName, conf);
        return system;
    }


    // workers
    @Override
    public List<ActorNode> getWorkers() {
        return Collections.unmodifiableList(workers);
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

    //clients
    @Override
    public List<ActorNode> getClients() {
        return Collections.unmodifiableList(clients);
    }

    @Override
    public void updateViewClients() {
        // nothing to do
    }

    @Override
    public void addClient(ActorNode newNode) {
        clients.add(newNode);
        listModelClients.addElement(newNode);
        updateViewClients();
    }

    // Requests
    @Override
    public List<MasterRequest> getRequests() {
        return Collections.unmodifiableList(requests);
    }

    @Override
    public void updateViewRequests() {
        // nothing to do
    }

    @Override
    public void addRequest(MasterRequest mr) {
        requests.add(mr);
        listModelRequests.addElement(mr);
        updateViewRequests();
    }

    // Data
    @Override
    public List<DDMaster> getData() {
        return Collections.unmodifiableList(data);
    }

    @Override
    public void updateViewData() {
        // nothing to do
    }


    @Override
    public void addData(DDMaster dd) {
        data.add(dd);
        listModelData.addElement(dd);
        updateViewData();
    }

    @Override
    public void addActorNode(ActorNode actorNode) {
        if (actorNode.getType().equals(ActorType.Worker))
            addWorker(actorNode);
        else if (actorNode.getType().equals(ActorType.Client))
            addClient(actorNode);
        else console.println("Unexpected actorType: " + actorNode);
    }

    @Override
    public void updateViewOfActorNode(ActorNode actorNode) {
        if (actorNode.getType().equals(ActorType.Worker))
            updateViewWorkers();
        else if (actorNode.getType().equals(ActorType.Client))
            updateViewClients();
        else console.println("Unexpected actorType: " + actorNode);
    }

    // FILE OPERATION =========================

    @Override
    public FileOutputStream openFileOutput(String fileName) throws FileNotFoundException {
        return new FileOutputStream(workingPathName + "/" + fileName);
    }

    @Override
    public FileInputStream openFileInput(String fileName) throws FileNotFoundException {
        return new FileInputStream(workingPathName + "/" + fileName);
    }

    @Override
    public void deleteFile(String fileName) {
        File f = new File(workingPathName + "/" + fileName);
        f.delete();
    }

    @Override
    public String[] getFileList() {
        return new File(workingPathName).list();
    }


    // CONSOLE OPERATION =========================

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
