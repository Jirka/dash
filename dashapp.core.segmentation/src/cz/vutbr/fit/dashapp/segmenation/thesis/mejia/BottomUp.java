// TODO generateDashboard findRegion

package cz.vutbr.fit.dashapp.segmenation.thesis.mejia;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.List;

import cz.vutbr.fit.dashapp.image.util.HistogramUtils;
import cz.vutbr.fit.dashapp.image.util.PosterizationUtils;
import cz.vutbr.fit.dashapp.model.Dashboard;
import cz.vutbr.fit.dashapp.model.GraphicalElement;
import cz.vutbr.fit.dashapp.segmenation.AbstractSegmentationAlgorithm;
import cz.vutbr.fit.dashapp.segmenation.ISegmentationAlgorithm;
import cz.vutbr.fit.dashapp.segmenation.thesis.mejia.util.BottomUpUtil;
import cz.vutbr.fit.dashapp.segmenation.util.image.FilterGradientsUtil;
import cz.vutbr.fit.dashapp.segmenation.util.region.Region;
import cz.vutbr.fit.dashapp.util.matrix.ColorMatrix;
import cz.vutbr.fit.dashapp.util.matrix.GrayMatrix;
import cz.vutbr.fit.dashapp.util.matrix.GrayMatrix.ThresholdCalculator;

/**
 * This algorithm was design as part of master thesis.
 * http://www.fit.vutbr.cz/study/DP/DP.php.en?id=20468&y=2017
 * 
 * @author Santiago Mejia
 *
 */
public class BottomUp extends AbstractSegmentationAlgorithm implements ISegmentationAlgorithm {

	private int _hMaxLineSize;
	private int _vMaxLineSize;
	private int _w, _h;

	@Override
	public String getName() {
		return "Mejia's Bottom Up";
	}

	public BottomUp() {
		super();
	}

	@Override
	public Dashboard processImage(BufferedImage image) {
		_w = image.getWidth();
		_h = image.getHeight();

		int[][] result1 = bottomUp1(image);
		float avgTest1 = BottomUpUtil.averageMatrix(result1, _w, _h);
		
		int[][] result2 = bottomUp2(image);
		float avgTest2 = BottomUpUtil.averageMatrix(result2, _w, _h);

		int[][] finalCombination = new int[_w][_h];
		BottomUpUtil.setArray(finalCombination, 255);

		for (int i = 0; i < _w; i++)
			for (int j = 0; j < _h; j++)
				if ((avgTest1 <= 0.95 && result1[i][j] == 0) || (avgTest2 < 0.95 && result2[i][j] == 0))
					finalCombination[i][j] = 0;

		BottomUpUtil.createRectangles(finalCombination);
		BottomUpUtil.createRectangles(finalCombination);
		
		List<Region> tmpRegions = BottomUpUtil.getRegions(result1, _w, _h);
		connectSmallRegions(finalCombination, BottomUpUtil.getMinRegionSize(tmpRegions));

		Dashboard dashboard = new Dashboard();
		dashboard.setDimension(0, 0, _w, _h);
		List<Region> regions = BottomUpUtil.getRegions(finalCombination, _w, _h);
		for (Rectangle r : regions) {
			dashboard.addChildGE(new GraphicalElement(r.x, r.y, r.width, r.height));
		}

		return dashboard;
	}

