package cz.vutbr.fit.dashapp.eval.analysis;

import java.util.List;

import cz.vutbr.fit.dashapp.model.WorkspaceFolder;
import cz.vutbr.fit.dashapp.util.DashboardCollection;

public abstract class AbstractAnalysis {
	
	public AbstractAnalysis() {
	}
	
	@Override
	public String toString() {
		return getLabel();
	}

	public abstract String getLabel();

	public abstract void processFolder(WorkspaceFolder actWorkspaceFolder, DashboardCollection actDashboards);
	
	public abstract void sumarizeFolders(WorkspaceFolder actWorkspaceFolder, List<WorkspaceFolder> analyzedFolders);

}
