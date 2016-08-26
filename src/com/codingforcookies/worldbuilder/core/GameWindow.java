package com.codingforcookies.worldbuilder.core;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.system.MemoryUtil;

import com.codingforcookies.worldbuilder.core.element.AbstractElement;
import com.codingforcookies.worldbuilder.texture.TextureLoader;
import com.codingforcookies.worldbuilder.util.RenderLoading;

public abstract class GameWindow {
	private long window;
	private String windowTitle;
	public String getWindowTitle() { return windowTitle; }

	private boolean running = false;

	protected double mouseX = 0, mouseY = 0;

	private static final int MOUSE_BUTTONS = 3;
	private boolean[] dragging = new boolean[3];
	private double[][] draggingPos = new double[MOUSE_BUTTONS][4];

	private int elementHover = -1;
	protected List<AbstractElement> elements;
	public <T> T getElement(Class<T> clazz) {
		for(AbstractElement e : elements)
			if(clazz.isInstance(e))
				return clazz.cast(e);
		return null;
	}

	public GameWindow(String windowTitle, int width, int height, int updatesPerSecond, int framesPerSecond) {
		for(int button = 0; button < MOUSE_BUTTONS; button++) {
			dragging[button] = false;
			draggingPos[button] = new double[] { -1, -1, -1, -1 };
		}

		this.windowTitle = windowTitle;
		GLUtil.glWidth = width;
		GLUtil.glHeight = height;

		elements = new ArrayList<AbstractElement>();
		run(updatesPerSecond, framesPerSecond);
	}

	/**
	 * Begin the game loop.
	 */
	public void run(int updatesPerSecond, int framesPerSecond) {
		running = true;

		startup();

		long now;
		long lastTime = System.nanoTime();
		float deltaT = 0F;
		float deltaR = 0F;
		float tns = 1000000000F / updatesPerSecond;
		float rns = 1000000000F / framesPerSecond;
		int tps = 0;
		int fps = 0;
		long timer = System.currentTimeMillis();

		while(running) {
			now = System.nanoTime();
			deltaR += (now - lastTime) / rns;
			deltaT += (now - lastTime) / tns;
			lastTime = now;

			if(deltaR >= 1.0) {
				GLUtil.make2D();

				render();

				GLFW.glfwSwapBuffers(window);
				GLFW.glfwPollEvents();

				deltaR--;
				fps++;
			}

			if(deltaT >= 1.0) {
				update(mouseX, mouseY);
				deltaT--;
				tps++;
			}

			if(System.currentTimeMillis() - timer >= 1000) {
				GLFW.glfwSetWindowTitle(window, windowTitle + " [FPS: " + fps + " TPS: " + tps + "]");

				timer += 1000;
				fps = 0;
				tps = 0;
			}

			if(GLFW.glfwWindowShouldClose(window))
				stop();
		}

		shutdown();
	}

	public void stop() {
		running = false;
	}

	public void startup() {
		GLFWErrorCallback.createPrint(System.err).set();

		if(!GLFW.glfwInit())
			throw new IllegalStateException("Unable to initialize GLFW");

		GLFW.glfwDefaultWindowHints();
		GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
		GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_FALSE);

		window = GLFW.glfwCreateWindow(GLUtil.glWidth, GLUtil.glHeight, windowTitle, MemoryUtil.NULL, MemoryUtil.NULL);
		if(window == MemoryUtil.NULL)
			throw new RuntimeException("Failed to create the GLFW window");

