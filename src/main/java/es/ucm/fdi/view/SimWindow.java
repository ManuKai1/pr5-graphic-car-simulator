package es.ucm.fdi.view;

import java.awt.BorderLayout;
import java.awt.Container;
import java.io.File;
import java.io.OutputStream;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;

import es.ucm.fdi.control.Controller;
import es.ucm.fdi.model.simulation.TrafficSimulation;
import es.ucm.fdi.model.simulation.TrafficSimulation.Listener;
import es.ucm.fdi.model.simulation.TrafficSimulation.UpdateEvent;

public class SimWindow extends JFrame implements Listener {
	//Faltaría separación de atributos
	private Controller control;
	private OutputStream reports;

	private JPanel mainPanel;
	private JPanel contentPanel1;
	private JPanel contentPanel2;
	private JPanel contentPanel3;
	private JPanel contentPanel4;
	private JPanel contentPanel5;
	
	
	private JMenu fileMenu;
	private JMenu simulatorMenu;
	private JMenu reportsMenu;
	private JToolBar toolBar;
	
	private JFileChooser fc;
	private File currentFile;
	
	private JButton loadButton;
	private JButton saveButton;
	private JButton clearEventsButton;
	private JButton checkInEventsButton;
	private JButton runButton;
	private JButton stopButton;
	private JButton resetButton;
	private JButton generateReportsButton;
	private JButton saveReportsButton;
	private JButton clearReportsButton;
	private JButton quitButton;
	
	private JSpinner stepsSpinner;
	
	private JTextField timeViewer;
	
	private JTextArea eventsTextArea;
	private JTextArea reportTextArea;
	
	//Tablas
	private SimTable eventsTable;
	private SimTable vehiclesTable;
	private SimTable roadsTable;
	private SimTable junctionsTable;
	
	//Opcional
	//private ReportDialog reportDialog;
	
	public SimWindow(TrafficSimulation simulator, Controller ctrl, String inFileName) {
		super("Traffic Simulator");
		control = ctrl;
		currentFile = inFileName != null ? new File(inFileName) : null;
		//reports = new JTextAreaOutputStream(reportTextArea,null);
		//ctrl.setOutStream(reports);
		initGUI();
		//simulator.addSimulatorListener(this);
	}
	
	private void addComponentsToLayout(){
		addMenuBar(); // barra de menus
		addToolBar(); // barra de herramientas
		addEventsEditor(); // editor de eventos
		addEventsView(); // cola de eventos
		addReportsArea(); // zona de informes
		addVehiclesTable(); // tabla de vehiculos
		addRoadsTable(); // tabla de carreteras
		addJunctionsTable(); // tabla de cruces
		addMap(); // mapa de carreteras
		addStatusBar(); // barra de estado
	}
	
	private void initPanels(){
		contentPanel1 = new JPanel();
		contentPanel1.setLayout(new BoxLayout(contentPanel1, BoxLayout.Y_AXIS));
		mainPanel.add(contentPanel1, BorderLayout.CENTER);
		
		contentPanel2 = new JPanel();
		contentPanel2.setLayout(new BoxLayout(contentPanel2, BoxLayout.X_AXIS));
		contentPanel3 = new JPanel();
		
		contentPanel3.setLayout(new BoxLayout(contentPanel3, BoxLayout.X_AXIS));
		contentPanel4 = new JPanel();
		contentPanel4.setLayout(new BoxLayout(contentPanel4, BoxLayout.Y_AXIS));
		contentPanel5 = new JPanel(new BorderLayout());
		
		contentPanel1.add(contentPanel2);
		contentPanel1.add(contentPanel3);
		contentPanel3.add(contentPanel4);
		contentPanel3.add(contentPanel5);
	}
	
	private void initGUI() {
		// TODO
		
		mainPanel = new JPanel(new BorderLayout());
		this.setContentPane(mainPanel);
		
		initPanels();
		
		fc = new JFileChooser();
	
		addComponentsToLayout();
		
		// Añade configuraciones de la ventana principal
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		setSize(1000, 1000);
		setVisible(true);
	}
	
	private void addToolBar(){
		//TODO
		toolBar = new JToolBar("Barra de herramientas");
		
		
	}
	
	@Override
	public void update(UpdateEvent ue, String error) {
		// TODO Auto-generated method stub

	}

}
