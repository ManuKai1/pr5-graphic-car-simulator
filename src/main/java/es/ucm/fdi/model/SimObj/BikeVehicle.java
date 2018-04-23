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
		// 1 //
		// Se crea la etiqueta de la sección (sin corchetes).
		String tag = REPORT_TITLE;
		tag = (String) tag.subSequence(1, tag.length() - 1);
		IniSection section = new IniSection(tag);

		// 2 // 
		// Se generan los datos en el informe.
		section.setValue("id", id);
		section.setValue("time", simTime);
		section.setValue("type", type);
		section.setValue("speed", actualSpeed);
		section.setValue("kilometrage", kilometrage);
		section.setValue("faulty", breakdownTime);
		section.setValue("location", hasArrived ? "arrived" : "(" + road.getID() + "," + location + ")");
		
		
		return section;
	}

	/*
	* ESTE MÉTODO NO CONSERVA EL ORDEN DE LOS EXPECTED OUTPUTS, 
	* PERO LA COMPARACIÓN ES CORRECTA POR SECCIONES.
	public IniSection generateIniSection(int simTime) {
		IniSection section = super.generateIniSection(simTime);
		section.setValue("type", type);

		return section;
	}
	*/
	
}
