package com.codingforcookies.worldbuilder;

import com.codingforcookies.worldbuilder.brush.BrushMoisture;
import com.codingforcookies.worldbuilder.brush.BrushObject;
import com.codingforcookies.worldbuilder.brush.BrushTerrain;
import com.codingforcookies.worldbuilder.brush.IBrush;
import com.codingforcookies.worldbuilder.core.Panel;
import com.codingforcookies.worldbuilder.core.element.BoxElement;
import com.codingforcookies.worldbuilder.core.element.ButtonToggleElement;
import com.codingforcookies.worldbuilder.core.element.ElementListElement;
import com.codingforcookies.worldbuilder.core.element.SliderElement;
import com.codingforcookies.worldbuilder.core.element.TextElement;

public class EditorPanel extends Panel {
	World world;
	
	public EditorPanel(int x, int y, int width, int height) {
		super(x, y, width, height);
		
		this.world = World.getWorld();
		
		BoxElement boxOptions;
		addElement(boxOptions = new BoxElement(0, 42, width, 36, new float[] { .33F, .33F, .33F }));
		boxOptions.setHidden(true);
		
		ElementListElement lee;
		addElement(lee = new ElementListElement(8, 8, width - 8, 28));
		
		lee.addElement(new BrushButton(new BrushTerrain(), boxOptions).setColorHighlight(new float[] { 1F, -.2F, -.2F }));
		lee.addElement(new BrushButton(new BrushMoisture(), boxOptions).setColorHighlight(new float[] { -.2F, -.2F, 1F }));
		lee.addElement(new BrushButton(new BrushObject(), boxOptions).setColorHighlight(new float[] { -.2F, 1F, -.2F }));

		BoxElement boxBrushSliders;
		addElement(boxBrushSliders = new BoxElement(0, getHeight() - 45, width, 40));
		
		boxBrushSliders.addElement(new TextElement("Size", null, 0, 0, 40, 16));
		boxBrushSliders.addElement(new SliderElement(40, 0, width - 50, 16, 150, 1) {
			@Override
			public double getCurrent() {
				return world.brushSize;
			}

			@Override
			public void onChange(double value) {
				world.brushSize = value;
			}
		});

		boxBrushSliders.addElement(new TextElement("Power", null, 0, 23, 40, 16));
		boxBrushSliders.addElement(new SliderElement(40, 23, width - 50, 16, 100, 1) {
			@Override
			public double getCurrent() {
				return world.brushPower * 200;
			}

			@Override
			public void onChange(double value) {
				world.brushPower = value / 200;
			}
		});
	}
	
	class BrushButton extends ButtonToggleElement {
		String id;
		IBrush brush;
		
		BoxElement boxOptions;
		ElementListElement optionList;
		
		public BrushButton(IBrush brush, BoxElement boxOptions) {
			super(0, 0, 0, 0);
			
			this.id = brush.getName().toLowerCase();
			this.brush = brush;
			this.boxOptions = boxOptions;
			
			setIcon("editor/" + id + "/icon");
			
			boxOptions.addElement(optionList = new ElementListElement(4, 4, boxOptions.getWidth() - 4, 28));
			
			for(String option : brush.getOptions())
				optionList.addElement(new ButtonToggleElement(0, 0, 0, 0) {
					@Override
					public boolean isToggled() {
						return world.brushOption == option;
					}
	
					@Override
					public void tryToggle(boolean current) {
						world.brushOption = option;
					}
				}.setIcon("editor/" + id + "/" + option));
			
		}

		@Override
		public boolean isToggled() {
			boolean b = brush.getClass().isInstance(world.editBrush);
			optionList.setHidden(!b);
			return b;
		}

		@Override
		public void tryToggle(boolean current) {
			if(current) {
				boxOptions.setHidden(true);
				
				world.editBrush = null;
				world.brushOption = null;
				return;
			}
			
			boxOptions.setHidden(false);
			world.editBrush = brush;
			world.brushOption = brush.getOptions()[0];
		}
	}
}