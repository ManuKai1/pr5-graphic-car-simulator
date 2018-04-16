package es.ucm.fdi.control.evbuild;

import es.ucm.fdi.ini.IniSection;
import es.ucm.fdi.model.events.Event;
import es.ucm.fdi.model.events.NewCrowdedJunction;

public class NewCrowdedJunctionBuilder extends EventBuilder {

    private final String type = "mc";

    public NewCrowdedJunctionBuilder() {
        super("new_junction");
    }

    // Parser de NewCrowdedJunction
    @Override
    Event parse(IniSection ini) throws IllegalArgumentException {
        boolean match = false;

        // Se comprueba si es un NewCrowdedJunction
        if ( ini.getTag().equals(iniName) && ini.getValue("type").equals(type) ) {
            match = true;
        }

        if (match) {
            String id = ini.getValue("id");
            int time = 0;

            // ID ok?
            if ( ! EventBuilder.validID(id) ) {
                throw new IllegalArgumentException("Illegal junction ID: " + id);
            }

            // TIME ok?
            String timeKey = ini.getValue("time");
            if (timeKey != null) {
                try {
                    time = Integer.parseInt(timeKey);
                }
                // El tiempo no era un entero
                catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Time reading failure in junction with ID: " + id);
                }
                // Comprobamos que el tiempo sea positivo
                if (time < 0) {
                    throw new IllegalArgumentException("Negative time in junction with ID: " + id);
                }
            }

            // New Crowded Junction.
            return new NewCrowdedJunction(time, id);
        } 
        else return null;
    }
}