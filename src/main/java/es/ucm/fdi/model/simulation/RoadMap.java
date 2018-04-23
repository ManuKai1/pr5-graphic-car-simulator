package es.ucm.fdi.model.simulation;

import java.util.ArrayList;

import es.ucm.fdi.model.SimObj.Junction;
import es.ucm.fdi.model.SimObj.Road;
import es.ucm.fdi.model.SimObj.Vehicle;

public class RoadMap {

    // MAPA
    private ArrayList<Junction> junctionObjects;
    private ArrayList<Road> roadObjects;
    private ArrayList<Vehicle> vehicleObjects;

    /**
     * Constructor de <code>RoadMap</code> que crea listas
     * vacías de los 3 principales objetos de simulación.
     */
    public RoadMap() {
        junctionObjects = new ArrayList<>();
        roadObjects = new ArrayList<>();
        vehicleObjects = new ArrayList<>();
    }

    // MÉTODOS

    /**
     * Devuelve la lista de <code>Roads</code>.
     * 
     * @return <code>roadObjects</code>
     */
    public ArrayList<Road> getRoads() {
        return roadObjects;
    }

    /**
     * Devuelve la lista de <code>Junctions</code>.
     * 
     * @return <code>junctionObjects</code>
     */
    public ArrayList<Junction> getJunctions() {
        return junctionObjects;
    }
    
    /**
     * Devuelve la lista de <code>Vehicles</code>.
     * 
     * @return <code>vehicleObjects</code>
     */
    public ArrayList<Vehicle> getVehicles() {
        return vehicleObjects;
    }

    /**
     * Añade una <code>Junction</code> a la lista de <code>Junctions</code>.
     * 
     * @param newJunction <code>Junction</code> a añadir
     */
    public void addJunction(Junction newJunction) {
        junctionObjects.add(newJunction);
    }

    /**
     * Añade una <code>Road</code> a la lista de <code>Roads</code>.
     * 
     * @param newRoad <code>Road</code> a añadir
     */
    public void addRoad(Road newRoad) {
        roadObjects.add(newRoad);
    }
    
    /**
     * Añade una <code>Vehicle</code> a la lista de <code>Vehicles</code>.
     * 
     * @param newVehicle <code>Vehicle</code> a añadir
     */
    public void addVehicle(Vehicle newVehicle) {
        vehicleObjects.add(newVehicle);
    }

    /**
     * Devuelve si existe una determinada <code>Junction</code> en
     * el mapa de la simulación.
     * 
     * @param id id de la <code>Junction</code> buscada
     * @return if <code>Junction</code> found
     */
    public boolean existsJunctionID(String id) {
        // O(n)
        for (Junction j : junctionObjects) {
            if (j.getID().equals(id)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Devuelve si existe una determinada <code>Road</code> en
     * el mapa de la simulación.
     * 
     * @param id id de la <code>Road</code> buscada
     * @return if <code>Road</code> found
     */
    public boolean existsRoadID(String id) {
        // O(n)
        for ( Road r : roadObjects ) {
            if ( r.getID().equals(id) ) {
                return true;
            }
        }

        return false;
    }

    /**
     * Devuelve si existe un determinado <code>Vehicle</code> en
     * el mapa de la simulación.
     * 
     * @param id id del <code>Vehicle</code> buscado
     * @return if <code>Vehicle</code> found
     */
    public boolean existsVehicleID(String id) {
        // O(n)
        for ( Vehicle v : vehicleObjects ) {
            if ( v.getID().equals(id) ) {
                return true;
            }
        }

        return false;
    }

    /**
     * Devuelve un <code>Vehicle</code> a partir de una id proporcionada.
     * Devuelve <code>null</code> si no se encuentra.
     * 
     * @param id id del <code>Vehicle</code> buscado
     * @return <code>Vehicle</code> buscado o <code>null</code>
     */
    public Vehicle getVehicleWithID(String id) {
        // O(n)
        for ( Vehicle v : vehicleObjects ) {
            if ( v.getID().equals(id) ) {
                return v;
            }
        }

        return null;
    }

    /**
     * Devuelve una <code>Junction</code> a partir de una id proporcionada.
     * Devuelve <code>null</code> si no se encuentra.
     * 
     * @param id id de la <code>Junction</code> buscado
     * @return <code>Junction</code> buscada o <code>null</code>
     */
    public Junction getJunctionWithID(String id) {
        // O(n)
        for ( Junction j : junctionObjects ) {
            if ( j.getID().equals(id) ) {
                return j;
            }
        }

        return null;
    }
    
    /**
     * Devuelve una <code>Road</code> a partir de una id proporcionada.
     * Devuelve <code>null</code> si no se encuentra.
     * 
     * @param id id de la <code>Road</code> buscado
     * @return <code>Road</code> buscada o <code>null</code>
     */
    public Road getRoadWithID(String id) {
        // O(n)
        for ( Road r : roadObjects ) {
            if ( r.getID().equals(id) ) {
                return r;
            }
        }

        return null;
    }

    public void clear(){
    	junctionObjects.clear();
    	roadObjects.clear();
    	vehicleObjects.clear();
    }

    
}