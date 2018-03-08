package cz.vutbr.fit.view.tools.image.action;

import java.awt.image.BufferedImage;

import cz.vutbr.fit.dashapp.image.colorspace.CIE;
import cz.vutbr.fit.dashapp.image.colorspace.ColorSpace;
import cz.vutbr.fit.dashapp.image.colorspace.ColorSpaceUtils;
import cz.vutbr.fit.dashapp.image.colorspace.HSB;
import cz.vutbr.fit.dashapp.image.util.HistogramUtils;
import cz.vutbr.fit.dashapp.util.matrix.ColorMatrix;
import cz.vutbr.fit.view.tools.image.AbstractImageToolAction;
import cz.vutbr.fit.view.util.Histogram;

public class ColorSpaceActions {

	public static class ImageToolAction_HSBSaturation extends AbstractImageToolAction {

		/**
		 * 
		 */
		private static final long serialVersionUID = -2841608860034218099L;

		@Override
		public String getName() {
			return "HSB Saturation";
		}

		@Override
		protected BufferedImage processImage(int[][] matrix) {
			ColorSpace[][] matrixHSB = HSB.fromRGB(matrix);
			ColorSpaceUtils.normalizeColorChannel(matrixHSB, matrix, HSB.CHANNEL_SATURATION);
			ColorMatrix.printMatrixToImage(image, matrix, dashboard);
			ColorMatrix.toGrayScale(matrix, true, false);
			int[] histogram = HistogramUtils.getGrayscaleHistogram(matrix);
			new Histogram(histogram).openWindow();
			return image;
		}
	}

	public static class ImageToolAction_LCHSaturation extends AbstractImageToolAction {

		/**
		 * 
		 */
		private static final long serialVersionUID = -8121313565381369846L;

		@Override
		public String getName() {
			return "LCH Saturation";
		}

		@Override
		protected BufferedImage processImage(int[][] matrix) {
			ColorSpace[][] matrixLCH = CIE.fromRGB(matrix);
			ColorSpaceUtils.normalizeColorChannel(matrixLCH, matrix, CIE.CHANNEL_SATURATION);
			ColorMatrix.printMatrixToImage(image, matrix, dashboard);
			ColorMatrix.toGrayScale(matrix, true, false);
			int[] histogram = HistogramUtils.getGrayscaleHistogram(matrix);
			new Histogram(histogram).openWindow();
			return image;
		}
	}

}
