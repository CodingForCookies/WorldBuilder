package com.codingforcookies.worldbuilder.core.element;

import org.lwjgl.opengl.GL11;

import com.codingforcookies.worldbuilder.texture.Texture;
import com.codingforcookies.worldbuilder.texture.TextureLoader;
import com.codingforcookies.worldbuilder.util.FontRenderer;

public class TextElement extends AbstractElement {
	protected float[] iconColor = new float[] { 1F, 1F, 1F };
	protected float[] textColor = new float[] { 1F, 1F, 1F };
	
	private String text;
	public String getText() { return text; }
	public void setText(String text) { this.text = text; }

	private Texture icon;
	public Texture getIcon() { return icon; }
	public void setIcon(String icon) {
		this.icon = TextureLoader.getTexture(icon);
		
		if(this.icon == null)
			TextureLoader.loadFile(icon, "icon/" + icon, () -> {
				this.icon = TextureLoader.getTexture(icon);
			});
	}
	
	public TextElement(String text, String icon, int x, int y, int width, int height) {
		super(x, y, width, height);
		
		if(text != null)
			setText(text);
		
		if(icon != null)
			setIcon(icon);
	}

	@Override
	public void render() {
		if(icon != null) {
			GL11.glColor3f(iconColor[0], iconColor[1], iconColor[2]);
			
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glEnable(GL11.GL_BLEND);
			
			icon.bind();
			GL11.glBegin(GL11.GL_QUADS);
			{
				GL11.glTexCoord2f(0F, 0F); GL11.glVertex2f(0F, 0F);
				GL11.glTexCoord2f(1F, 0F); GL11.glVertex2f(getWidth(), 0F);
				GL11.glTexCoord2f(1F, 1F); GL11.glVertex2f(getWidth(), getHeight());
				GL11.glTexCoord2f(0F, 1F); GL11.glVertex2f(0F, getHeight());
			}
			GL11.glEnd();
			
			GL11.glDisable(GL11.GL_TEXTURE_2D);
		}
		
		if(text != null) {
			GL11.glColor3f(textColor[0], textColor[1], textColor[2]);
			FontRenderer.draw(text, (icon != null ? 16F : 0F) + (getWidth() - FontRenderer.getStringWidth(text)) / 2, (getHeight() + FontRenderer.CHAR_WIDTH) / 2);
		}
	}
}