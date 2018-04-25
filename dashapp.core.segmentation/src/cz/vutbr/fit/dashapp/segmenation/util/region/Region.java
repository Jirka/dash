package cz.vutbr.fit.dashapp.segmenation.util.region;

import java.awt.Rectangle;

public class Region extends Rectangle {

	/**
	 * UID
	 */
	private static final long serialVersionUID = 6087794130906669747L;
	
	public static final int OTHER = 0;
	public static final int R_FILL = 1;
	public static final int R_MEDIUM = 2;
	public static final int R_BORDER = 3;
	public static final int DATA = 4;
	public static final int AMBIGUOUS = 5;
	public static final int JOIN = 6;
	
	public int color;
	public int type = OTHER;
	public int intersects = 0;

	public int tmpIntersectArea;
	
	public Region(int x, int y, int w, int h, int color) {
		super(x, y, w, h);
		this.color = color;
	}
	
	public static class HierarchyComparator implements Comparable<Region> {
		
		Region region;
		
		public HierarchyComparator(Region region) {
			setRegion(region);
		}
		
		public void setRegion(Region region) {
			this.region = region;
		}
		
		@Override
		public int compareTo(Region r) {
			Rectangle intersection = region.intersection(r);
			
			if(intersection.width == region.width && intersection.height == region.height) {
				if(intersection.width == r.width && intersection.height == r.height) {
					// they are same
					return 0;
				}
				return -1;
			}
			
			if(intersection.width == r.width && intersection.height == r.height) {
				return 1;
			}
			
			// independent regions
			return 0;
			
			
		}
		
	}

	public int area() {
		return width*height;
	}

}
