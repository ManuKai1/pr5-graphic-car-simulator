package es.ucm.fdi.model.events;

import java.util.Map;

import es.ucm.fdi.model.SimObj.DirtRoad;
import es.ucm.fdi.model.SimObj.Junction;
import es.ucm.fdi.model.simulation.AlreadyExistingSimObjException;
import es.ucm.fdi.model.simulation.NonExistingSimObjException;
import es.ucm.fdi.model.simulation.TrafficSimulation;
import es.ucm.fdi.util.TableDataType;

/**
 * {@link Event} que representa la creación de una
 * {@link DirtRoad} en la simulación. Hereda de 
 * {@link NewRoad}
 */
public class NewDirtRoad extends NewRoad {

    /**
     * Constructor de <code>NewRoad</code>.
	 * 
	 * @param newTime   tiempo de ejecución del evento
	 * @param ID        identificador de la nueva <code>DirtRoad</code>
	 * @param max       longitud de la vía
	 * @param lim       límite de velocidad
	 * @param fromID    <code>Junction</code> donde empieza
	 * @param toID      <code>Junction</code> donde acaba
	 */
    public NewDirtRoad(int newTime, String ID, int lgth, int lim,
            String fromID, String toID) {
        super(newTime, ID, lgth, lim, fromID, toID);
    }

    /**
     * {@inheritDoc}
     * <p>
     * El <code>NewDirtRoad</code> crea un nuevo objeto
     * <code>DirtRoad</code> en la simulación, derivado 
     * de una <code>Road</code>.
     * </p>
     * 
     * @param sim la simulación sobre la que se ejecuta el evento
     * 
     * @throws AlreadyExistingSimObjException   if <code>Road</code> 
     *                                          ID already registered
     */
    @Override
    public void execute(TrafficSimulation sim) 
        throws AlreadyExistingSimObjException, NonExistingSimObjException {
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
     * <p>
     * {@inheritDoc}
     * Añade un <code>NewDirtRoadEvent</code> al mapa. En
     * concreto, su descripción es de la forma:
     * </p> <p>
     * "New dirt road j4"
     * </p>
     * 
     * @param out {@inheritDoc}
     */
    @Override
    public void describe(Map<TableDataType, String> out) {
        // Descripción del evento.
        StringBuilder description = new StringBuilder();
        description.append("New junction ");
        description.append(id);

        // Inclusión en el mapa.
        String time = Integer.toString(getTime());
        String type = description.toString();
        out.put(TableDataType.E_TIME, time);
        out.put(TableDataType.E_TYPE, type);
    }

    /**
     * <p>
     * Devuelve la descripción <code>NewDirtRoad</code>
     * utilizada en las tablas de la GUI. Ejemplo:
     * </p> <p>
     * "New dirt road r3"
     * </p>
     * 
     * @return 	<code>String</code> con la descripción
     */
    @Override
    protected String getEventDescription() {
        // Descripción del evento.
        StringBuilder description = new StringBuilder();
        description.append("New dirt road ");
        description.append(id);

        return description.toString();
    }

    /**
     * Método que genera una nueva <code>DirtRoad</code>
     * a partir de los atributos del <code>Event<code>.
     * 
     * @param sim   la simulación sobre la que se ejecuta el evento
     * @return      <code>DirtRoad</code> con los datos del <code>Event</code>
     * 
     * @throws NonExistingSimObjException   si alguna de las 2 <code>Junctions</code>
     *                                      no está registrada
     */
    @Override
    protected DirtRoad newRoad(TrafficSimulation sim) 
            throws NonExistingSimObjException {
        Junction fromJunction = sim.getRoadMap().getJunctionWithID(fromJunctionID);
        Junction toJunction = sim.getRoadMap().getJunctionWithID(toJunctionID);

        if ( fromJunction != null && toJunction != null ) {
            return   new DirtRoad(id, length, speedLimit,
                            fromJunction, toJunction);
        } 
        else {
            throw new NonExistingSimObjException(
                "One or both junctions from Road with id: " + id + 
                " don't exist."
            );
        }
    }
}