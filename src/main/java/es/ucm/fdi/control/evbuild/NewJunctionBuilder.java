package es.ucm.fdi.control.evbuild;

import es.ucm.fdi.ini.IniSection;
import es.ucm.fdi.model.events.Event;
import es.ucm.fdi.model.events.NewJunction;

/**
 * Clase que construye un evento <code>NewJunction</code> utilizado para
 * crear un <code>Junction</code> en la simulación.
 */
public class NewJunctionBuilder extends EventBuilder{
	
	/**
	 * Constructor de <code>NewJunctionBuilder</code> que pasa
	 * el parámetro <code>new_junction</code> al constructor de la
	 * superclase.
	 */
	public NewJunctionBuilder() {
		super("new_junction");
	}
	
	/**
	 * Método de <code>parsing</code> de <code>NewJunctionBuilder</code> que comprueba
	 * si la <code>IniSection</code> pasada como argumento representa un <code>NewJunction</code>
	 * y si sus parámetros son correctos.
	 * 
	 * @param ini <code>IniSection</code> a parsear.
	 * @return <code>NewJunction</code> o <code>null</code>.
	 */
	@Override
	Event parse(IniSection ini) throws IllegalArgumentException {

		// Se comprueba si es un NewJunction
		if (iniNameMatch(ini) && typeMatch(ini, null)) {
            String id = ini.getValue("id");
            int time = 0;

            // ID ok?
            try{
				id = parseID(ini, "id");
			}
			catch(IllegalArgumentException e){
				throw new IllegalArgumentException(e + " in new Junction.");
			}

            // TIME ok?
            if(existsTimeKey(ini)){
				try{
					time = parseNoNegativeInt(ini, "time");
				}
				catch(IllegalArgumentException e){
					throw new IllegalArgumentException(e + 
							" when reading time in Junction with id " + id);
				}
			}

			NewJunction junction = new NewJunction(time, id);
			return junction;
		}
		else return null;
	}

}
