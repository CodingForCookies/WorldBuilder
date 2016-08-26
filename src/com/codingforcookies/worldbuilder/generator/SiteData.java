package com.codingforcookies.worldbuilder.generator;

import kn.uni.voronoitreemap.j2d.Site;

public class SiteData {
	public Site site;
	public int index;
	public int getIndex() { return index; }
	
	public float height = 0F;
	public float moisture = 0F;

	public boolean pond = false;
	public SiteData downhill;
	public float distanceToWater;

	/** The land type of this site. */
	public SiteType type;
	
	/** The biome of this site. */
	public SiteBiome biome;
	
	public SiteData(Site site, int index) {
		this.site = site;
		this.index = index;
	}
	
	public SiteType getType() {
		if(type != null)
			return type;
		if(pond)
			return (type = SiteType.WATER);
		for(SiteType type : SiteType.values())
			if(height >= type.height)
				return (this.type = type);
		return (type = SiteType.UNKNOWN);
	}
	
	public SiteBiome getBiome() {
		if(biome != null)
			return biome;
		if(height < 0)
			return (biome = SiteBiome.OCEAN);
		if(pond)
			return (biome = SiteBiome.POND);

		int moistureZone = (int)Math.floor(moisture / 70F * 5) + 1;
		int heightZone = (int)Math.floor(height * 4) + 1;
		
		for(Site neighbor : site.getNeighbours())
			if(neighbor.getData().moisture == 70F)
				return (biome = SiteBiome.SUBPROPICAL_DESERT);

		for(SiteBiome biome : SiteBiome.values())
			if(moistureZone >= biome.moistureZone && heightZone >= biome.heightZone)
				return (this.biome = biome);
		
		return SiteBiome.OCEAN;
	}
}