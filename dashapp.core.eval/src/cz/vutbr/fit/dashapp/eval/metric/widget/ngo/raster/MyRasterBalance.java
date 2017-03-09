package cz.vutbr.fit.dashapp.eval.metric.widget.ngo.raster;

import java.util.List;

import cz.vutbr.fit.dashapp.eval.metric.MetricResult;
import cz.vutbr.fit.dashapp.eval.metric.widget.AbstractWidgetMetric;
import cz.vutbr.fit.dashapp.image.GrayMatrix;
import cz.vutbr.fit.dashapp.model.Constants;
import cz.vutbr.fit.dashapp.model.Constants.Side;
import cz.vutbr.fit.dashapp.model.Dashboard;
import cz.vutbr.fit.dashapp.model.GraphicalElement;
import cz.vutbr.fit.dashapp.model.GraphicalElement.GEType;
import cz.vutbr.fit.dashapp.model.IDashboardFile;
import cz.vutbr.fit.dashapp.util.MatrixUtils;
import cz.vutbr.fit.dashapp.util.MatrixUtils.ColorChannel;
import cz.vutbr.fit.dashapp.util.MatrixUtils.ColorChannel.ColorChannelType;
import cz.vutbr.fit.dashapp.util.MatrixUtils.Gray;
import cz.vutbr.fit.dashapp.util.MatrixUtils.HSB;
import cz.vutbr.fit.dashapp.util.MatrixUtils.LCH;

public abstract class MyRasterBalance extends AbstractWidgetMetric {
	
	int i = 0;
	
	public double getBalance(Dashboard dashboard, GEType[] types, ColorChannel[][] image, int dimension) {
		Side s1, s2;
		if(dimension == Constants.X) {
			s1 = Side.LEFT;
			s2 = Side.RIGHT;
		} else {
			s1 = Side.UP;
			s2 = Side.DOWN;
		}
		
		// initialize weights
		double weight1 = 0.0;
		double weight2 = 0.0;
		// count weights
		List<GraphicalElement> ges = dashboard.getChildren(types);
		double a;
		for (GraphicalElement ge : ges) {
			// one side
			a = ge.area(s1);
			if(a > 0) {
				weight1 += a*ge.depth(s1)*getRatio(image, ge, s1);
			}
			// second side
			a = ge.area(s2);
			if(a > 0) {
				weight2 += a*ge.depth(s2)*getRatio(image, ge, s2);
			}
		}
		// return balance for particular dimension
		double b = Math.max(Math.abs(weight1), Math.abs(weight2));
		if(b == 0) {
			// there are no objects to compare -> balanced
			return 0.0;
		}
		return (weight1-weight2)/b;
	}

	protected abstract double getRatio(ColorChannel[][] matrix, GraphicalElement ge, Side s);

	@Override
	public MetricResult[] measure(Dashboard dashboard, GEType[] types) {
		IDashboardFile dashboardFile = dashboard.getDashboardFile();
		if(dashboardFile != null) {
			ColorChannel[][] imageMatrix = prepareImage(dashboardFile);
			double BM_V = getBalance(dashboard, types, imageMatrix, Constants.X);
			//double BM_V2 = getBalance2(dashboard, types, image, Constants.X);
			double BM_H = getBalance(dashboard, types, imageMatrix, Constants.Y);
			//double BM_H2 = getBalance2(dashboard, types, image, Constants.X);
			return new MetricResult[] {
					new MetricResult("Balance", "BM", 1-(Math.abs(BM_V)+Math.abs(BM_H))/2.0),
					new MetricResult("Vertical Balance", "BM_v", BM_V),
					new MetricResult("Horizontal Balance", "BM_h", BM_H)
			};
		}
		return EMPTY_RESULT;
	}
	
	protected abstract ColorChannel[][] prepareImage(IDashboardFile dashboardFile);

	public static class MyIntensityBalance extends MyRasterBalance {

		@Override
		protected double getRatio(ColorChannel[][] matrix, GraphicalElement ge, Side s) {
			ColorChannel[][] cropMatrix = MatrixUtils.cropMatrix(matrix, ge.getRectangle(s));
			return (GrayMatrix.WHITE-MatrixUtils.getColorChannelMean(cropMatrix, ColorChannelType.GRAY))/GrayMatrix.WHITE;
		}

		@Override
		protected ColorChannel[][] prepareImage(IDashboardFile dashboardFile) {
			Object value = dashboardFile.getCachedObject(Gray.class);
			Gray[][] imageMatrixGray = null;
			if(value != null && value instanceof Gray[][]) {
				imageMatrixGray = (Gray[][]) value;
			} else {
				int[][] imageMatrix = dashboardFile.getImageMatrix();
				if(imageMatrix != null) {
					imageMatrixGray = MatrixUtils.RGBtoGray(imageMatrix);
					dashboardFile.putIntoCache(Gray.class, imageMatrixGray);
				}
			}
			return imageMatrixGray;
		}
	}
	
	public static class MyColorfulnessBalance extends MyRasterBalance {
		
		@Override
		protected ColorChannel[][] prepareImage(IDashboardFile dashboardFile) {
			Object value = dashboardFile.getCachedObject(LCH.class);
			LCH[][] imageMatrixLCH = null;
			if(value != null && value instanceof LCH[][]) {
				imageMatrixLCH = (LCH[][]) value;
			} else {
				int[][] imageMatrix = dashboardFile.getImageMatrix();
				if(imageMatrix != null) {
					imageMatrixLCH = MatrixUtils.RGBtoLCH(imageMatrix);
					dashboardFile.putIntoCache(LCH.class, imageMatrixLCH);
				}
			}
			return imageMatrixLCH;
		}
		
