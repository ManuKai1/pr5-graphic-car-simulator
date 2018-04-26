package es.ucm.fdi.view;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;

import org.apache.commons.cli.ParseException;

import es.ucm.fdi.control.Controller;
import es.ucm.fdi.ini.Ini;
import es.ucm.fdi.model.simulation.SimulationException;
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
		// addEventsEditor(); // editor de eventos
		// addEventsView(); // cola de eventos
		// addReportsArea(); // zona de informes
		// addVehiclesTable(); // tabla de vehiculos
		// addRoadsTable(); // tabla de carreteras
		// addJunctionsTable(); // tabla de cruces
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
		
		fc = new JFileChooser();
	
		addComponentsToLayout();
		
		// Añade configuraciones de la ventana principal
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(1000, 1000);
		setVisible(true);
	}

	private void addMenuBar() {
		JMenuBar menu = new JMenuBar();

		JMenu file = getFileMenu();
		file.setMnemonic(KeyEvent.VK_F);
		menu.add(file);

		JMenu sim = getSimulatorMenu();
		sim.setMnemonic(KeyEvent.VK_S);
		menu.add(sim);





		JMenu report = new JMenu("Reports");
	}

	private JMenu getFileMenu() {
		JMenu file = new JMenu("File");

		// Cargar archivo de eventos.
		JMenuItem loadEv = getLoadEventsMenuItem();
		file.add(loadEv);

		// Guardar archivo de eventos.
		JMenuItem saveEv = getSaveEventsMenuItem();
		saveEv.add(saveEv);

		file.addSeparator();

		// Guardar archivo de informes.
		JMenuItem saveRep = getSaveReportMenuItem();
		file.add(saveRep);

		file.addSeparator();

		// Salir.
		JMenuItem exit = getExitMenuItem();
		file.add(exit);		


		return file;
	}

	private JMenuItem getLoadEventsMenuItem() {
		JMenuItem loadEv = new JMenuItem("Load Events");
		loadEv.setMnemonic(KeyEvent.VK_L);

		loadEv.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// ACCIÓN: Cargar un fichero de eventos.
				Ini loadFile = null;

				//
				try {
					loadFile = new Ini(getLoadFile());
				} catch (IOException exc) {
					JOptionPane.showMessageDialog(new JFrame(), // ?
						"Could not recognise the selected file as "+ 
						"a valid simulation events file. Simulation " + 
						"will clear.",
						"Error de lectura", JOptionPane.WARNING_MESSAGE
					);

					clearSimulation();			
				}

				if (loadFile != null) {
					// Se cambia el archivo de entrada del controlador.
					control.setIniInput(loadFile);

					// Se reinicia la simulación.
					try {
						control.reset();
					} catch (ParseException exc) {
						JOptionPane.showMessageDialog(new JFrame(),
							"There was an error during events parsing. " + 
							"Simulation will clear.",
							"Error de parsing", 
							JOptionPane.WARNING_MESSAGE
						);

						clearSimulation();
					}
				}

				// Si loadFile == null, no se ha elegido ningún
				// archivo -> No se hace nada.				
			}
		});

		return loadEv;
	}

	/**
	 * Método que devuelve la ruta del archivo elegido
	 * para ser abierto en el simulador. Si el usuario no
	 * elige ningún archivo, se devuelve <code>null</code>.
	 */
	private String getLoadFile() {
		JFileChooser fileLoader = new JFileChooser();

		// Abre la ventana del selector y espera la respuesta
		// del usuario.
		int returnValue = fileLoader.showOpenDialog(null);

		if (returnValue == JFileChooser.APPROVE_OPTION) {
			File selectedFile = fileLoader.getSelectedFile();
			
			return selectedFile.getAbsolutePath();
		}

		return null;
	}

	private JMenuItem getSaveEventsMenuItem() {
		JMenuItem saveEv = new JMenuItem("Save Events");
		saveEv.setMnemonic(KeyEvent.VK_S);

		saveEv.addActionListener(
			new ActionListener() {
			
				@Override
				public void actionPerformed(ActionEvent e) {
					// ACCIÓN: Guardar un fichero de eventos.
					String savePath = getSaveDirectory();

					try {
						saveEditedEventsTo(savePath);
					}
					catch (FileNotFoundException exc) {
						JOptionPane.showMessageDialog(new JFrame(),
							"There was an error during file creation. " + 
							"File was not saved.",
							"Error de guardado", 
							JOptionPane.WARNING_MESSAGE
						);
					}
					catch (IOException exc) {
						JOptionPane.showMessageDialog(new JFrame(),
							"There was an error during writing data in save file. " + 
							"File was not saved.",
							"Error de guardado", 
							JOptionPane.WARNING_MESSAGE
						);
					}
				}
			}
		);


		return saveEv;

	}

	/**
	 * Método que devuelve la ruta de archivo donde quiere 
	 * guardar el fichero de eventos editado en el simulador. Si el 
	 * usuario no elige ningún directorio, se devuelve <code>null</code>.
	 */
	private String getSaveDirectory() { 
		JFileChooser fileSaver = new JFileChooser();

		fileSaver.setDialogTitle("Save: ");

		int returnValue = fileSaver.showSaveDialog(null);
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			if ( fileSaver.getSelectedFile().isDirectory() ) {
				return 	fileSaver.getSelectedFile().getAbsolutePath();
			}
		}

		return null;
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

	private JMenuItem getSaveReportMenuItem() {
		JMenuItem saveRep = new JMenuItem("Save Report");
		saveRep.setMnemonic(KeyEvent.VK_R);

		saveRep.addActionListener(
			new ActionListener() {
			
				@Override
				public void actionPerformed(ActionEvent e) {
					// ACCIÓN: Guardar un fichero de informes.
					String savePath = getSaveDirectory();

					try {
						saveReportsAreaTo(savePath);
					}
					catch (FileNotFoundException exc) {
						JOptionPane.showMessageDialog(new JFrame(),
							"There was an error during file creation. " + 
							"File was not saved.",
							"Error de guardado", 
							JOptionPane.WARNING_MESSAGE
						);
					}
					catch (IOException exc) {
						JOptionPane.showMessageDialog(new JFrame(),
							"There was an error during writing data in save file. " + 
							"File was not saved.",
							"Error de guardado", 
							JOptionPane.WARNING_MESSAGE
						);
					}
				}
			}
		);

		return saveRep;
	}

	/**
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

	private JMenuItem getExitMenuItem() {
		JMenuItem exit = new JMenuItem("Exit");
		exit.setMnemonic(KeyEvent.VK_E);

		exit.addActionListener(
			new ActionListener() {
			
				@Override
				public void actionPerformed(ActionEvent e) {
					// ACCIÓN: Salir de la simulación, preguntando antes
					quit();
				}
			}
		);

		return exit;
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

		if (n == 0) {
			System.exit(0);
		}
	}




	private JMenu getSimulatorMenu() {
		JMenu sim = new JMenu("Simulator");

		JMenuItem run = getRunMenuItem();
		sim.add(run);

		// JMenuItem reset = getResetMenuItem();
		// sim.add(reset);

		// JMenuItem redirect = getRedirectMenuItem();
		// sim.add(redirect);

		return sim;
	}
	
	private JMenuItem getRunMenuItem() {
		JMenuItem run = new JMenuItem("Run");
		run.setMnemonic(KeyEvent.VK_R);
		
		run.addActionListener(
			new ActionListener() {
			
				@Override
				public void actionPerformed(ActionEvent e) {
					// ACCIÓN: Ejecuta el simulador las unidades de
					// tiempo indicadas por el usurio.

					int inputTime;
					try {
						inputTime = getUserInputTime();
					}
					catch (IllegalArgumentException exc) {
						JOptionPane.showMessageDialog(
							new JFrame(),
							"Not a valid input. " + exc,
							"Error de entrada", 
							JOptionPane.WARNING_MESSAGE
						);

						inputTime = -1;
					}

					if (inputTime != -1) {
						try {
							runSimulation(inputTime);
						}
						catch (SimulationException exc) {
							JOptionPane.showMessageDialog(
								new JFrame(),
								"Simulator error ocurred:\n" + exc +
								"\n\n" + "Simulation will reset",
								"Error de simulación", 
								JOptionPane.WARNING_MESSAGE
							);

							resetSimulation();
						}
						catch (IOException exc) {
							// Error al guardar los informes, pero no
							// deberían guardarse si no quiere el usuario.
							// Crear método de sólo simular en TrafficSim
							// y luego modificar el metodo simulate() de 
							// Controller
						}
					}
				}
			}
		);
		
		return run;
	}

	private int getUserInputTime() {
		// Recibe una input de un cuadro de diálogo.
		String userInput = (String) 
			JOptionPane.showInputDialog(
				new JFrame(),
				"Indica el número de ciclos: ",
				"Ciclos de ejecución",
				JOptionPane.PLAIN_MESSAGE,
				null, null, control.getBatchTimeLimit()
			);

		int inputTime;
		try {
			inputTime = Integer.parseInt(userInput);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException(
				"Int reading failure"
			);
		}

		// Comprobamos que el valor sea positivo
		if (inputTime <= 0) {
			throw new IllegalArgumentException(
				"Non-positive int failure"
			);
		}

		return inputTime;
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
	

	private void clearSimulation() {
		// To be implemented
	}

	private void resetSimulation() {
		// To be implemented
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
