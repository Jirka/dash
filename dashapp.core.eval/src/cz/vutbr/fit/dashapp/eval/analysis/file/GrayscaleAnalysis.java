package cz.vutbr.fit.dashapp.eval.analysis.file;

import java.awt.image.BufferedImage;
import java.text.DecimalFormat;

import cz.vutbr.fit.dashapp.eval.analysis.AbstractFileAnalysis;
import cz.vutbr.fit.dashapp.eval.analysis.IFileAnalysis;
import cz.vutbr.fit.dashapp.eval.metric.raster.gray.histogram.HistogramBackgroundShare;
import cz.vutbr.fit.dashapp.eval.metric.raster.gray.histogram.HistogramIntensitiesCount;
import cz.vutbr.fit.dashapp.image.util.HistogramUtils;
import cz.vutbr.fit.dashapp.image.util.PosterizationUtils;
import cz.vutbr.fit.dashapp.model.Dashboard;
import cz.vutbr.fit.dashapp.model.DashboardFile;
import cz.vutbr.fit.dashapp.util.matrix.ColorMatrix;

/**
 * 
 * @author Jiri Hynek
 *
 */
public class GrayscaleAnalysis extends AbstractFileAnalysis implements IFileAnalysis {

	@Override
	public String getLabel() {
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
		final DecimalFormat df = new DecimalFormat("#.#####");
		formatMetric(buffer, new HistogramBackgroundShare().measureGrayHistogram(histogram), df);
		formatMetric(buffer, new HistogramIntensitiesCount().measureGrayHistogram(histogram), df);
		buffer.append("\n");
	}

	@Override
	public String processFile(DashboardFile dashboardFile) {
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
