package cz.vutbr.fit.dashapp.model;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.convert.Convert;
import org.simpleframework.xml.convert.Converter;
import org.simpleframework.xml.stream.InputNode;
import org.simpleframework.xml.stream.OutputNode;

import cz.vutbr.fit.dashapp.model.Constants.Quadrant;
import cz.vutbr.fit.dashapp.model.Constants.Side;

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
	@Root
	@Convert(GETypeConverter.class)
	public static enum GEType {
		TOOLBAR, BUTTON, HEADER, CHART, LABEL, DECORATION,
		/**
		 * web-app compatibility
		 */
		bullet, sparklines, linegraph, text, table,
		graph_horizontal("graph-horizontal"), graph_vertical("graph-vertical");
		
		public static final GEType[] ALL_TYPES = null;
		
		private String name;
		
		private GEType() {
			this.name = this.toString();
		}
		
		private GEType(String name) {
			this.name = name;
		}
		
		public String getName() {
			return name;
		}

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
	
	public static class GETypeConverter implements Converter<GEType> {

		@Override
		public GEType read(InputNode node) throws Exception {
			final String value = node.getValue();

	        for(GEType geType : GEType.values()) {
	            if(geType.getName().equalsIgnoreCase(value) )
	                return geType;
	        }
	        throw new IllegalArgumentException("No enum available for " + value);
		}

		@Override
		public void write(OutputNode node, GEType value) throws Exception {
	        node.setValue(value.getName());
		}
		
	}
	
	/**
	 * relative x position of graphical element
	 */
	@Element(required=false)
	public int x;
	
	/**
	 * relative y position of graphical element
	 */
	@Element(required=false)
	public int y;
	
	/**
	 * width of graphical element
	 */
	@Element(required=false)
	public int width;
	
	/**
	 * height of hraphical element
	 */
	@Element(required=false)
	public int height;
	
	/**
	 * type of graphical element
	 */
	@Element(required=false)
	public GEType type = GEType.CHART;
	
	/**
	 * web-app compatibility
	 */
	@Element(required=false)
	public GEStyle style;
	
	/**
	 * dashboard
	 */
	GraphicalElement parent;
	
	/**
	 * Method used after deserialization of XML.
	 */
	public void refreshParents() {
		if(children != null) {
			for (GraphicalElement child : children) {
				child.setParent(this);
				child.refreshParents();
			}
		}
	}
	
	/**
	 * children
	 */
	@ElementList(inline=true, required=false)
	private List<GraphicalElement> children;
	
	public GraphicalElement() {
		// used by deserialization
	}
	
	public GraphicalElement(int x, int y, int width, int height) {
		this(x, y);
		this.width = width;
		this.height = height;
	}
	
	public GraphicalElement(int x, int y) {
		setParent(parent);
		this.x = x;
		this.y = y;
	}
	
	public GraphicalElement copy() {
		GraphicalElement copy = new GraphicalElement(x, y, width, height);
		copy.setParent(parent);
		copy.type = type;
		return copy;
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
		if(children != null) {
			for (GraphicalElement ge : children) {
				if(GEType.contains(types, ge.type)) {
					filteredGraphicalElements.add(ge);
				}
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
	 * Returns children which are not hidden behind another GE.
	 * 
	 * @param types
	 * @return visible child graphical elements
	 */
	public List<GraphicalElement> getVisibleChildren(GEType[] types) {
		ArrayList<GraphicalElement> result = new ArrayList<GraphicalElement>();
		// it returns empty array list instead of null array list 
		List<GraphicalElement> children = getChildren(types);
		if(children != null) {
			for (GraphicalElement child : children) {
				if(!child.isHidden(children)) {
					result.add(child);
				}
			}
		}
		return result;
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
		graphicalElement.setParent(this);
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
		return ((double)width)/2;
	}
	
	/**
	 * Returns Y center of graphical element.
	 * 
	 * @return relative center Y
	 */
	public double halfSizeY() {
		return ((double) height)/2;
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
		double cx = dashboard.centerX();
		double cy = dashboard.centerY();
		double x1 = absoluteX();
		double x2 = absoluteX()+width;
		double y1 = absoluteY();
		double y2 = absoluteY()+height;
		switch (q) {
			case I:
				x1 = Math.max(0, Math.min(x1, cx));
				x2 = Math.max(0, Math.min(x2, cx));
				y1 = Math.max(0, Math.min(y1, cy));
				y2 = Math.max(0, Math.min(y2, cy));
				break;
			case II:
				x1 = Math.max(cx, Math.min(x1, dashboard.width));
				x2 = Math.max(cx, Math.min(x2, dashboard.width));
				y1 = Math.max(0, Math.min(y1, cy));
				y2 = Math.max(0, Math.min(y2, cy));
				break;
			case III:
				x1 = Math.max(0, Math.min(x1, cx));
				x2 = Math.max(0, Math.min(x2, cx));
				y1 = Math.max(cy, Math.min(y1, dashboard.height));
				y2 = Math.max(cy, Math.min(y2, dashboard.height));
				break;
			case IV:
				x1 = Math.max(cx, Math.min(x1, dashboard.width));
				x2 = Math.max(cx, Math.min(x2, dashboard.width));
				y1 = Math.max(cy, Math.min(y1, dashboard.height));
				y2 = Math.max(cy, Math.min(y2, dashboard.height));
				break;
		}
		
		return (x2-x1)*(y2-y1);
	}
	
	/**
	 * Returns area which lays in specific dashboard side.
	 * 
	 * @param s
	 * @return area of dashboard side.
	 */
	public double area(Side s) {
		Dashboard dashboard = getDashboard();
		double cx = dashboard.centerX();
		double cy = dashboard.centerY();
		double x1 = absoluteX();
		double x2 = absoluteX()+width;
		double y1 = absoluteY();
		double y2 = absoluteY()+height;
		switch (s) {
			case LEFT:
				x1 = Math.max(0, Math.min(x1, cx));
				x2 = Math.max(0, Math.min(x2, cx));
				y1 = Math.max(0, Math.min(y1, dashboard.height));
				y2 = Math.max(0, Math.min(y2, dashboard.height));
				break;
			case RIGHT:
				x1 = Math.max(cx, Math.min(x1, dashboard.width));
				x2 = Math.max(cx, Math.min(x2, dashboard.width));
				y1 = Math.max(0, Math.min(y1, dashboard.height));
				y2 = Math.max(0, Math.min(y2, dashboard.height));
				break;
			case UP:
				x1 = Math.max(0, Math.min(x1, dashboard.width));
				x2 = Math.max(0, Math.min(x2, dashboard.width));
				y1 = Math.max(0, Math.min(y1, cy));
				y2 = Math.max(0, Math.min(y2, cy));
				break;
			case DOWN:
				x1 = Math.max(0, Math.min(x1, dashboard.width));
				x2 = Math.max(0, Math.min(x2, dashboard.width));
				y1 = Math.max(cy, Math.min(y1, dashboard.height));
				y2 = Math.max(cy, Math.min(y2, dashboard.height));
				break;
		}
		
		return (x2-x1)*(y2-y1);
	}
	
	/**
	 * Returns distance between center of dashboard and far border.
	 * 
	 * @param s
	 * @return distance between far border of GE and center of dashboard.
	 */
	public double depth(Side s) {
		GraphicalElement dashboard = getParent();
		double depth = 0.0;
		switch (s) {
			case LEFT:
				depth = dashboard.centerX()-absoluteX();
				break;
			case RIGHT:
				depth = absoluteX()+width-dashboard.centerX();
				break;
			case UP:
				depth = dashboard.centerY()-absoluteY();
				break;
			case DOWN:
				depth = absoluteY()+height-dashboard.centerY();
				break;
		}
		
		return depth;
	}
	
	/**
	 * TODO: make this class to extend Rectangle
	 * 
	 * @return rectangle
	 */
	public Rectangle getRectangle() {
		return new Rectangle(x, y, width, height);
	}
	
	/**
	 * Returns rectangle which lays in specific crop rectangle.
	 * 
	 * @param q
	 * @return rectangle.
	 */
	public Rectangle intersectionRectangle(Rectangle cropRectangle) {
		return getRectangle().intersection(cropRectangle);
	}
	
	/**
	 * Returns ge which lays in specific crop rectangle.
	 * 
	 * @param cropRectangle
	 * @return
	 */
	public GraphicalElement intersectionGE(Rectangle cropRectangle) {
		Rectangle intersectionRectangle = getRectangle().intersection(cropRectangle);
		GraphicalElement intersectionGE = copy();
		intersectionGE.setDimension(intersectionRectangle.x, intersectionRectangle.y, intersectionRectangle.width, intersectionRectangle.height);
		return intersectionGE;
	}
	
	/**
	 * Returns rectangle of GE which lays in specific dashboard side.
	 * 
	 * @param s
	 * @return
	 */
	public Rectangle getRectangle(Side s) {
		Dashboard dashboard = getDashboard();
		double cx = dashboard.centerX();
		double cy = dashboard.centerY();
		double x1 = absoluteX();
		double x2 = absoluteX()+width;
		double y1 = absoluteY();
		double y2 = absoluteY()+height;
		switch (s) {
			case LEFT:
				x1 = Math.max(0, Math.min(x1, cx));
				x2 = Math.max(0, Math.min(x2, cx));
				y1 = Math.max(0, Math.min(y1, dashboard.height));
				y2 = Math.max(0, Math.min(y2, dashboard.height));
				break;
			case RIGHT:
				x1 = Math.max(cx, Math.min(x1, dashboard.width));
				x2 = Math.max(cx, Math.min(x2, dashboard.width));
				y1 = Math.max(0, Math.min(y1, dashboard.height));
				y2 = Math.max(0, Math.min(y2, dashboard.height));
				break;
			case UP:
				x1 = Math.max(0, Math.min(x1, dashboard.width));
				x2 = Math.max(0, Math.min(x2, dashboard.width));
				y1 = Math.max(0, Math.min(y1, cy));
				y2 = Math.max(0, Math.min(y2, cy));
				break;
			case DOWN:
				x1 = Math.max(0, Math.min(x1, dashboard.width));
				x2 = Math.max(0, Math.min(x2, dashboard.width));
				y1 = Math.max(cy, Math.min(y1, dashboard.height));
				y2 = Math.max(cy, Math.min(y2, dashboard.height));
				break;
		}
		
		return new Rectangle((int) x1, (int) y1, (int) Math.round(x2-x1), (int) Math.round(y2-y1));
	}
	
	/**
	 * Returns rectangle of GE which lays in specific dashboard quadrant.
	 * 
	 * @param s
	 * @return
	 */
	public Rectangle getRectangle(Quadrant q) {
		Dashboard dashboard = getDashboard();
		double cx = dashboard.centerX();
		double cy = dashboard.centerY();
		double x1 = absoluteX();
		double x2 = absoluteX()+width;
		double y1 = absoluteY();
		double y2 = absoluteY()+height;
		switch (q) {
			case I:
				x1 = Math.max(0, Math.min(x1, cx));
				x2 = Math.max(0, Math.min(x2, cx));
				y1 = Math.max(0, Math.min(y1, cy));
				y2 = Math.max(0, Math.min(y2, cy));
				break;
			case II:
				x1 = Math.max(cx, Math.min(x1, dashboard.width));
				x2 = Math.max(cx, Math.min(x2, dashboard.width));
				y1 = Math.max(0, Math.min(y1, cy));
				y2 = Math.max(0, Math.min(y2, cy));
				break;
			case III:
				x1 = Math.max(0, Math.min(x1, cx));
				x2 = Math.max(0, Math.min(x2, cx));
				y1 = Math.max(cy, Math.min(y1, dashboard.height));
				y2 = Math.max(cy, Math.min(y2, dashboard.height));
				break;
			case IV:
				x1 = Math.max(cx, Math.min(x1, dashboard.width));
				x2 = Math.max(cx, Math.min(x2, dashboard.width));
				y1 = Math.max(cy, Math.min(y1, dashboard.height));
				y2 = Math.max(cy, Math.min(y2, dashboard.height));
				break;
		}
		
		return new Rectangle((int) x1, (int) y1, (int) Math.round(x2-x1), (int) Math.round(y2-y1));
	}
	
	/**
	 * Returns area which lays in specific rectangle.
	 * 
	 * @param q
	 * @return area of dashboard quadrant.
	 */
	public int area(Rectangle cropRectangle) {
		int absoluteX1 = absoluteX();
		int absoluteY1 = absoluteY();
		int absoluteX2 = absoluteX1 + width;
		int absoluteY2 = absoluteY1 + height;
		int x1 = Math.min(Math.max(cropRectangle.x, absoluteX()), absoluteX2);
		int x2 = Math.min(Math.max(cropRectangle.x+cropRectangle.width, absoluteX()), absoluteX2);
		int y1 = Math.min(Math.max(cropRectangle.y, absoluteY()), absoluteY2);
		int y2 = Math.min(Math.max(cropRectangle.y+cropRectangle.height, absoluteY()), absoluteY2);
		
		int area = (x2-x1)*(y2-y1);
		if(area < 0) {
			area = 0;
		}
		
		return area;
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
	
	/**
	 * Tests if this graphical element is all hidden behind some another GE.
	 * 
	 * @param ges
	 * @return
	 */
	public boolean isHidden(List<GraphicalElement> ges) {
		for (GraphicalElement ge : ges) {
			if(this != ge) {
				if(this.x() >= ge.x() && this.x2() <= ge.x2() &&
						this.y() >= ge.y() && this.y2() <= ge.y2()) {
					// actGe is hidden behind ge
					return true;
				}
			}
		}
		
		return false;
		
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
