package cz.vutbr.fit.dashapp.model;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import cz.vutbr.fit.dashapp.util.DashboardFileFilter;
import cz.vutbr.fit.dashapp.util.FolderFilter;

/**
 * 
 * @author Jiri Hynek
 *
 */
public class WorkspaceFolder extends WorkspaceFile implements IWorkspaceFile {
	
	File folderFile;
	IWorkspaceFile[] children;
	
	public WorkspaceFolder(DashAppModel model, File folderFile) {
		super(model);
		this.folderFile = folderFile;
		this.children = null;
	}
	
	public File getFile() {
		return folderFile;
	}
	
	@Override
	public String getFileName() {
		return folderFile.getName();
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
		return folderFile.equals(dashboardFolder.getFile().getParentFile());
	}
	
	@Override
	public String toString() {
		return getPath();
	}
	
	@SuppressWarnings("unchecked")
	public <T extends IWorkspaceFile> List<T> getChildren(Class<T> type, String regex, boolean forceRefresh) {
		List<T> filteredDashboardFiles = new LinkedList<>();
		IWorkspaceFile[] children = getChildren(forceRefresh);
		for (IWorkspaceFile file : children) {
			if(type.isInstance(file) && (regex == null || file.getFileName().matches(regex))) {
				filteredDashboardFiles.add((T) file);
			}
		}
		return filteredDashboardFiles;
	}
	
	public IWorkspaceFile[] getChildren(boolean forceRefresh) {
		if(this.children == null || forceRefresh) {
			if(this.folderFile != null && this.folderFile.exists()) {
				// folder files
				File[] subFolders = folderFile.listFiles(new FolderFilter());
				if(subFolders != null) {
					Arrays.sort(subFolders);
				}
				// dashboard files
				File[] files = folderFile.listFiles(new DashboardFileFilter());
				List<DashboardFile> dashboardFiles = new LinkedList<>();
				int dashboardsCount = 0;
				if(files != null) {
					Arrays.sort(files);
					for (File file : files) {
						String name = file.getName();
						int dotPosition = name.lastIndexOf('.');
						name = name.substring(0, dotPosition);
						DashboardFile existingFile = null;
						for (DashboardFile dashboardFile : dashboardFiles) {
							if(dashboardFile.toString().equals(name)) {
								existingFile = dashboardFile;
							}
						}
						if(existingFile != null) {
							existingFile.setFile(file);
						} else {
							dashboardFiles.add(new DashboardFile(getModel(), file));
							dashboardsCount++;
						}
					}
				}
				this.children = new IWorkspaceFile[subFolders.length+dashboardsCount];
				int i = 0;
				for (File subfolder : subFolders) {
					this.children[i] = new WorkspaceFolder(getModel(), subfolder);
					i++;
				}
				for (DashboardFile dashboardFile : dashboardFiles) {
					this.children[i] = dashboardFile;
					i++;
				}
			}
		}
		return this.children;
	}

}
