package cz.vutbr.fit.dashapp.controller;

import java.io.File;
import java.io.IOException;
import java.util.List;

import cz.vutbr.fit.dashapp.model.DashAppModel;
import cz.vutbr.fit.dashapp.model.Dashboard;
import cz.vutbr.fit.dashapp.model.DashboardFile;
import cz.vutbr.fit.dashapp.model.GraphicalElement;
import cz.vutbr.fit.dashapp.model.GraphicalElement.GEType;
import cz.vutbr.fit.dashapp.util.XMLUtils;
import cz.vutbr.fit.dashapp.controller.PropertyChangeEvent.Change;

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
		DASHBOARD_STATE_CHANGED;

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
	 * Method updates dashboard XML and dashboard model if specified XML is valid.
	 * Fires XML property change event if change is made.
	 * 
	 * @param dashboard
	 * @param xml
	 */
	public void updateDashboardXml(Dashboard dashboard, String xml) {
		assert xml == null;
		// compare the new and old versions
		String oldXML = dashboard.getSerializedDashboard().getXml();
		if(!oldXML.equals(xml)) {
			// try to deserialize XML (test if valid)
			Dashboard deserializedXML = XMLUtils.deserialize(xml);
			if(deserializedXML != null) {
				// update XML
				dashboard.getSerializedDashboard().setXml(xml);
				// update model (deserialize XML)
				dashboard.setDimension(deserializedXML.x, deserializedXML.y, deserializedXML.width, deserializedXML.height);
				dashboard.setChildren(deserializedXML.getChildren());
				// fire change
				controller.firePropertyChange(new PropertyChangeEvent(EventKind.XML_CHANGED, dashboard, null, new Change(oldXML, xml)));
				// updates dashboard state
				updateDashboardState(dashboard);
			}			
		}
	}
	
	/**
	 * Method updates graphical element. If change is made dashboard XML is updated
	 * and dashboard model property change event is fired.
	 * 
	 * @param ge
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param convertToRelative
	 */
	public void updateGraphicalElement(GraphicalElement ge, int x, int y, int width, int height, boolean convertToRelative) {
		// update dimensions of graphical element children
		updateGEChildren(ge, x, y, width, height, convertToRelative);
		// update graphical element dimensions
		GraphicalElement copy = updateGEDimension(ge, x, y, width, height, convertToRelative);
		// if change has been made, XML needs to be updated and change event needs to be fired 
		if(copy != null) {
			// update dashboard serialized definition (serialization problem should not occur)
			Dashboard dashboard = ge.getDashboard();
			String oldXML = dashboard.getSerializedDashboard().getXml();
			String newXML = XMLUtils.serialize(dashboard);
			dashboard.getSerializedDashboard().setXml(newXML);
			// fire property change
			controller.firePropertyChange(new PropertyChangeEvent(EventKind.GRAPHICAL_ELEMENT_CHANGED, dashboard, new Change(copy, ge), new Change(oldXML, newXML)));
			// updates dashboard state
			updateDashboardState(dashboard);
		}
	}
	
	/**
	 * Help method which updates child elements relative dimensions of currently modified graphical element.
	 * It does not fire any property change event. 
	 * 
	 * @param ge
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param convertToRelative
	 */
	private void updateGEChildren(GraphicalElement ge, int x, int y, int width, int height, boolean convertToRelative) {
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
	
	/**
	 * Help method which updates graphical element dimension.
	 * Returns true if change is made.
	 * 
	 * @param ge
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param convertToRelative
	 * @return
	 */
	private GraphicalElement updateGEDimension(GraphicalElement ge, int x, int y, int width, int height, boolean convertToRelative) {
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
	
	/**
	 * Updates graphical element type.
	 * If change is made XML is updated and model property change is fired.
	 * 
	 * @param ge
	 * @param type
	 */
	public void updateGraphicalElement(GraphicalElement ge, GEType type) {
		if(ge.type != type) {
			// update type if changed
			GraphicalElement copy = ge.copy();
			ge.type = type;
			// update dashboard serialized definition (serialization problem should not occur)
			Dashboard dashboard = ge.getDashboard();
			String oldXML = dashboard.getSerializedDashboard().getXml();
			String newXML = XMLUtils.serialize(dashboard);
			dashboard.getSerializedDashboard().setXml(newXML);
			// fire property change
			controller.firePropertyChange(new PropertyChangeEvent(EventKind.GRAPHICAL_ELEMENT_CHANGED, dashboard, new Change(copy, ge), new Change(oldXML, newXML)));
			// updates dashboard state
			updateDashboardState(dashboard);
		}
	}
	
	/**
	 * Deletes graphical element.
	 * Fires model property change event.
	 * 
	 * @param selectedElement
	 */
	public void deleteGraphicalElement(GraphicalElement selectedElement) {
		// it is not possible to delete root dashboard graphical element
		GraphicalElement parent = selectedElement.getParent();
		if(parent != null) {
			// delete from parent
			parent.deleteChildGE(selectedElement);
			// update dashboard serialized definition (serialization problem should not occur)
			Dashboard dashboard = parent.getDashboard();
			String oldXML = dashboard.getSerializedDashboard().getXml();
			String newXML = XMLUtils.serialize(dashboard);
			dashboard.getSerializedDashboard().setXml(newXML);
			// fire property change
			controller.firePropertyChange(new PropertyChangeEvent(EventKind.GRAPHICAL_ELEMENT_DELETED, 
					dashboard, new Change(selectedElement, parent), new Change(oldXML, newXML)));
			// updates dashboard state
			updateDashboardState(dashboard);
		}		
	}
	
	/**
	 * Creates new graphical element.
	 * Fires model property change event.
	 * 
	 * @param parent
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param convertToRelative
	 * @return new graphical element
	 */
	public GraphicalElement createGrapicalElement(GraphicalElement parent, int x, int y, int width, int height, boolean convertToRelative) {
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
		String newXML = XMLUtils.serialize(dashboard);
		dashboard.getSerializedDashboard().setXml(newXML);
		// fire property changed
		controller.firePropertyChange(new PropertyChangeEvent(EventKind.GRAPHICAL_ELEMENT_CHANGED, 
				dashboard, new Change(null, graphicalElement), new Change(oldXML, newXML)));
		// updates dashboard state
		updateDashboardState(dashboard);
		
		return graphicalElement;
	}

	/**
	 * Updates dashboard state.
	 * If state is changed dahboard state property change is fired.
	 * 
	 * @param dashboard
	 */
	public void updateDashboardState(Dashboard dashboard) {
		boolean shouldBeDirty = !dashboard.getSerializedDashboard().getXml().equals(dashboard.getDashboardFile().getXML());
		if(dashboard.getSerializedDashboard().isDirty() != shouldBeDirty) {
			dashboard.getSerializedDashboard().setDirty(shouldBeDirty);
			controller.firePropertyChange(new PropertyChangeEvent(EventKind.DASHBOARD_STATE_CHANGED, 
					dashboard, new Change(!shouldBeDirty, shouldBeDirty), null));
		}
	}
	
	/**
	 * Change dashboard selection.
	 * Fires selection property change event.
	 * 
	 * @param dashboard
	 */
	public void updateSelectedDashboard(Dashboard dashboard) {
		DashAppModel model = DashAppModel.getInstance();
		Dashboard oldDashboard = model.getSelectedDashboard();
		if(oldDashboard != dashboard) {
			model.setSelectedDashboard(dashboard);
			controller.firePropertyChange(new PropertyChangeEvent(EventKind.DASHBOARD_SELECTION_CHANGED, dashboard, new Change(oldDashboard, dashboard), null));
		}
	}
	
	/**
	 * Updates dashboards folder path.
	 * fires folder path property change event.
	 * 
	 * @param folderPath
	 */
	public void updateFolderPath(String folderPath) {
		assert folderPath == null;
		
		DashAppModel model = DashAppModel.getInstance();
		String oldPath = model.getFolderPath();
		if(!oldPath.equals(folderPath)) {
			updateSelectedDashboard((Dashboard) null);
			model.setFolderPath(folderPath);
			controller.firePropertyChange(new PropertyChangeEvent(EventKind.FOLDER_PATH_CHANGED, null, new Change(oldPath, folderPath), null));
			model.getDashboards().clear(); // folder is changed, all cached dashboards can be released
		}
	}
	
	/**
	 * Takes dashboard and reload it from file if dashboard file exists.
	 * It can fire XML property change event.
	 * 
	 * @param dashboard
	 * @throws Exception
	 */
	public void reloadDashboardFromFile(Dashboard dashboard) throws Exception {
		String xml = dashboard.getDashboardFile().readXMLFile();
		if(xml != null) {
			updateDashboardXml(dashboard, xml);
		}
	}
	
	/**
	 * Takes dashboard and save it to file.
	 * Dashboard state property change can be fired. 
	 * 
	 * @param dashboard
	 * @throws IOException
	 */
	public void saveDashboardToFile(Dashboard dashboard) throws IOException {
		dashboard.getDashboardFile().updateXMLFile(dashboard.getSerializedDashboard().getXml());
		// updates dashboard state
		updateDashboardState(dashboard);
	}
	
	/**
	 * Change dashboard selection according to DashboardFile definition.
	 * Dashboard selection property change event is fired.
	 * 
	 * @param dashboardFile
	 */
	public void updateSelectedDashboard(DashboardFile dashboardFile) {
		// find dashboard for selected dashboard definition
		DashAppModel model = DashAppModel.getInstance();
		Dashboard dashboard = model.getDashboard(dashboardFile);
		
		// if dashboard has not been opened yet it needs to be created
		if(dashboard == null && dashboardFile != null) {
			// create new dashboard for selected dashboard file definition
			dashboard = new Dashboard(model, dashboardFile);
			try {
				// dashboard is initialized, not changed
				controller.setListenersDisabled(true);
				reloadDashboardFromFile(dashboard);
			} catch (Exception e) {
				System.err.println("Unable to load dashboard " + dashboardFile.toString()); 
			} finally {
				controller.setListenersDisabled(false);
			}
			// add to model
			model.addDashboard(dashboard);
		}
		
		updateSelectedDashboard(dashboard);
	}

	/**
	 * Creates new empty dashboard.
	 * Dashboard selection property change event is fired.
	 * 
	 * @param width
	 * @param height
	 * @param name
	 * @return
	 */
	public boolean createEmptyDashboard(int width, int height, String name) {
		DashAppModel model = DashAppModel.getInstance();
		String folderPath = model.getFolderPath();
		File xmlFile = new File(folderPath + File.separator + name + ".xml");
		if(!xmlFile.exists()) {
			Dashboard dashboard = new Dashboard(model, new DashboardFile(xmlFile));
			dashboard.setDimension(0, 0, width, height);
			try {
				// serialize
				dashboard.getSerializedDashboard().setXml(XMLUtils.serialize(dashboard));
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
}