package cz.vutbr.fit.dashapp.segmenation.util;

import java.awt.Point;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;

import cz.vutbr.fit.dashapp.image.util.HistogramUtils;
import cz.vutbr.fit.dashapp.util.matrix.ColorMatrix;
import cz.vutbr.fit.dashapp.util.matrix.GrayMatrix;
import cz.vutbr.fit.dashapp.util.matrix.MatrixUtils;

public class FilterGradientsUtil {
	
	public static int[][] process(int[][] matrix, int filterLevel) {
		return process(matrix, filterLevel, false);
	}

	public static int[][] process(int[][] matrix, int filterLevel, boolean useColor) {
		final int mW = MatrixUtils.width(matrix);
		final int mH = MatrixUtils.height(matrix);
		int[][] workingCopy = GrayMatrix.copy(matrix);
		int[][] gradientMatrix = GrayMatrix.newMatrix(mW, mH, GrayMatrix.WHITE);
		int[][] markMatrix = GrayMatrix.newMatrix(mW, mH, GrayMatrix.WHITE);
		
		int color = -256;
		for (int x = 0; x < mW; x++) {
			for (int y = 0; y < mH; y++) {
				if(markMatrix[x][y] > 0) {
					processSeedPixel(workingCopy, gradientMatrix, x, y, color, useColor, markMatrix, filterLevel);
					color--;
				}
			}
		}
		
		return gradientMatrix;
	}
	
	private static void processSeedPixel(int[][] matrix, int[][] resultMatrix, int i, int j, int markColor, boolean useColor, int[][] markMatrix, int filterLevel) {
		int mW = MatrixUtils.width(matrix);
		int mH = MatrixUtils.height(matrix);
		
		// do flood fill algorithm
		int sum = 0, n = 0;
		int sum_r = 0, sum_g = 0, sum_b = 0;
		Queue<Point> queue = new LinkedList<Point>();
        queue.add(new Point(i, j));
        int x1 = i, x2 = i, y1 = j, y2 = j;
        int color;
        while (!queue.isEmpty()) {
            Point p = queue.remove();
        	color = matrix[p.x][p.y];
        	if(/*color >= 0 && */markMatrix[p.x][p.y] >= 0) {
        		//matrix[p.x][p.y] = markColor;
        		markMatrix[p.x][p.y] = markColor;
        		if(useColor) {
        			sum_r+=ColorMatrix.getRed(color);
        			sum_g+=ColorMatrix.getGreen(color);
        			sum_b+=ColorMatrix.getBlue(color);
        		} else {
        			sum+=color;
        		}
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
            	addPoint(matrix, queue, p.x + 1, p.y, mW, mH, color, useColor, markMatrix, filterLevel);
            	addPoint(matrix, queue, p.x - 1, p.y, mW, mH, color, useColor, markMatrix, filterLevel);
            	addPoint(matrix, queue, p.x, p.y + 1, mW, mH, color, useColor, markMatrix, filterLevel);
            	addPoint(matrix, queue, p.x, p.y - 1, mW, mH, color, useColor, markMatrix, filterLevel);
            	
            	addPoint(matrix, queue, p.x + 1, p.y + 1, mW, mH, color, useColor, markMatrix, filterLevel);
            	addPoint(matrix, queue, p.x + 1, p.y - 1, mW, mH, color, useColor, markMatrix, filterLevel);
            	addPoint(matrix, queue, p.x - 1, p.y + 1, mW, mH, color, useColor, markMatrix, filterLevel);
            	addPoint(matrix, queue, p.x - 1, p.y - 1, mW, mH, color, useColor, markMatrix, filterLevel);
        	}
        }
        
        x2++; y2++;
        
        int avgColor;
        
        if(useColor) {
        	avgColor = ColorMatrix.getRGB(sum_r/n, sum_g/n, sum_b/n);
        } else {
        	avgColor = sum/n;
        }
        
        for (int x = x1; x < x2; x++) {
			for (int y = y1; y < y2; y++) {
				if(markMatrix[x][y] == markColor) {
					resultMatrix[x][y] = avgColor;
				}
				
			}
			
		}
        
        return;
	}
	
	private static void addPoint(int[][] matrix, Queue<Point> queue, int x, int y, int mW, int mH, int refColor, boolean useColor, int[][] markMatrix, int filterLevel) {
		if ((x >= 0) && (x < mW) && (y >= 0) && (y < mH)) {
			if(markMatrix[x][y] >= 0) {
				int diff = 0;
				if(useColor) {
					int red = Math.abs(ColorMatrix.getRed(refColor)-ColorMatrix.getRed(matrix[x][y]));
					int green = Math.abs(ColorMatrix.getGreen(refColor)-ColorMatrix.getGreen(matrix[x][y]));
					int blue = Math.abs(ColorMatrix.getBlue(refColor)-ColorMatrix.getBlue(matrix[x][y]));
					diff = ColorMatrix.toGrayScaleValue(red, green, blue);
				} else {
					diff = Math.abs(refColor-matrix[x][y]);
				}
				if(diff <= filterLevel) {
					queue.add(new Point(x, y));
				}
			}
		}
	}

	public static int recommendLimit(int[][] matrix) {
		int[] histogram = HistogramUtils.getGrayscaleHistogram(matrix);
		
		int histLength = histogram.length;
		int a_limit = (int) (MatrixUtils.area(matrix)*0.01);
		int a_limit2 = (int) (MatrixUtils.area(matrix)*0.001);
		
		Map<Integer, Integer> maximums = new LinkedHashMap<>();
		
		int left, right, previous, val;
		for (int i = 0; i < histLength; i++) {
			val = histogram[i];
			
			// big enough
			if(val > a_limit) {
				// left side
				previous = i;
				left = 0;
				for (int j = i-1; j >= 0; j--) {
					if(histogram[j] < histogram[previous] && histogram[j] > histogram[previous]*0.01
							&& histogram[j] > a_limit2) {
						left++;
						previous = j;
					} else {
						if(left == 0) {
							left = -1;
						}
						break;
					}
				}
				
				// right side
				previous = i;
				right = 0;
				for (int j = i+1; j < histLength; j++) {
					if(histogram[j] < histogram[previous] && histogram[j] > histogram[previous]*0.01
							&& histogram[j] > a_limit2) {
						right++;
						previous = j;
					} else {
						if(right == 0) {
							right = -1;
						}
						break;
					}
				}
				
				if(left >= 0 && right >= 0 && (left > 0 || right > 0)) {
					maximums.put(i, Math.min(left, right));
				}
			}
		}
		
		int n = maximums.size();
		
		if(n == 0) {
			return 1;
		} else if(n == 1) {
			val = maximums.values().iterator().next();
			if(val > 0) {
				return 4;
			}
		} else {
			previous = -256;
			int maxVal = 0;
			int key, value;
			for (Entry<Integer, Integer> max : maximums.entrySet()) {
				key = max.getKey();
				value = max.getValue();
				/*if(Math.abs(max.getKey() - previous) <= 5) {
					return 1;
				}*/
				previous = key;
				if(value > maxVal) {
					maxVal = value;
				}
			}
			
			if(maxVal > 4) {
				return 4;
			} else if(maxVal > 2) {
				return 2;
			} else {
				return 1;
			}
		}
		
		return 0;
	}

}
