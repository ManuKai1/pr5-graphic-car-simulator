package es.ucm.fdi.model.SimObj;

import java.util.ArrayList;
import java.util.Iterator;

import es.ucm.fdi.ini.IniSection;
import es.ucm.fdi.model.simulation.SimulationException;

/**
 * Clase que representa un vehículo como un objeto de simulación
 */
public class Vehicle extends SimObject {

	/**
	 * Etiqueta que encabeza el informe de un <code>Vehicle</code> cualquiera.
	 */
	protected final String REPORT_TITLE = "[vehicle_report]";
	
	/**
	 * Ruta del <code>Vehicle</code> en forma de lista de <code>Junctions</code>.
	 */
	protected ArrayList<Junction> trip;

	/**
	 * Última posición en la lista que representa la ruta, de forma que
	 * <code>trip.get(lastTripPos)</code> es la última <code>Junction</code> por
	 * la que ha pasado el <code>Vehicle</code>.
	 */
	protected int lastTripPos;

	/**
	 * Máxima velocidad que puede alcanzar el <code>Vehicle</code> en 
	 * cualquier vía.
	 */
	protected int maxSpeed;

	/**
	 * Distancia recorrida por el <code>Vehicle</code> desde que empezo la
	 * simulación.
	 */
	protected int kilometrage;

	/**
	 * Tiempo restante hasta la recuperación de un <code>Vehicle</code> averiado. Si 
	 * <code>breakdownTime = 0</code>, no está averiado.
	 */
	protected int breakdownTime;

	/**
	 * Booleano que indica si el <code>Vehicle</code> ha llegado a si destino, es
	 * decir, a la última <code>Junction</code> de <code>trip</code>.
	 */
	protected boolean hasArrived;

	/**
	 * Booleano que indica si un <code>Vehicle</code> está esperando en la cola
	 * de una <code>Road</code> para cruzar una <code>Junction</code>.
	 */
	protected boolean isWaiting;

	/**
	 * <code>Road</code> en la que se encuentra el <code>Vehicle</code>.
	 */
	protected Road road;

	/**
	 * Localización del <code>Vehicle</code> dentro de la <code>road</code>.
	 */
	protected int location;

	/**
	 * Velocidad actual del coche en la <code>road</code>.
	 */
	protected int actualSpeed;	


	/**
	 * Constructor de <code>Vehicle</code>.
	 * 
	 * @param identifier identificador del objeto
	 * @param trp ruta de <code>Junctions</code>
	 * @param max máxima velocidad alcanzable
	 */
	public Vehicle(String identifier, ArrayList<Junction> trp, int max) {
		super(identifier);
		trip = trp;
		maxSpeed = max;

		// Valores iniciales.
		lastTripPos = 0;
		kilometrage = 0;
		breakdownTime = 0;

		hasArrived = false;
		isWaiting = false;

		// Se mete en la primera carretera.
		try {
			road = getRoadBetween( trip.get(lastTripPos), trip.get(lastTripPos + 1) );
			road.pushVehicle(this);
		}
		catch (SimulationException e) {
			System.err.println( e.getMessage() );
		}

		location = 0;
		actualSpeed = 0; // Irrelevante.
	}
	
	/**
	 * {@inheritDoc}
	 * Método de AVANCE de <code>Vehicle</code>:
	 * <p>
	 * En primer lugar, comprueba si el <code>Vehicle</code> está averiado. Si lo está, 
	 * se reduce su <code>breakdownTime</code> y no avanza. Si no lo está, se comprueba 
	 * si llegaría al final de la <code>Road</code> en este tick.
	 * </p> <p>
	 * Si es así, se le hace esperar en la <code>Junction</code>, en la cola 
	 * correspondiente a su <code>Road</code>. Si no, se modifica su 
	 * <code>location</code> sumándola su <code>actualSpeed</code>.
	 * </p>
	 */
	@Override
	public void proceed() {
		// Comprobamos primero si el vehículo está averiado o no
		if ( isFaulty() ) {
			breakdownTime--;
		}
		else {
			// Comprobamos si el vehículo llega al cruce.
			if ( location + actualSpeed >= road.getLength() ) {
				kilometrage += ( road.getLength() - location );
				waitInJunction();
			}
			else {
				location += actualSpeed;
				kilometrage += actualSpeed;
			}
		}		
	}

