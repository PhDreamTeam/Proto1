package pt.unl.fct.di.proto1.worker;

import akka.actor.ActorSystem;
import com.typesafe.config.ConfigFactory;
import pt.unl.fct.di.proto1.common.ProportionalLayout;
import unl.fct.di.proto1.common.IConsole;
import unl.fct.di.proto1.common.lib.ActorNode;
import unl.fct.di.proto1.common.lib.core.worker.DDPartition;
import unl.fct.di.proto1.common.workerService.IWorkerGui;
import unl.fct.di.proto1.common.workerService.WorkerRequest;
import unl.fct.di.proto1.common.workerService.WorkerService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by AT DR on 25-02-2015.
 *
 */
public class Worker_Gui extends JFrame implements IWorkerGui {

    // consoleTextArea area
    JTextArea consoleTextArea = new JTextArea();

    // service area
    ArrayList<ActorNode> services = new ArrayList<>();
    DefaultListModel listModelServices = new DefaultListModel();
    JList<ActorNode> listServices = new JList<>(listModelServices);

    // pending requests
    ArrayList<WorkerRequest> requests = new ArrayList<>();
    DefaultListModel listModelRequests = new DefaultListModel();
    JList<WorkerRequest> listRequests = new JList<>(listModelRequests);

    // data partitions
    ArrayList<DDPartition> ddPartitions = new ArrayList<>();
    DefaultListModel listModelDDPartitions = new DefaultListModel();
    JList<DDPartition> listDDPartitions = new JList<>(listModelDDPartitions);

    WorkerService workerService;

    String workerID = "ws";
    String workingPathName;

    IConsole console;


    // methods ===============================================

    public Worker_Gui(String [] args) {
        if(args.length == 1)
            workerID = args[0];

        console = this;

//        DefaultCaret caret = (DefaultCaret)consoleTextArea.getCaret();
//        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        workingPathName = "data/" + workerID;

        prepareWorkingDir();

        setTitle("..: Worker " + workerID + " :..");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(500, 600);
        setLocationRelativeTo(null);

        ProportionalLayout pl = new ProportionalLayout(0, 0.4f, 0, 0);
        pl.setVgap(5);
        setLayout(pl);

        JPanel panelViewer = new JPanel(new GridLayout(0, 1, 1, 1));
        add(panelViewer, ProportionalLayout.CENTER);

        // services
        listServices.setBorder(BorderFactory.createTitledBorder("Services"));
        panelViewer.add(new JScrollPane(listServices));
        listServices.setBackground(new Color(0xff4672af));
        listServices.setOpaque(true);

        // pending Requests
        listRequests.setBorder(BorderFactory.createTitledBorder("Requests"));
        panelViewer.add(new JScrollPane(listRequests));
        listRequests.setBackground(new Color(0x8D9FAF));
        listRequests.setOpaque(true);

        // DD Partitions
        listDDPartitions.setBorder(BorderFactory.createTitledBorder("DD Partitions"));
        panelViewer.add(new JScrollPane(listDDPartitions));
        listDDPartitions.setBackground(new Color(0xffa6aed1));
        listDDPartitions.setOpaque(true);

        // consoleTextArea
        JScrollPane jsConsole = new JScrollPane(consoleTextArea);
        jsConsole.setBorder(BorderFactory.createTitledBorder("Console"));
        add(jsConsole, ProportionalLayout.SOUTH);
        jsConsole.setBackground(new Color(0xffc8d5f4));
        jsConsole.setOpaque(true);
        consoleTextArea.setBackground(new Color(0xffc8d5f4));
        consoleTextArea.setOpaque(true);

        setJMenuBar(getMenuBarMaster());

        // show frame
        setVisible(true);

        // create clientService
        try {
            workerService = new WorkerService(this,  workerID);
        } catch (Exception e) {
            printException(e);
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
                workerService.showFiles();
            }
        });
        menu.add(listFilesMenuItem);

        // Load state item
        JMenuItem loadStateMenuItem = new JMenuItem("Load partitions");
        loadStateMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                workerService.loadPartitionsFromDisk();
            }
        });
        menu.add(loadStateMenuItem);


        // Save state item
        JMenuItem clearPersistentStateMenuItem = new JMenuItem("Clear persistent state");
        clearPersistentStateMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                workerService.clearPartitions();
            }
        });
        menu.add(clearPersistentStateMenuItem);

        return menuBar;
    }



    public static void main(final String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Worker_Gui(args);
            }
        });
    }

    private void prepareWorkingDir() {
        File wd = new File(workingPathName);
        if(!wd.exists()) {
            wd.mkdirs();
        }
    }


    //
    public ActorSystem createSystem(String systemName) throws IOException {
        final ActorSystem system = ActorSystem.create(systemName,
                ConfigFactory.load("conf/applicationWorkerService.conf"));
        return system;
    }

    // Interface methods
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
    public List<WorkerRequest> getRequests() {
        return Collections.unmodifiableList(requests);
    }

    @Override
    public void updateViewRequests() {
        // nothing to do
    }

    @Override
    public void addRequest(WorkerRequest req) {
        requests.add(req);
        listModelRequests.addElement(req);
        updateViewRequests();
    }


    @Override
    public List<DDPartition> getDDPartitions() {
        return Collections.unmodifiableList(ddPartitions);
    }

    @Override
    public void updateViewDDPartitions() {
        // nothing to do
    }

    @Override
    public void addDDPartition(DDPartition dd) {
        ddPartitions.add(dd);
        listModelDDPartitions.addElement(dd);
        updateViewDDPartitions();
    }





    @Override
    public void clearPartitions() {

    }

    @Override
    public FileOutputStream openFileOutput(String fileName) throws FileNotFoundException {
        return new FileOutputStream(workingPathName + "/" + fileName);
    }

    @Override
    public FileInputStream openFileInput(String fileName) throws FileNotFoundException {
        return new FileInputStream(workingPathName + "/" + fileName);
    }

    @Override
    public void serializeDDPartitions(ObjectOutputStream oos) throws IOException {
        oos.writeObject(ddPartitions);
    }

    @Override
    public void deleteFile(String fileName) {
        File file = new File(workingPathName + "/" + fileName);
        file.delete();
    }

    @Override
    public String[] getFileList() {
        return new File(workingPathName).list();
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