	/**
	 * Method connects regions in rows and columns, according to their
	 * closeness.
	 * @param image
	 */	
	private int[][] bottomUp1(BufferedImage image) {
		// convert buffered image to 2D array
		int[][] matrix = ColorMatrix.printImageToMatrix(image);
	
		// Preprocess
		// remove gradient
		matrix = FilterGradientsUtil.process(matrix, 3, true);
		// grayscale
		ColorMatrix.toGrayScale(matrix, false, false);
		// posterize
		PosterizationUtils.posterizeMatrix_BitSize(matrix, 3, false);
		// threshold
		ColorMatrix.toGrayScale(matrix, true, false);
		int[] histogram = HistogramUtils.getGrayscaleHistogram(matrix);
		int mostFrequentValue = HistogramUtils.findMax(histogram, -1);
		if (mostFrequentValue < (GrayMatrix.WHITE / 2)) {
			GrayMatrix.inverse(matrix, false);
			mostFrequentValue = GrayMatrix.WHITE - mostFrequentValue;
		}
		GrayMatrix.update(matrix, new ThresholdCalculator((int) mostFrequentValue - 1), false);
	
		BottomUpUtil.createRectangles(matrix);
	
		List<Region> tmpRegionList = BottomUpUtil.getRegions(matrix, _w, _h);
		BottomUpUtil.setArray(matrix, 255);
	
		for (int r = 0; r < tmpRegionList.size(); r++) {
			for (int i = 0; i < tmpRegionList.get(r).getWidth(); i++) {
				for (int j = 0; j < tmpRegionList.get(r).getHeight(); j++) {
					matrix[tmpRegionList.get(r).x + i][tmpRegionList.get(r).y + j] = 0;
				}
			}
		}
	
		BottomUpUtil.createRectangles(matrix);
		// create words / rows
		matrix = BottomUpUtil.joinRows(matrix, _w, _h);
		BottomUpUtil.createRectangles(matrix);
		BottomUpUtil.createRectangles(matrix);
	
		matrix = BottomUpUtil.joinColumns(matrix, _w, _h);
		BottomUpUtil.createRectangles(matrix);
		BottomUpUtil.createRectangles(matrix);
	
		return matrix;
	
	}

	/**
	 * Method connects regions according to their closeness.
	 * @param image
	 */	
	private int[][] bottomUp2(BufferedImage image) {
		int[][] outMatrix = ColorMatrix.printImageToMatrix(image);
		// remove gradient
		outMatrix = FilterGradientsUtil.process(outMatrix, 4, true);
		// threshold
		ColorMatrix.toGrayScale(outMatrix, true, false);
		int[] histogram = HistogramUtils.getGrayscaleHistogram(outMatrix);
		int mostFrequentValue = HistogramUtils.findMax(histogram, -1);
		if (mostFrequentValue < (GrayMatrix.WHITE / 2)) {
			GrayMatrix.inverse(outMatrix, false);
			mostFrequentValue = GrayMatrix.WHITE - mostFrequentValue;
		}
		GrayMatrix.update(outMatrix, new ThresholdCalculator((int) mostFrequentValue - 1), false);

		BottomUpUtil.createRectangles(outMatrix);
		List<Region> tmpRegionList = BottomUpUtil.getRegions(outMatrix, _w, _h);

		BottomUpUtil.setArray(outMatrix, 255);

		for (int region = 0; region < tmpRegionList.size(); region++) {
			Region tmpRegion = tmpRegionList.get(region);
			for (int i = tmpRegion.x; i < tmpRegion.x + tmpRegion.width; i++)
				for (int j = tmpRegion.y; j < tmpRegion.y + tmpRegion.height; j++)
					outMatrix[i][j] = 0;
		}
	
		int[] outThresholdArray = new int[2];
		BottomUpUtil.getTreshold(outMatrix, outThresholdArray, _w, _h);
		_hMaxLineSize = outThresholdArray[0];
		_vMaxLineSize = outThresholdArray[1];

		outMatrix = BottomUpUtil.reDrawRectangles(outMatrix, _hMaxLineSize, _vMaxLineSize, _w, _h);

		return outMatrix;
	}

	/**
	 * Method connects regions according to their closeness and size.
	 * @param image
	 */	
	private void connectSmallRegions(int[][] inMatrix, int minRegionSize) {
		int[][] resultMask = new int[_w][_h];
		int[][] compareMatrix = resultMask;
		BottomUpUtil.setArray(compareMatrix, 255);
		
		do {
			for (int i = 0; i < _w; i++)
				for (int j = 0; j < _h; j++)
					compareMatrix[i][j] = resultMask[i][j];
			
			List<Region> tmpRegionList = BottomUpUtil.getRegions(inMatrix, _w, _h);
			for (int i = 0; i < tmpRegionList.size(); i++) {
				if (tmpRegionList.get(i).area() > minRegionSize) {
					tmpRegionList.remove(i);
					i--;
				}
			}
			
			resultMask = BottomUpUtil.connectSmallRegions(tmpRegionList, inMatrix, _hMaxLineSize, _vMaxLineSize, _w, _h);
			for (int i = 0; i < _w; i++)
				for (int j = 0; j < _h; j++)
					if (resultMask[i][j] == 0 || inMatrix[i][j] == 0)
						inMatrix[i][j] = 0;
			BottomUpUtil.createRectangles(inMatrix);
		} while (BottomUpUtil.differentMatrices(resultMask, compareMatrix, _w, _h));
	}

}
