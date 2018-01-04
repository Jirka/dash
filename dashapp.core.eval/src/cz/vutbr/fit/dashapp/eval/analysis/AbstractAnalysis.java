package cz.vutbr.fit.dashapp.eval.analysis;

import java.util.List;

import cz.vutbr.fit.dashapp.model.WorkspaceFolder;

public abstract class AbstractAnalysis {
	
	public AbstractAnalysis() {
	}
	
	@Override
	public String toString() {
		return getLabel();
	}

	public abstract String getLabel();
	
	public abstract void init();

	public abstract void processFolder(WorkspaceFolder actWorkspaceFolder);
	
	public abstract void sumarizeFolders(WorkspaceFolder actWorkspaceFolder, List<WorkspaceFolder> analyzedFolders);

}
