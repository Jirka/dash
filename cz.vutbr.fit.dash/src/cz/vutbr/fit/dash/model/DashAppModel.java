package cz.vutbr.fit.dash.model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DashAppModel {
	
	private static DashAppModel model;
	
	private List<PropertyChangeListener> listeners;
	
	private String folderPath = "";
	private Dashboard selectedDashboard = null;
	private List<Dashboard> dashboards;
	private int zoomLevel;
	private WidgetActionKind widgetActionKind;
	private boolean listenersDisabled;
	private boolean attachEnabled;
	
	public static final double[] zoomField = { 0.125, 0.25, 0.375, 0.5, 0.75, 1.0, 1.5, 2.0, 3.0, 4.0, 8.0 };
	public static final int DEFAULT_ZOOM_LEVEL = 5;
	
	public static enum PropertyKind {
		FOLDER_PATH,
		DASHBOARD_SELECTION,
		ZOOM_LEVEL,
		WIDGET_ACTION,
		XML,
		DASHBOARD_ELEMENTS,
		GRAPHICAL_ELEMENT,
		IS_DIRTY,
		ATTACH
	}
	
	public static enum WidgetActionKind {
		VIEW,
		SELECT,
		INSERT,
		BOUND,
		WAND
	}
	
	public DashAppModel() {
		listeners = new ArrayList<PropertyChangeListener>();
		dashboards = new ArrayList<Dashboard>();
		//selectedDashboard = new Dashboard(this, null);
	}
	
	public static DashAppModel getInstance() {
		if(model == null) {
			model = new DashAppModel();
		}
		return model;
	}

	public boolean isListenersDisabled() {
		return listenersDisabled;
	}

	public void setListenersDisabled(boolean listenersDisabled) {
		this.listenersDisabled = listenersDisabled;
	}

	public boolean isAttachEnabled() {
		return attachEnabled;
	}

	public void setAttachEnabled(boolean attachEnabled) {
		if(this.attachEnabled != attachEnabled) {
			this.attachEnabled = attachEnabled;
			firePropertyChange(new PropertyChangeEvent(PropertyKind.ATTACH, !attachEnabled, attachEnabled));
		}
	}

	public String getFolderPath() {
		return folderPath;
	}

	public void setFolderPath(String folderPath) {
		assert folderPath == null;
		
		if(!this.folderPath.equals(folderPath)) {
			String oldPath = this.folderPath;
			this.folderPath = folderPath;
			firePropertyChange(new PropertyChangeEvent(PropertyKind.FOLDER_PATH, oldPath, folderPath));
			dashboards.clear(); // folder is changed, all cached dashboards can be released
		}
	}
	
	public Dashboard getSelectedDashboard() {
		return selectedDashboard;
	}
	
	private void setSelectedDashboard(Dashboard selectedDashboard) {
		if(this.selectedDashboard != selectedDashboard) {
			Dashboard oldDashboard = selectedDashboard;
			this.selectedDashboard = selectedDashboard;
			firePropertyChange(new PropertyChangeEvent(PropertyKind.DASHBOARD_SELECTION, oldDashboard, selectedDashboard));
		}
	}

	public boolean createEmptyDashboard(int width, int height, String name) {
		File xmlFile = new File(folderPath + File.separator + name + ".xml");
		if(!xmlFile.exists()) {
			Dashboard dashboard = new Dashboard(this, new DashboardFile(xmlFile));
			dashboard.initSize(0, 0, width, height);
			try {
				setListenersDisabled(true);
				try {
					dashboard.serialize();
					dashboard.saveToFile();
					dashboards.add(dashboard);
				} finally {
					setListenersDisabled(false);
				}
				
				// fire changes
				firePropertyChange(new PropertyChangeEvent(PropertyKind.FOLDER_PATH, folderPath, folderPath));
				setSelectedDashboard(dashboard);
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

	public void setSelectedDashboard(DashboardFile dashboardFile) {
		
		Dashboard dashboard = getDashboard(dashboardFile);
		if(dashboard == null && dashboardFile != null) {
			dashboard = new Dashboard(this, dashboardFile);
			try {
				setListenersDisabled(true);
				dashboard.reloadFromFile();
			} catch (Exception e) {
				// no information 
			} finally {
				setListenersDisabled(false);
			}
			dashboards.add(dashboard);
		}
		
		setSelectedDashboard(dashboard);
	}
	
	public Dashboard getDashboard(DashboardFile dashboardFile) {
		if(dashboardFile != null) {
			for (Dashboard dashboard : dashboards) {
				if(dashboard.getDashboardFile().equals(dashboardFile)) {
					return dashboard;
				}
			}
		}
		return null;
	}

	public int getZoomLevel() {
		return zoomLevel;
	}

	public void setZoomLevel(int zoomLevel) {
		if(this.zoomLevel != zoomLevel) {
			int oldZoomLevel = this.zoomLevel;
			this.zoomLevel = zoomLevel;
			firePropertyChange(new PropertyChangeEvent(PropertyKind.ZOOM_LEVEL, oldZoomLevel, zoomLevel));
		}
	}

	public WidgetActionKind getWidgetAction() {
		return widgetActionKind;
	}

	public void setWidgetAction(WidgetActionKind widgetActionKind) {
		if(this.widgetActionKind != widgetActionKind) {
			WidgetActionKind oldWidgetAction = this.widgetActionKind;
			this.widgetActionKind = widgetActionKind;
			firePropertyChange(new PropertyChangeEvent(PropertyKind.WIDGET_ACTION, oldWidgetAction, widgetActionKind));
		}
		this.widgetActionKind = widgetActionKind;
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		if(!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}
	
	public void removePropertyChangeListener(PropertyChangeListener toolBar) {
		listeners.remove(toolBar);
	}

	public void firePropertyChange(PropertyChangeEvent propertyChangeEvent) {
		if(!isListenersDisabled()) {
			for (PropertyChangeListener propertyChangeListener : listeners) {
				propertyChangeListener.firePropertyChange(propertyChangeEvent);
			}
		}
	}

	public void initModel() {
		setZoomLevel(DashAppModel.DEFAULT_ZOOM_LEVEL);
		setWidgetAction(WidgetActionKind.VIEW);
		setFolderPath("/home/jurij/Plocha/dashboards/evals/widget-based/random");
		//setFolderPath(System.getProperty("user.home"));
	}

}
