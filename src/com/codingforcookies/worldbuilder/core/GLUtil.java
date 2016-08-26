package com.codingforcookies.worldbuilder.core;

import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLCapabilities;

import com.codingforcookies.worldbuilder.core.element.AbstractElement;

public class GLUtil {
	private static GLCapabilities capabilities;
	public static GLCapabilities getCapabilities() { return capabilities; }
	public static void createCapabilities() {
		if(capabilities != null) {
			System.err.println("Attemted to recreate capabilities.");
			System.exit(1);
		}

		capabilities = GL.createCapabilities();
	}

	public static int glWidth = 0, glHeight = 0;

	public static void renderElement(AbstractElement e) {
		GL11.glPushMatrix();
		{
			if(e.is3D() && e.getFBO() != null && e.getFBO().isValid()) {
				make3D(e.getWidth(), e.getHeight());

				e.getFBO().bindFBO();
				{
					e.render();
				}
				e.getFBO().unbindFBO();

				make2D();
			}

			GL11.glPushAttrib(GL11.GL_SCISSOR_BIT);
			{
				GL11.glScissor(e.getAbsoluteX(), glHeight - e.getAbsoluteY() - e.getHeight(), e.getWidth(), e.getHeight());

				if(!e.is3D()) {
					GL11.glTranslatef(e.getX(), e.getY(), 0F);
					e.render();
				}else if(e.getFBO() != null && e.getFBO().isValid()) {
					GL11.glTranslatef(e.getAbsoluteX(), e.getAbsoluteY(), 0F);
					e.getFBO().renderFBO(e.getWidth(), e.getHeight());
				}

				e.renderChildren();
			}
			GL11.glPopAttrib();
		}
		GL11.glPopMatrix();
	}

	public static void make2D() {
		GL11.glClearColor(0F, 0F, 0F, 1F);

		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();

		GL11.glViewport(0, 0, glWidth, glHeight);
		GL11.glOrtho(0F, glWidth, glHeight, 0F, -1F, 1F);

		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();

		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glDisable(GL11.GL_LIGHTING);

		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glEnable(GL11.GL_BLEND);
	}

	public static void make3D(int width, int height) {
		GL11.glClearColor(0F, 0F, 0F, 1F);

		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();

		GL11.glViewport(0, 0, width, height);
		gluPerspective(45F, (float)width / height, 0.1F, 100000F);

		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();


		GL11.glHint(GL11.GL_PERSPECTIVE_CORRECTION_HINT, GL11.GL_NICEST);
		GL11.glShadeModel(GL11.GL_SMOOTH);

		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDepthFunc(GL11.GL_LEQUAL);
		GL11.glEnable(GL11.GL_CULL_FACE);

		GL11.glDisable(GL11.GL_BLEND);
	}

	private static void gluPerspective(float fovy, float aspect, float zNear, float zFar) {
		float xmin, xmax, ymin, ymax;

		ymax = zNear * (float)Math.tan(fovy * Math.PI / 360.0);
		ymin = -ymax;
		xmin = ymin * aspect;
		xmax = ymax * aspect;

		GL11.glFrustum(xmin, xmax, ymin, ymax, zNear, zFar);
	}
}