package es.ucm.fdi.model.events;

import java.util.ArrayList;
import java.util.List;

import es.ucm.fdi.model.SimObj.CarVehicle;
import es.ucm.fdi.model.SimObj.Junction;
import es.ucm.fdi.model.simulation.AlreadyExistingSimObjException;
import es.ucm.fdi.model.simulation.NonExistingSimObjException;
import es.ucm.fdi.model.simulation.TrafficSimulation;

/**
 * {@link Event} que representa la creación de un nuevo 
 * {@link CarVehicle} en la simulación. Hereda de 
 * {@link NewVehicle}
 */
public class NewCarVehicle extends NewVehicle {
	
	/**
	 * <code>Integer</code> que representa 
	 * la resistencia a las averías.
	 */
	private int resistance;

	/**
	 * Probabilidad de avería del 
	 * <code>CarVehicle</code>
	 */
	private double faultyChance;
	
	/**
	 * Duración máxima de la avería.
	 */
	private int faultDuration;
	
	/**
	 * Semilla aleatoria.
	 */
	private long randomSeed;
	
	/**
	 * Constructor de {@link NewCarVehicle}.
	 * 
	 * @param newTime 		tiempo de ejecución del evento
	 * @param ID 			identificador del nuevo <code>CarVehicle</code>
	 * @param max 			máxima velocidad alcanzable
	 * @param trip 			ruta de <code>Junctions</code>
	 * @param res 			resistencia a la avería
	 * @param breakChance 	probabilidad de avería
	 * @param breakDuration duración máxima de avería
	 * @param seed 			semilla aleatoria
	 */
	public NewCarVehicle(int newTime, String ID, int max, List<String> trip, 
			int res, double breakChance, int breakDuration, long seed) {
		super(newTime, ID, max, trip);
		resistance = res;
		faultyChance = breakChance;
		faultDuration = breakDuration;
		randomSeed = seed;
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * El <code>NewCarVehicle</code> crea un nuevo objeto 
	 * <code>CarVehicle</code> en la simulación, derivado 
	 * de un <code>Vehicle</code>.
	 * </p>
	 * 
	 * @param sim la simulación sobre la que se ejecuta el evento
	 * 
	 * @throws AlreadyExistingSimObjException 	if <code>Vehicle</code>
	 * 											ID already registered
	 */
	@Override
	public void execute(TrafficSimulation sim) 
			throws AlreadyExistingSimObjException {
		try {
			super.execute(sim);
		}
		catch ( AlreadyExistingSimObjException e ) {
			throw e;
		}
	}
	
	/**
	 * Método que genera un nuevo <code>CarVehicle</code> 
	 * a partir de los atributos del <code>Event<code>.
	 * 
	 * @param sim 	la simulación sobre la que se ejecuta el evento
	 * @return 		<code>CarVehicle</code> con los datos del <code>Event</code>
	 * 
	 * @throws NonExistingSimObjException 	si alguna <code>Junction</code> 
	 * 										en la ruta no está registrada
	 */
	@Override
	protected CarVehicle newVehicle(TrafficSimulation sim) 
			throws NonExistingSimObjException {
		ArrayList<Junction> trip = new ArrayList<Junction>();

		// Deben existir todos los cruces del itinerario 
		// en el momento del evento.
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

		return	new CarVehicle(id, trip, maxSpeed, resistance, faultyChance,
						faultDuration, randomSeed);
	}	
}
