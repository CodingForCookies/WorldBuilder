package com.codingforcookies.worldbuilder.generator;

public enum SiteBiome {
	SNOW("Snowy", 4, 5,
			new int[] { 220, 220, 220 }),
	TUNDRA("Tundra", 3, 4,
			new int[] { 171, 171, 137 }),
	BARE("Bare", 2, 4,
			new int[] { 117, 117, 117 }),
	SCORCHED("Scorched", 1, 4,
			new int[] { 83, 83, 83 }),
	TAIGA("Taiga", 5, 3,
			new int[] { 134, 142, 117 }),
	SHRUBLAND("Shrubland", 3, 3,
			new int[] { 106, 114, 67 }),
	TEMPERATE_DESERT("Temperate Desert", 1, 3,
			new int[] { 148, 152, 112 }),
	TEMPERATE_RAIN_FOREST("Temperate Rain Forest", 6, 2,
			new int[] { 74, 116, 78 }),
	TEMPERATE_DECIDUOUS_FOREST("Temperate Decidous Forest", 4, 2,
			new int[] { 100, 121, 79 }),
	GRASSLAND("Grassland", 2, 2,
			new int[] { 106, 132, 80 }),
	TEMPERATE_DESERT_2("Temperate Desert", 1, 2,
			new int[] { 148, 152, 112 }),
	TROPICAL_RAIN_FOREST("Tropical Rain Forest", 5, 1,
			new int[] { 66, 107, 79 }),
	TROPICAL_SEASONAL_FOREST("Tropical Seasonal Forest", 3, 1,
			new int[] { 79, 124, 74 }),
	GRASSLAND_2("Grassland", 2, 1,
			new int[] { 106, 132, 80 }),
	SUBPROPICAL_DESERT("Subtropical Desert", 1, 1,
			new int[] { 153, 141, 109 }),
	
	OCEAN("Ocean", 0, 0,
			new int[] { 68, 68, 122 }),
	POND("Pond", 0, 0,
			new int[] { 100, 100, 220 });
	
	public String name;
	public int moistureZone;
	public int heightZone;
	
	public float[] color;
	
	SiteBiome(String name, int moistureZone, int heightZone, int[] color) {
		this.name = name;
		this.moistureZone = moistureZone;
		this.heightZone = heightZone;
		
		this.color = new float[] { color[0] / 255F, color[1] / 255F, color[2] / 255F };
	}
}