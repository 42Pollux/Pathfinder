/**
 * @author pollux
 *
 */
package server;

import java.util.HashMap;

import com.sun.javafx.collections.MappingChange.Map;

import network.ConnectionCodes;

public class ResourceManager {
	public static String location = null;
	private static HashMap<Long, Object> object_list;
	
	public ResourceManager() {
	}
	
	public static void initialize() {
		object_list = new HashMap<Long, Object>();
	}
	
	public static void addObject(long object_id, Object obj) {
		object_list.put(object_id,  obj);
	}
	
	public static void removeObject(long object_id) {
		object_list.remove(object_id);
	}
	
	public static String returnFilePath(long item_id) {
		// TODO implement
		return "fsfksdf";
	}
	
	public static Object returnObject(long object_id) {
		// TODO implement
		// need a key value list for object requests
		if(object_list.containsKey(object_id)) {
			return object_list.get(object_id);
		}
		return null;
	}
	
	public static String getMapLocation(int map_id) {
		String map_location = null;
		switch(map_id) {
		case ConnectionCodes.MAP_MV: map_location = location + "map/mecklenburg-vorpommern.map"; break;
		case ConnectionCodes.MAP_SH: map_location = location + "map/schleswig-holstein.map"; break;
		case ConnectionCodes.MAP_NS: map_location = location + "map/niedersachsen.map"; break;
		case ConnectionCodes.MAP_HH: map_location = location + "map/hamburg.map"; break;
		case ConnectionCodes.MAP_HB: map_location = location + "map/bremen.map"; break;
		case ConnectionCodes.MAP_BR: map_location = location + "map/brandenburg.map"; break;
		case ConnectionCodes.MAP_B: map_location = location + "map/berlin.map"; break;
		case ConnectionCodes.MAP_SA: map_location = location + "map/sachsen-anhalt.map"; break;
		case ConnectionCodes.MAP_NRW: map_location = location + "map/nordrhein-westfalen.map"; break;
		case ConnectionCodes.MAP_HS: map_location = location + "map/hessen.map"; break;
		case ConnectionCodes.MAP_RP: map_location = location + "map/rheinland-pfalz.map"; break;
		case ConnectionCodes.MAP_SL: map_location = location + "map/saarland.map"; break;
		case ConnectionCodes.MAP_TH: map_location = location + "map/thueringen.map"; break;
		case ConnectionCodes.MAP_SS: map_location = location + "map/sachsen.map"; break;
		case ConnectionCodes.MAP_BW: map_location = location + "map/baden-wuerttemberg.map"; break;
		case ConnectionCodes.MAP_BA: map_location = location + "map/bayern.map"; break;
		case ConnectionCodes.MAP_GER: map_location = location + "map/Germany.map"; break;
		}
		
		return map_location;
	}

}
