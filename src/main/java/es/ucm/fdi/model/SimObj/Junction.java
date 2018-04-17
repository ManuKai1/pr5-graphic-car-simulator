package es.ucm.fdi.model.SimObj;

import java.util.ArrayList;

import es.ucm.fdi.ini.IniSection;
import es.ucm.fdi.model.simulation.SimulationException;

public class Junction extends SimObject {
	
	protected final String REPORT_TITLE = "[junction_report]";

	/**
	 * Lista de carreteras entrantes en el cruce.
	 */
	protected ArrayList<Road> incomingRoads;

	/**
	 * Lista de carreteras salientes en el cruce.
	 */
	protected ArrayList<Road> exitRoads;

	/**
	 * Entero que mediante la operación módulo representa el semáforo encendido.
	 */
	protected int light;	
	
	public Junction(String identifier) {
		super(identifier);
		// Listas vacías.
		incomingRoads = new ArrayList<>();
		exitRoads = new ArrayList<>();
		// Todos los semáforos en rojo al principio.
		light = -1;
	}

	/**
	 * Método de AVANCE de Junction. Provoca el paso de los vehículos 
	 * de la carretera entrante con el semáforo en verde. Finalmente, 
	 * se actualiza el semáforo circular.
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
	 * Genera una <code>IniSection</code> a partir de los datos de la
	 * <code>Junction</code>: <code>id, time, queues</code>
	 * 
	 * @param simTime tiempo del simulador
	 * @return informe <code>IniSection</code> de la <code>Junction</code>
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
		for (Road incR : incomingRoads) {
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
	 * Devuelve el ArrayList de carreteras entrantes
	 * @return arrayList de carreteras entrantes.
	 */
	public ArrayList<Road> getIncomingRoads() {
		return incomingRoads;
	}
	
	/**
	 * Comprueba si el cruce tiene carreteras entrantes.
	 */
	public boolean hasIncomingRoads() {
		return (incomingRoads.size() > 0);
	}

	/**
	 * Devuelve el ArrayList de carreteras salientes
	 * @return arrayList de carreteras salientes.
	 */
	public ArrayList<Road> getExitRoads() {
		return exitRoads;
	}
	
	public boolean equals(Object obj){
		boolean same;
		same = super.equals(obj);
		if(same){
			Junction other = (Junction) obj;
			same = same && light == other.light;
			same = same && incomingRoads.equals(other.incomingRoads);
			same = same && exitRoads.equals(other.exitRoads);
		}
		return same;
	}
	















	/**
	* WARNING: No se utiliza. Si se pretende utilizar, revisar el código del
	* método, pues seguramente contenga fallos.
	*/
	@Override
	public String getReport(int simTime) {
		StringBuilder report = new StringBuilder();
		// TITLE
		report.append(REPORT_TITLE + '\n');
		// ID
		report.append("id = " + id);
		// SimTime
		report.append("time = " + simTime);
		// Colas de espera
		report.append("queues = ");
		for (Road incR : incomingRoads) {
			report.append(incR.getWaitingState());
			report.append(",");
		}

		// Borrado de última coma
		if (report.length() > 0) {
			report.deleteCharAt(report.length() - 1);
		}

		return report.toString();
	}
}
