package cz.vutbr.fit.dashapp.segmenation;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.List;


import com.tree.TreeNode;

import cz.vutbr.fit.dashapp.image.util.PosterizationUtils;
import cz.vutbr.fit.dashapp.model.Dashboard;
import cz.vutbr.fit.dashapp.model.GraphicalElement;
import cz.vutbr.fit.dashapp.segmenation.util.EmphasizeSameColorsUtil;
import cz.vutbr.fit.dashapp.segmenation.util.FilterGradientsUtil;
import cz.vutbr.fit.dashapp.segmenation.util.FindFrequentValuesUtil;
import cz.vutbr.fit.dashapp.segmenation.util.FrequentValuesThresholdUtil;
import cz.vutbr.fit.dashapp.segmenation.util.HoughLineUtil;
import cz.vutbr.fit.dashapp.segmenation.util.region.DrawRegionsUtil;
import cz.vutbr.fit.dashapp.segmenation.util.region.FindSameColorRegionsUtil;
import cz.vutbr.fit.dashapp.segmenation.util.region.ProcessRegionsUtil;
import cz.vutbr.fit.dashapp.segmenation.util.region.Region;
import cz.vutbr.fit.dashapp.util.matrix.ColorMatrix;
import cz.vutbr.fit.dashapp.util.matrix.GrayMatrix;

public class XYCut4 extends AbstractSegmentationAlgorithm implements ISegmentationAlgorithm {
	
	public XYCut4(DebugMode debugMode) {
		super(debugMode);
	}
	
	public XYCut4() {
		super();
	}

