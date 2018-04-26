package es.ucm.fdi.control.evbuild;

import es.ucm.fdi.ini.IniSection;
import es.ucm.fdi.model.events.Event;

/**
 * Clase utilizada como herramienta de parseo de
 * {@link IniSection IniSections}, extraídas de 
 * archivos de extensión <code>.ini</code>, que
 * representan los {@link Event Events} de la 
 * simulación.
 */
public class EventParser {

	/**
	 * Array con los posibles <code>Events</code>
	 * del simulador.
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
	 * Constructor de {@link EventParser}.
	 */
	public EventParser() {
		/*NADA*/
	}
	
	/**
	 * Función de búsqueda de <code>Event</code> a partir
	 * de la <code>IniSection</code> de un archivo con 
	 * extensión <code>.ini</code>.
	 * 
	 * @param ini 	<code>IniSection</code> del archivo
	 * 
	 * @return 		<code>Event</code> indicado en 
	 * 				<code>ini</code> (si se encuentra).
	 * 
	 * @throws IllegalArgumentException 	if an event matched the
	 * 										section's tag, but the
	 * 										section data is not valid
	 * @throws IllegalArgumentException 	if no event matches the 
	 * 										section's tag
	 */
	public Event parse(IniSection ini) 
			throws IllegalArgumentException {
		
		for ( EventBuilder event : events ) {
			Event next;
			
			try {
				next = event.parse(ini);
			}
			catch (IllegalArgumentException e) {
				throw e;
			}				
			
			if (next != null) {
				return next;
			} 
		}
		

		// Si llegamos a este punto es que todos 
		// los parse han devuelto null
		throw new IllegalArgumentException(
			"No event found."
		);
	}
	
	
}
