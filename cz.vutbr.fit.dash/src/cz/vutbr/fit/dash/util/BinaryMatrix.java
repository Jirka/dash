package cz.vutbr.fit.dash.util;

import cz.vutbr.fit.dash.model.Dashboard;
import cz.vutbr.fit.dash.model.GraphicalElement;
import cz.vutbr.fit.dash.model.GraphicalElement.GEType;

public class BinaryMatrix {

	private boolean[][] mattrix;
	private int width, height;
	
	public void initMattrix(boolean initValue) {
		MatrixUtils.initMattrix(mattrix, initValue);
	}

	public void printDashboard(Dashboard dashboard, boolean clear, GEType[] types) {
		MatrixUtils.printDashboard(mattrix, dashboard, clear, types);
	}

	public void printGraphicalElement(boolean[][] mattrix, GraphicalElement graphicalElement) {
		MatrixUtils.printGraphicalElement(mattrix, graphicalElement);
	}
	
	public boolean[][] getMattrix() {
		return mattrix;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	
}
