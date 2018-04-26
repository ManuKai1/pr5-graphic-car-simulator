package es.ucm.fdi.control.evbuild;

import es.ucm.fdi.ini.IniSection;
import es.ucm.fdi.model.events.Event;
import es.ucm.fdi.model.events.NewJunction;
import es.ucm.fdi.model.SimObj.Junction;

/**
 * Clase que construye un <code>Event</code> 
 * {@link NewJunction} utilizado para crear una 
 * {@link Junction} durante la simulación.
 * Hereda de {@link EventBuilder}.
 */
public class NewJunctionBuilder extends EventBuilder {
	
	/**
	 * Etiqueta utilizada en las <code>IniSections</code>
	 * para representar este tipo de eventos.
	 */
	private static final String SECTION_TAG = "new_junction";
	
	/**
	 * Constructor de {@link NewJunctionBuilder} que 
	 * pasa el atributo <code>SECTION_TAG</code> al 
	 * constructor de la superclase.
	 */
	public NewJunctionBuilder() {
		super(SECTION_TAG);
	}
	
	/**
	 * Método de parsing que comprueba si la 
	 * <code>IniSection</code> pasada como argumento 
	 * representa un evento <code>NewJunction</code>
	 * y si sus parámetros son correctos.
	 * 
	 * @param ini 	<code>IniSection</code> a parsear
	 * @return 		<code>NewJunction</code> event or 
	 * 				<code>null</code> if parsing failed
	 * 
	 * @throws IllegalArgumentException if <code>ini</code> represents 
	 *	 								the searched event but its 
	 *									arguments are not valid
	 */
	@Override
	Event parse(IniSection ini) 
			throws IllegalArgumentException {

		// Se comprueba si es un NewJunction
		if ( iniNameMatch(ini) && typeMatch(ini, null) ) {
            String id = ini.getValue("id");
            int time = 0;

            // ID ok?
            try {
				id = parseID(ini, "id");
			}
			catch (IllegalArgumentException e) {
				throw new IllegalArgumentException(
					e + " in new Junction."
				);
			}

            // TIME ok?
            if ( existsTimeKey(ini) ) {
				try {
					time = parseNoNegativeInt(ini, "time");
				}
				catch (IllegalArgumentException e) {
					throw new IllegalArgumentException(
						e + " when reading time " + 
						"in Junction with id " + id
					);
				}
			}

			return new NewJunction(time, id);
		}
		else 
			return null;
	}

}
