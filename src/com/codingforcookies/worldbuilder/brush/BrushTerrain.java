package com.codingforcookies.worldbuilder.brush;

import com.codingforcookies.worldbuilder.generator.SiteData;
import com.codingforcookies.worldbuilder.generator.SiteType;

import kn.uni.voronoitreemap.j2d.Site;

public class BrushTerrain implements IBrush {
	@Override public String getName() { return "Terrain"; }
	@Override public String[] getOptions() { return new String[] { "alter", "normalize", "flatten" }; }
	@Override public BrushType getType(String option) { return BrushType.CIRCULAR; }

	@Override
	public void onLeft(String option, SiteData selection, double power) {
		switch(option) {
			case "alter":
				selection.height += power;
				
				if(selection.height > 0F)
					if(selection.moisture >= 70)
						selection.moisture = 69;
				break;
			case "normalize":
				// Makes sure water stays water, and land stays land, but attempts to average the landscape.
				boolean water = selection.getType() == SiteType.WATER;
				
				float average = selection.height;

				boolean b = false;
				for(Site neighbor : selection.site.getNeighbours()) {
					if(water && neighbor.getData().getType() == SiteType.WATER)
						b = true;
					else if(!water && neighbor.getData().getType() != SiteType.WATER)
						b = true;
					else
						b = false;
						
					if(b)
						average = (average + neighbor.getData().height) / 2;
				}
				
				float absdiff = Math.abs(selection.height) - Math.abs(average);
				
				if(average > selection.height) {
					b = selection.height + (average * power) < 0;
					if((water && b) || (!water && !b))
						selection.height += (average * power) / 10;
				}else if(average < selection.height) {
					b = selection.height - (average * power) * absdiff > 0;
					if((water && !b) || (!water && b))
						selection.height -= (average * power) * absdiff;
				}
				break;
			case "flatten":
				selection.height /= 1F + (10 * power);
				break;
		}
	}

	@Override
	public void onRight(String option, SiteData selection, double power) {
		if(option == "alter") {
			selection.height -= power;
			
			if(selection.height <= 0F)
				if(selection.moisture != 70F)
					selection.moisture = 70F;
		}
	}
}