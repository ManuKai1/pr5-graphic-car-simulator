package es.ucm.fdi.model.events;

import java.util.List;

import es.ucm.fdi.model.SimObj.Vehicle;
import es.ucm.fdi.model.simulation.NonExistingSimObjException;
import es.ucm.fdi.model.simulation.TrafficSimulation;

/**
 * {@link Event} que representa la avería de uno o varios 
 * {@link Vehicle} en la simulación.
 */
public class FaultyVehicle extends Event {

	/**
	 * Lista con los IDs de los <code>Vehicles</code>
	 * que se van a averiar.
	 */
	private List<String> vehiclesID;

	/**
	 * Duración de la avería inducida.
	 */
	private int duration;
	
	/**
	 * Constructor de {@link FaultyVehicle}.
	 * 
	 * @param newTime 	tiempo de ejecución del evento
	 * @param vehicles 	<code>List</code> con los IDs de
	 * 						los <code>Vehicles</code> a averiar
	 * @param dur 		tiempo de avería inducido
	 */
	public FaultyVehicle(int newTime, List<String> vehicles, int dur) {
		super(newTime);
		vehiclesID = vehicles;
		duration = dur;
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * El <code>FaultyVehicleEvent</code> produce la avería 
	 * de una colección de <code>Vehicles</code> dentro de 
	 * la simulación.
	 * </p> <p>
	 * La ejecución del evento puede fallar por la ausencia
	 * de un <code>SimObj</code> no registrado en la simulación.
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
	 * <p>
	 * Devuelve la descripción <code>FaultyVehicle</code>
	 * utilizada en las tablas de la GUI. Ejemplo:
	 * </p> <p>
	 * "Break vehicles [v2,v6,v8] for 11 units of time"
	 * </p>
	 * 
	 * @return 	<code>String</code> con la descripción
	 */
	@Override
	protected String getEventDescription() {
		// Descripción del evento.
		StringBuilder description = new StringBuilder();
		description.append("Break vehicles [");

		for (int i = 0; i < vehiclesID.size(); ++i) {
			String vID = vehiclesID.get(i);
			description.append(vID);

			if ( i < vehiclesID.size() - 1) {
				description.append(",");
			}
		}
		description.append("] for ");
		description.append( Integer.toString(duration) );
		description.append(" units of time");

		return 	description.toString();
	}


	/**
	 * {@inheritDoc}
	 * <p>
	 * En el caso de <code>FaultyVehicle</code>, comprueba
	 * también que la lista de <code>Vehicles</code> a averiar 
	 * y el tiempo de avería sean iguales.
	 * </p>
	 * 
	 * @param obj 	objeto a comparar
	 * @return 		if <code>FaultyVehicleEvent</code> 
	 * 					equals <code>obj</code>
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
