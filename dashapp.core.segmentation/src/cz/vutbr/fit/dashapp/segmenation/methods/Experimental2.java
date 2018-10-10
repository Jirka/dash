package cz.vutbr.fit.dashapp.segmenation.methods;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import cz.vutbr.fit.dashapp.image.util.PosterizationUtils;
import cz.vutbr.fit.dashapp.model.Dashboard;
import cz.vutbr.fit.dashapp.model.GraphicalElement;
import cz.vutbr.fit.dashapp.segmenation.AbstractSegmentationAlgorithm;
import cz.vutbr.fit.dashapp.segmenation.ISegmentationAlgorithm;
import cz.vutbr.fit.dashapp.segmenation.util.image.HoughLineUtil;
import cz.vutbr.fit.dashapp.segmenation.util.xycut.XYCutUtil;
import cz.vutbr.fit.dashapp.util.matrix.ColorMatrix;
import cz.vutbr.fit.dashapp.util.matrix.GrayMatrix;
import cz.vutbr.fit.dashapp.util.matrix.GrayMatrix.ThresholdCalculator;
import cz.vutbr.fit.dashapp.util.matrix.MatrixUtils;

/**
 * Experimental XY-cut.
 * 
 * @author Jiri Hynek
 *
 */
public class Experimental2 extends AbstractSegmentationAlgorithm implements ISegmentationAlgorithm {
	
	public Experimental2() {
		super();
	}
	
	@Override
	public String getName() {
		return "Experimental method 2";
	}

	@Override
	public Dashboard processImage(BufferedImage image) {
		// convert buffered image to 2D array
		int[][] matrix = ColorMatrix.printImageToMatrix(image);
		
		final int w = image.getWidth();
		final int h = image.getHeight();
		
		// image preprocessing
		ColorMatrix.toGrayScale(matrix, false, false); // convert to gray scale
		PosterizationUtils.posterizeMatrix(matrix, 256/(int)(Math.pow(2, 6)), false); // 6 bits posterization
		int[][] rawMatrix = ColorMatrix.toGrayScale(matrix, true, true); // convert to raw values (0-255) for simple use
		int[][] edgesMatrix = GrayMatrix.edges(rawMatrix);
		GrayMatrix.inverse(edgesMatrix, false);
		GrayMatrix.update(edgesMatrix, new ThresholdCalculator(GrayMatrix.WHITE-1), false); // threshold according to background
		//edgesMatrix = GrayMatrix.emphasize(edgesMatrix, 1);
		debugImage("edges matrix", GrayMatrix.printMatrixToImage(null, edgesMatrix));
		int[][] filteredEdgesMatrix = GrayMatrix.lines(edgesMatrix, 10, 10);
		debugImage("filtered edges matrix", GrayMatrix.printMatrixToImage(null, filteredEdgesMatrix));
		
		int[][] linesMatrix = GrayMatrix.lines(edgesMatrix, 40, 40);
		debugImage("lines matrix", GrayMatrix.printMatrixToImage(null, linesMatrix));
		
		// ------ Hough Transform to detect lines (experiment)
		int[][] edgesMatrix2 = MatrixUtils.copy(edgesMatrix); // debug
		int[][] houghLineMatrix = HoughLineUtil.process(edgesMatrix2, 0.5, 0.5);
		debugImage("hough_lines", GrayMatrix.printMatrixToImage(null, houghLineMatrix));
		debugImage("hough_lines_edges", GrayMatrix.printMatrixToImage(null, MatrixUtils.copyPixels(MatrixUtils.copy(edgesMatrix), houghLineMatrix, GrayMatrix.BLACK, Color.RED.getRGB())));
		debugImage("hough_lines_filtered", GrayMatrix.printMatrixToImage(null, GrayMatrix.filterPixels(edgesMatrix, houghLineMatrix, true)));
		
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
