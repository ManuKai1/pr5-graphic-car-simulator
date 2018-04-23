package es.ucm.fdi.control.evbuild;

import java.util.ArrayList;
import java.util.List;
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
	
	protected boolean existsTimeKey(IniSection ini){
		String timeKey = ini.getValue("time");
		return (timeKey != null);
	}
	
	protected boolean existsSeedKey(IniSection ini){
		String seedKey = ini.getValue("seed");
		return (seedKey != null);
	}
	
	protected boolean typeMatch(IniSection ini, String type){
		if(type == null){
			return ini.getValue("type") == null;
		}
		return ini.getValue("type").equals(type);
	}
	
	protected boolean iniNameMatch(IniSection ini){
		return ini.getTag().equals(iniName);
	}
	
	protected String parseID(IniSection ini, String key) throws IllegalArgumentException {
		String id = ini.getValue(key);
		if ( ! EventBuilder.validID(id) ) {
			throw new IllegalArgumentException("Illegal ID: " + id);
		}
		return id;
	}
	
	protected int parseNoNegativeInt(IniSection ini, String key)
			throws IllegalArgumentException{
		int result = 0;
		try {
			result = Integer.parseInt(ini.getValue(key));
		}
		// El numero no era un entero
		catch (NumberFormatException e) {
			throw new IllegalArgumentException("Int reading failure");
		}
		// Comprobamos que el numero sea no negativo
		if (result < 0) {
			throw new IllegalArgumentException("Negative int failure");
		}
		return result;
	}
	
	protected int parsePositiveInt(IniSection ini, String key)
			throws IllegalArgumentException {
		int result = 0;
		try {
			result = Integer.parseInt(ini.getValue(key));
		}
		// El valor no era un entero
		catch (NumberFormatException e) {
			throw new IllegalArgumentException("Int reading failure");
		}
		// Comprobamos que el valor sea positivo
		if (result <= 0) {
			throw new IllegalArgumentException("Non-positive int failure");
		}
		return result;
	}
	
	protected long parseLong(IniSection ini, String key)
			throws IllegalArgumentException {
		long result = 0;
		try {
			result = Long.parseLong(ini.getValue(key));
		}
		// El valor no era un long
		catch (NumberFormatException e) {
			throw new IllegalArgumentException("Long reading failure");
		}
		return result;
	}
	
	protected double parseProbability(IniSection ini, String key)
			throws IllegalArgumentException {
		double result;
		try {
			result = Double.parseDouble(ini.getValue(key));
		}
		//El valor no era un real
		catch (NumberFormatException e) {
			throw new IllegalArgumentException("Double reading failure");
		}
		//La probabilidad se va de los límites
		if (result < 0 || result > 1) {
			throw new IllegalArgumentException("Out of bounds probability");
		}
		return result;
	}
	
	protected List<String> parseIDList(IniSection ini, String key, int minElems) 
			throws IllegalArgumentException{
		List<String> result = new ArrayList<String>();
		
		// Array de Strings con las IDs.
		String line = ini.getValue(key);
		String[] input = line.split(",");

		// Comprobación de IDs.
		for (String idS : input) {
			if ( ! EventBuilder.validID(idS) ) {
				throw new IllegalArgumentException("Illegal ID: " + idS);
			}
			result.add(idS);
		}

		// Al menos un elemento.
		if (result.size() < minElems) {
			throw new IllegalArgumentException("Not enough elements");
		}
		return result;
	}
	
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
