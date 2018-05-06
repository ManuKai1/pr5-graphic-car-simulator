package es.ucm.fdi.model.events;


import es.ucm.fdi.model.SimObj.RobinJunction;
import es.ucm.fdi.model.simulation.AlreadyExistingSimObjException;
import es.ucm.fdi.model.simulation.TrafficSimulation;

/**
 * {@link Event} que representa la creación de una 
 * {@link RobinJunction} en la simulación. Heradad
 * de {@link NewJunction}
 */
public class NewRobinJunction extends NewJunction {
    
    /**
     * Tiempo mínimo de encendido del semáforo.
     */
    private int minTime;

    /**
     * Tiempo máximo de encendido del semáforo.
     */
    private int maxTime;

    /**
     * Constructor de {@link NewRobinJunction}.
     * 
     * @param newTime tiempo de ejecución del evento.
     * @param ID identificador de la nueva <code>RobinJunction</code>.
     * @param minT tiempo mínimo de semáforo
     * @param maxT tiempo máximo de semáforo
     */
    public NewRobinJunction(int newTime, String ID, int minT, int maxT) {
        super(newTime, ID);
        minTime = minT;
        maxTime = maxT;
    }

    /**
     * {@inheritDoc}
     * <p>
     * El <code>NewRobinJunction</code> crea un nuevo objeto <code>RobinJunction</code> en la 
     * simulación, derivado de una <code>Junction</code>
     * </p>
     * 
     * @param sim la simulación sobre la que se ejecuta el evento.
     * 
     * @throws AlreadyExistingSimObjException   if <code>Vehicle</code> 
     *                                          ID already registered
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
	 * <p>
	 * Devuelve la descripción <code>NewRobinJunction</code>
	 * utilizada en las tablas de la GUI. Ejemplo:
	 * </p> <p>
	 * "New robin junction j3"
	 * </p>
	 * 
	 * @return 	<code>String</code> con la descripción
	 */
	@Override
	protected String getEventDescription() {
		// Descripción del evento.
		StringBuilder description = new StringBuilder();
		description.append("New robin junction ");
		description.append(id);

		return 	description.toString();
	}

    /**
     * Método que genera una nueva <code>RobinJunction</code>ç
     * a partir de los actributos del <code>Event</code>.
     * 
     * @return <code>RobinJunction</code> with indicated attributes
     */
    @Override
    protected RobinJunction newJunction() {
        return  new RobinJunction(id, minTime, maxTime);
    }
}