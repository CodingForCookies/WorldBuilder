package com.codingforcookies.worldbuilder;

import com.codingforcookies.raisin.element.ButtonElement;
import com.codingforcookies.raisin.element.ButtonListElement;
import com.codingforcookies.raisin.element.Panel;
import com.codingforcookies.worldbuilder.generator.heightmap.HeightMaps;

public class ToolbarPanel extends Panel {
	public ToolbarPanel(int x, int y, int width, int height) {
		super(x, y, width, height);
		
		addElement(new ButtonElement(7, 7, 16, 16, null).setIcon("save"));
		addElement(new ButtonElement(33, 7, 16, 16, null).setIcon("open"));
		
		addElement(new ButtonListElement(width - 100, 0, 100, height, false, HeightMaps.getNames()) {
			@Override
			public void nextItem(String item) {
				World.getWorld().setHeightmap(item);
			}
		});
	}
}