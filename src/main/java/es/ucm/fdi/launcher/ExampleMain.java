package es.ucm.fdi.launcher;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import es.ucm.fdi.control.Controller;
import es.ucm.fdi.ini.Ini;
import es.ucm.fdi.view.SimWindow;


public class ExampleMain {

	/**
	 * Default time limit if none indicated by user.
	 */
	private final static Integer _TIMELIMIT_DEFAULT = 10;

	/**
	 * Default execution mode if none indicated by user.
	 */
	private final static String _MODE_DEFAULT = "batch";
	
	/**
	 * Execution time limit: number of ticks the simulator will do.
	 */
	private static Integer _timeLimit = null;

	/**
	 * <code>String</code> with the input file pathname.
	 */
	private static String _inFile = null;

	/**
	 * <code>String</code> with the output file pathname.
	 */
	private static String _outFile = null;

	/**
	 * Mode of execution: 'batch' or 'gui'.
	 */
	private static String _mode = null;

	/**
	 * Parses introduced <code>args</code>. If error found, a <code>ParseException</code>
	 * is caught and program exits with <code>1</code>.
	 * 
	 * @param args arguments of command line
	 */
	private static void parseArgs(String[] args) {

		// define the valid command line options
		//
		Options cmdLineOptions = buildOptions();

		// parse the command line as provided in args
		//
		CommandLineParser parser = new DefaultParser();
		try {
			CommandLine line = parser.parse(cmdLineOptions, args);
			parseModeOption(line);
			parseHelpOption(line, cmdLineOptions);
			parseInFileOption(line);
			parseOutFileOption(line);
			parseStepsOption(line);

			// if there are some remaining arguments, then something wrong is
			// provided in the command line!
			//
			String[] remaining = line.getArgs();
			if (remaining.length > 0) {
				String error = "Illegal arguments:";
				for (String o : remaining)
					error += (" " + o);
				throw new ParseException(error);
			}

		} catch (ParseException e) {
			// new Piece(...) might throw GameError exception
			System.err.println(e.getLocalizedMessage());
			System.exit(1);
		}
	}

	/**
	 * Generates and returns a collection of possible <code>Options</code> to be
	 * used in a <code>CommandLine</code>.
	 * 
	 * @return collection of <code>Options</code>
	 */
	private static Options buildOptions() {
		// Colección
		Options cmdLineOptions = new Options();

		// Comando de ayuda: -h; --help; "Print this message"
		cmdLineOptions.addOption(
			Option.builder("h")
			.longOpt("help")
			.desc("Print this message")
			.build()
		);
		
		// Comando de input: -i; --input; <arg.ini>; "Events input file"
		cmdLineOptions.addOption(
			Option.builder("i")
			.longOpt("input")
			.hasArg()
			.desc("Events input file")
			.build()
		);

		// Comando de modo: -m, --mode; <arg>; "'batch' for batch mode and 'gui' for GUI mode"
		cmdLineOptions.addOption(
			Option.builder("m")
			.longOpt("mode")
			.hasArg()
			.desc("'batch' for batch mode and 'gui' for GUI mode (default value is 'batch')")
			.build()
		);

		// Comando de salida: -o; --output; <arg.ini>; "Output file, where reports are written"
		cmdLineOptions.addOption(
			Option.builder("o")
			.longOpt("output")
			.hasArg()
			.desc("Output file, where reports are written.")
			.build()
		);
		
		// Comando de ticks: -t; --ticks; <x>; "Ticks to execute the simulator's main loop..."
		cmdLineOptions.addOption(
			Option.builder("t")
			.longOpt("ticks")
			.hasArg()
			.desc("Ticks to execute the simulator's main loop (default value is " + _TIMELIMIT_DEFAULT + ").")
			.build()
		);

		return cmdLineOptions;
	}

