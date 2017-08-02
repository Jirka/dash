package cz.vutbr.fit.dashapp.util.matrix;

import java.awt.Color;
import java.awt.image.BufferedImage;

import cz.vutbr.fit.dashapp.model.Dashboard;
import cz.vutbr.fit.dashapp.model.GraphicalElement;

public class ColorMatrix {
	
	public static final int WHITE = Color.white.getRGB();
	public static final int BLACK = Color.black.getRGB();
	
	public static int getRGB(int r, int g, int b) {
		int rgb = 255;
		rgb = (rgb << 8) + r;
		rgb = (rgb << 8) + g;
		return rgb = ((rgb << 8) + b)/* | -16777216*/;
	}
	
	public static int getRed(int rgb) {
		return (rgb >> 16) & 0xFF;
	}
	
	public static int getGreen(int rgb) {
		return (rgb >> 8) & 0xFF;
	}
	
	public static int getBlue(int rgb) {
		return rgb & 0xFF;
	}
	
	public static int toGrayScaleValue(int r, int g, int b) {
		if(r == g && g == b) {
			return r;
		}
		return (int) Math.sqrt(0.299*r*r+0.587*g*g+0.114*b*b);
	}
	
	public static int toGrayScaleValue(int rgb) {
		return toGrayScaleValue(getRed(rgb), getGreen(rgb), getBlue(rgb));
	}
	
	public static int toGrayScaleRGB(int r, int g, int b) {
		int gray = toGrayScaleValue(r, g, b);
		if(gray < 0 && gray >= 256) {
			System.out.println(gray);
		}
		return getRGB(gray, gray, gray);
	}
	
	public static int toGrayScaleRGB(int rgb) {
		return toGrayScaleRGB(getRed(rgb), getGreen(rgb), getBlue(rgb));
	}
	
	public static int[][] toGrayScale(int[][] matrix, boolean rawValues, boolean createCopy) {
		int[][] workingCopy = matrix;
		
		int mW = matrix.length;
		int mH = matrix[0].length;
		
		if(mW > 0) {
			if(createCopy) {
				workingCopy = new int[mW][mH];
			}
			int rgb;
			for (int i = 0; i < mW; i++) {
				for (int j = 0; j < mH; j++) {
					rgb = matrix[i][j];
					if(rawValues) {
						workingCopy[i][j] = toGrayScaleValue(getRed(rgb), getGreen(rgb), getBlue(rgb));
					} else {
						workingCopy[i][j] = toGrayScaleRGB(getRed(rgb), getGreen(rgb), getBlue(rgb));
					}
				}
			}
		}
		return workingCopy;
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
				image.setRGB(i, j, matrix[i][j]);
			}
		}
		return image;
	}
	
	/**
	 * Prints matrix to buffered image according to GE region.
	 * 
	 * @param image
	 * @param matrix
	 * @param dashboard
	 */
	public static void printMatrixToImage(BufferedImage image, int[][] matrix, GraphicalElement dashboard) {
		for (int i = 0, dashX = dashboard.x; i < dashboard.width; i++, dashX++) {
			for (int j = 0, dashY = dashboard.y; j < dashboard.height; j++, dashY++) {
				image.setRGB(dashX, dashY, matrix[i][j]);
			}
		}
	}
	
	/**
	 * Prints buffered image to matrix.
	 * 
	 * @param image
	 * @return
	 */
	public static int[][] printImageToMatrix(BufferedImage image) {
		int mW = image.getWidth();
		int mH = image.getHeight();
		int[][] matrix = new int[mW][mH];
		int[] pixels = image.getRGB(0, 0, mW, mH, null, 0, mW);;
		int k = 0;
		for (int j = 0; j < mH; j++) {
			for (int i = 0; i < mW; i++) {
				matrix[i][j] = pixels[k];
				//matrix[i][j] = image.getRGB(i, j);
				k++;
			}
			
		}
		//int[][] matrix = ImageToRGB.convertTo2DWithoutUsingGetRGB(image);
		return matrix;
	}
	
	/**
	 * Prints buffered image to matrix according to GE region.
	 * 
	 * @param image
	 * @param graphicalElement
	 * @return
	 */
	public static int[][] printImageToMatrix(BufferedImage image, GraphicalElement graphicalElement) {
		int[][] matrix = new int[graphicalElement.width][graphicalElement.height];
		
		for (int i = 0, dashX = graphicalElement.absoluteX(); i < graphicalElement.width; i++, dashX++) {
			for (int j = 0, dashY = graphicalElement.absoluteY(); j < graphicalElement.height; j++, dashY++) {
				matrix[i][j] = image.getRGB(dashX, dashY);
			}
		}
		return matrix;
	}
	
	/**
	 * Converts buffered image to gray scale according to GE region.
	 * 
	 * @param image
	 * @param dashboard
	 */
	public static void toGrayScaleImage(BufferedImage image, Dashboard dashboard) {
		for (int i = 0, dashX = dashboard.x; i < dashboard.width; i++, dashX++) {
			for (int j = 0, dashY = dashboard.y; j < dashboard.height; j++, dashY++) {
				int rgb = image.getRGB(dashX, dashY);
				image.setRGB(dashX, dashY, toGrayScaleRGB(getRed(rgb), getGreen(rgb), getBlue(rgb)));;
			}
		}
	}
}
