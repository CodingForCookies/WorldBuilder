package com.codingforcookies.worldbuilder.view;

import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import com.codingforcookies.worldbuilder.World;
import com.codingforcookies.worldbuilder.generator.SiteBiome;
import com.codingforcookies.worldbuilder.generator.SiteNeighbor;

import kn.uni.voronoitreemap.datastructure.OpenList;
import kn.uni.voronoitreemap.j2d.PolygonSimple;
import kn.uni.voronoitreemap.j2d.Site;

public class MapViewRender implements IMapView {
	private int update = 0;

	private ByteBuffer pixelBuffer;

	@Override
	public boolean is3D() { return false; }

	@Override
	public void calculate(World world) {
		if(update++ % 10 != 0)
			return;

		BufferedImage image = new BufferedImage(world.getWidth(), world.getHeight(), BufferedImage.TYPE_INT_RGB);
		
		OpenList sites = world.getGenerated().getSites();
		for(int i = 0; i < sites.size; i++) {
			Site site = sites.array[i];
			PolygonSimple polygon = site.getPolygon();
			if(polygon == null)
				continue;
			
			SiteBiome biome = site.getData().getBiome();
			boolean water = site.getData().height <= 0F;
			
			float tint;
			if(!water) {
				tint = site.getData().height / 5F + 1F;
				float shadowDistance = Float.MIN_VALUE;
				
				for(SiteNeighbor n : World.getAllNeighbors(site)) {
					Site neighbor = n.site;
					if(neighbor.x > site.x || neighbor.y > site.y)
						continue;
					if(neighbor.getData().height > site.getData().height) {
						float d = neighbor.getData().height - site.getData().height;
						if(d > shadowDistance)
							shadowDistance = d;
					}
				}
				
				if(shadowDistance != Float.MIN_VALUE)
					tint -= shadowDistance * 1F + .05F;
				else
					tint += .1F;
			}else
				tint = site.getData().height / 2.5F + 1F;
			
			Rectangle2D rect = polygon.getBounds2D();
			
			for(int x = (int)rect.getX(); x < rect.getX() + rect.getWidth(); x++) {
				for(int y = (int)rect.getY(); y < rect.getY() + rect.getHeight(); y++) {
					if(polygon.contains(x, y)) {
						int r = (int)(biome.color[0] * 255);
						int g = (int)(biome.color[1] * 255);
						int b = (int)(biome.color[2] * 255);

						r *= tint;
						g *= tint;
						b *= tint;
						
						if(r < 0) r = 0; if(r > 255) r = 255;
						if(g < 0) g = 0; if(g > 255) g = 255;
						if(b < 0) b = 0; if(b > 255) b = 255;
						
						int rgb = r;
						rgb = (rgb << 8) + g;
						rgb = (rgb << 8) + b;
						
						image.setRGB(x, y, rgb);
					}
				}
			}
		}
		
		int[] pixels = new int[image.getWidth() * image.getHeight()];
        image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());

        ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * 3);
        
        for(int y = 0; y < image.getHeight(); y++){
            for(int x = 0; x < image.getWidth(); x++){
                int pixel = pixels[y * image.getWidth() + x];
                buffer.put((byte)((pixel >> 16) & 0xFF));
                buffer.put((byte)((pixel >> 8) & 0xFF));
                buffer.put((byte)(pixel & 0xFF));
            }
        }

        buffer.flip();
        
        pixelBuffer = buffer;
	}

	@Override
	public void render(World world, float[] translation, int[] viewSize, double mouseX, double mouseY) {
		if(pixelBuffer == null)
			pixelBuffer = ByteBuffer.allocateDirect(world.getWidth() * world.getHeight() * 3).order(ByteOrder.nativeOrder());

		GL11.glRasterPos2i(0, 0);
		GL11.glDrawPixels(world.getWidth(), world.getHeight(), GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, pixelBuffer);
		GL11.glPixelZoom(0.5F, -0.5F);
	}

	@Override
	public void onClick(World world, int button, int mods, double mouseXOnMap, double mouseYOnMap) { }

	@Override
	public void onDrag(World world, int button, double mouseXOnMap, double mouseYOnMap) { }
}