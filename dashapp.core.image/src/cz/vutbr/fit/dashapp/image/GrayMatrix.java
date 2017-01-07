package cz.vutbr.fit.dashapp.image;

import java.awt.image.BufferedImage;

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
		return 255-(int)(probabilty*255);
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
	
	public static class EntrophyCalculator implements PixelCalculator {
		
		private int maxValue;

		public EntrophyCalculator(int maxValue) {
			this.maxValue = maxValue;
		}
		
		@Override
		public int calculateValue(int value) {
			double probabilty = (double) value/this.maxValue;
			return toGray(MathUtils.entrophy(probabilty));
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

	public static int meanValue(int[][] matrix) {
		double mean = 0;
		
		int mW = matrix.length;
		int mH = matrix[0].length;
		
		for (int i = 0; i < mW; i++) {
			for (int j = 0; j < mH; j++) {
				mean += matrix[i][j];
			}
		}
		
		mean = mean/(mW*mH);
		
		return (int) mean;
	}
}
