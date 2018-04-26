package es.ucm.fdi.control.evbuild;

import es.ucm.fdi.ini.IniSection;
import es.ucm.fdi.model.events.Event;
import es.ucm.fdi.model.events.NewHighwayRoad;
import es.ucm.fdi.model.SimObj.HighwayRoad;

/**
 * Clase que construye un <code>Event</code> 
 * {@link NewHighwayRoad} utilizado para crear una 
 * {@link HighwayRoad} durante la simulación.
 * Hereda de {@link EventBuilder}.
 */
public class NewHighwayRoadBuilder extends EventBuilder {

	/**
	 * Etiqueta utilizada en las <code>IniSections</code>
	 * para representar este tipo de eventos.
	 */
	private static final String SECTION_TAG = "new_road";

	/**
	 * Valor que debería almacenar la clave <code>type</code>
	 * de una <code>IniSection</code> que represente a un
	 * <code>HighwayRoad</code>.
	 */
	private static final String TYPE = "lanes";

    /**
	 * Constructor de {@link NewHighwayRoadBuilder} que 
	 * pasa el atributo <code>SECTION_TAG</code> al 
	 * constructor de la superclase.
	 */
    public NewHighwayRoadBuilder() {
        super(SECTION_TAG);
    }

	/**
	 * Método de parsing que comprueba si la 
	 * <code>IniSection</code> pasada como argumento 
	 * representa un evento <code>NewBikeVehicle</code>
	 * y si sus parámetros son correctos.
	 * 
	 * @param ini 	<code>IniSection</code> a parsear
	 * @return 		<code>NewHighwayRoad</code> event or 
	 * 				<code>null</code> if parsing failed
	 * 
	 * @throws IllegalArgumentException if <code>ini</code> represents 
	 *	 								the searched event but its 
	 *									arguments are not valid
	 */
    @Override
    Event parse(IniSection ini) 
			throws IllegalArgumentException {

        // Se comprueba si es una NewHighwayRoad.
        if ( iniNameMatch(ini) && typeMatch(ini, TYPE) ) {
            String id;
			int time = 0;
			int maxSpeed, length, lanes;
			String src, dest;
			
			// ID ok?
			try {
				id = parseID(ini, "id");
			}
			catch (IllegalArgumentException e) {
				throw new IllegalArgumentException(
					e + " in new Highway Road."
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
						"in Highway Road with id " + id
					);
				}
			}

			// SOURCE ok?	
			try {
				src = parseID(ini, "src");
			}
			catch (IllegalArgumentException e) {
				throw new IllegalArgumentException(
					e + " when reading dest " + 
					"in Highway Road with id " + id
				);
			}
			
			// DESTINY ok?
			try {
				dest = parseID(ini, "dest");
			}
			catch (IllegalArgumentException e) {
				throw new IllegalArgumentException(
					e + " when reading source " + 
					"in Highway Road with id " + id
				);
			}
			
			// MAXSPEED ok?
			try {
				maxSpeed = parsePositiveInt(ini, "max_speed");
			}
			catch (IllegalArgumentException e) {
				throw new IllegalArgumentException(
					e + " when reading max speed "+
					"in Highway Road with id " + id
				);
			}
			
			// LENGTH ok?
			try {
				length = parsePositiveInt(ini, "length");
			}
			catch (NumberFormatException e) {
				throw new IllegalArgumentException(
					e + " when reading length "+
					"in Highway Road with id " + id
				);
			}

            // LANES ok?
            try {
                lanes = parsePositiveInt(ini, "lanes");
            }
            catch (IllegalArgumentException e) {
                throw new IllegalArgumentException(
					e + " when reading length " + 
					"in Highway Road with id " + id
				);
            }

            // New Highway Road.
            return 	new NewHighwayRoad(time, id, length, maxSpeed, 
							src, dest, lanes);
        } 
        else 
			return null;
    }
}

