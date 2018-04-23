package es.ucm.fdi.control.evbuild;


import es.ucm.fdi.ini.IniSection;
import es.ucm.fdi.model.events.Event;
import es.ucm.fdi.model.events.NewRoad;

/**
 * Clase que construye un evento <code>NewRoad</code> utilizado para
 * crear un <code>Road</code> en la simulación.
 */
public class NewRoadBuilder extends EventBuilder{
	
	/**
	 * Constructor de <code>NewRoadBuilder</code> que pasa
	 * el parámetro <code>new_road</code> al constructor de la
	 * superclase.
	 */
	public NewRoadBuilder(){
		super("new_road");
	}
	
	/**
	 * Método de <code>parsing</code> de <code>NewRoadBuilder</code> que comprueba
	 * si la <code>IniSection</code> pasada como argumento representa un <code>NewRoad</code>
	 * y si sus parámetros son correctos.
	 * 
	 * @param ini <code>IniSection</code> a parsear.
	 * @return <code>NewRoad</code> o <code>null</code>.
	 */
	@Override
	Event parse(IniSection ini) {
		boolean match = false;

		// Se comprueba si es un NewRoad
		if ( ini.getTag().equals(iniName) && ini.getValue("type") == null ) {
			match = true;
		}

		if (match) {
			String id = ini.getValue("id");
			int time = 0;
			int maxSpeed;
			int length;
			String src = ini.getValue("src");
			String dest = ini.getValue("dest");
			
			// ID ok?
			if( ! EventBuilder.validID(id) ) {
				throw new IllegalArgumentException("Illegal road ID: " + id);
			}
			
			// TIME ok?
			String timeKey = ini.getValue("time");
			if(timeKey != null) {
				try {
					time = Integer.parseInt(timeKey);
				}
				//El tiempo no era un entero
				catch (NumberFormatException e) {
					throw new IllegalArgumentException("Time reading failure in road with ID: " + id);
				}
				//Comprobamos que el tiempo sea no negativo
				if(time < 0) {
					throw new IllegalArgumentException("Negative time in road with ID: " + id);
				}
			}

			// SOURCE ok?			
			if( ! EventBuilder.validID(src) ) {
				throw new IllegalArgumentException("Illegal source junction ID in road with ID: " + id);
			}
			
			// DESTINY ok?
			if( ! EventBuilder.validID(dest) ) {
				throw new IllegalArgumentException("Illegal destination junction ID in road with ID: " + id);
			}
			
			// MAXSPEED ok?
			try {
				maxSpeed = Integer.parseInt( ini.getValue("max_speed") );
			}
			// La velocidad no era un entero
			catch (NumberFormatException e) {
				throw new IllegalArgumentException("Max speed reading failure in road with ID: " + id);
			}
			//Comprobamos que la velocidad sea positiva
			if (maxSpeed <= 0) {
				throw new IllegalArgumentException("Non-positive speed in road with ID: " + id);
			}
			
			// LENGTH ok?
			try {
				length = Integer.parseInt( ini.getValue("length") );
			}
			//La longitud no era un entero
			catch (NumberFormatException e) {
				throw new IllegalArgumentException("Length reading failure in road with ID: " + id);
			}
			//Comprobamos que la longitud sea positiva
			if (length <= 0) {
				throw new IllegalArgumentException("Non-positive length in road with ID: " + id);
			}
			
			// New Road.
			NewRoad road = new NewRoad(time, id, length, maxSpeed, src, dest);
			return road;			
		}
		else return null;
	}

}
