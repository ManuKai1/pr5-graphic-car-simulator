package es.ucm.fdi.control.evbuild;

import es.ucm.fdi.ini.IniSection;
import es.ucm.fdi.model.events.Event;
import es.ucm.fdi.model.events.NewHighwayRoad;

/**
 * Clase que construye un evento <code>NewHighwayRoad</code> utilizado para
 * crear un <code>HighwayRoad</code> en la simulación.
 */
public class NewHighwayRoadBuilder extends EventBuilder {

    private final String type = "lanes";

    /**
     * Constructor de <code>NewHighwayRoadBuilder</code> que pasa
     * el parámetro <code>new_road</code> al constructor de la
     * superclase.
     */
    public NewHighwayRoadBuilder() {
        super("new_road");
    }

    @Override
    Event parse(IniSection ini) {

        // Se comprueba si es una NewHighwayRoad.
        if (iniNameMatch(ini) && typeMatch(ini, type)) {
            String id;
			int time = 0;
			int maxSpeed;
			int length;
			String src;
			String dest;
			int lanes;
			
			// ID ok?
			try{
				id = parseID(ini, "id");
			}
			catch(IllegalArgumentException e){
				throw new IllegalArgumentException(e + " in new Highway Road.");
			}
			
			// TIME ok?
			if(existsTimeKey(ini)){
				try{
					time = parseNoNegativeInt(ini, "time");
				}
				catch(IllegalArgumentException e){
					throw new IllegalArgumentException(e + 
							" when reading time in Highway Road with id " + id);
				}
			}

			// SOURCE ok?	
			try{
				src = parseID(ini, "src");
			}
			catch(IllegalArgumentException e){
				throw new IllegalArgumentException(e + 
						" when reading dest in Highway Road with id " + id);
			}
			
			// DESTINY ok?
			try{
				dest = parseID(ini, "dest");
			}
			catch(IllegalArgumentException e){
				throw new IllegalArgumentException(e + 
						" when reading source in Highway Road with id " + id);
			}
			
			// MAXSPEED ok?
			try {
				maxSpeed = parsePositiveInt(ini, "max_speed");
			}
			catch (IllegalArgumentException e) {
				throw new IllegalArgumentException(e + 
						" when reading max speed in Highway Road with id " + id);
			}
			
			// LENGTH ok?
			try {
				length = parsePositiveInt(ini, "length");
			}
			catch (NumberFormatException e) {
				throw new IllegalArgumentException(e + 
						" when reading length in Highway Road with id " + id);
			}

            // LANES ok?
            try {
                lanes = parsePositiveInt(ini, "lanes");
            }
            catch (IllegalArgumentException e) {
                throw new IllegalArgumentException(e + 
                		" when reading length in Highway Road with id " + id);
            }

            // New Highway Road.
            NewHighwayRoad road = new NewHighwayRoad(time, id, length, maxSpeed, src, dest, lanes);
            return road;
        } 
        else return null;
    }
}

