package com.codingforcookies.worldbuilder.view;

import com.codingforcookies.worldbuilder.World;

public interface IMapView {
	boolean is3D();
	
	void render(World world, float[] translation, int[] viewSize, double mouseX, double mouseY);

	void calculate(World world);

	void onClick(World world, int button, int mods, double mouseXOnMap, double mouseYOnMap);
	void onDrag(World world, int button, double mouseXOnMap, double mouseYOnMap);
}