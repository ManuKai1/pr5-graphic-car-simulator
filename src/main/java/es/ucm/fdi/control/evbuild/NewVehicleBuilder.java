package es.ucm.fdi.control.evbuild;

import java.util.ArrayList;

import es.ucm.fdi.ini.IniSection;
import es.ucm.fdi.model.events.Event;
import es.ucm.fdi.model.events.NewVehicle;

/**
 * Clase que construye un evento <code>NewVehicle</code> utilizado para
 * crear un <code>Vehicle</code> en la simulación.
 */
public class NewVehicleBuilder extends EventBuilder{

	/**
	 * Constructor de <code>NewVehicleBuilder</code> que pasa
	 * el parámetro <code>new_vehicle</code> al constructor de la
	 * superclase.
	 */
	public NewVehicleBuilder(){
		super("new_vehicle");
	}
	
	/**
	 * Método de <code>parsing</code> de <code>NewVehicleBuilder</code> que comprueba
	 * si la <code>IniSection</code> pasada como argumento representa un <code>NewVehicle</code>
	 * y si sus parámetros son correctos.
	 * 
	 * @param ini <code>IniSection</code> a parsear.
	 * @return <code>NewVehicle</code> o <code>null</code>.
	 */
	@Override
	Event parse(IniSection ini) {
		boolean match = false;

		if ( ini.getTag().equals(iniName) && ini.getValue("type") == null) {
			match = true;
		}

		if (match) {
			String id = ini.getValue("id");
			int time = 0;
			int maxSpeed;

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

			// New Vehicle.
			NewVehicle vehicle = new NewVehicle(time, id, maxSpeed, trip);
			return vehicle;
		}
		else return null;
	}
}
