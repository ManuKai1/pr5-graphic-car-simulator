package es.ucm.fdi.model.events;

import es.ucm.fdi.model.SimObj.Junction;
import es.ucm.fdi.model.simulation.AlreadyExistingSimObjException;
import es.ucm.fdi.model.simulation.TrafficSimulation;

/**
 * <code>Event</code> que representa la creación de una <code>Junction</code>
 * en la simulación.
 */
public class NewJunction extends Event {

	/**
	 * Identificador del objeto de simulación.
	 */
	protected String id;
	
	/**
	 * Constructor de <code>NewJunction</code>.
	 * 
	 * @param newTime tiempo de ejecución del evento.
	 * @param ID identificador de la nueva <code>Junction</code>.
	 */
	public NewJunction(int newTime, String ID) {
		super(newTime);
		id = ID;

		// throws IniError ?
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * El <code>NewJunction</code> crea una nueva <code>Junction</code> 
	 * dentro de la simulación.
	 * </p> <p>
	 * La ejecución del evento puede fallar por la presencia de un <code>SimObj</code>
	 * ya registrado en la simulación con el ID de la nueva <code>Junction</code>.
	 * </p>
	 * 
	 * @param sim la simulación sobre la que se ejecuta el evento.
	 * @throws AlreadyExistingSimObjException if <code>Junction</code> ID already registered 
	 */
	@Override
	public void execute(TrafficSimulation sim) throws AlreadyExistingSimObjException {
		if ( ! sim.existsJunction(id) ) {
			sim.addJunction( newJunction() );
		} 
		else {
			throw new AlreadyExistingSimObjException(
				"Junction with id:" + id + " already in simulation."
			);
		}
	}
	
	/**
	 * Método que genera una nueva <code>Junction</code> a partir de los actributos
	 * del <code>Event</code>.
	 * 
	 * @return <code>Junction</code> with indicated ID
	 */
	protected Junction newJunction() {
		return new Junction(id);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * En el caso de <code>NewJunction</code>, comprueba también que los IDs sean iguales.
	 * </p>
	 * 
	 * @param obj objeto a comparar
	 * @return if <code>NewJunction</code> equals <code>obj</code>
	 */
	@Override
	public boolean equals(Object obj){
		boolean same;
		same = super.equals(obj);

		if (same) {
			NewJunction other = (NewJunction) obj;

			same = ( same && id == other.id );
		}
		
		return same;
	}
	
}
