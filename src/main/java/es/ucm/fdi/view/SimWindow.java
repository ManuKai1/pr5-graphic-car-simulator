package es.ucm.fdi.view;

import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SpinnerNumberModel;

import es.ucm.fdi.control.Controller;
import es.ucm.fdi.model.SimObj.Junction;
import es.ucm.fdi.model.SimObj.Road;
import es.ucm.fdi.model.SimObj.Vehicle;
import es.ucm.fdi.model.events.Event;
import es.ucm.fdi.model.simulation.SimulationException;
import es.ucm.fdi.model.simulation.TrafficSimulation.Listener;
import es.ucm.fdi.model.simulation.TrafficSimulation.UpdateEvent;
import es.ucm.fdi.util.MultiTreeMap;
import es.ucm.fdi.util.TableDataType;

public class SimWindow extends JFrame implements Listener {
	
	//Para la ventana
	private final int DEF_HEIGHT = 1000, DEF_WIDTH = 1000;
	
	//Para el spinner
	private final int INITIAL_STEPS = 1;
	private final int MIN_TIME = 1;
	private final int MAX_TIME = 500;

	// Para las tablas.
	private final TableDataType[] eventDataHeaders = {
			TableDataType.E_NUM,
			TableDataType.E_TIME,
			TableDataType.E_TYPE
	};

	private final TableDataType[] junctionDataHeaders = {
			TableDataType.ID,
			TableDataType.J_TYPE,
			TableDataType.J_GREEN,
			TableDataType.J_RED,
	};

	private final TableDataType[] roadDataHeaders = {
			TableDataType.ID,
			TableDataType.R_TYPE,
			TableDataType.R_SOURCE,
			TableDataType.R_TARGET,
			TableDataType.R_LENGHT,
			TableDataType.R_MAX,
			TableDataType.R_STATE,
	};

	private final TableDataType[] vehicleDataHeaders = {
			TableDataType.ID,
			TableDataType.V_TYPE,
			TableDataType.V_ROAD,
			TableDataType.V_LOCATION,
			TableDataType.V_SPEED,
			TableDataType.V_KM,
			TableDataType.V_FAULTY,
			TableDataType.V_ROUTE
	};
	
	//Faltaría separación de atributos
	private Controller control;
	private OutputStream reports;

	private JPanel mainPanel;
	private JPanel contentPanel1;
	private JPanel contentPanel2;
	private JPanel contentPanel3;
	private JPanel contentPanel4;
	private JPanel contentPanel5;
	
	private JMenuBar menuBar = new JMenuBar();
	private JMenu fileMenu = new JMenu("File");
	private JMenu simulatorMenu = new JMenu("Simulator");
	private JMenu reportsMenu = new JMenu("Reports");
	
	private JToolBar toolBar = new JToolBar();
	
	private JFileChooser fileChooser = new JFileChooser();;
	private File currentFile;
	
	private JSpinner stepsSpinner = new JSpinner();
	
	private JTextField timeViewer = new JTextField("" + 0);
	
	private JTextArea eventsTextArea;
	private JTextArea reportsTextArea;
	
	//Tablas
	private SimTable eventsTable;
	private SimTable junctionsTable;	
	private SimTable roadsTable;
	private SimTable vehiclesTable;
	
	//Creaciones de acciones
	//Recordar activarlas y desactivarlas
	private SimulatorAction load =
			new SimulatorAction("Load Events", "open.png", 
					"Load an events file",
					KeyEvent.VK_L, "Control + Shift + L", 
					() -> loadFile());
	
	private SimulatorAction save =
			new SimulatorAction("Save Events", "save.png", 
					"Save an events file",
					KeyEvent.VK_S, "Control + Shift + S", 
					() -> saveFile(eventsTextArea));
	
	private SimulatorAction clear = 
			new SimulatorAction("Clear Events", "clear.png",
					"Clear event zone",
					KeyEvent.VK_C, "Control + Shift + C", 
					() -> clearEvents());
	
	private SimulatorAction insertEvents = 
			new SimulatorAction("Insert Events", "events.png",
					"Add events to simulation",
					KeyEvent.VK_E, "Control + Shift + E", 
					() -> eventsToSim());
	
