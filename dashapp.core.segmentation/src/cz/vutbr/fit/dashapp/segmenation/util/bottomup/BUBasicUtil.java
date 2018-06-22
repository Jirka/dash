package cz.vutbr.fit.dashapp.segmenation.util.bottomup;

import java.util.ArrayList;
import java.util.List;

import cz.vutbr.fit.dashapp.image.floodfill.SimpleRectangleFloodFill;
import cz.vutbr.fit.dashapp.segmenation.util.BottomUpUtil;
import cz.vutbr.fit.dashapp.segmenation.util.region.Region;
import cz.vutbr.fit.dashapp.util.matrix.GrayMatrix;
import cz.vutbr.fit.dashapp.util.matrix.MatrixUtils;

/**
 * 
 * @author Santiago Mejia (algorithms)
 * @author Jiri Hynek (code refactoring, modification of algorithms)
 *
 */
public class BUBasicUtil {
	
	/**
	 * Reuse of GrayMatrix.createRectangles(matrix, createNew) with different limitations.
	 * @param matrix
	 */
	public static int[][] createRectangles(int[][] matrix) {
		new BottomUpRectangleFloodFill(matrix, false, GrayMatrix.BLACK).process();
	
		return matrix;
	}
	
	/**
	 * Reuse of GrayMatrix.createRectangles(matrix, createNew) with different limitations.
	 * @param matrix
	 */
	public static int[][] createRectangles(int[][] matrix, boolean createNew) {
		new BottomUpRectangleFloodFill(matrix, createNew, GrayMatrix.BLACK).process();
	
		return matrix;
	}
	
	public static class BottomUpRectangleFloodFill extends SimpleRectangleFloodFill {

		public BottomUpRectangleFloodFill(int[][] matrix, boolean createNew, int refColor) {
			super(matrix, createNew, refColor);
		}
		
		@Override
		protected void postProcessSeedPixel(int x1, int y1, int x2, int y2, int markColor) {
			int dx = x2 - x1;
			int dy = y2 - y1;
			int a = (dx) * (dy);
			
			// test rectangle size
			if ((dx > mW * 0.9 && dy > mH * 0.9)) {
				/*// clear matrix
				for (int x = x1; x < x2; x++) {
					for (int y = y1; y < y2; y++) {
						if(matrix[x][y] == color) {
							matrix[x][y] = GrayMatrix.WHITE;
						}
					}
				}*/
			} else {
				if (!(dx > mW / 2 && dy < 15) && !(dx > mW / 2 && dy < 15)) {
					if (a > 7) {
						// create rectangle
						MatrixUtils.drawPixels(matrix, x1, y1, x2, y2, markColor);
					}
				}
			}
		}
	}
	
	/**
	 * Method creates a list of Regions from the matrix.
	 * @param matrix
	 * @param w
	 * @param h
	 */	
	public static List<Region> getRegions(int[][] matrix) {
		List<Region> resultRegions = new ArrayList<>();
		
		int mW = MatrixUtils.width(matrix);
		int mH = MatrixUtils.height(matrix);
		
		int[][] matrixCopy = MatrixUtils.copy(matrix);
		
		// tmp region
		Region r = new Region(0, 0, mW, mH, Region.OTHER);
		
		// go through black pixels in matrix
		for (int i = 0; i < mW; i++) {
			for (int j = 0; j < mH; j++) {
				if (matrixCopy[i][j] == GrayMatrix.BLACK) {
					r.setBounds(i, j, 0, mH);
					
					// find black rectangle in position [i,j]
					int k, l;
					for (k = r.x; (k < mW); k++) {
						if (matrixCopy[k][r.y] != GrayMatrix.BLACK) {
							break;
						}
						for (l = r.y; (l < mH); l++) {
							if (matrixCopy[k][l] != GrayMatrix.BLACK) {
								break;
							}
						}
						r.height = (l - r.y < r.height ? l - r.y : r.height);
					}
					r.width = k - r.x;
					
					// filter small rectangles
					if ((r.width > 3 && r.height > 3)) {
						// add rectangle to list and mark pixels
						resultRegions.add(new Region(r.x, r.y, r.width, r.height, 0));
						MatrixUtils.drawRectangle(matrixCopy, r, GrayMatrix.WHITE, false);
					}
				}
			}
		}
		return resultRegions;
	}
	
	/**
	 * Method predicts the minimum rectangle size for final regions.
	 * @param regions
	 */	
	public static int getPreferredMinRegionSize(List<Region> regions) {
		int result = 0;
		// calculate average area * 0.6
		int size = regions.size();
		if(size > 0) {
			int a = 0;
			for (Region r : regions) {
				a += r.area();
			}
			result = (int) ((a/size) * 0.6);
		}
		return result;
	}
	
	/**
	 * 
	 * @param result1
	 * @param result2
	 * @return
	 */
	public static int[][] combineResults(int[][] result1, int[][] result2) {
		int mW = MatrixUtils.width(result1);
		int mH = MatrixUtils.height(result1);
		
		float avgTest1 = BottomUpUtil.averageMatrix(result1, mW, mH);
		float avgTest2 = BottomUpUtil.averageMatrix(result2, mW, mH);
		
		int[][] finalCombination = GrayMatrix.newMatrix(mW, mH, GrayMatrix.WHITE);
		for (int i = 0; i < mW; i++) {
			for (int j = 0; j < mH; j++) {
				if ((avgTest1 <= 0.95 && result1[i][j] == 0) || (avgTest2 < 0.95 && result2[i][j] == 0)) {
					finalCombination[i][j] = 0;
				}
			}
		}
		
		// rectangles
		BUBasicUtil.createRectangles(finalCombination);
		BUBasicUtil.createRectangles(finalCombination);
		
		return finalCombination;
	}
	
	/**
	 * Method compare two matrices if they are different.
	 * 
	 * @param matrix1
	 * @param matrix2
	 * @param w
	 * @param h
	 */	
	public static boolean areEqualMatrices(int[][] matrix1, int[][] matrix2) {
		int mW = MatrixUtils.width(matrix1);
		int mH = MatrixUtils.height(matrix1);
		
		if(mW == MatrixUtils.width(matrix2) && mH == MatrixUtils.height(matrix2)) {
			for (int i = 0; i < mW; i++) {
				for (int j = 0; j < mH; j++) {
					if (matrix1[i][j] != matrix2[i][j]) {
						return false;
					}
				}
			}
			return true;
		}
		return false;
	}

}
