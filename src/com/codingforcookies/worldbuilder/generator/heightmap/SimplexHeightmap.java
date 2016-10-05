package com.codingforcookies.worldbuilder.generator.heightmap;

import com.codingforcookies.worldbuilder.util.SimplexNoiseGen;

import kn.uni.voronoitreemap.j2d.Point2D;
import kn.uni.voronoitreemap.j2d.Site;

public class SimplexHeightmap implements IHeightmap {
	private float[][] osn;
	
	@Override
	public void init(int width, int height, int seed) {
		SimplexNoiseGen sng = new SimplexNoiseGen(seed);
		osn = sng.generateOctavedSimplexNoise(width, height, 2, 0.3f, 0.004F);
	}

	@Override
	public void processHeight(Site site, Point2D center) {
		site.data.height = osn[(int)center.x][(int)center.y];
		for(int j = 0; j < 10; j++) {
			center = site.getPolygon().getInnerPoint();
			site.data.height += osn[(int)center.x][(int)center.y];
			site.data.height /= 2;
		}
		
		if(site.data.height > 0F)
			site.data.height = (float)(Math.pow(site.data.height, 3.5));
	}
}