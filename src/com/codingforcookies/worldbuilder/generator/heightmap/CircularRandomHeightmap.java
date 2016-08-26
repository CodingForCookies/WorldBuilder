package com.codingforcookies.worldbuilder.generator.heightmap;

import com.codingforcookies.worldbuilder.util.SimplexNoiseGen;

import kn.uni.voronoitreemap.j2d.Point2D;
import kn.uni.voronoitreemap.j2d.Site;

public class CircularRandomHeightmap implements IHeightmap {
	private int width, height;
	
	private float[][] osn;
	private double maxdist;
	
	@Override
	public void init(int width, int height, int seed) {
		this.width = width;
		this.height = height;
		
		SimplexNoiseGen sng = new SimplexNoiseGen(seed);
		osn = sng.generateOctavedSimplexNoise(width, height, 3, 0.3f, 0.005F);
		
		maxdist = Math.sqrt(Math.pow(width, 2) + Math.pow(height, 2)) / 2;
	}

	@Override
	public void processHeight(Site site, Point2D center) {
		float dist = (float)Math.sqrt(Math.pow(width / 2 - center.x, 2) + Math.pow(height / 2 - center.y, 2));

		dist /= -maxdist;
		
		dist *= 2F;
		dist += 1.1F;
		
		if(dist < 0)
			site.data.height = dist;
		else
			site.data.height = osn[(int)center.x][(int)center.y] * dist * 1.3F;
	}
}