package es.ucm.fdi.model.events;

import java.util.ArrayList;
import java.util.List;

import es.ucm.fdi.model.SimObj.BikeVehicle;
import es.ucm.fdi.model.SimObj.Junction;
import es.ucm.fdi.model.simulation.AlreadyExistingSimObjException;
import es.ucm.fdi.model.simulation.NonExistingSimObjException;
import es.ucm.fdi.model.simulation.TrafficSimulation;

/**
 * <code>Event</code> que representa la creación de un nuevo <code>BikeVehicle</code>
 * en la simulación. Hereda de <code>NewVehicle</code>
 */
public class NewBikeVehicle extends NewVehicle {

	/**
	 * Constructor de <code>NewBikeVehicle</code>.
	 * 
	 * @param newTime tiempo de ejecución del evento
	 * @param ID identificador del nuevo <code>BikeVehicle</code>
	 * @param max máxima velocidad alcanzable
	 * @param trip ruta de <code>Junctions</code>
	 */
	public NewBikeVehicle(int newTime, String ID, int max, List<String> trip) {
		super(newTime, ID, max, trip);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * El <code>NewBikeVehicle</code> crea un nuevo objeto <code>BikeVehicle</code> en la 
	 * simulación, derivado de un <code>Vehicle</code>
	 * </p>
	 * 
	 * @param sim la simulación sobre la que se ejecuta el evento.
	 * @throws AlreadyExistingSimObjException if <code>Vehicle</code> ID already registered
	 */
	@Override
	public void execute(TrafficSimulation sim) throws AlreadyExistingSimObjException {
		try {
			super.execute(sim);
		} 
		catch (AlreadyExistingSimObjException e) {
			throw e;
		}
	}
	
	/**
	 * Método que genera un nuevo <code>BikeVehicle</code> a partir de los atributos del
	 * <code>Event<code>.
	 * 
	 * @param sim la simulación sobre la que se ejecuta el evento
	 * @return <code>BikeVehicle</code> con los datos del <code>Event</code>
	 * @throws NonExistingSimObjException si alguna <code>Junction</code> en la ruta no está registrada
	 */
	@Override
	protected BikeVehicle newVehicle(TrafficSimulation sim) throws NonExistingSimObjException {
		ArrayList<Junction> trip = new ArrayList<Junction>();

		// Deben existir todos los cruces del itinerario en el momento del evento.
		for ( String jID : tripID ) {
			Junction j = sim.getRoadMap().getJunctionWithID(jID);
			if (j != null) {
				trip.add(j);
			}
			else {
				throw new NonExistingSimObjException(
					"Junction with id: " + jID + 
					" from itinerary of vehicle with id: " + getId() + 
					" not found in simulation."
				);
			}
		}
		
		return ( new BikeVehicle( getId(), trip, maxSpeed ) );
	}
}
