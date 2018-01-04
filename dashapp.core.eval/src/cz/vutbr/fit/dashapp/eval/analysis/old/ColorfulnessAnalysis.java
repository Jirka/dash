package cz.vutbr.fit.dashapp.eval.analysis.old;

import java.awt.image.BufferedImage;
import java.text.DecimalFormat;

import cz.vutbr.fit.dashapp.eval.metric.raster.color.Colorfulness;
import cz.vutbr.fit.dashapp.image.colorspace.CIE;
import cz.vutbr.fit.dashapp.image.colorspace.HSB;
import cz.vutbr.fit.dashapp.model.DashboardFile;
import cz.vutbr.fit.dashapp.util.matrix.ColorMatrix;

public class ColorfulnessAnalysis extends AbstractAnalysis implements IAnalysis {

	@Override
	public String getName() {
		return "Colorfulness analysis";
	}

	@Override
	public String analyze(DashboardFile dashboardFile) {
		StringBuffer buffer = new StringBuffer();
		
		if(dashboardFile != null) {
			BufferedImage image = dashboardFile.getImage();
			if(image != null) {
				DecimalFormat df = new DecimalFormat("#.#####");
				int[][] matrix = ColorMatrix.printImageToMatrix(image, dashboardFile.getDashboard(true));
				//MatrixUtils.posterizeMatrix(matrix, 256/(int)(Math.pow(2, 4)));
				
				buffer.append("====== COLORFULNESS ANALYSIS ======\n");
				buffer.append("\n");
				
				buffer.append("===== HSB COLORFULNESS =====\n");
				HSB matrixHSB[][] = HSB.fromRGB(matrix);
				formatMetric(buffer, new Colorfulness(HSB.CHANNEL_SATURATION).measure(matrixHSB), df);
				buffer.append("\n");
				
				buffer.append("===== CIE Lch COLORFULNESS =====\n");
				CIE matrixLCH[][] = CIE.fromRGB(matrix);
				formatMetric(buffer, new Colorfulness(CIE.CHANNEL_SATURATION).measure(matrixLCH), df);
			}
		}
		
		return buffer.toString();
	}

}
