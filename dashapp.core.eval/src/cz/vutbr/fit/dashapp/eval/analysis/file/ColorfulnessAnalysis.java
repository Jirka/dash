package cz.vutbr.fit.dashapp.eval.analysis.file;

import java.awt.image.BufferedImage;
import java.text.DecimalFormat;

import cz.vutbr.fit.dashapp.eval.analysis.AbstractFileAnalysis;
import cz.vutbr.fit.dashapp.eval.analysis.IFileAnalysis;
import cz.vutbr.fit.dashapp.eval.metric.raster.color.Colorfulness;
import cz.vutbr.fit.dashapp.image.colorspace.CIE;
import cz.vutbr.fit.dashapp.image.colorspace.HSB;
import cz.vutbr.fit.dashapp.model.DashboardFile;
import cz.vutbr.fit.dashapp.util.matrix.ColorMatrix;

/**
 * 
 * @author Jiri Hynek
 *
 */
public class ColorfulnessAnalysis extends AbstractFileAnalysis implements IFileAnalysis {

	@Override
	public String getLabel() {
		return "Colorfulness analysis";
	}

	@Override
	public String processFile(DashboardFile dashboardFile) {
		StringBuffer buffer = new StringBuffer();
		
		if(dashboardFile != null) {
			BufferedImage image = dashboardFile.getImage();
			if(image != null) {
				final DecimalFormat df = new DecimalFormat("#.#####");
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
