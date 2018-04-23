package es.ucm.fdi.model.SimObj;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.ucm.fdi.ini.IniSection;
import es.ucm.fdi.model.simulation.SimulationException;

/**
 * Clase que representa una intersección inteligente, que modifica
 * la duración del semáforo según el flujo de las <code>Roads</code>,
 * como un objeto de simulación.
 */
public class RobinJunction extends Junction {

    private final String type = "rr"; // round-robin

    /**
     * Tiempo mínimo de duración de un semáforo.
     */
    protected int minLightTime;
    
    /**
     * Tiempo máximo de duración de un semáforo.
     */
    protected int maxLightTime;

    /**
     * Mapa de <code>incomingRoads</code> a sus respectivos intervalos 
     * de duración de sus semáforos.
     */
    protected Map<Road, Integer> timeLapses = new HashMap<>();

    /**
     * Tiempo consumido (unidades: ticks)
     */
    protected int elapsedTime = 0;

    /**
     * Booleano que informa si en una <code>RobinJunction</code> el semáforo ha 
     * estado abierto y en ningún momento ha pasado ningún coche.
     */
    protected boolean uselessGreen = true;

    /**
     * Booleano que informa si en una <code>RobinJunction</code> el semáforo ha 
     * estado abierto y cada vez ha cruzado un coche.
     */
    protected boolean usefulGreen = true;


    /**
     * Constructor de <code>RobinJunction</code>.
     * 
     * @param identifier identificador del objeto
     * @param minTime duración mínima del semáforo
     * @param maxTime duración máxima del semáforo
     */
    public RobinJunction(String identifier, int minTime, int maxTime) {
        super(identifier);
        minLightTime = minTime;
        maxLightTime = maxTime;
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
    	List<String> array = new ArrayList<>(incomingRoads.keySet());
		String nextRoad = array.get(light);
        Road usedRoad = incomingRoads.get(nextRoad);
        int roadTimeLapse = timeLapses.get(usedRoad);

        // Se actualiza el tiempo transcurrido con el semáforo en verde.
        elapsedTime += 1;

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
    		nextRoad = array.get(light);
            incomingRoads.get(nextRoad).setLight(true);

            // 3 //
            // Se resetea elapsedTime y los booleanos
            elapsedTime = 0;
            uselessGreen = true;
            usefulGreen = true;
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
        section.setValue("queues", getQueuesValue());
        section.setValue("type", type);

        return section;
    }

    /*
    * ESTE MÉTODO NO CONSERVA EL ORDEN DE LOS EXPECTED OUTPUTS, 
    * PERO LA COMPARACIÓN ES CORRECTA POR SECCIONES.
    public IniSection generateIniSection(int simTime) {
        // Se utiliza getQueuesValue() de RobinJunction.
        IniSection section = super.generateIniSection(simTime);         
        section.setValue("type", type);
    
    	return section;
    }
    */
    
    
    
    /**
     * {@inheritDoc}
     * <p>
     * En una <code>RobinJunction</code> se incluye el tiempo restante del semáforo
     * de la <code>Road</code> en verde.
     * </p>
     * 
     * @return <code>String</code> con las colas.
     */
    @Override
    protected String getQueuesValue() {
        // Generación del string de queues
        StringBuilder queues = new StringBuilder();
        for (Road incR : incomingRoads.values()) {
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
     * Añade una nueva <code>Road</code> de entrada a la 
     * <code>RobinJunction</code>. Al introducir una entrante,
     * la duración de su semáforo es máxima.
     * 
     * @param newRoad Nueva <code>Road</code> entrante
     */
    @Override
    public void addNewIncomingRoad(Road newRoad) {
        incomingRoads.put(newRoad.getID(), newRoad);
        timeLapses.put(newRoad, maxLightTime);
    }
}