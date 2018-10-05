package cz.vutbr.fit.dashapp.segmenation.util.image;

import cz.vutbr.fit.dashapp.image.util.HistogramUtils;
import cz.vutbr.fit.dashapp.util.matrix.MatrixUtils;

/**
 * 
 * @author Jiri Hynek
 *
 */
public class PosterizationUtil {
	
	/**
	 * Method recommends the value for posterization.
	 * It is based on heuristic analysis of the image histogram.
	 * 
	 * @param matrix
	 * @return
	 */
	public static int recommendPosterizationValue(int[][] matrix) {
		int[] histogram = HistogramUtils.getGrayscaleHistogram(matrix);
		
		// analyze max value
		int max = HistogramUtils.findMax(histogram, -1);
		int a = MatrixUtils.area(matrix);
		double maxValueShare = (double) histogram[max]/a;
		//System.out.println("max: " + max + " " + maxValueShare);
		
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

}
