package com.codingforcookies.worldbuilder.brush;

import com.codingforcookies.worldbuilder.generator.SiteData;
import com.codingforcookies.worldbuilder.generator.SiteType;

import kn.uni.voronoitreemap.j2d.Site;

public class BrushMoisture implements IBrush {
	@Override public String getName() { return "Moisture"; }
	@Override public String[] getOptions() { return new String[] { "alter", "normalize" }; }
	@Override public BrushType getType(String option) { return BrushType.CIRCULAR; }

	@Override
	public void onLeft(String option, SiteData selection, double power) {
		if(selection.getType() == SiteType.WATER)
			return;
		
		switch(option) {
			case "alter":
				selection.moisture += power * 10;
				
				if(selection.moisture >= 70)
					selection.moisture = 69;
				break;
			case "normalize":
				float average = selection.moisture;
	
				boolean b = false;
				for(Site neighbor : selection.site.getNeighbours()) {
					if(neighbor.getData().getType() != SiteType.WATER)
						b = true;
					else
						b = false;
						
					if(b)
						average = (average + neighbor.getData().moisture) / 2;
				}
				
				float absdiff = Math.abs(selection.moisture) - Math.abs(average);
				
				if(average > selection.moisture) {
					if(selection.moisture + (average * power) > 0)
						selection.moisture += (average * power);
				}else if(average < selection.height) {
					if(selection.moisture - (average * power) * absdiff > 0)
						selection.moisture -= (average * power) * absdiff;
				}
				break;
		}
	}

	@Override
	public void onRight(String option, SiteData selection, double power) {
		if(selection.getType() == SiteType.WATER)
			return;
		
		power *= 10;
		if(option == "alter") {
			selection.moisture -= power;
			
			if(selection.moisture < 0)
				selection.moisture = 0;
		}
	}
}