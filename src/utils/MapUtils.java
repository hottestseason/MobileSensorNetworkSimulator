package utils;

import java.util.TreeMap;

public class MapUtils {
	static public void clearOld(TreeMap<?, ?> map, Integer left) {
		if (map != null) {
			while (map.size() > left) {
				map.remove(map.firstKey());
			}
		}
	}
}
