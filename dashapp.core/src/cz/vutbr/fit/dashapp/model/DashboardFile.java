package cz.vutbr.fit.dashapp.model;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

import javax.imageio.ImageIO;

import cz.vutbr.fit.dashapp.Logger;
import cz.vutbr.fit.dashapp.util.XMLUtils;

/**
 * This class represents dashboard source file
 * which can be represented by XML file or image file or both.
 * 
 * @author Jiri Hynek
 *
 */
public class DashboardFile extends WorkspaceFile implements IWorkspaceFile {

	private File imageFile;
	private File xmlFile;
	private String name;
	private Dashboard dashboard;
	private String cachedXMLFile;
	
	/**
	 * actual serialized representation (unsaved structural description - working copy)
	 */
	private SerializedDashboard serializedDashboard;

	public DashboardFile(DashAppModel model, File imageFile, File xmlFile) {
		super(model);
		setImageFile(imageFile);
		setXmlFile(xmlFile);
		serializedDashboard = new SerializedDashboard();
		cachedXMLFile = null;
	}

	public DashboardFile(DashAppModel model, File file) {
		super(model);
		serializedDashboard = new SerializedDashboard();
		cachedXMLFile = null;
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
	
	/**
	 * Returns raster representation of dashboard if exists
	 * 
	 * @return image
	 */
	public BufferedImage getImage() {
		BufferedImage image = null;
		File file = getImageFile();
		if(file != null && file.exists() && file.canRead()) {
			try {
		        image = ImageIO.read(file);
	        } catch (IOException e) {
	        	Logger.logError("Unable to open file" + file.getAbsolutePath() + ".", e);
	        }
		}
		
		return image;
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
	
	public void reloadDashboard() {
		dashboard = new Dashboard(this);
		try {
			String xml = readXMLFile();
			if(xml != null) {
				// try to deserialize XML (test if valid)
				Dashboard deserializedXML = XMLUtils.deserialize(xml);
				// description is empty -> set image default dimension
				if(xml.startsWith(SerializedDashboard.EMPTY_XML)) {
					BufferedImage image = getImage();
					if(image != null) {
						deserializedXML.width = image.getWidth();
						deserializedXML.height = image.getHeight();
					}
				} else {
					dashboard = new Dashboard(this);
					dashboard.setDimension(deserializedXML.x, deserializedXML.y, deserializedXML.width, deserializedXML.height);
					dashboard.setChildren(deserializedXML.getChildren());
				}
				serializedDashboard.setXml(xml);
			}
		} catch (IOException e) {
			dashboard = null;
		}
	}
	
	public Dashboard getDashboard(boolean forceDashboardLoad) {
		if(dashboard == null && forceDashboardLoad) {
			reloadDashboard();
		}
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
	 * Returns actual serialized representation
	 * (unsaved structural description - working copy)
	 * 
	 * @return serialized dashboard
	 */
	public SerializedDashboard getSerializedDashboard() {
		return serializedDashboard;
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
		out.write(getSerializedDashboard().getXml().getBytes());
		out.close();
	}

	public Object getXML() {
		return cachedXMLFile;
	}
}