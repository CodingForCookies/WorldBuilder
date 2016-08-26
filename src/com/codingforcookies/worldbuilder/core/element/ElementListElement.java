package com.codingforcookies.worldbuilder.core.element;

public class ElementListElement extends AbstractElement {
	@Override
	public void addElement(AbstractElement element) {
		super.addElement(element);

		int distanceX = getWidth();
		while(--distanceX * elements.size() > getWidth());
		
		int size = Math.min(getHeight(), distanceX);
		
		int i = 0;
		for(AbstractElement e : elements) {
			e.setX(i++ * distanceX + distanceX / 2 - size / 2);
			e.setWidth(size);
			e.setHeight(size);
		}
	}
	
	public ElementListElement(int x, int y, int width, int height) {
		super(x, y, width, height);
	}

	@Override
	public void render() { }
}