package cz.vutbr.fit.dashapp.segmenation.util.image;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import cz.vutbr.fit.dashapp.image.floodfill.BasicFloodFill;
import cz.vutbr.fit.dashapp.image.util.HistogramUtils;
import cz.vutbr.fit.dashapp.util.matrix.ColorMatrix;
import cz.vutbr.fit.dashapp.util.matrix.GrayMatrix;
import cz.vutbr.fit.dashapp.util.matrix.MatrixUtils;

/**
 * 
 * @author Jiri Hynek
 *
 */
public class FilterGradientsUtil {
	
	/**
	 * Takes matrix and finds areas containing color gradient of specified filter level using flood-fill-based algorithm.
	 * It converts color gradients into average color of the area.
	 * 
	 * @param matrix
	 * @param gradientThreshold
	 * @return
	 */
	public static int[][] process(int[][] matrix, int gradientThreshold) {
		return process(matrix, gradientThreshold, false);
	}

	/**
	 * Takes matrix and finds areas containing color gradient of specified filter level using flood-fill-based algorithm.
	 * It converts color gradients into average color of the area.
	 * It considers RGB color instead of direct values.
	 * 
	 * @param matrix
	 * @param gradientThreshold
	 * @param considerRGBColor
	 * @return
	 */
	public static int[][] process(int[][] matrix, int gradientThreshold, boolean considerRGBColor) {
		if(gradientThreshold <= 0) {
			return matrix;
		}
		FilterGradientFloodFill floodFill = new FilterGradientFloodFill(matrix, gradientThreshold, considerRGBColor);
		int[][] resultMatrix = floodFill.process();
		
		return resultMatrix;
	}
	
	/**
	 * 
	 * @author Jiri Hynek
	 *
	 */
	public static class FilterGradientFloodFill extends BasicFloodFill {

		private int gradientThreshold;
		private int sum_r;
		private int sum_g;
		private int sum_b;
		private int sum_gray;
		private boolean considerRGBColor;
		private int[][] resultMatrix;

		public FilterGradientFloodFill(int[][] matrix, int gradientThreshold, boolean considerRGBColor) {
			super(matrix, true);
			this.gradientThreshold = gradientThreshold;
			this.considerRGBColor = considerRGBColor;
			resultMatrix = GrayMatrix.newMatrix(mW, mH, GrayMatrix.WHITE);
		}
		
		@Override
		protected void postProcessPixel(int x, int y, int actColor) {
			if(considerRGBColor) {
    			sum_r += ColorMatrix.getRed(actColor);
    			sum_g += ColorMatrix.getGreen(actColor);
    			sum_b += ColorMatrix.getBlue(actColor);
    		} else {
    			sum_gray += actColor;
    		}
		}
		
		@Override
		protected void preprocessSeedPixel() {
			sum_r = sum_g = sum_b = sum_gray = 0;
		}
		
		@Override
		protected void postProcessSeedPixel(int x1, int y1, int x2, int y2) {
			// get average color
			int avgColor;
	        if(considerRGBColor) {
	        	avgColor = ColorMatrix.getRGB(sum_r/area, sum_g/area, sum_b/area);
	        } else {
	        	avgColor = sum_gray/area;
	        }
	        
	        // update flood-filled area
	        for (int x = x1; x < x2; x++) {
				for (int y = y1; y < y2; y++) {
					if(markMatrix[x][y] == markColor) {
						resultMatrix[x][y] = avgColor;
					}
				}
			}
		}
		
		@Override
		protected boolean matches(int x, int y, int actColor) {
			// measure difference between neighboring pixels 
			int diff = 0;
			if(considerRGBColor) {
				int red = Math.abs(ColorMatrix.getRed(actColor)-ColorMatrix.getRed(matrix[x][y]));
				int green = Math.abs(ColorMatrix.getGreen(actColor)-ColorMatrix.getGreen(matrix[x][y]));
				int blue = Math.abs(ColorMatrix.getBlue(actColor)-ColorMatrix.getBlue(matrix[x][y]));
				diff = ColorMatrix.toGrayScaleValue(red, green, blue);
			} else {
				diff = Math.abs(actColor-matrix[x][y]);
			}
			
			
			// test gradient threshold
			return diff <= gradientThreshold;
		}
		
