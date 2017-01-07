package cz.vutbr.fit.dashapp.eval.analysis.old;

import java.awt.image.BufferedImage;
import java.text.DecimalFormat;

import cz.vutbr.fit.dashapp.model.DashboardFile;
import cz.vutbr.fit.dashapp.util.MatrixUtils;
import cz.vutbr.fit.dashapp.eval.metric.BackgroundShare;
import cz.vutbr.fit.dashapp.eval.metric.IntensitiesCount;

public class GrayscaleAnalysis extends AbstractAnalysis implements IAnalysis {
	
	private final DecimalFormat df = new DecimalFormat("#.#####");

	public GrayscaleAnalysis(DashboardFile dashboardFile) {
		super(dashboardFile);
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
		
		if(dashboardFile != null) {
			BufferedImage image = dashboardFile.getImage();
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
