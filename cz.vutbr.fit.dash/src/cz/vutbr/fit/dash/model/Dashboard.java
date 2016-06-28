package cz.vutbr.fit.dash.model;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.imageio.ImageIO;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import cz.vutbr.fit.dash.Main;
import cz.vutbr.fit.dash.model.DashAppModel.PropertyKind;
import cz.vutbr.fit.dash.util.MatrixUtils;

@Root(name="dashboard")
public class Dashboard extends GraphicalElement {
	
	@ElementList(inline=true, required=false)
	private List<GraphicalElement> graphicalElements;
	
	private DashboardFile dashboardFile;
	
	private SerializedDashboard serializedDashboard;
	
	private DashAppModel model;

	private boolean reloadingFromFile;
	
	private boolean sizeInitialized;
	
	public Dashboard() {
		sizeInitialized = true;
		// used by deserialization
	}
	
	public Dashboard(DashAppModel model, DashboardFile dashboardFile) {
		setModel(model);
		setDashboardFile(dashboardFile);
		graphicalElements = new ArrayList<GraphicalElement>();
		serializedDashboard = new SerializedDashboard(this);
		// graphical element
		setDashboard(this);
		sizeInitialized = false;
	}
	
	public void initSize(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		sizeInitialized = true;
		DashAppModel.getInstance().firePropertyChange(new PropertyChangeEvent(PropertyKind.GRAPHICAL_ELEMENT, null, this));
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
		File file = dashboard.getDashboardFile().getImageFile();
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
	
	public List<GraphicalElement> getGraphicalElements(Type[] types) {
		if(types != null) {
			List<GraphicalElement> filteredGraphicalElements = new ArrayList<>();
			for (GraphicalElement ge : graphicalElements) {
				if(Type.contains(types, type)) {
					filteredGraphicalElements.add(ge);
				}
			}
			return filteredGraphicalElements;
		}
		return graphicalElements;
	}
	
	public boolean[][] getMattrix(Type[] types) {
		boolean[][] mattrix = new boolean[width][height];
		MatrixUtils.printDashboard(mattrix, dashboard, true, types);
		return mattrix;
	}
	
	public GraphicalElement createGrapicalElement(int x, int y, int width, int height, boolean convertToRelative) {
		if(convertToRelative) {
			x = x-this.x;
			y = y-this.y;
		}
		GraphicalElement graphicalElement = new GraphicalElement(this, x, y, width, height);
		graphicalElements.add(graphicalElement);
		getModel().firePropertyChange(new PropertyChangeEvent(PropertyKind.DASHBOARD_ELEMENTS, null, this));
		return graphicalElement;
	}
	
	public GraphicalElement deleteGrapicalElement(GraphicalElement graphicalElement) {
		if(graphicalElements.contains(graphicalElement)) {
			graphicalElements.remove(graphicalElement);
			getModel().firePropertyChange(new PropertyChangeEvent(PropertyKind.DASHBOARD_ELEMENTS, null, this));
		}
		return graphicalElement;
	}
	
	public SerializedDashboard serialize() throws Exception {
		Serializer serializer = new Persister();
		StringWriter writer = new StringWriter();
		serializer.write(this, writer);
		this.serializedDashboard.setXml(writer.toString(), true);
		return this.serializedDashboard;
	}
	
	public Dashboard deserialize(boolean update) throws Exception {
		Serializer serializer = new Persister();
		Dashboard dashboard = serializer.read(Dashboard.class, serializedDashboard.getXml());
		if(update) {
			updateDashboardModel(dashboard);
		}
		return dashboard;
	}

	private void updateDashboardModel(Dashboard dashboard) {
		if(dashboard != null) {
			initSize(dashboard.x, dashboard.y, dashboard.width, dashboard.height);
			graphicalElements = dashboard.graphicalElements;
			if(graphicalElements == null) {
				graphicalElements = new ArrayList<GraphicalElement>();
			}
			for (GraphicalElement graphicalElement : graphicalElements) {
				graphicalElement.setDashboard(this);
			}
			getModel().firePropertyChange(new PropertyChangeEvent(PropertyKind.DASHBOARD_ELEMENTS, null, this));
		}
	}
	
	public void saveToFile() throws IOException {
		File xmlFile = getDashboardFile().getXmlFile();
		if(!xmlFile.exists()) {
			xmlFile.createNewFile();
		}
		FileOutputStream out = new FileOutputStream(xmlFile);
		out.write(getSerializedDashboard().getXml().getBytes());
		out.close();
		getSerializedDashboard().setDirty(false);
	}
	
	public void reloadFromFile() throws Exception {
		File xmlFile = getDashboardFile().getXmlFile();
		if(xmlFile.exists() && xmlFile.canRead()) {
			BufferedReader br = new BufferedReader(new FileReader(xmlFile));
		    try {
		    	setReloadingFromFile(true);
		        StringBuilder sb = new StringBuilder();
		        String line = br.readLine();

		        while (line != null) {
		            sb.append(line);
		            sb.append("\n");
		            line = br.readLine();
		        }
		        getSerializedDashboard().setXml(sb.toString(), false);
		        deserialize(true);
		    } finally {
		        br.close();
		        setReloadingFromFile(false);
		    }
		}
	}

	public boolean isReloadingFromFile() {
		return reloadingFromFile;
	}

	public void setReloadingFromFile(boolean reloadingFromFile) {
		this.reloadingFromFile = reloadingFromFile;
	}
	
	@Override
	public void update(int x, int y, int width, int height, boolean convertToRelative) {
		// TODO Auto-generated method stub
		model.setListenersDisabled(true);
		// all child graphical elements with relative position need to be updated 
		if(this.x != x || this.y != y) {
			for (GraphicalElement graphicalElement : graphicalElements) {
				graphicalElement.update(graphicalElement.x+(this.x-x), graphicalElement.y+(this.y-y),
						graphicalElement.width, graphicalElement.height, false);
			}
		}
		model.setListenersDisabled(false);
		super.update(x, y, width, height, convertToRelative);
	}
	
	
	
	public int n(Type[] types) {
		return dashboard.getGraphicalElements(types).size();
	}
	
	public int getLayoutWidth(Type[] types) {
		// calculate width and height of layout
		int minX = this.width, maxX = 0;
		for (GraphicalElement graphicalElement : this.getGraphicalElements(types)) {
			if(minX > graphicalElement.x) {
				minX = graphicalElement.x;
			}
			if(maxX < graphicalElement.x+graphicalElement.width) {
				maxX = graphicalElement.x+graphicalElement.width;
			}
		}
		return maxX-minX;
	}
	
	public int getLayoutHeight(Type[] types) {
		int minY = this.height, maxY = 0;
		for (GraphicalElement graphicalElement : this.getGraphicalElements(types)) {
			if(minY > graphicalElement.y) {
				minY = graphicalElement.y;
			}
			if(maxY < graphicalElement.y+graphicalElement.height) {
				maxY = graphicalElement.y+graphicalElement.height;
			}
		}
		
		return maxY-minY;
	}
	
	public int getLayoutArea(Type[] types) {
		return getLayoutWidth(types)*getLayoutHeight(types);
	}
	
	public int getNumberOfSizes(Type[] types) {
		Set<Point> sizes = new HashSet<Point>();
		Point p;
		for (GraphicalElement graphicalElement : this.getGraphicalElements(types)) {
			p = new Point(graphicalElement.width, graphicalElement.height);
			if(!(sizes.contains(p))) {
				sizes.add(p);
			}
		}
		return sizes.size();
	}
	
	public int getElementsArea(Type[] types) {
		int areas = 0;
		for (GraphicalElement graphicalElement : this.getGraphicalElements(types)) {
			areas += graphicalElement.area();
		}
		return areas;
	}
	
	public int getHAP(Type[] types) {
		Set<Integer> listX = new HashSet<Integer>();
		for (GraphicalElement graphicalElement : this.getGraphicalElements(types)) {
			if(!listX.contains(graphicalElement.x)) {
				listX.add(graphicalElement.x);
			}
		}
		return listX.size();
	}
	
	public int getVAP(Type[] types) {
		Set<Integer> listY = new HashSet<Integer>();
		for (GraphicalElement graphicalElement : this.getGraphicalElements(types)) {
			if(!listY.contains(graphicalElement.y)) {
				listY.add(graphicalElement.y);
			}
		}
		return listY.size();
	}
}
