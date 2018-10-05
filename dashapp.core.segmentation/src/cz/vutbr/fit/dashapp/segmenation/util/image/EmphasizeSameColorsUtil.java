package cz.vutbr.fit.dashapp.segmenation.util.image;

import cz.vutbr.fit.dashapp.image.floodfill.BasicFloodFill;
import cz.vutbr.fit.dashapp.util.matrix.GrayMatrix;

/**
 * 
 * 
 * @author Jiri Hynek
 *
 */
public class EmphasizeSameColorsUtil {
	
	/**
	 * Takes matrix and finds the same-color areas of the minArea and minSize using flood-fill-based algorithm.
	 * 
	 * @param matrix
	 * @param minArea
	 * @param minSize
	 * @return
	 */
	public static int[][] process(int[][] matrix, int minArea, int minSize) {
		EmphasizeSameColorFloodFill floodFill = new EmphasizeSameColorFloodFill(matrix, minArea, minSize);
		int[][] resultMatrix = floodFill.process();
		
		return resultMatrix;
	}
	
	/**
	 * 
	 * @author Jiri Hynek
	 *
	 */
	public static class EmphasizeSameColorFloodFill extends BasicFloodFill {

		private int minArea;
		private int minSize;
		private int[][] resultMatrix;

		public EmphasizeSameColorFloodFill(int[][] matrix, int minArea, int minSize) {
			super(matrix, true);
			this.minArea = minArea;
			this.minSize = minSize;
			resultMatrix = GrayMatrix.newMatrix(mW, mH, GrayMatrix.WHITE);
		}
		
		@Override
		protected void postProcessSeedPixel(int x1, int y1, int x2, int y2) {
			int xx1, xx2, yy1, yy2;
	        if(area >= minArea && x2-x1 >= minSize && y2-y1 >= minSize) {
	        	// emphasize borders of area
	            boolean pixelNotFound;
	            for (int x = x1; x < x2; x++) {
	    			for (int y = y1; y < y2; y++) {
	    				if(markMatrix[x][y] == markColor) {
	    					xx1 = x-1;
	    					xx2 = x+1;
	    					yy1 = y-1;
	    					yy2 = y+1;
	    					pixelNotFound = true;
	    					for (int a = xx1; a <= xx2 && pixelNotFound; a++) {
	    						if(a >= 0 && a < mW) {
	    							for (int b = yy1; b <= yy2 && pixelNotFound; b++) {
	    								if(b >= 0 && b < mH) {
	    									if(markMatrix[a][b] != markColor) {
	    										pixelNotFound = false;
	    										resultMatrix[x][y] = GrayMatrix.BLACK;
	    									}
	    								}
	    							}
	    						}
	    					}
	    				}
	    			}
	    		}
	        }
		}
		
		@Override
		protected int[][] postProcessMatrix() {
			// return post processed matrix
			return this.resultMatrix;
		}
		
	}

}
