package cz.vutbr.fit.dashapp.segmenation;

import java.awt.image.BufferedImage;
import java.text.DecimalFormat;

import cz.vutbr.fit.dashapp.image.util.HistogramUtils;
import cz.vutbr.fit.dashapp.model.Dashboard;
import cz.vutbr.fit.dashapp.segmenation.AbstractSegmentationAlgorithm.DebugMode;
import cz.vutbr.fit.dashapp.segmenation.util.FilterGradientsUtil;
import cz.vutbr.fit.dashapp.util.matrix.ColorMatrix;
import cz.vutbr.fit.dashapp.util.matrix.MatrixUtils;

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
		int a = MatrixUtils.area(rawMatrix);
		System.out.println(a);
		DecimalFormat df = new DecimalFormat("#.0000");
		int[] histogram = HistogramUtils.getGrayscaleHistogram(rawMatrix);
		for (int i = 0; i < histogram.length; i++) {
			System.out.println(i + " " + histogram[i] + " " + df.format((double) histogram[i]/a));
		}
		
		//System.out.println(FilterGradientsUtil.recommendLimit(rawMatrix));
		//int[][] nonGradientMatrix = FilterGradientsUtil.process(rawMatrix, FilterGradientsUtil.recommendLimit(rawMatrix));
		
		Dashboard dashboard = new Dashboard();
		dashboard.setDimension(0, 0, w, h);
		
		return dashboard;
	}

	@Override
	public String getName() {
		return "XY cut final";
	}
	
}
