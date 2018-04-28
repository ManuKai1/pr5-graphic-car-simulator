package es.ucm.fdi.model.events;


import es.ucm.fdi.model.SimObj.CrowdedJunction;
import es.ucm.fdi.model.simulation.AlreadyExistingSimObjException;
import es.ucm.fdi.model.simulation.TrafficSimulation;

/**
 * {@link Event} que representa la creación de una 
 * {@link CrowdedJunction} en la simulación.
 */
public class NewCrowdedJunction extends NewJunction {

    /**
     * Constructor de {@link NewCrowdedJunction}.
     * 
     * @param newTime   tiempo de ejecución del evento.
     * @param ID        identificador de la nueva 
     *                      <code>CrowdedJunction</code>.
     */
    public NewCrowdedJunction(int newTime, String ID) {
        super(newTime, ID);
    }

    /**
     * {@inheritDoc}
     * <p>
     * El <code>NewCrowdedJunction</code> crea un nuevo objeto
     * <code>CrowdedJunction</code> en la* simulación, derivado 
     * de una <code>Junction</code>.
     * </p>
     * 
     * @param sim la simulación sobre la que se ejecuta el evento
     * 
     * @throws AlreadyExistingSimObjException   if <code>Vehicle</code> 
     *                                          ID already registered
     */
    @Override
    public void execute(TrafficSimulation sim) 
            throws AlreadyExistingSimObjException {
        try {
            super.execute(sim);
        } catch (AlreadyExistingSimObjException e) {
            throw e;
        }
    }

    /**
	 * <p>
	 * Devuelve la descripción <code>NewCrowdedJunction</code>
	 * utilizada en las tablas de la GUI. Ejemplo:
	 * </p> <p>
	 * "New crowded junction j3"
	 * </p>
	 * 
	 * @return 	<code>String</code> con la descripción
	 */
	@Override
	protected String getEventDescription() {
		// Descripción del evento.
		StringBuilder description = new StringBuilder();
		description.append("New crowded junction ");
		description.append(id);

		return 	description.toString();
	}

    /**
     * Método que genera una nueva <code>CrowdedJunction</code>
     * a partir de los atributos del <code>Event</code>.
     * 
     * @return <code>CrowdedJunction</code> with indicated attributes
     */
    @Override 
    protected CrowdedJunction newJunction() {
        return new CrowdedJunction(id); 
    }
}