package com.codingforcookies.worldbuilder.brush;

import com.codingforcookies.worldbuilder.generator.SiteData;

public interface IBrush {
	String getName();
	String[] getOptions();
	BrushType getType(String option);
	
	void onLeft(String option, SiteData selection, double power);
	void onRight(String option, SiteData selection, double power);
}