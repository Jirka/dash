package cz.vutbr.fit.dashapp.model;

import java.io.File;

public class WorkspaceFolder implements IWorkspaceFile {
	
	File folderFile;
	
	public WorkspaceFolder(File folderFile) {
		this.folderFile = folderFile;
	}
	
	public File getFile() {
		return folderFile;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof WorkspaceFolder) {
			((WorkspaceFolder) obj).getFile();
			return folderFile.getAbsolutePath().equals(((WorkspaceFolder) obj).getFile().getAbsolutePath());
		}
		return false;
	}

	public String getPath() {
		return folderFile.getAbsolutePath();
	}

	public boolean isParentOf(WorkspaceFolder dashboardFolder) {
		return dashboardFolder.getFile().getParentFile().equals(folderFile);
	}
	
	@Override
	public String toString() {
		return getPath();
	}

}
