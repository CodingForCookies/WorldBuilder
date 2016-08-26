package com.codingforcookies.worldbuilder.texture;

import org.lwjgl.opengl.GL11;

/**
 * Class for a loaded texture
 * @author Stumblinbear
 */
public class Texture {
	private int texture = 0;
	
	public Texture(int textureid) {
		texture = textureid;
	}
	
	/**
	 * Bind the texture
	 */
	public void bind() {
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
	}
}