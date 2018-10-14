package cz.vutbr.fit.dashapp.eval.analysis.heatmap;

import java.util.ArrayList;
import java.util.List;

import cz.vutbr.fit.dashapp.model.Dashboard;
import cz.vutbr.fit.dashapp.model.DashboardFile;
import cz.vutbr.fit.dashapp.model.GraphicalElement;
import cz.vutbr.fit.dashapp.model.WorkspaceFolder;

/**
 * 
 * @author Jiri Hynek
 *
 */
public class TestOfGEBoundariesAnalysis extends AbstractHeatMapAnalysis {
	
	List<WorkspaceFolder> folders;

	@Override
	public String getLabel() {
		return "Text of XML";
	}

	@Override
	public void init() {
		folders = new ArrayList<>();
	}

	@Override
	public void processFolder(WorkspaceFolder actWorkspaceFolder) {
		List<DashboardFile> dashboardCandidates = actWorkspaceFolder.getChildren(
				DashboardFile.class, actWorkspaceFolder.getFileName(), false
		);
		if(dashboardCandidates != null && dashboardCandidates.size() == 1) {
			Dashboard dashboard = dashboardCandidates.get(0).getDashboard(true);
			if(dashboard != null) {
				int w = dashboard.width;
				int h = dashboard.height;
				List<GraphicalElement> ges = dashboard.getChildren();
				if(ges != null) {
					for (GraphicalElement ge : ges) {
						if(ge.x < 0 || ge.x2() > w || ge.y < 0 || ge.y2() > h) {
							System.out.println(w + " " + h + " " + ge.x + " " + ge.x2() + " " + ge.y + " " + ge.y2());
							folders.add(actWorkspaceFolder);
							break;
						}
					}
				}
			}
		}
	}

	@Override
	public void sumarizeFolders(WorkspaceFolder actWorkspaceFolder, List<WorkspaceFolder> analyzedFolders) {
		for (WorkspaceFolder workspaceFolder : folders) {
			System.out.println(workspaceFolder.getFileName() + " contains ge outside dashboard area.");
		}
	}

}
