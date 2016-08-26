package com.codingforcookies.worldbuilder;

import java.io.File;

import org.lwjgl.glfw.GLFW;

import com.codingforcookies.worldbuilder.core.GLUtil;
import com.codingforcookies.worldbuilder.core.GameWindow;
import com.codingforcookies.worldbuilder.texture.TextureLoader;
import com.codingforcookies.worldbuilder.util.FontRenderer;

public class WorldBuilder extends GameWindow {
	public static void main(String[] args) {
		System.setProperty("org.lwjgl.librarypath", new File("natives").getAbsolutePath());
		
		new WorldBuilder();
	}
	
	WorldBuilder() {
		super("World Builder", 1000, 700, 20, 120);
	}

	@Override
	public void startup() {
		super.startup();
		
		TextureLoader.loadFile("font", "font/default", new Runnable() {
			public void run() {
				FontRenderer.font = TextureLoader.getTexture("font");
			}
		});

		new World();
		
		elements.add(new WorldPanel(0, 30, GLUtil.glWidth, GLUtil.glHeight - 30));
		elements.add(new ToolbarPanel(this, 0, 0, GLUtil.glWidth, 30).setBackground(.3F, .3F, .3F));
		elements.add(new EditorPanel(GLUtil.glWidth - 200, 30, 200, 130).setBackground(.35F, .35F, .35F));
		elements.add(new SidebarPanel(this, GLUtil.glWidth - 200, 160, 200, 20).setBackground(.4F, .4F, .4F));
	}

	@Override
	public void keyClicked(int button, int mods) {
		if(button == GLFW.GLFW_KEY_GRAVE_ACCENT)
			World.getWorld().generateRandom(World.getWorld().getWidth(), World.getWorld().getHeight());
	}
}