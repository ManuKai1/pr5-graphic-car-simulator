package es.ucm.fdi.control;

import java.io.OutputStream;

import es.ucm.fdi.control.evbuild.EventParser;
import es.ucm.fdi.ini.Ini;
import es.ucm.fdi.ini.IniSection;
import es.ucm.fdi.model.simulation.SimulationException;
import es.ucm.fdi.model.simulation.TrafficSimulation;
import es.ucm.fdi.model.events.Event;

/**
 * Clase utilizada como controlador, y que, en su método <code>execute()</code>, crea
 * un simulador <code>TrafficSimulation</code> a partir de un archivo <code>.ini</code>
 * de <code>Events</code> y ejecuta el simulador durante un tiempo determinado <code>timeLimit</code>,
 * volcando los resultados en un flujo de salida <code>outStream</code>.
 */
public class Controller {
    
    /**
     * Archivo <code>.ini</code> dividido en <code>IniSections</code> del que
     * se extraen los <code>Events</code> de la simulación.
     */
    Ini iniInput;

    /**
     * Flujo de salida donde se vuelcan los datos del simulador tras cada tick.
     */
    OutputStream outStream;

    /**
     * Número de ticks que se ejecuta el simulador.
     */
    int timeLimit;

    /**
     * Constructor de <code>Controller</code> que recibe el archivo <code>.ini</code>,
     * el flujo de salida y el tiempo límite de ejecución.
     * 
     * @param in <code>Ini</code> con el archivo <code>.ini</code>.
     * @param out <code>OutputStream</code> donde se vuelcan los datos.
     * @param time tiempo límite de ejecución.
     */
    public Controller(Ini in, OutputStream out, int time) {
        iniInput = in;
        outStream = out;
        timeLimit = time;
    }

    /**
     * Método principal de <code>Controller</code> que crea una <code>Simulation</code>
     * y un <code>EventParser</code>, recorre las secciones de <code>iniInput</code> guardando
     * los eventos en la simulación, y ejecuta la <code>Simulation</code>.
     */
    public void execute() {
        TrafficSimulation simulator = new TrafficSimulation();
        EventParser parser = new EventParser();

        // 1 //
        // Recorre las secciones del archivo .ini de entrada
        // y construye y guarda los eventos en el simulador.
        for ( IniSection sec : iniInput.getSections() ) {
        	try{
        		Event ev = parser.parse(sec);
                simulator.pushEvent(ev);   
        	}
            catch(IllegalArgumentException e){
            	System.err.println( e.getMessage() );
            }   
        	catch (SimulationException e) {
                System.err.println( e.getMessage() );
            } 
        }

        // 2 // 
        // Se ejecuta el simulador el número de pasos timeLimit
        // y se actualiza el OutputStream.
        simulator.execute(timeLimit, outStream);
    }
}
