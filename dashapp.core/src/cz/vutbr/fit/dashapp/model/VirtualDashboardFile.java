package cz.vutbr.fit.dashapp.model;

import java.awt.image.BufferedImage;

import cz.vutbr.fit.dashapp.util.matrix.ColorMatrix;

public class VirtualDashboardFile extends WorkspaceFile implements IDashboardFile {
	
	protected Dashboard dashboard;
	protected String fileName;
	protected String xml;
	protected SerializedDashboard serializedDashboard;
	protected BufferedImage image;
	protected int[][] imageMatrix;
	
	public VirtualDashboardFile(DashAppModel model) {
		super(model);
	}
	
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	@Override
	public String getFileName() {
		return this.fileName;
	}
	
	public void setSerializedDashboard(SerializedDashboard serializedDashboard) {
		this.serializedDashboard = serializedDashboard;
	}

	@Override
	public SerializedDashboard getSerializedDashboard() {
		return serializedDashboard;
	}
	
	public void setXml(String xml) {
		this.xml = xml;
	}

	@Override
	public String getXML() {
		return xml;
	}
	
	public void setDashboard(Dashboard dashboard) {
		this.dashboard = dashboard;
	}

	@Override
	public Dashboard getDashboard(boolean forceDashboardLoad) {
		return dashboard;
	}
	
	public void setImage(BufferedImage image) {
		this.image = image;
	}
	
	@Override
	public BufferedImage getImage() {
		return image;
	}
	
	@Override
	public int[][] getImageMatrix() {
		if(imageMatrix == null) {
			imageMatrix = ColorMatrix.printImageToMatrix(image);
		}
		return imageMatrix;
	}
	
	@Override
	public void clearCache() {
		super.clearCache();
		imageMatrix = null;
	}

}
