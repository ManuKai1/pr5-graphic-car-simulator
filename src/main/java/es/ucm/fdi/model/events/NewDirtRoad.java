package es.ucm.fdi.model.events;

import es.ucm.fdi.model.SimObj.DirtRoad;
import es.ucm.fdi.model.SimObj.Junction;
import es.ucm.fdi.model.simulation.AlreadyExistingSimObjException;
import es.ucm.fdi.model.simulation.NonExistingSimObjException;
import es.ucm.fdi.model.simulation.TrafficSimulation;

/**
 * <code>Event</code> que representa la creación de una <code>DirtRoad</code>
 * en la simulación. Hereda de <code>NewRoad</code>.
 */
public class NewDirtRoad extends NewRoad {

    /**
     * Constructor de <code>NewRoad</code>
	 * 
	 * @param newTime tiempo de ejecución del evento
	 * @param ID identificador de la nueva <code>DirtRoad</code>
	 * @param max longitud de la vía
	 * @param lim límite de velocidad
	 * @param fromID <code>Junction</code> donde empieza
	 * @param toID <code>Junction</code> donde acaba
	 */
    public NewDirtRoad(int newTime, String ID, int lgth, int lim, String fromID, String toID) {
        super(newTime, ID, lgth, lim, fromID, toID);
    }

    /**
     * {@inheritDoc}
     * <p>
     * El <code>NewDirtRoad</code> crea un nuevo objeto <code>DirtRoad</code> en la 
     * simulación, derivado de una <code>Road</code>
     * </p>
     * 
     * @param sim la simulación sobre la que se ejecuta el evento.
     * @throws AlreadyExistingSimObjException if <code>Road</code> ID already registered
     */
    @Override
    public void execute(TrafficSimulation sim) throws AlreadyExistingSimObjException, NonExistingSimObjException {
        try {
            super.execute(sim);
        } catch (AlreadyExistingSimObjException e) {
            throw e;
        }
        catch (NonExistingSimObjException e) {
            throw e;
        }
    }

    /**
     * Método que genera una nueva <code>DirtRoad</code> a partir de los atributos del
     * <code>Event<code>.
     * 
     * @param sim la simulación sobre la que se ejecuta el evento
     * @return <code>DirtRoad</code> con los datos del <code>Event</code>
     * @throws NonExistingSimObjException si alguna de las 2 <code>Junctions</code> no está registrada
     */
    @Override
    protected DirtRoad newRoad(TrafficSimulation sim) throws NonExistingSimObjException {
        Junction fromJunction, toJunction;
        fromJunction = sim.getRoadMap().getJunctionWithID(fromJunctionID);
        toJunction = sim.getRoadMap().getJunctionWithID(toJunctionID);

        if ( fromJunction != null && toJunction != null ) {
            return ( new DirtRoad(id, length, speedLimit, fromJunction, toJunction) );
        } 
        else {
            throw new NonExistingSimObjException(
                "One or both junctions from Road with id: " + id + 
                " don't exist."
            );
        }
    }
}