package es.ucm.fdi.model.SimObj;

import java.util.ArrayList;

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
    HashMap<Road, int> timeLapses;

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
        timeLapses = HashMap<>();
        for ( Road inc : getIncomingRoads() ) {
            timeLapses.put(inc, maxLightTime);
        }

        elapsedTime = 0;
    }

    @Override
    public void roadAdvance(Road greenRoad) {
        // Si hay vehículos esperando.
		if ( ! greenRoad.noVehiclesWaiting() ) {
			// El vehículo cruza si no está averiado.
			try {
				greenRoad.moveWaitingVehicles();
			}
			catch (SimulationException e) {
				System.err.println( e.getMessage() );
			}
        }
    }

    @Override
    public void lightAdvance() {
        Road usedRoad = getIncomingRoads().get(light)
        int roadTimeLapse = timeLapses.get(usedRoad);

        // El semáforo ha agotado su tiempo.
        if ( roadTimeLapse == elapsedTime ) {
            // * //
            // Se actualiza el indicador resp. de la carretera.
            greenRoad.setLight(false);

            // 1 // 
            // Se calcula la nueva duración del semáforo.
            int newTimeLapse = 0;

            if (uselessGreen) {
                newTimeLapse = max(roadTimeLapse - 1, minLightTime);
            } 
            else if (usefulGreen) {
                newTimeLapse = min(roadTimeLapse + 1, maxLightTime);
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
            // Se resetea elapsedTime
            elapsedTime = 0;
        }        
    }






}