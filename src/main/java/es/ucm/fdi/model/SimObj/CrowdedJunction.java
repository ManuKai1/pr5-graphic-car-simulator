package es.ucm.fdi.model.SimObj;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import es.ucm.fdi.ini.IniSection;

/**
 * Clase que representa una intersección inteligente, 
 * que modifica la duración y el orden del semáforo 
 * según el tamaño de las colas de las <code>incomingRoads</code>, 
 * como un objeto de simulación. Hereda de {@link Junction}
 */
public class CrowdedJunction extends Junction {

    /**
     * Información sobre el tipo de <code>Junction</code> 
     * que debe ponerse como valor en la clave 
     * <code>type</code> de la <code>IniSection</code> 
     * generada.
     */
    private static final String TYPE = "mc"; // most-crowded

    /**
     * Mapa de <code>incomingRoads</code> a sus respectivos 
     * intervalos de duración de sus semáforos.
     */
    protected Map<Road, Integer> timeLapses = new HashMap<>();

    /**
    * Tiempo consumido (unidades: ticks)
    */
    protected int elapsedTime = 0;

    /**
     * Constructor de {@link CrowdedJunction}.
     * 
     * @param identifier identificador del objeto
     */
    public CrowdedJunction(String identifier) {
        super(identifier); // light: -1

        // Al inicio de la simulación, la duración 
        // de los semáforos es nula.
        for ( Road inc : incomingRoads.values() ) {
            timeLapses.put(inc, 0);
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * En una <code>CrowdedJunction</code>, el primer semáforo que 
     * se pone en verde es el de la <code>Road</code> con más 
     * <code>Vehicles</code> en la cola de espera.
     * </p> <p>
     * Normalmente, al inicio de la simulación, no habrá ningún 
     * <code>Vehicle</code> esperando, para lo cual se sigue el 
     * orden establecido en <code>incomingRoads</code> y se 
     * actualiza la <code>Road</code> con 
     * <code>timeLapse = 1</code>.
     * </p>
     */
    @Override
    protected void firstLightUpdate() {
        // 1 // 
        // La carretera con la cola más concurrida se pone en verde.
        light = mostCrowdedRoad();
        List<String> array = new ArrayList<>(incomingRoads.keySet());
		String nextRoad = array.get(light);
        Road crowdedRoad = incomingRoads.get(nextRoad);

        crowdedRoad.setLight(true);
        
        // 2 //
        // Se actualiza su timeLapse respecto al número de vehículos esperando.
        int numWaiting = crowdedRoad.getNumWaitingVehicles();
        int newTimeLapse = Math.max( (numWaiting / 2) , 1 );
        timeLapses.put(crowdedRoad, newTimeLapse);

        // No se actualiza elapsedTime, 
        // pues no había ningún semáforo en verde.
    }

    /**
     * {@inheritDoc}
     * <p>
     * En una <code>CrowdedJunction</code>, el cruce de 
     * un <code>Vehicle</code> (si es posible) es análogo 
     * al de una <code>Junction</code> común.
     * </p>
     * 
     * @param greenRoad     <code>Road</code> con 
     *                      el semáforo en verde.
     */
    @Override
    protected void roadUpdate(Road greenRoad) {
        super.roadUpdate(greenRoad);
    }

    /**
     * {@inheritDoc}
     * <p>
     * En una <code>CrowdedJunction</code>, se comprueba si 
     * el semáforo de la <code>usedRoad</code> ha agotado 
     * su <code>timeLapse</code>.
     * </p> <p>
     * Si es así: se pone en rojo, se busca la <code>Road</code> 
     * con la cola más concurrida y se pone en verde, se actualiza 
     * su <code>timeLapse</code> con {@link #mostCrowdedRoad()},
     * y se resetea <code>elapsedTime</code>.
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
            // La carretera con la cola más concurrida se pone en verde.
            light = mostCrowdedRoad();
            nextRoad = array.get(light);
            Road crowdedRoad = incomingRoads.get(nextRoad);

            crowdedRoad.setLight(true);

            // 2 //
            // Se actualiza su timeLapse respecto 
            // al número de vehículos esperando.
            int numWaiting = crowdedRoad.getNumWaitingVehicles();
            int newTimeLapse = Math.max((numWaiting / 2), 1);
            timeLapses.put(crowdedRoad, newTimeLapse);

            // 3 //
            // Se resetea elapsedTime.
            elapsedTime = 0;
        }
    }

    /**
     * Busca la carretera más concurrida y devuelve su 
     * posición en la lista <code>incomingRoads</code>. 
     * En caso de empate, devuelve la posición menor:
     * la de la primera <code>Road</code> en registrarse
     * en la <code>Junction</code>.
     * 
     * @return  posición de la <code>Road</code> 
     *          más concurrida
     */
    private int mostCrowdedRoad() {
        int max = 0; // 0 vehículos
        int crowdedPos = 0; // la primera carretera

        List<String> array = new ArrayList<>(incomingRoads.keySet());
        
        // Se halla el máximo.
        for (int i = 0; i < array.size(); ++i) {
            int numVehicles = incomingRoads.get(array.get(i)).
            		getNumWaitingVehicles();

            if (numVehicles > max) {
                max = numVehicles;
                crowdedPos = i;
            }
        }

        // Posiciones de las carreteras que empatan.
        HashSet<Integer> equallyCrowdedPos = new HashSet<>();
        
        for (int i = 0; i < array.size(); ++i) {
            int numVehicles = incomingRoads.get(array.get(i)).
            		getNumWaitingVehicles();

            if (numVehicles == max) {
                equallyCrowdedPos.add(i);
            }
        }

        // Si hay empate, se sigue el orden de eventos
        if (equallyCrowdedPos.size() > 1) {
            // Si hay empate, de seguro el semáforo en verde 
            // no estará en la Road que se acaba de poner 
            // en rojo (light).
            crowdedPos = ( (light + 1) % incomingRoads.size() );

            if ( ! equallyCrowdedPos.contains(crowdedPos) ) {
                crowdedPos = ( (crowdedPos + 1) % incomingRoads.size() );
            }
        }

        return crowdedPos;
    }

    /**
     * {@inheritDoc}
     * <p>
     * En una <code>CrowdedJunction</code> se incluye además 
     * <code>type</code> y se incluye el tiempo restante del 
     * semáforo de la <code>Road</code> en verde.
     * </p>
     * 
     * @param simTime   tiempo del simulador
     * @return          informe <code>IniSection</code> de 
     *                  la <code>CrowdedJunction</code>
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
        section.setValue("type", TYPE);

        return section;
    }

    /*
    * ESTE MÉTODO NO CONSERVA EL ORDEN DE LOS EXPECTED OUTPUTS, 
    * PERO LA COMPARACIÓN ES CORRECTA POR SECCIONES.
    public IniSection generateIniSection(int simTime) {
        // Se utiliza getQueuesValue() de RobinJunction.
        IniSection section = super.generateIniSection(simTime);         
        section.setValue("type", TYPE);
    
    	return section;
    }
    */

    /**
     * {@inheritDoc}
     * <p>
     * En una <code>CrowdedJunction</code> se incluye el 
     * tiempo restante del semáforo de la <code>Road</code> 
     * en verde.
     * </p>
     * 
     * @return  <code>String</code> con las colas
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
     * Devuelve el tiempo restante del semáforo de 
     * cualquier <code>Road</code> con respecto a 
     * <code>elapsedTime</code>. No se comprueba 
     * que la <code>Road</code> esté en verde.
     * 
     * @param road  <code>Road</code> de la que 
     *              se quiere conocer el tiempo 
     *              del semáforo
     * @return      tiempo restante del semáforo.
     */
    private int lastingLightTime(Road road) {
        return ( timeLapses.get(road) - elapsedTime );
    }


}