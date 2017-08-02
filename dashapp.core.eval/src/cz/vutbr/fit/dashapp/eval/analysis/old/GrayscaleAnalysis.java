package cz.vutbr.fit.dashapp.eval.analysis.old;

import java.awt.image.BufferedImage;
import java.text.DecimalFormat;

import cz.vutbr.fit.dashapp.eval.metric.raster.gray.histogram.BackgroundShare;
import cz.vutbr.fit.dashapp.eval.metric.raster.gray.histogram.IntensitiesCount;
import cz.vutbr.fit.dashapp.image.util.HistogramUtils;
import cz.vutbr.fit.dashapp.image.util.PosterizationUtils;
import cz.vutbr.fit.dashapp.model.Dashboard;
import cz.vutbr.fit.dashapp.model.DashboardFile;
import cz.vutbr.fit.dashapp.util.matrix.ColorMatrix;

public class GrayscaleAnalysis extends AbstractAnalysis implements IAnalysis {
	
	private final DecimalFormat df = new DecimalFormat("#.#####");

	@Override
	public String getName() {
		return "Grayscale analysis";
	}
	
	private void analyzeImage(StringBuffer buffer, BufferedImage image, Dashboard dashboard, int bit) {
		int[][] matrix = ColorMatrix.printImageToMatrix(image, dashboard);
		ColorMatrix.toGrayScale(matrix, false, false);
		if(bit != 8) {
			PosterizationUtils.posterizeMatrix(matrix, 256/(int)(Math.pow(2, bit)), false);
		}
		ColorMatrix.toGrayScale(matrix, true, false);
		
		int histogram[] = HistogramUtils.getGrayscaleHistogram(matrix);
		
		buffer.append("===== " + bit + " bit (" + (int)(Math.pow(2, bit)) + " colors) =====\n");
		formatMetric(buffer, new BackgroundShare().measureGrayHistogram(histogram), df);
		formatMetric(buffer, new IntensitiesCount().measureGrayHistogram(histogram), df);
		buffer.append("\n");
	}

	@Override
	public String analyze(DashboardFile dashboardFile) {
		StringBuffer buffer = new StringBuffer();
		
		if(dashboardFile != null) {
			BufferedImage image = dashboardFile.getImage();
			if(image != null) {
				buffer.append("====== GRAYSCALE ANALYSIS ======\n");
				buffer.append("  -> gray scale\n");
				buffer.append("  -> posterization\n");
				buffer.append("\n");
				
				Dashboard dashboard = dashboardFile.getDashboard(true);
				analyzeImage(buffer, image, dashboard, 8);
				analyzeImage(buffer, image, dashboard, 7);
				analyzeImage(buffer, image, dashboard, 6);
				analyzeImage(buffer, image, dashboard, 5);
				analyzeImage(buffer, image, dashboard, 4);
				analyzeImage(buffer, image, dashboard, 3);
			}
		}
		
		return buffer.toString();
	}

}
