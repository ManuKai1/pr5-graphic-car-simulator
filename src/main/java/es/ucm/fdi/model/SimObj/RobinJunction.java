package es.ucm.fdi.model.SimObj;

import java.util.HashMap;

import es.ucm.fdi.ini.IniSection;
import es.ucm.fdi.model.simulation.SimulationException;

public class RobinJunction extends Junction {

    private final String type = "rr"; // round-robin

    /**
     * Tiempos mínimo y máximo de duración de un semáforo.
     */
    protected int minLightTime, maxLightTime;

    /**
     * Mapa de incomingRoads a sus respectivos intervalos de duración
     * de sus semáforos.
     */
    protected HashMap<Road, Integer> timeLapses;

    /**
     * Tiempo consumido (unidades: ticks)
     */
    protected int elapsedTime;

    /**
     * Booleano que informa si en un cruce el semáforo ha estado abiero
     * y en ningún momento ha pasado ningún coche.
     */
    protected boolean uselessGreen;

    /**
     * Booleano que informa si en un cruce el semáforo ha estado abierto
     * y cada vez ha cruzado un coche.
     */
    protected boolean usefulGreen;

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

    /**
     * {@inheritDoc}
     * <p>
     * En una <code>RobinJunction</code>, la primera actualización es análoga 
     * a la de una <code>Junction</code> común: se pone en verde el primer semáforo 
     * de la lista de <code>incomingRoads</code>.
     * </p>
     */
    @Override
    protected void firstLightUpdate() {
        super.firstLightUpdate(); // Mismo proceder

        // No se actualiza elapsedTime, pues no había ningún semáforo en verde.
    }

    /**
     * {@inheritDoc}
     * <p>
     * En una <code>RobinJunction</code>, se comprueba si algún <code>Vehicle</code>
     * ha cruzado para actualizar los parámetros <code>uselessGreen</code> y
     * <code>usefulGreen</code> que se utilizarán luego para modificar la duración
     * del semáforo.
     * </p>
     * 
     * @param greenRoad <code>Road</code> con el semáforo en verde
     */
    @Override
    protected void roadUpdate(Road greenRoad) {
        boolean hasCrossed = false;
        
        // Si hay vehículos esperando.
		if ( ! greenRoad.noVehiclesWaiting() ) {
			// El vehículo cruza si no está averiado.
            
			try {
				hasCrossed = greenRoad.moveWaitingVehicle();
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

    /**
     * {@inheritDoc}
     * <p>
     * En una <code>RobinJunction</code>, se comprueba si el semáforo de la
     * <code>usedRoad</code> ha agotado su <code>timeLapse</code>.
     * </p> <p>
     * Si es así: se pone en rojo, se calcula la nueva duración del semáforo cuando
     * vuelva a ponerse verde, se pone en verde el semáforo de la siguiente
     * <code>Road</code> en <code>incomingRoads</code>, y se resetean los parámetros
     * <code>elapsedTime, uselessGreen, usefulGreen</code>.
     * </p> <p>
     * Si no, no ocurre nada y se actualiza <code>elapsedTime</code>.
     * </p>
     */
    @Override
    protected void lightUpdate() {
        Road usedRoad = incomingRoads.get(light);
        int roadTimeLapse = timeLapses.get(usedRoad);

        // El semáforo ha agotado su tiempo.
        if ( roadTimeLapse == elapsedTime ) {
            // * //
            // La carretera actualizada se pone en rojo.
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
        else {
            // Se actualiza el tiempo transcurrido con el semáforo en verde.
            elapsedTime += 1;
        }    
    }
	
	/**
     * {@inheritDoc}
     * <p>
     * En una <code>RobinJunction</code> se incluye además <code>type</code> y
     * se incluye el tiempo restante del semáforo de la <code>Road</code> en verde.
     * </p>
     * 
     * @param simTime tiempo del simulador
     * @return informe <code>IniSection</code> de la <code>RobinJunction</code>
     */
	public IniSection generateIniSection(int simTime) {
        // Se utiliza getQueuesValue() de RobinJunction.
        IniSection section = super.generateIniSection(simTime);         
        section.setValue("type", type);

		return section;
	}

    /**
     * {@inherirDoc}
     * <p>
     * En una <code>RobinJunction</code> se incluye el tiempo restante del semáforo
     * de la <code>Road</code> en verde.
     * </p>
     * 
     * @return <code>String</code> con las colas.
     */
    protected String getQueuesValue() {
        // Generación del string de queues
        StringBuilder queues = new StringBuilder();
        for (Road incR : incomingRoads) {
            // Semáforo en verde.
            if (incR.isGreen()) {
                queues.append(incR.getWaitingState(lastingLightTime(incR)));
            } else { // En rojo.
                queues.append(incR.getWaitingState());
            }
            queues.append(",");
        }

        // Borrado de última coma (si queues no es vacío).
        if (queues.length() > 0) {
            queues.deleteCharAt(queues.length() - 1);
        }

        return queues.toString();
    }

    /**
     * Devuelve el tiempo restante del semáforo de cualquier <code>Road</code>
     * con respecto a <code>elapsedTime</code>. El método no comprueba que la
     * <code>Road</code> esté en verde.
     * 
     * @param road <code>Road</code> de la que se quiere conocer el tiempo del semáforo.
     * @return tiempo restante del semáforo.
     */
    private int lastingLightTime(Road road) {
        return timeLapses.get(road) - elapsedTime;
    }

















    /**
     * {@inheritDoc} 
     * <p>
     * Informe de la Robin-Round Junction en cuestión, mostrando: id,
     * tiempo de simulación, colas de espera de sus carreteras entrantes.
     * </p>
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
        for (Road incR : incomingRoads) {
            // Semáforo en verde.
            if (incR.isGreen()) {
                report.append(incR.getWaitingState(lastingLightTime(incR)));
            } else { // En rojo.
                report.append(incR.getWaitingState());
            }
            report.append(",");
        }

        // Borrado de última coma
        if (report.length() > 0) {
            report.deleteCharAt(report.length() - 1);
        }

        return report.toString();
    }
}