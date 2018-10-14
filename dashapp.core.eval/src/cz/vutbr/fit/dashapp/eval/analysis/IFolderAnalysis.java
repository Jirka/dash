package cz.vutbr.fit.dashapp.eval.analysis;

import java.util.List;

import cz.vutbr.fit.dashapp.model.WorkspaceFolder;

/**
 * 
 * @author Jiri Hynek
 *
 */
public interface IFolderAnalysis extends IAnalysis {
	
	public abstract void init();

	public abstract void processFolder(WorkspaceFolder actWorkspaceFolder);
	
	public abstract void sumarizeFolders(WorkspaceFolder actWorkspaceFolder, List<WorkspaceFolder> analyzedFolders);

}
