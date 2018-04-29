package es.ucm.fdi.model.simulation;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import es.ucm.fdi.ini.Ini;
import es.ucm.fdi.model.SimObj.Junction;
import es.ucm.fdi.model.SimObj.Road;
import es.ucm.fdi.model.SimObj.Vehicle;
import es.ucm.fdi.model.events.Event;
import es.ucm.fdi.util.EventType;
import es.ucm.fdi.util.MultiTreeMap;

/**
 * Clase que representa el simulador de tráfico, almacenando los <code>Events</code>
 * de la simulación, así como los objetos de simulación en un <code>RoadMap</code>, y el
 * tiempo de actual de la simulación.
 */
public class TrafficSimulation {

	// Clases internas para manejo de eventos
	
	public interface Listener {
		void update(UpdateEvent ue, String error);
	}
	
	public class UpdateEvent {
		
		EventType event;
		
		public UpdateEvent(EventType ev){
			event = ev;
		}
		
		public EventType getEvent() {
			return event;
		}
		
		public RoadMap getRoadMap() {
			return roadMap;
		}
		
		public MultiTreeMap<Integer, Event> getEventQueue() {
			return events;
		}
		
		public int getCurrentTime() {
			return time;
		}
	}

	
	/**
	 * Mapa de eventos donde: Integer representa el tiempo de
	 * ejecución de un evento, Event para añadir listas de eventos
	 * que se ejecutan en ese tiempo.
	 */
	private MultiTreeMap<Integer, Event> events = new MultiTreeMap<>();

	/**
	 * Lista con los listeners registrados en el simulador.
	 */
	private List<Listener> listeners = new ArrayList<>();

	/**
	 * Mapa de simulación que relaciona Junction con sus carreteras entrantes
	 * y salientes.
	 */
	RoadMap roadMap = new RoadMap();

	/**
	 * Tiempo actual de la simulación.
	 */
	private int time = 0;
	
	public TrafficSimulation() {}
	
	/**
	 * Añade un evento al mapa de <code>Events</code>> de la simulación, 
	 * comprobando que el tiempo del <code>Evente</code> sea mayor que el 
	 * de la simulación.
	 * 
	 * @param e <code>Event</code> a añadir
	 * @throws IllegalArgumentException	 if event time lower thar sim time
	 */
	public void pushEvent(Event e) 
			throws IllegalArgumentException {
		
				// Comprueba el tiempo.
		if( e.getTime() < time ) {
			throw new IllegalArgumentException(
				"Event time is lower than current time."
			);
		}

		// Añade el evento al mapa.
		events.putValue(e.getTime(), e);
		fireUpdateEvent(EventType.NEW_EVENT, "New Event error.");
	}
	
	/**
	 * Simula un número determinado de ticks y guarda el fichero de salida
	 * de esta ejecución.
	 * 
	 * @param steps número de pasos a ejecutar
	 * @param file fichero de salida
	 * 
	 * @throws SimulationException 	if an error ocurred while
	 * 								executing an event
	 * @throws IOException 			if an IO error ocurred during
	 * 								reports generation
	 */
	public void execute(int steps, OutputStream file) 
			throws IOException, SimulationException {
		// * //
		// Tiempo límite en que para la simulación.
		int timeLimit = time + steps - 1;

		// ** //
		// Bucle de la simulación.
		while (time <= timeLimit) {
			// 1 // EVENTOS //
			// Se ejecutan los eventos correspondientes a ese tiempo.			
			try {
				executeEvents();
			} catch (SimulationException e1) {
				fireUpdateEvent(EventType.ERROR, "Simulation Exception error");
				throw e1;
			}
			
			// 2 // SIMULACIÓN //
			proceedAll();
			
			//Aviso a Listeners de avance
			fireUpdateEvent(EventType.ADVANCED, "Advanced error");
			
			// Se avanza un tick.
			time++;

			// 3 // INFORME //
			// Escribir un informe en OutputStream en caso de que no sea nulo
			try {
				generateReports(file);
			}
			catch (IOException e) {
				throw e;
			}

		}
	}
	
	/**
	 * Llama a los métodos de avance de carreteras
	 * y de cruces.
	 * 
	 * @throws SimulationException	if event tried to create
	 * 								a SimObj with an already
	 * 								existing ID
	 * @throws SimulationException	if event tried to interact
	 * 								with a non-existint SimObj
	 */
	private void executeEvents() throws SimulationException{
		if ( events.get(time) != null ) {
			for ( Event event : events.get(time) ) {
				try {
					event.execute(this);
					
				}
				catch (AlreadyExistingSimObjException e1) {
					throw new SimulationException(
						"Simulation error:\n" + e1
					);
				}
				catch (NonExistingSimObjException e2) {
					throw new SimulationException(
						"Simulation error:\n" + e2
					);
				}		
			}
		}
	}
	
