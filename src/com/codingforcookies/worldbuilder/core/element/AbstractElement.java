package com.codingforcookies.worldbuilder.core.element;

import java.util.ArrayList;
import java.util.List;

import com.codingforcookies.worldbuilder.core.AbstractPosition;
import com.codingforcookies.worldbuilder.core.FBO;
import com.codingforcookies.worldbuilder.core.GLUtil;
import com.codingforcookies.worldbuilder.core.impl.IRenderable;
import com.codingforcookies.worldbuilder.core.impl.ITickable;

public abstract class AbstractElement extends AbstractPosition implements IRenderable, ITickable {
	private AbstractElement parent;
	public int getAbsoluteX() { return (parent != null ? parent.getAbsoluteX() : 0) + getX(); }
	public int getAbsoluteY() { return (parent != null ? parent.getAbsoluteY() : 0) + getY(); }

	private boolean hidden = false;
	public boolean isHidden() { return hidden; }
	public void setHidden(boolean hidden) { this.hidden = hidden; }
	
	private boolean threeD = false;
	public boolean is3D() { return threeD; }
	public void set3D(boolean threeD) {
		this.threeD = threeD;
		
		if(threeD)
			fbo = new FBO(getWidth(), getHeight());
		else
			fbo = null;
	}
	
	private FBO fbo;
	public FBO getFBO() { return fbo; }

	private int elementHover = -1;
	protected List<AbstractElement> elements;
	
	public void addElement(AbstractElement element) {
		if(elements == null)
			elements = new ArrayList<AbstractElement>();
		element.parent = this;
		elements.add(element);
	}
	
	public <T> T getElement(Class<T> clazz) {
		for(AbstractElement e : elements)
			if(clazz.isInstance(e))
				return clazz.cast(e);
		return null;
	}
	
	public AbstractElement() { super(0, 0, 0, 0); }
	
	public AbstractElement(int x, int y, int width, int height) {
		super(x, y, width, height);
	}

	@Override
	public void update(boolean mouseIn, double mouseX, double mouseY) {
		if(is3D())
			fbo.revalidate(getWidth(), getHeight());
	}

	public void updateChildren(boolean mouseIn, double mouseX, double mouseY) {
		if(elements != null) {
			elementHover = -1;
			
			for(int i = elements.size() - 1; i >= 0; i--) {
				AbstractElement e = elements.get(i);
				if(e.isHidden())
					continue;
				if(mouseX > e.getX() && mouseX < e.getX() + e.getWidth() && mouseY > e.getY() && mouseY < e.getY() + e.getHeight()) {
					elementHover = i;
					break;
				}
			}
			
			for(int i = 0; i < elements.size(); i++) {
				AbstractElement e = elements.get(i);
				if(e.isHidden())
					continue;
				e.update(elementHover == i, mouseX - e.getX(), mouseY - e.getY());
				e.updateChildren(elementHover == i, mouseX - e.getX(), mouseY - e.getY());
			}
		}
	}

	public void renderChildren() {
		if(elements != null)
			for(AbstractElement e : elements) {
				if(e.isHidden())
					continue;
				GLUtil.renderElement(e);
			}
	}
	
	/**
	 * Returns false of no child elements have been clicked.
	 */
	public boolean onClick(int button, int mods, double x, double y) {
		if(elementHover == -1)
			return false;
		AbstractElement e = elements.get(elementHover);
		if(e.isHidden())
			return false;
		return e.onClick(button, mods, x - e.getX(), y - e.getY());
	}

	/**
	 * Returns false of no child elements have been clicked.
	 */
	public boolean onDrag(int button, double x, double y) {
		if(elementHover == -1)
			return false;
		AbstractElement e = elements.get(elementHover);
		if(e.isHidden())
			return false;
		return e.onDrag(button, x, y);
	}

	/**
	 * Returns false of no child elements have been clicked.
	 */
	public boolean onScroll(double x, double y) {
		if(elementHover == -1)
			return false;
		AbstractElement e = elements.get(elementHover);
		if(e.isHidden())
			return false;
		return e.onScroll(x, y);
	}

	@Override
	public void setWidth(int width) {
		super.setWidth(width);
		
		if(is3D())
			fbo.invalidate();
	}

	@Override
	public void setHeight(int height) {
		super.setHeight(height);

		if(is3D())
			fbo.invalidate();
	}
}