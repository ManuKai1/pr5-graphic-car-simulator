package es.ucm.fdi.control.evbuild;


import es.ucm.fdi.ini.IniSection;
import es.ucm.fdi.model.events.Event;
import es.ucm.fdi.model.events.NewRoad;
import es.ucm.fdi.model.SimObj.Road;

/**
 * Clase que construye un <code>Event</code> 
 * {@link NewRoad} utilizado para crear una
 * {@link Road} durante la simulación.
 * Hereda de {@link EventBuilder}.
 */
public class NewRoadBuilder extends EventBuilder {
	
	/**
	 * Etiqueta utilizada en las <code>IniSections</code>
	 * para representar este tipo de eventos.
	 */
	private static final String SECTION_TAG = "new_road";

	/**
	 * Constructor de <code>NewRoadBuilder</code> que pasa
	 * el parámetro <code>new_road</code> al constructor de la
	 * superclase.
	 */
	public NewRoadBuilder() {
		super(SECTION_TAG);
	}
	
	/**
	 * Método de <code>parsing</code> de <code>NewRoadBuilder</code> que comprueba
	 * si la <code>IniSection</code> pasada como argumento representa un <code>NewRoad</code>
	 * y si sus parámetros son correctos.
	 * 
	 * @param ini <code>IniSection</code> a parsear.
	 * @return <code>NewRoad</code> o <code>null</code>.
	 */
	@Override
	Event parse(IniSection ini) {

		// Se comprueba si es un NewRoad
		if ( iniNameMatch(ini) && typeMatch(ini, null) ) {
            String id;
			int time = 0;
			int maxSpeed, length;
			String src, dest;
			
			// ID ok?
			try {
				id = parseID(ini, "id");
			}
			catch (IllegalArgumentException e) {
				throw new IllegalArgumentException(
					e + " in new Road."
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
						"in Road with id " + id
					);
				}
			}

			// SOURCE ok?	
			try {
				src = parseID(ini, "src");
			}
			catch (IllegalArgumentException e) {
				throw new IllegalArgumentException(
					e + " when reading dest "+
					"in Road with id " + id
				);
			}
			
			// DESTINY ok?
			try {
				dest = parseID(ini, "dest");
			}
			catch (IllegalArgumentException e) {
				throw new IllegalArgumentException(
					e + " when reading source "+ 
					"in Road with id " + id
				);
			}
			
			// MAXSPEED ok?
			try {
				maxSpeed = parsePositiveInt(ini, "max_speed");
			}
			catch (IllegalArgumentException e) {
				throw new IllegalArgumentException(
					e + " when reading max speed "+
					"in Road with id " + id
				);
			}
			
			// LENGTH ok?
			try {
				length = parsePositiveInt(ini, "length");
			}
			catch (NumberFormatException e) {
				throw new IllegalArgumentException(
					e + " when reading length "+
					"in Road with id " + id
				);
			}
			
			// New Road.
			return 	new NewRoad(time, id, length, 
							maxSpeed, src, dest);
		}
		else 
			return null;
	}
}
