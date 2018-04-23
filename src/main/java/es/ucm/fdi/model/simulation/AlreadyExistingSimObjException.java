package es.ucm.fdi.model.simulation;

/**
 * Excepción utilizada cuando se procede a introducir un elemento en 
 * la simulación que ya existe y es equivalente.
 */
public class AlreadyExistingSimObjException extends SimulationException {

    public AlreadyExistingSimObjException(String info) {
        super(info);
    }

}