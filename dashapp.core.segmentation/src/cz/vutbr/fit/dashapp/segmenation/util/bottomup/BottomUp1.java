package cz.vutbr.fit.dashapp.segmenation.util.bottomup;

import java.util.List;

import cz.vutbr.fit.dashapp.image.util.HistogramUtils;
import cz.vutbr.fit.dashapp.model.Constants;
import cz.vutbr.fit.dashapp.segmenation.util.FilterGradientsUtil;
import cz.vutbr.fit.dashapp.segmenation.util.region.DrawRegionsUtil;
import cz.vutbr.fit.dashapp.segmenation.util.region.Region;
import cz.vutbr.fit.dashapp.util.matrix.ColorMatrix;
import cz.vutbr.fit.dashapp.util.matrix.GrayMatrix;
import cz.vutbr.fit.dashapp.util.matrix.GrayMatrix.ThresholdCalculator;

/**
 * 
 * @author Santiago Mejia (algorithms)
 * @author Jiri Hynek (code refactoring, modification of algorithms)
 *
 */
public class BottomUp1 {
	
	public static class BottomUp1Result {
		public int[][] matrix;
	}
	
	/**
	 * Method connects regions in rows and columns, according to their
	 * closeness.
	 * @param image
	 */	
	public static BottomUp1Result bottomUp1(int[][] matrix) {		
		// prepare image
		int[][] resultMatrix = bottomUp1Preprocess(matrix);
	
		// create rectangles
		BUBasicUtil.createRectangles(resultMatrix);
		
		// find regions and keep only larger than 3x3
		List<Region> tmpRegionList = BUBasicUtil.getRegions(resultMatrix);
		GrayMatrix.clearMatrix(resultMatrix, GrayMatrix.WHITE);
		DrawRegionsUtil.drawRegions(resultMatrix, tmpRegionList, GrayMatrix.BLACK, true);
		BUBasicUtil.createRectangles(resultMatrix);
		
		// create words / rows
		resultMatrix = BUJoinLineUtil.joinLine(resultMatrix, Constants.X);
		BUBasicUtil.createRectangles(resultMatrix);
		BUBasicUtil.createRectangles(resultMatrix);
	
		// join columns
		resultMatrix = BUJoinLineUtil.joinLine(resultMatrix, Constants.Y);
		BUBasicUtil.createRectangles(resultMatrix);
		BUBasicUtil.createRectangles(resultMatrix);
		
		// result
		BottomUp1Result result = new BottomUp1Result();
		result.matrix = resultMatrix;
	
		return result;
	
	}

	public static int[][] bottomUp1Preprocess(int[][] matrix) {
		// remove gradient
		matrix = FilterGradientsUtil.process(matrix, 3, true);
		// gray scale
		ColorMatrix.toGrayScale(matrix, true, false);
		// posterize
		GrayMatrix.posterizeMatrix(matrix, 3, false);
		// threshold according to most frequent value
		int[] histogram = HistogramUtils.getGrayscaleHistogram(matrix);
		int mostFrequentValue = HistogramUtils.findMax(histogram, -1);
		if (mostFrequentValue < (GrayMatrix.WHITE / 2)) {
			GrayMatrix.inverse(matrix, false);
			mostFrequentValue = GrayMatrix.WHITE - mostFrequentValue;
		}
		GrayMatrix.update(matrix, new ThresholdCalculator((int) mostFrequentValue - 1), false);
		
		return matrix;
	}

}
