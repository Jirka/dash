package cz.vutbr.fit.dashapp.util;

import java.util.List;

import cz.vutbr.fit.dashapp.model.Dashboard;
import cz.vutbr.fit.dashapp.model.GraphicalElement.GEType;

public class DashboardCollection {
	
	public Dashboard[] dashboards;
	public int length;
	public int width;
	public int height;
	
	public DashboardCollection(List<Dashboard> dashboards) {
		this.dashboards = new Dashboard[dashboards.size()];
		this.length = 0;
		for (Dashboard dashboard : dashboards) {
			this.dashboards[length] = dashboard;
			updateSize(dashboard);
			length++;
		}
	}

	public DashboardCollection(Dashboard[] dashboards) {
		this.length = dashboards.length;
		this.dashboards = new Dashboard[dashboards.length];
		this.length = 0;
		for (Dashboard dashboard : dashboards) {
			this.dashboards[length] = dashboard;
			updateSize(dashboard);
			length++;
		}
	}

	private void updateSize(Dashboard dashboard) {
		if(dashboard.width > this.width) {
			this.width = dashboard.width;
		}
		if(dashboard.height > this.height) {
			this.height = dashboard.height;
		}
	}
	
	public int[][] printDashboards(GEType[] types) {
		// init matrix
		int[][] matrix = new int[width][height];
		
		// print dashboards
		for (Dashboard dashboard : dashboards) {
			boolean[][] dashboardMatrix = new boolean[dashboard.width][dashboard.height];
			MatrixUtils.printDashboard(dashboardMatrix, dashboard, true, types);
			for(int i = 0; i < dashboard.width; i++) {
				for(int j = 0; j < dashboard.height; j++) {
					if(dashboardMatrix[i][j]) {
						matrix[i][j]++; // addition
					}
				}
			}
			/*List<GraphicalElement> graphicalElements = dashboard.getChildren(types);
			// print graphical elements
			for(GraphicalElement graphicalElement : graphicalElements) {
				// optimization
				int x2 = graphicalElement.x2();
				int y2 = graphicalElement.y2();
				// print
				for(int i = graphicalElement.x; i < x2; i++) {
					for(int j = graphicalElement.y; j < y2; j++) {
						matrix[i][j]++; // addition
					}
				}
			}*/
		}
		return matrix;
	}

	public int size() {
		return this.width*this.height;
	}

}
