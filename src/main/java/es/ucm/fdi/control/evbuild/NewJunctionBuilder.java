package es.ucm.fdi.control.evbuild;

import es.ucm.fdi.ini.IniSection;
import es.ucm.fdi.model.events.Event;
import es.ucm.fdi.model.events.NewJunction;

/**
 * Clase que construye un evento <code>NewJunction</code> utilizado para
 * crear un <code>Junction</code> en la simulación.
 */
public class NewJunctionBuilder extends EventBuilder{
	
	/**
	 * Constructor de <code>NewJunctionBuilder</code> que pasa
	 * el parámetro <code>new_junction</code> al constructor de la
	 * superclase.
	 */
	public NewJunctionBuilder() {
		super("new_junction");
	}
	
	/**
	 * Método de <code>parsing</code> de <code>NewJunctionBuilder</code> que comprueba
	 * si la <code>IniSection</code> pasada como argumento representa un <code>NewJunction</code>
	 * y si sus parámetros son correctos.
	 * 
	 * @param ini <code>IniSection</code> a parsear.
	 * @return <code>NewJunction</code> o <code>null</code>.
	 */
	@Override
	Event parse(IniSection ini) throws IllegalArgumentException {
		boolean match = false;

		// Se comprueba si es un NewJunction
		if ( ini.getTag().equals(iniName) && ini.getValue("type") == null ) {
			match = true;
		}

		if (match) {
			String id = ini.getValue("id");
			int time = 0;

			// ID ok?
			if ( ! EventBuilder.validID(id) ) {
				throw new IllegalArgumentException("Illegal junction ID: " + id);
			}

			// TIME ok?
			String timeKey = ini.getValue("time");
			if (timeKey != null) {
				try {
					time = Integer.parseInt(timeKey);
				}
				// El tiempo no era un entero
				catch(NumberFormatException e) {
					throw new IllegalArgumentException("Time reading failure in junction with ID: " + id);
				}
				// Comprobamos que el tiempo sea positivo
				if (time < 0) {
					throw new IllegalArgumentException("Negative time in junction with ID: " + id);
				}
            }

			NewJunction junction = new NewJunction(time, id);
			return junction;
		}
		else return null;
	}

}
