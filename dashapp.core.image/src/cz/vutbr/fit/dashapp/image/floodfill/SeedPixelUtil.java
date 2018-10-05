package cz.vutbr.fit.dashapp.image.floodfill;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.LinkedList;
import java.util.Queue;

import cz.vutbr.fit.dashapp.util.matrix.MatrixUtils;

/**
 * 
 * @author Jiri Hynek
 *
 */
public class SeedPixelUtil {
	
	public static final int QUEUE_COLOR = -1;

	/**
	 * basic flood-fill seed pixel method which returns boundary of the area.
	 * 
	 * @param i
	 * @param j
	 * @param refColor
	 * @param markColor
	 * @param matrix
	 * @return
	 */
	public static Rectangle processSeedPixel(int i, int j, int refColor, int markColor, int[][] matrix) {
		int mW = MatrixUtils.width(matrix);
		int mH = MatrixUtils.height(matrix);
		
		// do flood fill algorithm
		Queue<Point> queue = new LinkedList<Point>();
        queue.add(new Point(i, j));
        int x1 = i, x2 = i, y1 = j, y2 = j;
        while (!queue.isEmpty()) {
            Point p = queue.remove();
        	matrix[p.x][p.y] = markColor;
        	
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
        	addPoint(matrix, queue, p.x + 1, p.y, mW, mH, refColor);
        	addPoint(matrix, queue, p.x - 1, p.y, mW, mH, refColor);
        	addPoint(matrix, queue, p.x, p.y + 1, mW, mH, refColor);
        	addPoint(matrix, queue, p.x, p.y - 1, mW, mH, refColor);
        	
        	addPoint(matrix, queue, p.x + 1, p.y + 1, mW, mH, refColor);
        	addPoint(matrix, queue, p.x + 1, p.y - 1, mW, mH, refColor);
        	addPoint(matrix, queue, p.x - 1, p.y + 1, mW, mH, refColor);
        	addPoint(matrix, queue, p.x - 1, p.y - 1, mW, mH, refColor);
        }
        
        x2++; y2++;
        
        return new Rectangle(x1, y1, x2-x1, y2-y1);
	}
	
	private static void addPoint(int[][] matrix, Queue<Point> queue, int x, int y, int mW, int mH, int refColor) {
		if ((x >= 0) && (x < mW) && (y >= 0) && (y < mH)) {
			if(matrix[x][y] == refColor) {
				matrix[x][y] = QUEUE_COLOR; // is in queue (optimization)
				queue.add(new Point(x, y));
			}
		}
	}
	
}
