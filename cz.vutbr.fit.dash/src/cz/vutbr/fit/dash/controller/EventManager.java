package cz.vutbr.fit.dash.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import cz.vutbr.fit.dash.controller.PropertyChangeEvent.Change;
import cz.vutbr.fit.dash.model.DashAppModel;
import cz.vutbr.fit.dash.model.Dashboard;
import cz.vutbr.fit.dash.model.DashboardFile;
import cz.vutbr.fit.dash.model.GraphicalElement;
import cz.vutbr.fit.dash.model.SerializedDashboard;
import cz.vutbr.fit.dash.model.GraphicalElement.GEType;

/**
 * Manager which provides operations managing dashboard model.
 * It is advised to used these operations because model integrity is watched. 
 * 
 * @author Jiri Hynek
 *
 */
public class EventManager {
	
	/**
	 * Controller which manages model property change listeners.
	 */
	DashAppController controller;
	
	/**
	 * Initializes event manager.
	 * 
	 * @param controller
	 */
	public EventManager(DashAppController controller) {
		this.controller = controller;
	}
	
	/**
	 * List of change event kinds.
	 * 
	 * @author Jiri Hynek
	 *
	 */
	public static enum EventKind {
		FOLDER_PATH_CHANGED,
		DASHBOARD_SELECTION_CHANGED,
		XML_CHANGED,
		GRAPHICAL_ELEMENT_CHANGED,
		GRAPHICAL_ELEMENT_CREATED,
		GRAPHICAL_ELEMENT_DELETED,
		IS_DIRTY;

		/**
		 * 
		 * @param e
		 * @return true if event kind is equal to model change event kind
		 */
		public static boolean isModelChanged(PropertyChangeEvent e) {
			switch (e.propertyKind) {
				case XML_CHANGED:
				case GRAPHICAL_ELEMENT_CHANGED:
				case GRAPHICAL_ELEMENT_CREATED:
				case GRAPHICAL_ELEMENT_DELETED:
					return true;
				default:
					return false;
			}
		}
	}
	
	/**
	 * Updates dashboards folder path.
	 * 
	 * @param folderPath
	 */
	public void _updateFolderPath(String folderPath) {
		assert folderPath == null;
		
		DashAppModel model = DashAppModel.getInstance();
		String oldPath = model.getFolderPath();
		if(!oldPath.equals(folderPath)) {
			model.setFolderPath(folderPath);
			controller.firePropertyChange(new PropertyChangeEvent(EventKind.FOLDER_PATH_CHANGED, new Change(oldPath, folderPath), null));
			model.getDashboards().clear(); // folder is changed, all cached dashboards can be released
		}
	}
	
	public void reloadDashboardFromFile(Dashboard dashboard) throws Exception {
		File xmlFile = dashboard.getDashboardFile().getXmlFile();
		String xml = readXMLFile(xmlFile);
		_updateDashboardXml(dashboard, xml, false);
	}
	
	public void _updateDashboardXml(Dashboard dashboard, String xml, boolean dirty) {
		assert xml == null;
		// compare the new and old versions
		String oldXML = dashboard.getSerializedDashboard().getXml();
		if(!oldXML.equals(xml)) {
			// try to deserialize XML (test if valid)
			Dashboard deserializedXML = _deserialize(xml);
			if(deserializedXML != null) {
				// update XML
				dashboard.getSerializedDashboard().setXml(xml);
				// update model (deserialize XML)
				copyDashboardDefinition(dashboard, deserializedXML);
				// fire change
				controller.firePropertyChange(new PropertyChangeEvent(EventKind.XML_CHANGED, null, new Change(oldXML, xml)));
			}			
		}
	}
	
	/**
	 * TODO remove
	 * 
	 * @param to
	 * @param from
	 */
	public void copyDashboardDefinition(Dashboard to, Dashboard from) {
		if(from != null) {
			to.setDimension(from.x, from.y, from.width, from.height);
			to.setChildren(from.getChildren());
		}
	}
	
