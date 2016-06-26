package cz.vutbr.fit.dash.analyses;

import java.awt.image.BufferedImage;
import java.text.DecimalFormat;

import cz.vutbr.fit.dash.metric.Colorfulness;
import cz.vutbr.fit.dash.model.Dashboard;
import cz.vutbr.fit.dash.util.MatrixUtils;
import cz.vutbr.fit.dash.util.MatrixUtils.ColorChannel.ColorChannelType;
import cz.vutbr.fit.dash.util.MatrixUtils.HSB;
import cz.vutbr.fit.dash.util.MatrixUtils.LCH;

public class ColorfulnessAnalysis extends AbstractAnalysis implements IAnalysis {

	public ColorfulnessAnalysis(Dashboard dashboard) {
		super(dashboard);
	}

	@Override
	public String getName() {
		return "Colorfulness analysis";
	}

	@Override
	public String analyse() {
		StringBuffer buffer = new StringBuffer();
		
		if(dashboard != null) {
			BufferedImage image = dashboard.getImage();
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
