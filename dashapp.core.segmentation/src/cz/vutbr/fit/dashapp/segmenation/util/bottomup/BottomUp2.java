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
public class BottomUp2 {
	
	public static class BottomUp2Result {
		public int[][] matrix;
		public int hMaxLineSize;
		public int vMaxLineSize;
	}
	
	/**
	 * Method connects regions according to their closeness.
	 * @param image
	 */	
	public static BottomUp2Result bottomUp2(int[][] matrix) {
		// prepare image
		int[][] resultMatrix = bottomUp2Preprocess(matrix);

		// create rectangles
		BUBasicUtil.createRectangles(resultMatrix);
		
		// get regions
		List<Region> tmpRegionList = BUBasicUtil.getRegions(resultMatrix);
		
		// print regions
		GrayMatrix.clearMatrix(resultMatrix, GrayMatrix.WHITE);
		DrawRegionsUtil.drawRegions(resultMatrix, tmpRegionList, GrayMatrix.BLACK, true);
	
		// result
		BottomUp2Result result = new BottomUp2Result();
		
		// redraw rectangles
		// TODO: BUJoinRectanglesUtil.getTreshold contains some uclear code which should be replaced
		result.hMaxLineSize = BUJoinRectanglesUtil.getTreshold(resultMatrix, Constants.X);
		result.vMaxLineSize = BUJoinRectanglesUtil.getTreshold(resultMatrix, Constants.Y);
		result.matrix = BUJoinRectanglesUtil.reDrawRectangles(resultMatrix, result.hMaxLineSize, result.vMaxLineSize);

		return result;
	}
	
	protected static int[][] bottomUp2Preprocess(int[][] matrix) {
		// remove gradient
		matrix = FilterGradientsUtil.process(matrix, 4, true);
		// threshold
		ColorMatrix.toGrayScale(matrix, true, false);
		// threshold
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
