package cz.vutbr.fit.dashapp.view.action.image;

import java.awt.image.BufferedImage;

import cz.vutbr.fit.dashapp.image.floodfill.SimpleRectangleFloodFill;
import cz.vutbr.fit.dashapp.image.util.HistogramUtils;
import cz.vutbr.fit.dashapp.image.util.PosterizationUtils;
import cz.vutbr.fit.dashapp.util.matrix.ColorMatrix;
import cz.vutbr.fit.dashapp.util.matrix.GrayMatrix;
import cz.vutbr.fit.dashapp.util.matrix.GrayMatrix.ThresholdCalculator;
import cz.vutbr.fit.dashapp.view.util.Histogram;

/**
 * 
 * @author Jiri Hynek
 *
 */
public class BasicImageToolActions {
	
	/**
	 * 
	 * @author Jiri Hynek
	 *
	 */
	public static class ImageToolAction_Reset extends AbstractImageToolAction {
		
		/**
		 * UID
		 */
		private static final long serialVersionUID = 3998576591613960198L;

		@Override
		public String getName() {
			return "Reset";
		}

		@Override
		protected BufferedImage processImage(int[][] matrix) {
			return df.getImage();
		}
	}
	
	/**
	 * 
	 * @author Jiri Hynek
	 *
	 */
	public static class ImageToolAction_Gray extends AbstractImageToolAction {
		
		/**
		 * UID
		 */
		private static final long serialVersionUID = 4564827426480494939L;

		@Override
		public String getName() {
			return "Gray";
		}

		@Override
		protected BufferedImage processImage(int[][] matrix) {
			ColorMatrix.toGrayScale(matrix, false, false);
			return ColorMatrix.printMatrixToImage(null, matrix);
		}
	}
	
	/**
	 * 
	 * @author Jiri Hynek
	 *
	 */
	public static class ImageToolAction_Posterize extends AbstractImageToolAction {
		
		/**
		 * UID
		 */
		private static final long serialVersionUID = -1136116896393557877L;

		@Override
		public String getName() {
			return "Posterize";
		}

		@Override
		protected BufferedImage processImage(int[][] matrix) {
			int range = askForInteger("color bit width", "Posterization option", 6);
			PosterizationUtils.posterizeMatrix_BitSize(matrix, range, false);
			return ColorMatrix.printMatrixToImage(null, matrix);
		}
	}
	
	/**
	 * 
	 * @author Jiri Hynek
	 *
	 */
	public static class ImageToolAction_Edges extends AbstractImageToolAction {
		
		/**
		 * UID
		 */
		private static final long serialVersionUID = 3246598798032325451L;

		@Override
		public String getName() {
			return "Edges";
		}

		@Override
		protected BufferedImage processImage(int[][] matrix) {
			ColorMatrix.toGrayScale(matrix, true, false);
			matrix = GrayMatrix.edges(matrix);
			return GrayMatrix.printMatrixToImage(null, matrix);
		}
	}
	
	/**
	 * 
	 * @author Jiri Hynek
	 *
	 */
	public static class ImageToolAction_Inverse extends AbstractImageToolAction {
		
		/**
		 * UID
		 */
		private static final long serialVersionUID = 3246598798032325451L;

		@Override
		public String getName() {
			return "Inverse";
		}

		@Override
		protected BufferedImage processImage(int[][] matrix) {
			ColorMatrix.toGrayScale(matrix, true, false);
			matrix = GrayMatrix.inverse(matrix, false);
			return GrayMatrix.printMatrixToImage(null, matrix);
		}
	}
	
	/**
	 * 
	 * @author Jiri Hynek
	 *
	 */
	public static class ImageToolAction_EdgesInverse extends AbstractImageToolAction {
		
		/**
		 * UID
		 */
		private static final long serialVersionUID = 3246598798032325451L;

		@Override
		public String getName() {
			return "Edges + inverse";
		}

		@Override
		protected BufferedImage processImage(int[][] matrix) {
			ColorMatrix.toGrayScale(matrix, true, false);
			matrix = GrayMatrix.edges(matrix);
			GrayMatrix.inverse(matrix, false);
			return GrayMatrix.printMatrixToImage(null, matrix);
		}
	}
	
	/**
	 * 
	 * @author Jiri Hynek
	 *
	 */
	public static class ImageToolAction_Lines extends AbstractImageToolAction {
		
