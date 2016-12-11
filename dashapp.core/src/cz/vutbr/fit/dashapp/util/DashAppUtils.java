package cz.vutbr.fit.dashapp.util;

import cz.vutbr.fit.dashapp.model.DashAppModel;
import cz.vutbr.fit.dashapp.model.Dashboard;
import cz.vutbr.fit.dashapp.model.DashboardFile;
import cz.vutbr.fit.dashapp.model.IWorkspaceFile;

public class DashAppUtils {
	
	public static Dashboard getSelectedDashboard() {
		IWorkspaceFile selectedFile = DashAppModel.getInstance().getSelectedFile();
		if(selectedFile != null && selectedFile instanceof DashboardFile) {
			return ((DashboardFile) selectedFile).getDashboard(false);
		}
		return null;
	}
	
	public static DashboardFile getSelectedDashboardFile() {
		IWorkspaceFile selectedFile = DashAppModel.getInstance().getSelectedFile();
		if(selectedFile instanceof DashboardFile) {
			return (DashboardFile) selectedFile;
		}
		return null;
	}

}
