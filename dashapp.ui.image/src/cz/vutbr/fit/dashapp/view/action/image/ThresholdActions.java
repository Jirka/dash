package cz.vutbr.fit.dashapp.view.action.image;

import java.awt.image.BufferedImage;

import cz.vutbr.fit.dashapp.image.util.AdaptiveThresholdUtils;
import cz.vutbr.fit.dashapp.util.matrix.ColorMatrix;

/**
 * 
 * @author Jiri Hynek
 *
 */
public class ThresholdActions {

	/**
	 * 
	 * @author Jiri Hynek
	 *
	 */
	public static class ImageToolAction_Adaptive1 extends AbstractImageToolAction {

		/**
		 * UID
		 */
		private static final long serialVersionUID = -6426635271695774022L;

		@Override
		public String getName() {
			return "Adaptive 1";
		}

		@Override
		protected BufferedImage processImage(int[][] matrix) {
			int s = askForInteger("Select s", "Threshold option", 8);
			int t = askForInteger("Select t", "Threshold option", 6);
			AdaptiveThresholdUtils.adaptiveThreshold(matrix, false, s, t, false);
			return ColorMatrix.printMatrixToImage(null, matrix);
		}
	}

	/**
	 * 
	 * @author Jiri Hynek
	 *
	 */
	public static class ImageToolAction_Adaptive2 extends AbstractImageToolAction {

		/**
		 * UID
		 */
		private static final long serialVersionUID = -5803821177933011334L;

		@Override
		public String getName() {
			return "Adaptive 2";
		}

		@Override
		protected BufferedImage processImage(int[][] matrix) {
			int s = askForInteger("Select s", "Threshold option", 8);
			int t = askForInteger("Select t", "Threshold option", 6);
			AdaptiveThresholdUtils.adaptiveThreshold(matrix, true, s, t, false);
			return ColorMatrix.printMatrixToImage(null, matrix);
		}
	}

}
