package es.ucm.fdi.model.SimObj;

import java.util.HashMap;

import es.ucm.fdi.ini.IniSection;
import es.ucm.fdi.model.simulation.SimulationException;

public class CrowdedJunction extends Junction {

    private final String type = "mc"; // most-crowded

    /**
     * Mapa de incomingRoads a sus respectivos intervalos de duración
     * de sus semáforos.
     */
    protected HashMap<Road, Integer> timeLapses;

    /**
    * Tiempo consumido (unidades: ticks)
    */
    protected int elapsedTime;

    public CrowdedJunction(String identifier) {
        super(identifier); // light: -1

        // Al inicio de la sumulación, la duración de los semáforos es nula.
        timeLapses = new HashMap<>();
        for ( Road inc : incomingRoads ) {
            timeLapses.put(inc, 0);
        }

        elapsedTime = 0;
    }

    /**
     * {@inheritDoc}
     * <p>
     * En una <code>CrowdedJunction</code>, el primer semáforo que se pone en verde
     * es el de la <code>Road</code> con más <code>Vehicles</code> en la cola de
     * espera.
     * </p> <p>
     * Normalmente, al inicio de la simulación, no habrá ningún <code>Vehicle</code>
     * esperando, para lo cual se sigue el orden establecido en <code>incomingRoads</code>
     * y se actualiza la <code>Road</code> con <code>timeLapse = 1</code>.
     * </p>
     */
    @Override
    protected void firstLightUpdate() {
        // 1 // 
        // La carretera con la cola más concurrida se pone en verde.
        light = mostCrowdedRoad();
        Road crowdedRoad = incomingRoads.get(light);

        crowdedRoad.setLight(true);
        
        // 2 //
        // Se actualiza su timeLapse respecto al número de vehículos esperando.
        int numWaiting = crowdedRoad.getNumWaitingVehicles();
        int newTimeLapse = Math.max( (numWaiting / 2) , 1 );
        timeLapses.put(crowdedRoad, newTimeLapse);

        // No se actualiza elapsedTime, pues no había ningún semáforo en verde.
    }

    /**
     * {@inheritDoc}
     * <p>
     * En una <code>CrowdedJunction</code>, el cruce de un <code>Vehicle</code> (si es
     * posible) es análogo al de una <code>Junction</code> común.
     * </p>
     * 
     * @param greenRoad <code>Road</code> con el semáforo en verde.
     */
    @Override
    protected void roadUpdate(Road greenRoad) {
        super.roadUpdate(greenRoad);
    }

    /**
     * {@inheritDoc}
     * <p>
     * En una <code>CrowdedJunction</code>, se comprueba si el semáforo de la
     * <code>usedRoad</code> ha agotado su <code>timeLapse</code>.
     * </p> <p>
     * Si es así: se pone en rojo, se busca la <code>Road</code> con la cola más
     * concurrida y se pone en verde, se actualiza su <code>timeLapse</code> con 
     * <code>mostCrowdedRoad()</code>, y se resetea <code>elapsedTime</code>.
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
            // La carretera con la cola más concurrida se pone en verde.
            light = mostCrowdedRoad();
            Road crowdedRoad = incomingRoads.get(light);

            crowdedRoad.setLight(true);

            // 2 //
            // Se actualiza su timeLapse respecto al número de vehículos esperando.
            int numWaiting = crowdedRoad.getNumWaitingVehicles();
            int newTimeLapse = Math.max((numWaiting / 2), 1);
            timeLapses.put(crowdedRoad, newTimeLapse);

            // 3 //
            // Se resetea elapsedTime.
            elapsedTime = 0;
        }
        else {
            // Se actualiza el tiempo transcurrido con el semáforo en verde.
            elapsedTime += 1;
        }
    }

    /**
     * Busca la carretera más concurrida y devuelve su posición en la lista
     * <code>incomingRoads</code>. En caso de empate, devuelve la posición menor:
     * la de la primera <code>Road</code> en registrarse en la <code>Junction</code>.
     * 
     * @return posición de la <code>Road</code> más concurrida
     */
    private int mostCrowdedRoad() {
        int max = 0; // 0 vehículos
        int crowdedPos = 0; // la primera carretera

        for (int i = 0; i < incomingRoads.size(); ++i) {
            int numVehicles = incomingRoads.get(i).getNumWaitingVehicles();

            if (numVehicles > max) {
                max = numVehicles;
                crowdedPos = i;
            }
        }

        return crowdedPos;
    }

    /**
     * {@inheritDoc}
     * <p>
     * En una <code>CrowdedJunction</code> se incluye además <code>type</code> y
     * se incluye el tiempo restante del semáforo de la <code>Road</code> en verde.
     * </p>
     * 
     * @param simTime tiempo del simulador
     * @return informe <code>IniSection</code> de la <code>CrowdedJunction</code>
     */
    @Override
    public IniSection generateIniSection(int simTime) {
        // Se utiliza getQueuesValue() de CrowdedJunction.
        IniSection section = super.generateIniSection(simTime);
        section.setValue("type", type);

        return section;
    }

    /**
     * {@inherirDoc}
     * <p>
     * En una <code>CrowdedJunction</code> se incluye el tiempo restante del semáforo
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
     * Informe de la <code>Crowdedjunction</code>, mostrando:
     * </p>
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
        // Type
        report.append("type = " + type);
        // Colas de espera
        report.append("queues = ");

        // Borrado de última coma
        if (report.length() > 0) {
            report.deleteCharAt(report.length() - 1);
        }

        return report.toString();
    }
}