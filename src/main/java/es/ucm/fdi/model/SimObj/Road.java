package es.ucm.fdi.model.SimObj;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import es.ucm.fdi.ini.IniSection;
import es.ucm.fdi.model.simulation.SimulationException;
import es.ucm.fdi.util.TableDataType;

/**
 * Clase que representa una carretera como un objeto
 * de simulación. Hereda de {@link SimObject}
 */
public class Road extends SimObject {
	
	/**
	 * Etiqueta que encabeza el informe de una 
	 * <code>Road</code> cualquiera.
	 */
	protected final String REPORT_TITLE = "[road_report]";

	/**
	 * Información sobre el tipo de carretera que
	 * debe ponerse como valor en la clave <code>type</code>
	 * de la <code>IniSection</code> generada.
	 */
	private static final String TYPE = ""; // carretera normal
	
	/**
	 * Longitud de la <code>Road</code>.
	 */
	private int length;

	/**
	 * Límite de velocidad para los 
	 * <code>Vehicles</code> en la <code>Road</code>.
	 */
	protected int speedLimit;

	/**
	 * <code>Junction</code> donde empieza 
	 * la <code>Road</code>.
	 */
	private Junction fromJunction;

	/**
	 * <code>Junction</code> donde acaba 
	 * la <code>Road</code>.
	 */
	private Junction toJunction;
	
	/**
	 * Lista de <code>Vehicles</code>> ordenada por orden 
	 * de entrada en la <code>Road</code>. Utilizada para 
	 * el caso en que dos <code>Vehicles</code> se encuentran 
	 * en la misma posición.
	 */
	private Deque<Vehicle> entryRecord = new ArrayDeque<>();

	/**
	 * Lista de <code>Vehicles</code> en la <code>Road</code> 
	 * que no están esperando a cruzar la <code>toJunction</code>.
	 */
	protected List<Vehicle> vehiclesOnRoad = new ArrayList<>();

	/**
	 * Lista temporal reutilizada en cada tick en la que 
	 * se ordenan los <code>Vehicles</code> que llegan a 
	 * <code>toJunction</code> por tiempo de llegada.
	 */
	private List<ArrivedVehicle> arrivalsToWaiting = new ArrayList<>();

	/**
	 * Lista de <code>Vehicles</code> en la <code>Road</code> 
	 * que están  esperando para cruzar <code>toJunction</code>.
	 */
	private Deque<Vehicle> waiting = new ArrayDeque<>();

	/**
	 * Booleano que indica si el semáforo de la 
	 * <code>toJunction</code> está verde para la <code>Road</code>.
	 */
	private boolean isGreen = false;
	
	/**
	 * Comparador según la localización de 2 <code>Vehicles</code>
	 * en la <code>carretera</code, para ordenar 
	 * <code>vehiclesOnRoad</code> tras cada avance de los 
	 * <code>Vehicles</code>.
	 */
	private static class CompByLocation implements Comparator<Vehicle> {
		
		Deque<Vehicle> entries;		

		public CompByLocation(Road road) {
			entries = road.getEntryRecord();
		}

		@Override
		public int compare(Vehicle v1, Vehicle v2) {
			int dist = v2.getLocation() - v1.getLocation();

			if (dist != 0) {
				return dist;
			}
			else {
				// Están en la misma posición, se ordena por orden 
				// de entrada en carretera.
				for (Vehicle v : entries ) {
					if (v == v1) {
						return -1;
					}
					if (v == v2) {
						return 1;
					}
				}

				// Fallo del programa (no se debería dar)
				throw new RuntimeException("Vehicles weren´t recorded when entered their road.");
			}
		}

		// ROADEND - (v1, 80) < (v2, 78) < (v3, 50) < (v4, 20) - ROADBEGIN
	}

	/**
	 * Comparador según el tiempo de llegada al final 
	 * de la <code>Road</code>, para ordenar los 
	 * <code>arrivedVehicles</code> según su tiempo de llegada.
	 */
	private static class CompArrivedVehicles implements Comparator<ArrivedVehicle> {
		