	public void _updateGraphicalElement(GraphicalElement ge, int x, int y, int width, int height, boolean convertToRelative) {
		// update dimensions of graphical element children
		_updateGEChildren(ge, x, y, width, height, convertToRelative);
		// update graphical element dimensions
		GraphicalElement copy = _updateGEDimension(ge, x, y, width, height, convertToRelative);
		// if change has been made, XML needs to be updated and change event needs to be fired 
		if(copy != null) {
			// update dashboard serialized definition (serialization problem should not occur)
			Dashboard dashboard = ge.getDashboard();
			String oldXML = dashboard.getSerializedDashboard().getXml();
			String newXML = _serialize(dashboard);
			dashboard.getSerializedDashboard().setXml(newXML);
			// fire property change
			controller.firePropertyChange(new PropertyChangeEvent(EventKind.GRAPHICAL_ELEMENT_CHANGED, new Change(copy, ge), new Change(oldXML, newXML)));
		}
	}
	
	public void _updateGraphicalElement(GraphicalElement ge, GEType type) {
		if(ge.type != type) {
			// update type if changed
			GraphicalElement copy = ge.copy();
			ge.type = type;
			// update dashboard serialized definition (serialization problem should not occur)
			Dashboard dashboard = ge.getDashboard();
			String oldXML = dashboard.getSerializedDashboard().getXml();
			String newXML = _serialize(dashboard);
			dashboard.getSerializedDashboard().setXml(newXML);
			// fire property change
			controller.firePropertyChange(new PropertyChangeEvent(EventKind.GRAPHICAL_ELEMENT_CHANGED, new Change(copy, ge), new Change(oldXML, newXML)));
		}
	}
	
	private GraphicalElement _updateGEDimension(GraphicalElement ge, int x, int y, int width, int height, boolean convertToRelative) {
		GraphicalElement copy = null;
		if(convertToRelative) {
			x = ge.getParent().toRelativeX(x);
			y = ge.getParent().toRelativeY(y);
		}
		if(ge.x != x) {
			copy = ge.copy();
			ge.x = x;
		}
		if(ge.y != y) {
			if(copy == null) copy = ge.copy();
			ge.y = y;
		}
		if(ge.width != width) {
			if(copy == null) copy = ge.copy();
			ge.width = width;
		}
		if(ge.height != height) {
			if(copy == null) copy = ge.copy();
			ge.height = height;
		}
		return copy;
	}
	
	private void _updateGEChildren(GraphicalElement ge, int x, int y, int width, int height, boolean convertToRelative) {
		// all child graphical elements with relative position need to be updated 
		if(ge.x != x || ge.y != y) {
			List<GraphicalElement> children = ge.getChildren();
			if(children != null) {
				for (GraphicalElement childGE : children) {
					childGE.setDimension(childGE.x+(ge.x-x), childGE.y+(ge.y-y), childGE.width, childGE.height);
				}
			}
		}
	}
	
	public void _deleteGraphicalElement(GraphicalElement selectedElement) {
		// it is not possible to delete root dashboard graphical element
		GraphicalElement parent = selectedElement.getParent();
		if(parent != null) {
			// delete from parent
			parent.deleteChildGE(selectedElement);
			// update dashboard serialized definition (serialization problem should not occur)
			Dashboard dashboard = parent.getDashboard();
			String oldXML = dashboard.getSerializedDashboard().getXml();
			String newXML = _serialize(dashboard);
			dashboard.getSerializedDashboard().setXml(newXML);
			// fire property change
			controller.firePropertyChange(new PropertyChangeEvent(EventKind.GRAPHICAL_ELEMENT_DELETED, 
					new Change(selectedElement, parent), new Change(oldXML, newXML)));
		}		
	}
	
	public GraphicalElement _createGrapicalElement(GraphicalElement parent, int x, int y, int width, int height, boolean convertToRelative) {
		if(convertToRelative) {
			x = parent.toRelativeX(x);
			y = parent.toRelativeX(y);
		}
		// create graphical element
		GraphicalElement graphicalElement = new GraphicalElement(parent, x, y, width, height);
		// add to parent
		parent.addChildGE(graphicalElement);
		// update dashboard serialized definition (serialization problem should not occur)
		Dashboard dashboard = parent.getDashboard();
		String oldXML = dashboard.getSerializedDashboard().getXml();
		String newXML = _serialize(dashboard);
		dashboard.getSerializedDashboard().setXml(newXML);
		// fire property changed
		controller.firePropertyChange(new PropertyChangeEvent(EventKind.GRAPHICAL_ELEMENT_CHANGED, 
				new Change(null, graphicalElement), new Change(oldXML, newXML)));
		
		return graphicalElement;
	}

	public void updateSerializedDashboardState(SerializedDashboard sd, boolean isDirty) {
		if(sd.isDirty() != isDirty) {
			sd.setDirty(isDirty);
			controller.firePropertyChange(new PropertyChangeEvent(EventKind.IS_DIRTY, new Change(!isDirty, sd), null));
		}
	}
	
