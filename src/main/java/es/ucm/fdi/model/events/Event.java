package es.ucm.fdi.model.events;

import java.util.Map;

import es.ucm.fdi.model.simulation.AlreadyExistingSimObjException;
import es.ucm.fdi.model.simulation.NonExistingSimObjException;
import es.ucm.fdi.model.simulation.TrafficSimulation;
import es.ucm.fdi.util.Describable;
import es.ucm.fdi.util.TableDataType;

/**
 * Clase con métodos abstractos que sirve de base
 * para cualquier evento del simulador.
 */
public abstract class Event implements Describable {
	
	/**
	 * Tiempo de ejecución del evento.
	 */
	private int time;
	
	/**
	 * Constructor de {@link Event}.
	 * 
	 * @param newTime tiempo de ejecución del evento
	 */
	public Event(int newTime) {
		time = newTime;
	}

	@Override
	public void describe(Map<TableDataType, Object> out)
	{
		// Inclusión en el mapa.
		String time = Integer.toString(this.time);
		String description = getEventDescription();
		out.put(TableDataType.E_TIME, time);
		out.put(TableDataType.E_TYPE, description);
	}

	protected abstract String getEventDescription();

	/**
	 * Ejecuta el <code>Event</code> en la simulación <code>sim</code> pasada como argumento.
	 */
	public abstract void execute(TrafficSimulation sim) throws AlreadyExistingSimObjException, NonExistingSimObjException;

	/**
	 * Devuelve el tiempo en que se ejecutará el <code>Event</code>.
	 * 
	 * @return tiempo de ejecución del <code>Event</code>
	 */
	public int getTime() {
		return time;
	}
	







	/**
	 * Comprueba si el <code>Event</code> es igual a un objeto dado
	 * <code>obj</code>.
	 * 
	 * @param obj objeto a comparar
	 * @return if <code>Event</code> equals <code>obj</code>
	 */
	public boolean equals(Object obj) {
		// Mismo evento.
		if (this == obj) {
			return true;
		}
		
		// 'obj' no es nada.
		if (obj == null) {
			return false;
		}
			
		// Clases distintas.
		if ( getClass() != obj.getClass() ) {
			return false;
		}
		
		// Comparación del tiempo de ejecución.
		Event other = (Event) obj;
		return ( time == other.getTime() );
	}	
}
