package cz.vutbr.fit.dashapp.model;

import java.io.File;
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
	
	private WorkspaceFolder folder;
	private IWorkspaceFile selectedFile = null;
	private List<DashboardFile> openedDashboardFiles;
	private Settings settings;
	
	public DashAppModel() {
	}
	
	public void initModel() {
		settings = new Settings();
		openedDashboardFiles = new ArrayList<DashboardFile>();
		//selectedDashboard = new Dashboard(this, null);
		
		//setFolderPath(getSettings().getDefaultWorkspacePath());
		setWorkspaceFolder(new WorkspaceFolder(this, new File(System.getProperty("user.home"))));
	}
	
	public Settings getSettings() {
		return settings;
	}
	
	public List<DashboardFile> getOpenedDashboardFiles() {
		return openedDashboardFiles;
	}
	
	public void setDashboardFileOpened(DashboardFile dashboardFile) {
		openedDashboardFiles.add(dashboardFile);
	}
	
	public void setDashboardFileClosed(DashboardFile dashboardFile) {
		openedDashboardFiles.remove(dashboardFile);
	}
	
	public DashboardFile getOpenedDashboardFile(DashboardFile dashboardFile) {
		if(dashboardFile != null) {
			for (DashboardFile openedDashboardFile : openedDashboardFiles) {
				if(dashboardFile.equals(openedDashboardFile)) {
					return openedDashboardFile;
				}
			}
		}
		return null;
	}
	
	public IWorkspaceFile getSelectedFile() {
		return selectedFile;
	}
	
	public void setSelectedFile(IWorkspaceFile selectedWorkspaceFile) {
		this.selectedFile = selectedWorkspaceFile;
	}
	
	public WorkspaceFolder getWorkspaceFolder() {
		return this.folder;
	}

	public void setWorkspaceFolder(WorkspaceFolder folder) {
		this.folder = folder;
	}

}
