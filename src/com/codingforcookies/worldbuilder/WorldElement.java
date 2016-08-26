package com.codingforcookies.worldbuilder;

import java.util.ArrayList;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import com.codingforcookies.worldbuilder.core.element.AbstractElement;
import com.codingforcookies.worldbuilder.core.element.ButtonToggleElement;
import com.codingforcookies.worldbuilder.util.RenderLoading;
import com.codingforcookies.worldbuilder.view.IMapView;
import com.codingforcookies.worldbuilder.view.MapView2D;
import com.codingforcookies.worldbuilder.view.MapView2DHeightmap;
import com.codingforcookies.worldbuilder.view.MapView2DTemperature;
import com.codingforcookies.worldbuilder.view.MapView3D;
import com.codingforcookies.worldbuilder.view.MapViewRender;

import kn.uni.voronoitreemap.datastructure.OpenList;
import kn.uni.voronoitreemap.j2d.PolygonSimple;
import kn.uni.voronoitreemap.j2d.Site;

public class WorldElement extends AbstractElement {
	class ViewButton extends ButtonToggleElement {
		Class<?> viewClass;
		
		public ViewButton(int x, int y, int width, int height, Class<?> viewClass) {
			super(x, y, width, height);
			
			this.viewClass = viewClass;
		}

		@Override
		public boolean isToggled() {
			return viewClass.isInstance(mapView);
		}

