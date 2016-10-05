package com.codingforcookies.worldbuilder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.lwjgl.glfw.GLFW;

import com.codingforcookies.worldbuilder.brush.IBrush;
import com.codingforcookies.worldbuilder.generator.SiteNeighbor;
import com.codingforcookies.worldbuilder.generator.WorldGenerator;
import com.codingforcookies.worldbuilder.generator.heightmap.HeightMaps;
import com.codingforcookies.worldbuilder.generator.heightmap.IHeightmap;

import kn.uni.voronoitreemap.j2d.Point2D;
import kn.uni.voronoitreemap.j2d.PolygonSimple;
import kn.uni.voronoitreemap.j2d.Site;

public class World {
	private static World instance;
	public static World getWorld() { return instance; }
	
	private IHeightmap heightmap = HeightMaps.get(HeightMaps.getNames().iterator().next());
	public IHeightmap getHeightmap() { return heightmap; }
	public void setHeightmap(String name) { heightmap = HeightMaps.get(name); }
	
	private WorldGenerator world;
	public boolean isGenerated() { return (world == null ? false : world.isReady()); }
	public WorldGenerator getGenerated() { return (world == null || !world.isReady() ? null : world); }
	
	public int hover = -1;
	public int getHovered() { return hover; }
	
	private int width;
	public int getWidth() { return width; }
	
	private int height;
	public int getHeight() { return height; }

	public List<Site> selection;
	public IBrush editBrush;
	public String brushOption = null;
	public double brushSize = 10F, brushPower = .02F;
	
	public void runBrush(int button, double mouseXOnMap, double mouseYOnMap) {
		if(button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
			for(Site site : selection)
				editBrush.onLeft(brushOption, site.getData(), brushPower / site.getPoint().distance(mouseXOnMap, mouseYOnMap));
		}else if(button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
			for(Site site : selection)
				editBrush.onRight(brushOption, site.getData(), brushPower / site.getPoint().distance(mouseXOnMap, mouseYOnMap));
		}
	}
	
	public World() {
		instance = this;
		
		generateRandom(1000, 1000);
	}
	
	public void generateRandom(int width, int height) {
		Random rand = new Random();
		generateSeed(rand.nextInt(), width, height);
	}

	public void generateSeed(int seed, int width, int height) {
		this.width = width;
		this.height = height;
		
		if(world != null) {
			// Generating too fast. If allowed, this causes horrible memory leaks under the wrong conditions.
			if(!world.isReady())
				return;
			world.destroy();
		}
		
		world = null;
		hover = -1;
		
		new Thread() {
			public void run() {
				world = new WorldGenerator(width, height, 60000, seed, heightmap);
			}
		}.start();
	}
	
	public void recalculate() {
		world.calculate();
	}

	/**
	 * A helper function that gets ALL neighbors. The algorithm doesn't include vertex-touching neighbors.
	 */
	public static List<SiteNeighbor> getAllNeighbors(Site site) {
		List<SiteNeighbor> neighbors = new ArrayList<SiteNeighbor>();

		for(Site neighbor : site.getNeighbours()) {
			neighbors.add(new SiteNeighbor(neighbor, true));

			for(Site neighbor2 : neighbor.getNeighbours()) {
				if(site.getNeighbours().contains(neighbor2))
					continue;

				PolygonSimple n2Polygon = neighbor2.getPolygon();
				Iterator<Point2D> n2Iterator = n2Polygon.iterator();
				Point2D n2Point = null;

				while(n2Iterator.hasNext() ? (n2Point = n2Iterator.next()) != null : false) {
					PolygonSimple polygon = site.getPolygon();
					Iterator<Point2D> iterator = polygon.iterator();
					Point2D point = null;
					while(iterator.hasNext() ? (point = iterator.next()) != null : false) {
						if(point.distance(n2Point) > 2)
							continue;
						neighbors.add(new SiteNeighbor(neighbor2, false));
						break;
					}
					break;
				}
			}
		}

		return neighbors;
	}
}