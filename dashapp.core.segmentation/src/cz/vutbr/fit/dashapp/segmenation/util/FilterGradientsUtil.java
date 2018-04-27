package cz.vutbr.fit.dashapp.segmenation.util;

import java.awt.Point;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
		if(filterLevel <= 0) {
			return matrix;
		}
		final int mW = MatrixUtils.width(matrix);
		final int mH = MatrixUtils.height(matrix);
		int[][] workingCopy = GrayMatrix.copy(matrix);
		int[][] nonGradientMatrix = GrayMatrix.newMatrix(mW, mH, GrayMatrix.WHITE);
		int[][] markMatrix = GrayMatrix.newMatrix(mW, mH, GrayMatrix.WHITE);
		
		int color = -256;
		for (int x = 0; x < mW; x++) {
			for (int y = 0; y < mH; y++) {
				if(markMatrix[x][y] > 0) {
					processSeedPixel(workingCopy, nonGradientMatrix, x, y, color, useColor, markMatrix, filterLevel);
					color--;
				}
			}
		}
		
		return nonGradientMatrix;
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
	
	public static int recommendGradientLimitCombined(int[][] matrix) {
		// init
		int filterLevel = 0;
		int[][] nonGradientMatrix = process(matrix, filterLevel);
		int[] histogram = HistogramUtils.getGrayscaleHistogram(matrix);
		int max = HistogramUtils.findMax(histogram, -1);
		HistogramPoint[] histogramPoints = getHistogramPoints(histogram, max);
		List<HistogramRange> ranges = HistogramRange.getRanges(histogramPoints);		
		
		while(filterLevel <= 4 && ranges != null) {
			
			
			// increment variables
			filterLevel++;
		};
		
		return filterLevel;
	}
	
	public static final int OK = 0;
	public static final int STOP = 1;
	public static final int WARNING = 2;
	
	public static int recommendGradientLimitIterative(int[][] matrix) {
		
		// init variables
		final int filterLevelLimit = 4;
		int filterLevel = 0;
		
		int[] histogram;
		int max;
		HistogramPoint[] histogramPoints;
		List<HistogramRange> ranges;
		int isAllowed = OK;
		int a = MatrixUtils.area(matrix);
		
		int amounts[] = new int[] { 0, 0, 0, 0, 0 };
		int weights[] = new int[] { 0, 0, 0, 0, 0 };
		int recommend[] = new int[] { OK, OK, OK, OK, OK };
		double maxShares[] = new double[] { 0, 0, 0, 0, 0 };
		
		do {
			histogram = HistogramUtils.getGrayscaleHistogram(process(matrix, filterLevel)); // histogram
			max = HistogramUtils.findMax(histogram, -1); // maximal value
			histogramPoints = getHistogramPoints(histogram, max); // histogram important points
			ranges = HistogramRange.getRanges(histogramPoints);
			
			amounts[filterLevel] = totalAmount(ranges);
			weights[filterLevel] = totalWeight(ranges);
			maxShares[filterLevel] = (double) max/a;
			
			// is recommended to continue (close max points)
			isAllowed = isRecommendedToContiue(histogram, histogramPoints, ranges, filterLevel, a);
			recommend[filterLevel] = isAllowed;
			if(filterLevel > 0 && recommend[filterLevel-1] == WARNING) {
				if(maxShares[filterLevel] >= maxShares[filterLevel-1]*1.5) {
					isAllowed = STOP;
					filterLevel--;
				}
			}
			
			// next iteration
			filterLevel++;
		} while(!ranges.isEmpty() && filterLevel <= filterLevelLimit && isAllowed == OK);
		
		filterLevel--; // last filter level
		int lastFilterLevel = filterLevel;
		
		System.out.println("amounts " + Arrays.toString(amounts));
		System.out.println("weights " + Arrays.toString(weights));
		
		for (int i = 0; i < filterLevelLimit+1 && i <= lastFilterLevel; i++) {
			
			if(weights[i] <= 6) {
				filterLevel = i;
				break;
			}
			
			if(i < filterLevelLimit && (weights[i]-weights[i+1] < 0 || amounts[i]-amounts[i+1] < 0)) {
				filterLevel = i;
				break;
			}
			
			//if(i == filterLevelLimit) {
				filterLevel = i;
			//}
		}
		
		if(filterLevel == 0) {
			if(weights[0] > 4 && amounts[0]-amounts[1] >= 0) {
				filterLevel = 1;
			}
		}
		
		return filterLevel;
		
	}
	
	private static int isRecommendedToContiue(int[] histogram, HistogramPoint[] histogramPoints, List<HistogramRange> ranges, int filterLevel, int a) {
		histogramPoints = Arrays.copyOf(histogramPoints, histogramPoints.length);
		Arrays.sort(histogramPoints);
		HistogramPoint maxPoint = histogramPoints[histogramPoints.length-1];
		double maxPointShare = (double)maxPoint.value()/a;
		double maxValueLimitShare = Math.max(maxPointShare*0.2, 0.08);
		System.out.println("maxPointShare: " + maxPointShare + ", maxValueLimitShare: " + maxValueLimitShare);
		double maxValueLimit = maxValueLimitShare*a;
		int minDistance = 256;
		for (int i = histogramPoints.length-1; i >= 0; i--) {
			//if(histogramPoints[i].type == HistogramPoint.SMALL) {
			if(histogramPoints[i].value() < maxValueLimit) {
				break;
			}
			
			if(histogramPoints[i].isLocalMax()) {
				for (int j = 0; j < histogramPoints.length; j++) {
					if(i != j && histogramPoints[j].isLocalMax()
							&& histogramPoints[j].value() > maxValueLimit
							//&& histogramPoints[j].type != HistogramPoint.SMALL
					) {
						int distance = Math.abs(histogramPoints[i].i - histogramPoints[j].i);
						if(distance < minDistance) {
							boolean areInSameRange = areInSameRange(ranges, histogramPoints[i], histogramPoints[j]);
							int minGapI = minGap(histogram, histogramPoints[i], histogramPoints[j]);
							double minGapShare = (double) histogram[minGapI]/Math.max(histogramPoints[i].value(), histogramPoints[i].value());
							
							if(minGapShare >= 0.7) {
								// OK
							} else if(minGapShare <= 0.2) {
								if(distance <= 4) {
									System.out.println("distance!");
									return STOP;
								} else if(distance <= 10) {
									System.out.println("warning");
									return WARNING;
								}
							} else {
								/*if(distance <= 10 && areInSameRange) {
									System.out.println("distance <= 10");
									return false;
								}*/
							}
							
							//minDistance = distance;
							/*if(distance <= 4 ||
									(distance <= 10 && areInSameRange)) {
								System.out.println("distance!");
								return false;
							} else if(distance <= 10) {
								System.out.println("distance <= 10");
								if(filterLevel > 1) {
									return false;
								}
							}*/
						}
					}
				}
			}
		}
		return OK;
		
	}

	private static int minGap(int[] histogram, HistogramPoint p1, HistogramPoint p2) {
		int i1 = Math.min(p1.i, p2.i);
		int i2 = Math.max(p1.i, p2.i);
		int min = p1.i;
		for (int i = i1; i < i2; i++) {
			if(histogram[i] < histogram[min]) {
				min = i;
			}
		}
		return min;
	}

	private static int totalAmount(List<HistogramRange> ranges) {
		int sum = 0;
		for (HistogramRange range : ranges) {
			sum += range.size();
		}
		return sum;
	}
	
	private static int totalWeight(List<HistogramRange> ranges) {
		int sum = 0;
		for (HistogramRange range : ranges) {
			sum += range.allPointsWeight();
		}
		return sum;
	}

	private static HistogramRange getMostImportantRange(List<HistogramRange> ranges) {
		if(ranges.isEmpty()) {
			return null;
		}
		HistogramRange maxRange = ranges.get(0);
		int maxWeight = maxRange.maxPointWeight();
		int weight;
		for (HistogramRange range : ranges) {
			weight = range.maxPointWeight();
			if(range.maxPointWeight() > maxWeight) {
				maxRange = range;
				maxWeight = weight;
			}
		}
		return maxRange;
	}

	public static int recommendGradientLimit(int[][] matrix) {
		int[] histogram = HistogramUtils.getGrayscaleHistogram(matrix);
		
		// analyze max value
		int max = HistogramUtils.findMax(histogram, -1);
		int a = MatrixUtils.area(matrix);
		double maxValueShare = (double) histogram[max]/a;
		
		System.out.println("max: " + max + " " + (double) histogram[max]/a);
		
		if(maxValueShare < 0.2) {
			// a lot of small values - gradients expected
			System.out.println("gradient 4: small values");
			return 4;
		}
		
		// analyze important histogram points
		HistogramPoint[] histogramPoints = getHistogramPoints(histogram, max);
		System.out.println(Arrays.toString(histogramPoints));
		
		// analyze ranges of important points
		List<HistogramRange> ranges = HistogramRange.getRanges(histogramPoints);
		if(!ranges.isEmpty()) {
			Collections.sort(ranges);
			Collections.reverse(ranges);
			HistogramRange biggestRange = ranges.get(0);
			if(biggestRange.size() <= 3) {
				return 2;
			}
			
			Arrays.sort(histogramPoints);
			HistogramPoint maxPoint = histogramPoints[histogramPoints.length-1];
			double maxValueLimit = maxPoint.value()*0.2;
			
			int minDistance = 256;
			for (int i = histogramPoints.length-1; i >= 0; i--) {
				//if(histogramPoints[i].type == HistogramPoint.SMALL) {
				if(histogramPoints[i].value() < maxValueLimit) {
					break;
				}
				
				if(histogramPoints[i].isLocalMax()) {
					for (int j = 0; j < histogramPoints.length; j++) {
						if(i != j && histogramPoints[j].isLocalMax()
								&& histogramPoints[j].value() > maxValueLimit
								//&& histogramPoints[j].type != HistogramPoint.SMALL
						) {
							int distance = Math.abs(histogramPoints[i].i - histogramPoints[j].i);
							if(distance < minDistance) {
								//minDistance = distance;
								if(distance <= 4 ||
										(distance <= 10 && areInSameRange(ranges, histogramPoints[i], histogramPoints[j]))) {
									System.out.println("distance!");
									return 0;
								} else if(distance <= 10) {
									System.out.println("distance <= 10");
									return 2;
								}
							}
						}
					}
				}
			}
			
			return 4;
			
			/*int returnValue = 4;
			int pointsSize = histogramPoints.length;
			int start, end = 0;
			int minDistance = 256;
			for (HistogramRange range : ranges) {
				//double[] diffVector = range.getDiffVector();
				HistogramPoint[] points = range.points;
				Arrays.sort(points);
				
				//maxValueShare = points[points.length-1].value();
				double valueLimit = points[points.length-1].value()*0.5;
				for (int i = points.length-1; i >= 0; i--) {
					if(points[i].value() < valueLimit) {
						break;
					}
					
					for (int j = 0; j < points.length; j++) {
						if(i != j && points[j].isLocalMax()) {
							int distance = Math.abs(points[j].i - points[j].i);
							if(distance < minDistance) {
								minDistance = distance;
								if(minDistance < 6) {
									return 
								}
							}
						}
					}
					
				}
				
				
				// analyze range
				System.out.println("range: " + range);
				if(range.size() <= 3) {
					break;
				}
			}
			return returnValue;*/
		}
		
		
		//if(maxValueShare > 0.35) {
			// one very dominant color
			//return 4;
		//}
		
		return 1;
	}

	private static boolean areInSameRange(List<HistogramRange> ranges, HistogramPoint histogramPoint, HistogramPoint histogramPoint2) {
		for (HistogramRange range : ranges) {
			if(range.containsBoth(histogramPoint, histogramPoint2)) {
				return true;
			}
		}
		return false;
	}

	private static HistogramPoint[] getHistogramPoints(int[] histogram, int max) {
		List<HistogramPoint> histogramPoints = new ArrayList<>();
		
		double maxValue = histogram[max];
		double share;
		//DecimalFormat df = new DecimalFormat("#.0000");
		for (int i = 0; i < histogram.length; i++) {
			//System.out.println(i + " " + histogram[i] + " " + df.format((double) histogram[i]/a));
			share = histogram[i]/maxValue;
			if(share > 0.1) {
				histogramPoints.add(new HistogramPoint(histogram, i, HistogramPoint.BIG));
			} else if(share > 0.04) {
				histogramPoints.add(new HistogramPoint(histogram, i, HistogramPoint.MEDIUM));
			} else if(share > 0.015) {
				histogramPoints.add(new HistogramPoint(histogram, i, HistogramPoint.SMALL));
			}
		}
		
		return histogramPoints.toArray(new HistogramPoint[histogramPoints.size()]);
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
