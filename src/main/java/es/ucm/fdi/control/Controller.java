package es.ucm.fdi.control;

import java.io.IOException;
import java.io.OutputStream;

import javax.xml.parsers.ParserConfigurationException;

import es.ucm.fdi.control.evbuild.EventParser;
import es.ucm.fdi.ini.Ini;
import es.ucm.fdi.ini.IniSection;
import es.ucm.fdi.model.events.Event;
import es.ucm.fdi.model.simulation.SimulationException;
import es.ucm.fdi.model.simulation.TrafficSimulation;

/**
 * <p>
 * Clase utilizada como controlador del programa.
 * </p> <p>
 * En su método {@link #execute()} crea un simulador
 * {@link TrafficSimulation} a partir de un archivo
 * <code>.ini</code> que almacena los {@link Event Events}.
 * </p> <p>
 * El simulador se actualiza durante un tiempo determinado
 * {@link #timeLimit}, volcando los resultados en un flujo
 * de salida {@link #outStream}.
 * </p>
 */
public class Controller {
    
    /**
     * Archivo <code>.ini</code> dividido en 
     * <code>IniSections</code> del que se extraen 
     * los <code>Events</code> de la simulación.
     */
    private Ini iniInput;

    /**
     * Flujo de salida donde se vuelcan los datos
     * del simulador tras cada actualización.
     */
    private OutputStream outStream;

    /**
     * Número de ticks que se ejecuta el simulador.
     */
    private int timeLimit;

    /**
     * Constructor de {@link Controller} que recibe 
     * el archivo <code>.ini</code>, el flujo de salida
     * y el tiempo límite de ejecución.
     * 
     * @param in    <code>Ini</code> con el archivo 
     *              <code>.ini</code>
     * @param out   <code>OutputStream</code> donde 
     *              se vuelcan los datos
     * @param time  tiempo límite de ejecución
     */
    public Controller(Ini in, OutputStream out, int time) {
        iniInput = in;
        outStream = out;
        timeLimit = time;
    }

    /**
     * <p>
     * Método de ejecución que:
     * </p> <p>
     * 1. Crea una <code>TrafficSimulation</code> y un 
     * <code>EventParser</code>.
     * </p> <p>
     * 2. Recorre las secciones de <code>iniInput</code> 
     * guardando los eventos en la simulación.
     * </p> <p>
     * 3. Ejecuta la <code>TrafficSimulation</code>.
     * </p> 
     *
     * @throws ParserConfigurationException     if event parsing failed (no matching
     *                                          event or invalid data)
     * @throws IllegalArgumentException         if event time is lower than
     *                                          sim time
     * @throws SimulationException              if an error ocurred during the execution
     *                                          of events in the simulation
     * @throws IOException                      if an error ocurred during report generation
     *                                          in the simulation
     */
    public void execute() throws ParserConfigurationException, IOException, SimulationException {
        TrafficSimulation simulator = new TrafficSimulation();
        EventParser parser = new EventParser();

        // 1 //
        // Recorre las secciones del archivo .ini de entrada
        // y construye y guarda los eventos en el simulador.
        for ( IniSection sec : iniInput.getSections() ) {
        	Event ev;
            
            try{
        		ev = parser.parse(sec);
                
        	}
            catch(IllegalArgumentException e) {
            	throw new ParserConfigurationException(
                    "Event parsing failed:\n" + e
                );
            }

            try {
                simulator.pushEvent(ev);   
            }
            catch (IllegalArgumentException e) {
                throw e; // Illegal time
            }            
        }

        // 2 // 
        // Se ejecuta el simulador el número de pasos timeLimit
        // y se actualiza el OutputStream.
        try {
			simulator.execute(timeLimit, outStream);
		}
        catch (SimulationException e) {
            throw e;
        } 
        catch (IOException e) {
			throw e;
		} 
    }

    public void setOutputStream(JTextAreaOutputStream report) {

    }
}
