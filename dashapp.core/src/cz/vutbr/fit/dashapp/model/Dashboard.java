package cz.vutbr.fit.dashapp.model;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import cz.vutbr.fit.dashapp.util.matrix.BooleanMatrix;

/**
 * This class represents dashboard definition.
 * 
 * @author Jiri Hynek
 *
 */
@Root(name="dashboard")
public class Dashboard extends GraphicalElement {
	
	/**
	 * web-app compatibility
	 */
	@Element(required=false)
	public String title;
	
	/**
	 * dashboard physical representation (image or structural description)
	 */
	private IDashboardFile dashboardFile;
	
	/**
	 * flag which specifies whether dashboard has already been initialized
	 */
	private boolean sizeInitialized;
	
	/**
	 * This constructor is used by XML deserialization 
	 */
	public Dashboard() {
		sizeInitialized = true;
		// used by deserialization
	}
	
	/**
	 * 
	 * @param model
	 * @param dashboardFile
	 */
	public Dashboard(DashboardFile dashboardFile) {
		setDashboardFile(dashboardFile);
		// graphical element
		setParent(this);
		sizeInitialized = false;
	}
	
	/**
	 * Updates dashboard dimension
	 */
	public void setDimension(int x, int y, int width, int height) {
		super.setDimension(x, y, width, height);
		if(!sizeInitialized) {
			sizeInitialized = true;
		}
	}
	
	/**
	 * 
	 * @return true if dashboard dimension has been initialized
	 */
	public boolean isSizeInitialized() {
		return sizeInitialized;
	}

	/**
	 * Returns dashboard file which represents
	 * physical representation (image or structural description)
	 * 
	 * @return dashboard file
	 */
	public IDashboardFile getDashboardFile() {
		return dashboardFile;
	}
	
	/**
	 * Updates dashboard file which represents
	 * physical representation (image or structural description)
	 * 
	 * @param dashboardFile
	 */
	public void setDashboardFile(IDashboardFile dashboardFile) {
		this.dashboardFile = dashboardFile;
	}
	
	@Override
	public List<GraphicalElement> getChildren(GEType[] types) {
		// it returns empty array list instead of null array list 
		List<GraphicalElement> children = super.getChildren(types);
		if(children == null) {
			children = new ArrayList<GraphicalElement>();
		}
		return children;
	}

	@Override
	public boolean equals(Object obj) {
		if(this.dashboardFile == obj) {
			return true;
		}
		
		if(this.dashboardFile == null) {
			return false;
		}
		
		if(obj instanceof DashboardFile) {
			return dashboardFile.equals(obj);
		}
		
		return false;
	}
	
	public boolean[][] getBooleanMatrix(GEType[] types) {
		boolean[][] mattrix = new boolean[width][height];
		BooleanMatrix.printDashboard(mattrix, this, true, types);
		return mattrix;
	}
	
	/**
	 * 
	 * @param types
	 * @return number of children
	 */
	public int n(GEType[] types) {
		return getChildren(types).size();
	}
	
	/**
	 * 
	 * @param types
	 * @return size of width which is used by child elements
	 */
	public int getLayoutWidth(GEType[] types) {
		// calculate width and height of layout
		int minX = this.width, maxX = 0;
		for (GraphicalElement graphicalElement : this.getChildren(types)) {
			if(minX > graphicalElement.x) {
				minX = graphicalElement.x;
			}
			if(maxX < graphicalElement.x+graphicalElement.width) {
				maxX = graphicalElement.x+graphicalElement.width;
			}
		}
		return maxX-minX;
	}
	
	/**
	 * 
	 * @param types
	 * @return size of height which is used by child elements
	 */
	public int getLayoutHeight(GEType[] types) {
		int minY = this.height, maxY = 0;
		for (GraphicalElement graphicalElement : this.getChildren(types)) {
			if(minY > graphicalElement.y) {
				minY = graphicalElement.y;
			}
			if(maxY < graphicalElement.y+graphicalElement.height) {
				maxY = graphicalElement.y+graphicalElement.height;
			}
		}
		
		return maxY-minY;
	}
	
	/**
	 * 
	 * @param types
	 * @return rectangle area which is used by child elements
	 */
	public int getLayoutArea(GEType[] types) {
		return getLayoutWidth(types)*getLayoutHeight(types);
	}
	
	/**
	 * Returns rectangle of layout.
	 * 
	 * @param types
	 * @return
	 */
	public Rectangle getLayoutRectangle(GEType[] types) {
		int minX = this.width, maxX = 0;
		int minY = this.height, maxY = 0;
		List<GraphicalElement> ges = this.getChildren(types);
		for (GraphicalElement graphicalElement : ges) {
			if(minX > graphicalElement.x) {
				minX = graphicalElement.x;
			}
			if(maxX < graphicalElement.x+graphicalElement.width) {
				maxX = graphicalElement.x+graphicalElement.width;
			}
			if(minY > graphicalElement.y) {
				minY = graphicalElement.y;
			}
			if(maxY < graphicalElement.y+graphicalElement.height) {
				maxY = graphicalElement.y+graphicalElement.height;
			}
		}
		return new Rectangle(minX, minY, maxX-minX, maxY-minY);
	}
	
