package cz.vutbr.fit.dashapp.eval.analysis;

import java.awt.image.BufferedImage;
import java.text.DecimalFormat;

import cz.vutbr.fit.dashapp.model.DashboardFile;
import cz.vutbr.fit.dashapp.util.MatrixUtils;
import cz.vutbr.fit.dashapp.util.MatrixUtils.ColorChannel.ColorChannelType;
import cz.vutbr.fit.dashapp.eval.metric.Colorfulness;
import cz.vutbr.fit.dashapp.util.MatrixUtils.HSB;
import cz.vutbr.fit.dashapp.util.MatrixUtils.LCH;

public class ColorfulnessAnalysis extends AbstractAnalysis implements IAnalysis {

	public ColorfulnessAnalysis(DashboardFile dashboardFile) {
		super(dashboardFile);
	}

	@Override
	public String getName() {
		return "Colorfulness analysis";
	}

	@Override
	public String analyse() {
		StringBuffer buffer = new StringBuffer();
		
		if(dashboardFile != null) {
			BufferedImage image = dashboardFile.getImage();
			if(image != null) {
				DecimalFormat df = new DecimalFormat("#.#####");
				int[][] matrix = MatrixUtils.printBufferedImage(image, dashboard);
				//MatrixUtils.posterizeMatrix(matrix, 256/(int)(Math.pow(2, 4)));
				
				buffer.append("====== COLORFULNESS ANALYSIS ======\n");
				buffer.append("\n");
				
				buffer.append("===== HSB COLORFULNESS =====\n");
				HSB matrixHSB[][] = MatrixUtils.RGBtoHSB(matrix);
				formatMetric(buffer, new Colorfulness(dashboard, matrixHSB, ColorChannelType.SATURATION), df);
				buffer.append("\n");
				
				buffer.append("===== CIE Lch COLORFULNESS =====\n");
				LCH matrixLCH[][] = MatrixUtils.RGBtoLCH(matrix);
				formatMetric(buffer, new Colorfulness(dashboard, matrixLCH, ColorChannelType.SATURATION), df);
			}
		}
		
		return buffer.toString();
	}

}