		@Override
		public int compare(ArrivedVehicle av1, ArrivedVehicle av2) {
			// Si av1.time < av2.time -> av1 < av2
			float diff = av1.getTime() - av2.getTime();
			
			int intDiff;
			if ( diff < 0 ) intDiff = -1;
			else intDiff = 1;
			
			return intDiff;
		}

		// ROADEND - (v1, 0.1s) < (v2, 0.5s) < (v3, 2s) < (v4, 3s) - ROADBEGIN
	}

	/**
	 * Clase interna que guarda cada <code>Vehicle</code> con 
	 * su tiempo de llegada al final de la <code>Road</code>.
	 */
	private class ArrivedVehicle {
		private Vehicle arrived;
		private float time;

		public ArrivedVehicle(Vehicle arr, float t) {
			arrived = arr;
			time = t;
		}

		public Vehicle getArrived() {
			return arrived;
		}
		
		public float getTime() {
			return time;
		}
	}
		
	/**
	 * Constructor de {@link Road}.
	 * 
	 * @param identifier 	identificador del objeto
	 * @param len 			longitud de la vía
	 * @param spLimit 		límite de velocidad
	 * @param fromJ 		<code>Junction</code> donde empieza
	 * @param toJ 			<code>Junction</code> donde acaba
	 */
	public Road(String identifier, int len, int spLimit, 
			Junction fromJ, Junction toJ) {
		super(identifier);
		length = len;
		speedLimit = spLimit;
		fromJunction = fromJ;
		toJunction = toJ;
		
		// Actualización de cruces afectados.
		getInOwnJunctions();	
	}

	/**
	 * {@inheritDoc}
	 * Método de AVANCE de <code>Road</code>.
	 * <p>
	 * En primer lugar, modifica la velocidad que llevarán 
	 * los <code>Vehicles</code> durante el avance, teniendo 
	 * en cuenta factores de la <code>Road</code>. 
	 * </p> <p>
	 * En segundo lugar, provoca el avance de los <code>Vehicles</code> 
	 * en la <code>Road</code> y los reordena si ha habido adelantamientos. 
	 * </p> <p> 
	 * Finalmente, introduce a los <code>Vehicles</code> que han llegado
	 * al final de la <code>Road</code> en la cola de espera 
	 * <code>waiting</code>.
	 * </p>
	 */
	@Override
	public void proceed() {
		// * //
		// Se crea lista con los vehículos en la 
		// carretera en ese momento, pues pueden salir 
		// durante su proceed y provocar un error en 
		// el foreach
		ArrayList<Vehicle> onRoad = new ArrayList<>();
		for (Vehicle v : vehiclesOnRoad) {
			onRoad.add(v);
		}

		// 1 //
		// Se modifica la velocidad a la que avanzarán los 
		// vehículos, teniendo en cuenta el factor de reducción.
		vehicleSpeedModifier(onRoad);

		// 2 //
		// Los vehículos avanzan y se pueden adelantar.
		for (Vehicle v : onRoad) {
			v.proceed();
		}
		vehiclesOnRoad.sort(new CompByLocation(this));

		// 3 //
		// Los coches que llegan al final entran 
		// por orden en la cola de espera.
		pushArrivalsToWaiting();
	}

	/**
	 * Calcula la velocidad base de la <code>Road</code>: 
	 * el mínimo entre el <code>speedLimit</code> y la 
	 * velocidad que permite la congestión del tráfico en
	 * la <code>Road</code>.
	 * 
	 * @return 	la velocidad base de 
	 * 			la <code>Road</code>.
	 */
	protected int getBaseSpeed() {
		// Cálculo de velocidadBase según la fórmula
		int congestionSpeed = ( speedLimit / Math.max(vehiclesOnRoad.size(), 1) ) + 1;

		return ( Math.min(speedLimit, congestionSpeed) );
	}