		@Override
		protected double getRatio(ColorChannel[][] matrix, GraphicalElement ge, Side s) {
			ColorChannel cropMatrix[][] = MatrixUtils.cropMatrix(matrix, ge.getRectangle(s));
			double mean = MatrixUtils.getColorChannelMean(cropMatrix, ColorChannelType.SATURATION);
			double stdDev = MatrixUtils.getColorChannelStdDev(cropMatrix, mean, ColorChannelType.SATURATION);
			return mean+stdDev;
		}
	}
	
	public static class MyHSBBalance extends MyRasterBalance {
		
		@Override
		protected ColorChannel[][] prepareImage(IDashboardFile dashboardFile) {
			Object value = dashboardFile.getCachedObject(HSB.class);
			HSB[][] imageMatrixLCH = null;
			if(value != null && value instanceof LCH[][]) {
				imageMatrixLCH = (HSB[][]) value;
			} else {
				int[][] imageMatrix = dashboardFile.getImageMatrix();
				if(imageMatrix != null) {
					imageMatrixLCH = MatrixUtils.RGBtoHSB(imageMatrix);
					dashboardFile.putIntoCache(LCH.class, imageMatrixLCH);
				}
			}
			return imageMatrixLCH;
		}

		@Override
		protected double getRatio(ColorChannel[][] matrix, GraphicalElement ge, Side s) {
			ColorChannel cropMatrix[][] = MatrixUtils.cropMatrix(matrix, ge.getRectangle(s));
			//double meanH = MatrixUtils.getColorChannelMean(cropMatrix, ColorChannelType.HUE);
			//double stdDevH = MatrixUtils.getColorChannelStdDev(cropMatrix, meanH, ColorChannelType.HUE);
			double meanS = MatrixUtils.getColorChannelMean(cropMatrix, ColorChannelType.SATURATION);
			//double stdDevS = MatrixUtils.getColorChannelStdDev(cropMatrix, meanS, ColorChannelType.SATURATION);
			double meanB = MatrixUtils.getColorChannelMean(cropMatrix, ColorChannelType.BRIGHTNESS);
			//double stdDevB = MatrixUtils.getColorChannelStdDev(cropMatrix, meanB, ColorChannelType.BRIGHTNESS);
			return meanS+meanB;
		}		
	}
	
	public static class MyHSBBalance2 extends MyHSBBalance {

		@Override
		protected double getRatio(ColorChannel[][] matrix, GraphicalElement ge, Side s) {
			ColorChannel cropMatrix[][] = MatrixUtils.cropMatrix(matrix, ge.getRectangle(s));
			//double meanH = MatrixUtils.getColorChannelMean(cropMatrix, ColorChannelType.HUE);
			//double stdDevH = MatrixUtils.getColorChannelStdDev(cropMatrix, meanH, ColorChannelType.HUE);
			double meanS = MatrixUtils.getColorChannelMean(cropMatrix, ColorChannelType.SATURATION);
			//double stdDevS = MatrixUtils.getColorChannelStdDev(cropMatrix, meanS, ColorChannelType.SATURATION);
			double meanB = MatrixUtils.getColorChannelMean(cropMatrix, ColorChannelType.BRIGHTNESS);
			//double stdDevB = MatrixUtils.getColorChannelStdDev(cropMatrix, meanB, ColorChannelType.BRIGHTNESS);
			return meanS*Math.abs(meanB-0.5);
		}		
	}
	
	public static class MyHSBBalance4 extends MyHSBBalance {

		@Override
		protected double getRatio(ColorChannel[][] matrix, GraphicalElement ge, Side s) {
			ColorChannel cropMatrix[][] = MatrixUtils.cropMatrix(matrix, ge.getRectangle(s));
			//double meanH = MatrixUtils.getColorChannelMean(cropMatrix, ColorChannelType.HUE);
			//double stdDevH = MatrixUtils.getColorChannelStdDev(cropMatrix, meanH, ColorChannelType.HUE);
			double meanS = MatrixUtils.getColorChannelMean(cropMatrix, ColorChannelType.SATURATION);
			//double stdDevS = MatrixUtils.getColorChannelStdDev(cropMatrix, meanS, ColorChannelType.SATURATION);
			double meanB = MatrixUtils.getColorChannelMean(cropMatrix, ColorChannelType.BRIGHTNESS);
			if(meanB == 0.0 || meanS == 0.0 || meanB == 1.0) {
				return 0.0;
			}
			//double stdDevB = MatrixUtils.getColorChannelStdDev(cropMatrix, meanB, ColorChannelType.BRIGHTNESS);
			return meanS*(-(meanB*Math.log(meanB)+(1-meanB)*Math.log(1-meanB)));
		}		
	}
	
	public static class MyBalance extends MyRasterBalance {

		@Override
		protected double getRatio(ColorChannel[][] matrix, GraphicalElement ge, Side s) {
			return 1.0;
		}

		@Override
		protected ColorChannel[][] prepareImage(IDashboardFile dashboardFile) {
			// TODO Auto-generated method stub
			return null;
		}		
	}

}
