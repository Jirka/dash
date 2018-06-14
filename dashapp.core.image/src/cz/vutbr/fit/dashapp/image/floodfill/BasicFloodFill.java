package cz.vutbr.fit.dashapp.image.floodfill;

import java.awt.Point;
import java.util.LinkedList;
import java.util.Queue;

import cz.vutbr.fit.dashapp.util.matrix.MatrixUtils;

/**
 * 
 * @author Jiri Hynek
 *
 */
public class BasicFloodFill {
	
	protected int[][] matrix;
	protected boolean createNew;
	protected int refColor;
	protected int mW;
	protected int mH;
	
	public static final int QUEUE_COLOR = -1;

	public BasicFloodFill(int[][] matrix, boolean createNew, int refColor) {
		this.matrix = matrix;
		this.createNew = createNew;
		this.refColor = refColor;
	}
	
	/**
	 * Expects black and white matrix.
	 * 
	 * @param matrix
	 * @param createNew
	 * @return
	 */
	public int[][] process() {
		mW = MatrixUtils.width(matrix);
		mH = MatrixUtils.height(matrix);
		
		int resultMatrix[][] = matrix;
		if(createNew) {
			resultMatrix = new int[mW][mH];
			MatrixUtils.copy(resultMatrix, matrix);
		}
		
		// process matrix
		int markColor = QUEUE_COLOR-1;
		for (int i = 0; i < mW; i++) {
			for (int j = 0; j < mH; j++) {
				if(resultMatrix[i][j] == refColor) {
					processSeedPixel(i, j, refColor, markColor, resultMatrix);
					markColor--;
				}
			}
		}
		
		postProcessMatrix(resultMatrix);
		
		return resultMatrix;
	}
	
	protected void postProcessMatrix(int[][] resultMatrix) {
		// update according to requirements
	}

	protected void processSeedPixel(int i, int j, int refColor, int markColor, int[][] matrix) {
		
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
        
        postProcessSeedPixel(x1, y1, x2, y2, markColor);
	}
	
	private void addPoint(int[][] matrix, Queue<Point> queue, int x, int y, int mW, int mH, int refColor) {
		if ((x >= 0) && (x < mW) && (y >= 0) && (y < mH)) {
			if(matrix[x][y] == refColor) {
				matrix[x][y] = QUEUE_COLOR; // is in queue (optimization)
				queue.add(new Point(x, y));
			}
		}
	}
	
	protected void postProcessSeedPixel(int x1, int y1, int x2, int y2, int color) {
		// update according to requirements
	}

}
