package com.codingforcookies.worldbuilder.core;

import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;

public class FBO {
	private int width;
	public int getWidth() { return width; }
	public void setWidth(int width) { this.width = width; invalidate(); }

	private int height;
	public int getHeight() { return height; }
	public void setHeight(int height) { this.height = height; invalidate(); }

	private boolean fboValid = false;
	public boolean isValid() { return fboValid; }
	public void invalidate() { fboValid = false; }

	private int colorTextureID;
	private int framebufferID;
	private int depthRenderBufferID;

	private String shader;
	public void setShader(String shader) { this.shader = shader; }

	public FBO(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public void revalidate(int width, int height) {
		if(isValid())
			return;

		if(!GLUtil.getCapabilities().GL_EXT_framebuffer_object) {
			System.out.println("FBO not supported.");
			System.exit(0);
		}

		framebufferID = EXTFramebufferObject.glGenFramebuffersEXT();
		colorTextureID = GL11.glGenTextures();

		EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, framebufferID);

		GL11.glBindTexture(GL11.GL_TEXTURE_2D, colorTextureID);
		GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, getWidth(), getHeight(), 0, GL11.GL_RGBA, GL11.GL_INT, (java.nio.ByteBuffer) null);
		EXTFramebufferObject.glFramebufferTexture2DEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, EXTFramebufferObject.GL_COLOR_ATTACHMENT0_EXT, GL11.GL_TEXTURE_2D, colorTextureID, 0);

		depthRenderBufferID = EXTFramebufferObject.glGenRenderbuffersEXT();
		EXTFramebufferObject.glBindRenderbufferEXT(EXTFramebufferObject.GL_RENDERBUFFER_EXT, depthRenderBufferID);
		EXTFramebufferObject.glRenderbufferStorageEXT(EXTFramebufferObject.GL_RENDERBUFFER_EXT, GL14.GL_DEPTH_COMPONENT24, getWidth(), getHeight());
		EXTFramebufferObject.glFramebufferRenderbufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, EXTFramebufferObject.GL_DEPTH_ATTACHMENT_EXT, EXTFramebufferObject.GL_RENDERBUFFER_EXT, depthRenderBufferID);

		EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, 0);

		fboValid = true;
	}

	public void bindFBO() {
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, framebufferID);

		GL11.glClearColor(0F, 0F, 0F, 1F);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
	}

	public void unbindFBO() {
		EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, 0);
	}

	public void renderFBO(int width, int height) {
		if(shader != null)
			ShaderManager.getInstance().enableShader(shader);

		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, colorTextureID);

		GL11.glColor3f(1F, 1F, 1F);
		GL11.glBegin(GL11.GL_TRIANGLE_STRIP);
		{
			GL11.glTexCoord2f(0F, 0F); GL11.glVertex2f(0F, 0F);
			GL11.glTexCoord2f(1F, 0F); GL11.glVertex2f(width, 0F);
			GL11.glTexCoord2f(1F, -1F); GL11.glVertex2f(width, height);

			GL11.glTexCoord2f(0F, 0F); GL11.glVertex2f(0F, 0F);
			GL11.glTexCoord2f(0F, -1F); GL11.glVertex2f(0F, height);
			GL11.glTexCoord2f(1F, -1F); GL11.glVertex2f(width, height);
		}
		GL11.glEnd();

		GL11.glDisable(GL11.GL_TEXTURE_2D);

		if(shader != null)
			ShaderManager.getInstance().enableShader(null);
	}
}