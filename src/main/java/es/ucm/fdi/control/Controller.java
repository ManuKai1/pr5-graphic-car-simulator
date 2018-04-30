package es.ucm.fdi.control;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.cli.ParseException;

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
 * {@link #batchTimeLimit}, volcando los resultados en un flujo
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
     * Número de ticks que se ejecuta el simulador en modo
     * batch.
     */
    private int batchTimeLimit;

    /**
     * Simulación a la que el controlador tiene acceso.
     */
    private TrafficSimulation simulator;

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
        batchTimeLimit = time;
        simulator = new TrafficSimulation();
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
     * @throws ParseException                   if event parsing failed 
     *                                          (no matching event or 
     *                                          invalid data)
     * @throws IllegalArgumentException         if event time is lower 
     *                                          than sim time
     * @throws SimulationException              if an error ocurred during 
     *                                          the execution of events in 
     *                                          the simulation
     * @throws IOException                      if an error ocurred during 
     *                                          report generation in the 
     *                                          simulation
     */
    public void executeBatch() 
            throws ParseException, IOException, SimulationException {

        // 1 //
        // Recorre las secciones del archivo .ini de entrada
        // y construye y guarda los eventos en el simulador.
        try {
            pushEvents();
        }
        catch (ParseException e1) {
            throw e1;
        }
        catch (IllegalArgumentException e2) {
            throw e2;
        }
        

        // 2 // 
        // Se ejecuta el simulador el número de pasos batchTimeLimit
        // y se actualiza el OutputStream.
        try {
			simulate(batchTimeLimit);
		}
        catch (SimulationException e3) {
            throw e3;
        } 
        catch (IOException e4) {
			throw e4;
		} 
    }

    /**
     * Carga los eventos del archivo de entrada
     * {@code iniInput} en el {@code simulator}.
     * 
     * @throws ParseException               if event parsing failed 
     *                                      (no matching event or 
     *                                      invalid data)
     * @throws IllegalArgumentException     if event time is lower 
     *                                      than sim time   
     */
    public void pushEvents() 
            throws ParseException, IllegalArgumentException {
        
        EventParser parser = new EventParser();

        for ( IniSection sec : iniInput.getSections() ) {
        	Event ev;
            
            try {
        		ev = parser.parse(sec);
                
        	}
            catch (IllegalArgumentException e) {
            	throw new ParseException(
                    "Event parsing failed:\n" + 
                    		e.getMessage());
            }

            try {
                simulator.pushEvent(ev);   
            }
            catch (IllegalArgumentException e) {
                throw e; // Illegal time
            }            
        }
    }

    /**
     * Ejecuta el simulador durante un tiempo
     * determinado {@code time}.
     * 
     * @param time -    número de ticks que se
     *                  ejecutará el simulador
     * 
     * @throws SimulationException              if an error ocurred during 
     *                                          the execution of events in 
     *                                          the simulation
     * @throws IOException                      if an error ocurred during 
     *                                          report generation in the 
     *                                          simulation
     */
    public void simulate(int time) 
            throws SimulationException, IOException {

        try {
			simulator.execute(time, outStream);
		}
        catch (SimulationException e) {
            throw e;
        } 
        catch (IOException e) {
			throw e;
		} 
    }

    public void setIniInput(Ini newIni) {
        iniInput = newIni;
    }
    
    public void setIniInput(InputStream is) throws IOException {
        try {
			iniInput = new Ini(is);
		} catch (IOException e) {
			throw e;
		}
    }

    public void reset() throws ParseException {
        simulator = new TrafficSimulation();
        EventParser parser = new EventParser();

        // Recorre las secciones del archivo .ini de entrada
        // y construye y guarda los eventos en el simulador.
        for ( IniSection sec : iniInput.getSections() ) {
        	Event ev;
            
            try {
        		ev = parser.parse(sec);
                
        	}
            catch(IllegalArgumentException e) {
            	throw new ParseException(
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
    }

    public int getExecutionTime() {
        return simulator.getCurrentTime();
    }

    public int getBatchTimeLimit() {
        return batchTimeLimit;
    }

    //Setter de outStream
	public void setOutStream(OutputStream newOut) {
		outStream = newOut;	
	}

	public TrafficSimulation getSimulator() {
		return simulator;
	}
}
