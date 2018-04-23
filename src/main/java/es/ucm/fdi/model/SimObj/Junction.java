package es.ucm.fdi.model.SimObj;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import es.ucm.fdi.ini.IniSection;
import es.ucm.fdi.model.simulation.SimulationException;

/**
 * Clase que representa una intersección como un objeto de simulación.
 */
public class Junction extends SimObject {
	
	protected final String REPORT_TITLE = "[junction_report]";

	/**
	 * Mapa de <code>Roads</code> entrantes en la <code>Junction</code>.
	 */
	protected List<Road> incomingRoads = new ArrayList<>();

	/**
	 * Mapa de <code>Roads</code> salientes en la <code>Junction</code>.
	 */
	protected Map<String, Road> exitRoads = new HashMap<>();

	/**
	 * Entero que mediante la operación módulo representa el semáforo encendido.
	 */
	protected int light = -1;	
	
	/**
	 * Constructor de <code>Junction</code>.
	 * 
	 * @param identifier identificador del objeto
	 */
	public Junction(String identifier) {
		super(identifier);
	}

	/**
	 * Método de AVANCE de Junction. Provoca el paso de los vehículos 
	 * de la carretera entrante con el semáforo en verde. Finalmente, 
	 * se actualiza el semáforo circular.
	 */
	/**
	 * {@inheritDoc}
	 * Método de AVANCE de <code>Junction</code>.
	 * <p>
	 * * En la primera iteración tras la creación de la <code>Junction</code>, se
	 * produce la primera actualización del semáforo con <code>firstLightUpdate()</code>.
	 * </p> <p>
	 * En primer lugar, se actualiza en <code>roadUpdate(Road)</code> la cola de
	 * la <code>greenRoad</code> con el semáforo en verde.
	 * </p> <p>
	 * En segundo lugar, se actualiza el tiempo de avería de los <code>Vehicles</code>
	 * averiados en la cola de espera con <code>refreshWaiting()</code>.
	 * </p> <p> 
	 * Finalmente, se actualiza el semáforo de la <code>Junction</code> mediante
	 * <code>lightUpdate()</code>.
	 * </p>
	 */
	@Override
	public void proceed() {
		
		if (light == -1) {			
			// * //
			// Primera actualización del semáforo.
			firstLightUpdate();
		}
		else {
			// 1 //
			// Actualización de la cola de la Road con el semáforo en verde.
			Road greenRoad = incomingRoads.get(light);
			roadUpdate(greenRoad);
			
			// 2 //
			// Actualización del tiempo de avería de los coches de la cola.
			greenRoad.refreshWaiting();

			// 3 //
			// Actualización del semáforo.
			lightUpdate();		
		}		
	}

	/**
	 * Actualiza el semáforo en el primer tick de la simulación.
	 */
	protected void firstLightUpdate() {
		light = 0; // Suponemos que hay al menos una carretera entrante

		// El semáforo de la carretera se pone verde.
		incomingRoads.get(light).setLight(true);
	}

	/**
	 * Actualiza la cola de la <code>Road</code> con el semáforo en verde.
	 * 
	 * @param greenRoad <code>Road</code> con el semáforo en verde
	 */
	protected void roadUpdate(Road greenRoad) {
		// Si no hay vehículos esperando, no ocurre nada.
		// Si hay vehículos en la cola, se intenta mover uno.
		if ( ! greenRoad.noVehiclesWaiting() ) {
			try {
				// El primer vehículo de la cola puede estar averiado:
				// no ocurre nada, cruzará cuando se agote su breakdownTime.
				greenRoad.moveWaitingVehicle();
			} catch (SimulationException e) {
				// Si la greenRoad está en rojo, se captura la excepción.
				System.err.println(e.getMessage());
			}
		}
	}
	
	/**
	 * Actualiza el semáforo de la <code>Junction</code>.
	 */
	protected void lightUpdate() {
		Road usedRoad = incomingRoads.get(light); // Carretera actualizada

		// * //
		// La carretera actualizada se pone en rojo.
		usedRoad.setLight(false);

		// 1 //
		// El semáforo avanza a la siguiente carretera.
		int numIncomingRoads = incomingRoads.size();
		light = (light + 1) % numIncomingRoads;

		// 2 // 
		// La siguiente carretera se pone en verde.
		incomingRoads.get(light).setLight(true);
	}
	