	/**
	 * 
	 * @param selectedDashboard
	 */
	private void updateSelectedDashboard(Dashboard selectedDashboard) {
		DashAppModel model = DashAppModel.getInstance();
		Dashboard oldDashboard = model.getSelectedDashboard();
		if(oldDashboard != selectedDashboard) {
			model.setSelectedDashboard(selectedDashboard);
			controller.firePropertyChange(new PropertyChangeEvent(EventKind.DASHBOARD_SELECTION_CHANGED, new Change(oldDashboard, selectedDashboard), null));
		}
	}
	
	/**
	 * TODO what is it good for?
	 * 
	 * @param dashboardFile
	 */
	public void updateSelectedDashboard(DashboardFile dashboardFile) {
		DashAppModel model = DashAppModel.getInstance();
		Dashboard dashboard = model.getDashboard(dashboardFile);
		if(dashboard == null && dashboardFile != null) {
			dashboard = new Dashboard(model, dashboardFile);
			try {
				controller.setListenersDisabled(true);
				reloadDashboardFromFile(dashboard);
			} catch (Exception e) {
				// no information 
			} finally {
				controller.setListenersDisabled(false);
			}
			model.addDashboard(dashboard);
		}
		
		updateSelectedDashboard(dashboard);
	}

	public boolean createEmptyDashboard(int width, int height, String name) {
		DashAppModel model = DashAppModel.getInstance();
		String folderPath = model.getFolderPath();
		File xmlFile = new File(folderPath + File.separator + name + ".xml");
		if(!xmlFile.exists()) {
			Dashboard dashboard = new Dashboard(model, new DashboardFile(xmlFile));
			dashboard.setDimension(0, 0, width, height);
			try {
				// serialize
				dashboard.getSerializedDashboard().setXml(_serialize(dashboard));
				// save
				saveDashboardToFile(dashboard);
				// add to model
				model.addDashboard(dashboard);
				// update selection
				updateSelectedDashboard(dashboard);
			} catch (IOException e) {
				return false;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
			
			return true;
		}
		return false;
	}
	
	public void initDashboard(Dashboard dashboard, int x, int y, int width, int height) {
		dashboard.setDimension(x, y, width, height);
		controller.firePropertyChange(new PropertyChangeEvent(EventKind.GRAPHICAL_ELEMENT_CHANGED, new Change(null, dashboard), null));
	}
	
	public void saveDashboardToFile(Dashboard dashboard) throws IOException {
		File xmlFile = dashboard.getDashboardFile().getXmlFile();
		if(!xmlFile.exists()) {
			xmlFile.createNewFile();
		}
		FileOutputStream out = new FileOutputStream(xmlFile);
		SerializedDashboard sd = dashboard.getSerializedDashboard();
		out.write(dashboard.getSerializedDashboard().getXml().getBytes());
		out.close();
		sd.setDirty(false);
	}
	
	
	/**
	 * 
	 * @param dashboard
	 * @return
	 */
	public String _serialize(Dashboard dashboard) {
		Serializer serializer = new Persister();
		StringWriter writer = new StringWriter();
		try {
			serializer.write(dashboard, writer);
		} catch (Exception e) {
			System.err.println("Unable to serialize dashboard");
		}
		return writer.toString();
	}
	
	/**
	 * 
	 * @param xml
	 * @return
	 */
	public Dashboard _deserialize(String xml) {
		Serializer serializer = new Persister();
		Dashboard newDashboard = null;
		try {
			newDashboard = serializer.read(Dashboard.class, xml);
		} catch (Exception e) {
			System.err.println("Unable to deserialize dashboard");
		}
		return newDashboard;
	}
	
	/**
	 * 
	 * @param xmlFile
	 * @return
	 * @throws IOException
	 */
	public String readXMLFile(File xmlFile) throws IOException {
		String result = null;
		if(xmlFile.exists() && xmlFile.canRead()) {
			BufferedReader br = new BufferedReader(new FileReader(xmlFile));
		    try {
		        StringBuilder sb = new StringBuilder();
		        String line = br.readLine();

		        while (line != null) {
		            sb.append(line);
		            sb.append("\n");
		            line = br.readLine();
		        }
		        result = sb.toString();
		    } finally {
		        br.close();
		    }
		}
		return result;
	}
}
