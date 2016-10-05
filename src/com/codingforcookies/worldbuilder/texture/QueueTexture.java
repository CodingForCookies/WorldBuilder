package com.codingforcookies.worldbuilder.texture;

import java.awt.image.BufferedImage;

public class QueueTexture {
	public String name = "";
	public BufferedImage texture;
	public Runnable loadedCallback;
	
	public QueueTexture(String name, BufferedImage texture, Runnable loadedCallback) {
		this.name = name;
		this.texture = texture;
		this.loadedCallback = loadedCallback;
	}
}