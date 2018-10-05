package cz.vutbr.fit.dashapp.segmenation.thesis.mejia;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.List;

import cz.vutbr.fit.dashapp.model.Dashboard;
import cz.vutbr.fit.dashapp.model.GraphicalElement;
import cz.vutbr.fit.dashapp.segmenation.AbstractSegmentationAlgorithm;
import cz.vutbr.fit.dashapp.segmenation.ISegmentationAlgorithm;
import cz.vutbr.fit.dashapp.segmenation.thesis.mejia.util.BUBasicUtil;
import cz.vutbr.fit.dashapp.segmenation.thesis.mejia.util.BUConnectSmallRegionsUtil;
import cz.vutbr.fit.dashapp.segmenation.thesis.mejia.util.BottomUp1;
import cz.vutbr.fit.dashapp.segmenation.thesis.mejia.util.BottomUp2;
import cz.vutbr.fit.dashapp.segmenation.thesis.mejia.util.BottomUp1.BottomUp1Result;
import cz.vutbr.fit.dashapp.segmenation.thesis.mejia.util.BottomUp2.BottomUp2Result;
import cz.vutbr.fit.dashapp.segmenation.util.region.Region;
import cz.vutbr.fit.dashapp.util.matrix.ColorMatrix;
import cz.vutbr.fit.dashapp.util.matrix.GrayMatrix;

/**
 * 
 * Refactorization of Bottom Up algorithm.
 * 
 * @author Santiago Mejia
 * @author Jiri Hynek (code refactoring)
 *
 */
public class BottomUpRefactorized extends AbstractSegmentationAlgorithm implements ISegmentationAlgorithm {
	
	public BottomUpRefactorized() {
		super();
	}

	@Override
	public String getName() {
		return "Mejia's Bottom Up 2";
	}

	@Override
	public Dashboard processImage(BufferedImage image) {
		//setDebugMode(DebugMode.NONE);
		final int w = image.getWidth();
		final int h = image.getHeight();
		
		// convert buffered image to 2D array
		int[][] matrix = ColorMatrix.printImageToMatrix(image);
		
		// version 1
		BottomUp1Result result1 = BottomUp1.bottomUp1(matrix);
		debugImage("bottom up 1", GrayMatrix.printMatrixToImage(null, result1.matrix));
		
		// version 2
		// TODO bottomUp2 contains some unclear code which should be replaced
		BottomUp2Result result2 = BottomUp2.bottomUp2(matrix);
		debugImage("bottom up 2", GrayMatrix.printMatrixToImage(null, result2.matrix));
		
		// combination
		int[][] finalCombination = BUBasicUtil.combineResults(result1.matrix, result2.matrix);
		debugImage("combination", GrayMatrix.printMatrixToImage(null, finalCombination));
		
		// connect small regions
		// TODO: getPreferredMinRegionSize is not optimal, it should be changed
		int preferredMinSize = BUBasicUtil.getPreferredMinRegionSize(BUBasicUtil.getRegions(result1.matrix));
		BUConnectSmallRegionsUtil.connectSmallRegions(finalCombination, preferredMinSize, result2.hMaxLineSize, result2.vMaxLineSize);

		// convert to dashboard model
		Dashboard dashboard = new Dashboard();
		dashboard.setDimension(0, 0, w, h);
		List<Region> regions = BUBasicUtil.getRegions(finalCombination);
		for (Rectangle r : regions) {
			dashboard.addChildGE(new GraphicalElement(dashboard, r.x, r.y, r.width, r.height));
		}
		
		return dashboard;
	}

}
