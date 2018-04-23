package es.ucm.fdi.control.evbuild;

import es.ucm.fdi.ini.IniSection;
import es.ucm.fdi.model.events.Event;
import es.ucm.fdi.model.events.NewDirtRoad;

/**
 * Clase que construye un evento <code>NewDirtRoad</code> utilizado para
 * crear un <code>DirtRoad</code> en la simulación.
 */
public class NewDirtRoadBuilder extends EventBuilder {

    private final String type = "dirt";

    /**
     * Constructor de <code>NewDirtRoadBuilder</code> que pasa
     * el parámetro <code>new_road</code> al constructor de la
     * superclase.
     */
    public NewDirtRoadBuilder() {
        super("new_road");
    }

    /**
     * Método de <code>parsing</code> de <code>NewDirtRoadBuilder</code> que comprueba
     * si la <code>IniSection</code> pasada como argumento representa un <code>NewDirtRoad</code>
     * y si sus parámetros son correctos.
     * 
     * @param ini <code>IniSection</code> a parsear.
     * @return <code>NewDirtRoad</code> o <code>null</code>.
     */
    @Override
    Event parse(IniSection ini) {
        if (iniNameMatch(ini) && typeMatch(ini, type)) {
            String id;
			int time = 0;
			int maxSpeed;
			int length;
			String src;
			String dest;
			
			// ID ok?
			try{
				id = parseID(ini, "id");
			}
			catch(IllegalArgumentException e){
				throw new IllegalArgumentException(e + " in new Dirt Road.");
			}
			
			// TIME ok?
			if(existsTimeKey(ini)){
				try{
					time = parseNoNegativeInt(ini, "time");
				}
				catch(IllegalArgumentException e){
					throw new IllegalArgumentException(e + 
							" when reading time in Dirt Road with id " + id);
				}
			}

			// SOURCE ok?	
			try{
				src = parseID(ini, "src");
			}
			catch(IllegalArgumentException e){
				throw new IllegalArgumentException(e + 
						" when reading dest in Dirt Road with id " + id);
			}
			
			// DESTINY ok?
			try{
				dest = parseID(ini, "dest");
			}
			catch(IllegalArgumentException e){
				throw new IllegalArgumentException(e + 
						" when reading source in Dirt Road with id " + id);
			}
			
			// MAXSPEED ok?
			try {
				maxSpeed = parsePositiveInt(ini, "max_speed");
			}
			catch (IllegalArgumentException e) {
				throw new IllegalArgumentException(e + 
						" when reading max speed in Dirt Road with id " + id);
			}
			
			// LENGTH ok?
			try {
				length = parsePositiveInt(ini, "length");
			}
			catch (NumberFormatException e) {
				throw new IllegalArgumentException(e + 
						" when reading length in Dirt Road with id " + id);
			}
			
			// New Road.
			NewDirtRoad road = new NewDirtRoad(time, id, length, maxSpeed, src, dest);
			return road;

        } 
        else return null;
    }
}
