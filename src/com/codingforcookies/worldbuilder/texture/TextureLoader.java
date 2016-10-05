package com.codingforcookies.worldbuilder.texture;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class TextureLoader {
	public static List<QueueTexture> queuedTextures = new ArrayList<QueueTexture>(); 
	public static HashMap<String, Texture> textures = new HashMap<String, Texture>();
	
	/**
	 * Returns a loaded texture.
	 */
	public static Texture getTexture(String texture) {
		return textures.get(texture);
	}
	
	/**
	 * Load a texture from a file.
	 */
	public static void loadFile(String name, Object file, Runnable loadedCallback) {
		BufferedImage texture;
		try {
			BufferedImage temptexture;
			if(file instanceof File)
				temptexture = ImageIO.read((File)file);
			else if(file instanceof InputStream)
				temptexture = ImageIO.read((InputStream)file);
			else if(file instanceof String) {
				temptexture = ImageIO.read(Thread.currentThread().getContextClassLoader().getResourceAsStream("resources/" + String.valueOf(file) + ".png"));
			}else
				return;
			
			texture = new BufferedImage(temptexture.getWidth(), temptexture.getHeight(), BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = texture.createGraphics();
			g.drawImage(temptexture, 0, 0, null);
			g.dispose();
		} catch(Exception e) {
			System.err.println(String.valueOf(file));
			e.printStackTrace();
			return;
		}
		
		queuedTextures.add(new QueueTexture(name, texture, loadedCallback));
		System.out.println("[Texture Queue] Added " + name);
	}
	
	public static void loadQueuedTexture() {
		if(queuedTextures.size() == 0)
			return;
		
		QueueTexture tex = queuedTextures.get(0);
		textures.put(tex.name, new Texture(loadTexture(tex.texture)));
		tex.loadedCallback.run();
		queuedTextures.remove(0);
		
		System.out.println("[Texture Queue] Loaded " + tex.name);
	}
	
	/**
	 * 3 = RGB<br />
	 * 4 = RGBA
	 */
	private static final int BYTES_PER_PIXEL = 4;
	private static int loadTexture(BufferedImage image) {
		int[] pixels = new int[image.getWidth() * image.getHeight()];
		image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());

		ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * BYTES_PER_PIXEL);

		for(int y = 0; y < image.getHeight(); y++) {
			for(int x = 0; x < image.getWidth(); x++) {
				int pixel = pixels[y * image.getWidth() + x];
				buffer.put((byte)((pixel >> 16) & 0xFF));
				buffer.put((byte)((pixel >> 8) & 0xFF));
				buffer.put((byte)(pixel & 0xFF));
				buffer.put((byte)((pixel >> 24) & 0xFF));
			}
		}

		buffer.flip();
		
		int textureID = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
		
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
		
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, image.getWidth(), image.getHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
		
		return textureID;
	}
}