package es.ucm.fdi.model.SimObj;

import java.util.ArrayList;

import es.ucm.fdi.ini.IniSection;

/**
 * Clase que representa un camino de tierra como 
 * un objeto de simulación. Hereda de {@link Road}.
 */
public class DirtRoad extends Road {

    /**
     * Información sobre el tipo de <code>Road</code> 
     * que debe ponerse como valor en la clave 
     * <code>type</code> de la <code>IniSection</code> 
     * generada.
     */
    private String TYPE = "dirt";

    /**
     * Constructor de {@link DirtRoad}.
     * 
     * @param identifier    identificador del objeto
     * @param len           longitud de la vía
     * @param spLimit       límite de velocidad
     * @param fromJ         <code>Junction</code> donde empieza
     * @param toJ           <code>Junction</code> donde acaba
     */
    public DirtRoad(String identifier, int len, int spLimit,
            Junction fromJ, Junction toJ) {
        super(identifier, len, spLimit, fromJ, toJ);
    }

    /**
     * Calcula la velocidad base de la <code>DirtRoad</code>: 
     * el límite de velocidad <code>speedLimit</code>.
     * 
     * @return  la velocidad base de 
     *          la <code>DirtRoad</code>.
     */
    @Override
    protected int getBaseSpeed() {
        return speedLimit;
    }

    /**
     * <p>
     * Modifica la velocidad que llevarán los 
     * <code>Vehicles</code> en la <code>DirtRoad</code> 
     * previo avance.
     * </p> <p>
     * En la <code>DirtRoad</code>, el <code>reductionFactor</code> 
     * aumenta en uno por cada <code>Vehicle</code> averiado 
     * delante de un <code>Vehicle</code>.
     * </p>
     * 
     * @param onRoad    lista de <code>Vehicles</code> 
     *                  en <code>DirtRoad</code>.
     */
    @Override
    protected void vehicleSpeedModifier(ArrayList<Vehicle> onRoad) {
        // Velocidad máxima a la que pueden avanzar los vehículos.
        int baseSpeed = getBaseSpeed();

        // Factor de reducción de velocidad en caso de obstáculos delante.
        int reductionFactor = 1;

        // Se modifica la velocidad a la que avanzarán los vehículos,
        // teniendo en cuenta el factor de reducción.
        for (Vehicle v : onRoad) {
            v.setSpeed(baseSpeed / reductionFactor);

            if (v.getBreakdownTime() > 0) {
                reductionFactor += 1;
            }
        }
    }

    /**
     * Genera una <code>IniSection</code> que informa de los 
     * atributos de la <code>DirtRoad</code> en el 
     * tiempo del simulador.
     * 
     * @param simTime   tiempo del simulador
     * @return          <code>IniSection</code> con información
     *                  de la <code>DirtRoad</code>
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
        section.setValue("type", TYPE);
        section.setValue("state", getRoadState().toString());

        return section;
    }

    /*
    * ESTE MÉTODO NO CONSERVA EL ORDEN DE LOS EXPECTED OUTPUTS, 
    * PERO LA COMPARACIÓN ES CORRECTA POR SECCIONES.
    public IniSection generateIniSection(int simTime) {
        IniSection section = super.generateIniSection(simTime);
        section.setValue("type", TYPE);
        
        return section;
    }
    */
}