package es.ucm.fdi.control.evbuild;

import java.util.ArrayList;
import java.util.List;

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
		// Se comprueba si es un NewCarVehicle
		if ( iniNameMatch(ini) && typeMatch(ini, type) ) {
			String id;
			int time = 0;
			int maxSpeed;
			int resistance;
			double faultyChance;
			int faultDuration;
			long seed;

			// ID ok?
			try{
				id = parseID(ini, "id");
			}
			catch(IllegalArgumentException e){
				throw new IllegalArgumentException(e + " in new Car.");
			}

			// TIME ok?
			if(existsTimeKey(ini)){
				try{
					time = parseNoNegativeInt(ini, "time");
				}
				catch(IllegalArgumentException e){
					throw new IllegalArgumentException(e + 
							" when reading time in car with id " + id);
				}
			}
			
			// MAXSPEED ok?
			try{
				maxSpeed = parseNoNegativeInt(ini, "max_speed");
			}
			catch(IllegalArgumentException e){
				throw new IllegalArgumentException(e + 
						" when reading maxSpeed in car with id " + id);
			}

			// TRIP ok?
			// Creación de la ruta de Junction IDs.
			List<String> trip;
			try{
				trip = parseIDList(ini, "itinerary", 2);
			}
			catch(IllegalArgumentException e){
				throw new IllegalArgumentException(e + 
						" when reading itinerary in car with id " + id);
			}

			// RESISTANCE ok?
			try {
				resistance = parsePositiveInt(ini, "resistance");
			}
			//La resistencia no era un entero
			catch (IllegalArgumentException e) {
				throw new IllegalArgumentException(e + 
						" when reading resistance in car with id " + id);
			}

			// FAULTY_CHANCE ok?
			try {
				faultyChance = parseProbability(ini, "fault_probability");
			}
			//La probabilidad de avería no era un real
			catch (NumberFormatException e) {
				throw new IllegalArgumentException(e +
						" when reading faulty chance in car with id " + id);
			}
			

			// FAULT_DURATION ok?
			try {
				faultDuration = parsePositiveInt(ini, "max_fault_duration");
			}
			//La duración de avería no era un entero
			catch (IllegalArgumentException e) {
				throw new IllegalArgumentException(e + 
						" when reading fault duration in car with id " + id);
			}

			// SEED ok?
			if (existsSeedKey(ini)) {
				try {
					seed = parseLong(ini, "seed");
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