	/**
	 * Modifica la velocidad que llevarán los <code>Vehicles</code>
	 * en la <code>Road</code> previo avance.
	 * 
	 * @param onRoad 	lista de <code>Vehicles</code> 
	 * 					en <code>Road</code>
	 */
	protected void vehicleSpeedModifier(ArrayList<Vehicle> onRoad) {
		// Velocidad máxima a la que pueden avanzar los vehículos.
		int baseSpeed = getBaseSpeed();
		
		// Factor de reducción de velocidad en caso de obstáculos delante.
		int reductionFactor = 1;

		// Se modifica la velocidad a la que avanzarán los vehículos,
		// teniendo en cuenta el factor de reducción.
		for (Vehicle v : onRoad) {
			v.setSpeed(baseSpeed / reductionFactor);

			if (v.getBreakdownTime() > 0) {
				reductionFactor = 2;
			}
		}
	}

	/**
	 * Inserta los <code>Vehicles</code> que han llegado al 
	 * final de la  <code>Road</code> en <code>waiting</code>, 
	 * ordenados por tiempo de llegada.
	 */
	public void pushArrivalsToWaiting() {
		// Se hace cuando han avanzado todos los coches.
		arrivalsToWaiting.sort(new CompArrivedVehicles());

		// Se insertan ordenados en la cola de espera.
		for (ArrivedVehicle av : arrivalsToWaiting) {
			waiting.addLast(av.getArrived());
		}

		// Se vacía el array para el siguiente tick
		arrivalsToWaiting.clear();
	}

	/**
	 * Guarda un <code>Vehicle</code> y su tiempo de llegada 
	 * en la lista de <code>Vehicles</code> que van a entrar
	 * en <code>waiting</code>.
	 * 
	 * @param toWait 		<code>Vehicle</code> que va a 
	 * 						entrar a la cola de espera
	 * @param arrivalTime 	tiempo que ha tardado en llegar 
	 * 						al final en el tick actual
	 */
	public void arriveToWaiting(Vehicle toWait, float arrivalTime) {
		// Se guarda en el Map su información de llegada.
		arrivalsToWaiting.add(new ArrivedVehicle(toWait, arrivalTime));
	}	

	/**
	 * Mueve el primer <code>Vehicle</code> a la espera en la 
	 * <code>Junction</code> de salida de la <code>Road</code>
	 * a su correspondiente <code>Road</code> indicada por la ruta.
	 * 
	 * @return 	si ha cruzado el 
	 * 			<code>Vehicle</code>
	 * 
	 * @throws SimulationException 	si la <code>Road</code> 
	 * 								está en rojo
	 */
	public boolean moveWaitingVehicle() throws SimulationException {
		if ( isGreen ) {
			boolean hasCrossed = false;

			// Primer vehículo que está esperando.
			Vehicle toMove = waiting.getFirst();

			// Si hay algún vehículo y no está averiado.
			if (toMove != null && toMove.getBreakdownTime() == 0) {
				// Se le saca de la lista de espera y del registro de entradas. 
				entryRecord.remove(toMove);
				waiting.pollFirst();

				// Se mueve a la siguiente carretera.
				toMove.moveToNextRoad();

				hasCrossed = true;
			}

			return hasCrossed;
		}
		else {
			throw new SimulationException(
				"Tried to advance waiting vehicle with red traffic lights" + 
				" in road with id: " + id
			);
		}	
	}

	/**
	 * Actualiza el estado de los <code>Vehicles>/code>
	 * averiados en la  cola de espera <code>waiting</code>.
	 */
	public void refreshWaiting() {
		for ( Vehicle v : waiting ) {
			if ( v.getBreakdownTime() > 0 ) {
				v.setBreakdownTime(-1); // Se resta un día.
			}
		}
	}

	/**
	 * <p>
	 * Devuelve un <code>StringBuilder</code> con el estado 
	 * de la <code>Road</code>.
	 * </p> <p>
	 * Ejemplo:
	 * </p> <p>
	 * (v1, 80), (v3, 80), (v2, 76), (v5, 33)
	 * </p>
	 * 
	 * @return 	<code>StringBuilder</code> with 
	 * 			state of <code>Road</code>
	 */
	public StringBuilder getRoadState() {
		StringBuilder state = new StringBuilder();

		// Primero los vehículos en la cola de espera.
		for (Vehicle v : waiting) {
			// ID
			state.append("(" + v.getID());
			// Location
			state.append("," + v.getLocation());

			state.append("),");
		}

		// Después los vehículos en la carretera.
		for (Vehicle v : vehiclesOnRoad) {
			// ID
			state.append("(" + v.getID());
			// Location
			state.append("," + v.getLocation());

			state.append("),");
		}
		
		// Borrado de última coma
		if (state.length() > 0) {
			state.deleteCharAt(state.length() - 1);
		}

		return state;
	}

