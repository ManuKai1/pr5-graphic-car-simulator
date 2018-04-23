package es.ucm.fdi.view;

import java.io.File;
import java.io.OutputStream;

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
	private JPanel contentPanel_1;
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
	private JTable eventsTable;
	private JTable vehiclesTable;
	private JTable roadsTable;
	private JTable junctionsTable;
	
	//Opcional
	private ReportDialog reportDialog;
	
	public SimWindow(TrafficSimulation simulator, Controller ctrl, String inFileName) {
		super("Traffic Simulator");
		control = ctrl;
		currentFile = inFileName != null ? new File(inFileName) : null;
		reports = new JTextAreaOutputStream(reportTextArea,null);
		ctrl.setOutputStream(reports);
		initGUI();
		simulator.addSimulatorListener(this);
	}
	
	private void initGUI() {
		// TODO
	}

	@Override
	public void update(UpdateEvent ue, String error) {
		// TODO Auto-generated method stub

	}

}