	private SimulatorAction run =
			new SimulatorAction("Run", "play.png", 
					"Run the simulator",
					KeyEvent.VK_P, "Control + Shift + P", 
					() -> runSimulator());

	private SimulatorAction reset =
			new SimulatorAction("Reset", "reset.png",
					"Reset the simulator",
					KeyEvent.VK_R, "Control + Shift + R", 
					() -> resetSimulator());
	
	private SimulatorAction generateRep =
			new SimulatorAction("Generate Reports", "report.png",
					"Report generator",
					KeyEvent.VK_G, "Control + Shift + G", 
					() -> generateReports());
	
	private SimulatorAction clearRep =
			new SimulatorAction("Clear Reports", "delete_report.png",
					"Clears reports",
					KeyEvent.VK_D, "Control + Shift + D", 
					() -> clearReports());
	
	private SimulatorAction saveRep =
			new SimulatorAction("Save Reports", "save_report.png",
					"Save reports to file",
					KeyEvent.VK_F, "Control + Shift + F", 
					() -> saveFile(reportsTextArea));
	
	private SimulatorAction exit =
			new SimulatorAction("Exit", "exit.png",
					"Exit the simulator",
					KeyEvent.VK_ESCAPE, "Control + Shift + ESC", 
					() -> quit());
	
	//Opcional
	//private ReportDialog reportDialog;
	
	public SimWindow(Controller ctrl, String inFileName) {
		super("Traffic Simulator");
		control = ctrl;
		currentFile = inFileName != null ? new File(inFileName) : null;
		//control.setOutStream(reports);
		initGUI();
		control.getSimulator().addSimulatorListener(this);
	}
	
	private Object clearReports() {
		// TODO Auto-generated method stub
		return null;
	}

	private Object generateReports() {
		// TODO Auto-generated method stub
		return null;
	}

	private Object runSimulator() {
		// TODO Auto-generated method stub
		return null;
	}

	private Object eventsToSim() {
		// TODO Auto-generated method stub
		return null;
	}

	private Object clearEvents() {
		// TODO Auto-generated method stub
		return null;
	}

	private void addComponentsToLayout(){
		addMenuBar(); // barra de menus
		//addToolBar(); // barra de herramientas
		// addEventsEditor(); // editor de eventos
		addEventsView(); // cola de eventos
		// addReportsArea(); // zona de informes
		addJunctionsTable(); // tabla de cruces
		addRoadsTable(); // tabla de carreteras
		addVehiclesTable(); // tabla de vehiculos
		// addMap(); // mapa de carreteras
		// addStatusBar(); // barra de estado
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
	
		addComponentsToLayout();
		
		// Añade configuraciones de la ventana principal
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(DEF_WIDTH, DEF_HEIGHT);
		setVisible(true);
	}

	/**
	 * Función que crea 
	 * la barra de menú.
	 */
	private void addMenuBar() {
		fileMenu.add(load);
		fileMenu.add(save);
		fileMenu.addSeparator();
		fileMenu.add(saveRep);
		fileMenu.addSeparator();
		fileMenu.add(exit);
		
		simulatorMenu.add(run);
		simulatorMenu.add(reset);
		//simulatorMenu.add(redirectOutput);
		
		reportsMenu.add(generateRep);
		reportsMenu.add(clearRep);
		
		menuBar.add(fileMenu);
		menuBar.add(simulatorMenu);
		menuBar.add(reportsMenu);
		
		setJMenuBar(menuBar);
	}
	/**
	 * Función que crea
	 * la barra de herramientas.
	 */
	private void addToolBar(){
		toolBar.addSeparator();
		
		toolBar.add(load);
		toolBar.add(save);
		toolBar.add(clear);
		
		toolBar.addSeparator();
		
		toolBar.add(insertEvents);
		toolBar.add(run);
		toolBar.add(reset);
		
		toolBar.addSeparator();
		
		toolBar.add(new JLabel("  Steps:  "));
		stepsSpinner.setModel(new SpinnerNumberModel(INITIAL_STEPS, MIN_TIME, MAX_TIME, 1));
		toolBar.add(stepsSpinner);
		toolBar.add(new JLabel("  Current Time:  "));
		timeViewer.setEditable(false);
		toolBar.add(timeViewer);
		
		toolBar.addSeparator();
		
		toolBar.add(generateRep);
		toolBar.add(clearRep);
		toolBar.add(saveRep);
		
		toolBar.addSeparator();
		
		toolBar.add(exit);
		
		add(toolBar, BorderLayout.PAGE_START);
	}

