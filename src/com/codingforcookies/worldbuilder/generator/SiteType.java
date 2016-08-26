package com.codingforcookies.worldbuilder.generator;

public enum SiteType {
	MOUNTAIN_PEAK("Mountain", 1F),
	MOUNTAIN("Mountain", .75F),
	MOUNTAIN_INCLINE("Mountain", .65F),
	LAND("Land", 0F),
	WATER("Water", -3F),
	
	UNKNOWN("Unknown", Float.MIN_VALUE);
	
	public String name;
	public float height;
	
	SiteType(String name, float height) {
		this.name = name;
		this.height = height;
	}
}