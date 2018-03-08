package cz.vutbr.fit.tools.segmentation;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.List;

import com.tree.TreeNode;

import cz.vutbr.fit.dashapp.segmenation.util.EmphasizeSameColorsUtil;
import cz.vutbr.fit.dashapp.segmenation.util.FilterGradientsUtil;
import cz.vutbr.fit.dashapp.segmenation.util.FindFrequentValuesUtil;
import cz.vutbr.fit.dashapp.segmenation.util.FrequentValuesThresholdUtil;
import cz.vutbr.fit.dashapp.segmenation.util.HoughLineUtil;
import cz.vutbr.fit.dashapp.segmenation.util.region.DrawRegionsUtil;
import cz.vutbr.fit.dashapp.segmenation.util.region.FindSameColorRegionsUtils;
import cz.vutbr.fit.dashapp.segmenation.util.region.ProcessRegionsUtil;
import cz.vutbr.fit.dashapp.segmenation.util.region.Region;
import cz.vutbr.fit.dashapp.util.matrix.ColorMatrix;
import cz.vutbr.fit.dashapp.util.matrix.GrayMatrix;
import cz.vutbr.fit.dashapp.util.matrix.MatrixUtils;
import cz.vutbr.fit.view.tools.image.AbstractImageToolAction;

public class SegmentationImageToolActions {
	
	public static class ImageToolAction_EmphasizeSameColor extends AbstractImageToolAction {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 3246598798032325451L;

		@Override
		public String getName() {
			return "Emphasize same color";
		}

		@Override
		protected BufferedImage processImage(int[][] matrix) {
			int minArea = askForInteger("minimal area", "Minimal area", 1000);
			int minSize = askForInteger("minimal size", "Minimal size", 20);
			ColorMatrix.toGrayScale(matrix, true, false);
			matrix = EmphasizeSameColorsUtil.process(matrix, minArea, minSize);
			GrayMatrix.printMatrixToImage(image, matrix);
			return image;
		}
	}
	
	public static class ImageToolAction_RemoveGradients extends AbstractImageToolAction {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 3246598798032325451L;

		@Override
		public String getName() {
			return "Remove gradients";
		}

		@Override
		protected BufferedImage processImage(int[][] matrix) {
			int maxDiff = askForInteger("max diff", "Maximal difference", 1);
			ColorMatrix.toGrayScale(matrix, true, false);
			matrix = FilterGradientsUtil.process(matrix, maxDiff);
			GrayMatrix.printMatrixToImage(image, matrix);
			return image;
		}
	}
	
	public static class ImageToolAction_HoughLines extends AbstractImageToolAction {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 3246598798032325451L;

		@Override
		public String getName() {
			return "Hough lines";
		}

		@Override
		protected BufferedImage processImage(int[][] matrix) {
			double scaleW = askForDouble("min horizontal length share", "Minimal horizontal length share", 0.5);
			double scaleH = askForDouble("min vertical length share", "Minimal vertical length share", 0.5);
			ColorMatrix.toGrayScale(matrix, true, false);
			matrix = HoughLineUtil.process(matrix, scaleW, scaleH);
			GrayMatrix.printMatrixToImage(image, matrix);
			return image;
		}
	}
	
	public static class ImageToolAction_HoughLinesImage extends AbstractImageToolAction {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 3246598798032325451L;

		@Override
		public String getName() {
			return "Image + Hough lines";
		}

		@Override
		protected BufferedImage processImage(int[][] matrix) {
			double scaleW = askForDouble("min horizontal length share", "Minimal horizontal length share", 0.5);
			double scaleH = askForDouble("min vertical length share", "Minimal vertical length share", 0.5);
			ColorMatrix.toGrayScale(matrix, true, false);
			int[][] houghLineMatrix = HoughLineUtil.process(matrix, scaleW, scaleH);
			GrayMatrix.copyPixels(matrix, houghLineMatrix, GrayMatrix.BLACK, Color.RED.getRGB());
			GrayMatrix.printMatrixToImage(image, matrix);
			return image;
		}
	}
	
	public static class ImageToolAction_HistogramThreshold11 extends AbstractImageToolAction {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 3246598798032325451L;

