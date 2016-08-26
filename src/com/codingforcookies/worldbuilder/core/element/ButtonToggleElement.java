package com.codingforcookies.worldbuilder.core.element;

public abstract class ButtonToggleElement extends ButtonElement {
	public ButtonToggleElement(int x, int y, int width, int height) {
		super(x, y, width, height, null);
	}

	@Override
	public void update(boolean mouseIn, double mouseX, double mouseY) {
		useColorHighlight = isToggled();
		
		super.update(mouseIn, mouseX, mouseY);
	}
	
	@Override
	public boolean onClick(int button, int mods, double x, double y) {
		tryToggle(isToggled());
		return true;
	}

	public abstract boolean isToggled();
	public abstract void tryToggle(boolean current);
}