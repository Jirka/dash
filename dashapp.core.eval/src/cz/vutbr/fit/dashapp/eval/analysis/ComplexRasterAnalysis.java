package cz.vutbr.fit.dashapp.eval.analysis;

import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.util.Map;

import cz.vutbr.fit.dashapp.model.Dashboard;
import cz.vutbr.fit.dashapp.util.MatrixUtils;
import cz.vutbr.fit.dashapp.util.MatrixUtils.ColorChannel.ColorChannelType;
import cz.vutbr.fit.dashapp.eval.metric.BackgroundShare;
import cz.vutbr.fit.dashapp.eval.metric.ColorShare;
import cz.vutbr.fit.dashapp.eval.metric.Colorfulness;
import cz.vutbr.fit.dashapp.eval.metric.IntensitiesCount;
import cz.vutbr.fit.dashapp.eval.metric.RasterBalance;
import cz.vutbr.fit.dashapp.eval.metric.RasterSymmetry;
import cz.vutbr.fit.dashapp.eval.metric.ThresholdDensity;
import cz.vutbr.fit.dashapp.util.MatrixUtils.HSB;
import cz.vutbr.fit.dashapp.util.MatrixUtils.LCH;

public class ComplexRasterAnalysis extends AbstractAnalysis implements IAnalysis {

	public ComplexRasterAnalysis(Dashboard dashboard) {
		super(dashboard);
	}

	@Override
	public String getName() {
		return "Complex raster analysis";
	}

	@Override
	public String analyse() {
		// HSB		HSB h mean	HSB h stdev	HSB h	HSB s mean	HSB s stdev	HSB s	HSB b mean	HSB b stdev	HSB b		
		// CIE Lab	CIE L mean	CIE L stdev	CIE L	CIE a mean	CIE a stdev	CIE a	CIE b mean	CIE b stdev	CIE b	CIE c mean	CIE c stdev	CIE c	CIE h mean	CIE h stdev	CIE h	LCH colorf.	LCH mean	LCH std var		
		// RGB 3*8	Color count	% used clr	% 1.st	% 2.nd	% 1.+2.		
		// RGB 3*4	Color count	% used clr	% used clr	% 1.st	% 2.nd	% 1.+2.	1. st color		
		// gray 8	Color count	Color count	Color count	Color count	Color count	Color count	% used clr	% 1.st	% 2.nd	% 1.+2.	balance	H balance	V balance	symmetry	H balance	V balance		
		// gray 4	Color count	Color count	Color count	Color count	Color count	Color count	% used clr	% used clr	% 1.st	% 2.nd	% 1.+2.		
		// BW 1		% black	balance	H balance	V balance	symmetry	H balance	V balance
		StringBuffer buffer = new StringBuffer();
		
		if(dashboard != null) {
			BufferedImage image = dashboard.getImage();
			if(image != null) {
				DecimalFormat df = new DecimalFormat("#.#####");
				int[][] matrix = MatrixUtils.printBufferedImage(image, dashboard);
				//MatrixUtils.posterizeMatrix(matrix, 256/(int)(Math.pow(2, 4)));
				
				// HSB
				HSB matrixHSB[][] = MatrixUtils.RGBtoHSB(matrix);
				Colorfulness colorfunessMetric = new Colorfulness(dashboard, matrixHSB, null);
				appendValue(buffer, df, colorfunessMetric.measure(ColorChannelType.HUE), 0, true);
				appendValue(buffer, df, colorfunessMetric.measure(ColorChannelType.SATURATION), 1, true);
				appendValue(buffer, df, colorfunessMetric.measure(ColorChannelType.BRIGHTNESS), 1, true);
				
				// CIE Lab/Lch
				LCH matrixLCH[][] = MatrixUtils.RGBtoLCH(matrix);
				colorfunessMetric = new Colorfulness(dashboard, matrixLCH, null);
				appendValue(buffer, df, colorfunessMetric.measure(ColorChannelType.LIGHTNESS), 2, true);
				appendValue(buffer, df, colorfunessMetric.measure(ColorChannelType.A), 1, true);
				appendValue(buffer, df, colorfunessMetric.measure(ColorChannelType.B), 1, true);
				appendValue(buffer, df, colorfunessMetric.measure(ColorChannelType.CHROMA), 1, true);
				appendValue(buffer, df, colorfunessMetric.measure(ColorChannelType.HUE), 1, true);
				appendValue(buffer, df, colorfunessMetric.measure(ColorChannelType.SATURATION), 1, true);
				
				// RGB 3*8 and 3*4 bit
				ColorShare colorShare = new ColorShare(dashboard, matrix);
				appendValue(buffer, df, colorShare.measure(), 2, false);
				appendValue(buffer, df, colorShare.measure(4), 2, false);
				
				// Gray
				int matrixGray[][] = MatrixUtils.grayScale(matrix, false, true);
				// 8 bit
				int matrixGrayValue[][] = MatrixUtils.grayScaleToValues(matrixGray, true);
				int histogram[] = MatrixUtils.getGrayscaleHistogram(matrixGrayValue);
				appendValue(buffer, df, (new IntensitiesCount(dashboard, histogram)).measure(), 2, false);
				appendValue(buffer, df, (new BackgroundShare(dashboard, histogram)).measure(), 1, true);
				appendValue(buffer, df, (new RasterBalance(dashboard, matrixGrayValue)).measure(), 2, true);
				appendValue(buffer, df, (new RasterSymmetry(dashboard, matrixGrayValue)).measure(), 1, true);
				
				// 4 bit
				matrixGrayValue = MatrixUtils.grayScaleToValues(MatrixUtils.posterizeMatrix(matrixGray, (int)(Math.pow(2, 4)), true), false);
				histogram = MatrixUtils.getGrayscaleHistogram(matrixGrayValue);
				appendValue(buffer, df, (new IntensitiesCount(dashboard, histogram)).measure(), 2, false);
				appendValue(buffer, df, (new BackgroundShare(dashboard, histogram)).measure(), 1, true);
				appendValue(buffer, df, (new RasterBalance(dashboard, matrixGrayValue)).measure(), 2, true);
				appendValue(buffer, df, (new RasterSymmetry(dashboard, matrixGrayValue)).measure(), 1, true);
				
				// BW
				int matrixBW[][] = MatrixUtils.grayScale(MatrixUtils.adaptiveThreshold(matrix, false, 0, 0, true), true, false);
				appendValue(buffer, df, (new ThresholdDensity(dashboard, matrixBW)).measure(), 2, false);
				appendValue(buffer, df, (new RasterBalance(dashboard, matrixBW)).measure(), 2, true);
				appendValue(buffer, df, (new RasterSymmetry(dashboard, matrixBW)).measure(), 1, true);
			}
		}
		
		return buffer.toString();
	}
	
	private void appendValue(StringBuffer buffer, DecimalFormat df, Object value, int tabNum, boolean rolFirst) {
		for (int i = 0; i < tabNum; i++) {
			buffer.append('\t');
		}
		if(value instanceof Double) {
			buffer.append(df.format(value));
		} else if(value instanceof Object[]) {
			Object[] mm = (Object[]) value;
			int i = 0;
			Object first = 0;
			for (Object d : mm) {
				if(rolFirst && i == 0) {
					first = d;
				} else {
					if((i > 0 && !rolFirst) || i > 1) {
						buffer.append('\t');
					}
					if(d instanceof Double) {
						buffer.append(df.format((double) d));
					} else {
						buffer.append(d.toString());
					}
				}
				i++;
			}
			if (rolFirst) {
				buffer.append('\t');
				buffer.append(df.format(first));
			}
		}
	}

}