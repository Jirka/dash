package cz.vutbr.fit.dashapp.eval.analysis.old;

import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import cz.vutbr.fit.dashapp.eval.metric.raster.gray.GrayBalance;
import cz.vutbr.fit.dashapp.eval.metric.raster.gray.GraySymmetry;
import cz.vutbr.fit.dashapp.eval.metric.raster.gray.histogram.BackgroundShare;
import cz.vutbr.fit.dashapp.eval.metric.raster.gray.histogram.IntensitiesCount;
import cz.vutbr.fit.dashapp.eval.metric.widget.basic.Area;
import cz.vutbr.fit.dashapp.eval.metric.widget.ngo.NgoCohesion;
import cz.vutbr.fit.dashapp.eval.metric.widget.ngo.NgoProportion;
import cz.vutbr.fit.dashapp.eval.metric.widget.ngo.NgoRegularity;
import cz.vutbr.fit.dashapp.eval.metric.MetricResult;
import cz.vutbr.fit.dashapp.eval.metric.raster.color.ColorShare;
import cz.vutbr.fit.dashapp.eval.metric.raster.color.Colorfulness;
import cz.vutbr.fit.dashapp.eval.metric.raster.gray.BlackDensity;
import cz.vutbr.fit.dashapp.model.Dashboard;
import cz.vutbr.fit.dashapp.model.DashboardFile;
import cz.vutbr.fit.dashapp.model.GraphicalElement;
import cz.vutbr.fit.dashapp.model.GraphicalElement.GEType;
import cz.vutbr.fit.dashapp.util.MatrixUtils;
import cz.vutbr.fit.dashapp.util.MatrixUtils.ColorChannel.ColorChannelType;
import cz.vutbr.fit.dashapp.util.MatrixUtils.HSB;
import cz.vutbr.fit.dashapp.util.MatrixUtils.LCH;

public class ComplexWidgetAnalysis extends AbstractAnalysis implements IAnalysis {

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
		DecimalFormat df = new DecimalFormat("#.#####");
		
