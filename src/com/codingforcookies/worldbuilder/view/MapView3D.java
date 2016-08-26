package com.codingforcookies.worldbuilder.view;

import java.awt.Rectangle;

import org.lwjgl.opengl.GL11;

import com.codingforcookies.worldbuilder.World;
import com.codingforcookies.worldbuilder.generator.SiteBiome;
import com.codingforcookies.worldbuilder.generator.SiteData;
import com.codingforcookies.worldbuilder.generator.SiteType;

import kn.uni.voronoitreemap.datastructure.OpenList;
import kn.uni.voronoitreemap.j2d.PolygonSimple;
import kn.uni.voronoitreemap.j2d.Site;

public class MapView3D implements IMapView {
	private static int MAP_RESOLUTION = 5;
	private static float[][][] terrain;
	
	@Override
	public boolean is3D() { return true; }

	@Override
	public void calculate(World world) {
		OpenList sites = World.getWorld().getGenerated().getSites();
		
		float[][][] tempTerrain = new float[(int)world.getWidth() / MAP_RESOLUTION][(int)world.getHeight() / MAP_RESOLUTION][8];
		
		for(int z = 0; z < (int)world.getHeight() / MAP_RESOLUTION; z++) {
			for(int x = 0; x < (int)world.getWidth() / MAP_RESOLUTION; x++) {
				tempTerrain[x][z][0] = 0F;
				tempTerrain[x][z][1] = 0F;
				tempTerrain[x][z][2] = x * MAP_RESOLUTION;
				tempTerrain[x][z][3] = 0F;
				tempTerrain[x][z][4] = z * MAP_RESOLUTION;
				tempTerrain[x][z][5] = .26F;
				tempTerrain[x][z][6] = .26F;
				tempTerrain[x][z][7] = .48F;
			}
		}
		
		for(Site site : sites) {
			PolygonSimple polygon = site.getPolygon();
			Rectangle bounds = polygon.getBounds();

			for(int z = bounds.y; z < bounds.y + bounds.height; z++) {
				for(int x = bounds.x; x < bounds.x + bounds.width; x++) {
					int mapX = x / MAP_RESOLUTION;
					int mapZ = z / MAP_RESOLUTION;
					
					if(!polygon.contains(x, z))
						continue;

					SiteData data = site.getData();

					tempTerrain[mapX][mapZ][3] = (float)(data.height < 0F ? 0F : (data.height * 60F) + 1F);

					SiteType type = data.getType();
					SiteBiome biome = data.getBiome();

					float c = data.height / (type == SiteType.WATER ? 12F : -10F);
					tempTerrain[mapX][mapZ][5] = biome.color[0] + c;
					tempTerrain[mapX][mapZ][6] = biome.color[1] + c;
					tempTerrain[mapX][mapZ][7] = biome.color[2] + c;

					tempTerrain[mapX][mapZ][0] = 1F;
					tempTerrain[mapX][mapZ][1] = site.getData().getIndex();
				}
			}
		}

		terrain = tempTerrain;
	}
	
