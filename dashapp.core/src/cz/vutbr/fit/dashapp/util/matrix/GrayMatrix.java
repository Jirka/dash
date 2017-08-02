package cz.vutbr.fit.dashapp.util.matrix;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import cz.vutbr.fit.dashapp.util.MathUtils;

public class GrayMatrix {
	
	public static final int BLACK = 0;
	public static final int WHITE = 255;
	
	/**
	 * Returns RGB value of selected intensity value.
	 * 
	 * @param value
	 * @return
	 */
	public static int getRGB(int value) {
		int rgb = 255;
		rgb = (rgb << 8) + value;
		rgb = (rgb << 8) + value;
		return rgb = ((rgb << 8) + value)/* | -16777216*/;
	}
	
	/**
	 * Returns gray value of probability value.
	 * 
	 * @param probabilty
	 * @return
	 */
	public static int toGray(double probabilty) {
		return WHITE-(int)(probabilty*WHITE);
	}
	
	/**
	 * Prints matrix to buffered image.
	 * 
	 * @param image
	 * @param matrix
	 */
	public static BufferedImage printMatrixToImage(BufferedImage image, int[][] matrix) {
		int mW = matrix.length;
		int mH = matrix[0].length;
		
		if(image == null) {
			image = new BufferedImage(mW, mH, BufferedImage.TYPE_INT_RGB);
		}
		
		for (int i = 0; i < mW; i++) {
			for (int j = 0; j < mH; j++) {
				image.setRGB(i, j, getRGB(matrix[i][j]));
			}
		}
		return image;
	}
	
	/**
	 * Makes convolution between matrix and filter
	 * 
	 * @param matrix
	 * @param filter
	 * @param createNew
	 * @return
	 */
	public static int[][] convolve(int[][] matrix, int[][] filter) {
		int mW = matrix.length;
		int mH = matrix[0].length;
		
		int output[][] = new int[mW][mH];
		
		int fW = filter.length;
		int fW_2 = (fW-1)/2;
		int fH = filter[0].length;
		int fH_2 = (fH-1)/2;
		
		for (int x = 0; x < mW; x++) {
			for (int y = 0; y < mH; y++) {
				int value = 0;
				for (int i = 0; i < fW; i++) {
					for (int j = 0; j < fH; j++) {
						// border pixels use pixels from the other side
						int imageX = (x - fW_2 + i + mW) % mW;
					    int imageY = (y - fH_2 + j + mH) % mH;
					    value += matrix[imageX][imageY]*filter[i][j];
					}
				}
				output[x][y] = Math.min(Math.max(value,0), 255);
			}
		}
		
		return output;
	}
	
	/**
	 * Makes inversion of all pixels.
	 * 
	 * @param matrix
	 * @param createNew
	 * @return
	 */
	public static int[][] inverse(int[][] matrix, boolean createNew) {
		int mW = matrix.length;
		int mH = matrix[0].length;
		
		int inverseMatrix[][] = matrix;
		if(createNew) {
			inverseMatrix = new int[mW][mH];
		}
		
		// inverse mattrix
		for (int i = 0; i < mW; i++) {
			for (int j = 0; j < mH; j++) {
				inverseMatrix[i][j] = 255-matrix[i][j];
			}
		}
		
		return inverseMatrix;
	}
	
	/**
	 * Detects edges of image
	 * 
	 * @param matrix
	 * @param createNew
	 * @return
	 */
	public static int[][] edges(int[][] matrix) {
		
		//int edgesMatrix[][] = inverse(matrix, true);
		
		// calculate edges
		int[][] filter =
			{
				{ 1, 1, 1 },
				{ 1, -8, 1 },
				{ 1, 1, 1 }
			};
			
		int edgesMatrix[][] = convolve(matrix, filter);
		
		return edgesMatrix;
	}

	public static int[][] normalize(int[][] matrix, int maxValue, boolean createNew) {
		int mW = matrix.length;
		int mH = matrix[0].length;
		
		int normalizedMatrix[][] = matrix;
		if(createNew) {
			normalizedMatrix = new int[mW][mH];
		}
		
		double d;
		for (int i = 0; i < mW; i++) {
			for (int j = 0; j < mH; j++) {
				d = ((double) matrix[i][j])/maxValue;
				normalizedMatrix[i][j] = GrayMatrix.toGray(d);
			}
		}
		return normalizedMatrix;
	}
	