	/**
	 * <p>
	 * Saca a <code>Vehicle</code> de <code>road.vehiclesOnRoad</code> y lo
	 * introduce en <code>road.arrivalsToWaiting</code>.
	 * </p> <p>
	 * Queda a la espera de ser introducido en la cola del cruce 
	 * <code>road.waiting</code> una vez se hayan movido todos los 
	 * <code>Vehicle</code> de la <code>road</code>.
	 * </p>
	 */
	public void waitInJunction() {
		// Saca al vehículo de la zona de circulación de la Road
		road.popVehicle(this);
		
		// Cálculo del tiempo de llegada.
		float arrivalTime = ( actualSpeed / (road.getLength() - location) );
		// Se mete el Vehicle en la lista de llegados a la cola de espera.
		// Será introducido en road.waiting una vez que todos hayan llegado.
		road.arriveToWaiting(this, arrivalTime);	
		
		// Localización = longitud de Road
		location = road.getLength();
		isWaiting = true;
		actualSpeed = 0;
	}

	/**
	 * <p>
	 * Mueve el <code>Vehicle</code> a la siguiente <code>Road</code> que le 
	 * corresponde según su <code>trip</code>.
	 * </p> <p>
	 * El método falla si no encuentra ninguna <code>Road</code> entre las dos 
	 * <code>Junctions</code>
	 */
	public void moveToNextRoad() {
		int waitingPos = lastTripPos + 1; // Cruce donde estaba esperando
		int nextWaitingPos = waitingPos + 1; // Cruce donde debe acabar la siguiente road

		if ( nextWaitingPos == trip.size() ) {
			// Última vez. El cruce donde se espera es el destino final.
			hasArrived = true;
		}				 
		else {
			// Cambio normal de una road a otra.
			try {
				road = getRoadBetween(trip.get(waitingPos), trip.get(nextWaitingPos));
				road.pushVehicle(this);

				location = 0;
			} catch (SimulationException e) {
				System.err.println( e.getMessage() );
			}			
		}

		// Se ha pasado ya la siguiente Junction
		lastTripPos++;
		// El vehículo ya no está esperando
		isWaiting = false;
	}

	/**
	 * Método que devuelve la <code>Road</code> entre dos <code>Junctions</code>.
	 * 
	 * @param fromJunction <code>Junction</code> de origen
	 * @param toJunction <code>Junction</code> de destino
	 * @return <code>Road</code> entre las dos <code>Junctions</code>
	 * @throws SimulationException if <code>Road</code> between <code>Junctions</code> not found
	 */
	private Road getRoadBetween(Junction fromJunction, Junction toJunction) throws SimulationException {
		// Carreteras de salida y entrada.
		ArrayList<Road> fromRoads = fromJunction.getExitRoads();
		ArrayList<Road> toRoads = toJunction.getIncomingRoads();
		// Carretera buscada.
		Road searched = null;

		// Se recorren las carreteras de salida.
		boolean found = false;
		Iterator<Road> fromIt = fromRoads.iterator();
		while (fromIt.hasNext() && !found) {
			Road fromR = fromIt.next();

			// Se recorren las carreteras de entrada.
			Iterator<Road> toIt = toRoads.iterator();
			while (toIt.hasNext() && !found) {
				Road toR = toIt.next();
				if (toR == fromR) {
					found = true;
					searched = fromR;
				}
			}
		}

		if (found) {
			return searched;
		} 
		else {
			throw new SimulationException(
				"Road not fot found on route of vehicle with id: " + id + 
				" between junctions with id: " + 
				fromJunction.getID() + ", " + toJunction.getID()
			);
		}
	}
	
