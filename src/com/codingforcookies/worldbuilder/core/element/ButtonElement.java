package com.codingforcookies.worldbuilder.core.element;

import org.lwjgl.opengl.GL11;

public class ButtonElement extends AbstractElement {
	protected TextElement text;
	protected float color = .6F;
	
	protected boolean useColorHighlight = false;
	protected float[] colorHighlight = new float[] { .2F, .2F, .2F };
	public ButtonElement setColorHighlight(float[] colorHighlight) {
		this.colorHighlight = colorHighlight;
		return this;
	}
	
	protected boolean hideBg = false;
	
	protected boolean hovering = false;
	protected Runnable onClick;
	
	public ButtonElement(int x, int y, int width, int height, Runnable onClick) {
		super(x, y, width, height);
		
		this.onClick = onClick;
		
		addElement(text = new TextElement(null, null, 0, 0, getWidth(), getHeight()));
	}

	@Override
	public void setWidth(int width) {
		super.setWidth(width);
		text.setWidth(width);
	}

	@Override
	public void setHeight(int height) {
		super.setHeight(height);
		text.setHeight(height);
	}
	
	public ButtonElement setText(String text) {
		hideBg = false;
		
		this.text.setText(text);
		return this;
	}
	
	public ButtonElement setIcon(String icon) {
		if(this.text.getText() == null)
			hideBg = true;
		
		this.text.setIcon(icon);
		return this;
	}

	@Override
	public void update(boolean mouseIn, double mouseX, double mouseY) {
		hovering = mouseIn;
		
		color = .6F;
		
		if(hovering)
			color += .2F;

		if(hideBg)
			color += .2F;
		
		text.iconColor = new float[] { color + (useColorHighlight ? colorHighlight[0] : 0F),
									color + (useColorHighlight ? colorHighlight[1] : 0F),
									color + (useColorHighlight ? colorHighlight[2] : 0F) };
	}

	@Override
	public void render() {
		if(hideBg)
			return;
		
		GL11.glColor3f(color, color, color);

		GL11.glBegin(GL11.GL_QUADS);
		{
			GL11.glVertex2f(0F, 0F);
			GL11.glVertex2f(getWidth(), 0F);
			GL11.glVertex2f(getWidth(), getHeight());
			GL11.glVertex2f(0F, getHeight());
		}
		GL11.glEnd();
	}
	
	@Override
	public boolean onClick(int button, int mods, double x, double y) {
		if(onClick != null)
			onClick.run();
		
		return true;
	}
}