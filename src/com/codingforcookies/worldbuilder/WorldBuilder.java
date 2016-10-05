package com.codingforcookies.worldbuilder;

import java.io.File;

import com.codingforcookies.raisin.core.GameWindow;
import com.codingforcookies.raisin.element.AbstractView;
import com.codingforcookies.raisin.util.GLUtil;
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
		
		views.add(new WorldBuilderView());
	}
	
	class WorldBuilderView extends AbstractView {
		WorldBuilderView() {
			WorldPanel worldElement;
			addElement(worldElement = new WorldPanel(0, 30, GLUtil.glWidth, GLUtil.glHeight - 30));
			addElement(new ToolbarPanel(0, 0, GLUtil.glWidth, 30).setBackground(.3F, .3F, .3F));
			addElement(new EditorPanel(GLUtil.glWidth - 200, 30, 200, 130).setBackground(.35F, .35F, .35F));
			addElement(new SidebarPanel(worldElement.getElement(WorldElement.class), GLUtil.glWidth - 200, 160, 200, 20).setBackground(.4F, .4F, .4F));
		}
	}
	
	/*

	@Override
	public void keyClicked(int button, int mods) {
		if(button == GLFW.GLFW_KEY_GRAVE_ACCENT)
			World.getWorld().generateRandom(World.getWorld().getWidth(), World.getWorld().getHeight());
	}*/
}