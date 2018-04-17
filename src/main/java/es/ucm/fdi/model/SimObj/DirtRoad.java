package es.ucm.fdi.model.SimObj;

import java.util.ArrayList;

import es.ucm.fdi.ini.IniSection;

/**
 * Clase que representa un camino como un objeto de simulación.
 * Hereda de <code>Road</code>.
 */
public class DirtRoad extends Road {

    private String type = "dirt";

    /**
     * Constructor de <code>DirtRoad</code>.
     * 
     * @param identifier identificador del objeto
     * @param len longitud de la vía
     * @param spLimit límite de velocidad
     * @param fromJ <code>Junction</code> donde empieza
     * @param toJ <code>Junction</code> donde acaba
     */
    public DirtRoad(String identifier, int len, int spLimit, Junction fromJ, Junction toJ) {
        super(identifier, len, spLimit, fromJ, toJ);
    }

    /**
     * Calcula la velocidad base de la <code>DirtRoad</code>: el límite de
     * velocidad <code>speedLimit</code>.
     * 
     * @return la velocidad base de la <code>DirtRoad</code>.
     */
    @Override
    protected int getBaseSpeed() {
        return speedLimit;
    }

    /**
     * <p>
     * Modifica la velocidad que llevarán los <code>Vehicles</code> en la
     * <code>DirtRoad</code> previo avance.
     * </p> <p>
     * En la <code>DirtRoad</code>, el <code>reductionFactor</code> aumenta en uno
     * por cada <code>Vehicle</code> averiado delante de un <code>Vehicle</code>.
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
     * Genera una <code>IniSection</code> que informa de los atributos de la
     * <code>DirtRoad</code> en el tiempo del simulador.
     * 
     * @param simTime tiempo del simulador
     * @return <code>IniSection</code> con información de la <code>DirtRoad</code>
     */
    @Override
    public IniSection generateIniSection(int simTime) {
        IniSection section = super.generateIniSection(simTime);
        section.setValue("type", type);
        
        return section;
    }









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