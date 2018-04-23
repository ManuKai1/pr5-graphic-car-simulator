package es.ucm.fdi.model.simulation;

import java.lang.Exception;

/**
 * Excepción utilizada para marcar cualquier error sucedido durante la simulación.
 */
public class SimulationException extends Exception {

	public SimulationException(String info){
		super(info);
	}
	
}
