package es.ucm.fdi.control.evbuild;

import java.util.ArrayList;

import es.ucm.fdi.ini.IniSection;
import es.ucm.fdi.model.events.Event;
import es.ucm.fdi.model.events.FaultyVehicle;

/**
 * Clase que construye un evento <code>FaultyVehicle</code> utilizado para
 * averiar <code>Vehicles</code> durante la simulación.
 */
public class FaultyVehicleBuilder extends EventBuilder {
	
	/**
	 * Constructor de <code>FaultyVehicleBuilder</code> que pasa
	 * el parámetro <code>make_vehicle_faulty</code> al constructor de la
	 * superclase.
	 */
	public FaultyVehicleBuilder() {
		super("make_vehicle_faulty");
	}
	
	/**
	 * Método de <code>parsing</code> de <code>FaultyVehicleBuilder</code> que comprueba
	 * si la <code>IniSection</code> pasada como argumento representa un <code>FaultyVehicleEvent</code>
	 * y si sus parámetros son correctos.
	 * 
	 * @param ini <code>IniSection</code> a parsear.
	 * @return <code>FaultyVehicle</code> o <code>null</code>.
	 */
	@Override
	public Event parse(IniSection ini) {
		boolean match = false;

		// Se comprueba si es un FaultyVehicle
		if ( ini.getTag().equals(iniName) ) {
			match = true;
		}

		if (match) {
			int time = 0;
			int duration;

			// TIME ok?
			String timeKey = ini.getValue("time");
			if (timeKey != null) {
				try {
					time = Integer.parseInt(timeKey);
				}
				// El tiempo no era un entero
				catch (NumberFormatException e) {
					throw new IllegalArgumentException("Time reading failure in faulty vehicles.");
				}
				// Comprobamos que el tiempo sea no negativo
				if (time < 0) {
					throw new IllegalArgumentException("Negative time in faulty vehicles.");
				}
			}

			// DURATION ok?
			try {
				duration = Integer.parseInt(ini.getValue("duration"));
			}
			// La duracion no era un entero
			catch (NumberFormatException e) {
				throw new IllegalArgumentException("Duration reading failure in faulty vehicles.");
			}
			// Comprobamos que la duracion sea positiva
			if (duration <= 0) {
				throw new IllegalArgumentException("Non-positive duration in faulty vehicles.");
			}

			// VEHICLE_LIST ok?
			// Creación de la lista de vehículos.
			ArrayList<String> vehicles = new ArrayList<>();
			
			// Array de Strings con las IDs de los vehículos.
			String line = ini.getValue("vehicles");
			String[] input = line.split(",");

			// Comprobación de IDs.
			for (String idS : input) {
				if ( ! EventBuilder.validID(idS) ) {
					throw new IllegalArgumentException("Illegal vehicle ID: " + idS + " in faulty vehicles.");
				}
				vehicles.add(idS);
			}

			// Al menos un vehículo.
			if (vehicles.size() < 1) {
				throw new IllegalArgumentException("Less than one vehicle in faulty vehicles.");
			}

			// Faulty Vehicle.
			FaultyVehicle faulty = new FaultyVehicle(time, vehicles, duration);
			return faulty;
		}
		else return null;
	}
}
