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
		
        // Se comprueba si es un NewRobinJunction
		if (iniNameMatch(ini) && typeMatch(ini, type)) {
			String id;
            int time = 0;
            int minTime, maxTime;
			
            // ID ok?
            try{
				id = parseID(ini, "id");
			}
			catch(IllegalArgumentException e){
				throw new IllegalArgumentException(e + " in new Robin Junction.");
			}

            // TIME ok?
            if(existsTimeKey(ini)){
				try{
					time = parseNoNegativeInt(ini, "time");
				}
				catch(IllegalArgumentException e){
					throw new IllegalArgumentException(e + 
							" when reading time in Robin Junction with id " + id);
				}
			}
            
            // TIMELAPSES ok?
            // Tiempo mínimo.
            try {
                minTime = parseNoNegativeInt(ini,"min_time_slice");
            }
            catch (IllegalArgumentException e) {
                throw new IllegalArgumentException(e + 
                		" when reading minimum Time in Robin Junction with ID " + id);
            }

            // Tiempo máximo.
            try {
                maxTime = parseNoNegativeInt(ini,"max_time_slice");
            }
            catch (IllegalArgumentException e) {
                throw new IllegalArgumentException(e + 
                		" when reading maximum Time in Robin Junction with ID " + id);
            }

            // Mínimo menor que máximo.
            if (minTime > maxTime) {
                throw new IllegalArgumentException(
                		"Not a valid time lapse in Robin Junction ID: " + id);
            }
			
            // New Robin Junction.
			return new NewRobinJunction(time, id, minTime, maxTime);
		}
		else return null;
	}
}