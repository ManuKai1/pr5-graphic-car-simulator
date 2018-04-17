package es.ucm.fdi.control.evbuild;

import java.util.regex.Pattern;

import es.ucm.fdi.ini.IniSection;
import es.ucm.fdi.model.events.Event;

/**
 * Clase utilizada como base para la construcción de <code>Events</code> del
 * simulador.
 */
public abstract class EventBuilder {

	/**
	 * Nombre de la <code>IniSection</code>.
	 */
	protected String iniName;
	
	/**
	 * Constructor de <code>EventBuilder</code>.
	 * 
	 * @param name nombre de la <code>IniSection</code>.
	 */
	public EventBuilder(String name){
		iniName = name;
	}
	
	abstract Event parse(IniSection ini);
	
	/**
	 * Comprueba si un <code>ID</code> dado es válido para el
	 * simulador.
	 * 
	 * @return si el <code>ID</code> es válido.
	 */
	static boolean validID(String id) {
		return Pattern.matches("\\w+", id);
	}
}
