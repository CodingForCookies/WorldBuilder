package com.codingforcookies.worldbuilder.view;

import java.util.Iterator;

import org.lwjgl.opengl.GL11;

import com.codingforcookies.worldbuilder.World;
import com.codingforcookies.worldbuilder.brush.BrushType;
import com.codingforcookies.worldbuilder.generator.SiteBiome;
import com.codingforcookies.worldbuilder.generator.SiteData;
import com.codingforcookies.worldbuilder.generator.SiteType;

import kn.uni.voronoitreemap.datastructure.OpenList;
import kn.uni.voronoitreemap.j2d.Point2D;
import kn.uni.voronoitreemap.j2d.PolygonSimple;
import kn.uni.voronoitreemap.j2d.Site;

public class MapView2D implements IMapView {
	public float[] getColor(SiteData data) {
		SiteType type = data.getType();
		SiteBiome biome = data.getBiome();
		
		float c = data.height / (type == SiteType.WATER ? 12F : -10F);
		return new float[] { biome.color[0] + c, biome.color[1] + c, biome.color[2] + c };
	}
	
	@Override
	public boolean is3D() { return false; }

	@Override
	public void calculate(World world) { }
	
	@Override
	public void render(World world, float[] translation, int[] viewSize, double mouseX, double mouseY) {
		OpenList sites = World.getWorld().getGenerated().getSites();
		for(int i = 0; i < sites.size; i++) {
			Site site = sites.array[i];
			if(site.x < -translation[0] - 10 || site.y < -translation[1] - 10
					|| site.x > -translation[0] + viewSize[0] + 10 || site.y > -translation[1] + viewSize[1] + 10)
				continue;
			
			SiteData data = site.getData();

			float[] c = getColor(data);
			GL11.glColor3f(c[0], c[1], c[2]);

			GL11.glBegin(GL11.GL_POLYGON);
			{
				PolygonSimple polygon = site.getPolygon();
				Iterator<Point2D> iterator = polygon.iterator();
				Point2D point = null;

				while(iterator.hasNext() ? (point = iterator.next()) != null : false)
					GL11.glVertex2d(point.x, point.y);
			}
			GL11.glEnd();
		}
		
		if(world.editBrush != null && world.editBrush.getType(world.brushOption) == BrushType.CIRCULAR) {
			GL11.glColor3f(1F, 0F, 0F);
			
			GL11.glTranslated(mouseX, mouseY, 0F);
			
			GL11.glBegin(GL11.GL_LINE_LOOP);
			for(int i = 0; i <= 300; i++){
				double angle = 2 * Math.PI * i / 300;
				GL11.glVertex2d(world.brushSize * Math.cos(angle), world.brushSize * Math.sin(angle));
			}
			GL11.glEnd();
			
			GL11.glTranslated(-mouseX, -mouseY, 0F);
		}else if(world.hover != -1) {
			Site site = sites.array[world.hover];

			GL11.glColor3f(1F, 0F, 0F);

			GL11.glBegin(GL11.GL_LINE_LOOP);
			{
				PolygonSimple polygon = site.getPolygon();
				Iterator<Point2D> iterator = polygon.iterator();
				Point2D point = null;
				while(iterator.hasNext() ? (point = iterator.next()) != null : false)
					GL11.glVertex2d(point.x, point.y);
			}
			GL11.glEnd();
			
			if(world.editBrush != null && world.editBrush.getType(world.brushOption) == BrushType.POINT) {
				if(site.getData().downhill != null) {
					GL11.glColor3f(1F, 1F, 1F);

					GL11.glBegin(GL11.GL_LINE_STRIP);
					{
						SiteData d = site.getData();
						do {
							Point2D center = d.site.getPolygon().getCentroid();
							GL11.glVertex2d(center.x, center.y);
						} while((d = d.downhill) != null);
					}
					GL11.glEnd();
				}
			}
		}
	}

	@Override
	public void onClick(World world, int button, int mods, double mouseXOnMap, double mouseYOnMap) {
		if(world.editBrush != null)
			world.runBrush(button, mouseXOnMap, mouseYOnMap);
	}

	@Override
	public void onDrag(World world, int button, double mouseXOnMap, double mouseYOnMap) {
		if(world.editBrush != null)
			world.runBrush(button, mouseXOnMap, mouseYOnMap);
	}
}