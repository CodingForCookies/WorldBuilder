package com.codingforcookies.worldbuilder.generator.heightmap;

import kn.uni.voronoitreemap.j2d.Point2D;
import kn.uni.voronoitreemap.j2d.Site;

public class FlatHeightmap implements IHeightmap {
	@Override
	public void init(int width, int height, int seed) { }

	@Override
	public void processHeight(Site site, Point2D center) {
		site.data.height = -.01F;
	}
}