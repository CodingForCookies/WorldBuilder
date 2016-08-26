package com.codingforcookies.worldbuilder.core.element;

import org.lwjgl.opengl.GL11;

public class BoxElement extends AbstractElement {
	protected float[] color;
	
	public BoxElement(int x, int y, int width, int height, float[] color) {
		super(x, y, width, height);
		
		this.color = color;
	}

	public BoxElement(int x, int y, int width, int height) {
		this(x, y, width, height, new float[] { .35F, .35F, .35F });
	}

	@Override
	public void render() {
		GL11.glColor3f(color[0], color[1], color[2]);
		
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