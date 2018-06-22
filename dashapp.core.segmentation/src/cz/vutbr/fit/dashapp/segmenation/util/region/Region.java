package cz.vutbr.fit.dashapp.segmenation.util.region;

import java.awt.Rectangle;

import cz.vutbr.fit.dashapp.model.Constants;

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
	public int joinType;

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

	public Region copy() {
		return new Region(x, y, width, height, color);
	}

	public int p(int dimension) {
		if(dimension == Constants.X) {
			return x;
		} else if(dimension == Constants.Y) {
			return y;
		}
		return -1;
	}
	
	public int size(int dimension) {
		if(dimension == Constants.X) {
			return this.width;
		} else if(dimension == Constants.Y) {
			return this.height;
		}
		return -1;
	}

	public int x2() {
		return x+width;
	}
	
	public int y2() {
		return y+height;
	}
	
	public int p2(int dimension) {
		if(dimension == Constants.X) {
			return x2();
		} else if(dimension == Constants.Y) {
			return y2();
		}
		return -1;
	}
	
	@Override
	public String toString() {
		return "[" + x +  "," + y + "](" + width + "," + height + ")";
	}

	public int distance(Region r2) {
		if(intersects(r2)) {
			return 0;
		}
		
		// r2 rectangle is...
		boolean left = r2.x2() <= x;
		boolean right = r2.x >= x2();
		boolean top = r2.y2() <= y;
		boolean bottom = r2.y >= y2();
		
		if(!top && !bottom) {
			if(left) {
				return x - r2.x2();
			} else if(right) {
				return r2.x-x2();
			}
		} else if(!left && !right) {
			if(top) {
				return y-r2.y2();
			} else if(bottom) {
				return r2.y - y2();
			}
		}
		
		return -1;
	}

	public boolean intersects(int x, int y) {
		return x >= this.x && x < this.x2() && y >= this.y && y < this.y2();
	}

	public Region joinWith(Region r) {
		Region joinRegion = new Region(0,0,0,0,JOIN);
		joinRegion.x = Math.min(this.x, r.x);
		joinRegion.y = Math.min(this.y, r.y);
		joinRegion.width = Math.max(this.x2(), r.x2())-joinRegion.x;
		joinRegion.height = Math.max(this.y2(), r.y2())-joinRegion.y;
		return joinRegion;
	}

}
