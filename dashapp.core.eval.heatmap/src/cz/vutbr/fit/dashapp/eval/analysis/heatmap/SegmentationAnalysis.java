package cz.vutbr.fit.dashapp.eval.analysis.heatmap;

import java.util.List;

import cz.vutbr.fit.dashapp.model.Dashboard;
import cz.vutbr.fit.dashapp.model.DashboardFile;
import cz.vutbr.fit.dashapp.model.WorkspaceFolder;

public class SegmentationAnalysis extends AbstractHeatMapAnalysis {

	@Override
	public String getLabel() {
		return "Segmentation Analysis";
	}

	@Override
	public void init() {
		
	}

	@Override
	public void processFolder(WorkspaceFolder actWorkspaceFolder) {
		List<DashboardFile> dashboardCandidates = actWorkspaceFolder.getChildren(
				DashboardFile.class, actWorkspaceFolder.getFileName(), false
		);
		if(dashboardCandidates != null && dashboardCandidates.size() == 1) {
			Dashboard dashboard = dashboardCandidates.get(0).getDashboard(true);
			
		}
	}

	@Override
	public void sumarizeFolders(WorkspaceFolder actWorkspaceFolder, List<WorkspaceFolder> analyzedFolders) {
	}

}
