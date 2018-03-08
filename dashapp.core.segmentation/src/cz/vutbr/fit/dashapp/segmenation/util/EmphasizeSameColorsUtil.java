package cz.vutbr.fit.dashapp.segmenation.util;

import java.awt.Point;
import java.util.LinkedList;
import java.util.Queue;

import cz.vutbr.fit.dashapp.util.matrix.GrayMatrix;
import cz.vutbr.fit.dashapp.util.matrix.MatrixUtils;

public class EmphasizeSameColorsUtil {
	
	public static int[][] process(int[][] matrix, int minArea, int minSize) {
		final int mW = MatrixUtils.width(matrix);
		final int mH = MatrixUtils.height(matrix);
		
		int[][] workingCopy = GrayMatrix.copy(matrix);
		int[][] resultMatrix = GrayMatrix.newMatrix(mW, mH, GrayMatrix.WHITE);
		
		int markColor = -1;
		for (int x = 0; x < mW; x++) {
			for (int y = 0; y < mH; y++) {
				if(workingCopy[x][y] >= 0) {
					processSeedPixel(workingCopy, resultMatrix, x, y, workingCopy[x][y], markColor, minArea, minSize);
					markColor--;
				}
			}
		}
		
		return resultMatrix;
	}
	
	private static void processSeedPixel(int[][] matrix, int[][] resultMatrix, int i, int j, int color, int markColor, int minArea, int minSize) {
		int mW = MatrixUtils.width(matrix);
		int mH = MatrixUtils.height(matrix);
		
		// do flood fill algorithm
		Queue<Point> queue = new LinkedList<Point>();
        queue.add(new Point(i, j));
        int x1 = i, x2 = i, y1 = j, y2 = j;
        int n = 0;
        int actColor;
        while (!queue.isEmpty()) {
            Point p = queue.remove();
            actColor = matrix[p.x][p.y];
            if(actColor >= 0) {
            	matrix[p.x][p.y] = markColor;
        		n++;
                	
            	// update min/max points 
            	if(p.x < x1) {
            		x1 = p.x;
            	} else if(p.x > x2) {
            		x2 = p.x;
            	}
            	if(p.y < y1) {
            		y1 = p.y;
            	} else if(p.y > y2) {
            		y2 = p.y;
            	}

            	// add neighbour points
            	addPoint(matrix, queue, p.x + 1, p.y, mW, mH, color);
            	addPoint(matrix, queue, p.x - 1, p.y, mW, mH, color);
            	addPoint(matrix, queue, p.x, p.y + 1, mW, mH, color);
            	addPoint(matrix, queue, p.x, p.y - 1, mW, mH, color);
            	
            	addPoint(matrix, queue, p.x + 1, p.y + 1, mW, mH, color);
            	addPoint(matrix, queue, p.x + 1, p.y - 1, mW, mH, color);
            	addPoint(matrix, queue, p.x - 1, p.y + 1, mW, mH, color);
            	addPoint(matrix, queue, p.x - 1, p.y - 1, mW, mH, color);
            }
        }
        
        x2++; y2++;
        
        int xx1, xx2, yy1, yy2;
        if(n >= minArea && x2-x1 >= minSize && y2-y1 >= minSize) {
        	// emphasize borders of area
            boolean pixelNotFound;
            for (int x = x1; x < x2; x++) {
    			for (int y = y1; y < y2; y++) {
    				if(matrix[x][y] == markColor) {
    					//resultMatrix[x][y] = 125; // debug
    					xx1 = x-1;
    					xx2 = x+1;
    					yy1 = y-1;
    					yy2 = y+1;
    					pixelNotFound = true;
    					for (int a = xx1; a <= xx2 && pixelNotFound; a++) {
    						if(a >= 0 && a < mW) {
    							for (int b = yy1; b <= yy2 && pixelNotFound; b++) {
    								if(b >= 0 && b < mH) {
    									if(matrix[a][b] != markColor) {
    										pixelNotFound = false;
    										resultMatrix[x][y] = GrayMatrix.BLACK;
    										//resultMatrix[x][y] = c;
    									}
    								}
    							}
    						}
    					}
    				}
    			}
    		}
        }
        
        return;
	}
	
	private static void addPoint(int[][] matrix, Queue<Point> queue, int x, int y, int mW, int mH, int refColor) {
		if ((x >= 0) && (x < mW) && (y >= 0) && (y < mH)) {
			if(matrix[x][y] == refColor) {
				queue.add(new Point(x, y));
			}
		}
	}

}
