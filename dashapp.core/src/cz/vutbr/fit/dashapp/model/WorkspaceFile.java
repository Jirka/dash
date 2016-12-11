package cz.vutbr.fit.dashapp.model;

public abstract class WorkspaceFile implements IWorkspaceFile {
	
	/**
	 * model definition
	 */
	protected DashAppModel model;
	
	public WorkspaceFile(DashAppModel model) {
		this.model = model;
	}
	
	/**
	 * 
	 * @return model
	 */
	public DashAppModel getModel() {
		return model;
	}

	/**
	 * 
	 * @param model
	 */
	public void setModel(DashAppModel model) {
		this.model = model;
	}

}
