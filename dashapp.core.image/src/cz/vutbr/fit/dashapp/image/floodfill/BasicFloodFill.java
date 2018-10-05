package cz.vutbr.fit.dashapp.image.floodfill;

import java.awt.Point;
import java.util.LinkedList;
import java.util.Queue;

import cz.vutbr.fit.dashapp.util.matrix.BooleanMatrix;
import cz.vutbr.fit.dashapp.util.matrix.GrayMatrix;
import cz.vutbr.fit.dashapp.util.matrix.MatrixUtils;

/**
 * 
 * @author Jiri Hynek
 *
 */
public class BasicFloodFill {
	
	protected int[][] matrix;
	protected int[][] markMatrix;
	protected boolean[][] processedMatrix;
	protected boolean createNew;
	protected int refColor;
	protected int mW;
	protected int mH;
	protected int area;
	protected boolean isRefColor = false;
	protected int startColor;
	protected int markColor;
	protected int seedX;
	protected int seedY;
	Queue<Point> queue;
	
	public static final int INIT_QUEUE_COLOR = -1;

	public BasicFloodFill(int[][] matrix, boolean createNew, int refColor) {
		setMatrix(matrix);
		this.createNew = createNew;
		setRefColor(refColor);
	}
	
	public BasicFloodFill(int[][] matrix, boolean createNew) {
		setMatrix(matrix);
		this.createNew = createNew;
	}
	
	public void setRefColor(int refColor) {
		this.refColor = refColor;
		this.isRefColor = true;
	}
	
	public void setMatrix(int[][] matrix) {
		this.matrix = matrix;
		mW = MatrixUtils.width(matrix);
		mH = MatrixUtils.height(matrix);
	}
	
	/**
	 * Expects gray matrix.
	 * 
	 * @param matrix
	 * @param createNew
	 * @return
	 */
	public int[][] process() {
		processedMatrix = BooleanMatrix.newMatrix(mW, mH, false);
		markMatrix = matrix;
		if(createNew) {
			//markMatrix = MatrixUtils.copy(matrix);
			markMatrix = GrayMatrix.newMatrix(mW, mH, GrayMatrix.WHITE);
		}
		
		// process matrix
		markColor = INIT_QUEUE_COLOR;
		queue = new LinkedList<Point>();
		for (seedX = 0; seedX < mW; seedX++) {
			for (seedY = 0; seedY < mH; seedY++) {
				if(!processedMatrix[seedX][seedY]) {
					startColor = matrix[seedX][seedY];
					if(!isRefColor || startColor == refColor) {
						processSeedPixel();
						markColor--;
					}
				}
			}
		}
		
		return postProcessMatrix();
	}
	
	protected int[][] postProcessMatrix() {
		// update according to requirements
		return markMatrix;
	}

	protected void processSeedPixel() {
		preprocessSeedPixel();
		
		// do flood fill algorithm
        queue.add(new Point(seedX, seedY));
        processedMatrix[seedX][seedY] = true;
        area = 0;
        int x1 = seedX, x2 = seedX, y1 = seedY, y2 = seedY;
        int actColor;
        while (!queue.isEmpty()) {
            Point p = queue.remove();
            actColor = matrix[p.x][p.y];
        	markMatrix[p.x][p.y] = markColor;
        	area++;
        	
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
        	addPoint(p.x + 1, p.y, actColor);
        	addPoint(p.x - 1, p.y, actColor);
        	addPoint(p.x, p.y + 1, actColor);
        	addPoint(p.x, p.y - 1, actColor);
        	
        	addPoint(p.x + 1, p.y + 1, actColor);
        	addPoint(p.x + 1, p.y - 1, actColor);
        	addPoint(p.x - 1, p.y + 1, actColor);
        	addPoint(p.x - 1, p.y - 1, actColor);
        	
        	postProcessPixel(p.x, p.y, actColor);
        }
        
        x2++; y2++;
        
        postProcessSeedPixel(x1, y1, x2, y2);
	}

	private void addPoint(int x, int y, int actColor) {
		if ((x >= 0) && (x < mW) && (y >= 0) && (y < mH)) {
			if(!processedMatrix[x][y]) {
				if(matches(x, y, actColor)) {
					processedMatrix[x][y] = true; // is in queue (optimization)
					queue.add(new Point(x, y));
				}
			}
		}
	}
	
	protected boolean matches(int x, int y, int actColor) {
		// update according to requirements
		return matrix[x][y] == startColor;
	}
	
	protected void postProcessPixel(int x, int y, int actColor) {
		// update according to requirements
	}
	
	protected void preprocessSeedPixel() {
		// update according to requirements
	}
	
	protected void postProcessSeedPixel(int x1, int y1, int x2, int y2) {
		// update according to requirements
	}

}
