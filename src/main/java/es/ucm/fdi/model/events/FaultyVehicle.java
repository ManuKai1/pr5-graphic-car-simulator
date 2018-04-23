package es.ucm.fdi.model.events;

import java.util.ArrayList;

import es.ucm.fdi.model.simulation.NonExistingSimObjException;
import es.ucm.fdi.model.simulation.TrafficSimulation;

/**
 * <code>Event</code> que representa la avería de uno o varios <code>Vehicle</code> 
 * en la simulación.
 */
public class FaultyVehicle extends Event {

	/**
	 * Lista con los IDs de los <code>Vehicles</code> que se van a averiar.
	 */
	private ArrayList<String> vehiclesID;

	/**
	 * Duración de la avería inducida.
	 */
	private int duration;
	
	/**
	 * Constructor de <code>FaultyVehicle</code>.
	 * 
	 * @param newTime tiempo de ejecución del evento
	 * @param vID <code>ArrayList</code> con los IDs de los <code>Vehicles</code> a averiar
	 * @param dur tiempo de avería inducido
	 */
	public FaultyVehicle(int newTime, ArrayList<String> vID, int dur) {
		super(newTime);
		vehiclesID = vID;
		duration = dur;
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * El <code>FaultyVehicleEvent</code> produce la avería de una colección 
	 * de <code>Vehicles</code> dentro de la simulación.
	 * </p> <p>
	 * La ejecución del evento puede fallar por la ausencia de un <code>SimObj</code>
	 * no registrado en la simulación.
	 * </p>
	 * 
	 * @param sim la simulación sobre la que se ejecuta el evento.
	 */
	@Override
	public void execute(TrafficSimulation sim) {
		try {
			sim.makeFaulty(vehiclesID, duration);
		}
		catch (NonExistingSimObjException e) {
			System.err.println(e.getMessage());
		}
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * En el caso de <code>FaultyVehicle</code>, comprueba también que la lista de
	 * vehículos a averiar y el tiempo de avería sean iguales.
	 * </p>
	 * 
	 * @param obj objeto a comparar
	 * @return if <code>FaultyVehicleEvent</code> equals <code>obj</code>
	 */
	@Override
	public boolean equals(Object obj) {
		boolean same;
		same = super.equals(obj);
		
		if (same) {
			FaultyVehicle other = (FaultyVehicle) obj;
			
			same = ( same && vehiclesID.equals(other.vehiclesID) );
			same = ( same && duration == other.duration );
		}

		return same;
	}
}
