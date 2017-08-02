package cz.vutbr.fit.dashapp.util.matrix;

import java.util.List;

import cz.vutbr.fit.dashapp.model.Dashboard;
import cz.vutbr.fit.dashapp.model.GraphicalElement;
import cz.vutbr.fit.dashapp.model.GraphicalElement.GEType;

public class BooleanMatrix {
	
	public static int count(boolean[][] matrix) {
		int sum = 0;
		int mW = matrix.length;
		int mH = matrix[0].length;
		
		for (int i = 0; i < mW; i++) {
			for (int j = 0; j < mH; j++) {
				if(matrix[i][j]) {
					sum++;
				}
			}
		}
		return sum;
	}
	
	public static void initMattrix(boolean[][] matrix, boolean initValue) {
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[i].length; j++) {
				matrix[i][j] = initValue;
			}
		}
	}
	
	public static boolean[][] printDashboard(Dashboard dashboard, boolean clear, GEType[] types) {
		boolean[][] matrix = new boolean[dashboard.width][dashboard.height];
		printDashboard(matrix, dashboard, clear, types, false);
		return matrix;
	}
	
	public static void printDashboard(boolean[][] matrix, Dashboard dashboard, boolean clear, GEType[] types) {
		printDashboard(matrix, dashboard, clear, types, false);
	}

	public static void printDashboard(boolean[][] matrix, Dashboard dashboard, boolean clear, GEType[] types, boolean excludeBorders) {
		if(clear) {
			initMattrix(matrix, false);
		}
		List<GraphicalElement> graphicalElements = dashboard.getChildren(types);
		for(GraphicalElement graphicalElement : graphicalElements) {
			printGraphicalElement(matrix, graphicalElement, excludeBorders);
		}
	}
	
	public static void printGraphicalElement(boolean[][] matrix, GraphicalElement graphicalElement) {
		printGraphicalElement(matrix, graphicalElement, false);
	}

	public static void printGraphicalElement(boolean[][] matrix, GraphicalElement graphicalElement, boolean excludeBorders) {
		// optimization
		int x2 = graphicalElement.x2();
		int y2 = graphicalElement.y2();
		
		int x1 = excludeBorders ? (graphicalElement.x+1) : graphicalElement.x;
		int y1 = excludeBorders ? (graphicalElement.y+1) : graphicalElement.y;
		x2 = excludeBorders ? (x2-1) : x2;
		y2 = excludeBorders ? (y2-1) : y2;
		
		// print
		for(int i = x1; i < x2; i++) {
			for(int j = y1; j < y2; j++) {
				matrix[i][j] = true;
			}
		}
	}

}
