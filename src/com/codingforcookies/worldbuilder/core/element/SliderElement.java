package com.codingforcookies.worldbuilder.core.element;

import org.lwjgl.opengl.GL11;

public abstract class SliderElement extends AbstractElement {
	protected boolean hovering = false;
	
	protected double current, max;
	
	protected double step;
	protected int stepDecimals = 1;
	
	public SliderElement(int x, int y, int width, int height, double max, double step) {
		super(x, y, width, height);
		
		this.max = max;
		this.step = step;
		
		String s = Double.toString(Math.abs(step));
		if(s.contains("\\.")) {
			String stepDec = "1";
			
			int i = s.split(".")[1].length();
			for(; i > 0; i--)
				stepDec += "0";
			
			stepDecimals = Integer.parseInt(stepDec);
		}
	}

	@Override
	public void update(boolean mouseIn, double mouseX, double mouseY) {
		hovering = mouseIn;
		
		current = getCurrent();
		
		if(current > max)
			current = max;
		if(current < 0)
			current = 0;
	}

	@Override
	public void render() {
		GL11.glColor3f(.6F, .6F, .6F);
		
		float center = getHeight() / 2F;
		
		GL11.glBegin(GL11.GL_QUADS);
		{
			GL11.glVertex2f(0F, center - 2F);
			GL11.glVertex2f(getWidth(), center - 2F);
			GL11.glVertex2f(getWidth(), center + 2F);
			GL11.glVertex2f(0F, center + 2F);
		}
		GL11.glEnd();
		
		double sliderX = (current / max) * getWidth();
		GL11.glBegin(GL11.GL_QUADS);
		{
			GL11.glVertex2d(sliderX - 2, 0);
			GL11.glVertex2d(sliderX + 2, 0);
			GL11.glVertex2d(sliderX + 2, getHeight());
			GL11.glVertex2d(sliderX - 2, getHeight());
		}
		GL11.glEnd();
	}
	
	@Override
	public boolean onClick(int button, int mods, double x, double y) {
		onChange((double)((x / step) * stepDecimals) / stepDecimals);
		return true;
	}

	public boolean onDrag(int button, double x, double y) {
		onChange(getCurrent() + x);
		return true;
	}

	public boolean onScroll(double x, double y) {
		onChange(getCurrent() + (y > 0 ? step : -step));
		return true;
	}
	
	public abstract double getCurrent();
	public abstract void onChange(double value);
}