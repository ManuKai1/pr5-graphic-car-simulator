package es.ucm.fdi.control.evbuild;

import java.util.List;
import java.util.ArrayList;

import es.ucm.fdi.ini.IniSection;
import es.ucm.fdi.model.events.Event;
import es.ucm.fdi.model.events.NewBikeVehicle;

/**
 * Clase que construye un evento <code>NewBikeVehicle</code> utilizado para
 * crear un <code>BikeVehicle</code> en la simulación.
 */
public class NewBikeVehicleBuilder extends EventBuilder {
	
	private final String type = "bike";

	/**
	 * Constructor de <code>NewBikeVehicleBuilder</code> que pasa
	 * el parámetro <code>new_vehicle</code> al constructor de la
	 * superclase.
	 */
	public NewBikeVehicleBuilder(){
		super("new_vehicle");
	}
	
	/**
	 * Método de <code>parsing</code> de <code>NewBikeVehicleBuilder</code> que comprueba
	 * si la <code>IniSection</code> pasada como argumento representa un <code>NewBikeVehicle</code>
	 * y si sus parámetros son correctos.
	 * 
	 * @param ini <code>IniSection</code> a parsear.
	 * @return <code>NewBikeVehicle</code> o <code>null</code>.
	 */
	@Override
	Event parse(IniSection ini) {
		// Se comprueba si es un NewBikeVehicle
		if ( iniNameMatch(ini) && typeMatch(ini, type) ) {
			String id;
			int time = 0;
			int maxSpeed;

			// ID ok?
			try{
				id = parseID(ini, "id");
			}
			catch(IllegalArgumentException e){
				throw new IllegalArgumentException(e + " in new Bike.");
			}

			// TIME ok?
			if(existsTimeKey(ini)){
				try{
					time = parseNoNegativeInt(ini, "time");
				}
				catch(IllegalArgumentException e){
					throw new IllegalArgumentException(e + 
							" when reading time in bike with id " + id);
				}
			}

			// MAXSPEED ok?
			try{
				maxSpeed = parseNoNegativeInt(ini, "max_speed");
			}
			catch(IllegalArgumentException e){
				throw new IllegalArgumentException(e + 
						" when reading maxSpeed in bike with id " + id);
			}

			// TRIP ok?
			// Creación de la ruta de Junction IDs.
			List<String> trip;
			try{
				trip = parseIDList(ini, "itinerary", 2);
			}
			catch(IllegalArgumentException e){
				throw new IllegalArgumentException(e + 
						" when reading itinerary in bike with id " + id);
			}
			
			// New Bike Vehicle.
			NewBikeVehicle vehicle = new NewBikeVehicle(time, id, maxSpeed, trip);
			return vehicle;
		}
		
		else return null;
	}
}