	/**
	 * <p>
	 * Devuelve un <code>StringBuilder</code> con el estado de 
	 * la cola de espera <code>waiting</code> de <code>Road</code>.
	 * </p> <p>
	 * Ejemplo:
	 * </p> <p>
	 * (r2,red,[v3,v2,v5])
	 * 
	 * @return 	<code>StringBuilder</code> with 
	 * 			state of <code>waiting</code>
	 */
	public StringBuilder getWaitingState() {
		StringBuilder state = new StringBuilder();
		// ID
		state.append("(" + getID() + ",");
		// Semáforo
		state.append(isGreen ? "green" : "red");
		// Cola de espera
		state.append(",[");
		if (waiting.isEmpty()) {
			state.append("]");
		} else {
			for (Vehicle v : waiting) {
				state.append(v.getID() + ",");
			}
			if (waiting.size() > 0) {
				state.deleteCharAt(state.length() - 1);
			}
			state.append("]");
		}
		state.append(")");

		return state;
	}

	/**
	 * <p>
	 * Devuelve un <code>StringBuilder</code> con el estado de 
	 * la cola de espera <code>waiting</code> de <code>Road</code>, 
	 * guardando también el tiempo restante que le queda al semáforo 
	 * en verde para ponerse rojo, <code>lightTime</code>.
	 * </p> <p>
	 * Ejemplos:
	 * </p> <p>
	 * (r2,red,[v3,v2,v5])
	 * </p> <p>
	 * (r4,green:4,[v1,v6])
	 * 
	 * @param lightTime tiempo restante que el semáforo estará encendido
	 * @return <code>StringBuilder</code> with state of <code>waiting</code>
	 */
	public StringBuilder getWaitingState(int lightTime) {
		StringBuilder state = new StringBuilder();
		// ID
		state.append("(" + getID() + ",");
		// Semáforo
		state.append(isGreen ? "green" : "red");
		// Tiempo de semáforo
		state.append(":" + lightTime);
		// Cola de espera
		state.append(",[");
		if (waiting.isEmpty()) {
			state.append("]");
		} else {
			for (Vehicle v : waiting) {
				state.append(v.getID() + ",");
			}
			if (waiting.size() > 0) {
				state.deleteCharAt(state.length() - 1);
			}
			state.append("]");
		}
		state.append(")");

		return state;
	}

	/**
	 * Genera una <code>IniSection</code> que informa de los 
	 * atributos de la <code>Road</code> en el tiempo del simulador.
	 * 
	 * @param simTime 	tiempo del simulador
	 * @return 			<code>IniSection</code> con 
	 * 					información de la <code>Road</code>
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
		section.setValue("state", getRoadState().toString());
		
		
		return section;
	}
	
	/**
	 * Método que actualiza la información de <code>fromJunction,
	 * toJunction</code> para que incluyen a la instancia 
	 * <code>Road</code> en sus listas <code>incomingRoads, exitRoads</code>.
	 */
	private void getInOwnJunctions() {
		// fromJunction.getExitRoads().add(this);
		// toJunction.getIncomingRoads().add(this);

		fromJunction.addNewExitRoad(this);
		toJunction.addNewIncomingRoad(this);
	}

	/**
	 * Mete un <code>Vehicle</code> al final de 
	 * <code>vehiclesOnRoad</code>.
	 * 
	 * @param v 	<code>Vehicle</code> a 
	 * 				añadir al final
	 */
	public void pushVehicle(Vehicle v) {
		vehiclesOnRoad.add(v);

		// Se guarda el último de la lista en 
		// el registro de entradas, pues ha sido
		// el último en entrar.
		entryRecord.add(v);
	}

