package es.ucm.fdi.util;

import java.util.Map;

public interface Describable {

    /**
     * Método de actualización de mapa.
     * 
     * @param out - mapa a actualizar con los 
     *              pares clave-valor
     */
    public void describe(Map<TableDataType, Object> out);
}