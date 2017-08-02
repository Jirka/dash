package cz.vutbr.fit.dashapp.eval.metric.widget.ngo.raster;

import java.awt.Rectangle;
import java.util.LinkedList;
import java.util.List;

import cz.vutbr.fit.dashapp.image.colorspace.CIE;
import cz.vutbr.fit.dashapp.image.colorspace.ColorChannelUtils;
import cz.vutbr.fit.dashapp.image.colorspace.ColorSpace;
import cz.vutbr.fit.dashapp.image.colorspace.Gray;
import cz.vutbr.fit.dashapp.image.colorspace.HSB;
import cz.vutbr.fit.dashapp.image.colorspace.HSL;
import cz.vutbr.fit.dashapp.image.util.PosterizationUtils;
import cz.vutbr.fit.dashapp.model.GraphicalElement;
import cz.vutbr.fit.dashapp.model.IDashboardFile;
import cz.vutbr.fit.dashapp.model.Constants.Quadrant;
import cz.vutbr.fit.dashapp.model.Constants.Side;
import cz.vutbr.fit.dashapp.util.MathUtils;
import cz.vutbr.fit.dashapp.util.matrix.GrayMatrix;

public abstract class RasterRatioCalculator {
	
	public double getRatio(ColorSpace[][] matrix, GraphicalElement ge, Object screenPart) {
		return getRatio(cropMatrix(matrix, ge, screenPart));
	}
	
	public double getRatio(ColorSpace[][] matrix, boolean[][] geMatrix) {
		return getRatio(cropMatrix(matrix, geMatrix));
	}
	
	public double getRatio(ColorSpace[][] matrix, Rectangle rectangle) {
		return getRatio(ColorChannelUtils.cropMatrix(matrix, rectangle));
	}
	
	protected double getRatio(ColorSpace[][] matrix) {
		if(matrix.length > 0) {
			if(matrix[0].length > 0) {
				return calculateRatio(matrix);
			}
		}
		return 0.0;
	}
	
	protected abstract double calculateRatio(ColorSpace[][] matrix);

	public abstract ColorSpace[][] prepareImage(IDashboardFile dashboardFile);
	
	public abstract double getMaxRatio();
	
	protected ColorSpace[][] cropMatrix(ColorSpace[][] matrix, GraphicalElement ge, Object screenPart) {
		if(screenPart != null) {
			if(screenPart instanceof Side) {
				return ColorChannelUtils.cropMatrix(matrix, ge.getRectangle((Side) screenPart));
			} else if(screenPart instanceof Quadrant) {
				return ColorChannelUtils.cropMatrix(matrix, ge.getRectangle((Quadrant) screenPart));
			}
		}
		return ColorChannelUtils.cropMatrix(matrix, new Rectangle(ge.x, ge.y, ge.width, ge.height));
	}
	
	protected ColorSpace[][] cropMatrix(ColorSpace[][] matrix, boolean[][] geMatrix) {
		if(matrix == null) {
			return new ColorSpace[0][0];
		}
		int mW = matrix.length;
		int mH = matrix[0].length;
		List<ColorSpace> pixels = new LinkedList<>();
		int size = 0;
		for (int i = 0; i < mW; i++) {
			for (int j = 0; j < mH; j++) {
				if(geMatrix[i][j]) {
					pixels.add(matrix[i][j]);
					size++;
				}
			}			
		}
		ColorSpace[][] result = new ColorSpace[1][];
		result[0] = pixels.toArray(new ColorSpace[size]);
		return result;
	}
	
	public static class DummyRatioCalculator extends RasterRatioCalculator {
		
		@Override
		public double getRatio(ColorSpace[][] matrix, GraphicalElement ge, Object screenPart) {
			return calculateRatio(null);
		}
		
		@Override
		public double getRatio(ColorSpace[][] matrix, Rectangle rectangle) {
			return calculateRatio(null);
		}
		
		@Override
		public double getRatio(ColorSpace[][] matrix, boolean[][] geMatrix) {
			return calculateRatio(null);
		}

		@Override
		public double calculateRatio(ColorSpace[][] matrix) {
			return 1.0;
		}

		@Override
		public ColorSpace[][] prepareImage(IDashboardFile dashboardFile) {
			return null;
		}

		@Override
		public double getMaxRatio() {
			return 1.0;
		}		
	}
	
