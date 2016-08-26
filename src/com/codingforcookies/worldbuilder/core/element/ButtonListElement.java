package com.codingforcookies.worldbuilder.core.element;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

public abstract class ButtonListElement extends AbstractElement {
	protected TextElement text;
	
	protected boolean hovering = false;
	
	protected int selected = 0;
	
	protected boolean iconMode = false;
	protected List<String> items = new ArrayList<String>();
	
	public ButtonListElement(int x, int y, int width, int height, boolean iconMode, Collection<? extends String> collection) {
		super(x, y, width, height);
		
		this.iconMode = iconMode;
		
		this.items.addAll(collection);
		
		addElement(text = new TextElement(iconMode ? null : this.items.get(0), iconMode ? this.items.get(0) : null, 0, 0, getWidth(), getHeight()));
	}

	@Override
	public void update(boolean mouseIn, double mouseX, double mouseY) {
		hovering = mouseIn;
	}

	@Override
	public void render() {
		float c = .6F;
		if(hovering)
			c += .2F;
		
		GL11.glColor3f(c, c, c);

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
		selected += (button == GLFW.GLFW_MOUSE_BUTTON_LEFT ? 1 : (button == GLFW.GLFW_MOUSE_BUTTON_RIGHT ? -1 : 0));
		
		if(selected >= items.size())
			selected = 0;
		else if(selected < 0)
			selected = items.size() - 1;
		
		if(!iconMode)
			text.setText(items.get(selected));
		else
			text.setIcon(items.get(selected));
		
		nextItem(items.get(selected));
		return true;
	}
	
	public abstract void nextItem(String item);
}