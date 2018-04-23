package es.ucm.fdi.control.evbuild;

import java.util.ArrayList;
import java.util.List;

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

		if (iniNameMatch(ini)) {
			int time = 0;
			int duration;

			// TIME ok?
			try{
				time = parseNoNegativeInt(ini, "time");
			}
			catch(IllegalArgumentException e){
				throw new IllegalArgumentException(e + 
						"when reading time of faulty vehicles.");
			}

			// DURATION ok?
			try{
				duration = parsePositiveInt(ini, "duration");
			}
			catch(IllegalArgumentException e){
				throw new IllegalArgumentException(e + 
						"when reading duration of faulty vehicles.");
			}

			// VEHICLE_LIST ok?
			// Creación de la lista de vehículos.
			List<String> vehicles;
			try{
				vehicles = parseIDList(ini, "vehicles", 1);
			}
			catch(IllegalArgumentException e){
				throw new IllegalArgumentException(e + 
						"when reading list of faulty vehicles.");
			}

			// Faulty Vehicle.
			FaultyVehicle faulty = new FaultyVehicle(time, vehicles, duration);
			return faulty;
		}
		else return null;
	}
}
