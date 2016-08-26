package com.codingforcookies.worldbuilder.core.impl;

public interface ITickable {
	/**
	 * @param mouseIn If the mouse is within this element's borders.
	 * @param mouseX The relative mouse x position within this element.
	 * @param mouseY The relative mouse y position within this element.
	 */
	void update(boolean mouseIn, double mouseX, double mouseY);
}