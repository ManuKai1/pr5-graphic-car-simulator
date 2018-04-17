package es.ucm.fdi.control.evbuild;

import es.ucm.fdi.ini.IniSection;
import es.ucm.fdi.model.events.Event;

/**
 * Clase utilizada como herramienta de parseo de <code>IniSections</code> de 
 * archivos de extensión <code>.ini</code> en <code>Events</code>.
 */
public class EventParser {

	/**
	 * Array con los posibles eventos del simulador.
	 */
	private static EventBuilder[] events = 
		{
			new FaultyVehicleBuilder(), 

			new NewJunctionBuilder(),
			new NewRobinJunctionBuilder(),
			new NewCrowdedJunctionBuilder(),

			new NewRoadBuilder(), 
			new NewHighwayRoadBuilder(),
			new NewDirtRoadBuilder(),

			new NewVehicleBuilder(),
			new NewCarVehicleBuilder(),
			new NewBikeVehicleBuilder()
		};

	/**
	 * Constructor de <code>EventParser</code>.
	 */
	public EventParser() {
		/*NADA*/
	}
	
	/**
	 * Función de búsqueda de <code>Event</code> a partir de la <code>IniSection</code>
	 * de un archivo con extensión <code>.ini</code>.
	 * 
	 * @param ini <code>IniSection</code> del archivo.
	 * @return <code>Event</code> indicado en <code>ini</code> (si se encuentra).
	 * @throws IllegalArgumentException if no event matches the section's description
	 */
	public Event parse(IniSection ini) throws IllegalArgumentException {
		try {
			for ( EventBuilder event : events ) {
				Event next = event.parse(ini);
				if (next != null) {
					return next;
				} 
			}
		}
		catch (IllegalArgumentException e) {
			throw e;
		}
		// Si llegamos a este punto es que todos los parse han devuelto null
		throw new IllegalArgumentException("No event found.");
	}
	
	
}