	/**
	 * Genera una <code>IniSection</code> que informa de los atributos del
	 * <code>Vehicle</code> en el timmpo del simulador.
	 * 
	 * @param simTime tiempo del simulador
	 * @return <code>IniSection</code> con información del <code>Vehicle</code>
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
		section.setValue("speed", actualSpeed);
		section.setValue("kilometrage", kilometrage);
		section.setValue("faulty", breakdownTime);
		section.setValue("location", hasArrived ? "arrived" : "(" + road.getID() + "," + location + ")");
		
		
		return section;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * En el caso de <code>Vehicle</code>, comprueba además todos los atributos
	 * correspondientes.
	 * </p>
	 * 
	 * @param obj objeto a comparar
	 * @return if <code>Vehicle</code> equals <code>obj</code>.
	 */
	public boolean equals(Object obj) {
		boolean same;
		same = super.equals(obj);

		if (same) {
			Vehicle other = (Vehicle) obj;

			same = (same && trip.equals(other.trip));
			same = (same && lastTripPos == other.lastTripPos);
			same = (same && maxSpeed == other.maxSpeed);
			same = (same && kilometrage == other.kilometrage);
			same = (same && breakdownTime == other.breakdownTime);
			same = (same && hasArrived == other.hasArrived);
			same = (same && isWaiting == other.isWaiting);
			same = (same && road.equals(other.road));
			same = (same && location == other.location);
			same = (same && actualSpeed == other.actualSpeed);
		}

		return same;
	}
	
	
	/**
	 * Añade más tiempo de avería al ya existente.
	 * 
	 * @param addedBreakdownTime tiempo de avería a sumar
	 */
	public void setBreakdownTime(int addedBreakdownTime)  {
		breakdownTime += addedBreakdownTime;
	}	
	
	/**
	 * Modifica la velocidad del <code>Vehicle</code> como el mínimo entre
	 * la velocidad permitida por la <code>Road</code> y la velocidad máxima 
	 * alcanzable por <code>Vehicle</code>.
	 * 
	 * @param roadSpeed velocidad permitida por la <code>Road</code>
	 */
	public void setSpeed(int roadSpeed) {
		if (breakdownTime == 0 ) {
			actualSpeed = Math.min(roadSpeed, maxSpeed);
		}
		else {
			actualSpeed = 0;
		}
	}	
	
	/**
	 * Devuelve el tiempo restante de avería del <code>Vehicle</code>
	 * 
	 * @return tiempo de avería
	 */
	public int getBreakdownTime() {
		return breakdownTime;
	}
	
	/**
	 * Devuelve si el <code>Vehicle</code> está esperando en la cola de la
	 * <code>Road</code> para cruzar una <code>Junction</code>.
	 * 
	 * @return si <code>Vehicle</code> está esperando.
	 */
	public boolean getIsWaiting() {
		return isWaiting;
	}
	
	/**
	 * Devuelve la <code>Road</code> en la que se encuentra el <code>Vehicle</code>.
	 * 
	 * @return la <code>Road</code> del <code>Vehicle</code>.
	 */
	public Road getRoad() {
		return road;
	}

	/**
	 * Devuelve la localización del <code>Vehicle</code> en la <code>Road</code>.
	 * 
	 * @return la localización del <code>Vehicle</code>
	 */
	public int getLocation() {
		return location;
	}
	
	/**
	 * Devuelve si el <code>Vehicle</code> está averiado.
	 * 
	 * @return si hay avería
	 */
	public boolean isFaulty() {
		return (breakdownTime > 0);
	}
	
	














	/**
	* Informe de el Vehicle en cuestión, mostrando: id, tiempo de simulación,
	* velocidad actual, kilometraje, tiempo de avería, localización, llegada a
	* destino
	*/
	@Override
	public String getReport(int simTime) {
		StringBuilder report = new StringBuilder();
		// TITLE
		report.append(REPORT_TITLE + '\n');
		// ID
		report.append("id = " + id + '\n');
		// SimTime
		report.append("time = " + simTime + '\n');
		// Velocidad actual
		report.append("speed = " + actualSpeed + '\n');
		// Kilometraje
		report.append("kilometrage = " + kilometrage + '\n');
		// Tiempo de avería
		report.append("faulty = " + breakdownTime + '\n');
		// Localización
		report.append("location = ");
		report.append(hasArrived ? "arrived" : "(" + road.getID() + "," + location + ")");

		return report.toString();
	}
	
}