	public static class IntensityRatioCalculator extends RasterRatioCalculator {

		@Override
		public double calculateRatio(ColorSpace[][] matrix) {
			return (GrayMatrix.WHITE-ColorChannelUtils.getColorChannelMean(matrix, Gray.CHANNEL_GRAY))/GrayMatrix.WHITE;
		}

		@Override
		public ColorSpace[][] prepareImage(IDashboardFile dashboardFile) {
			Object value = dashboardFile.getCachedObject(Gray.class);
			Gray[][] imageMatrixGray = null;
			if(value != null && value instanceof Gray[][]) {
				imageMatrixGray = (Gray[][]) value;
			} else {
				int[][] imageMatrix = dashboardFile.getImageMatrix();
				if(imageMatrix != null) {
					imageMatrixGray = Gray.fromRGB(imageMatrix);
					dashboardFile.putIntoCache(Gray.class, imageMatrixGray);
				}
			}
			return imageMatrixGray;
		}
		
		@Override
		public double getMaxRatio() {
			return 1.0;
		}
	}
	
	public static class PosterizedIntensityRatioCalculator extends RasterRatioCalculator {

		@Override
		public double calculateRatio(ColorSpace[][] matrix) {
			return (GrayMatrix.WHITE-ColorChannelUtils.getColorChannelMean(matrix, Gray.CHANNEL_GRAY))/GrayMatrix.WHITE;
		}

		@Override
		public ColorSpace[][] prepareImage(IDashboardFile dashboardFile) {
			Object value = dashboardFile.getCachedObject(Gray.class);
			Gray[][] imageMatrixGray = null;
			if(value != null && value instanceof Gray[][]) {
				imageMatrixGray = (Gray[][]) value;
			} else {
				int[][] imageMatrix = dashboardFile.getImageMatrix();
				if(imageMatrix != null) {
					imageMatrixGray = Gray.fromRGB(imageMatrix);
					PosterizationUtils.posterizeMatrix(imageMatrixGray, (int)(Math.pow(2, 4)), false);
					dashboardFile.putIntoCache(Gray.class, imageMatrixGray);
				}
			}
			return imageMatrixGray;
		}
		
		@Override
		public double getMaxRatio() {
			return 1.0;
		}
	}
	
	public static class ColorfulnessRatioCalculator extends RasterRatioCalculator {
		
		@Override
		public ColorSpace[][] prepareImage(IDashboardFile dashboardFile) {
			Object value = dashboardFile.getCachedObject(CIE.class);
			CIE[][] imageMatrixLCH = null;
			if(value != null && value instanceof CIE[][]) {
				imageMatrixLCH = (CIE[][]) value;
			} else {
				int[][] imageMatrix = dashboardFile.getImageMatrix();
				if(imageMatrix != null) {
					imageMatrixLCH = CIE.fromRGB(imageMatrix);
					dashboardFile.putIntoCache(CIE.class, imageMatrixLCH);
				}
			}
			return imageMatrixLCH;
		}
		
		@Override
		public double calculateRatio(ColorSpace[][] matrix) {
			double mean = ColorChannelUtils.getColorChannelMean(matrix, CIE.CHANNEL_SATURATION);
			double stdDev = ColorChannelUtils.getColorChannelStdDev(matrix, mean, CIE.CHANNEL_SATURATION);
			return mean+stdDev;
		}
		
		@Override
		public double getMaxRatio() {
			return Double.POSITIVE_INFINITY;
		}
	}
	
	public static abstract class HSBRatioCalculator extends RasterRatioCalculator {
		
		@Override
		public ColorSpace[][] prepareImage(IDashboardFile dashboardFile) {
			Object value = dashboardFile.getCachedObject(HSB.class);
			HSB[][] imageMatrixLCH = null;
			if(value != null && value instanceof HSB[][]) {
				imageMatrixLCH = (HSB[][]) value;
			} else {
				int[][] imageMatrix = dashboardFile.getImageMatrix();
				if(imageMatrix != null) {
					imageMatrixLCH = HSB.fromRGB(imageMatrix);
					dashboardFile.putIntoCache(HSB.class, imageMatrixLCH);
				}
			}
			return imageMatrixLCH;
		}
		
		@Override
		public double getMaxRatio() {
			return 1.0;
		}
	}
	
	public static class HSBRatioCalculator_sb extends HSBRatioCalculator {

