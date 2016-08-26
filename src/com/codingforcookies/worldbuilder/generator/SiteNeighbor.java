package com.codingforcookies.worldbuilder.generator;

import kn.uni.voronoitreemap.j2d.Site;

public class SiteNeighbor {
	public Site site;
	public boolean exact;
	
	public SiteNeighbor(Site site, boolean exact) {
		this.site = site;
		this.exact = exact;
	}
}