	@Override
	public Dashboard processImage(BufferedImage image) {
		//debugMode = DebugMode.NONE;
		// convert buffered image to 2D array
		int[][] matrix = ColorMatrix.printImageToMatrix(image);
		
		final int w = image.getWidth();
		final int h = image.getHeight();
		final int a = w*h;
		
		// ------ gray posterized
		ColorMatrix.toGrayScale(matrix, false, false); // convert to gray scale
		int[][] rawMatrix = ColorMatrix.toGrayScale(matrix, true, true);
		//PosterizationUtils.posterizeMatrix_BitSize(matrix, 6, false); // n bit posterization
		//int[][] rawMatrix6 = ColorMatrix.toGrayScale(matrix, true, true); // convert to raw values (0-255) for simple use
		
		// ------ find large same color regions
		//int[][] sameColorAreasMatrix = EmphasizeSameColorsUtil.process(rawMatrix, 1000, 20);
		//debug("same colors", GrayMatrix.printMatrixToImage(null, sameColorAreasMatrix));
		
		// ------ gradients detection (experiment)
		int[][] nonGradientMatrix = FilterGradientsUtil.process(rawMatrix, 2);
		debug("gradient removal", GrayMatrix.printMatrixToImage(null, nonGradientMatrix));
		
		// ------ gradients detection using RGB matrix (experiment)
		//int[][] nonGradientMatrixColor = FilterGradientsUtil.process(ColorMatrix.printImageToMatrix(image), 4, true);
		//ColorMatrix.toGrayScale(nonGradientMatrixColor, false, false);
		//debug("gradient removal", ColorMatrix.printMatrixToImage(null, nonGradientMatrixColor));
		
		// posterization is needed after gradient removal
		GrayMatrix.posterizeMatrix_mod(nonGradientMatrix, 256/(int)(Math.pow(2, 6)), false); // 6 bits posterization
		debug("gradient removal post", GrayMatrix.printMatrixToImage(null, nonGradientMatrix));
		
		// ------ edges (experiment)
		//int[][] edgesMatrix = GrayMatrix.edges(nonGradientMatrix);
		//GrayMatrix.inverse(edgesMatrix, false);
		//debug("edges", GrayMatrix.printMatrixToImage(null, edgesMatrix));
		
		// ------ lines (experiment)
		//int[][] linesMatrix = GrayMatrix.lines(edgesMatrix, 20, 20);
		//debug("lines", GrayMatrix.printMatrixToImage(null, linesMatrix));
		
		// ------ median filter (experiment)
		//int[][] blurMatrix = GrayMatrix.medianFilter(rawMatrix, 1);
		//debug("blur", GrayMatrix.printMatrixToImage(null, blurMatrix));
		
		// ------ median filter - only for noise (experiment)
		//int[][] blurMatrixNoise = GrayMatrix.medianFilter(nonGradientMatrix, 1, true);
		//debug("blur noise", GrayMatrix.printMatrixToImage(null, blurMatrixNoise));
		
		// ------ sharpen (experiment)
		//int[][] sharpenMatrix = GrayMatrix.sharpen(rawMatrix);
		//int[][] sharpenEdgesMatrix = GrayMatrix.edges(sharpenMatrix);
		//GrayMatrix.inverse(sharpenEdgesMatrix, false);
		//debug("sharpen", GrayMatrix.printMatrixToImage(null, sharpenMatrix));
		//debug("sharpen edges", GrayMatrix.printMatrixToImage(null, sharpenEdgesMatrix));
		
		// ------ Hough Transform to detect lines (experiment)
		//int[][] edgesMatrix2 = GrayMatrix.copy(edgesMatrix); // debug
		//int[][] houghLineMatrix = HoughLineUtil.process(edgesMatrix2, 0.5, 0.5);
		//debug("hough_lines", GrayMatrix.printMatrixToImage(null, houghLineMatrix));
		//debug("hough_lines_edges", GrayMatrix.printMatrixToImage(null, GrayMatrix.copyPixels(GrayMatrix.copy(edgesMatrix), houghLineMatrix, GrayMatrix.BLACK, Color.RED.getRGB())));
		//debug("hough_lines_filtered", GrayMatrix.printMatrixToImage(null, GrayMatrix.filterPixels(edgesMatrix, houghLineMatrix, true)));
		
		// ------ threshold according to histogram (the most frequent values)
		// matrix can contain several color values (mostly 2 - 5)
		List<Integer> frequentValues = FindFrequentValuesUtil.find(nonGradientMatrix);
		int[][] frequentColorMatrix = FrequentValuesThresholdUtil.threshold(nonGradientMatrix, frequentValues);
		debug("histogram_70_gradient", GrayMatrix.printMatrixToImage(null, frequentColorMatrix));
		
		List<Integer> frequentValues2 = FindFrequentValuesUtil.find2(nonGradientMatrix);
		int[][] frequentColorMatrix2 = FrequentValuesThresholdUtil.threshold2(nonGradientMatrix, frequentValues2);
		debug("histogram_70_2_gradient", GrayMatrix.printMatrixToImage(null, frequentColorMatrix2));
		
		// ------ histogram threshold + edges + lines (experiment)
		//int[][] frequentColorMatrixEdges = GrayMatrix.edges(frequentColorMatrix);
		//GrayMatrix.inverse(frequentColorMatrixEdges, false);
		//frequentColorMatrixEdges = GrayMatrix.lines(frequentColorMatrixEdges, 20, 20);
		//debug("histogram_70_edges", GrayMatrix.printMatrixToImage(null, frequentColorMatrixEdges));
		
		// ------ find rectangle regions
		List<Region> regions = FindSameColorRegionsUtil.findRegions(frequentColorMatrix2);
		//debug("rectangles", GrayMatrix.printMatrixToImage(null, DrawRegionsUtil.drawRegions(GrayMatrix.newMatrix(w, h, GrayMatrix.WHITE), regions)));
		//debug("rectangle types", GrayMatrix.printMatrixToImage(null, DrawRegionsUtil.drawRegionTypes(GrayMatrix.newMatrix(w, h, GrayMatrix.WHITE), regions)));
		
		// ------ construct tree
		TreeNode<Region> root = ProcessRegionsUtil.constructTree(regions, 0, 0, w, h);
		debug("tree rectangle types", GrayMatrix.printMatrixToImage(null, DrawRegionsUtil.drawRegions(root, -1)));
		
		List<Region> mainRegions = ProcessRegionsUtil.getMainRegions(root); // result rectangles*/
		//List<Region> mainRegions = new ArrayList<>();
		
		//XYCutUtil.xyStep(rawMatrix, new Rectangle(0, 0, w, h), rectangles, true); // first step of recursive XY-cut
		
		// create dashboard (represents graphical regions)
		Dashboard dashboard = new Dashboard();
		dashboard.setDimension(0, 0, w, h);
		
		for (Rectangle r : mainRegions) {
			dashboard.addChildGE(new GraphicalElement(dashboard, r.x, r.y, r.width, r.height));
		}
		
		return dashboard;
	}

	@Override
	public String getName() {
		return "XY-cut";
	}

}