		@Override
		public double calculateRatio(ColorSpace[][] matrix) {
			//double meanH = MatrixUtils.getColorChannelMean(cropMatrix, ColorChannelType.HUE);
			//double stdDevH = MatrixUtils.getColorChannelStdDev(cropMatrix, meanH, ColorChannelType.HUE);
			double meanS = ColorChannelUtils.getColorChannelMean(matrix, HSB.CHANNEL_SATURATION);
			//double stdDevS = MatrixUtils.getColorChannelStdDev(cropMatrix, meanS, ColorChannelType.SATURATION);
			double meanB = ColorChannelUtils.getColorChannelMean(matrix, HSB.CHANNEL_BRIGHTNESS);
			//double stdDevB = MatrixUtils.getColorChannelStdDev(cropMatrix, meanB, ColorChannelType.BRIGHTNESS);
			return meanS+meanB;
		}
		
		@Override
		public double getMaxRatio() {
			return 2.0;
		}
	}
	
	public static class HSBRatioCalculator_sb05 extends HSBRatioCalculator {

		@Override
		public double calculateRatio(ColorSpace[][] matrix) {
			//double meanH = MatrixUtils.getColorChannelMean(cropMatrix, ColorChannelType.HUE);
			//double stdDevH = MatrixUtils.getColorChannelStdDev(cropMatrix, meanH, ColorChannelType.HUE);
			double meanS = ColorChannelUtils.getColorChannelMean(matrix, HSB.CHANNEL_SATURATION);
			//double stdDevS = MatrixUtils.getColorChannelStdDev(cropMatrix, meanS, ColorChannelType.SATURATION);
			double meanB = ColorChannelUtils.getColorChannelMean(matrix, HSB.CHANNEL_BRIGHTNESS);
			//double stdDevB = MatrixUtils.getColorChannelStdDev(cropMatrix, meanB, ColorChannelType.BRIGHTNESS);
			return meanS*Math.abs(meanB-0.5);
		}
		
		@Override
		public double getMaxRatio() {
			return 0.5;
		}
	}
	
	public static class HSBRatioCalculator_sb05_max1 extends HSBRatioCalculator_sb05 {
		
		@Override
		public double getMaxRatio() {
			return 1.0;
		}
	}
	
	public static class HSBRatioCalculator_sblog extends HSBRatioCalculator {

		@Override
		public double calculateRatio(ColorSpace[][] matrix) {
			//double meanH = MatrixUtils.getColorChannelMean(cropMatrix, ColorChannelType.HUE);
			//double stdDevH = MatrixUtils.getColorChannelStdDev(cropMatrix, meanH, ColorChannelType.HUE);
			double meanS = ColorChannelUtils.getColorChannelMean(matrix, HSB.CHANNEL_SATURATION);
			//double stdDevS = MatrixUtils.getColorChannelStdDev(cropMatrix, meanS, ColorChannelType.SATURATION);
			double meanB = ColorChannelUtils.getColorChannelMean(matrix, HSB.CHANNEL_BRIGHTNESS);
			if(meanB == 0.0 || meanS == 0.0 || meanB == 1.0) {
				return 0.0;
			}
			//double stdDevB = MatrixUtils.getColorChannelStdDev(cropMatrix, meanB, ColorChannelType.BRIGHTNESS);
			return meanS*(-(meanB*Math.log(meanB)+(1-meanB)*Math.log(1-meanB)));
		}
		
		@Override
		public double getMaxRatio() {
			return 1.0;
		}
	}
	
	public static class HSLRatioCalculator extends RasterRatioCalculator {
		
		@Override
		public ColorSpace[][] prepareImage(IDashboardFile dashboardFile) {
			Object value = dashboardFile.getCachedObject(HSL.class);
			HSL[][] imageMatrixLCH = null;
			if(value != null && value instanceof HSL[][]) {
				imageMatrixLCH = (HSL[][]) value;
			} else {
				int[][] imageMatrix = dashboardFile.getImageMatrix();
				if(imageMatrix != null) {
					imageMatrixLCH = HSL.fromRGB(imageMatrix);
					dashboardFile.putIntoCache(HSL.class, imageMatrixLCH);
				}
			}
			return imageMatrixLCH;
		}
		
		@Override
		public double getMaxRatio() {
			return 1.0;
		}

