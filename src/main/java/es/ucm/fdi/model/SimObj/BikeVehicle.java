package es.ucm.fdi.model.SimObj;

import java.util.ArrayList;

import es.ucm.fdi.ini.IniSection;

/**
 * Clase que representa una bicileta como un objeto de simulación.
 * Hereda de <code>Vehicle</code>.
 */
public class BikeVehicle extends Vehicle {

	private final String type = "bike"; // bike

	/**
	 * Constructor de <code>BikeVehicle</code>.
	 * 
	 * @param identifier identificador del objeto
	 * @param trp ruta de <code>Junctions</code>
	 * @param max máxima velocidad alcanzable
	 */
	public BikeVehicle(String identifier, ArrayList<Junction> trp, int max) {
		super(identifier, trp, max);
	}

	/**
	 * Modifica el tiempo de avería según el comportamiento especial
	 * de un <code>BileVehicle</code>.
	 * 
	 * @param addedBreakdownTime tiempo de avería a sumar
	 */
	@Override
	public void setBreakdownTime(int addedBreakdownTime)  {
		// Si la bicicleta avanza más rápido que la mitad de su velocidad
		// alcanzable, entonces podrá sumársele el tiempo de avería.
		if ( actualSpeed > (maxSpeed / 2) ) {
			breakdownTime += addedBreakdownTime;
		}
	}	
	

	/**
	 * Genera una <code>IniSection</code> que informa de los atributos del
	 * <code>BikeVehicle</code> en el tiempo del simulador.
	 * 
	 * @param simTime tiempo del simulador
	 * @return <code>IniSection</code> con información del <code>BikeVehicle</code>
	 */
	@Override
	public IniSection generateIniSection(int simTime) {
		IniSection section = super.generateIniSection(simTime);
		section.setValue("type", type);

		return section;
	}















	/**
	* Informe de la bike en cuestión, mostrando: id, tiempo de simulación, tipo bici
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
		// Type
		report.append("type = bike" + '\n');
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
