package cz.vutbr.fit.dashapp.model;

/**
 * This class represents serialized dashboard
 * 
 * @author Jiri Hynek
 *
 */
public class SerializedDashboard {
	
	public static final String EMPTY_XML = "<dashboard/>";
	
	private String xml = EMPTY_XML;
	private boolean isDirty = false;
	
	public SerializedDashboard() {
	}

	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}

	public boolean isDirty() {
		return isDirty;
	}

	public void setDirty(boolean isDirty) {
		this.isDirty = isDirty;
	}
}