		@Override
		public double calculateRatio(ColorSpace[][] matrix) {
			//double meanH = MatrixUtils.getColorChannelMean(cropMatrix, ColorChannelType.HUE);
			//double stdDevH = MatrixUtils.getColorChannelStdDev(cropMatrix, meanH, ColorChannelType.HUE);
			double meanS = ColorChannelUtils.getColorChannelMean(matrix, HSL.CHANNEL_SATURATION);
			//double stdDevS = MatrixUtils.getColorChannelStdDev(cropMatrix, meanS, ColorChannelType.SATURATION);
			double meanB = ColorChannelUtils.getColorChannelMean(matrix, HSL.CHANNEL_LIGHTNESS);
			if(meanB == 0.0 || meanS == 0.0 || meanB == 1.0) {
				return 0.0;
			}
			//double stdDevB = MatrixUtils.getColorChannelStdDev(cropMatrix, meanB, ColorChannelType.BRIGHTNESS);
			return meanS*(-(meanB*MathUtils.log2_via_e(meanB)+(1-meanB)*MathUtils.log2_via_e(1-meanB)));
		}
	}
	
	public static class HSBRatioCalculator_sblog_x extends HSBRatioCalculator {

		@Override
		public double calculateRatio(ColorSpace[][] matrix) {
			//double meanH = MatrixUtils.getColorChannelMean(cropMatrix, ColorChannelType.HUE);
			//double stdDevH = MatrixUtils.getColorChannelStdDev(cropMatrix, meanH, ColorChannelType.HUE);
			double meanS = ColorChannelUtils.getColorChannelMean(matrix, HSB.CHANNEL_SATURATION);
			//double stdDevS = MatrixUtils.getColorChannelStdDev(cropMatrix, meanS, ColorChannelType.SATURATION);
			double meanB = ColorChannelUtils.getColorChannelMean(matrix, HSB.CHANNEL_BRIGHTNESS);
			double log = 0;
			if(meanB != 0.0 && meanB != 1.0) {
				log = 1+(meanB*MathUtils.log2_via_e(meanB)+(1-meanB)*MathUtils.log2_via_e(1-meanB));
			}
			//double stdDevB = MatrixUtils.getColorChannelStdDev(cropMatrix, meanB, ColorChannelType.BRIGHTNESS);
			return meanB*meanS*log + (1-meanB)*(1-meanB);
		}
	}
	
	public static class HSBRatioCalculator_sb_final extends HSBRatioCalculator {

		@Override
		public double calculateRatio(ColorSpace[][] matrix) {
			//double meanH = MatrixUtils.getColorChannelMean(cropMatrix, ColorChannelType.HUE);
			//double stdDevH = MatrixUtils.getColorChannelStdDev(cropMatrix, meanH, ColorChannelType.HUE);
			double meanS = ColorChannelUtils.getColorChannelMean(matrix, HSB.CHANNEL_SATURATION);
			//double stdDevS = MatrixUtils.getColorChannelStdDev(cropMatrix, meanS, ColorChannelType.SATURATION);
			double meanB = ColorChannelUtils.getColorChannelMean(matrix, HSB.CHANNEL_BRIGHTNESS);
			//double stdDevB = MatrixUtils.getColorChannelStdDev(cropMatrix, meanB, ColorChannelType.BRIGHTNESS);
			return 1-(1-meanS)*meanB;
		}
	}
	
	public static class HSBRatioCalculator_sb_final05 extends HSBRatioCalculator {

		@Override
		public double calculateRatio(ColorSpace[][] matrix) {
			//double meanH = MatrixUtils.getColorChannelMean(cropMatrix, ColorChannelType.HUE);
			//double stdDevH = MatrixUtils.getColorChannelStdDev(cropMatrix, meanH, ColorChannelType.HUE);
			double meanS = ColorChannelUtils.getColorChannelMean(matrix, HSB.CHANNEL_SATURATION);
			//double stdDevS = MatrixUtils.getColorChannelStdDev(cropMatrix, meanS, ColorChannelType.SATURATION);
			double meanB = ColorChannelUtils.getColorChannelMean(matrix, HSB.CHANNEL_BRIGHTNESS);
			//double stdDevB = MatrixUtils.getColorChannelStdDev(cropMatrix, meanB, ColorChannelType.BRIGHTNESS);
			return 0.5-(0.5-meanS)*meanB;
		}
	}

}