	@Override
	public void render(World world, float[] translation, int[] viewSize, double mouseX, double mouseY) {
		if(terrain == null)
			return;
		
		GL11.glColor3f(0F, 0F, 0F);
		
		int xMax = world.getWidth() / MAP_RESOLUTION - 1;
		int zMax = world.getHeight() / MAP_RESOLUTION - 1;
		
		for(int x = 0; x < xMax; x++) {
			for(int z = 0; z < zMax; z++) {
				if(terrain[x][z][0] == 0)
					GL11.glBegin(GL11.GL_LINE_STRIP);
				else
					GL11.glBegin(GL11.GL_QUADS);
				{
					GL11.glColor3f(terrain[x][z][5], terrain[x][z][6], terrain[x][z][7]);

					GL11.glNormal3f(terrain[x][z + 1][2], terrain[x][z + 1][3], terrain[x][z + 1][4]);
					GL11.glVertex3f(terrain[x][z + 1][2], terrain[x][z + 1][3], terrain[x][z + 1][4]);

					GL11.glNormal3f(terrain[x + 1][z + 1][2], terrain[x + 1][z + 1][3], terrain[x + 1][z + 1][4]);
					GL11.glVertex3f(terrain[x + 1][z + 1][2], terrain[x + 1][z + 1][3], terrain[x + 1][z + 1][4]);

					GL11.glNormal3f(terrain[x + 1][z][2], terrain[x + 1][z][3], terrain[x + 1][z][4]);
					GL11.glVertex3f(terrain[x + 1][z][2], terrain[x + 1][z][3], terrain[x + 1][z][4]);

					GL11.glNormal3f(terrain[x][z][2], terrain[x][z][3], terrain[x][z][4]);
					GL11.glVertex3f(terrain[x][z][2], terrain[x][z][3], terrain[x][z][4]);
				}
				GL11.glEnd();
				
				// Draw the black trim. Prevents seeing through the mountains due to cullface.
				if(z == 0) {
					GL11.glBegin(GL11.GL_QUADS);
					{
						GL11.glColor3f(0F, 0F, 0F);
						GL11.glVertex3f(terrain[x + 1][z][2], -10F, terrain[x][z][4]);
						GL11.glVertex3f(terrain[x][z][2], -10F, terrain[x][z][4]);
						GL11.glVertex3f(terrain[x][z][2], terrain[x][z][3], terrain[x][z][4]);
						GL11.glVertex3f(terrain[x + 1][z][2], terrain[x + 1][z][3], terrain[x][z][4]);
					}
					GL11.glEnd();
				}
				
				if(z == zMax - 1) {
					GL11.glBegin(GL11.GL_QUADS);
					{
						GL11.glColor3f(0F, 0F, 0F);
						GL11.glVertex3f(terrain[x][z][2], -10F, terrain[x][z + 1][4]);
						GL11.glVertex3f(terrain[x + 1][z][2], -10F, terrain[x][z + 1][4]);
						GL11.glVertex3f(terrain[x + 1][z][2], terrain[x + 1][z + 1][3], terrain[x][z + 1][4]);
						GL11.glVertex3f(terrain[x][z][2], terrain[x][z + 1][3], terrain[x][z + 1][4]);
					}
					GL11.glEnd();
				}
				
				if(x == 0) {
					GL11.glBegin(GL11.GL_QUADS);
					{
						GL11.glColor3f(0F, 0F, 0F);
						GL11.glVertex3f(terrain[x][z][2], -10F, terrain[x][z][4]);
						GL11.glVertex3f(terrain[x][z][2], -10F, terrain[x][z + 1][4]);
						GL11.glVertex3f(terrain[x][z][2], terrain[x][z + 1][3], terrain[x][z + 1][4]);
						GL11.glVertex3f(terrain[x][z][2], terrain[x][z][3], terrain[x][z][4]);
					}
					GL11.glEnd();
				}
				
				if(x == xMax - 1) {
					GL11.glBegin(GL11.GL_QUADS);
					{
						GL11.glColor3f(0F, 0F, 0F);
						GL11.glVertex3f(terrain[x][z][2] + MAP_RESOLUTION, -10F, terrain[x + 1][z + 1][4]);
						GL11.glVertex3f(terrain[x][z][2] + MAP_RESOLUTION, -10F, terrain[x + 1][z][4]);
						GL11.glVertex3f(terrain[x][z][2] + MAP_RESOLUTION, terrain[x + 1][z][3], terrain[x + 1][z][4]);
						GL11.glVertex3f(terrain[x][z][2] + MAP_RESOLUTION, terrain[x + 1][z + 1][3], terrain[x + 1][z + 1][4]);
					}
					GL11.glEnd();
				}
			}
			
			// Render the quad under the landscape.
			GL11.glBegin(GL11.GL_QUADS);
			{
				GL11.glColor3f(0F, 0F, 0F);
				
				GL11.glVertex3f(0F, -10F, 0F);
				GL11.glVertex3f(world.getWidth() - MAP_RESOLUTION, -10F, 0F);
				GL11.glVertex3f(world.getWidth() - MAP_RESOLUTION, -10F, world.getHeight() - MAP_RESOLUTION);
				GL11.glVertex3f(0F, -10F, world.getHeight() - MAP_RESOLUTION);
			}
			GL11.glEnd();
		}
	}

	@Override
	public void onClick(World world, int button, int mods, double mouseXOnMap, double mouseYOnMap) { }

	@Override
	public void onDrag(World world, int button, double mouseXOnMap, double mouseYOnMap) { }
}