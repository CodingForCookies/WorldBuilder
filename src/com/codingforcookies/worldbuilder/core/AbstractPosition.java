package com.codingforcookies.worldbuilder.core;

import com.codingforcookies.worldbuilder.core.impl.IPositioned;

public class AbstractPosition implements IPositioned {
	private int x, y, width, height;

	@Override public int getX() { return x; }
	@Override public void setX(int x) { this.x = x; }

	@Override public int getY() { return y; }
	@Override public void setY(int y) { this.y = y; }

	@Override public int getWidth() { return width; }
	@Override public void setWidth(int width) { this.width = width; }

	@Override public int getHeight() { return height; }
	@Override public void setHeight(int height) { this.height = height; }
	
	public AbstractPosition(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
}