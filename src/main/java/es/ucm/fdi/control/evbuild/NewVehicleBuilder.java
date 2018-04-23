package es.ucm.fdi.control.evbuild;

import java.util.ArrayList;
import java.util.List;

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
		
		//Se comprueba que es un NewVehicle
		if (iniNameMatch(ini) && typeMatch(ini, null)) {
			String id;
			int time = 0;
			int maxSpeed;

			// ID ok?
			try{
				id = parseID(ini, "id");
			}
			catch(IllegalArgumentException e){
				throw new IllegalArgumentException(e + " in new Vehicle.");
			}

			// TIME ok?
			if(existsTimeKey(ini)){
				try{
					time = parseNoNegativeInt(ini, "time");
				}
				catch(IllegalArgumentException e){
					throw new IllegalArgumentException(e + 
							" when reading time in Vehicle with id " + id);
				}
			}

			// MAXSPEED ok?
			try{
				maxSpeed = parseNoNegativeInt(ini, "max_speed");
			}
			catch(IllegalArgumentException e){
				throw new IllegalArgumentException(e + 
						" when reading maxSpeed in Vehicle with id " + id);
			}

			// TRIP ok?
			// Creación de la ruta de Junction IDs.
			List<String> trip;
			try{
				trip = parseIDList(ini, "itinerary", 2);
			}
			catch(IllegalArgumentException e){
				throw new IllegalArgumentException(e + 
						" when reading itinerary in Vehicle with id " + id);
			}

			// New Vehicle.
			NewVehicle vehicle = new NewVehicle(time, id, maxSpeed, trip);
			return vehicle;
		}
		else return null;
	}
}
