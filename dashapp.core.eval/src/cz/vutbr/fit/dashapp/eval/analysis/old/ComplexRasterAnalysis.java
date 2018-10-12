package cz.vutbr.fit.dashapp.eval.analysis.old;

import java.awt.image.BufferedImage;
import java.text.DecimalFormat;

import cz.vutbr.fit.dashapp.eval.metric.raster.gray.GrayBalance;
import cz.vutbr.fit.dashapp.eval.metric.raster.gray.GraySymmetry;
import cz.vutbr.fit.dashapp.eval.metric.raster.gray.histogram.HistogramBackgroundShare;
import cz.vutbr.fit.dashapp.eval.metric.raster.gray.histogram.HistogramIntensitiesCount;
import cz.vutbr.fit.dashapp.image.colorspace.CIE;
import cz.vutbr.fit.dashapp.image.colorspace.HSB;
import cz.vutbr.fit.dashapp.image.util.AdaptiveThresholdUtils;
import cz.vutbr.fit.dashapp.image.util.HistogramUtils;
import cz.vutbr.fit.dashapp.image.util.PosterizationUtils;
import cz.vutbr.fit.dashapp.eval.metric.MetricResult;
import cz.vutbr.fit.dashapp.eval.metric.raster.ColorShare;
import cz.vutbr.fit.dashapp.eval.metric.raster.color.Colorfulness;
import cz.vutbr.fit.dashapp.eval.metric.raster.gray.BlackDensity;
import cz.vutbr.fit.dashapp.model.Dashboard;
import cz.vutbr.fit.dashapp.model.DashboardFile;
import cz.vutbr.fit.dashapp.util.matrix.ColorMatrix;

public class ComplexRasterAnalysis extends AbstractAnalysis implements IAnalysis {

	@Override
	public String getName() {
		return "Complex raster analysis";
	}