	/**
	 * Saca un <code>Vehicle</code> de <code>vehiclesOnRoad</code>.
	 * 
	 * @param v 	<code>Vehicle</code> a quitar
	 * 
	 * @throws NoSuchElementException 	si <code>v</code> no está 
	 * 									en <code>vehiclesOnRoad</code>
	 */
	public void popVehicle(Vehicle v) throws NoSuchElementException {
		if ( ! vehiclesOnRoad.remove(v) ) {
			throw new NoSuchElementException(
				"Vehicle to pop not found."
			);
		}
	}	
	
	/**
	 * Devuelve la longitud de la vía.
	 * 
	 * @return longitud de la vía
	 */
	public int getLength() {
		return length;
	}

	/**
	 * Devuelve si el semáforo está en verde.
	 * 
	 * @return si el semáforo está en verde
	 */
	public boolean isGreen() {
		return isGreen;
	}

	/**
	 * Devuelve si la cola de espera <code>waiting</code> está vacía.
	 * 
	 * @return si <code>waiting</code> está vacía
	 */
	public boolean noVehiclesWaiting() {
		return waiting.isEmpty();
	}

	/**
	 * Método de modificación del estado del semáforo
	 * 
	 * @param green nuevo estado del semáforo
	 */
	public void setLight(boolean green) {
		isGreen = green;
	}

	/**
	 * Devuelve la <code>Junction</code> desde la que 
	 * empieza la <code>Road</code>.
	 * 
	 * @return <code>fromJunction</code>
	 */
	public Junction getFromJunction() {
		return fromJunction;
	}

	/**
	 * Devuelve la <code>Junction</code> donde acaba la
	 * <code>Road</code>.
	 * 
	 * @return <code>toJunction</code>
	 */
	public Junction getToJunction() {
		return toJunction;
	}

	/**
	 * Devuelve la lista de registro de entradas de 
	 * los <code>Vehicles</code> que están en la 
	 * <code>Road</code>
	 * 
	 * @return <code>entryRecord</code>
	 */
	public Deque<Vehicle> getEntryRecord() {
		return entryRecord;
	}

	/**
	 * Devuelve el número de <code>Vehicles</code> esperando
	 * en la cola <code>waiting</code>.
	 * 
	 * @return número de <code>Vehicles</code> esperando.
	 */
	public int getNumWaitingVehicles() {
		return waiting.size();
	}


	/**
	 * {@inheritDoc}
	 * Añade una <code>Road</code> al map, con los datos:
	 * id, source, target, length, max speed, vehicles
	 * 
	 * @param out {@inheritDoc}
	 */
	@Override
	public void describe(Map<TableDataType, String> out) {
		String source = fromJunction.getID();
		String target = toJunction.getID();
		String length = Integer.toString(this.length);
		String maxSpeed = Integer.toString(this.speedLimit);
		String state = getRoadStateDescription();


		out.put(TableDataType.ID, id);
		out.put(TableDataType.R_TYPE, TYPE);
		out.put(TableDataType.R_SOURCE, source);
		out.put(TableDataType.R_TARGET, target);
		out.put(TableDataType.R_LENGHT, length);
		out.put(TableDataType.R_MAX, maxSpeed);
		out.put(TableDataType.R_STATE, state);
	}

	private String getRoadStateDescription() {
		StringBuilder state = new StringBuilder();

		state.append("[");
		// Primero los vehículos en la cola de espera.
		for (Vehicle v : waiting) {
			// ID
			state.append( v.getID() );
			state.append(",");
		}

		// Después los vehículos en la carretera.
		for (Vehicle v : vehiclesOnRoad) {
			// ID
			state.append(v.getID());
			state.append(",");
		}

		// Borrado de última coma (mín "[")
		if (state.length() > 1) {
			state.deleteCharAt(state.length() - 1);
		}

		state.append("]");

		return 	state.toString();
	}


	public List<Vehicle> getRoadVehicles() {
		List<Vehicle> list = new ArrayList<>();

		list.addAll(vehiclesOnRoad);
		list.addAll(waiting);

		return list;
	}

}
