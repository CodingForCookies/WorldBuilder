package com.codingforcookies.worldbuilder.view;

import com.codingforcookies.worldbuilder.generator.SiteData;

public class MapView2DHeightmap extends MapView2D {
	public float[] getColor(SiteData data) {
		float c = (data.height + 1F) / 2.5F;
		return new float[] { c, c, c };
	}
}