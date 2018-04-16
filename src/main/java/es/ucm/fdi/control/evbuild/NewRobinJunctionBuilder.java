package es.ucm.fdi.control.evbuild;

import es.ucm.fdi.ini.IniSection;
import es.ucm.fdi.model.events.Event;
import es.ucm.fdi.model.events.NewJunction;

public class NewRobinJunctionBuilder extends EventBuilder {
    
    public NewJunctionBuilder() {
		super("new_junction");
    }
    
    // Parser de NewRobinJunction
	@Override
	Event parse(IniSection ini) throws IllegalArgumentException {
		// Comprobación de que es un NewJunction
		if ( ini.getTag().equals(iniName) ) {
			String id = ini.getValue("id");
            int time = 0;
            int minTime, maxTime;
			
			// El ID sólo contiene letras,. números, o '_'
			if (!EventBuilder.validID(id)) {
				throw new IllegalArgumentException("Illegal junction ID: " + id);
			}
			
			// Si se ha incluido la key time
			String timeKey = ini.getValue("time");
			if (timeKey != null) {
				try {
					time = Integer.parseInt(timeKey);
				}
				// El tiempo no era un entero
				catch(NumberFormatException e) {
					throw new IllegalArgumentException("Time reading failure in junction with ID: " + id);
				}
				// Comprobamos que el tiempo sea positivo
				if (time < 0) {
					throw new IllegalArgumentException("Negative time in junction with ID: " + id);
				}
            }
            
            // Intervalo de tiempos de espera.
            try {
                minTime = Integer.parseInt( ini.getValue("min_time_slice") );
            }
            catch (NumberFormatException e) {
                throw new IllegalArgumentException("Light lapse reading failure in junction with ID: " + id);
            }
            if (minTime < 0) {
                throw new IllegalArgumentException("Negative time lapse in junction with ID: " + id);
            }

            try {
                maxTime = Integer.parseInt( ini.getValue("max_time_slice") );
            }
            catch (NumberFormatException e) {
                throw new IllegalArgumentException("Light lapse reading failure in junction with ID: " + id);
            }
            if (maxTime < 0) {
                throw new IllegalArgumentException("Negative time lapse in junction with ID: " + id);
            }

            if (maxTime > minTime) {
                throw new IllegalArgumentException("Not a valid time lapse in junction with ID: " + id);
            }
			
			return new NewRobinJunction(time, id, minTime, maxTime);
		}
		else return null;
	}
}