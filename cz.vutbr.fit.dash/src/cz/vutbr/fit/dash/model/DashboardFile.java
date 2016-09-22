package cz.vutbr.fit.dash.model;

import java.io.File;

/**
 * This class represents dashboard source file
 * which can be represented by XML file or image file or both.
 * 
 * @author Jiri Hynek
 *
 */
public class DashboardFile {

	private File imageFile;
	private File xmlFile;
	private String name;
	private Dashboard dashboard;

	public DashboardFile(File imageFile, File xmlFile) {
		setImageFile(imageFile);
		setXmlFile(xmlFile);
	}

	public DashboardFile(File file) {
		setFile(file);
	}

	public void setFile(File file) {
		if (file.getName().toLowerCase().endsWith(".xml")) {
			setXmlFile(file);
		} else {
			setImageFile(file);
		}
	}

	public File getImageFile() {
		return imageFile;
	}

	public void setImageFile(File imageFile) {
		this.imageFile = imageFile;
		if (name == null) {
			setName(imageFile);
		}
	}

	public File getXmlFile() {
		if(xmlFile == null) {
			xmlFile = new File(DashAppModel.getInstance().getFolderPath()+File.separator+name+".xml");
		}
		return xmlFile;
	}

	public void setXmlFile(File xmlFile) {
		this.xmlFile = xmlFile;
		if (name == null) {
			setName(xmlFile);
		}
	}
	
	private void setName(File file) {
		String name = file.getName();
		int dotPosition = name.lastIndexOf('.');
		this.name = name.substring(0, dotPosition);
	}
	
	public Dashboard getDashboard() {
		return dashboard;
	}

	public void setDashboard(Dashboard dashboard) {
		this.dashboard = dashboard;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public boolean equals(Object obj) {
		return toString().equals(obj.toString());
	}
}