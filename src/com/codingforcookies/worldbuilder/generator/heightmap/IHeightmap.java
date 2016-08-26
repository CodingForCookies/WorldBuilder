package com.codingforcookies.worldbuilder.generator.heightmap;

import kn.uni.voronoitreemap.j2d.Point2D;
import kn.uni.voronoitreemap.j2d.Site;

public interface IHeightmap {
	void init(int width, int height, int seed);
	void processHeight(Site site, Point2D center);
}