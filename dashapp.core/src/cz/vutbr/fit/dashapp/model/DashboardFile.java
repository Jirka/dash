package cz.vutbr.fit.dashapp.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

/**
 * This class represents dashboard source file
 * which can be represented by XML file or image file or both.
 * 
 * @author Jiri Hynek
 *
 */
public class DashboardFile implements IWorkspaceFile {

	private File imageFile;
	private File xmlFile;
	private String name;
	private Dashboard dashboard;
	private String cachedXMLFile;

	public DashboardFile(File imageFile, File xmlFile) {
		setImageFile(imageFile);
		setXmlFile(xmlFile);
		cachedXMLFile = null;
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
			xmlFile = new File(DashAppModel.getInstance().getWorkspaceFolder()+File.separator+name+".xml");
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
	
	public String getFileName() {
		return name;
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
	
	/**
	 * 
	 * @param xmlFile
	 * @return
	 * @throws IOException
	 */
	public String readXMLFile() throws IOException {
		if(xmlFile != null && xmlFile.exists() && xmlFile.canRead()) {
			BufferedReader br = new BufferedReader(new FileReader(xmlFile));
		    try {
		        StringBuilder sb = new StringBuilder();
		        String line = br.readLine();

		        while (line != null) {
		            sb.append(line);
		            sb.append("\n");
		            line = br.readLine();
		        }
		        cachedXMLFile = sb.toString();
		    } finally {
		        br.close();
		    }
		}
		return cachedXMLFile;
	}
	
	public void updateXMLFile(String xml) throws IOException {
		cachedXMLFile = xml;
		if(xmlFile == null) {
			xmlFile = getXmlFile();
		}
		if(!xmlFile.exists()) {
			xmlFile.createNewFile();
		}
		FileOutputStream out = new FileOutputStream(xmlFile);
		out.write(dashboard.getSerializedDashboard().getXml().getBytes());
		out.close();
	}

	public Object getXML() {
		return cachedXMLFile;
	}
}