	@Override
	public String analyze(DashboardFile dashboardFile) {
		// HSB		HSB h mean	HSB h stdev	HSB h	HSB s mean	HSB s stdev	HSB s	HSB b mean	HSB b stdev	HSB b		
		// CIE Lab	CIE L mean	CIE L stdev	CIE L	CIE a mean	CIE a stdev	CIE a	CIE b mean	CIE b stdev	CIE b	CIE c mean	CIE c stdev	CIE c	CIE h mean	CIE h stdev	CIE h	LCH colorf.	LCH mean	LCH std var		
		// RGB 3*8	Color count	% used clr	% 1.st	% 2.nd	% 1.+2.		
		// RGB 3*4	Color count	% used clr	% used clr	% 1.st	% 2.nd	% 1.+2.	1. st color		
		// gray 8	Color count	Color count	Color count	Color count	Color count	Color count	% used clr	% 1.st	% 2.nd	% 1.+2.	balance	H balance	V balance	symmetry	H balance	V balance		
		// gray 4	Color count	Color count	Color count	Color count	Color count	Color count	% used clr	% used clr	% 1.st	% 2.nd	% 1.+2.		
		// BW 1		% black	balance	H balance	V balance	symmetry	H balance	V balance
		StringBuffer buffer = new StringBuffer();
		
		if(dashboardFile != null) {
			BufferedImage image = dashboardFile.getImage();
			if(image != null) {
				DecimalFormat df = new DecimalFormat("#.#####");
				Dashboard dashboard = dashboardFile.getDashboard(true);
				int[][] matrix = ColorMatrix.printImageToMatrix(image, dashboard);
				//MatrixUtils.posterizeMatrix(matrix, 256/(int)(Math.pow(2, 4)));
				
				Colorfulness colorfunessMetric = new Colorfulness();
				// HSB
				HSB matrixHSB[][] = HSB.fromRGB(matrix);
				appendValue(buffer, df, colorfunessMetric.setColorChannel(HSB.CHANNEL_HUE).setColorSpaceClass(HSB.class).measure(matrixHSB), 0, true);
				appendValue(buffer, df, colorfunessMetric.setColorChannel(HSB.CHANNEL_SATURATION).measure(matrixHSB), 1, true);
				appendValue(buffer, df, colorfunessMetric.setColorChannel(HSB.CHANNEL_BRIGHTNESS).measure(matrixHSB), 1, true);
				
				// CIE Lab/Lch
				CIE matrixLCH[][] = CIE.fromRGB(matrix);
				appendValue(buffer, df, colorfunessMetric.setColorChannel(CIE.CHANNEL_LIGHTNESS).setColorSpaceClass(CIE.class).measure(matrixLCH), 2, true);
				appendValue(buffer, df, colorfunessMetric.setColorChannel(CIE.CHANNEL_A).measure(matrixLCH), 1, true);
				appendValue(buffer, df, colorfunessMetric.setColorChannel(CIE.CHANNEL_B).measure(matrixLCH), 1, true);
				appendValue(buffer, df, colorfunessMetric.setColorChannel(CIE.CHANNEL_CHROMA).measure(matrixLCH), 1, true);
				appendValue(buffer, df, colorfunessMetric.setColorChannel(CIE.CHANNEL_HUE).measure(matrixLCH), 1, true);
				appendValue(buffer, df, colorfunessMetric.setColorChannel(CIE.CHANNEL_SATURATION).measure(matrixLCH), 1, true);
				
				// RGB 3*8 and 3*4 bit
				ColorShare colorShare = new ColorShare();
				appendValue(buffer, df, colorShare.measure(matrix), 2, false);
				appendValue(buffer, df, colorShare.setPosterizationBitValue(4).measure(matrix), 2, false);
				
				// Gray
				int matrixGray[][] = ColorMatrix.toGrayScale(matrix, false, true);
				// 8 bit
				int matrixGrayValue[][] = ColorMatrix.toGrayScale(matrixGray, true, true);
				int histogram[] = HistogramUtils.getGrayscaleHistogram(matrixGrayValue);
				appendValue(buffer, df, (new HistogramIntensitiesCount()).measureGrayHistogram(histogram), 2, false);
				appendValue(buffer, df, (new HistogramBackgroundShare()).measureGrayHistogram(histogram), 1, true);
				appendValue(buffer, df, (new GrayBalance()).measureGrayMatrix(matrixGrayValue), 2, true);
				appendValue(buffer, df, (new GraySymmetry()).measureGrayMatrix(matrixGrayValue), 1, true);
				
				// 4 bit
				matrixGrayValue = ColorMatrix.toGrayScale(PosterizationUtils.posterizeMatrix(matrixGray, (int)(Math.pow(2, 4)), true), true, false);
				histogram = HistogramUtils.getGrayscaleHistogram(matrixGrayValue);
				appendValue(buffer, df, (new HistogramIntensitiesCount()).measureGrayHistogram(histogram), 2, false);
				appendValue(buffer, df, (new HistogramBackgroundShare()).measureGrayHistogram(histogram), 1, true);
				appendValue(buffer, df, (new GrayBalance()).measureGrayMatrix(matrixGrayValue), 2, true);
				appendValue(buffer, df, (new GraySymmetry()).measureGrayMatrix(matrixGrayValue), 1, true);
				
				// BW
				int matrixBW[][] = ColorMatrix.toGrayScale(AdaptiveThresholdUtils.adaptiveThreshold(matrix, false, 0, 0, true), true, false);
				appendValue(buffer, df, (new BlackDensity()).measureGrayMatrix(matrixBW), 2, false);
				appendValue(buffer, df, (new GrayBalance()).measureGrayMatrix(matrixBW), 2, true);
				appendValue(buffer, df, (new GraySymmetry()).measureGrayMatrix(matrixBW), 1, true);
			}
		}
		
		return buffer.toString();
	}
	
	private void appendValue(StringBuffer buffer, DecimalFormat df, MetricResult[] values, int tabNum, boolean rolFirst) {
		for (int i = 0; i < tabNum; i++) {
			buffer.append('\t');
		}
		if(values.length == 1) {
			if(values[0].value instanceof Double) {
				buffer.append(df.format(values[0].value));
			} else {
				buffer.append(values[0].value.toString());
			}
		} else if(values.length > 1) {
			int i = 0;
			Object first = 0;
			for (MetricResult dd : values) {
				Object d = dd.value;
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