	public static interface PixelCalculator {
		int calculateValue(int value);
	};
	
	public static class EntrophyNormalization implements PixelCalculator {
		
		private int maxValue;

		public EntrophyNormalization(int normalizationValue) {
			this.maxValue = normalizationValue;
		}
		
		@Override
		public int calculateValue(int value) {
			double probabilty = (double) value/this.maxValue;
			return toGray(MathUtils.entrophy(probabilty));
		}
	}
	
	public static class ThresholdNormalization implements PixelCalculator {
		
		private int normalizationValue;
		private double threshold;
		
		public ThresholdNormalization(double threshold, int normalizationValue) {
			this.threshold = threshold;
			this.normalizationValue = normalizationValue;
		}

		@Override
		public int calculateValue(int value) {
			double probability = (double) value/normalizationValue;
			probability = probability > threshold ? 1.0 : 0.0;
			return GrayMatrix.toGray(probability);
		}
	}
	
	public static class ThresholdCalculator implements PixelCalculator {
		
		private int threshold;
		
		public ThresholdCalculator(int threshold) {
			this.threshold = threshold;
		}

		@Override
		public int calculateValue(int value) {
			return value > threshold ? WHITE : BLACK;
		}
	}
	
	public static int[][] update(int[][] matrix, PixelCalculator calculator, boolean createNew) {
		int mW = matrix.length;
		int mH = matrix[0].length;
		
		int updatedMatrix[][] = matrix;
		if(createNew) {
			updatedMatrix = new int[mW][mH];
		}
		
		try {
			for (int i = 0; i < mW; i++) {
				for (int j = 0; j < mH; j++) {
					updatedMatrix[i][j] = calculator.calculateValue(matrix[i][j]);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return updatedMatrix;
	}

	public static int[][] compareMatrices(int[][] matrix, int[][] matrix2) {
		int mW_max = Math.max(matrix.length, matrix2.length);
		int mH_max = Math.max(matrix[0].length, matrix2[0].length);
		int mW_min = Math.max(matrix.length, matrix2.length);
		int mH_min = Math.max(matrix[0].length, matrix2[0].length);
		
		int[][] cmpMatrix = new int[mW_max][mH_max];
		
		for (int i = 0; i < mW_min; i++) {
			for (int j = 0; j < mH_min; j++) {
				cmpMatrix[i][j] = WHITE-Math.abs(matrix[i][j]-matrix2[i][j]);
			}
			// finish according to max height
			for (int j = mH_min; j < mH_max; j++) {
				cmpMatrix[i][j] = BLACK;
			}
		}
		// finish according to max width
		for (int i = mW_min; i < mW_max; i++) {
			for (int j = 0; j < mH_min; j++) {
				cmpMatrix[i][j] = BLACK;
			}
		}
		
		return cmpMatrix;
	}
	
	public static void clearMatrix(int[][] matrix, int color) {
		int mW = matrix.length;
		int mH = matrix[0].length;
		
		for (int i = 0; i < mW; i++) {
			for (int j = 0; j < mH; j++) {
				matrix[i][j] = color;
			}
		}
	}

	public static int getColorCount(int[][] matrix, int color) {
		int count = 0;
		
		int mW = matrix.length;
		int mH = matrix[0].length;
		
		for (int i = 0; i < mW; i++) {
			for (int j = 0; j < mH; j++) {
				if(matrix[i][j] == color) {
					count++;
				}
			}
		}
		
		return count;
	}

	public static int[][] cropMatrix(int[][] matrix, Rectangle cropRectangle) {
		int mW = matrix.length;
		int mH = matrix[0].length;
		
		int x1 = Math.min(cropRectangle.x, mW);
		int x2 = Math.min(cropRectangle.x+cropRectangle.width, mW);
		int y1 = Math.min(cropRectangle.y, mH);
		int y2 = Math.min(cropRectangle.y+cropRectangle.height, mH);
		
		int cW = x2-x1; 
		int cH = y2-y1;
		
		if(cW <= 0 || cH <= 0) {
			return new int[0][0];
		}
		
		int[][] cropMatrix = new int[cW][cH];
		
		for (int ci = 0, i = cropRectangle.x; ci < cW; ci++, i++) {
			for (int cj = 0, j = cropRectangle.y; cj < cH; cj++, j++) {
				cropMatrix[ci][cj] = matrix[i][j];
			}
		}
		
		return cropMatrix;
	}
	
	
	
}
