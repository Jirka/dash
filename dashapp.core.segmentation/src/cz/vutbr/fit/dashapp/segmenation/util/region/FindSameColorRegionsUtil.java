package cz.vutbr.fit.dashapp.segmenation.util.region;

import java.awt.Point;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import cz.vutbr.fit.dashapp.image.util.HistogramUtils;
import cz.vutbr.fit.dashapp.util.MathUtils;
import cz.vutbr.fit.dashapp.util.matrix.GrayMatrix;
import cz.vutbr.fit.dashapp.util.matrix.MatrixUtils;

public class FindSameColorRegionsUtil {
	
	/**
	 * 
	 * @param matrix
	 * @return
	 */
	public static List<Region> findRegions(int[][] matrix) {
		// get used values
		List<Integer> usedValues = HistogramUtils.getUsedValues(matrix);
		List<Region> regions = new ArrayList<>();
		for (Integer usedValue : usedValues) {
			List<Region> actRegions = findRegions(matrix, usedValue);
			regions.addAll(actRegions);
		}
		
		return regions;
	}

	/**
	 * 
	 * @param matrix
	 * @param color
	 * @return
	 */
	private static List<Region> findRegions(int[][] matrix, int color) {
		final int mW = MatrixUtils.width(matrix);
		final int mH = MatrixUtils.height(matrix);
		
		// working copy
		int[][] workingCopy = MatrixUtils.copy(matrix);
		int[][] edgesMatrix = GrayMatrix.inverse(GrayMatrix.edges(matrix), false);
		
		// debug
		//int[][] workingCopy = GrayMatrix.newMatrix(mW, mH, GrayMatrix.WHITE);
		//GrayMatrix.copyPixels(workingCopy, matrix, color, GrayMatrix.BLACK);
		//debug("debug", GrayMatrix.printMatrixToImage(null, workingCopy));
		
		int markColor = -1;
		List<Region> regions = new ArrayList<>();
		for (int x = 0; x < mW; x++) {
			for (int y = 0; y < mH; y++) {
				if(workingCopy[x][y] == color) {
					Region region = processSeedPixel(workingCopy, x, y, color, markColor);
					
					// analyze region
					analyzeRegion(region, workingCopy, edgesMatrix, markColor);
					
					if((region.width > 2 && region.height > 2 && color == GrayMatrix.BLACK) ||
							region.width > 9 && region.height > 9 && color != GrayMatrix.BLACK) {
						//System.out.println(region);
						regions.add(region);
					}
					markColor--;
				}
			}
		}
		return regions;
	}

	/**
	 * 
	 * @param matrix
	 * @param i
	 * @param j
	 * @param color
	 * @param markColor
	 * @return
	 */
	private static Region processSeedPixel(int[][] matrix, int i, int j, int color, int markColor) {
		int mW = MatrixUtils.width(matrix);
		int mH = MatrixUtils.height(matrix);
		
		// do flood fill algorithm
		Queue<Point> queue = new LinkedList<Point>();
        queue.add(new Point(i, j));
        int x1 = i, x2 = i, y1 = j, y2 = j;
        while (!queue.isEmpty()) {
            Point p = queue.remove();
            if ((p.x >= 0) && (p.x < mW) && (p.y >= 0) && (p.y < mH)) {
                if (matrix[p.x][p.y] == color) {
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
                    queue.add(new Point(p.x + 1, p.y));
                    queue.add(new Point(p.x - 1, p.y));
                    queue.add(new Point(p.x, p.y + 1));
                    queue.add(new Point(p.x, p.y - 1));
                    
                    queue.add(new Point(p.x + 1, p.y + 1));
                    queue.add(new Point(p.x + 1, p.y - 1));
                    queue.add(new Point(p.x - 1, p.y + 1));
                    queue.add(new Point(p.x - 1, p.y - 1));
                }
            }
        }
        
        x2++; y2++;
        
        return new Region(x1, y1, x2-x1, y2-y1, color);
	}
	
	/**
	 * 
	 * @param r
	 * @param matrix
	 * @param edgesMatrix
	 * @param markColor
	 */
	private static void analyzeRegion(Region r, int[][] matrix, int[][] edgesMatrix, int markColor) {
		int mW = MatrixUtils.width(matrix);
		int mH = MatrixUtils.height(matrix);
		int mA = mW*mH;
		
		int x1 = MathUtils.roundInRange(r.x, 0, mW);
		int x2 = MathUtils.roundInRange(r.x + r.width, 0, mW);
		int y1 = MathUtils.roundInRange(r.y, 0, mH);
		int y2 = MathUtils.roundInRange(r.y + r.height, 0, mH);
		int rA = r.width*r.height;
		int rO = 2*(r.width+r.height);
		
		int x2_last = x2-1;
		int y2_last = y2-1;
		
		int realArea = 0;
		int edgesCount = 0;
		
		// copy region to the special matrix representing borders (black) and area (mark color)
		//int[][] regionMatrix = GrayMatrix.newMatrix(mW, mH, GrayMatrix.WHITE);
		//boolean edgeFound;
		
		for (int x = x1; x < x2; x++) {
			for (int y = y1; y < y2; y++) {
				
				// real area increment
				if(matrix[x][y] == markColor) {
					realArea++;
					
					//regionMatrix[x][y] = markColor;
					
					// edges
					/*edgeFound = false;
					for (int i = x-1; i <= x+1 && !edgeFound; i++) {
						if(i >= 0 && i < mW) {
							for (int j = y-1; j <= y+1 && !edgeFound; j++) {
								// it is border pixel
								if(j >= 0 && j < mH) {
									if(matrix[i][j] != markColor) {
										edgeFound = true;
									}
								} else {
									edgeFound = true;
								}
							}
						} else {
							edgeFound = true;
						}
					}
					
					if(edgeFound) {
						regionMatrix[x][y] = GrayMatrix.BLACK;
					}*/
					
					// border edges
					if(x == x1 || x == x2_last || y == y1 || y == y2_last) {
						edgesCount++;
					}
				}
			}
		}
		
		// test left region border
		// TODO add heuristics for alignment of non-rectangle objects
		/*int borderCount = 0;
		int limit = mH/4;
		int x;
		for (x = x1; x < x2 && borderCount < limit; x++) {
			borderCount = 0;
			for(int y = y1; y < y2; y++) {
				if(matrix[x][y] == markColor) {
					borderCount++;
				}
			}	
		}*/		
		
		double usedArea = (double) realArea/rA;
		double usedEdges = (double) edgesCount/rO;
		
		// debug
		// System.out.println(r);
		// System.out.println("used area: " + (double) realArea/rA);
		// System.out.println("used edges: " + (double) edgesCount/rO);
		
		// heuristics to split and categorize regions
		if((double) rA/mA < 0.01) {
			// small data rectangles
			r.type = Region.DATA;
		} else {
			if(usedEdges >= 0.7) {
				if(usedArea > 0.5) {
					// fill rectangle
					r.type = Region.R_FILL;
				} else if(usedArea > 0.1) {
					// medium rectangle
					r.type = Region.R_MEDIUM;
				} else {
					// border rectangle
					r.type = Region.R_BORDER;
				}
			} else {
				//if((double) rA/mA > 0.01) {
					// larger ambiguous rectangles
					r.type = Region.AMBIGUOUS;
					// TODO possible split
				//} else {
				//	// small data rectangles
				//	r.type = Region.DATA;
				//}
			}
		}
	}

}