	private void addEventsView() {
		MultiTreeMap<Integer, Event> events = control.getSimulator().getEvents();

		eventsTable = new SimTable(eventDataHeaders, events.valuesList());
	}

	private void addJunctionsTable() {
		List<Junction> junctions = new ArrayList<>(
			control.getSimulator().getRoadMap().getJunctions().values()
		);
		
		junctionsTable = new SimTable(junctionDataHeaders, junctions);
	}

	private void addRoadsTable() {
		List<Road> roads = new ArrayList<>(
			control.getSimulator().getRoadMap().getRoads().values()
		);

		roadsTable = new SimTable(roadDataHeaders, roads);
	}

	private void addVehiclesTable() {
		List<Vehicle> vehicles = new ArrayList<>(
			control.getSimulator().getRoadMap().getVehicles().values()
		);

		vehiclesTable = new SimTable(vehicleDataHeaders, vehicles);
	}

	private void loadFile() {
		//TODO
		
		// Abre la ventana del selector y espera la respuesta
		// del usuario.
		int returnValue = fileChooser.showOpenDialog(null);

		if (returnValue == JFileChooser.APPROVE_OPTION) {
			File selectedFile = fileChooser.getSelectedFile();
			
		}

	}

	private void saveFile(JTextArea fromArea) { 
		//TODO
	}

	/**
	 * Método que guarda el área de texto editable de eventos
	 * de la simulación en la ruta indicada.
	 */
	private void saveEditedEventsTo(String path) 
			throws FileNotFoundException, IOException {
		
		// Creación del OutputStream
		File outFile = null;
		OutputStream os = null;
		try {
			outFile = new File(path);
			os = new FileOutputStream(outFile);

			StringBuilder edited = new StringBuilder();
			edited.append("!!getEdited()");

			os.write(edited.toString().getBytes());	
		}
		catch (FileNotFoundException e) {
			throw e;
		}
		catch (IOException e) {
			throw e;
		}
		finally {
			os.close();
		}		
	}

	/**
	 * OPCIONAL
	 * Método que guarda el área de texto donde se muestran los
	 * reportes de la simulación en la ruta indicada.
	 */
	private void saveReportsAreaTo(String path) 
			throws FileNotFoundException, IOException {
		
		// Creación del OutputStream
		File outFile = null;
		OutputStream os = null;
		try {
			outFile = new File(path);
			os = new FileOutputStream(outFile);

			StringBuilder edited = new StringBuilder();
			edited.append("!!getEdited()");

			os.write(edited.toString().getBytes());
		} 
		catch (FileNotFoundException e) {
			throw e;
		} 
		catch (IOException e) {
			throw e;
		}
		finally {
			os.close();
		}
	}

	/**
	 * Método que pregunta en un cuadro de diálogo al
	 * usuario si desea salir, terminando el programa si
	 * lo confirma.
	 */
	private void quit() {
		int n = JOptionPane.showOptionDialog(
			new JFrame(),
			"Are you sure you want to quit?",
			"Quit",
			JOptionPane.YES_NO_OPTION,
			JOptionPane.QUESTION_MESSAGE,
			null, null, null
		);

		if (n == JOptionPane.YES_OPTION) {
			System.exit(0);
		}
	}

	private void runSimulation(int time) 
			throws SimulationException, IOException {
		try {
			control.simulate(time);
		}
		catch (SimulationException e) {
			throw e;
		}
		catch (IOException e) {
			throw e;
		}
		
	}
	
	private void resetSimulator() {
		//TODO
	}
	
	@Override
	public void update(UpdateEvent ue, String error) {
		// TODO Auto-generated method stub

	}

}
