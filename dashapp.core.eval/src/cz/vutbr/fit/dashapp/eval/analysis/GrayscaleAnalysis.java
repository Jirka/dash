package cz.vutbr.fit.dashapp.eval.analysis;

import java.awt.image.BufferedImage;
import java.text.DecimalFormat;

import cz.vutbr.fit.dashapp.model.Dashboard;
import cz.vutbr.fit.dashapp.util.MatrixUtils;
import cz.vutbr.fit.dashapp.eval.metric.BackgroundShare;
import cz.vutbr.fit.dashapp.eval.metric.IntensitiesCount;

public class GrayscaleAnalysis extends AbstractAnalysis implements IAnalysis {
	
	private final DecimalFormat df = new DecimalFormat("#.#####");

	public GrayscaleAnalysis(Dashboard dashboard) {
		super(dashboard);
	}

	@Override
	public String getName() {
		return "Grayscale analysis";
	}
	
	private void analyseImage(StringBuffer buffer, BufferedImage image, int bit) {
		int[][] matrix = MatrixUtils.printBufferedImage(image, dashboard);
		MatrixUtils.grayScale(matrix, false, false);
		if(bit != 8) {
			MatrixUtils.posterizeMatrix(matrix, 256/(int)(Math.pow(2, bit)), false);
		}
		MatrixUtils.grayScaleToValues(matrix, false);
		
		int histogram[] = MatrixUtils.getGrayscaleHistogram(matrix);
		
		buffer.append("===== " + bit + " bit (" + (int)(Math.pow(2, bit)) + " colors) =====\n");
		formatMetric(buffer, new BackgroundShare(dashboard, histogram), df);
		formatMetric(buffer, new IntensitiesCount(dashboard, histogram), df);
		buffer.append("\n");
	}

	@Override
	public String analyse() {
		StringBuffer buffer = new StringBuffer();
		
		if(dashboard != null) {
			BufferedImage image = dashboard.getImage();
			if(image != null) {
				buffer.append("====== GRAYSCALE ANALYSIS ======\n");
				buffer.append("  -> gray scale\n");
				buffer.append("  -> posterization\n");
				buffer.append("\n");
				
				analyseImage(buffer, image, 8);
				analyseImage(buffer, image, 7);
				analyseImage(buffer, image, 6);
				analyseImage(buffer, image, 5);
				analyseImage(buffer, image, 4);
				analyseImage(buffer, image, 3);
			}
		}
		
		return buffer.toString();
	}

}
