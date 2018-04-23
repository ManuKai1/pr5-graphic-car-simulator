package es.ucm.fdi.control.evbuild;

import es.ucm.fdi.ini.IniSection;
import es.ucm.fdi.model.events.Event;
import es.ucm.fdi.model.events.NewRobinJunction;

/**
 * Clase que construye un evento <code>NewRobinJunction</code> utilizado para
 * crear un <code>RobinJunction</code> en la simulación.
 */
public class NewRobinJunctionBuilder extends EventBuilder {
    
    private final String type = "rr";

    /**
     * Constructor de <code>NewRobinJunctionBuilder</code> que pasa
     * el parámetro <code>new_junction</code> al constructor de la
     * superclase.
     */
    public NewRobinJunctionBuilder() {
		super("new_junction");
    }
    
    /**
     * Método de <code>parsing</code> de <code>NewRobinJunctionBuilder</code> que comprueba
     * si la <code>IniSection</code> pasada como argumento representa un <code>NewRobinJunction</code>
     * y si sus parámetros son correctos.
     * 
     * @param ini <code>IniSection</code> a parsear.
     * @return <code>NewRobinJunction</code> o <code>null</code>.
     */
	@Override
	Event parse(IniSection ini) throws IllegalArgumentException {
        boolean match = false;

        // Se comprueba si es un NewRobinJunction
        if ( ini.getTag().equals(iniName) && ini.getValue("type").equals(type) ) {
            match = true;
        }

		if (match) {
			String id = ini.getValue("id");
            int time = 0;
            int minTime, maxTime;
			
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
				catch(NumberFormatException e) {
					throw new IllegalArgumentException("Time reading failure in junction with ID: " + id);
				}
				// Comprobamos que el tiempo sea positivo
				if (time < 0) {
					throw new IllegalArgumentException("Negative time in junction with ID: " + id);
				}
            }
            
            // TIMELAPSES ok?
            // Tiempo mínimo.
            try {
                minTime = Integer.parseInt( ini.getValue("min_time_slice") );
            }
            catch (NumberFormatException e) {
                throw new IllegalArgumentException("Light lapse reading failure in junction with ID: " + id);
            }
            if (minTime < 0) {
                throw new IllegalArgumentException("Negative time lapse in junction with ID: " + id);
            }

            // Tiempo máximo.
            try {
                maxTime = Integer.parseInt( ini.getValue("max_time_slice") );
            }
            catch (NumberFormatException e) {
                throw new IllegalArgumentException("Light lapse reading failure in junction with ID: " + id);
            }
            if (maxTime < 0) {
                throw new IllegalArgumentException("Negative time lapse in junction with ID: " + id);
            }

            // Mínimo menor que máximo.
            if (minTime > maxTime) {
                throw new IllegalArgumentException("Not a valid time lapse in junction with ID: " + id);
            }
			
            // New Robin Junction.
			return new NewRobinJunction(time, id, minTime, maxTime);
		}
		else return null;
	}
}