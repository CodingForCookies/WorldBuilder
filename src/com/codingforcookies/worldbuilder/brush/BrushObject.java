package com.codingforcookies.worldbuilder.brush;

import com.codingforcookies.worldbuilder.generator.SiteData;

public class BrushObject implements IBrush {
	@Override public String getName() { return "Object"; }
	@Override public String[] getOptions() { return new String[] { "river", "village" }; }
	@Override public BrushType getType(String option) { return BrushType.POINT; }

	@Override
	public void onLeft(String option, SiteData selection, double power) {
		
	}

	@Override
	public void onRight(String option, SiteData selection, double power) {
		
	}
}