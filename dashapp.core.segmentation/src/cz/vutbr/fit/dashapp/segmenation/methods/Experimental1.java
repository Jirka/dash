package cz.vutbr.fit.dashapp.segmenation.methods;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import cz.vutbr.fit.dashapp.image.floodfill.SimpleRectangleFloodFill;
import cz.vutbr.fit.dashapp.image.util.HistogramUtils;
import cz.vutbr.fit.dashapp.image.util.PosterizationUtils;
import cz.vutbr.fit.dashapp.model.Dashboard;
import cz.vutbr.fit.dashapp.model.GraphicalElement;
import cz.vutbr.fit.dashapp.segmenation.AbstractSegmentationAlgorithm;
import cz.vutbr.fit.dashapp.segmenation.ISegmentationAlgorithm;
import cz.vutbr.fit.dashapp.segmenation.util.xycut.XYCutUtil;
import cz.vutbr.fit.dashapp.util.matrix.ColorMatrix;
import cz.vutbr.fit.dashapp.util.matrix.GrayMatrix;
import cz.vutbr.fit.dashapp.util.matrix.GrayMatrix.ThresholdCalculator;

/**
 * Experimental XY cut.
 * 
 * @author Jiri Hynek
 *
 */
public class Experimental1 extends AbstractSegmentationAlgorithm implements ISegmentationAlgorithm {
	
	public Experimental1() {
		super();
	}
	
	@Override
	public String getName() {
		return "Experimental method 1";
	}

	@Override
	public Dashboard processImage(BufferedImage image) {
		// convert buffered image to 2D array
		int[][] matrix = ColorMatrix.printImageToMatrix(image);
		
		final int w = image.getWidth();
		final int h = image.getHeight();
		
		// image preprocessing
		ColorMatrix.toGrayScale(matrix, false, false); // convert to gray scale
		PosterizationUtils.posterizeMatrix(matrix, 256/(int)(Math.pow(2, 4)), false); // 4 bits posterization
		debugImage("posterize", ColorMatrix.printMatrixToImage(null, matrix));
		
		int[][] rawMatrix = ColorMatrix.toGrayScale(matrix, true, true); // convert to raw values (0-255) for simple use
		int[] histogram = HistogramUtils.getGrayscaleHistogram(rawMatrix); // make histogram
		int mostFrequentValue = HistogramUtils.findMax(histogram, -1); // find most frequent value (possible background)
		if(mostFrequentValue < (GrayMatrix.WHITE/2)) {
			GrayMatrix.inverse(rawMatrix, false);
			mostFrequentValue = GrayMatrix.WHITE-mostFrequentValue;
		}
		GrayMatrix.update(rawMatrix, new ThresholdCalculator((int) mostFrequentValue-1), false); // threshold according to background
		debugImage("threshold most frequent value", GrayMatrix.printMatrixToImage(null, rawMatrix));
		rawMatrix = GrayMatrix.medianFilter(rawMatrix, 1); // median filter (remove noise)
		debugImage("+ median filter 1", GrayMatrix.printMatrixToImage(null, rawMatrix));
		rawMatrix = GrayMatrix.medianFilter(rawMatrix, 2); // median filter (remove noise)
		debugImage("+ median filter 2", GrayMatrix.printMatrixToImage(null, rawMatrix));
		
		int[][] edgesMatrix = GrayMatrix.edges(rawMatrix);
		GrayMatrix.inverse(edgesMatrix, false);
		debugImage("edges matrix", GrayMatrix.printMatrixToImage(null, edgesMatrix));
		
		new SimpleRectangleFloodFill(rawMatrix, false, GrayMatrix.BLACK).process();
		new SimpleRectangleFloodFill(rawMatrix, false, GrayMatrix.BLACK).process();
		debugImage("rectangles", GrayMatrix.printMatrixToImage(null, rawMatrix));
		
		List<Rectangle> rectangles = new ArrayList<>(); // result rectangles
		XYCutUtil.xyStep(rawMatrix, new Rectangle(0, 0, w, h), rectangles, true); // first step of recursive XY-cut
		
		// create dashboard (represents graphical regions)
		Dashboard dashboard = new Dashboard();
		dashboard.setDimension(0, 0, w, h);
		
		for (Rectangle r : rectangles) {
			dashboard.addChildGE(new GraphicalElement(r.x, r.y, r.width, r.height));
		}
		
		return dashboard;
	}

}
