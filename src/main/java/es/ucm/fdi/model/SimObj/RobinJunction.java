package es.ucm.fdi.model.SimObj;

import java.util.HashMap;

import es.ucm.fdi.ini.IniSection;
import es.ucm.fdi.model.simulation.SimulationException;

public class RobinJunction extends Junction {

    private final String type = "rr"; // round-robin

    /**
     * Tiempos mínimo y máximo de duración de un semáforo.
     */
    int minLightTime, maxLightTime;

    /**
     * Mapa de incomingRoads a sus respectivos intervalos de duración
     * de sus semáforos.
     */
    HashMap<Road, Integer> timeLapses;

    /**
     * Tiempo consumido (unidades: ticks)
     */
    int elapsedTime;

    /**
     * 
     */
    boolean uselessGreen;

    /**
     * Booleano que informa si en un cruce el semáforo ha estado abierto
     * y cada vez ha cruzado un coche.
     */
    boolean usefulGreen;

    public RobinJunction(String identifier, int minTime, int maxTime) {
        super(identifier);
        minLightTime = minTime;
        maxLightTime = maxTime;

        // Al inicio de la sumulación, la duración de los 
        // semáforos es máxima.
        timeLapses = new HashMap<>();
        for ( Road inc : incomingRoads ) {
            timeLapses.put(inc, maxLightTime);
        }

        elapsedTime = 0;

        // Suponemos ambos ciertos.
        uselessGreen = true;
        usefulGreen = true;
    }

    @Override
    protected void roadAdvance(Road greenRoad) {
        boolean hasCrossed = false;
        
        // Si hay vehículos esperando.
		if ( ! greenRoad.noVehiclesWaiting() ) {
			// El vehículo cruza si no está averiado.
            
			try {
				hasCrossed = greenRoad.moveWaitingVehicles();
			}
			catch (SimulationException e) {
				System.err.println( e.getMessage() );
			}
        }

        // Comprobación de cruce
        if (uselessGreen && hasCrossed) {
            uselessGreen = false;
        }
        if (usefulGreen && ! hasCrossed) {
            usefulGreen = false;
        }
    }

    @Override
    protected void lightAdvance() {
        Road usedRoad = incomingRoads.get(light);
        int roadTimeLapse = timeLapses.get(usedRoad);

        // El semáforo ha agotado su tiempo.
        if ( roadTimeLapse == elapsedTime ) {
            // * //
            // Se actualiza el indicador resp. de la carretera.
            usedRoad.setLight(false);

            // 1 // 
            // Se calcula la nueva duración del semáforo.
            int newTimeLapse = 0;

            if (uselessGreen) {
                newTimeLapse = Math.max(roadTimeLapse - 1, minLightTime);
            } 
            else if (usefulGreen) {
                newTimeLapse = Math.min(roadTimeLapse + 1, maxLightTime);
            }
            else {
                newTimeLapse = roadTimeLapse;
            }

            // Nueva duración del semáforo para la carretera.
            timeLapses.put(usedRoad, newTimeLapse);
            

            // 2 // 
            // Se pone en verde el semáforo siguiente.
            // Número de carreteras entrantes en el cruce.
            int numIncomingRoads = incomingRoads.size();
            
            // Avanza en 1 el semáforo circular.
            light = (light + 1) % numIncomingRoads;

            // El semáforo de la carretera se pone verde.
            incomingRoads.get(light).setLight(true);

            // 3 //
            // Se resetea elapsedTime y los booleanos
            elapsedTime = 0;
            uselessGreen = true;
            usefulGreen = true;
        }        
    }

    /**
	 * Informe de la Robin-Round Junction en cuestión, mostrando: id,
	 * tiempo de simulación, colas de espera de sus carreteras entrantes.
	 * 
     * @param simTime tiempo de la simulación 
	 */
	@Override
	public String getReport(int simTime) {
		StringBuilder report = new StringBuilder();
		// TITLE
		report.append(REPORT_TITLE + '\n');
		// ID
		report.append("id = " + id);
		// SimTime
		report.append("time = " + simTime);
		// Colas de espera
		report.append("queues = ");
		for ( Road incR : incomingRoads ) {
            StringBuilder waitingState = incR.getWaitingState();
            report.append( getRobinState(waitingState, incR) );
            report.append(",");
		}

		// Borrado de última coma
		if (report.length() > 0) {
			report.deleteCharAt(report.length() - 1);
		}
		
		return report.toString();
	}
	
	/**
	 * A partir de los datos del cruce (robin) genera una IniSection
     * 
	 * @param simTime tiempo del simulador
	 * @return IniSection report del cruce
	 */
	public IniSection generateIniSection(int simTime) {
		// Creación de etiqueta (sin corchetes)
        String tag = REPORT_TITLE;        
		tag = (String) tag.subSequence(1, tag.length() - 1);
		
        // Creación de IniSection
        IniSection section = new IniSection(tag);
		section.setValue("id", id);
		section.setValue("time", simTime);
        section.setValue("type", type);

        // Generación del string de queues
		StringBuilder queues = new StringBuilder();
		for ( Road incR : incomingRoads ) {
			StringBuilder waitingState = incR.getWaitingState();
            queues.append( getRobinState(waitingState, incR) );
			queues.append(",");
		}
		
		// Borrado de última coma
		if (queues.length() > 0) {
			queues.deleteCharAt(queues.length() - 1);
			// En caso contrario, queues es vacío y produciría
			// una OutOfBoundsException.
		}
		
		section.setValue("queues", queues.toString());
		return section;
	}

    /**
     * Si el waitingState de la Road tiene el semáforo verde, se divide el 
     * StringBuilder y se introduce el número de ticks que quedan para apagar
     * el semáforo.
     * Si no, se devuelve el que se ha pasado.
     */
    private StringBuilder getRobinState(StringBuilder waitingState, Road road) {
        StringBuilder robinState = new StringBuilder();
        String waitingString = waitingState.toString();

        String[] waitingSplit = waitingString.split("\\,");
        
        if (waitingSplit[1] == "green") {
            robinState.append(waitingSplit[0]); // Carretera
            robinState.append(",");
            robinState.append(waitingSplit[1]); // Semáforo
            robinState.append(":" + getLastingTime(road));
            robinState.append(",");
            robinState.append(waitingSplit[2]);

            return robinState;
        }
        else { // "red"
            return waitingState;
        }
    }

    private int getLastingTime(Road road) {
        return timeLapses.get(road) - elapsedTime;
    }
}