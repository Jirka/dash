package cz.vutbr.fit.dashapp.image.colorspace;

import java.awt.Rectangle;

import cz.vutbr.fit.dashapp.util.matrix.ColorMatrix;
import cz.vutbr.fit.dashapp.util.matrix.MatrixUtils;

public class ColorSpaceUtils {
	
	public static ColorSpace[][] fromRGB(int[][] matrix, Class<?> colorSpaceClass) {
		if(colorSpaceClass == CIE.class) {
			return CIE.fromRGB(matrix);
		} else if(colorSpaceClass == Gray.class) {
			return Gray.fromRGB(matrix);
		} else if(colorSpaceClass == HSB.class) {
			return HSB.fromRGB(matrix);
		} else if(colorSpaceClass == HSL.class) {
			return HSL.fromRGB(matrix);
		}
		
		return null;
	}
	
	/**
	 * Crops matrix according to given rectangle.
	 * 
	 * @param matrix
	 * @param cropRectangle
	 * @return
	 */
	public static ColorSpace[][] cropMatrix(ColorSpace[][] matrix, Rectangle cropRectangle) {
		if(cropRectangle.width == 0 || cropRectangle.height == 0) {
			//return 
		}
		
		
		int mW = MatrixUtils.width(matrix);
		if(mW > 0) {
			int mH = MatrixUtils.height(matrix);
			if(mH > 0) {
				int x1 = Math.min(cropRectangle.x, mW);
				int x2 = Math.min(cropRectangle.x+cropRectangle.width, mW);
				int y1 = Math.min(cropRectangle.y, mH);
				int y2 = Math.min(cropRectangle.y+cropRectangle.height, mH);
				
				int cW = x2-x1; 
				int cH = y2-y1;
				
				if(cW <= 0 || cH <= 0) {
					return new ColorSpace[0][0];
				}
				
				ColorSpace[][] cropMatrix = new ColorSpace[cW][cH];
				
				for (int ci = 0, i = cropRectangle.x; ci < cW; ci++, i++) {
					for (int cj = 0, j = cropRectangle.y; cj < cH; cj++, j++) {
						cropMatrix[ci][cj] = matrix[i][j];
					}
				}
				
				return cropMatrix;
			}
		}
		
		return new ColorSpace[0][0];
	}
	
	/**
	 * Returns average value of color channel in matrix.
	 * 
	 * @param matrix
	 * @param colorChannel
	 * @return average value of color channel
	 */
	public static double getColorChannelMean(ColorSpace[][] matrix, int colorChannel) {
		double mean = 0.0;
		
		int mW = MatrixUtils.width(matrix);
		if(mW > 0) {
			int mH = MatrixUtils.height(matrix);
			if(mH > 0) {
				for (int i = 0; i < mW; i++) {
					for (int j = 0; j < mH; j++) {
						 mean += (double) matrix[i][j].getColorChannel(colorChannel);
					}
				}
				return mean/(MatrixUtils.area(matrix));
			}
		}
		return mean;
	}
	
	/**
	 * Returns variance of color channel in matrix.
	 * 
	 * @param matrix
	 * @param mean
	 * @param colorChannel
	 * @return variance of color channel
	 */
	public static double getColorChannelVariance(ColorSpace[][] matrix, double mean, int colorChannel) {
		double variance = 0.0;
		
		int mW = MatrixUtils.width(matrix);
		if(mW > 0) {
			int mH = MatrixUtils.height(matrix);
			if(mH > 0) {
				for (int i = 0; i < mW; i++) {
					for (int j = 0; j < mH; j++) {
						double act = (double) matrix[i][j].getColorChannel(colorChannel);
						variance += (mean-act)*(mean-act);
					}
				}
				return variance/(MatrixUtils.area(matrix));
			}
		}
		return variance;
	}
	
	public static double getColorChannelStdDev(ColorSpace[][] matrix, double mean, int colorChannel) {
		return Math.sqrt(getColorChannelVariance(matrix, mean, colorChannel));
	}

	/**
	 * ?
	 * 
	 * @param matrixHSB
	 * @param matrix
	 * @param colorChannel
	 */
	public static void normalizeColorChannel(ColorSpace[][] matrixHSB, int[][] matrix, int colorChannel) {
		int mW = MatrixUtils.width(matrixHSB);
		int mH = MatrixUtils.height(matrixHSB);
		for (int i = 0; i < mW; i++) {
			for (int j = 0; j < mH; j++) {
				int act = 255-(int)(((double) matrixHSB[i][j].getColorChannel(colorChannel)/4)*255);
				matrix[i][j] = ColorMatrix.getRGB(act, act, act);
			}
		}
	}
	
	/**
	 * ?
	 * 
	 * @param matrixHSB
	 * @param matrix
	 * @param colorChannel
	 */
	public static void normalizeSaturationVariance(ColorSpace[][] matrixHSB, int[][] matrix, int colorChannel) {
		double mean = getColorChannelMean(matrixHSB, colorChannel);
		int mW = MatrixUtils.width(matrixHSB);
		int mH = MatrixUtils.height(matrixHSB);
		for (int i = 0; i < mW; i++) {
			for (int j = 0; j < mH; j++) {
				double variance = (double) matrixHSB[i][j].getColorChannel(colorChannel);
				variance = Math.abs((mean-variance)*(mean-variance));
				int act = 255-(int)((variance/4)*255);
				matrix[i][j] = ColorMatrix.getRGB(act, act, act);
			}
		}
	}

}
