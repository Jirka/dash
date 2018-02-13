package cz.vutbr.fit.dashapp.segmenation.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cz.vutbr.fit.dashapp.image.util.HistogramUtils;
import cz.vutbr.fit.dashapp.util.matrix.MatrixUtils;

public class PosterizeUtil {
	
	public static int recommendPosterizationLimit(int[][] matrix) {
		int[] histogram = HistogramUtils.getGrayscaleHistogram(matrix);
		
		// analyze max value
		int max = HistogramUtils.findMax(histogram, -1);
		int a = MatrixUtils.area(matrix);
		
		double maxValueShare = (double) histogram[max]/a;
		
		//System.out.println("max: " + max + " " + (double) histogram[max]/a);
		
		// analyze important histogram points
		//HistogramPoint[] histogramPoints = getHistogramPoints(histogram, max);
		//System.out.println(Arrays.toString(histogramPoints));
		
		if(maxValueShare > 0.35) {
			// one dominant color
			
			int left, right, previous, val;
			val = histogram[max];
			int limit = (int) (val*0.1);
			
			// left side
			previous = max;
			left = max;
			for (int j = max-1; j >= 0; j--) {
				if(histogram[j] < histogram[previous]*0.9 && histogram[j] > limit) {
					left--;
					previous = j;
				} else {
					break;
				}
			}
			
			// right side
			previous = max;
			right = max;
			for (int j = max+1; j < histogram.length; j++) {
				if(histogram[j] < histogram[previous]*0.9 && histogram[j] > limit) {
					right++;
					previous = j;
				} else {
					break;
				}
			}
			
			// test big values
			while(histogram[max] > limit) {
				if(max < left || max > right) {
					return 6;
				}
				max = HistogramUtils.findMax(histogram, max);
			}
			
			return 4;
		}
		
		
		return 6;
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
			}
		}
		
		return histogramPoints.toArray(new HistogramPoint[histogramPoints.size()]);
	}

}
