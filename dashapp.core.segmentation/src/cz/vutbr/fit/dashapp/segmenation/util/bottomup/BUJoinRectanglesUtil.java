package cz.vutbr.fit.dashapp.segmenation.util.bottomup;

import java.util.List;

import cz.vutbr.fit.dashapp.model.Constants;
import cz.vutbr.fit.dashapp.segmenation.util.region.Region;
import cz.vutbr.fit.dashapp.util.matrix.GrayMatrix;
import cz.vutbr.fit.dashapp.util.matrix.MatrixUtils;

/**
 * 
 * @author Santiago Mejia (algorithms)
 * @author Jiri Hynek (code refactoring)
 * 
 * FIXME: There are blocks of code with unclear meaning.
 * Even the original author is not sure about their meaning.
 *
 */
public class BUJoinRectanglesUtil {
	
	/**
	 * Method calculates max horizontal and vertical distance between joinable rectangles.
	 * @param matrix
	 * @param outArray
	 * @param w
	 * @param h
	 */	
	public static int getTreshold(int[][] matrix, int dm) {
		int maxLineSize = 0;
		
		// transpose matrix for X dimension (so the method can be universally used in both directions)
		if(dm == Constants.X) {
			matrix = MatrixUtils.transposeMatrix(matrix);
		}
		int mW = MatrixUtils.width(matrix);
		int mH =  MatrixUtils.height(matrix);
		
		// make histogram of white pixel sequence occurrence
		int j2, actDistance;
		int[] tmpLongest = new int[mH];
		boolean blackPixelFound;
		for (int i = 0; i < mW; i++) {
			blackPixelFound = false; // we are looking only for sequences between black pixels
			for (int j = 0; j < mH; j++) {
				// beginning of a sequence
				if (matrix[i][j] == GrayMatrix.WHITE && blackPixelFound) {
					j2 = j+1;
					while ((j2) < mH && matrix[i][j2] == GrayMatrix.WHITE) {
						// find end of the sequence
						j2++;
					}
					if (j2 == mH) {
						// it is not sequence between black pixels
						// go to next line
						break;
					}
					// get distance and increment histogram
					actDistance = j2-j;
					tmpLongest[actDistance] += 1;
					// we can skip the sequence
					j += actDistance;
				} else {
					blackPixelFound = true;
				}
			}
		}
		
		// FIXME: ??? (cumulative average)
		int sum = 0;
		float[] avgr = new float[mH];
		for (int i = 0; i < mH; i++) {
			sum += (tmpLongest[i]);
			avgr[i] = (float) sum / (float) (i + 1);
		}

		// FIXME: ??? (max cumulative average is found)
		int max = 0;
		for (int i = 0; i < mH; i++) {
			if (avgr[i] > avgr[max]) {
				max = i;
			}
		}
		
		// FIXME: what is it good for to use max cumulative average
		
		// minimum value is 4 (heuristic)
		maxLineSize = max < 4 ? 4 : max;

		return maxLineSize;
	}
	
	/**
	 * FIXME: this method does not make any sense at all
	 * 
	 * Method connects rectangles closer than a given limit.
	 * @param matrix
	 * @param hMaxLineSize
	 * @param vMaxLineSize
	 */	
	public static int[][] reDrawRectangles(int[][] matrix, int hMaxLineSize, int vMaxLineSize) {
		int mW = MatrixUtils.width(matrix);
		int mH =  MatrixUtils.height(matrix);
		
		int[][] resultMatrix = GrayMatrix.newMatrix(mW, mH, GrayMatrix.WHITE);
		
		// filter small regions
		List<Region> regions = BUBasicUtil.getRegions(matrix);
		for (Region r : regions) {
			MatrixUtils.drawRectangle(resultMatrix, r, GrayMatrix.BLACK, false);
		}

		// compare every region with every region
		int diff;
		for (Region r1 : regions) {
			for (Region r2 : regions) {
				// regions are distant [0,hMaxLineSize] in the first dimension
				if(r2.x > r1.x2()) {
					// r2 is located after r1
					diff = r2.x - r1.x2();
				}/* else if(r1.x > r2.x2()) {
					// r1 is located after r2
					diff = r1.x - r2.x2();
					// FIXME: this branch might be redundant since we compare all regions with all regions
				}*/ else {
					continue;
				}
				
				if (diff < (hMaxLineSize)) {
					// overlap in the second dimension
					if(r1.y <= r2.y2() && r2.y <= r1.y2()) {
						int y1 = Math.min(r1.y, r2.y);
						int y2 = Math.max(r1.y2(), r2.y2());
						MatrixUtils.drawPixels(resultMatrix, r1.x, y1, r2.x2(), y2, GrayMatrix.BLACK);
					}
				}
			}
		}

		for (Region r1 : regions) {
			for (Region r2 : regions) {
				// regions are distant [0,vMaxLineSize] in the first dimension
				if(r2.y > r1.y2()) {
					// r2 is located after r1
					diff = r2.y - r1.y2();
				}/* else if(r1.y > r2.y2()) {
					// r1 is located after r2
					diff = r1.y - r2.y2();
					// FIXME: this branch might be redundant since we compare all regions with all regions
				}*/ else {
					continue;
				}
				
				if (diff < (vMaxLineSize)) {
					// overlap in the second dimension
					if(r1.x <= r2.x2() && r2.x <= r1.x2()) {
						int x1 = Math.min(r1.x, r2.x);
						int x2 = Math.max(r1.x2(), r2.x2());
						MatrixUtils.drawPixels(resultMatrix, x1, r1.y, x2, r2.y2(), GrayMatrix.BLACK);
					}
				}

			}

		}

		// draw filtered regions
		MatrixUtils.copyPixels(resultMatrix, matrix, GrayMatrix.BLACK);

		return resultMatrix;

	}

}