		@Override
		public void tryToggle(boolean current) {
			try {
				mapView = (IMapView)viewClass.newInstance();
				
				WorldElement.this.set3D(mapView.is3D());
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}
	
	private final World world;
	
	private boolean isCalculating = false;
	private long lastTerrainUpdate = 0;
	private long nextTerrainUpdateDelay = 0;
	
	public IMapView mapView = new MapView2D();
	
	private double mouseX, mouseY;
	private float[] translate2D = new float[] { 0F, 0F };

	private boolean stopTimer = false;
	private float threeDTimer = 0F;
	private float[] translate3D = new float[] { 0F, 0F, 0F };
	private float[] rotate3D = new float[] { 0F, 0F, 0F };
	private float scale3D = 1F;

	public WorldElement(World world, int x, int y, int width, int height) {
		super(x, y, width, height);
		this.world = world;

		addElement(new ViewButton(5, 5, 20, 20, MapView2D.class).setIcon("view/globe"));
		addElement(new ViewButton(30, 5, 20, 20, MapView2DTemperature.class).setIcon("view/weather"));
		addElement(new ViewButton(55, 5, 20, 20, MapView2DHeightmap.class).setIcon("view/heightmap"));
		
		addElement(new ViewButton(5, 30, 20, 20, MapView3D.class).setIcon("view/3d"));
		
		addElement(new ViewButton(5, 55, 20, 20, MapViewRender.class).setIcon("view/render"));
	}

	@Override
	public void update(boolean mouseIn, double mouseX, double mouseY) {
		super.update(mouseIn, mouseX, mouseY);
		
		if(!world.isGenerated()) {
			lastTerrainUpdate = nextTerrainUpdateDelay = 0;
			isCalculating = false;
			return;
		}
		
		if(System.currentTimeMillis() > lastTerrainUpdate + (nextTerrainUpdateDelay / 2))
			calculateTerrain();
		
		if(is3D() != mapView.is3D())
			set3D(mapView.is3D());
		
		if(is3D()) {
			if(!stopTimer)
				threeDTimer += 1F;
			return;
		}else if(stopTimer)
			stopTimer = false;

		mouseX -= translate2D[0];
		mouseY -= translate2D[1];
		
		this.mouseX = mouseX;
		this.mouseY = mouseY;
		
		if(mapView instanceof MapView2D && mouseIn) {
			world.selection = new ArrayList<Site>();
			
			OpenList sites = World.getWorld().getGenerated().getSites();
			for(int i = 0; i < sites.size; i++) {
				Site site = sites.array[i];
				PolygonSimple polygon = site.getPolygon();
				if(polygon == null)
					continue;
				
				if(polygon.contains(mouseX, mouseY))
					world.hover = i;
				
				if(site.getPoint().distance(mouseX, mouseY) <= world.brushSize)
					world.selection.add(site);
			}
		}
	}

	@Override
	public void render() {
		if(!world.isGenerated()) {
			RenderLoading.draw(getWidth() / 2, getHeight() / 2, 75F);
			return;
		}

		if(!mapView.is3D())
			GL11.glTranslatef(translate2D[0], translate2D[1], 0F);
		else{
			GL11.glTranslatef(-world.getWidth() / 2F, 40F, -world.getHeight() * 2F);
			GL11.glRotatef(20F, 1F, 0F, 0F);

			GL11.glTranslatef(translate3D[0], translate3D[1], translate3D[2]);

			GL11.glTranslatef(world.getWidth() / 2, 0F, world.getHeight() / 2);
			{
				GL11.glScalef(scale3D, scale3D, scale3D);
				
				GL11.glRotatef(rotate3D[0] + threeDTimer / 2F, 0F, 1F, 0F);
				GL11.glRotatef(rotate3D[1], 1F, 0F, 0F);
				GL11.glRotatef(rotate3D[2], 0F, 0F, 1F);
				
				GL11.glEnable(GL11.GL_LIGHT0);
				{
					GL11.glLightfv(GL11.GL_LIGHT0, GL11.GL_AMBIENT, new float[] { -0.1F, -0.1F, -0.1F, 0F });
					GL11.glLightfv(GL11.GL_LIGHT0, GL11.GL_DIFFUSE, new float[] { 0.001F, 0.001F, 0.001F, 1F });
					GL11.glLightfv(GL11.GL_LIGHT0, GL11.GL_SPECULAR, new float[] { 1F, 1F, 1F, 1F });

					GL11.glLightfv(GL11.GL_LIGHT0, GL11.GL_POSITION, new float[] { world.getWidth() / 2, 100F, world.getHeight() / 2, 1F });
				}

				GL11.glLightModeli(GL11.GL_LIGHT_MODEL_TWO_SIDE, GL11.GL_TRUE);
				GL11.glColorMaterial(GL11.GL_FRONT_AND_BACK, GL11.GL_AMBIENT_AND_DIFFUSE);

				GL11.glEnable(GL11.GL_COLOR_MATERIAL);
				GL11.glEnable(GL11.GL_LIGHTING);
			}
			GL11.glTranslatef(-world.getWidth() / 2, 0F, -world.getHeight() / 2);
		}
		
		mapView.render(world, (!is3D() ? new float[] { translate2D[0], translate2D[1] } : translate3D), new int[] { getWidth(), getHeight() }, mouseX, mouseY);

		if(!mapView.is3D())
			GL11.glTranslatef(-translate2D[0], -translate2D[1], 0F);
		else
			GL11.glTranslatef(-translate3D[0], -translate3D[1], -translate3D[2]);
	}

	private void calculateTerrain() {
		// Prevent overlapping generation
		if(isCalculating) {
			if(System.currentTimeMillis() > lastTerrainUpdate + 5000) {
				System.err.println("Its been a while since the terrain updated, did it get stuck?");
				lastTerrainUpdate = System.currentTimeMillis();
			}
			
			return;
		}
		
		isCalculating = true;
		
		new Thread() {
			public void run() {
				if(!World.getWorld().isGenerated()) {
					isCalculating = false;
					return;
				}
				
				world.recalculate();
				
				mapView.calculate(world);
				
				nextTerrainUpdateDelay = (System.currentTimeMillis() - lastTerrainUpdate) + nextTerrainUpdateDelay;
				nextTerrainUpdateDelay /= 2;
				
				if(nextTerrainUpdateDelay > 100000)
					nextTerrainUpdateDelay = 1000;
				else if(nextTerrainUpdateDelay > 1000)
					System.err.println("Terrain updates are taking a while: " + nextTerrainUpdateDelay + " milliseconds on average!");
				
				lastTerrainUpdate = System.currentTimeMillis();
				isCalculating = false;
			}
		}.start();
	}

	@Override
	public boolean onClick(int button, int mods, double x, double y) {
		if(super.onClick(button, mods, x, y))
			return true;
		
		if(!world.isGenerated())
			return false;
		
		mapView.onClick(world, button, mods, mouseX, mouseY);
		
		return true;
	}

	@Override
	public boolean onDrag(int button, double x, double y) {
		if(super.onDrag(button, x, y))
			return true;
		
		mapView.onDrag(world, button, mouseX, mouseY);
		
		if(!is3D()) {
			if(button == GLFW.GLFW_MOUSE_BUTTON_MIDDLE) {
				translate2D[0] += x / 2;
				translate2D[1] += y / 2;
				
				if(translate2D[0] > getWidth() / 2)
					translate2D[0] = getWidth() / 2;
				else if(translate2D[0] < getWidth() / 2 - world.getWidth())
					translate2D[0] = getWidth() / 2 - world.getWidth();
				
				if(translate2D[1] > getHeight() / 2)
					translate2D[1] = getHeight() / 2;
				else if(translate2D[1] < getHeight() / 2 - world.getHeight())
					translate2D[1] = getHeight() / 2 - world.getHeight();
			}
		}else{
			if(button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
				translate3D[0] += x / 2;
				translate3D[1] -= y / 2;
			}else if(button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
				rotate3D[0] += x / 10;
				//rotate[1] += y / 10;
				
				stopTimer = true;
			}
		}
		
		return true;
	}
	
	public boolean onScroll(double x, double y) {
		if(super.onScroll(x, y))
			return true;
		
		if(is3D()) {
			scale3D += y / 20;
			
			if(scale3D < .5F)
				scale3D = .5F;
		}
		
		return true;
	}
}