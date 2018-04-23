package es.ucm.fdi.model.simulation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.ucm.fdi.model.SimObj.Junction;
import es.ucm.fdi.model.SimObj.Road;
import es.ucm.fdi.model.SimObj.Vehicle;

public class RoadMap {

    // MAPA
    private Map<String, Junction> junctionObjects = new HashMap<>();
    private Map<String, Road> roadObjects = new HashMap<>();
    private Map<String, Vehicle> vehicleObjects = new HashMap<>();

    /**
     *
     */
    public RoadMap() {}

    // MÉTODOS

    /**
     * Devuelve la lista de <code>Roads</code>.
     * 
     * @return <code>roadObjects</code>
     */
    public Map<String, Road> getRoads() {
        return roadObjects;
    }

    /**
     * Devuelve la lista de <code>Junctions</code>.
     * 
     * @return <code>junctionObjects</code>
     */
    public Map<String, Junction> getJunctions() {
        return junctionObjects;
    }
    
    /**
     * Devuelve la lista de <code>Vehicles</code>.
     * 
     * @return <code>vehicleObjects</code>
     */
    public Map<String, Vehicle> getVehicles() {
        return vehicleObjects;
    }

    /**
     * Añade una <code>Junction</code> a la lista de <code>Junctions</code>.
     * 
     * @param newJunction <code>Junction</code> a añadir
     */
    public void addJunction(Junction newJunction) {
        junctionObjects.put(newJunction.getID(), newJunction);
    }

    /**
     * Añade una <code>Road</code> a la lista de <code>Roads</code>.
     * 
     * @param newRoad <code>Road</code> a añadir
     */
    public void addRoad(Road newRoad) {
        roadObjects.put(newRoad.getID(), newRoad);
    }
    
    /**
     * Añade una <code>Vehicle</code> a la lista de <code>Vehicles</code>.
     * 
     * @param newVehicle <code>Vehicle</code> a añadir
     */
    public void addVehicle(Vehicle newVehicle) {
        vehicleObjects.put(newVehicle.getID(), newVehicle);
    }

    /**
     * Devuelve si existe una determinada <code>Junction</code> en
     * el mapa de la simulación.
     * 
     * @param id id de la <code>Junction</code> buscada
     * @return if <code>Junction</code> found
     */
    public boolean existsJunctionID(String id) {
    	//O(1)
       return junctionObjects.containsKey(id);
    }

    /**
     * Devuelve si existe una determinada <code>Road</code> en
     * el mapa de la simulación.
     * 
     * @param id id de la <code>Road</code> buscada
     * @return if <code>Road</code> found
     */
    public boolean existsRoadID(String id) {
    	//O(1)
       return roadObjects.containsKey(id);
    }

    /**
     * Devuelve si existe un determinado <code>Vehicle</code> en
     * el mapa de la simulación.
     * 
     * @param id id del <code>Vehicle</code> buscado
     * @return if <code>Vehicle</code> found
     */
    public boolean existsVehicleID(String id) {
    	//O(1)
    	return vehicleObjects.containsKey(id);
    }

    /**
     * Devuelve un <code>Vehicle</code> a partir de una id proporcionada.
     * Devuelve <code>null</code> si no se encuentra.
     * 
     * @param id id del <code>Vehicle</code> buscado
     * @return <code>Vehicle</code> buscado o <code>null</code>
     */
    public Vehicle getVehicleWithID(String id) {
    	//O(1)
        return vehicleObjects.get(id);
    }

    /**
     * Devuelve una <code>Junction</code> a partir de una id proporcionada.
     * Devuelve <code>null</code> si no se encuentra.
     * 
     * @param id id de la <code>Junction</code> buscado
     * @return <code>Junction</code> buscada o <code>null</code>
     */
    public Junction getJunctionWithID(String id) {
    	//O(1)
    	return junctionObjects.get(id);
    }
    
    /**
     * Devuelve una <code>Road</code> a partir de una id proporcionada.
     * Devuelve <code>null</code> si no se encuentra.
     * 
     * @param id id de la <code>Road</code> buscado
     * @return <code>Road</code> buscada o <code>null</code>
     */
    public Road getRoadWithID(String id) {
    	//O(1)
    	return roadObjects.get(id);
    }

    public void clear(){
    	junctionObjects.clear();
    	roadObjects.clear();
    	vehicleObjects.clear();
    }

    
}