	/**
	 * 
	 * @param types
	 * @return area which is used by child elements
	 */
	public int getElementsArea(GEType[] types, boolean spaceArea) {
		if(spaceArea) {
			boolean matrix[][] = BooleanMatrix.printDashboard(this, true, GEType.ALL_TYPES);
			return BooleanMatrix.count(matrix);
		} else {
			int areas = 0;
			for (GraphicalElement graphicalElement : this.getChildren(types)) {
				areas += graphicalElement.area();
			}
			return areas;
		}
	}
	
	/**
	 * 
	 * @param types
	 * @return number of all child element dimensions
	 */
	public int getNumberOfSizes(GEType[] types) {
		Set<Point> sizes = new HashSet<Point>();
		Point p;
		for (GraphicalElement graphicalElement : this.getChildren(types)) {
			p = new Point(graphicalElement.width, graphicalElement.height);
			if(!(sizes.contains(p))) {
				sizes.add(p);
			}
		}
		return sizes.size();
	}
	
	/**
	 * Returns number of sizes for visible children.
	 * 
	 * @param types
	 * @return
	 */
	public int getNumberOfVisibleSizes(GEType[] types) {
		Set<Point> sizes = new HashSet<Point>();
		Point p;
		for (GraphicalElement graphicalElement : this.getVisibleChildren(types)) {
			p = new Point(graphicalElement.width, graphicalElement.height);
			if(!(sizes.contains(p))) {
				sizes.add(p);
			}
		}
		return sizes.size();
	}
	
	/**
	 * 
	 * @param types
	 * @return number of horizontal alignment points
	 */
	public int getHAP(GEType[] types) {
		Set<Integer> listX = new HashSet<Integer>();
		List<GraphicalElement> actChildren = this.getChildren(types);
		for (GraphicalElement graphicalElement : actChildren) {
			if(!listX.contains(graphicalElement.x)) {
				listX.add(graphicalElement.x);
			}
		}
		return listX.size();
	}
	
	/**
	 * 
	 * @param types
	 * @return number of vertical alignment points
	 */
	public int getVAP(GEType[] types) {
		Set<Integer> listY = new HashSet<Integer>();
		List<GraphicalElement> actChildren = this.getChildren(types);
		for (GraphicalElement graphicalElement : actChildren) {
			if(!listY.contains(graphicalElement.y)) {
				listY.add(graphicalElement.y);
			}
		}
		return listY.size();
	}
	
	/**
	 * Creates copy of dashboard
	 */
	public Dashboard copy() {
		Dashboard d = new Dashboard();
		d.setParent(d);
		d.setDimension(this.x, this.y, this.width, this.height);
		List<GraphicalElement> children = getChildren();
		if(children != null) {
			for (GraphicalElement childGE : children) {
				GraphicalElement childGECopy = childGE.copy();
				d.addChildGE(childGECopy);
			}
		}
		return d;
	}
	
	public Dashboard copy(Rectangle cr, int tolerance) {
		Dashboard d = new Dashboard();
		d.setParent(d);
		d.setDimension(cr.x, cr.y, cr.width, cr.height);
		
		// copy and update children
		List<GraphicalElement> children = getChildren();
		if(children != null) {
			for (GraphicalElement childGE : children) {
				GraphicalElement childGECopy = childGE.copy();
				// update GE relative position
				childGECopy.setDimension(d.toRelativeX(childGE.absoluteX()), d.toRelativeY(childGE.absoluteY()), childGE.width, childGE.height);
				// crop size
				int x1 = Math.max(0, Math.min(d.width, childGECopy.x()));
				int x2 = Math.max(0, Math.min(d.width, childGECopy.x2()));
				int y1 = Math.max(0, Math.min(d.height, childGECopy.y()));
				int y2 = Math.max(0, Math.min(d.height, childGECopy.y2()));
				childGECopy.setDimension(x1, y1, x2-x1, y2-y1);
				// evaluate size
				if(childGECopy.width > tolerance && childGECopy.height > tolerance && (double) childGECopy.area()/childGE.area() > 0.25) {
					// add to the new dashboard
					d.addChildGE(childGECopy);
				}
			}
		}
		
		return d;
	}
	
	public Dashboard filter(Rectangle cr, int tolerance) {
		Dashboard d = new Dashboard();
		d.setParent(d);
		d.setDimension(x, y, width, height);
		
		List<GraphicalElement> children = getChildren();
		for (GraphicalElement childGE : children) {
			// calculate used size
			int x1 = Math.max(cr.x, Math.min(cr.x+cr.width, childGE.x()));
			int x2 = Math.max(cr.x, Math.min(cr.x+cr.width, childGE.x2()));
			int y1 = Math.max(cr.y, Math.min(cr.y+cr.height, childGE.y()));
			int y2 = Math.max(cr.y, Math.min(cr.y+cr.height, childGE.y2()));
			int w = x2-x1;
			int h = y2-y1;
			// evaluate size
			if(w > tolerance && h > tolerance && (double) childGE.area(cr)/childGE.area() > 0.25) {
				// add to the new dashboard
				d.addChildGE(childGE.copy());
			}
		}
		
		return d;
	}
}
