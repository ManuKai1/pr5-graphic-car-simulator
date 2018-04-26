package es.ucm.fdi.control.evbuild;

import java.util.List;

import es.ucm.fdi.ini.IniSection;
import es.ucm.fdi.model.events.Event;
import es.ucm.fdi.model.events.NewVehicle;
import es.ucm.fdi.model.SimObj.Vehicle;

/**
 * Clase que construye un <code>Event</code> 
 * {@link NewVehicle} utilizado para crear un 
 * {@link Vehicle} durante la simulación.
 * Hereda de {@link EventBuilder}.
 */
public class NewVehicleBuilder extends EventBuilder {

	/**
	 * Etiqueta utilizada en las <code>IniSections</code>
	 * para representar este tipo de eventos.
	 */
	private static final String SECTION_TAG = "new_vehicle";

	/**
	 * Constructor de {@link NewVehicleBuilder} que 
	 * pasa el atributo <code>SECTION_TAG</code> al 
	 * constructor de la superclase.
	 */
	public NewVehicleBuilder(){
		super(SECTION_TAG);
	}
	
	/**
	 * Método de parsing que comprueba si la 
	 * <code>IniSection</code> pasada como argumento 
	 * representa un evento <code>NewVehicle</code>
	 * y si sus parámetros son correctos.
	 * 
	 * @param ini 	<code>IniSection</code> a parsear
	 * @return 		<code>NewVehicle</code> event or 
	 * 				<code>null</code> if parsing failed
	 * 
	 * @throws IllegalArgumentException if <code>ini</code> represents 
	 *	 								the searched event but its 
	 *									arguments are not valid
	 */
	@Override
	Event parse(IniSection ini) 
			throws IllegalArgumentException {
		
		//Se comprueba que es un NewVehicle
		if ( iniNameMatch(ini) && typeMatch(ini, null) ) {
			String id;
			int time = 0;
			int maxSpeed;

			// ID ok?
			try {
				id = parseID(ini, "id");
			}
			catch (IllegalArgumentException e) {
				throw new IllegalArgumentException(
					e + " in new Vehicle."
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
						"in Vehicle with id " + id
					);
				}
			}

			// MAXSPEED ok?
			try {
				maxSpeed = parseNoNegativeInt(ini, "max_speed");
			}
			catch (IllegalArgumentException e) {
				throw new IllegalArgumentException(
					e + " when reading maxSpeed " +
					"in Vehicle with id " + id
				);
			}

			// TRIP ok?
			// Creación de la ruta de Junction IDs.
			List<String> trip;
			try {
				trip = parseIDList(ini, "itinerary", 2);
			}
			catch (IllegalArgumentException e) {
				throw new IllegalArgumentException(
					e + " when reading itinerary "+
					"in Vehicle with id " + id
				);
			}

			// New Vehicle.
			return 	new NewVehicle(time, id, maxSpeed, trip);
		}
		else 
			return null;
	}
}
