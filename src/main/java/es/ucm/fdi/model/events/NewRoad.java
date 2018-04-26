package es.ucm.fdi.model.events;

import es.ucm.fdi.model.SimObj.Junction;
import es.ucm.fdi.model.SimObj.Road;
import es.ucm.fdi.model.simulation.AlreadyExistingSimObjException;
import es.ucm.fdi.model.simulation.NonExistingSimObjException;
import es.ucm.fdi.model.simulation.TrafficSimulation;

/**
 * {@link Event} que representa la creación de una
 * {@link Road} en la simulación.
 */
public class NewRoad extends Event {
	
	/**
	 * Identificador del objeto de simulación.
	 */
	protected String id;

	/**
	 * Longitud de la <code>Road</Road>.
	 */
	protected int length;

	/**
	 * Límite de velocidad de los <code>Vehicles</code>
	 * en la <code>Road</code>.
	 */
	protected int speedLimit;

	/**
	 * <code>Junction</code> donde empieza
	 * la <code>Road</code>.
	 */
	protected String fromJunctionID;

	/**
	 * <code>Junction</code> donde acaba
	 * la <code>Road</code>.
	 */
	protected String toJunctionID;	

	/**
	 * Constructor de {@link NewRoad}.
	 * 
	 * @param newTime 	tiempo de ejecución del evento
	 * @param ID 		identificador de la nueva <code>Road</code>
	 * @param max 		longitud de la vía
	 * @param lim 		límite de velocidad
	 * @param fromID 	<code>Junction</code> donde empieza
	 * @param toID 		<code>Junction</code> donde acaba
	 */
	public NewRoad(int newTime, String ID, int lgth, 
			int lim, String fromID, String toID) {
		super(newTime);
		id = ID;
		fromJunctionID = fromID;
		toJunctionID = toID;
		speedLimit = lim;
		length = lgth;
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * El <code>NewRoad</code> crea una nueva
	 * <code>Road</code>  dentro de la simulación.
	 * </p> <p>
	 * La ejecución del evento puede fallar por la 
	 * presencia de un <code>SimObj</code> ya 
	 * registrado en la simulación con el ID de la 
	 * nueva <code>Road</code>.
	 * </p>
	 * 
	 * @param sim la simulación sobre la que se ejecuta el evento
	 * 
	 * @throws AlreadyExistingSimObjException 	if <code>Road</code> 
	 * 											ID already registered 
	 */
	@Override
	public void execute(TrafficSimulation sim) 
			throws AlreadyExistingSimObjException, NonExistingSimObjException {
		if ( ! sim.getRoadMap().existsRoadID(id) ) {
			try {
				sim.addRoad( newRoad(sim) );			
			}
			catch (NonExistingSimObjException e) {
				throw e;
			}
		}
		else {
			throw new AlreadyExistingSimObjException(
				"Road with id: " + id + " already in simulation."
			);
		}
	}

	/**
	 * Método que genera una nueva <code>Road</code>
	 * a partir de los atributos del <code>Event<code>.
	 * 
	 * @param sim 	la simulación sobre la que se ejecuta el evento
	 * @return 		<code>Road</code> con los datos del <code>Event</code>
	 * 
	 * @throws NonExistingSimObjException 	si alguna de las 2 <code>Junctions</code> 
	 * 										no está registrada
	 */
	protected Road newRoad(TrafficSimulation sim) 
			throws NonExistingSimObjException {
		Junction fromJunction = sim.getRoadMap().getJunctionWithID(fromJunctionID);
		Junction toJunction = sim.getRoadMap().getJunctionWithID(toJunctionID);

		if ( fromJunction != null && toJunction != null ) {
			return 	new Road(id, length, speedLimit, 
							fromJunction, toJunction);
		}
		else {
			throw new NonExistingSimObjException(
				"One or both junctions from Road with id: " + id + 
				" don't exist."
			);
		}
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * En el caso de <code>NewRoad</code>, comprueba
	 * también que los IDs, la longitud, el límite de
	 * velocidad y las <code>Junctions</code> de entrada
	 * y salida son iguales.
	 * </p>
	 * 
	 * @param obj 	objeto a comparar
	 * @return 		if <code>NewRoad</code> equals <code>obj</code>
	 */
	@Override
	public boolean equals(Object obj) {
		boolean same;
		same = super.equals(obj);

		if (same) {
			NewRoad other = (NewRoad) obj;

			same = ( same && id == other.id );
			same = ( same && length == other.length );
			same = ( same && speedLimit == other.speedLimit );
			same = ( same && fromJunctionID.equals(other.fromJunctionID) );
			same = ( same && toJunctionID.equals(other.toJunctionID) );
		}
		
		return same;
	}
}
