package com.codingforcookies.worldbuilder.generator;

import com.codingforcookies.worldbuilder.generator.heightmap.IHeightmap;
import com.codingforcookies.worldbuilder.util.Random;
import com.codingforcookies.worldbuilder.util.RenderLoading;

import kn.uni.voronoitreemap.datastructure.OpenList;
import kn.uni.voronoitreemap.diagram.PowerDiagram;
import kn.uni.voronoitreemap.j2d.Point2D;
import kn.uni.voronoitreemap.j2d.PolygonSimple;
import kn.uni.voronoitreemap.j2d.Site;

public class WorldGenerator {
	private boolean ready = false;
	public boolean isReady() { return ready; }
	
	private PowerDiagram diagram;
	public PowerDiagram getPowerDiagram() { return diagram; }
	
	private OpenList sites;
	public OpenList getSites() { return sites; }
	
	private PolygonSimple rootPolygon;
	public PolygonSimple getRootPolygon() { return rootPolygon; }
	
	public WorldGenerator(int width, int height, int amount, int seed, IHeightmap heightmap) {
		diagram = new PowerDiagram();
		sites = new OpenList();
		rootPolygon = new PolygonSimple();
		
		Random rand = new Random(seed);

		rootPolygon.add(0, 0);
		rootPolygon.add(width, 0);
		rootPolygon.add(width, height);
		rootPolygon.add(0, height);

		RenderLoading.process = "Generating";
		
		for(int i = 0; i < amount; i++) {
			Site site = new Site(rand.nextDouble() * width, rand.nextDouble() * height);
			sites.add(site);
		}

		diagram.setSites(sites);
		diagram.setClipPoly(rootPolygon);
		diagram.computeDiagram();
		
		for(int i = 0; i < sites.size; i++) {
			Site site = sites.get(i);
			SiteData data = new SiteData(site, i);
			site.data = data;
		}
		
		
		RenderLoading.process = "Heightmap";
		heightmap.init(width, height, seed);
		
		for(int i = 0; i < sites.size; i++) {
			RenderLoading.process = "Height (" + (int)(i / (float)sites.size * 100) + "%)";
			
			Site site = sites.get(i);
			Point2D point = site.getPolygon().getInnerPoint();
			heightmap.processHeight(site, point);
		}
		
		calculate();
		
		// Run this twice in case any ponds are made
		for(int times = 0; times < 2; times++) {
			for(int i = 0; i < sites.size; i++) {
				RenderLoading.process = "Moist (" + (int)(i / (float)sites.size * 100) + "%)";
				
				Site site = sites.get(i);
				SiteData data = site.getData();
				
				SiteType type = data.getType();
				if(type == SiteType.WATER)
					data.moisture = 70F;
				else if(data.downhill != null) {
					int tries = 1000;
					SiteData d = data;
					do {
						d = d.downhill;
					} while(d.downhill != null && tries-- > 0);
					
					if(tries == 0)
						d.downhill = null;
					
					data.distanceToWater = (float)data.site.getPolygon().getCentroid().distance(d.site.getPolygon().getCentroid());
					
					float c = data.distanceToWater / 1.3F;
					data.moisture = (c > 70 ? 0 : 70 - c) + data.height * 20;
				}else{
					data.pond = true;
					data.moisture = 70F;
				}
			}
		}
		
		ready = true;
	}
	
	public void calculate() {
		for(int i = 0; i < sites.size; i++) {
			RenderLoading.process = "Down (" + (int)(i / (float)sites.size * 100) + "%)";
			
			Site site = sites.get(i);
			SiteData data = site.getData();
			data.downhill = null;
			data.type = null;
			data.biome = null;
			data.pond = false;
			
			SiteType type = data.getType();
			if(type != SiteType.WATER) {
				SiteData downhill = data;
				
				for(Site s : site.getNeighbours()) {
					if(s.getData().getType() == SiteType.WATER) {
						downhill = s.getData();
						break;
					}
					
					if(s.getData().height < downhill.height)
						downhill = s.getData();
				}
				
				if(downhill != data)
					data.downhill = downhill;
			}
		}
	}
	
	public void destroy() {
		sites.clear();
		rootPolygon.clearCacheOnly();

		diagram = null;
		sites = null;
		rootPolygon = null;
	}
}