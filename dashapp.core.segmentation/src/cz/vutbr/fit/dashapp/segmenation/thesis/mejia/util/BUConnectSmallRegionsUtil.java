package cz.vutbr.fit.dashapp.segmenation.thesis.mejia.util;

import java.util.ArrayList;
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
 */
public class BUConnectSmallRegionsUtil {
	
	/**
	 * Method connects regions according to their closeness and size.
	 * @param vMaxLineSize 
	 * @param hMaxLineSize 
	 * @param image
	 */	
	public static void connectSmallRegions(int[][] matrix, int minRegionSize, int hMaxLineSize, int vMaxLineSize) {
		int mW = MatrixUtils.width(matrix);
		int mH = MatrixUtils.height(matrix);
		
		int[][] resultMask = null;
		int[][] previousMask = new int[mW][mH];
		
		List<Region> actRegionsList, smallRegionsList;
		
		do {
			if(resultMask != null) {
				// result mask is null in the first iteration
				MatrixUtils.copy(previousMask, resultMask);
			}
			
			actRegionsList = BUBasicUtil.getRegions(matrix);
			smallRegionsList = new ArrayList<>();
			// filter large regions
			for (Region r : actRegionsList) {
				if(r.area() <= minRegionSize) {
					smallRegionsList.add(r);
				}
			}
			
			// connect small regions with nearest black pixels if possible
			resultMask = connectSmallRegions(smallRegionsList, matrix, hMaxLineSize, vMaxLineSize);
			// copy result mask to matrix
			MatrixUtils.copyPixels(matrix, resultMask, GrayMatrix.BLACK);
			// expand connections to rectangles
			BUBasicUtil.createRectangles(matrix);
		} while (!MatrixUtils.equals(resultMask, previousMask));
	}
	
	/**
	 * Method tries to connect as many regions as possible according to the max distance limit.
	 * 
	 * Note: createRectangles needs to be called after this method.
	 * 
	 * @param regions
	 * @param matrix
	 * @param hMaxLineSize
	 * @param vMaxLineSize
	 */	
	public static int[][] connectSmallRegions(List<Region> regions, int[][] matrix, int hMaxLineSize, int vMaxLineSize) {
		int mW = MatrixUtils.width(matrix);
		int mH = MatrixUtils.height(matrix);
		
		int[][] outMatrix = GrayMatrix.newMatrix(mW, mH, GrayMatrix.WHITE);

		int finalDistance, actDistance, finalX, finalY, actPoint, direction;
		int x1, y1, x2, y2;
		for (Region r : regions) {
			finalDistance = mW > mH ? mW : mH;
			finalX = finalY = 0;
			direction = -1;
			
			// try top direction
			for (int j = r.x; j < r.x2(); j++) {
				actPoint = r.y - 1;
				// look for the nearest black pixel
				while (actPoint >= 0 && matrix[j][actPoint] != GrayMatrix.BLACK) {
					actPoint--;
				}
				
				actDistance = r.y - actPoint;
				if (actPoint >= 0 && finalDistance > actDistance) {
					// we found black pixel which is nearer that previous black pixels
					finalDistance = actDistance;
					finalX = j;
					finalY = actPoint;
					direction = Constants.Y;
				}
			}

			// try right direction
			for (int j = r.y; j < r.y2(); j++) {
				actPoint = r.x2() + 1;
				// look for the nearest black pixel
				while (actPoint < mW && matrix[actPoint][j] != GrayMatrix.BLACK) {
					actPoint++;
				}
				
				actDistance = actPoint - r.x2();
				if (actPoint < mW && finalDistance > actDistance) {
					// we found black pixel which is nearer that previous black pixels
					finalDistance = actDistance;
					finalX = actPoint;
					finalY = j;
					direction = Constants.X;
				}
			}

			// try bottom direction
			for (int j = r.x; j < r.x2(); j++) {
				actPoint = r.y2() + 1;
				// look for the nearest black pixel
				while (actPoint < mH && matrix[j][actPoint] != GrayMatrix.BLACK) {
					actPoint++;
				}
				
				actDistance = actPoint - r.y2();
				if (actPoint < mH && finalDistance > actDistance) {
					// we found black pixel which is nearer that previous black pixels
					finalDistance = actDistance;
					finalX = j;
					finalY = actPoint;
					direction = Constants.Y;
				}
			}

			// try left direction
			for (int j = r.y; j < r.y2(); j++) {
				actPoint = r.x - 1;
				// look for the nearest black pixel
				while (actPoint >= 0 && matrix[actPoint][j] != GrayMatrix.BLACK) {
					actPoint--;
				}
				
				actDistance = r.x - actPoint;
				if (actPoint >= 0 && finalDistance > actDistance) {
					// we found black pixel which is nearer that previous black pixels
					finalDistance = actDistance;
					finalX = actPoint;
					finalY = j;
					direction = Constants.X;
				}
			}

			if (finalDistance <= ((direction == Constants.X) ? hMaxLineSize * 3 : vMaxLineSize * 3)) {
				x1 = Math.min(r.x, finalX);
				y1 = Math.min(r.y, finalY);
				x2 = Math.max(r.x2(), finalX);
				y2 = Math.max(r.y2(), finalY);
				for (int i = x1; i < x2; i++) {
					for (int j = y1; j < y2; j++) {
						outMatrix[i][j] = GrayMatrix.BLACK;
					}
				}
				
				// note: createRectangles method needs to be called after finish of this method
			}
		}

		return outMatrix;
	}

}