		GLFW.glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
			if(key == GLFW.GLFW_KEY_GRAVE_ACCENT && action == GLFW.GLFW_RELEASE)
				keyClicked(key, mods);
		});
		
		GLFW.glfwSetWindowRefreshCallback(window, (window) -> {
			IntBuffer w = BufferUtils.createIntBuffer(1);
			IntBuffer h = BufferUtils.createIntBuffer(1);
			GLFW.glfwGetWindowSize(window, w, h);

			if(GLUtil.glWidth == w.get(0) && GLUtil.glHeight == h.get(0))
				return;
			
			
			
			/*double previousWidth = GLUtil.glWidth;
			double previousHeight = GLUtil.glHeight;
			GLUtil.glWidth = w.get(0);
			GLUtil.glHeight = h.get(0);

			for(AbstractElement e : elements) {
				double xPercent = e.getX() / previousWidth;
				double yPercent = e.getY() / previousHeight;
				double widthPercent = e.getWidth() / previousWidth;
				double heightPercent = e.getHeight() / previousHeight;

				e.setX((int)Math.floor(GLUtil.glWidth * xPercent));
				e.setY((int)Math.floor(GLUtil.glHeight * yPercent));
				e.setWidth((int)Math.floor(GLUtil.glWidth * widthPercent));
				e.setHeight((int)Math.floor(GLUtil.glHeight * heightPercent));
			}*/
		});

		GLFW.glfwSetCursorPosCallback(window, (window, mouseX, mouseY) -> {
			this.mouseX = mouseX;
			this.mouseY = mouseY;

			for(int button = 0; button < MOUSE_BUTTONS; button++) {
				if(draggingPos[button][0] == -1)
					continue;

				if(!dragging[button]) {
					if(Math.sqrt(Math.pow((draggingPos[button][0] - this.mouseX), 2) + Math.pow((draggingPos[button][1] - this.mouseY), 2)) > 5)
						dragging[button] = true;
				}

				if(dragging[button]) {
					double changeX = mouseX - draggingPos[button][2];
					double changeY = mouseY - draggingPos[button][3];

					mouseDragged(button, draggingPos[button][0], draggingPos[button][1], changeX, changeY);

					draggingPos[button][2] = mouseX;
					draggingPos[button][3] = mouseY;
				}
			}
		});

		GLFW.glfwSetMouseButtonCallback(window, (window, button, action, mods) -> {
			if(action == GLFW.GLFW_PRESS) {
				draggingPos[button][0] = draggingPos[button][2] = this.mouseX;
				draggingPos[button][1] = draggingPos[button][3] = this.mouseY;
			}else if(action == GLFW.GLFW_RELEASE) {
				if(!dragging[button])
					mouseClicked(button, mods, mouseX, mouseY);

				dragging[button] = false;
				draggingPos[button][0] = draggingPos[button][1] = draggingPos[button][2] = draggingPos[button][3] = -1;
			}
		});

		GLFW.glfwSetScrollCallback(window, (window, changeX, changeY) -> {
			mouseScroll(mouseX, mouseY, changeX, changeY);
		});

		GLFWVidMode vidmode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());

		GLFW.glfwSetWindowPos(window, (vidmode.width() - GLUtil.glWidth) / 2, (vidmode.height() - GLUtil.glHeight) / 2);

		GLFW.glfwMakeContextCurrent(window);
		GLFW.glfwSwapInterval(0);

		GLFW.glfwShowWindow(window);

		GLUtil.createCapabilities();

		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		System.out.println("GL_VERSION: " + GL11.glGetString(GL11.GL_VERSION));
		System.out.println("SHADING_LANGUAGE VERSION: " + GL11.glGetString(GL20.GL_SHADING_LANGUAGE_VERSION));
		System.out.println("EXTENSIONS: " + GL11.glGetString(GL11.GL_EXTENSIONS));

		ShaderManager.getInstance();
	}

	public void shutdown() {
		Callbacks.glfwFreeCallbacks(window);
		GLFW.glfwDestroyWindow(window);

		GLFW.glfwTerminate();
		GLFW.glfwSetErrorCallback(null).free();
	}

	public void update(double mouseX, double mouseY) {
		TextureLoader.loadQueuedTexture();

		elementHover = -1;

		RenderLoading.update();

		for(int i = elements.size() - 1; i >= 0; i--) {
			AbstractElement e = elements.get(i);
			if(mouseX > e.getX() && mouseX < e.getX() + e.getWidth() && mouseY > e.getY() && mouseY < e.getY() + e.getHeight()) {
				elementHover = i;
				break;
			}
		}

		for(int i = 0; i < elements.size(); i++) {
			AbstractElement e = elements.get(i);
			e.update(elementHover == i, mouseX - e.getX(), mouseY - e.getY());
			e.updateChildren(elementHover == i, mouseX - e.getX(), mouseY - e.getY());
		}
	}

	public void render() {
		GL11.glEnable(GL11.GL_SCISSOR_TEST);
		{
			for(AbstractElement e : elements) {
				GLUtil.renderElement(e);

				// Render left shadow
				if(e.getX() > 0) {
					GL11.glEnable(GL11.GL_BLEND);
					GL11.glBegin(GL11.GL_QUADS);
					{
						GL11.glColor4f(.2F, .2F, .2F, .5F);
						GL11.glVertex2f(e.getX(), e.getY());
						GL11.glVertex2f(e.getX(), e.getY() + e.getHeight());

						GL11.glColor4f(.2F, .2F, .2F, 0F);
						GL11.glVertex2f(e.getX() - 3F, e.getY() + e.getHeight());
						GL11.glVertex2f(e.getX() - 3F, e.getY());
					}
					GL11.glEnd();
					GL11.glDisable(GL11.GL_BLEND);
				}

				// Render bottom shadow
				if(e.getY() + e.getHeight() < GLUtil.glHeight) {
					GL11.glEnable(GL11.GL_BLEND);
					GL11.glBegin(GL11.GL_QUADS);
					{
						GL11.glColor4f(.2F, .2F, .2F, .5F);
						GL11.glVertex2f(e.getX(), e.getY() + e.getHeight());
						GL11.glVertex2f(e.getX() + e.getWidth(), e.getY() + e.getHeight());

						GL11.glColor4f(.2F, .2F, .2F, 0F);
						GL11.glVertex2f(e.getX() + e.getWidth(), e.getY() + e.getHeight() + 3F);
						GL11.glVertex2f(e.getX(), e.getY() + e.getHeight() + 3F);
					}
					GL11.glEnd();
					GL11.glDisable(GL11.GL_BLEND);
				}
			}
		}
		GL11.glDisable(GL11.GL_SCISSOR_TEST);
	}


	public void mouseClicked(int button, int mods, double mouseX, double mouseY) {
		if(elementHover == -1)
			return;
		AbstractElement p = elements.get(elementHover);
		p.onClick(button, mods, mouseX - p.getX(), mouseY - p.getY());
	}

	public void mouseDragged(int button, double initialX, double initialY, double mouseX, double mouseY) {
		if(elementHover == -1)
			return;
		elements.get(elementHover).onDrag(button, mouseX, mouseY);
	}

	public void mouseScroll(double mouseX, double mouseY, double changeX, double changeY) {
		if(elementHover == -1)
			return;
		elements.get(elementHover).onScroll(changeX, changeY);
	}

	public abstract void keyClicked(int button, int mods);
}