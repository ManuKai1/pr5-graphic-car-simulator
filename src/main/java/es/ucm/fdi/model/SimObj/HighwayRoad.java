package es.ucm.fdi.model.SimObj;

import java.util.ArrayList;

import es.ucm.fdi.ini.IniSection;

/**
 * Clase que representa un vía de varios carriles como un objeto de simulación.
 * Hereda de <code>Road</code>.
 */
public class HighwayRoad extends Road {
    
    private final String type = "lanes";

    /**
     * Número de carriles de la autopista.
     */
    private int numLanes;

    /**
     * Constructor de <code>HighwayRoad</code>.
     * 
     * @param identifier identificador del objeto
     * @param len longitud de la vía
     * @param spLimit límite de velocidad
     * @param fromJ <code>Junction</code> donde empieza
     * @param toJ <code>Junction</code> donde acaba
     * @param lanes número de carriles
     */
    public HighwayRoad(String identifier, int len, int spLimit, Junction fromJ, Junction toJ, int lanes) {
        super(identifier, len, spLimit, fromJ, toJ);
        numLanes = lanes;
    }
    
    /**
     * Calcula la velocidad base de la <code>HighwayRoad</code>: el mínimo
     * entre la velocidad de congestión y el límite de velocidad 
     * <code>speedLimit</code>.
     * 
     * @return la velocidad base de la <code>HighwayRoad</code>.
     */
    @Override
    protected int getBaseSpeed() {
        // Cálculo de velocidadBase según la fórmula
        int congestionSpeed = ( speedLimit / Math.max(vehiclesOnRoad.size(), numLanes) ) + 1;

        return ( Math.min(speedLimit, congestionSpeed) );
    }

    /**
     * <p>
     * Modifica la velocidad que llevarán los <code>Vehicles</code> en la
     * <code>HighwayRoad</code> previo avance.
     * </p> <p>
     * En la <code>HighwayRoad</code>, el <code>reductionFactor</code> es
     * inicialmente <code>1</code> y aumenta a <code>2</code> si el número
     * de <code>Vehicles</code> averiados supera al número de carriles
     * <code>numLanes</code>.
     * </p>
     * 
     * @param onRoad lista de <code>Vehicles</code> en <code>DirtRoad</code>.
     */
    @Override
    protected void vehicleSpeedModifier(ArrayList<Vehicle> onRoad) {
        // Velocidad máxima a la que pueden avanzar los vehículos.
        int baseSpeed = getBaseSpeed();

        // Factor de reducción de velocidad en caso de obstáculos delante.
        int reductionFactor = 1;

        // Número de vehículos averiados.
        int brokenVehicles = 0;

        // Se modifica la velocidad a la que avanzarán los vehículos,
        // teniendo en cuenta el factor de reducción.
        for (Vehicle v : onRoad) {
            v.setSpeed(baseSpeed / reductionFactor);

            if (v.getBreakdownTime() > 0) {
                brokenVehicles += 1;
            }

            if (brokenVehicles >= numLanes) {
                reductionFactor = 2;
            }
        }
    }

    /**
     * Genera una <code>IniSection</code> que informa de los atributos de la
     * <code>HighwayRoad</code> en el tiempo del simulador.
     * 
     * @param simTime tiempo del simulador
     * @return <code>IniSection</code> con información de la <code>HighwayRoad</code>
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
        section.setValue("type", type);
        section.setValue("state", getRoadState().toString());

        return section;
    }

    /*
    * ESTE MÉTODO NO CONSERVA EL ORDEN DE LOS EXPECTED OUTPUTS, 
    * PERO LA COMPARACIÓN ES CORRECTA POR SECCIONES.
    public IniSection generateIniSection(int simTime) {
        IniSection section = super.generateIniSection(simTime);
        section.setValue("type", type);
        
        return section;
    }
    */















    /**
    * Informe de la HighwayRoad en cuestión, mostrando: id,
    * tiempo de simulación, tipo y estado.
    * @param simTime tiempo de simulación
    * @returns well-formatted String representing a Road report
    */
    @Override
    public String getReport(int simTime) {
        StringBuilder report = new StringBuilder();
        // TITLE
        report.append(REPORT_TITLE + '\n');
        // ID
        report.append("id = " + id + '\n');
        // SimTime
        report.append("time = " + simTime + '\n');
        // Type
        report.append("type = " + type + '\n');
        // Road State
        report.append("state = ");
        report.append(getRoadState());

        return report.toString();
    }
}