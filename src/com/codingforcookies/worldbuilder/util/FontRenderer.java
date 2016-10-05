package com.codingforcookies.worldbuilder.util;

import org.lwjgl.opengl.GL11;

import com.codingforcookies.worldbuilder.texture.Texture;

public class FontRenderer {
	private static final double TEXTURE_WIDTH = 256;
	public static final int CHAR_FILE_WIDTH = 16;
	
	private static float SAVED_CHAR_WIDTH = 8;
	public static void push(float size) {
		SAVED_CHAR_WIDTH = CHAR_WIDTH;
		CHAR_WIDTH = size;
		CHAR_WIDTH_HALF = size / 2F;
	}
	public static void pop() {
		CHAR_WIDTH = SAVED_CHAR_WIDTH;
		CHAR_WIDTH_HALF = SAVED_CHAR_WIDTH / 2F;
	}
	
	public static float CHAR_WIDTH = 8F;
	public static float CHAR_WIDTH_HALF = CHAR_WIDTH / 2F;
	public static final float CHAR_SPACING = -2F;
	
	public static float getStringWidth(String str) {
		return str.length() * (CHAR_WIDTH + CHAR_SPACING);
	}
	
	/**
	 * Character mappings 
	 */
	private static final String chars =
			"                " +
					"                " +
					" !\"#$%&'()*+,-./" +
					"0123456789:;<=>?" +
					"@ABCDEFGHIJKLMNO" +
					"PQRSTUVWXYZ[\\]^_" +
					"`abcdefghijklmno" +
					"pqrstuvwxyz{|}~" +
					"" +
					"" +
					"" +
					"" +
					"" +
					"" +
					"" +
					"" +
					"";
	
	/**
	 * The font texture
	 */
	public static Texture font;
	
	/**
	 * Various draw functions
	 */
	public static void draw(String string, float x, float y) {
		draw(string, x, y, CHAR_WIDTH);
	}
	
	/**
	 * Draw the text to the screen
	 */
	private static void draw(String string, float x, float y, float size) {
		if(font == null)
			return;
		
		GL11.glPushMatrix();
		{
			GL11.glEnable(GL11.GL_TEXTURE_2D); 
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			
			font.bind();
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);

			for(int i = 0; i < string.length(); i++) {
				if(string.charAt(i) == ' ')
					continue;

				int ch = chars.indexOf(string.charAt(i));
				if(ch < 0)
					continue;

				int xx = (ch % 16) * CHAR_FILE_WIDTH;
				int yy = (ch / 16) * CHAR_FILE_WIDTH;
				float xd = x + i * (size + CHAR_SPACING);

				GL11.glBegin(GL11.GL_QUADS);
				{
					float left = (float)(1.0 * (xx / TEXTURE_WIDTH));
					float right = (float)(1.0 * ((xx + CHAR_FILE_WIDTH) / TEXTURE_WIDTH));
					float top = (float)(1.0 * (yy / TEXTURE_WIDTH));
					float bottom = (float)(1.0 * ((yy + CHAR_FILE_WIDTH) / TEXTURE_WIDTH));

					GL11.glTexCoord2f(left, bottom); GL11.glVertex2f(xd, y);
					GL11.glTexCoord2f(right, bottom); GL11.glVertex2f(xd + size, y);
					GL11.glTexCoord2f(right, top); GL11.glVertex2f(xd + size, y - size);
					GL11.glTexCoord2f(left, top); GL11.glVertex2f(xd, y - size);
				}
				GL11.glEnd();
			}

			GL11.glDisable(GL11.GL_BLEND);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
		}
		GL11.glPopMatrix();
	}
}