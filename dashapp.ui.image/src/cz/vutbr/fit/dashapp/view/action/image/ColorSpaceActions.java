package cz.vutbr.fit.dashapp.view.action.image;

import java.awt.image.BufferedImage;

import cz.vutbr.fit.dashapp.image.colorspace.CIE;
import cz.vutbr.fit.dashapp.image.colorspace.ColorSpace;
import cz.vutbr.fit.dashapp.image.colorspace.ColorSpaceUtils;
import cz.vutbr.fit.dashapp.image.colorspace.HSB;
import cz.vutbr.fit.dashapp.image.util.HistogramUtils;
import cz.vutbr.fit.dashapp.util.matrix.ColorMatrix;
import cz.vutbr.fit.dashapp.view.util.Histogram;

/**
 * 
 * @author Jiri Hynek
 *
 */
public class ColorSpaceActions {

	/**
	 * 
	 * @author Jiri Hynek
	 *
	 */
	public static class ImageToolAction_HSBSaturation extends AbstractImageToolAction {

		/**
		 * UID
		 */
		private static final long serialVersionUID = -2841608860034218099L;

		@Override
		public String getName() {
			return "HSB Saturation";
		}

		@Override
		protected BufferedImage processImage(int[][] matrix) {
			ColorSpace[][] matrixHSB = HSB.fromRGB(matrix);
			ColorSpaceUtils.normalizeColorChannel(matrixHSB, matrix, HSB.CHANNEL_SATURATION, 1);
			ColorMatrix.printMatrixToImage(image, matrix, dashboard);
			ColorMatrix.toGrayScale(matrix, true, false);
			int[] histogram = HistogramUtils.getGrayscaleHistogram(matrix);
			new Histogram(histogram).openWindow();
			return image;
		}
	}

	/**
	 * 
	 * @author Jiri Hynek
	 *
	 */
	public static class ImageToolAction_LCHSaturation extends AbstractImageToolAction {

		/**
		 * UID
		 */
		private static final long serialVersionUID = -8121313565381369846L;

		@Override
		public String getName() {
			return "LCH Saturation";
		}

		@Override
		protected BufferedImage processImage(int[][] matrix) {
			ColorSpace[][] matrixLCH = CIE.fromRGB(matrix);
			ColorSpaceUtils.normalizeColorChannel(matrixLCH, matrix, CIE.CHANNEL_SATURATION, 2);
			ColorMatrix.printMatrixToImage(image, matrix, dashboard);
			ColorMatrix.toGrayScale(matrix, true, false);
			int[] histogram = HistogramUtils.getGrayscaleHistogram(matrix);
			new Histogram(histogram).openWindow();
			return image;
		}
	}

}
