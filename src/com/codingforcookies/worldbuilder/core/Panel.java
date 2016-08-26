package com.codingforcookies.worldbuilder.core;

import org.lwjgl.opengl.GL11;

import com.codingforcookies.worldbuilder.core.element.AbstractElement;

public abstract class Panel extends AbstractElement {
	float r = 1F, g = 1F, b = 1F;
	public AbstractElement setBackground(float r, float g, float b) { this.r = r; this.g = g; this.b = b; return this; }
	
	public Panel(int x, int y, int width, int height) {
		super(x, y, width, height);
	}

	@Override
	public void render() {
		GL11.glColor3f(r, g, b);

		GL11.glBegin(GL11.GL_QUADS);
		{
			GL11.glVertex2f(0F, 0F);
			GL11.glVertex2f(getWidth(), 0F);
			GL11.glVertex2f(getWidth(), getHeight());
			GL11.glVertex2f(0F, getHeight());
		}
		GL11.glEnd();
	}
}