		@Override
		public String getName() {
			return "Histogram 1 + threshold 1";
		}

		@Override
		protected BufferedImage processImage(int[][] matrix) {
			ColorMatrix.toGrayScale(matrix, true, false);
			List<Integer> frequentValues = FindFrequentValuesUtil.find(matrix);
			matrix = FrequentValuesThresholdUtil.threshold(matrix, frequentValues);
			GrayMatrix.printMatrixToImage(image, matrix);
			return image;
		}
	}
	
	public static class ImageToolAction_HistogramThreshold12 extends AbstractImageToolAction {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 3246598798032325451L;

		@Override
		public String getName() {
			return "Histogram 1 + threshold 2";
		}

		@Override
		protected BufferedImage processImage(int[][] matrix) {
			ColorMatrix.toGrayScale(matrix, true, false);
			List<Integer> frequentValues = FindFrequentValuesUtil.find(matrix);
			matrix = FrequentValuesThresholdUtil.threshold2(matrix, frequentValues);
			GrayMatrix.printMatrixToImage(image, matrix);
			return image;
		}
	}
	
	public static class ImageToolAction_HistogramThreshold21 extends AbstractImageToolAction {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 3246598798032325451L;

		@Override
		public String getName() {
			return "Histogram 2 + threshold 1";
		}

		@Override
		protected BufferedImage processImage(int[][] matrix) {
			ColorMatrix.toGrayScale(matrix, true, false);
			List<Integer> frequentValues = FindFrequentValuesUtil.find2(matrix);
			matrix = FrequentValuesThresholdUtil.threshold(matrix, frequentValues);
			GrayMatrix.printMatrixToImage(image, matrix);
			return image;
		}
	}
	
	public static class ImageToolAction_HistogramThreshold22 extends AbstractImageToolAction {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 3246598798032325451L;

		@Override
		public String getName() {
			return "Histogram 2 + threshold 2";
		}

		@Override
		protected BufferedImage processImage(int[][] matrix) {
			ColorMatrix.toGrayScale(matrix, true, false);
			List<Integer> frequentValues = FindFrequentValuesUtil.find2(matrix);
			matrix = FrequentValuesThresholdUtil.threshold2(matrix, frequentValues);
			GrayMatrix.printMatrixToImage(image, matrix);
			return image;
		}
	}
	
	public static class ImageToolAction_SameColorRegions extends AbstractImageToolAction {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 3246598798032325451L;

		@Override
		public String getName() {
			return "Same color regions";
		}

		@Override
		protected BufferedImage processImage(int[][] matrix) {
			int maxDepth = askForInteger("max depth", "Maximal tree depth", -1);
			int mW = MatrixUtils.width(matrix);
			int mH = MatrixUtils.height(matrix);
			ColorMatrix.toGrayScale(matrix, true, false);
			List<Region> regions = FindSameColorRegionsUtils.findRegions(matrix);
			TreeNode<Region> root = ProcessRegionsUtil.constructTree(regions, 0, 0, mW, mH);
			matrix = DrawRegionsUtil.drawRegions(root, maxDepth);
			GrayMatrix.printMatrixToImage(image, matrix);
			return image;
		}
	}
	
	public static class ImageToolAction_SameColorDominantRegions extends AbstractImageToolAction {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 3246598798032325451L;

		@Override
		public String getName() {
			return "Same color dominant regions";
		}

		@Override
		protected BufferedImage processImage(int[][] matrix) {
			int mW = MatrixUtils.width(matrix);
			int mH = MatrixUtils.height(matrix);
			int[][] workingCopy = ColorMatrix.toGrayScale(matrix, true, true);
			List<Region> regions = FindSameColorRegionsUtils.findRegions(workingCopy);
			TreeNode<Region> root = ProcessRegionsUtil.constructTree(regions, 0, 0, mW, mH);
			List<Region> mainRegions = ProcessRegionsUtil.getMainRegions(root);
			workingCopy = DrawRegionsUtil.drawRegions(matrix, mainRegions, Color.RED.getRGB());
			ColorMatrix.printMatrixToImage(image, matrix);
			return image;
		}
	}

}
