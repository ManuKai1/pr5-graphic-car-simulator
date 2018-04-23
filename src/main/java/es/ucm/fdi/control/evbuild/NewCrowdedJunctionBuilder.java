package es.ucm.fdi.control.evbuild;

import es.ucm.fdi.ini.IniSection;
import es.ucm.fdi.model.events.Event;
import es.ucm.fdi.model.events.NewCrowdedJunction;

/**
 * Clase que construye un evento <code>NewCrowdedJunction</code> utilizado para
 * crear un <code>CrowdedJunction</code> en la simulación.
 */
public class NewCrowdedJunctionBuilder extends EventBuilder {

    private final String type = "mc";

    /**
     * Constructor de <code>NewCrowdedJunctionBuilder</code> que pasa
     * el parámetro <code>new_junction</code> al constructor de la
     * superclase.
     */
    public NewCrowdedJunctionBuilder() {
        super("new_junction");
    }

    /**
     * Método de <code>parsing</code> de <code>NewCrowdedJunctionBuilder</code> que comprueba
     * si la <code>IniSection</code> pasada como argumento representa un <code>NewCrowdedJunction</code>
     * y si sus parámetros son correctos.
     * 
     * @param ini <code>IniSection</code> a parsear.
     * @return <code>NewCrowdedJunction</code> o <code>null</code>.
     */
    @Override
    Event parse(IniSection ini) throws IllegalArgumentException {

        // Se comprueba si es un NewCrowdedJunction
        if (iniNameMatch(ini) && typeMatch(ini, type)) {
            String id = ini.getValue("id");
            int time = 0;

            // ID ok?
            try{
				id = parseID(ini, "id");
			}
			catch(IllegalArgumentException e){
				throw new IllegalArgumentException(e + " in new Crowded Junction.");
			}

            // TIME ok?
            if(existsTimeKey(ini)){
				try{
					time = parseNoNegativeInt(ini, "time");
				}
				catch(IllegalArgumentException e){
					throw new IllegalArgumentException(e + 
							" when reading time in Crowded Junction with id " + id);
				}
			}

            // New Crowded Junction.
            return new NewCrowdedJunction(time, id);
        } 
        else return null;
    }
}