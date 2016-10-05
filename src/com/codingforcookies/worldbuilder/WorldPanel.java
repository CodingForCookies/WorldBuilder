package com.codingforcookies.worldbuilder;

import com.codingforcookies.raisin.element.Panel;
import com.codingforcookies.worldbuilder.view.MapView3D;
import com.codingforcookies.worldbuilder.view.MapViewRender;

public class WorldPanel extends Panel {
	public WorldPanel(int x, int y, int width, int height) {
		super(x, y, width, height);
		
		WorldElement we;
		addElement(we = new WorldElement(World.getWorld(), 0, 0, width, height / 2));
		addElement(we = new WorldElement(World.getWorld(), 0, height / 2, width / 2, height / 2));
		we.mapView = new MapView3D();
		addElement(we = new WorldElement(World.getWorld(), width / 2, height / 2, width / 2, height / 2));
		we.mapView = new MapViewRender();
	}
}