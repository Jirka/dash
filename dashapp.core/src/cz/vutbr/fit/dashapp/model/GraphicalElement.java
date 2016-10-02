package cz.vutbr.fit.dashapp.model;

import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;

import cz.vutbr.fit.dashapp.model.Constants.Quadrant;

/**
 * 
 * @author Jiri Hynek
 *
 */
@Element(name="graphicalElement")
public class GraphicalElement {
	
	/**
	 * Type of graphical element.
	 * 
	 * @author Jiri Hynek
	 *
	 */
	public static enum GEType {
		TOOLBAR, BUTTON, HEADER, CHART, LABEL, DECORATION;
		
		public static final GEType[] ALL_TYPES = null;

		public static boolean contains(GEType[] types, GEType type) {
			for (GEType preferredType : types) {
				if(type == preferredType) {
					return true;
				}
			}
			return false;
		}

		public static GEType getValue(String itemName) {
			for (GEType geType : values()) {
				if(itemName.equals(geType.name())) {
					return geType;
				}
			}
			return null;
		}
	}
	
	/**
	 * relative x position of graphical element
	 */
	@Element
	public int x;
	
	/**
	 * relative y position of graphical element
	 */
	@Element
	public int y;
	
	/**
	 * width of graphical element
	 */
	@Element
	public int width;
	
	/**
	 * height of hraphical element
	 */
	@Element
	public int height;
	
	/**
	 * type of graphical element
	 */
	@Element(required=false)
	public GEType type = GEType.CHART;
	
	/**
	 * dashboard
	 */
	GraphicalElement parent;
	
	/**
	 * children
	 */
	@ElementList(inline=true, required=false)
	private List<GraphicalElement> children;
	
	public GraphicalElement() {
		// used by deserialization
	}
	
	public GraphicalElement(GraphicalElement parent, int x, int y, int width, int height) {
		this(parent, x, y);
		this.width = width;
		this.height = height;
	}
	
	public GraphicalElement(GraphicalElement parent, int x, int y) {
		setParent(parent);
		this.x = x;
		this.y = y;
	}
	
	public GraphicalElement copy() {
		return new GraphicalElement(parent, x, y, width, height);
	}
	
