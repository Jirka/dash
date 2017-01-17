package cz.vutbr.fit.dashapp.eval.analysis.old;

import java.awt.image.BufferedImage;
import java.text.DecimalFormat;

import cz.vutbr.fit.dashapp.eval.metric.raster.gray.GrayBalance;
import cz.vutbr.fit.dashapp.eval.metric.raster.gray.GraySymmetry;
import cz.vutbr.fit.dashapp.eval.metric.raster.gray.BlackDensity;
import cz.vutbr.fit.dashapp.model.Dashboard;
import cz.vutbr.fit.dashapp.model.DashboardFile;
import cz.vutbr.fit.dashapp.util.MatrixUtils;

public class RasterAnalysis extends AbstractAnalysis implements IAnalysis {

	@Override
	public String getName() {
		return "Raster analysis";
	}

	@Override
	public String analyze(DashboardFile dashboardFile) {
		StringBuffer buffer = new StringBuffer();
		
		if(dashboardFile != null) {
			BufferedImage image = dashboardFile.getImage();
			if(image != null) {
				DecimalFormat df = new DecimalFormat("#.#####");
				
				buffer.append("====== RASTER COMMON ANALYSIS ======\n");
				buffer.append("\n");
				
				buffer.append("===== ADAPTIVE THRESHOLD =====\n");
				
				Dashboard dashboard = dashboardFile.getDashboard(true);
				int[][] matrix = MatrixUtils.printBufferedImage(image, dashboard);
				MatrixUtils.adaptiveThreshold(matrix, false, 0, 0, false);
				MatrixUtils.grayScale(matrix, true, false);
				formatMetric(buffer, new BlackDensity().measureGrayMatrix(matrix), df);
				formatMetric(buffer, new GrayBalance().measureGrayMatrix(matrix), df);
				formatMetric(buffer, new GraySymmetry().measureGrayMatrix(matrix), df);
				buffer.append("\n");
				
				buffer.append("===== GRAYSCALE =====\n");
				
				matrix = MatrixUtils.printBufferedImage(image, dashboard);
				MatrixUtils.grayScale(matrix, true, false);
				formatMetric(buffer, new GrayBalance().measureGrayMatrix(matrix), df);
				formatMetric(buffer, new GraySymmetry().measureGrayMatrix(matrix), df);
			}
		}
		
		return buffer.toString();
	}

}