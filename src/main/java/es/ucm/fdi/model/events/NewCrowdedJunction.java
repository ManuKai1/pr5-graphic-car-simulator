package es.ucm.fdi.model.events;

import es.ucm.fdi.model.SimObj.CrowdedJunction;

public class NewCrowdedJunction extends NewJunction {

    public NewCrowdedJunction(int newTime, String ID) {
        super(newTime, ID);
    }

    /**
     * Devuelve una instancia de cruce (crowded) con los atributos
     * del evento
     */
    @Override 
    protected CrowdedJunction newJunction() {
        return new CrowdedJunction(id); 
    }
}