package es.ucm.fdi.control.evbuild;

import java.util.ArrayList;

import es.ucm.fdi.ini.IniSection;
import es.ucm.fdi.model.events.Event;
import es.ucm.fdi.model.events.NewCarVehicle;

/**
 * Clase que construye un evento <code>NewCarVehicle</code> utilizado para
 * crear un <code>CarVehicle</code> en la simulación.
 */
public class NewCarVehicleBuilder extends EventBuilder {

	private final String type = "car";

	/**
	 * Constructor de <code>NewCarVehicleBuilder</code> que pasa
	 * el parámetro <code>new_vehicle</code> al constructor de la
	 * superclase.
	 */
	public NewCarVehicleBuilder() {
		super("new_vehicle");
	}

	/**
	 * Método de <code>parsing</code> de <code>NewCarVehicleBuilder</code> que comprueba
	 * si la <code>IniSection</code> pasada como argumento representa un <code>NewCarVehicle</code>
	 * y si sus parámetros son correctos.
	 * 
	 * @param ini <code>IniSection</code> a parsear.
	 * @return <code>NewCarVehicle</code> o <code>null</code>.
	 */
	@Override
	Event parse(IniSection ini) {
		boolean match = false;

		// Se comprueba si es un NewCarVehicle
		if ( ini.getTag().equals(iniName) && ini.getValue("type").equals(type) ) {
			match = true;
		}

		if (match) {
			String id = ini.getValue("id");
			int time = 0;
			int maxSpeed;
			int resistance;
			double faultyChance;
			int faultDuration;
			long seed;



			// ID ok?
			if ( ! EventBuilder.validID(id) ) {
				throw new IllegalArgumentException("Illegal vehicle ID: " + id);
			}

			// TIME ok?
			String timeKey = ini.getValue("time");
			if (timeKey != null) {
				try {
					time = Integer.parseInt(timeKey);
				}
				// El tiempo no era un entero
				catch (NumberFormatException e) {
					throw new IllegalArgumentException("Time reading failure in vehicle with ID: " + id);
				}
				// Comprobamos que el tiempo sea no negativo
				if (time < 0) {
					throw new IllegalArgumentException("Negative time in vehicle with ID: " + id);
				}
			}

			// MAXSPEED ok?
			try {
				maxSpeed = Integer.parseInt(ini.getValue("max_speed"));
			}
			//La velocidad no era un entero
			catch (NumberFormatException e) {
				throw new IllegalArgumentException("Max speed reading failure in vehicle with ID: " + id);
			}
			//Comprobamos que la velocidad sea positiva
			if (maxSpeed <= 0) {
				throw new IllegalArgumentException("Non-positive speed in vehicle with ID: " + id);
			}

			// TRIP ok?
			// Creación de la ruta de Junction IDs.
			ArrayList<String> trip = new ArrayList<>();

			// Array de Strings con las IDs de los vehículos.
			String line = ini.getValue("itinerary");
			String[] input = line.split(",");

			// Comprobación de IDs.
			for (String idS : input) {
				if ( ! EventBuilder.validID(idS) ) {
					throw new IllegalArgumentException("Illegal junction ID: " + idS + " in vehicle trip, with ID: " + id);
				}
				trip.add(idS);
			}

			// Al menos 2 Junctions.
			if (trip.size() < 2) {
				throw new IllegalArgumentException("Less than two junctions in vehicle with ID: " + id);
			}

			// RESISTANCE ok?
			try {
				resistance = Integer.parseInt( ini.getValue("resistance") );
			}
			//La resistencia no era un entero
			catch (NumberFormatException e) {
				throw new IllegalArgumentException("Resistance reading failure in car with ID: " + id);
			}
			if (resistance <= 0) {
				throw new IllegalArgumentException("Resistance is not positive in car with ID: " + id);
			}

			// FAULTY_CHANCE ok?
			try {
				faultyChance = Double.parseDouble(ini.getValue("fault_probability"));
			}
			//La probabilidad de avería no era un real
			catch (NumberFormatException e) {
				throw new IllegalArgumentException("Fault probability reading failure in car with ID: " + id);
			}
			if (faultyChance < 0 || faultyChance > 1) {
				throw new IllegalArgumentException("Fault probability is out of bounds [0,1] in car with ID: " + id);
			}

			// FAULT_DURATION ok?
			try {
				faultDuration = Integer.parseInt(ini.getValue("max_fault_duration"));
			}
			//La duración de avería no era un entero
			catch (NumberFormatException e) {
				throw new IllegalArgumentException("Fault duration reading failure in car with ID: " + id);
			}
			if (faultDuration <= 0) {
				throw new IllegalArgumentException("Fault duration is a non-positive number in car with ID: " + id);
			}

			// SEED ok?
			String seedKey = ini.getValue("seed");
			if (seedKey != null) {
				try {
					seed = Long.parseLong(seedKey);
				}
				//La semilla no era un long
				catch (NumberFormatException e) {
					throw new IllegalArgumentException("Seed reading failure in car with ID: " + id);
				}
			}
			else {
				seed = System.currentTimeMillis();
			}

			// New Car Vehicle.
			return new NewCarVehicle(time, id, maxSpeed, trip, resistance, faultyChance, faultDuration, seed);
		}
		else return null;
	}

}