	/**
	 * Llama a los métodos de avance de carreteras
	 * y de cruces.
	 */
	private void proceedAll(){
		// Para cada carretera, los coches que no están esperando avanzan.
		for ( Road road : roadMap.getRoads().values() ) {
			road.proceed();
		}

		// Para cada cruce, avanzan los vehículos a la espera que puedan y se actualiza 
		// el semáforo y los tiempos de avería de los vehículos a la espera.
		for ( Junction junction : roadMap.getJunctions().values() ) {
			junction.proceed();			
		}
	}
	
	/**
	 * Genera informes de todos los SimObject.
	 * @param file fichero de salida
	 */
	private void generateReports(OutputStream file) 
			throws IOException {
		
		if (file != null) {
			//Creación de ini
			Ini iniFile = new Ini();
			//Junctions:
			for (Junction junction : roadMap.getJunctions().values() ) {
				iniFile.addsection(junction.generateIniSection(time));
			}
			//Roads:
			for (Road road : roadMap.getRoads().values() ) {
				iniFile.addsection(road.generateIniSection(time));
			}
			//Vehicles:
			for (Vehicle vehicle : roadMap.getVehicles().values() ) {
				iniFile.addsection(vehicle.generateIniSection(time));
			}
			
			// Guardado en el outputStream
			try{
				iniFile.store(file);
			}
			catch (IOException e) {
				throw new IOException(
					"Error when saving file on time " + time + ":" + e.getMessage()
				);
			}
		}
	}
	
	/**
	 * Añade tiempo de avería a los <code>Vehicles</code> con los ID de la lista.
	 * Además comprueba que existan los <code>Vehicles</code> referenciados 
	 * por esos IDs.
	 * 
	 * @param vehiclesID lista de IDs de los <code>Vehicles</code> a averiar
	 * @param breakDuration duración del tiempo de avería a añadir
	 */
	public void makeFaulty(List<String> vehiclesID, int breakDuration) throws NonExistingSimObjException {
		for ( String id : vehiclesID ) {
			Vehicle toBreak = roadMap.getVehicleWithID(id);

			if ( toBreak != null ) {
				toBreak.setBreakdownTime(breakDuration);
			}
			else {
				throw new NonExistingSimObjException(
					"Vehicle with id: " + id + " to make faulty not found."
				);
			}
		}
	}

	/**
	 * Añade un <code>Vehicle</code> al <code>roadMap</code>.
	 * 
	 * @param newVehicle <code>Vehicle</code> a añadir
	 */

	public void addVehicle(Vehicle newVehicle) {
		// Se guarda en el inventario de objetos de simulación.
		roadMap.addVehicle(newVehicle);
	}

	/**
	 * Añade una <code>Road</code> al <code>roadMap</code>.
	 * 
	 * @param newRoad <code>Road</code> a añadir
	 */
	public void addRoad(Road newRoad) {
		// Se mete en el RoadMap.
		roadMap.addRoad(newRoad);
	}

	/**
	 * Añade una <code>Junction</code> al <code>roadMap</code>.
	 * 
	 * @param newJunction <code>Junction</code> a añadir
	 */
	public void addJunction(Junction newJunction) {
		// Se mete en el RoadMap
		roadMap.addJunction(newJunction);
	}

	/**
	 * @return el RoadMap del simulador
	 */
	public RoadMap getRoadMap(){
		return roadMap;
	}

	/**
	 * 
	 */
	public int getCurrentTime() {
		return time;
	}

	public MultiTreeMap<Integer, Event> getEvents() { 
		return events;
	}





	/**
	 *  Añade un listener a la lista (además, implementa registered).
	 *  @param l es el listener a añadir
	 */
	public void addSimulatorListener(Listener l) {
		listeners.add(l);
		UpdateEvent ue = new UpdateEvent(EventType.REGISTERED);
		// evita pseudo-recursividad
		// Error?
		SwingUtilities.invokeLater(() -> l.update(ue, "Registered error."));
	}

	/**
	 *  Elimina un listener de la lista.
	 *  @param l es el listener a eliminar
	 */
	public void removeListener(Listener l) {
		listeners.remove(l);
	}

	/**
	 * Método de uso interno que informa a todos los
	 * listeners registrados de un EventType en simulación.
	 */
	private void fireUpdateEvent(EventType type, String error) {
		UpdateEvent ue = new UpdateEvent(type);
		for (Listener l : listeners) {
			l.update(ue, error);
		}
	}

	/**
	 * Reinicia el simulador
	 */
	public void reset() {
		events.clear();
		roadMap.clear();
		time = 0;
		fireUpdateEvent(EventType.RESET, "Reset error");
	}
}
