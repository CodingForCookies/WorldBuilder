package com.codingforcookies.worldbuilder.generator.heightmap;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class HeightMaps {
	private static Map<String, IHeightmap> maps = new HashMap<String, IHeightmap>();
	public static Collection<? extends String> getNames() { return maps.keySet(); }
	public static IHeightmap get(String name) { return maps.get(name); }
	
	static {
		maps.put("Flat", new FlatHeightmap());
		maps.put("Simplex", new SimplexHeightmap());
		maps.put("Circular", new CircularHeightmap());
		maps.put("CircularRandom", new CircularRandomHeightmap());
	}
}