		@Override
		protected int[][] postProcessMatrix() {
			return resultMatrix;
		}
		
	}
	
	public static final int RECOMMEND_OK = 0;
	public static final int RECOMMEND_STOP = 1;
	public static final int RECOMMEND_WARNING = 2;
	
	/**
	 * Method recommends gradient threshold using iterative increase of threshold and analysis of image histogram.
	 * 
	 * @param matrix
	 * @return
	 */
	public static int recommendGradientThreshlodIterative(int[][] matrix) {
		
		// init variables
		final int gradientThresholdLimit = 4;
		int gradientThreshold = 0;
		
		int[] histogram;
		int mostFrequentColor;
		HistogramBar[] histogramBars;
		List<HistogramRange> histogramRanges;
		int isAllowedToIncrement = RECOMMEND_OK;
		int a = MatrixUtils.area(matrix);
		
		int histRangesSizes[] = new int[] { 0, 0, 0, 0, 0 };
		int histBarsWeights[] = new int[] { 0, 0, 0, 0, 0 };
		int recommendations[] = new int[] { RECOMMEND_OK, RECOMMEND_OK, RECOMMEND_OK, RECOMMEND_OK, RECOMMEND_OK };
		double mostFrequentColorShares[] = new double[] { 0, 0, 0, 0, 0 };
		
		// iteratively increase the threshold and analyse histogram
		do {
			histogram = HistogramUtils.getGrayscaleHistogram(process(matrix, gradientThreshold)); // histogram
			mostFrequentColor = HistogramUtils.findMax(histogram, -1); // maximal value
			histogramBars = getHistogramBars(histogram, mostFrequentColor); // histogram important bars
			histogramRanges = HistogramRange.getRanges(histogramBars);
			
			histRangesSizes[gradientThreshold] = rangesSize(histogramRanges);
			histBarsWeights[gradientThreshold] = barsWeight(histogramRanges);
			mostFrequentColorShares[gradientThreshold] = (double) mostFrequentColor/a;
			
			// is recommended to continue (close max bars)
			isAllowedToIncrement = isRecommendedToContiue(histogram, histogramBars, histogramRanges, gradientThreshold, a);
			recommendations[gradientThreshold] = isAllowedToIncrement;
			if(gradientThreshold > 0 && recommendations[gradientThreshold-1] == RECOMMEND_WARNING) {
				if(mostFrequentColorShares[gradientThreshold] >= mostFrequentColorShares[gradientThreshold-1]*1.5) {
					isAllowedToIncrement = RECOMMEND_STOP;
					gradientThreshold--;
				}
			}
			
			// next iteration
			gradientThreshold++;
		} while(!histogramRanges.isEmpty() && gradientThreshold <= gradientThresholdLimit && isAllowedToIncrement == RECOMMEND_OK);
		
		gradientThreshold--; // last filter level
		int lastFilterLevel = gradientThreshold;
		
		System.out.println("segmentation: gradient: histRangesSizes " + Arrays.toString(histRangesSizes));
		System.out.println("segmentation: gradient: histBarsWeights " + Arrays.toString(histBarsWeights));
		
		// select gradient threshold based on results of histogram analysis. 
		for (int i = 0; i < gradientThresholdLimit+1 && i <= lastFilterLevel; i++) {
			if(histBarsWeights[i] <= 6) {
				// low weights of bars (image contains negligible color gradient)
				gradientThreshold = i;
				break;
			}
			
			if(i < gradientThresholdLimit && (histBarsWeights[i]-histBarsWeights[i+1] < 0 || histRangesSizes[i]-histRangesSizes[i+1] < 0)) {
				// low change between bar weights (low effect)
				gradientThreshold = i;
				break;
			}
			
			gradientThreshold = i;
		}
		
		if(gradientThreshold == 0) {
			// heuristics: it is usually better to use threshold=1 than threshold=0
			if(histBarsWeights[0] > 4 && histRangesSizes[0]-histRangesSizes[1] >= 0) {
				gradientThreshold = 1;
			}
		}
		
		return gradientThreshold;
		
	}
	