	/**
	 * If indicated in the command line, help is shown in console with the help message
	 * of all possible options available to use in a command line.
	 * 
	 * @param line <code>CommandLine</code> introduced.
	 * @param cmdLineOptions collection of <code>Options</code> available.
	 */
	private static void parseHelpOption(CommandLine line, Options cmdLineOptions) {
		if ( line.hasOption("h") ) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp(ExampleMain.class.getCanonicalName(), cmdLineOptions, true);
			
			System.exit(0);
		}
	}

	/**
	 * Modifies the input file name attribute: <code>_outFile</code> with the one indicated
	 * in the command line, after parsing it.
	 * 
	 * @param line <code>CommandLine</code> introduced.
	 * @throws ParseException if not a valid input file name.
	 */
	private static void parseInFileOption(CommandLine line) throws ParseException {
		_inFile = line.getOptionValue("i");
		if (_inFile == null) {
			if(!_mode.equals("gui")){
				throw new ParseException("An events file is missing");
			}
		}
	}

	/**
	 * Modifies the input file name attribute: <code>_outFile</code> with the one indicated
	 * in the command line, after parsing it.
	 * 
	 * @param line <code>CommandLine</code> introduced.
	 * @throws ParseException if not a valid input file name.
	 */
	private static void parseModeOption(CommandLine line) throws ParseException {
		_mode = line.getOptionValue("m");
		
		if (_mode == null) {
			_mode = _MODE_DEFAULT;
		}

		if (!_mode.equals("batch") && !_mode.equals("gui")) {
			throw new ParseException("Not a valid execution mode.");
		}
	}

	/**
	 * Modifies the output file name attribute: <code>_outFile</code> with the one indicated
	 * in the command line, after partsing it.
	 * 
	 * @param line <code>CommandLine</code> introduced.
	 * @throws ParseException if not a valid output file name.
	 */
	private static void parseOutFileOption(CommandLine line) throws ParseException {
		_outFile = line.getOptionValue("o");
	}

	/**
	 * <p>
	 * Updates the number of steps indicated by the command line and stored in attribute
	 * <code>_timeLimit</code>.
	 * </p> <p>
	 * If no value is indicated, automatically set up to <code>_TIMELIMIT_DEFAULT</code>.
	 * </p>
	 * 
	 * @param line <code>CommandLine</code> introduced
	 * @throws ParseException if the time value is not valid
	 */
	private static void parseStepsOption(CommandLine line) throws ParseException {
		// Si no se ha introducido ningún valor, se toma por defecto.
		String t = line.getOptionValue("t", _TIMELIMIT_DEFAULT.toString());

		// Se comprueba que el valor introducido sea válido.
		try {
			_timeLimit = Integer.parseInt(t);
			assert (_timeLimit < 0);
		} catch (Exception e) {
			throw new ParseException("Invalid value for time limit: " + t);
		}
	}

	/**
	 * <p>
	 * Runs the simulator on all files that end with <code>.ini</code> in the given
	 * <code>path</code>, and compares that output to the expected output. 
	 * </p> <p>
	 * It assumed that for example <code>example.ini</code> the expected output is stored 
	 * in <code>example.ini.eout</code>.
	 * </p> <p>
	 * The simulator's output will be stored in <code>example.ini.out</code>.
	 * </p>
	 * 
	 * @param path <code>String</code> with the directory path
	 * @throws IOException if failure in reading/writing files.
	 */
	static void test(String path) throws IOException {
		// Directorio.
		File dir = new File(path);

		// Directorio ok?
		if ( ! dir.exists() ) {
			throw new FileNotFoundException(path);
		}
		
		// Array de archivos de prueba (filtrado por "acabados en .ini")
		File[] files = dir.listFiles( 
			new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					return name.endsWith(".ini");
				}
			}
		);

		// Prueba de todos los archivos del directorio.
		for (File file : files) {
			test(
				file.getAbsolutePath(), 
				file.getAbsolutePath() + ".out", 
				file.getAbsolutePath() + ".eout",
				_TIMELIMIT_DEFAULT
			);
		}
	}

	/**
	 * Runs the simulator on a file <code>inFile</code>, writes the simulation report in <code>outFile</code>, and
	 * compares the result with the expected report stored in the file <code>expectedOutFile</code>. 
	 * 
	 * @param inFile <code>String</code> with the input file abstract pathname
	 * @param outFile <code>String</code> with the output file abstract pathname
	 * @param expectedOutFile <code>String</code> with the expected output file abstract pathname
	 * @param timeLimit execution time limit
	 * @throws IOException if failure in reading/writing of files
	 */
	private static void test(String inFile, String outFile, String expectedOutFile, int timeLimit) throws IOException {
		_inFile = inFile;
		_outFile = outFile;		
		_timeLimit = timeLimit;

		// Ejecución en batch.
		try {
			startBatchMode();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Comprobación del resultado.
		boolean equalOutput = ( new Ini(_outFile) ).equals( new Ini(expectedOutFile) );
		
		// Muestra por consola.
		System.out.println(
			"Result for: '" + _inFile + "' : " + 
			( equalOutput ? ("OK!") : ("not equal to expected output +'" + expectedOutFile + "'") )
 		);
	}

	/**
	 * Run the <code>Simulator</code> in <code>batch</code> mode.
	 * 
	 * @throws IOException if failure in reading/writing files.
	 */
	private static void startBatchMode() throws Exception {		
		// Argumentos
		Ini iniInput = new Ini(_inFile);
		File outFile = new File(_outFile);
		OutputStream os = new FileOutputStream(outFile);

		// Controlador
		Controller control = new Controller(iniInput, os, _timeLimit);

		// Ejecución y captura de excepciones
		try {
			control.executeBatch();
		}
		catch (Exception e) {
			throw e;
		}
	}

	/**
	 * Run the <code>Simulator</code> in <code>GUI</code> mode.
	 * @throws Exception 
	 */
	private static void startGUIMode() throws Exception{
		// Argumentos
		Ini iniInput = null;
		if(_inFile != null){
			iniInput = new Ini(_inFile);
		}

		// Controlador de salida nula
		Controller control = new Controller(iniInput, null, _timeLimit);

		// Interfaz gráfica
		try {
			SwingUtilities.invokeAndWait(
				new Runnable() {
					public void run() {
						new SimWindow(control, _inFile);
					}
				}
			);
		}
		catch (Exception e) {
			throw e;
		}
		
	}

	/**
	 * Runs the <code>Simulator</code> in <code>command line</code> mode.
	 * 
	 * @param args simulation arguments
	 */
	private static void start(String[] args) {
		try	{
			parseArgs(args);
			switch (_mode) {
			case "batch" : 
				startBatchMode();
				break;
			case "gui":
				startGUIMode();
				break;
			}
		}
		catch(Exception e){
			e.printStackTrace();
			System.err.println("Aborting execution...");
		}

		
	}

	public static void main(String[] args) throws IOException, InvocationTargetException, InterruptedException {

		/*
		* Command lines, examples:
		*
		* -i resources/examples/events/basic/ex1.ini
		* -i resources/examples/events/basic/ex1.ini -o ex1.out
		* -i resources/examples/events/basic/ex1.ini -t 20
		* -i resources/examples/events/basic/ex1.ini -o ex1.out -t 20
		* --help
		*/
		
		start(args);
	}
}
