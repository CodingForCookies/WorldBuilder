package com.codingforcookies.worldbuilder;

import org.lwjgl.opengl.GL11;

import com.codingforcookies.worldbuilder.core.Panel;
import com.codingforcookies.worldbuilder.core.element.BoxElement;
import com.codingforcookies.worldbuilder.generator.SiteBiome;
import com.codingforcookies.worldbuilder.generator.SiteData;
import com.codingforcookies.worldbuilder.generator.SiteType;
import com.codingforcookies.worldbuilder.util.FontRenderer;

import kn.uni.voronoitreemap.datastructure.OpenList;

public class SidebarPanel extends Panel {
	private final WorldElement worldElement;

	private BoxElement box;
	private boolean open = false;
	
	public SidebarPanel(WorldBuilder worldBuilder, int x, int y, int width, int height) {
		super(x, y, width, height);
		
		worldElement = worldBuilder.getElement(WorldPanel.class).getElement(WorldElement.class);
		
		addElement(box = new BoxElement(15, 95, 180, 80));
		box.setHidden(true);
		
		box.addElement(new BoxElement(0, 0, 90, 20, SiteBiome.SNOW.color));
		box.addElement(new BoxElement(90, 0, 30, 20, SiteBiome.TUNDRA.color));
		box.addElement(new BoxElement(120, 0, 30, 20, SiteBiome.BARE.color));
		box.addElement(new BoxElement(150, 0, 30, 20, SiteBiome.SCORCHED.color));

		box.addElement(new BoxElement(0, 20, 60, 20, SiteBiome.TAIGA.color));
		box.addElement(new BoxElement(60, 20, 60, 20, SiteBiome.SHRUBLAND.color));
		box.addElement(new BoxElement(120, 20, 60, 20, SiteBiome.TEMPERATE_DESERT.color));

		box.addElement(new BoxElement(0, 40, 30, 20, SiteBiome.TEMPERATE_RAIN_FOREST.color));
		box.addElement(new BoxElement(30, 40, 60, 20, SiteBiome.TEMPERATE_DECIDUOUS_FOREST.color));
		box.addElement(new BoxElement(90, 40, 60, 20, SiteBiome.GRASSLAND.color));
		box.addElement(new BoxElement(150, 40, 30, 20, SiteBiome.TEMPERATE_DESERT_2.color));

		box.addElement(new BoxElement(0, 60, 60, 20, SiteBiome.TROPICAL_RAIN_FOREST.color));
		box.addElement(new BoxElement(60, 60, 60, 20, SiteBiome.TROPICAL_SEASONAL_FOREST.color));
		box.addElement(new BoxElement(120, 60, 30, 20, SiteBiome.GRASSLAND_2.color));
		box.addElement(new BoxElement(150, 60, 30, 20, SiteBiome.SUBPROPICAL_DESERT.color));
	}
	
	@Override
	public void update(boolean mouseIn, double mouseX, double mouseY) {
		super.update(mouseIn, mouseX, mouseY);

		if(worldElement.is3D() || !World.getWorld().isGenerated()) {
			setHeight(30);
			return;
		}
		
		int hovered = World.getWorld().getHovered();
		if(open) {
			if(hovered != -1)
				setHeight(182);
			else{
				setHeight(20);
				open = false;
			}
		}else if(hovered != -1)
			open = true;
		else
			setHeight(20);
		
		box.setHidden(getHeight() < 182);
	}

	@Override
	public void render() {
		super.render();
		
		GL11.glColor3f(1F, 1F, 1F);

		if(!World.getWorld().isGenerated()) {
			FontRenderer.push(16F);
			{
				FontRenderer.draw("Generating", (getWidth() - FontRenderer.getStringWidth("Generating")) / 2F, 24F);
			}
			FontRenderer.pop();
			return;
		}
		
		if(worldElement.is3D()) {
			FontRenderer.push(16F);
			{
				FontRenderer.draw("3D View", (getWidth() - FontRenderer.getStringWidth("3D View")) / 2F, 24F);
			}
			FontRenderer.pop();
			return;
		}

		OpenList sites = World.getWorld().getGenerated().getSites();
		int hovered = World.getWorld().getHovered();
		
		if(open) {
			if(hovered != -1) {
				SiteData data = sites.get(hovered).getData();
				
				SiteType type = data.getType();
				String typeName = type.name;

				GL11.glColor3f(.5F, .5F, .5F);
				
				GL11.glBegin(GL11.GL_LINE_STRIP);
				{
					GL11.glVertex2f(4F, 40F);
					GL11.glVertex2f(getWidth() - 8F, 40F);
				}
				GL11.glEnd();
	
				GL11.glColor3f(1F, 1F, 1F);
				
				FontRenderer.push(16F);
				{
					FontRenderer.draw(typeName, (getWidth() - FontRenderer.getStringWidth(typeName)) / 2F, 24F);
				}
				FontRenderer.pop();
				
				FontRenderer.draw(data.getBiome().name, (getWidth() - FontRenderer.getStringWidth(data.getBiome().name)) / 2F, 34F);
				
				FontRenderer.draw("Height: " + data.height, 4F, 60F);
	
				FontRenderer.draw("To Sea: " + (int)data.distanceToWater + "px", 4F, 75F);

				if(data.height > 0) {
					GL11.glTranslatef(5F, 90F, 0F);
					
					float fH = (data.height > 1F ? 1F : data.height) * 80;
					
					GL11.glTranslatef(0F, 80F - fH, 0F);
					
					GL11.glBegin(GL11.GL_TRIANGLE_STRIP);
					{
						GL11.glVertex2f(0F, 0F);
						GL11.glVertex2f(8F, 4F);
						GL11.glVertex2f(0F, 4F);
					}
					GL11.glEnd();
					
					GL11.glTranslatef(0F, -80F + fH, 0F);
					
					float fM = data.moisture / 70F * 180F;
					
					GL11.glTranslatef(190F - fM, -4F, 0F);
					
					GL11.glBegin(GL11.GL_TRIANGLE_STRIP);
					{
						GL11.glVertex2f(0F, 0F);
						GL11.glVertex2f(-4F, 0F);
						GL11.glVertex2f(0F, 8F);
					}
					GL11.glEnd();
					
					GL11.glTranslatef(-190F + fM, 4F, 0F);
					
					GL11.glTranslatef(-5F, -90F, 0F);
				}
			}
		}else{
			if(World.getWorld().getHovered() != -1) {
				SiteData data = sites.get(World.getWorld().getHovered()).getData();
				String typeName = data.getType().name;

				GL11.glColor3f(.7F, .7F, .7F);
				FontRenderer.draw(data.getBiome().name, 4F, getHeight() - 4F);
				FontRenderer.draw(typeName, getWidth() - FontRenderer.getStringWidth(typeName) - 4F, getHeight() - 4F);
			}
		}
	}
}