package cz.vutbr.fit.dashapp.util;

import java.util.LinkedList;
import java.util.List;

import cz.vutbr.fit.dashapp.model.DashAppModel;
import cz.vutbr.fit.dashapp.model.Dashboard;
import cz.vutbr.fit.dashapp.model.DashboardFile;
import cz.vutbr.fit.dashapp.model.IWorkspaceFile;

/**
 * 
 * @author Jiri Hynek
 *
 */
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
	
	public static List<Dashboard> getDashboards(List<DashboardFile> dashboardFiles) {
		List<Dashboard> dashboards = new LinkedList<>();
		for (DashboardFile dashboardFile : dashboardFiles) {
			dashboards.add(dashboardFile.getDashboard(true));
		}
		return dashboards;
	}

	public static DashboardCollection makeDashboardCollection(List<DashboardFile> dashboardFiles) {
		return new DashboardCollection(getDashboards(dashboardFiles));
	}

}