		/**
		 * UID
		 */
		private static final long serialVersionUID = 3246598798032325451L;

		@Override
		public String getName() {
			return "Lines";
		}

		@Override
		protected BufferedImage processImage(int[][] matrix) {
			int limit = askForInteger("minimal line length", "Minimal line length", 40);
			ColorMatrix.toGrayScale(matrix, true, false);
			matrix = GrayMatrix.lines(matrix, limit, limit);
			return GrayMatrix.printMatrixToImage(null, matrix);
		}
	}
	
	/**
	 * 
	 * @author Jiri Hynek
	 *
	 */
	public static class ImageToolAction_MedianFilter extends AbstractImageToolAction {
		
		/**
		 * UID
		 */
		private static final long serialVersionUID = 3246598798032325451L;

		@Override
		public String getName() {
			return "Median filter";
		}

		@Override
		protected BufferedImage processImage(int[][] matrix) {
			int kernelDepth = askForInteger("kernel depth", "Kernel depth", 1);
			ColorMatrix.toGrayScale(matrix, true, false);
			matrix = GrayMatrix.medianFilter(matrix, kernelDepth);
			return GrayMatrix.printMatrixToImage(null, matrix);
		}
	}
	
	/**
	 * 
	 * @author Jiri Hynek
	 *
	 */
	public static class ImageToolAction_Sharpen extends AbstractImageToolAction {
		
		/**
		 * UID
		 */
		private static final long serialVersionUID = 3246598798032325451L;

		@Override
		public String getName() {
			return "Sharpen";
		}

		@Override
		protected BufferedImage processImage(int[][] matrix) {
			ColorMatrix.toGrayScale(matrix, true, false);
			matrix = GrayMatrix.sharpen(matrix);
			return GrayMatrix.printMatrixToImage(null, matrix);
		}
	}
	
	/**
	 * 
	 * @author Jiri Hynek
	 *
	 */
	public static class ImageToolAction_Threshold extends AbstractImageToolAction {
		
		/**
		 * UID
		 */
		private static final long serialVersionUID = 3246598798032325451L;

		@Override
		public String getName() {
			return "Threshold";
		}

		@Override
		protected BufferedImage processImage(int[][] matrix) {
			int threshold = askForInteger("threshold (0-255, -1 most frequent value)", "Threshold", -1);
			ColorMatrix.toGrayScale(matrix, true, false);
			int[] histogram = HistogramUtils.getGrayscaleHistogram(matrix); // make histogram
			if(threshold < 0 || threshold >= 255) {
				int mostFrequentValue = HistogramUtils.findMax(histogram, -1); // find most frequent value (possible background)
				if(mostFrequentValue < (GrayMatrix.WHITE/2)) {
					GrayMatrix.inverse(matrix, false);
					mostFrequentValue = GrayMatrix.WHITE-mostFrequentValue;
				}
				threshold = mostFrequentValue-1;
			}
			GrayMatrix.update(matrix, new ThresholdCalculator((int) threshold), false); // threshold according to background
			return GrayMatrix.printMatrixToImage(null, matrix);
		}
	}
	
	/**
	 * 
	 * @author Jiri Hynek
	 *
	 */
	public static class ImageToolAction_Rectangles extends AbstractImageToolAction {
		
		/**
		 * UID
		 */
		private static final long serialVersionUID = 3246598798032325451L;

		@Override
		public String getName() {
			return "Rectangles";
		}

		@Override
		protected BufferedImage processImage(int[][] matrix) {
			ColorMatrix.toGrayScale(matrix, true, false);
			new SimpleRectangleFloodFill(matrix, false, GrayMatrix.BLACK).process();
			return GrayMatrix.printMatrixToImage(null, matrix);
		}
	}
	
	/**
	 * 
	 * @author Jiri Hynek
	 *
	 */
	public static class ImageToolAction_Histogram extends AbstractImageToolAction {
		
		/**
		 * UID
		 */
		private static final long serialVersionUID = -2450211821477218910L;

		@Override
		public String getName() {
			return "Histogram";
		}

		@Override
		protected BufferedImage processImage(int[][] matrix) {
			ColorMatrix.toGrayScale(matrix, true, false);
			int[] histogram = HistogramUtils.getGrayscaleHistogram(matrix);
			new Histogram(histogram).openWindow();
			return null;
		}
	}

}
