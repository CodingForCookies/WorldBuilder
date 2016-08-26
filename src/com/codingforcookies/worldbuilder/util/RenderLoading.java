package com.codingforcookies.worldbuilder.util;

import org.lwjgl.opengl.GL11;

public class RenderLoading {
	public static String process = "";
	public static boolean complete = false;

	private static final float DEG2RAD = (float)Math.PI / 180F;
	private static float loadingDegree = 0;
	private static float loadingOffset = 0;

	private static void renderBox(float x, float y, float width, float height) {
		GL11.glBegin(GL11.GL_TRIANGLES);
		{
			GL11.glVertex2f(x, y);
			GL11.glVertex2f(x, y - height);
			GL11.glVertex2f(x + width, y - height);

			GL11.glVertex2f(x, y);
			GL11.glVertex2f(x + width, y);
			GL11.glVertex2f(x + width, y - height);
		}
		GL11.glEnd();
	}
	
	public static void update() {
		if(loadingDegree >= 360F)
			loadingOffset += 10F;

		if(loadingDegree > 720F)
			loadingDegree = 0;
		else
			loadingDegree += 20F;
	}

	public static void draw(float x, float y, float diameter) {
		float radius = diameter / 2F - 3F;
		GL11.glPushMatrix();
		{
			GL11.glHint(GL11.GL_PERSPECTIVE_CORRECTION_HINT, GL11.GL_NICEST);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

			GL11.glTranslatef(x, y, 0F);
			GL11.glColor4f(0F, 0F, 0F, .5F);

			renderBox(-radius - 6F, radius + 6F, diameter + 6F, diameter + 6F);
			GL11.glColor4f(1F, 1F, 1F, .9F);

			FontRenderer.draw(process, -FontRenderer.getStringWidth(process) / 2 + 1F, FontRenderer.CHAR_WIDTH_HALF);

			GL11.glLineWidth(diameter / 20F);
			GL11.glBegin(GL11.GL_LINE_STRIP);
			if(loadingDegree < 360F) {
				for(int i = 0; i < loadingDegree; i++) {
					float degInRad = (-i + 90F - loadingDegree - loadingOffset) * DEG2RAD;
					GL11.glVertex2f((float)Math.cos(degInRad) * radius, (float)Math.sin(degInRad) * radius);
				}
			}else{
				for(float i = loadingOffset; i > loadingDegree - 720 + loadingOffset; i--) {
					float degInRad = (-i + 90F) * DEG2RAD;
					GL11.glVertex2f((float)Math.cos(degInRad) * radius, (float)Math.sin(degInRad) * radius);
				}
			}
			GL11.glEnd();
			GL11.glLineWidth(1F);
		}
		GL11.glPopMatrix();
	}
}