	public void setDimension(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	/**
	 * Returns root model object.
	 * 
	 * @return dashboard
	 */
	public Dashboard getDashboard() {
		GraphicalElement parent = getParent();
		while(parent != null && !(parent instanceof Dashboard)) {
			parent = parent.getParent();
		}
		return parent != null ? (Dashboard) parent : null;
	}

	/**
	 * Returns parent graphical element.
	 * 
	 * @return parent graphical element
	 */
	public GraphicalElement getParent() {
		return parent;
	}
	
	/**
	 * Sets parent graphical element.
	 * 
	 * @param parent
	 */
	public void setParent(GraphicalElement parent) {
		this.parent = parent;
	}
	
	/**
	 * Returns child graphical elements. Filter according to selected types.
	 * 
	 * @param types
	 * @return child graphical element
	 */
	public List<GraphicalElement> getChildren(GEType[] types) {
		if(types == null) {
			return getChildren();
		}
		
		List<GraphicalElement> filteredGraphicalElements = new ArrayList<>();
		for (GraphicalElement ge : children) {
			if(GEType.contains(types, ge.type)) {
				filteredGraphicalElements.add(ge);
			}
		}
		return filteredGraphicalElements;
	}
	
	/**
	 * Returns child graphical elements.
	 * 
	 * @return child graphical element
	 */
	public List<GraphicalElement> getChildren() {
		return children;
	}
	
	/**
	 * Replace children with the selected list.
	 * 
	 * @param children
	 */
	public void setChildren(List<GraphicalElement> children) {
		this.children = children;
		if(children != null) {
			for (GraphicalElement child : children) {
				child.setParent(this);
			}
		}
	}
	
	/**
	 * Adds selected child graphical element. 
	 * 
	 * @param graphicalElement
	 */
	public void addChildGE(GraphicalElement graphicalElement) {
		if(children == null) {
			children = new ArrayList<>();
		}
		children.add(graphicalElement);
	}
	
	/**
	 * Removes selected child graphical element.
	 * 
	 * @param graphicalElement
	 * @return deleted graphical element
	 */
	public GraphicalElement deleteChildGE(GraphicalElement graphicalElement) {
		if(children.contains(graphicalElement)) {
			children.remove(graphicalElement);
		}
		return graphicalElement;
	}
	
	/**
	 * Returns size of specified dimension.
	 * 
	 * @param dimension
	 * @return
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
	 * Returns center of graphical element.
	 * 
	 * @param dimension
	 * @return relative center
	 */
	public double halfSize(int dimension) {
		if(dimension == Constants.X) {
			return halfSizeX();
		} else if(dimension == Constants.Y) {
			return halfSizeY();
		}
		return -1;
	}
	
	/**
	 * Returns X center of graphical element.
	 * 
	 * @return relative center X
	 */
	public double halfSizeX() {
		return width/2.0;
	}
	
	/**
	 * Returns Y center of graphical element.
	 * 
	 * @return relative center Y
	 */
	public double halfSizeY() {
		return height/2.0;
	}
	
	/**
	 * Returns area of graphical element.
	 * 
	 * @return area
	 */
	public int area() {
		return width*height;
	}
	
	/**
	 * Returns area which lays in specific dashboard quadrant.
	 * 
	 * @param q
	 * @return area of dashboard quadrant.
	 */
	public double area(Quadrant q) {
		Dashboard dashboard = getDashboard();
		double dx = 0;
		double dy = 0;
		switch (q) {
		case I:
			dx = dashboard.halfSizeX()-x;
			dy = dashboard.halfSizeY()-y;
			break;
		case II:
			dx = x2()-dashboard.halfSizeX();
			dy = dashboard.halfSizeY()-y;
			break;
		case III:
			dx = dashboard.halfSizeX()-x;
			dy = y2()-dashboard.halfSizeY();
			break;
		case IV:
			dx = x2()-dashboard.halfSizeX();
			dy = y2()-dashboard.halfSizeY();
			break;
		}
		
		return Math.min(dx, width)
				* Math.min(dy, height);
	}
	
	/**
	 * Returns aspect ratio of graphical element.
	 * If normalization is set result will be always in range [0,1]. 
	 * 
	 * @param normalized
	 * @return aspect ratio
	 */
	public double aspectRatio(boolean normalized) {
		double pi = ((double) height)/width;
		if(normalized && pi > 1.0) {
			pi = 1/pi;
		}
		return pi;
	}
	
	
	
	
	// ----- POINTS RELATIVE TO ITS PARENT
	
	
	/**
	 * Returns position of specified dimension relative to its parent.
	 * 
	 * @param dimension
	 * @return relative position
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
	 * Returns X position relative to its parent.
	 * 
	 * @return relative X
	 */
	public int x() {
		return x;
	}
	
	/**
	 * Returns Y position relative to its parent.
	 * 
	 * @return relative Y
	 */
	public int y() {
		return y;
	}
	
	/**
	 * Returns second rectangle position of specified dimension relative to its parent.
	 * 
	 * @param dimension
	 * @return relative position 2
	 */
	public double p2(int dimension) {
		if(dimension == Constants.X) {
			return x2();
		} else if(dimension == Constants.Y) {
			return y2();
		}
		return -1;
	}
	
	/**
	 * Returns second rectangle X position relative to its parent.
	 * 
	 * @return relative X2
	 */
	public int x2() {
		return x+width;
	}
	
	/**
	 * Returns second rectangle Y position relative to its parent.
	 * 
	 * @return relative Y2
	 */
	public int y2() {
		return y+height;
	}
	
	/**
	 * Returns relative position of center of the graphical element.
	 * 
	 * @param dimension
	 * @return relative center to its parent
	 */
	public double center(int dimension) {
		if(dimension == Constants.X) {
			return centerX();
		} else if(dimension == Constants.Y) {
			return centerY();
		}
		return -1;
	}
	
	/**
	 * Returns relative position of X center of the graphical element.
	 * 
	 * @return relative X center to its parent
	 */
	public double centerX() {
		return ((double) this.x)+halfSizeX();
	}
	
	/**
	 * Returns relative position of Y center of the graphical element.
	 * 
	 * @return relative Y center to its parent
	 */
	public double centerY() {
		return ((double) this.y)+halfSizeY();
	}
	
	/**
	 * Returns distance between center and relative point.
	 * 
	 * @param p
	 * @param dimension
	 * @return distance from center
	 */
	public double d(double p, int dimension) {
		if(dimension == Constants.X) {
			return dx(p);
		} else if(dimension == Constants.Y) {
			return dy(p);
		}
		return -1;
	}
	
	/**
	 * Returns distance between center X and relative point.
	 * 
	 * @param x
	 * @return distance from center X
	 */
	public double dx(double x) {
		return centerX()-x;
	}
	
	/**
	 * Returns distance between center Y and relative point.
	 * 
	 * @param y
	 * @return distance from center Y
	 */
	public double dy(double y) {
		return centerY()-y;
	}
	
	/**
	 * Converts absolute position to position relative due to the graphical element.
	 * 
	 * @param p
	 * @param dimension
	 * @return relative p
	 */
	public int toRelative(int p, int dimension) {
		if(dimension == Constants.X) {
			return toRelativeX(p);
		} else if(dimension == Constants.Y) {
			return toRelativeY(p);
		}
		return -1;
	}
	
	/**
	 * Converts absolute X position to X position relative due to the graphical element.
	 * 
	 * @param x
	 * @return relative x
	 */
	public int toRelativeX(int x) {
		return x-this.absoluteX();
	}
	
	/**
	 * Converts absolute Y position to Y position relative due to the graphical element.
	 * 
	 * @param y
	 * @return relative y
	 */
	public int toRelativeY(int y) {
		return y-this.absoluteY();
	}
	
	
	
	
	// ----- ABSOLUTE POINTS
	
	
	/**
	 * Returns absolute position of specified dimension.
	 * 
	 * @param dimension
	 * @return absolute position
	 */
	public int absolute(int dimension) {
		if(dimension == Constants.X) {
			return absoluteX();
		} else if(dimension == Constants.Y) {
			return absoluteY();
		}
		return -1;
	}
	
	/**
	 * Returns absolute X position.
	 * 
	 * @return absolute X
	 */
	public int absoluteX() {
		return parent == this ? x : x+parent.absoluteX();
	}
	
	/**
	 * Returns absolute Y position.
	 * 
	 * @return absolute Y
	 */
	public int absoluteY() {
		return parent == this ? y : y+parent.absoluteY();
	}
	
	/**
	 * Converts position relative due to the graphical element to absolute position.
	 * 
	 * @param p
	 * @param dimension
	 * @return absolute p
	 */
	public int toAbsolute(int p, int dimension) {
		if(dimension == Constants.X) {
			return toAbsoluteX(p);
		} else if(dimension == Constants.Y) {
			return toAbsoluteY(p);
		}
		return -1;
	}
	
	/**
	 * Converts X position relative due to the graphical element to absolute X position.
	 * 
	 * @param x
	 * @return absolute x
	 */
	public int toAbsoluteX(int x) {
		return this.absoluteX()+x;
	}
	
	/**
	 * Converts Y position relative due to the graphical element to absolute Y position.
	 * 
	 * @param y
	 * @return absolute y
	 */
	public int toAbsoluteY(int y) {
		return this.absoluteY()+y;
	}
}
