package cz.vutbr.fit.dash.model;

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

import cz.vutbr.fit.dash.Main;
import cz.vutbr.fit.dash.util.MatrixUtils;

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
	
	public void setDimension(int x, int y, int width, int height) {
		super.setDimension(x, y, width, height);
		if(!sizeInitialized) {
			sizeInitialized = true;
		}
	}
	
	public boolean isSizeInitialized() {
		return sizeInitialized;
	}
	
	public DashAppModel getModel() {
		return model;
	}

	public void setModel(DashAppModel model) {
		this.model = model;
	}

	public DashboardFile getDashboardFile() {
		return dashboardFile;
	}
	
	public void setDashboardFile(DashboardFile dashboardFile) {
		this.dashboardFile = dashboardFile;
		this.dashboardFile.setDashboard(this);
	}
	
	public BufferedImage getImage() {
		BufferedImage image = null;
		File file = getDashboard().getDashboardFile().getImageFile();
		if(file != null && file.exists() && file.canRead()) {
			try {
		        image = ImageIO.read(file);
	        } catch (IOException e) {
	        	Main.logError("Unable to open file" + file.getAbsolutePath() + ".", e);
	        }
		}
		
		return image;
	}
	
	public SerializedDashboard getSerializedDashboard() {
		return serializedDashboard;
	}
	
	@Override
	public List<GraphicalElement> getChildren(GEType[] types) {
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
	
	public int n(GEType[] types) {
		return getChildren(types).size();
	}
	
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
	
	public int getLayoutArea(GEType[] types) {
		return getLayoutWidth(types)*getLayoutHeight(types);
	}
	
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
	
	public int getElementsArea(GEType[] types) {
		int areas = 0;
		for (GraphicalElement graphicalElement : this.getChildren(types)) {
			areas += graphicalElement.area();
		}
		return areas;
	}
	
	public int getHAP(GEType[] types) {
		Set<Integer> listX = new HashSet<Integer>();
		for (GraphicalElement graphicalElement : this.getChildren(types)) {
			if(!listX.contains(graphicalElement.x)) {
				listX.add(graphicalElement.x);
			}
		}
		return listX.size();
	}
	
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
