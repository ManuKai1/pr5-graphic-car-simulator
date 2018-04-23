package es.ucm.fdi.model.SimObj;

import es.ucm.fdi.ini.IniSection;

/**
 * Clase base que representa un objeto cualquiera de la simulación, a saber:
 * <code>Junctions</code>, <code>Roads</code> y <code>Vehicles</code>.
 */
public abstract class SimObject { 

	/**
	 * Identificador del objeto de simulación.
	 */
	protected String id;	

	/**
	 * Método de avance de cualquier objetio de la simulación. Ocurre en
	 * un tick.
	 */
	public abstract void proceed();
	public abstract IniSection generateIniSection(int simTime);

	/**
	 * Constructor de <code>SimObject</code>.
	 * 
	 * @param identifier identificador del objeto
	 */
	public SimObject(String identifier) {
		id = identifier;
	}
	
	/**
	 * Devuelve el identificador del objeto de la simulación.
	 * 
	 * @return identificador
	 */
	public String getID() {
		return id;
	}
	
	/**
	 * Comprueba si el <code>SimObject</code> es igual a un objeto dado
	 * <code>obj</code>.
	 * 
	 * @param obj objeto a comparar
	 * @return if <code>SimObject</code> equals <code>obj</code>
	 */
	public boolean equals(Object obj) {
		// Mismo objeto
		if (this == obj) {
			return true;
		}
		
		// obj no es ningún objeto
		if (obj == null) {
			return false;
		}
		
		// Misma clase.
		if ( getClass() != obj.getClass() ) {
			return false;
		}
		
		// Mismo identificador.
		SimObject other = (SimObject) obj;
		return (id == other.id);
	}	
}
