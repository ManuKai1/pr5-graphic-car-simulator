package es.ucm.fdi.control.evbuild;

import java.util.List;

import es.ucm.fdi.ini.IniSection;
import es.ucm.fdi.model.events.Event;
import es.ucm.fdi.model.events.NewBikeVehicle;

/**
 * Clase que construye un <code>Event</code> 
 * {@link NewBikeVehicle} utilizado para crear un 
 * {@link BikeVehicle} durante la simulación.
 * Hereda de {@link EventBuilder}.
 */
public class NewBikeVehicleBuilder extends EventBuilder {
	
	/**
	 * Etiqueta utilizada en las <code>IniSections</code>
	 * para representar este tipo de eventos.
	 */
	private static final String SECTION_TAG = "new_vehicle";

	/**
	 * Valor que debería almacenar la clave <code>type</code>
	 * de una <code>IniSection</code> que represente a un
	 * <code>BikeVehicle</code>.
	 */
	private static final String TYPE = "bike";

	/** 
	 * Constructor de {@link NewBikeVehicleBuilder} que 
	 * pasa el atributo <code>SECTION_TAG</code> al 
	 * constructor de la superclase.
	 */
	public NewBikeVehicleBuilder() {
		super(SECTION_TAG);
	}
	
	/**
	 * Método de parsing que comprueba si la 
	 * <code>IniSection</code> pasada como argumento 
	 * representa un evento <code>NewBikeVehicle</code>
	 * y si sus parámetros son correctos.
	 * 
	 * @param ini 	<code>IniSection</code> a parsear
	 * @return 		<code>NewBikeVehicle</code> event or 
	 * 				<code>null</code> if parsing failed
	 * 
	 * @throws IllegalArgumentException if <code>ini</code> represents 
	 *	 								the searched event but its 
	 *									arguments are not valid
	 */
	@Override
	Event parse(IniSection ini)
			throws IllegalArgumentException {
		// Se comprueba si es un NewBikeVehicle
		if ( iniNameMatch(ini) && typeMatch(ini, TYPE) ) {
			String id;
			int time = 0;
			int maxSpeed;

			// ID ok?
			try {
				id = parseID(ini, "id");
			}
			catch (IllegalArgumentException e) {
				throw new IllegalArgumentException(
					e + " in new Bike."
				);
			}

			// TIME ok?
			if( existsTimeKey(ini) ) {
				try {
					time = parseNoNegativeInt(ini, "time");
				}
				catch (IllegalArgumentException e) {
					throw new IllegalArgumentException(
						e + " when reading time " + 
						"in bike with id: " + id);
				}
			}

			// MAXSPEED ok?
			try {
				maxSpeed = parseNoNegativeInt(ini, "max_speed");
			}
			catch (IllegalArgumentException e) {
				throw new IllegalArgumentException(
					e + " when reading maxSpeed " +
					"in bike with id: " + id);
			}

			// TRIP ok?
			// Creación de la ruta de Junction IDs.
			List<String> trip;
			try {
				trip = parseIDList(ini, "itinerary", 2);
			}
			catch (IllegalArgumentException e) {
				throw new IllegalArgumentException(
					e + " when reading itinerary " + 
					"in bike with id: " + id);
			}
			
			// New Bike Vehicle.
			return 	new NewBikeVehicle(time, id, maxSpeed, trip);
		}
		else 
			return null;
	}
}
