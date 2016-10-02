package cz.vutbr.fit.dashapp.model;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.imageio.ImageIO;

import org.simpleframework.xml.Root;

import cz.vutbr.fit.dashapp.Logger;
import cz.vutbr.fit.dashapp.util.MatrixUtils;

/**
 * This class represents dashboard definition.
 * 
 * @author Jiri Hynek
 *
 */
@Root(name="dashboard")
public class Dashboard extends GraphicalElement {
	
	/**
	 * dashboard physical representation (image or structural description)
	 */
	private DashboardFile dashboardFile;
	
	/**
	 * actual serialized representation (unsaved structural description - working copy)
	 */
	private SerializedDashboard serializedDashboard;
	
	/**
	 * model definition
	 */
	private DashAppModel model;
	
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
	public Dashboard(DashAppModel model, DashboardFile dashboardFile) {
		setModel(model);
		setDashboardFile(dashboardFile);
		serializedDashboard = new SerializedDashboard(this);
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
	 * 
	 * @return model
	 */
	public DashAppModel getModel() {
		return model;
	}

	/**
	 * 
	 * @param model
	 */
	public void setModel(DashAppModel model) {
		this.model = model;
	}

	/**
	 * Returns dashboard file which represents
	 * physical representation (image or structural description)
	 * 
	 * @return dashboard file
	 */
	public DashboardFile getDashboardFile() {
		return dashboardFile;
	}
	
	/**
	 * Updates dashboard file which represents
	 * physical representation (image or structural description)
	 * 
	 * @param dashboardFile
	 */
	public void setDashboardFile(DashboardFile dashboardFile) {
		this.dashboardFile = dashboardFile;
		this.dashboardFile.setDashboard(this);
	}
	
	/**
	 * Returns actual serialized representation
	 * (unsaved structural description - working copy)
	 * 
	 * @return serialized dashboard
	 */
	public SerializedDashboard getSerializedDashboard() {
		return serializedDashboard;
	}
	
	/**
	 * Returns raster representation of dashboard if exists
	 * 
	 * @return image
	 */
	public BufferedImage getImage() {
		BufferedImage image = null;
		File file = getDashboard().getDashboardFile().getImageFile();
		if(file != null && file.exists() && file.canRead()) {
			try {
		        image = ImageIO.read(file);
	        } catch (IOException e) {
	        	Logger.logError("Unable to open file" + file.getAbsolutePath() + ".", e);
	        }
		}
		
		return image;
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
	
	public boolean[][] getMattrix(GEType[] types) {
		boolean[][] mattrix = new boolean[width][height];
		MatrixUtils.printDashboard(mattrix, this, true, types);
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
	 * 
	 * @param types
	 * @return area which is used by child elements
	 */
	public int getElementsArea(GEType[] types) {
		int areas = 0;
		for (GraphicalElement graphicalElement : this.getChildren(types)) {
			areas += graphicalElement.area();
		}
		return areas;
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
	 * 
	 * @param types
	 * @return number of horizontal alignment points
	 */
	public int getHAP(GEType[] types) {
		Set<Integer> listX = new HashSet<Integer>();
		for (GraphicalElement graphicalElement : this.getChildren(types)) {
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
		for (GraphicalElement graphicalElement : this.getChildren(types)) {
			if(!listY.contains(graphicalElement.y)) {
				listY.add(graphicalElement.y);
			}
		}
		return listY.size();
	}
}
