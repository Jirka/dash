package cz.vutbr.fit.dashapp.model;

import java.awt.image.BufferedImage;

/**
 * 
 * @author Jiri Hynek
 *
 */
public interface IDashboardFile extends IWorkspaceFile {

	SerializedDashboard getSerializedDashboard();

	String getXML();

	Dashboard getDashboard(boolean forceDashboardLoad);

	BufferedImage getImage();

	int[][] getImageMatrix();

}