	/**
	 * Genera una <code>IniSection</code> que informa de los atributos de la
	 * <code>Junction</code> en el tiempo del simulador.
	 * 
	 * @param simTime tiempo del simulador
	 * @return <code>IniSection</code> con información de la <code>Junction</code>
	 */
	public IniSection generateIniSection(int simTime) {
		// 1 //
		// Se crea la etiqueta de la sección (sin corchetes).
		String tag = REPORT_TITLE;
		tag = (String) tag.subSequence(1, tag.length() - 1);
		IniSection section = new IniSection(tag);

		// 2 // 
		// Se generan los datos en el informe.
		section.setValue("id", id);
		section.setValue("time", simTime);
		section.setValue("queues", getQueuesValue() );


		return section;
	}

	/**
	 * Genera un <code>StringBuilder</code> con la información sobre las
	 * colas de la <code>Junction</code>.
	 * 
	 * @return <code>String</code> con las colas.
	 */
	protected String getQueuesValue() {
		// Generación del string de queues
		StringBuilder queues = new StringBuilder();
		for (Road incR : incomingRoads ) {
			queues.append(incR.getWaitingState());
			queues.append(",");
		}

		// Borrado de última coma (si queues no es vacío).
		if (queues.length() > 0) {
			queues.deleteCharAt(queues.length() - 1);
		}

		return queues.toString();
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * En el caso de <code>Junction</code>, comprueba además todos los atributos
	 * correspondientes.
	 * </p>
	 * 
	 * @param obj objeto a comparar
	 * @return if <code>Junction</code> equals <code>obj</code>.
	 */
	public boolean equals(Object obj) {
		boolean same;
		same = super.equals(obj);

		if (same) {
			Junction other = (Junction) obj;

			same = (same && light == other.light);
			same = (same && incomingRoads.equals(other.incomingRoads));
			same = (same && exitRoads.equals(other.exitRoads));
		}

		return same;
	}
	
	/**
	 * Devuelve la Mapa de <code>Roads</code> entrantes
	 * 
	 * @return Mapa de <code>Roads</code> entrantes.
	 */
	public List<Road> getIncomingRoads() {
		return incomingRoads;
	}
	
	/**
	 * Comprueba si la <code>Junction</code> tiene <code>Roads</code> entrantes.
	 * 
	 * @return si <code>incomingRoads</code> no es vacía
	 */
	public boolean hasIncomingRoads() {
		return (incomingRoads.size() > 0);
	}

	/**
	 * Devuelve el Mapa de <code>Roads</code> salientes
	 * 
	 * @return mapa de <code>Roads</code> salientes
	 */
	public Map<String, Road> getExitRoads() {
		return exitRoads;
	}

	/**
	 * Añade una nueva <code>Road</code> de salida a la 
	 * <code>Junction</code>.
	 * 
	 * @param newRoad Nueva <code>Road</code> saliente
	 */
	public void addNewExitRoad(Road newRoad) {
		exitRoads.put(newRoad.getID(), newRoad);
	}

	/**
	 * Añade una nueva <code>Road</code> de entrada a la 
	 * <code>Junction</code>.
	 * 
	 * @param newRoad Nueva <code>Road</code> entrante
	 */
	public void addNewIncomingRoad(Road newRoad) {
		incomingRoads.add(newRoad);
	}
	
	
	/**
	 * Método que devuelve la <code>Road</code> entre dos <code>Junctions</code>.
	 * La junction de origen es la actual.
	 * 
	 * @param toJunction <code>Junction</code> de destino
	 * @return <code>Road</code> entre las dos <code>Junctions</code>
	 * @throws SimulationException if <code>Road</code> between <code>Junctions</code> not found
	 */
	public Road getRoadTo(Junction junction) throws SimulationException {
		// Se recorren las carreteras de entrada a la intersección siguiente.
		Iterator<Road> toIt = junction.getIncomingRoads().iterator();
		while (toIt.hasNext()) {
			String toID = toIt.next().getID();
			
			if ( exitRoads.containsKey(toID) ) {
				return exitRoads.get(toID);
			}
		}
		
		throw new SimulationException(
			"Road not fot found between junctions with id: " + 
			id + ", " + junction.getID()
		);
	}
	
}
