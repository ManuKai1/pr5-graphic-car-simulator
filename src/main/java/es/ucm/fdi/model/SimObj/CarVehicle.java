package es.ucm.fdi.model.SimObj;

import java.util.ArrayList;
import java.util.Random;

import es.ucm.fdi.ini.IniSection;

public class CarVehicle extends Vehicle {
	
	private final String type = "car"; // car

	/**
	 * Resistencia a las averías.
	 */
	private int resistance;

	/**
	 * Probabilidad de avería.
	 */
	private double faultyChance;
	
	/**
	 * Duración máxima de la avería.
	 */
	private int faultDuration;
	
	/**
	 * Semilla aleatoria.
	 */
	private Random randomSeed;
	
	/**
	 * Distancia transcurrida desde la última avería.
	 */
	private int kmSinceFaulty;
	
	/**
	 * Constructor de <code>CarVehicle</code>.
	 * 
	 * @param identifier identificador del objeto
	 * @param trp ruta de <code>Junctions</code>
	 * @param max máxima velocidad alcanzable
	 * @param res resistencia a averiarse
	 * @param breakChance probabilidad de avería
	 * @param breakDuration duración máxima de avería
	 * @param seed semilla aleatoria
	 */
	public CarVehicle(String identifier, ArrayList<Junction> trp, int max, 
			int res, double breakChance, int breakDuration, long seed) {
		super(identifier, trp, max);
		resistance = res;
		faultyChance = breakChance;
		faultDuration = breakDuration;
		randomSeed = new Random(seed);
		kmSinceFaulty = 0;
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * ----------
	 * </p> <p>
	 * PREVIAMENTE a lo anterior: como <code>CarVehicle</code>, se comprueba si el
	 * <code>Vehicle</code> puede averiarse por distancia recorrida y probabilidad
	 * de avería.
	 * </p>
	 */
	@Override
	public void proceed() {
		// 1 //
		// No está averiado, pero puede averiarse si se dan las condiciones.
		if ( ! isFaulty() ) {
			if ( kmSinceFaulty > resistance ) {
				if ( randomSeed.nextDouble() < faultyChance ) {
					// Generamos un tiempo de avería entre 1 y faultDuration
					setBreakdownTime( randomSeed.nextInt(faultDuration) + 1 );
				}
			}
		}

		// 2 //
		// Puede averarse por un evento o si se dan las condiciones anteriores.
		if ( isFaulty() ) {
			kmSinceFaulty = 0;
			actualSpeed = 0;
		}

		// 3 //
		// El coche avanza como un vehículo normal y con las diferencias de kilometraje
		// se calculan la distancia que lleva el coche sin averiarse.
		int oldKilometrage = kilometrage;
		super.proceed();

		kmSinceFaulty += kilometrage - oldKilometrage;
	}
	
	/**
	 * Genera una <code>IniSection</code> que informa de los atributos del
	 * <code>CarVehicle</code> en el tiempo del simulador.
	 * 
	 * @param simTime tiempo del simulador
	 * @return <code>IniSection</code> con información del <code>CarVehicle</code>
	 */
	@Override
	public IniSection generateIniSection(int simTime) {
		IniSection section = super.generateIniSection(simTime);
		section.setValue("type", type);

		return section;
	}









	/**
	 * Informe del car en cuestión, mostrando: id, tiempo de simulación, tipo coche
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
		report.append("type = car" + '\n');
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
