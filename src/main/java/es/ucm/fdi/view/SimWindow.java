package es.ucm.fdi.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.apache.commons.cli.ParseException;

import es.ucm.fdi.control.Controller;
import es.ucm.fdi.model.SimObj.Junction;
import es.ucm.fdi.model.SimObj.Road;
import es.ucm.fdi.model.SimObj.Vehicle;
import es.ucm.fdi.model.events.Event;
import es.ucm.fdi.model.simulation.RoadMap;
import es.ucm.fdi.model.simulation.SimulationException;
import es.ucm.fdi.model.simulation.TrafficSimulation.Listener;
import es.ucm.fdi.model.simulation.TrafficSimulation.UpdateEvent;
import es.ucm.fdi.util.MultiTreeMap;
import es.ucm.fdi.util.TableDataType;

public class SimWindow extends JFrame implements Listener {
	
	//Para la ventana
	private final int DEF_HEIGHT = 1000, DEF_WIDTH = 1000;
	
	//Para los Split Pane
	private final double VERTICAL_SPLIT = 0.3, HORIZONTAL_SPLIT = 0.5;
	
	//Para el spinner
	private final int INITIAL_STEPS = 1;
	private final int MIN_TIME = 1;
	private final int MAX_TIME = 500;
	
	//Para las áreas de texto
	private final String EVENTS_TITLE = "Events";
	private final String REPORTS_TITLE = "Reports";

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
	
	private Controller control;
	private OutputStream reports;

	private JPanel eventsAndReports = new JPanel( new GridLayout(1, 3));
	private JPanel tablesPanel = new JPanel( new GridLayout(3, 1));
	private JPanel graphPanel = new JPanel();
	
	private JSplitPane tablesAndGraph = new JSplitPane(
			JSplitPane.HORIZONTAL_SPLIT, tablesPanel, graphPanel);
	
	private JSplitPane lowAndTop = new JSplitPane(
			JSplitPane.VERTICAL_SPLIT, eventsAndReports, tablesAndGraph);
	
	private JMenuBar menuBar = new JMenuBar();
	private JMenu fileMenu = new JMenu("File");
	private JMenu simulatorMenu = new JMenu("Simulator");
	private JMenu reportsMenu = new JMenu("Reports");
	
	private JToolBar toolBar = new JToolBar();
	
	private JFileChooser fileChooser = new JFileChooser();;
	private File currentFile;
	
	private JSpinner stepsSpinner = new JSpinner();
	
	private JTextField timeViewer = new JTextField("" + 0, 5);
	
	private JTextArea eventsTextArea = new JTextArea();
	private JTextArea reportsTextArea = new JTextArea();
	
	// Tablas
	private SimTable eventsTable;
	private SimTable junctionsTable;	
	private SimTable roadsTable;
	private SimTable vehiclesTable;

	// Grafo
	private SimGraph simGraph;
	
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
	
	public SimWindow(Controller ctrl, String inFileName) {
		super("Traffic Simulator");
		control = ctrl;
		currentFile = inFileName != null ? new File(inFileName) : null;
		//control.setOutStream(reports);
		initGUI();
		control.getSimulator().addSimulatorListener(this);
	}
	
	private void clearReports() {
		// TODO Auto-generated method stub
	}

	private void generateReports() {
		// TODO Auto-generated method stub
	}