		Dashboard dashboard = dashboardFile.getDashboard(true);
		if(dashboard != null) {
			// TODO Metric API changed (it will not work)
			GEType[] chartType = new GEType[] { GEType.CHART };
			appendValue(buffer, df, new NgoCohesion().measure(dashboard, null), 0, false);
			appendValue(buffer, df, new NgoCohesion().measure(dashboard, new GEType[] { GEType.CHART }), 1, false);
			
			appendValue(buffer, df, new NgoProportion().measure(dashboard, null), 2, false);
			appendValue(buffer, df, new NgoProportion().measure(dashboard, new GEType[] { GEType.CHART }), 1, false);
			
			appendValue(buffer, df, new NgoRegularity().measure(dashboard, null), 2, false);
			appendValue(buffer, df, new NgoRegularity().measure(dashboard, new GEType[] { GEType.CHART }), 1, false);
			
			appendValue(buffer, df, new Area().measure(dashboard, new GEType[] { GEType.BUTTON, GEType.HEADER, GEType.TOOLBAR, GEType.DECORATION } ), 2, false);
			appendValue(buffer, df, new Area().measure(dashboard, new GEType[] { GEType.BUTTON, GEType.DECORATION, GEType.HEADER, GEType.TOOLBAR, GEType.LABEL } ), 1, false);
			
			BufferedImage image = dashboardFile.getImage();
			if(image != null) {
				List<GraphicalElement> elements = dashboard.getChildren(chartType);
				List<Double> hsbSaturation = new ArrayList<>();
				List<Double> lchSaturation = new ArrayList<>();
				List<Double> rgb12amount = new ArrayList<>();
				List<Double> rgb12first = new ArrayList<>();
				List<Double> rgb12firstSecond = new ArrayList<>();
				List<Double> rgb4amount = new ArrayList<>();
				List<Double> rgb4amount10 = new ArrayList<>();
				List<Double> rgb4amount5 = new ArrayList<>();
				List<Double> rgb4amount1 = new ArrayList<>();
				List<Double> rgb4first = new ArrayList<>();
				List<Double> rgb4firstSecond = new ArrayList<>();
				List<Double> rgb4Balance = new ArrayList<>();
				List<Double> rgb4Symmetry = new ArrayList<>();
				List<Double> bwBlack = new ArrayList<>();
				for (GraphicalElement ge : elements) {
					int[][] matrix = MatrixUtils.printBufferedImage(image, ge);
					//MatrixUtils.posterizeMatrix(matrix, 256/(int)(Math.pow(2, 4)));
					
					// HSB
					HSB matrixHSB[][] = MatrixUtils.RGBtoHSB(matrix);
					Colorfulness colorfunessMetric = new Colorfulness();
					hsbSaturation.add(new Double((double) (colorfunessMetric.measure(matrixHSB, ColorChannelType.SATURATION))[0].value));
					
					// CIE Lab/Lch
					LCH matrixLCH[][] = MatrixUtils.RGBtoLCH(matrix);
					lchSaturation.add(new Double((double) (colorfunessMetric.measure(matrixLCH, ColorChannelType.SATURATION))[0].value));
					
					// RGB 12 bit
					ColorShare colorShare = new ColorShare();
					MetricResult[] result = colorShare.measure(matrix, 4);
					rgb12amount.add(new Double((long) result[0].value));
					rgb12first.add(new Double((double) result[3].value));
					rgb12firstSecond.add(new Double((double) result[7].value));
					
					// Gray 4 bit
					int matrixGray[][] = MatrixUtils.grayScale(matrix, false, true);
					int matrixGrayValue[][] = MatrixUtils.grayScaleToValues(MatrixUtils.posterizeMatrix(matrixGray, (int)(Math.pow(2, 4)), true), false);
					int histogram[] = MatrixUtils.getGrayscaleHistogram(matrixGrayValue);
					
					result = (new IntensitiesCount()).measureGrayHistogram(histogram);
					rgb4amount.add(new Double((int) result[0].value));
					rgb4amount10.add(new Double((int) result[2].value));
					rgb4amount5.add(new Double((int) result[3].value));
					rgb4amount1.add(new Double((int) result[4].value));
					
					result = (new BackgroundShare()).measureGrayHistogram(histogram);
					rgb4firstSecond.add(new Double((double) result[0].value));
					rgb4first.add(new Double((double) result[1].value));
					
					result = (new GrayBalance()).measureGrayMatrix(matrixGrayValue);
					rgb4Balance.add(new Double((double) result[0].value));
					
					result = (new GraySymmetry()).measureGrayMatrix(matrixGrayValue);
					rgb4Symmetry.add(new Double((double) result[0].value));
					
					// BW 1 bit
					int matrixBW[][] = MatrixUtils.grayScale(MatrixUtils.adaptiveThreshold(matrix, false, 0, 0, true), true, false);
					bwBlack.add(new Double((double) (new BlackDensity()).measureGrayMatrix(matrixBW)[0].value));
				}
				
				appendValue(buffer, df, getStdDev(hsbSaturation), 2, false);
				appendValue(buffer, df, getStdDev(lchSaturation), 2, false);
				appendValue(buffer, df, getStdDev(rgb12amount), 2, false);
				appendValue(buffer, df, getStdDev(rgb12first), 2, false);
				appendValue(buffer, df, getStdDev(rgb12firstSecond), 2, false);
				appendValue(buffer, df, getStdDev(rgb4amount), 2, false);
				appendValue(buffer, df, getStdDev(rgb4amount10), 2, false);
				appendValue(buffer, df, getStdDev(rgb4amount5), 2, false);
				appendValue(buffer, df, getStdDev(rgb4amount1), 2, false);
				appendValue(buffer, df, getStdDev(rgb4first), 2, false);
				appendValue(buffer, df, getStdDev(rgb4firstSecond), 2, false);
				appendValue(buffer, df, getStdDev(rgb4Balance), 2, false);
				appendValue(buffer, df, getStdDev(rgb4Symmetry), 2, false);
				appendValue(buffer, df, getStdDev(bwBlack), 2, false);
			}
		}
		
		return buffer.toString();
	}

    double getMean(List<Double> values) {
        double sum = 0.0;
        for(double a : values)
            sum += a;
        return sum/values.size();
    }
	
	Double[] getVariance(List<Double> values) {
        double mean = getMean(values);
        double temp = 0;
        for(double a : values)
            temp += (mean-a)*(mean-a);
        return new Double[] { mean, (temp/values.size()) };
    }
	
	Double[] getStdDev(List<Double> values) {
		Double[] result = getVariance(values);
		result[1] = Math.sqrt(result[1]);
        return result;
    }
	
	private void appendValue(StringBuffer buffer, DecimalFormat df, Object value, int tabNum, boolean rolFirst) {
		for (int i = 0; i < tabNum; i++) {
			buffer.append('\t');
		}
		if(value instanceof MetricResult[]) {
			MetricResult[] values = (MetricResult[]) value;
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
		} else if(value instanceof Double) {
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