	/**
	 * Method analyzes image histogram and create recommendation for iterative increase of threshold.
	 * 
	 * There are images which contain two dominant colors which are very similar.
	 * It is important to keep these colors since they might represent different important color layers.
	 * 
	 * @param histogram
	 * @param histogramBars
	 * @param ranges
	 * @param filterLevel
	 * @param a
	 * @return
	 */
	private static int isRecommendedToContiue(int[] histogram, HistogramBar[] histogramBars, List<HistogramRange> ranges, int filterLevel, int a) {
		// sort bars according to their share
		histogramBars = Arrays.copyOf(histogramBars, histogramBars.length);
		Arrays.sort(histogramBars);
		// get max bar and establish max bar value limit 
		// we focus only on dominant colors
		HistogramBar maxBar = histogramBars[histogramBars.length-1];
		double maxBarShare = (double)maxBar.value()/a;
		double maxValueLimitShare = Math.max(maxBarShare*0.2, 0.08);
		double maxValueLimit = maxValueLimitShare*a;
		System.out.println("segmentation: maxBarShare: " + maxBarShare + ", maxValueLimitShare: " + maxValueLimitShare);
		int minDistance = 256;
		
		// go through all bars and compare the distance between the bar and remaining bars
		// if the distance is low, it is possible that these bars could be joined by higher gradient threshold
		for (int i = histogramBars.length-1; i >= 0; i--) {
			//if(histogramBars[i].type == HistogramBar.SMALL) {
			if(histogramBars[i].value() < maxValueLimit) {
				// small bar -> not important to analyze the remaining bars
				break;
			}
			
			// bar should represent local maximum in histogram
			if(histogramBars[i].isLocalMax()) {
				for (int j = 0; j < histogramBars.length; j++) {
					if(i != j && histogramBars[j].isLocalMax()
							&& histogramBars[j].value() > maxValueLimit
							//&& histogramBar[j].type != HistogramBar.SMALL
					) {
						// measure distance between bars and analyze it if it is lower than min distance
						int distance = Math.abs(histogramBars[i].i - histogramBars[j].i);
						if(distance < minDistance) {
							// find then lowest bar between the analyzed bars
							int lowestBarInDistance_Index = minGapBetweenBars(histogram, histogramBars[i], histogramBars[j]);
							double lowestBarInDistance_ValueShare = (double) histogram[lowestBarInDistance_Index]/Math.max(histogramBars[i].value(), histogramBars[i].value());
							
							if(lowestBarInDistance_ValueShare >= 0.7) {
								// OK
								// the two bars are connected with range of frequently used colors
							} else if(lowestBarInDistance_ValueShare <= 0.2) {
								// there is big gap between the two bars
								if(distance <= 4) {
									// very low distance between important bars - stop
									System.out.println("segmentation: gradient: distance!");
									return RECOMMEND_STOP;
								} else if(distance <= 10) {
									// low distance between important bars - warning
									System.out.println("segmentation: gradient: warning");
									return RECOMMEND_WARNING;
								}
							} else {
								// OK
								// TODO improve heuristics for medium size bar gaps
								// experiments
								/*if(distance <= 10 && areInSameRange) {
									System.out.println("distance <= 10");
									return false;
								}*/
							}
							
							// OLD heuristics, see recommendGradientThresholdExperimental
							//boolean areInSameRange = areInSameRange(ranges, histogramBars[i], histogramBars[j]);
							//minDistance = distance;
							/*if(distance <= 4 || (distance <= 10 && areInSameRange)) {
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
		return RECOMMEND_OK;
		
	}

	/**
	 * Returns index of the smallest bar between two bars 
	 * 
	 * @param histogram
	 * @param b1
	 * @param b2
	 * @return
	 */
	private static int minGapBetweenBars(int[] histogram, HistogramBar b1, HistogramBar b2) {
		int i1 = Math.min(b1.i, b2.i);
		int i2 = Math.max(b1.i, b2.i);
		int min = b1.i;
		for (int i = i1; i < i2; i++) {
			if(histogram[i] < histogram[min]) {
				min = i;
			}
		}
		return min;
	}

	/**
	 * Returns sum of range sizes.
	 * 
	 * @param ranges
	 * @return
	 */
	private static int rangesSize(List<HistogramRange> ranges) {
		int sum = 0;
		for (HistogramRange range : ranges) {
			sum += range.size();
		}
		return sum;
	}
	
	/**
	 * Returns sum of bar weights.
	 * 
	 * @param ranges
	 * @return
	 */
	private static int barsWeight(List<HistogramRange> ranges) {
		int sum = 0;
		for (HistogramRange range : ranges) {
			sum += range.allBarsWeight();
		}
		return sum;
	}

	@SuppressWarnings("unused")
	private static HistogramRange getMostImportantRange(List<HistogramRange> ranges) {
		if(ranges.isEmpty()) {
			return null;
		}
		HistogramRange maxRange = ranges.get(0);
		int maxWeight = maxRange.maxBarWeight();
		int weight;
		for (HistogramRange range : ranges) {
			weight = range.maxBarWeight();
			if(range.maxBarWeight() > maxWeight) {
				maxRange = range;
				maxWeight = weight;
			}
		}
		return maxRange;
	}

	/**
	 * Experimental method gradient threshold recommendation.
	 * 
	 * @param matrix
	 * @return
	 */
	public static int recommendGradientThresholdExperimental(int[][] matrix) {
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
		
		// analyze important histogram bars
		HistogramBar[] histogramBars = getHistogramBars(histogram, max);
		System.out.println(Arrays.toString(histogramBars));
		
		// analyze ranges of important bars
		List<HistogramRange> ranges = HistogramRange.getRanges(histogramBars);
		if(!ranges.isEmpty()) {
			Collections.sort(ranges);
			Collections.reverse(ranges);
			HistogramRange biggestRange = ranges.get(0);
			if(biggestRange.size() <= 3) {
				return 2;
			}
			
			Arrays.sort(histogramBars);
			HistogramBar maxBar = histogramBars[histogramBars.length-1];
			double maxValueLimit = maxBar.value()*0.2;
			
			int minDistance = 256;
			for (int i = histogramBars.length-1; i >= 0; i--) {
				//if(histogramBars[i].type == HistogramBar.SMALL) {
				if(histogramBars[i].value() < maxValueLimit) {
					break;
				}
				
				if(histogramBars[i].isLocalMax()) {
					for (int j = 0; j < histogramBars.length; j++) {
						if(i != j && histogramBars[j].isLocalMax()
								&& histogramBars[j].value() > maxValueLimit
								//&& histogramBars[j].type != HistogramBar.SMALL
						) {
							int distance = Math.abs(histogramBars[i].i - histogramBars[j].i);
							if(distance < minDistance) {
								//minDistance = distance;
								if(distance <= 4 ||
										(distance <= 10 && areInSameRange(ranges, histogramBars[i], histogramBars[j]))) {
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
		}
		
		
		//if(maxValueShare > 0.35) {
			// one very dominant color
			//return 4;
		//}
		
		return 1;
	}

	private static boolean areInSameRange(List<HistogramRange> ranges, HistogramBar histogramBar, HistogramBar histogramBar2) {
		for (HistogramRange range : ranges) {
			if(range.containsBoth(histogramBar, histogramBar2)) {
				return true;
			}
		}
		return false;
	}

	private static HistogramBar[] getHistogramBars(int[] histogram, int max) {
		List<HistogramBar> histogramBars = new ArrayList<>();
		
		double maxValue = histogram[max];
		double share;
		//DecimalFormat df = new DecimalFormat("#.0000");
		for (int i = 0; i < histogram.length; i++) {
			//System.out.println(i + " " + histogram[i] + " " + df.format((double) histogram[i]/a));
			share = histogram[i]/maxValue;
			if(share > 0.1) {
				histogramBars.add(new HistogramBar(histogram, i, HistogramBar.BIG));
			} else if(share > 0.04) {
				histogramBars.add(new HistogramBar(histogram, i, HistogramBar.MEDIUM));
			} else if(share > 0.015) {
				histogramBars.add(new HistogramBar(histogram, i, HistogramBar.SMALL));
			}
		}
		
		return histogramBars.toArray(new HistogramBar[histogramBars.size()]);
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