	private void runSimulator() {
		//De momento, se ejecuta con outstream null.
		try {
			control.getSimulator().execute((int) stepsSpinner.getValue(),
					null);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this,
					e.getMessage());
		} catch (SimulationException e) {
			JOptionPane.showMessageDialog(this,
					e.getMessage());
		}
	}

	private void eventsToSim() {
		try {
			control.setIniInput(new ByteArrayInputStream(eventsTextArea.getText().
					getBytes()));
			control.pushEvents();
			run.setEnabled(true);
		} catch (IllegalArgumentException e) {
			JOptionPane.showMessageDialog(this,
					e.getMessage());
		} catch (ParseException e) {
			JOptionPane.showMessageDialog(this,
					e.getMessage());
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this,
					e.getMessage());
		}
	}

	private void clearEvents() {
		eventsTextArea.setText("");
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
		// addStatusBar(); // barra de estado
	}
	
	
	private void initPanels(){
		tablesAndGraph.setResizeWeight(.5);
		lowAndTop.setResizeWeight(.5);
		add(lowAndTop, BorderLayout.CENTER);
	}
	
	private void initGUI() {
		// TODO
		
		initPanels();
	
		addComponentsToLayout();
		
		// Añade configuraciones de la ventana principal
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(DEF_WIDTH, DEF_HEIGHT);
		setVisible(true);
		tablesAndGraph.setDividerLocation(HORIZONTAL_SPLIT);
		lowAndTop.setDividerLocation(VERTICAL_SPLIT);
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
	 * Además, deshabilita algunas
	 * acciones al comienzo.
	 */
	private void addToolBar(){
		toolBar.addSeparator();
		
		toolBar.add(load);
		toolBar.add(save);
		toolBar.add(clear);
		save.setEnabled(false);
		clear.setEnabled(false);
		
		toolBar.addSeparator();
		
		toolBar.add(insertEvents);
		toolBar.add(run);
		toolBar.add(reset);
		insertEvents.setEnabled(false);
		run.setEnabled(false);
		reset.setEnabled(false);
		
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
		generateRep.setEnabled(false);
		clearRep.setEnabled(false);
		saveRep.setEnabled(false);
		
		toolBar.addSeparator();
		
		toolBar.add(exit);
		
		add(toolBar, BorderLayout.PAGE_START);
	}
	


	private void addEventsEditor(){
		eventsTextArea.setEditable(true);
		eventsTextArea.setLineWrap(true);
		Border lineBorder = BorderFactory.createLineBorder(Color.BLACK);
		Border borderWithTitle = BorderFactory.createTitledBorder(lineBorder, EVENTS_TITLE);
		eventsTextArea.setFont(new Font("Verdana", Font.PLAIN, 12));
		eventsTextArea.setBorder(borderWithTitle);
		//Se activan y desactivan los botones según
		//esté vacío el área de texto.
		eventsTextArea.getDocument().addDocumentListener(
				new DocumentListener(){
			
					public void changedUpdate(DocumentEvent e) {
		                if (eventsTextArea.getText().isEmpty()) {
		                    disableEventButtons();
		                } else {
		                    enableEventButtons();
		                }
		            }

					@Override
					public void insertUpdate(DocumentEvent arg0) {
						if (eventsTextArea.getText().isEmpty()) {
		                    disableEventButtons();
		                } else {
		                    enableEventButtons();
		                }
					}

					@Override
					public void removeUpdate(DocumentEvent arg0) {
						if (eventsTextArea.getText().isEmpty()) {
		                    disableEventButtons();
		                } else {
		                    enableEventButtons();
		                }
					}
		});
		
		eventsAndReports.add(new JScrollPane(eventsTextArea,
        		JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));
	}

	
	
	private void addEventsView() {
		MultiTreeMap<Integer, Event> events = control.getSimulator().getEvents();
		eventsTable = new SimTable(eventDataHeaders, events.valuesList());
		
		eventsAndReports.add(eventsTable);
	}
	
	
	
	private void addReportsArea(){
		reportsTextArea.setEditable(false);
		reportsTextArea.setLineWrap(true);
		Border lineBorder = BorderFactory.createLineBorder(Color.BLACK);
		Border borderWithTitle = BorderFactory.createTitledBorder(lineBorder, REPORTS_TITLE);
		reportsTextArea.setFont(new Font("Verdana", Font.PLAIN, 12));
		reportsTextArea.setBorder(borderWithTitle);
		eventsAndReports.add(new JScrollPane(reportsTextArea,
        		JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));
	}
	

	
	private void addJunctionsTable() {
		List<Junction> junctions = new ArrayList<>(
			control.getSimulator().getRoadMap().getJunctions().values()
		);
		
		junctionsTable = new SimTable(junctionDataHeaders, junctions);
		tablesPanel.add(junctionsTable);
	}
	

	
	private void addRoadsTable() {
		List<Road> roads = new ArrayList<>(
			control.getSimulator().getRoadMap().getRoads().values()
			);
			
		roadsTable = new SimTable(roadDataHeaders, roads);
		tablesPanel.add(roadsTable);
	}
	

	
	private void addVehiclesTable() {
		List<Vehicle> vehicles = new ArrayList<>(
			control.getSimulator().getRoadMap().getVehicles().values()
			);
			
		vehiclesTable = new SimTable(vehicleDataHeaders, vehicles);
		tablesPanel.add(vehiclesTable);
	}
	


	private void addMap() {	
		/*
		TEST

		RoadMap map = new RoadMap();
		Junction[] jArray = {
			new Junction("j1"),
			new Junction("j2"),
			new Junction("j3"),
			new Junction("j4")
		};

		Road[] rArray = {
			new Road("r1", 50, 50, jArray[0], jArray[1]),
			new Road("r2", 50, 50, jArray[1], jArray[2]),
			new Road("r3", 50, 50, jArray[2], jArray[3]),
		};

		for (Junction j : jArray) {
			map.addJunction(j);
		}
		for (Road r : rArray) {
			map.addRoad(r);
		}		
		*/

		RoadMap map = control.getSimulator().getRoadMap();

		simGraph = new SimGraph(map);
		graphPanel.add(simGraph);
	}
	
	private void loadFile(){
		// Abre la ventana del selector y espera la respuesta
		// del usuario.
		int returnValue = fileChooser.showOpenDialog(this);
		//Si fue un éxito
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			currentFile = fileChooser.getSelectedFile();
			try {
				//Lectura de fichero y paso de bytes a String
			    byte[] byteText = Files.readAllBytes(currentFile.toPath());
			    String text = new String(byteText);
			    eventsTextArea.setText(text);
			}
			catch(Exception e){
				JOptionPane.showMessageDialog(this,
						"Error while loading the file.");
			}
		}
	}
	
	private void saveFile(JTextArea fromArea) { 
		int returnValue = fileChooser.showSaveDialog(this);
		if (returnValue == JFileChooser.APPROVE_OPTION){
			// Creación del OutputStream
			File outFile = null;
			OutputStream os = null;
			try {
				outFile = fileChooser.getSelectedFile();
				os = new FileOutputStream(outFile);
					StringBuilder edited = new StringBuilder();
				edited.append(fromArea.getText());
					os.write(edited.toString().getBytes());	
				//Mensaje de éxito
				JOptionPane.showMessageDialog(this,
					"The file was saved.");
					
			} catch (Exception e) {
				JOptionPane.showMessageDialog(this,
						"Error while saving the file.");
			}
			finally {
				try {
					os.close();
				} catch (IOException e) {
					JOptionPane.showMessageDialog(this,
							"Error while saving the file.");
				}
			}
		}
	}

	private void enableEventButtons(){
	    save.setEnabled(true);
	    clear.setEnabled(true);
	    insertEvents.setEnabled(true);
	}
	
	private void disableEventButtons(){
	    save.setEnabled(false);
	    clear.setEnabled(false);
	    insertEvents.setEnabled(false);
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
		switch(ue.getEvent()){
		case NEW_EVENT :
			List<Event> addedEvents = ue.getEventQueue().valuesList();
			eventsTable.setList(addedEvents);
			eventsTable.update();
			break;
		case ADVANCED :
			List<Junction> addedJunctions = 
				new ArrayList<Junction>(ue.getRoadMap().
						getJunctions().values());
			junctionsTable.setList(addedJunctions);
			junctionsTable.update();
			
			List<Vehicle> addedVehicles = 
				new ArrayList<Vehicle>(ue.getRoadMap().
						getVehicles().values());
			vehiclesTable.setList(addedVehicles);
			vehiclesTable.update();
			
			List<Road> addedRoads = 
				new ArrayList<Road>(ue.getRoadMap().
						getRoads().values());
			roadsTable.setList(addedRoads);
			roadsTable.update();
			
			simGraph.generateGraph();
		}
	}

}
