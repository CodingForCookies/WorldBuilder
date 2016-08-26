package com.codingforcookies.worldbuilder.view;

import com.codingforcookies.worldbuilder.generator.SiteData;

public class MapView2DTemperature extends MapView2D {
	public float[] getColor(SiteData data) {
		float c = data.moisture / 70F / 1.2F;
		return new float[] { 1F - c, 1F - c, c };
	}
}