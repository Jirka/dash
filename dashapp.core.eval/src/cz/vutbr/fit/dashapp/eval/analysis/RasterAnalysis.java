package cz.vutbr.fit.dashapp.eval.analysis;

import java.awt.image.BufferedImage;
import java.text.DecimalFormat;

import cz.vutbr.fit.dashapp.model.Dashboard;
import cz.vutbr.fit.dashapp.util.MatrixUtils;
import cz.vutbr.fit.dashapp.eval.metric.RasterBalance;
import cz.vutbr.fit.dashapp.eval.metric.RasterSymmetry;
import cz.vutbr.fit.dashapp.eval.metric.ThresholdDensity;

public class RasterAnalysis extends AbstractAnalysis implements IAnalysis {

	public RasterAnalysis(Dashboard dashboard) {
		super(dashboard);
	}

	@Override
	public String getName() {
		return "Raster analysis";
	}

	@Override
	public String analyse() {
		StringBuffer buffer = new StringBuffer();
		
		if(dashboard != null) {
			BufferedImage image = dashboard.getImage();
			if(image != null) {
				DecimalFormat df = new DecimalFormat("#.#####");
				
				buffer.append("====== RASTER COMMON ANALYSIS ======\n");
				buffer.append("\n");
				
				buffer.append("===== ADAPTIVE THRESHOLD =====\n");
				
				int[][] matrix = MatrixUtils.printBufferedImage(image, dashboard);
				MatrixUtils.adaptiveThreshold(matrix, false, 0, 0, false);
				MatrixUtils.grayScale(matrix, true, false);
				formatMetric(buffer, new ThresholdDensity(dashboard, matrix), df);
				formatMetric(buffer, new RasterBalance(dashboard, matrix), df);
				formatMetric(buffer, new RasterSymmetry(dashboard, matrix), df);
				buffer.append("\n");
				
				buffer.append("===== GRAYSCALE =====\n");
				
				matrix = MatrixUtils.printBufferedImage(image, dashboard);
				MatrixUtils.grayScale(matrix, true, false);
				formatMetric(buffer, new RasterBalance(dashboard, matrix), df);
				formatMetric(buffer, new RasterSymmetry(dashboard, matrix), df);
			}
		}
		
		return buffer.toString();
	}

}
