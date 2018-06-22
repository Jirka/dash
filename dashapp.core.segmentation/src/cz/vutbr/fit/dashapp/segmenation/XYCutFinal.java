package cz.vutbr.fit.dashapp.segmenation;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.List;

import com.tree.TreeNode;

import cz.vutbr.fit.dashapp.model.Dashboard;
import cz.vutbr.fit.dashapp.model.GraphicalElement;
import cz.vutbr.fit.dashapp.segmenation.util.FilterGradientsUtil;
import cz.vutbr.fit.dashapp.segmenation.util.FindFrequentValuesUtil;
import cz.vutbr.fit.dashapp.segmenation.util.FrequentValuesThresholdUtil;
import cz.vutbr.fit.dashapp.segmenation.util.PosterizeUtil;
import cz.vutbr.fit.dashapp.segmenation.util.region.DrawRegionsUtil;
import cz.vutbr.fit.dashapp.segmenation.util.region.FindSameColorRegionsUtil;
import cz.vutbr.fit.dashapp.segmenation.util.region.JoinSmallRegionsUtil;
import cz.vutbr.fit.dashapp.segmenation.util.region.ProcessRegionsUtil;
import cz.vutbr.fit.dashapp.segmenation.util.region.Region;
import cz.vutbr.fit.dashapp.segmenation.util.region.RegionsOverlapUtil;
import cz.vutbr.fit.dashapp.util.matrix.ColorMatrix;
import cz.vutbr.fit.dashapp.util.matrix.GrayMatrix;

public class XYCutFinal extends AbstractSegmentationAlgorithm implements ISegmentationAlgorithm {

	public XYCutFinal(DebugMode debugMode) {
		super(debugMode);
	}
	
	public XYCutFinal() {
		super();
	}
	
	@Override
	public Dashboard processImage(BufferedImage image) {
		//debugMode = DebugMode.NONE;
		final int w = image.getWidth();
		final int h = image.getHeight();
		
		// convert buffered image to 2D array
		int[][] matrix = ColorMatrix.printImageToMatrix(image);
		
		// convert to gray scale
		int[][] rawMatrix = ColorMatrix.toGrayScale(matrix, true, true);
		
		// remove gradients
		int gradientLimit = FilterGradientsUtil.recommendGradientLimitIterative(rawMatrix);
		System.out.println("gradient limit: " + gradientLimit);
		int[][] nonGradientMatrix = rawMatrix;
		/*if(gradientLimit > 2) {
			nonGradientMatrix = FilterGradientsUtil.process(rawMatrix, 2);
		}*/
		nonGradientMatrix = FilterGradientsUtil.process(nonGradientMatrix, gradientLimit);
		
		// experiment
		//GrayMatrix.printMatrixToImage(image, nonGradientMatrix);
		//nonGradientMatrix = ColorMatrix.toGrayScale(ColorMatrix.printImageToMatrix(image), true, true);
		
		// experiment
		//int[][] nonGradientMatrix = FilterGradientsUtil.process(matrix, FilterGradientsUtil.recommendGradientLimit(rawMatrix), true);
		//ColorMatrix.toGrayScale(nonGradientMatrix, true, false);
		
		
		//debug("non-gradient", GrayMatrix.printMatrixToImage(null, nonGradientMatrix));
		//debugHistogram("non-gradient", nonGradientMatrix);
		
		// posterization
		System.out.println("posterization: " + PosterizeUtil.recommendPosterizationLimit(nonGradientMatrix));
		GrayMatrix.posterizeMatrix(nonGradientMatrix, PosterizeUtil.recommendPosterizationLimit(nonGradientMatrix), false);
		//debug("post. non-gradient", GrayMatrix.printMatrixToImage(null, nonGradientMatrix));
		//debugHistogram("post. non-gradient", nonGradientMatrix);
		
		// max values threshold
		List<Integer> frequentValues = FindFrequentValuesUtil.find(nonGradientMatrix);
		int[][] frequentColorMatrix = FrequentValuesThresholdUtil.threshold(nonGradientMatrix, frequentValues);
		//debug("frequent", GrayMatrix.printMatrixToImage(null, frequentColorMatrix));
		//debugHistogram("frequent", frequentColorMatrix);
		
		// get regions
		List<Region> regions = FindSameColorRegionsUtil.findRegions(frequentColorMatrix);
		TreeNode<Region> root = ProcessRegionsUtil.constructTree(regions, 0, 0, w, h);
		List<Region> mainRegions = ProcessRegionsUtil.getMainRegions(root); // result rectangles*/
		
		//debug("tree rectangle types", GrayMatrix.printMatrixToImage(null, DrawRegionsUtil.drawRegions(root, -1)));
		//debug("main regions", GrayMatrix.printMatrixToImage(null, DrawRegionsUtil.drawRegions(new int[w][h], mainRegions, GrayMatrix.BLACK, false)));
		
		// some regions overlap other
		mainRegions = RegionsOverlapUtil.arrangeOverlaps(new Region(0, 0, w, h, Region.OTHER), mainRegions);
		
		// find small regions located in empty spaces and construct main regions from them
		mainRegions = JoinSmallRegionsUtil.completeEmptySpaces(mainRegions, root, this);
		
		// create dashboard
		Dashboard dashboard = new Dashboard();
		dashboard.setDimension(0, 0, w, h);
		
		for (Rectangle r : mainRegions) {
			dashboard.addChildGE(new GraphicalElement(dashboard, r.x, r.y, r.width, r.height));
		}
		
		return dashboard;
	}

	@Override
	public String getName() {
		return "XY cut final";
	}
	
}
