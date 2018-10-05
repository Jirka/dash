package cz.vutbr.fit.dashapp.segmenation.util.region;

import java.awt.Rectangle;

import cz.vutbr.fit.dashapp.model.Constants;

/**
 * Class which represents region. It contains methods for basic operations with regions.
 * 
 * @author Jiri Hynek
 *
 */
public class Region extends Rectangle {

	/**
	 * UID
	 */
	private static final long serialVersionUID = 6087794130906669747L;
	
	/**
	 * color of the area that the region represents.
	 */
	public int color;
	
	/**
	 * type of region
	 */
	public int type = TYPE_OTHER;
	
	/**
	 * Unknown type of region
	 */
	public static final int TYPE_OTHER = 0;
	
	/**
	 * filled rectangle
	 */
	public static final int TYPE_RECT_FILL = 1;
	
	/**
	 * half-filled rectangle
	 * (rectangle might contain other graphical elements)
	 */
	public static final int TYPE_RECT_MEDIUM = 2;
	
	/**
	 * rectangle represented by border line
	 */
	public static final int TYPE_RECT_BORDER = 3;
	
	/**
	 * small data region
	 */
	public static final int TYPE_DATA = 4;
	
	/**
	 * non-rectangular larger region
	 */
	public static final int TYPE_AMBIGUOUS = 5;
	
	/**
	 * rectangle which was created by joining of other regions.
	 */
	public static final int TYPE_JOIN = 6;
	
	/**
	 * Category of region in layout.
	 */
	public int category;
	
	/**
	 * Main region.
	 */
	public static final int CATEGORY_MAIN = 1;
	
	/**
	 * Candidate region.
	 */
	public static final int CATEGORY_CANDIDATE = 2;
	
	/**
	 * Help variable which stores the actual number of intersections.
	 * 
	 * It is used for heuristics.
	 */
	public int tmpNumberOfIntersections = 0;

	/**
	 * Help variable which stores the actual area of intersection.
	 * 
	 * It is used for heuristics
	 */
	public int tmpIntersectArea;
	
	public Region(int x, int y, int w, int h, int color) {
		this(x, y, w, h, color, CATEGORY_CANDIDATE);
	}
	
	public Region(int x, int y, int w, int h, int color, int category) {
		super(x, y, w, h);
		this.color = color;
		this.category = category;
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

	/**
	 * 
	 * @return area of region
	 */
	public int area() {
		return width*height;
	}
	
	/**
	 * 
	 * @return perimeter of region
	 */
	public int perimeter() {
		return 2*(width+height);
	}

	/**
	 * 
	 * @return copy of region
	 */
	public Region copy() {
		return new Region(x, y, width, height, color);
	}

	/**
	 * 
	 * @param dimension
	 * @return point in X/Y demsion
	 */
	public int p(int dimension) {
		if(dimension == Constants.X) {
			return x;
		} else if(dimension == Constants.Y) {
			return y;
		}
		return -1;
	}
	
	/**
	 * 
	 * @param dimension
	 * @return width or height
	 */
	public int size(int dimension) {
		if(dimension == Constants.X) {
			return this.width;
		} else if(dimension == Constants.Y) {
			return this.height;
		}
		return -1;
	}

	/**
	 * 
	 * @return x2
	 */
	public int x2() {
		return x+width;
	}
	
	/**
	 * 
	 * @return y2
	 */
	public int y2() {
		return y+height;
	}
	
	/**
	 * 
	 * @param dimension
	 * @return x2 or y2
	 */
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

	/**
	 * 
	 * @param r2
	 * @return distance from region r2
	 */
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

	/**
	 * 
	 * @param x
	 * @param y
	 * @return true if intersects point [x,y]
	 */
	public boolean intersects(int x, int y) {
		return x >= this.x && x < this.x2() && y >= this.y && y < this.y2();
	}

	/**
	 * 
	 * @param r
	 * @return region as the result of join operation of the this region with the region r
	 */
	public Region joinWith(Region r) {
		Region joinRegion = new Region(0,0,0,0,TYPE_JOIN);
		joinRegion.x = Math.min(this.x, r.x);
		joinRegion.y = Math.min(this.y, r.y);
		joinRegion.width = Math.max(this.x2(), r.x2())-joinRegion.x;
		joinRegion.height = Math.max(this.y2(), r.y2())-joinRegion.y;
		return joinRegion;
	}
	
	/**
	 * Method measures share of regions area due to area of join region
	 * 
	 * @param r
	 * @param r2
	 * @return
	 */
	public double shareOfJoinArea(Region r2) {
		int joinArea = (Math.max(x2(), r2.x2())-Math.min(x, r2.x))
		* (Math.max(y2(), r2.y2())-Math.min(y, r2.y));
		
		return (area()+r2.area())/(double) joinArea;
	}

}
