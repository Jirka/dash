package cz.vutbr.fit.dashapp.segmenation.methods;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.List;

import com.tree.TreeNode;

import cz.vutbr.fit.dashapp.model.Dashboard;
import cz.vutbr.fit.dashapp.model.GraphicalElement;
import cz.vutbr.fit.dashapp.segmenation.AbstractSegmentationAlgorithm;
import cz.vutbr.fit.dashapp.segmenation.ISegmentationAlgorithm;
import cz.vutbr.fit.dashapp.segmenation.util.image.ColorLayersSegmentationUtil;
import cz.vutbr.fit.dashapp.segmenation.util.image.FilterGradientsUtil;
import cz.vutbr.fit.dashapp.segmenation.util.image.FindFrequentValuesUtil;
import cz.vutbr.fit.dashapp.segmenation.util.image.PosterizationUtil;
import cz.vutbr.fit.dashapp.segmenation.util.region.BottomUpAnalysisUtil;
import cz.vutbr.fit.dashapp.segmenation.util.region.DetectRegionsUtil;
import cz.vutbr.fit.dashapp.segmenation.util.region.DrawRegionsUtil;
import cz.vutbr.fit.dashapp.segmenation.util.region.LayoutUtil;
import cz.vutbr.fit.dashapp.segmenation.util.region.OverlappingRegionsUtil;
import cz.vutbr.fit.dashapp.segmenation.util.region.Region;
import cz.vutbr.fit.dashapp.segmenation.util.region.TopDownAnalysisUtil;
import cz.vutbr.fit.dashapp.util.matrix.ColorMatrix;
import cz.vutbr.fit.dashapp.util.matrix.GrayMatrix;

/**
 * Final method for the segmentation of dashboards.
 * 
 * @author Jirka Hynek
 *
 */
public class DashboardSegmentation extends AbstractSegmentationAlgorithm implements ISegmentationAlgorithm {
	
	public DashboardSegmentation() {
		super();
	}
	
	@Override
	public String getName() {
		return "Dashboard Segmentation";
	}
	
	@Override
	public Dashboard processImage(BufferedImage image) {
		final int w = image.getWidth();
		final int h = image.getHeight();
		
		// convert buffered image to 2D array
		int[][] matrix = ColorMatrix.printImageToMatrix(image);
		
		// convert to gray scale
		int[][] rawMatrix = ColorMatrix.toGrayScale(matrix, true, true);
		
		// remove gradients
		int gradientLimit = FilterGradientsUtil.recommendGradientThreshlodIterative(rawMatrix);
		System.out.println("segmentation: gradient limit = " + gradientLimit);
		int[][] nonGradientMatrix = rawMatrix;
		/*if(gradientLimit > 2) {
			nonGradientMatrix = FilterGradientsUtil.process(rawMatrix, 2);
		}*/
		nonGradientMatrix = FilterGradientsUtil.process(nonGradientMatrix, gradientLimit);		
		debugImage("non-gradient", GrayMatrix.printMatrixToImage(null, nonGradientMatrix));
		debugHistogram("non-gradient", nonGradientMatrix);
		
		// posterization
		int posetrizationLimit = PosterizationUtil.recommendPosterizationValue(nonGradientMatrix);
		System.out.println("segmentation : posterization limit = " + posetrizationLimit);
		GrayMatrix.posterizeMatrix(nonGradientMatrix, posetrizationLimit, false);
		debugImage("post. non-gradient", GrayMatrix.printMatrixToImage(null, nonGradientMatrix));
		debugHistogram("post. non-gradient", nonGradientMatrix);
		
		// detect colour layers
		List<Integer> frequentValues = FindFrequentValuesUtil.find(nonGradientMatrix);
		int[][] frequentColorMatrix = ColorLayersSegmentationUtil.segment(nonGradientMatrix, frequentValues);
		debugImage("frequent", GrayMatrix.printMatrixToImage(null, frequentColorMatrix));
		debugHistogram("frequent", frequentColorMatrix);
		
		// detect layout primitives
		List<Region> regions = DetectRegionsUtil.findRegions(frequentColorMatrix);
		
		// construct layout
		TreeNode<Region> root = LayoutUtil.constructTree(regions, 0, 0, w, h);
		debugImage("tree rectangle types", GrayMatrix.printMatrixToImage(null, DrawRegionsUtil.drawRegions(root, -1)));
		
		// top-down analysis (detect main regions)
		List<Region> mainRegions = TopDownAnalysisUtil.getMainRegions(root); // result rectangles*/
		debugImage("main regions", GrayMatrix.printMatrixToImage(null, DrawRegionsUtil.drawRegions(new int[w][h], mainRegions, GrayMatrix.BLACK, false)));
		
		// analyse overlap regions
		mainRegions = OverlappingRegionsUtil.arrangeOverlaps(new Region(0, 0, w, h, Region.TYPE_OTHER), mainRegions);
		
		// bottom-up analysis (find small regions located in empty spaces and construct main regions from them)
		mainRegions = BottomUpAnalysisUtil.clusterRemainingSmallRegions(mainRegions, root, this);
		
		// create dashboard
		Dashboard dashboard = new Dashboard();
		dashboard.setDimension(0, 0, w, h);
		
		// convert main regions to dashapp model
		for (Rectangle r : mainRegions) {
			dashboard.addChildGE(new GraphicalElement(dashboard, r.x, r.y, r.width, r.height));
		}
		
		return dashboard;
	}
	
}
