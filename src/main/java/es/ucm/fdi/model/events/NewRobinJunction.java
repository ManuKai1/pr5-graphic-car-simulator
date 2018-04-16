package es.ucm.fdi.model.events;

import es.ucm.fdi.ini.IniError;
import es.ucm.fdi.model.SimObj.Junction;
import es.ucm.fdi.model.SimObj.RobinJunction;
import es.ucm.fdi.model.simulation.AlreadyExistingSimObjException;
import es.ucm.fdi.model.simulation.TrafficSimulation;

public class NewRobinJunction extends NewJunction {
    
    private int minTime, maxTime;

    public NewRobinJunction(int newTime, String ID, int minT, int maxT) {
        super(newTime, ID);
        minTime = minT;
        maxTime = maxT;
    }

    /**
     * Devuelve una instancia de cruce (round-robin) con los atributos
     * del evento.
     */
    @Override
    protected RobinJunction newRoad(TrafficSimulation sim) throws NonExistingSimObjException {
        return new RobinJunction(id, minTime, maxTime);
    }
}