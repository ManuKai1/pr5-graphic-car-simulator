package es.ucm.fdi.model.events;

import es.ucm.fdi.model.SimObj.HighwayRoad;
import es.ucm.fdi.model.SimObj.Junction;
import es.ucm.fdi.model.simulation.AlreadyExistingSimObjException;
import es.ucm.fdi.model.simulation.NonExistingSimObjException;
import es.ucm.fdi.model.simulation.TrafficSimulation;

/**
 * {@link Event} que representa la creación de una
 * {@link HighwayRoad} en la simulación. Hereda de 
 * {@link NewRoad}.
 */
public class NewHighwayRoad extends NewRoad {
    
    /**
     * Número de carriles de la vía.
     */
    private int numLanes;

    /**
     * Constructor de {@link NewHighwayRoad}.
     * 
     * @param newTime   tiempo de ejecución del evento
     * @param ID        identificador de la nueva <code>HighwayRoad</code>
     * @param max       longitud de la vía
     * @param lim       límite de velocidad
     * @param fromID    <code>Junction</code> donde empieza
     * @param toID      <code>Junction</code> donde acaba
     * @param numLanes  número de carriles de la vía
     */
    public NewHighwayRoad(int newTime, String ID, int lgth, int lim,
            String fromID, String toID, int lanes) {
        super(newTime, ID, lgth, lim, fromID, toID);
        numLanes = lanes;
    }

    /**
     * {@inheritDoc}
     * <p>
     * El <code>NewHighwayRoad</code> crea un nuevo objeto
     * <code>HighwayRoad</code> en la simulación, derivado 
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
        } catch (NonExistingSimObjException e) {
            throw e;
        }
    }

    /**
     * Método que genera una nueva <code>HighwayRoad</code>
     * a partir de los atributos del <code>Event<code>.
     * 
     * @param sim   la simulación sobre la que se ejecuta el evento
     * @return      <code>HighwayRoad</code> con los datos del <code>Event</code>
     * 
     * @throws NonExistingSimObjException   si alguna de las 2 <code>Junctions</code> 
     *                                      no está registrada
     */
    @Override
    protected HighwayRoad newRoad(TrafficSimulation sim) 
            throws NonExistingSimObjException {
        Junction fromJunction = sim.getRoadMap().getJunctionWithID(fromJunctionID);
        Junction toJunction = sim.getRoadMap().getJunctionWithID(toJunctionID);

        if ( fromJunction != null && toJunction != null ) {
            return  new HighwayRoad(id, length, speedLimit, 
                            fromJunction, toJunction, numLanes);
        } 
        else {
            throw new NonExistingSimObjException(
                "One or both junctions from Road with id: " + id + 
                " don't exist."
            );
        }
    }
}