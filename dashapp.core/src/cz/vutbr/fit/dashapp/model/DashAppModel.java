package cz.vutbr.fit.dashapp.model;

import java.util.ArrayList;
import java.util.List;

public class DashAppModel {
	
	private static DashAppModel model;
	
	public static DashAppModel getInstance() {
		if(model == null) {
			model = new DashAppModel();
		}
		return model;
	}
	
	private String folderPath = "";
	private Dashboard selectedDashboard = null;
	private List<Dashboard> dashboards;
	private Settings settings;
	
	public DashAppModel() {
	}
	
	public void initModel() {
		settings = new Settings();
		dashboards = new ArrayList<Dashboard>();
		//selectedDashboard = new Dashboard(this, null);
		
		//setFolderPath(getSettings().getDefaultWorkspacePath());
		setFolderPath(System.getProperty("user.home"));
	}
	
	public Settings getSettings() {
		return settings;
	}
	
	public List<Dashboard> getDashboards() {
		return dashboards;
	}
	
	public void addDashboard(Dashboard dashboard) {
		dashboards.add(dashboard);
	}
	
	public void removeDashboard(Dashboard dashboard) {
		dashboards.remove(dashboard);
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
	
	public Dashboard getSelectedDashboard() {
		return selectedDashboard;
	}
	
	public void setSelectedDashboard(Dashboard selectedDashboard) {
		this.selectedDashboard = selectedDashboard;
	}
	
	public String getFolderPath() {
		return folderPath;
	}

	public void setFolderPath(String folderPath) {
		this.folderPath = folderPath;
	}

}
