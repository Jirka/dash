package cz.vutbr.fit.dash.model;

import cz.vutbr.fit.dash.model.DashAppModel.PropertyKind;

public class SerializedDashboard {
	
	private String xml = "<dashboard/>";
	private boolean isDirty = false;
	private Dashboard dashboard;
	
	public SerializedDashboard(Dashboard dashboard) {
		this.dashboard = dashboard;
	}

	public String getXml() {
		return xml;
	}

	public void setXml(String xml, boolean dirty) {
		assert xml == null;
		
		setDirty(dirty);
		if(!this.xml.equals(xml)) {
			String oldXml = this.xml;
			this.xml = xml;
			getDashboard().getModel().firePropertyChange(new PropertyChangeEvent(PropertyKind.XML, oldXml, this));
		}
	}

	public boolean isDirty() {
		return isDirty;
	}

	public void setDirty(boolean isDirty) {
		if(this.isDirty != isDirty) {
			this.isDirty = isDirty;
			getDashboard().getModel().firePropertyChange(new PropertyChangeEvent(PropertyKind.IS_DIRTY, !isDirty, this));
		}
	}

	public Dashboard getDashboard() {
		return dashboard;
	}

	public void setDashboard(Dashboard dashboard) {
		this.dashboard = dashboard;
	}

}
