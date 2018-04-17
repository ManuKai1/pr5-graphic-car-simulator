package es.ucm.fdi.model.events;

import java.util.ArrayList;

import es.ucm.fdi.model.SimObj.Junction;
import es.ucm.fdi.model.SimObj.Vehicle;
import es.ucm.fdi.model.simulation.AlreadyExistingSimObjException;
import es.ucm.fdi.model.simulation.NonExistingSimObjException;
import es.ucm.fdi.model.simulation.TrafficSimulation;

/**
 * <code>Event</code> que representa la creación de un nuevo <code>Vehicle</code>
 * en la simulación.
 */
public class NewVehicle extends Event {

	/**
	 * Identificador del objeto de simulación.
	 */
	protected String id;

	/**
	 * Máxima velocidad alcanzable.
	 */
	protected int maxSpeed;

	/**
	 * Ruta del <code>Vehicle</code> a lo largo del simulador.
	 */
	protected ArrayList<String> tripID;
	
	/**
	 * Constructor de <code>NewVehicle</code>
	 * 
	 * @param newTime tiempo de ejecución del evento
	 * @param ID identificador del nuevo <code>Vehicle</code>
	 * @param max máxima velocidad alcanzable
	 * @param junctions ruta de <code>Junctions</code>
	 */
	public NewVehicle(int newTime, String ID, int max, ArrayList<String> junctions) {
		super(newTime);
		id = ID;
		maxSpeed = max;
		tripID = junctions;
	}
	
	/**
	 * Devuelve el identificador del <code>Vehicle</code>.
	 * 
	 * @return identificador de <code>Vehicle</code>
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * Devuelve la velocidad máxima alcanzable por el <code>Vehicle</code>.
	 * 
	 * @return velocidad máxima alcanzable
	 */
	public int getMaxSpeed() {
		return maxSpeed;
	}
	
	/**
	 * Devuelve la ruta del <code>Vehicle</code> en forma de lista de <code>Junctions</code>.
	 * 
	 * @return <code>ArrayList</code> con los ID de las <code>Junctions</code> de la ruta
	 */
	public ArrayList<String> getTripID(){
		return tripID;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * El <code>NewVehicle</code> crea un nuevo <code>Vehicle</code> 
	 * dentro de la simulación.
	 * </p> <p>
	 * La ejecución del evento puede fallar por la presencia de un <code>SimObj</code>
	 * ya registrado en la simulación con el ID del nuevo <code>Vehicle</code>.
	 * </p>
	 * 
	 * @param sim la simulación sobre la que se ejecuta el evento.
	 * @throws AlreadyExistingSimObjException if <code>Vehicle</code> ID already registered 
	 */
	@Override
	public void execute(TrafficSimulation sim) throws AlreadyExistingSimObjException {
		if ( ! sim.existsVehicle(getId()) ) {
			try {
				Vehicle newV = newVehicle(sim);
				sim.addVehicle(newV);
			}
			catch (NonExistingSimObjException e) {
				System.err.println( e.getMessage() );
			}
		}
		else {
			throw new AlreadyExistingSimObjException(
				"Vehicle with id: " + getId() + " already in simulation."
			);
		}
	}
	
	/**
	 * Método que genera un nuevo <code>Vehicle</code> a partir de los atributos del
	 * <code>Event<code>.
	 * 
	 * @param sim la simulación sobre la que se ejecuta el evento
	 * @return <code>Vehicle</code> con los datos del <code>Event</code>
	 * @throws NonExistingSimObjException si alguna <code>Junction</code> en la ruta no está registrada
	 */
	protected Vehicle newVehicle(TrafficSimulation sim) throws NonExistingSimObjException {
		ArrayList<Junction> trip = new ArrayList<Junction>();

		// Deben existir todos los cruces del itinerario en el momento del evento.
		for ( String jID : tripID ) {
			Junction j = sim.getJunction(jID);
			if ( j != null ) {
				trip.add(j);
			}
			else {
				throw new NonExistingSimObjException("Junction with id: " + jID + " from itinerary of vehicle with id: " + getId() + " not found in simulation.");
			}
		}
		return ( new Vehicle(getId(), trip, maxSpeed) );
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * En el caso de <code>NewVehicle</code>, comprueba también que los IDs, la velocidad
	 * máxima y la ruta son iguales. 
	 * </p>
	 * 
	 * @param obj objeto a comparar
	 * @return if <code>NewVehicle</code> equals <code>obj</code>
	 */
	@Override
	public boolean equals(Object obj) {
		boolean same;
		same = super.equals(obj);

		if (same) {
			NewVehicle other = (NewVehicle) obj;

			same = ( same && getId() == other.getId() );
			same = ( same && maxSpeed == other.maxSpeed );
			same = ( same && tripID.equals(other.tripID) );
		}
		
		return same;
	}
}
