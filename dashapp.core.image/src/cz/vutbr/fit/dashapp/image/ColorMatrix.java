package cz.vutbr.fit.dashapp.image;

import java.awt.image.BufferedImage;

public class ColorMatrix {
	
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
		return (int) Math.sqrt(0.299*r*r+0.587*g*g+0.114*b*b);
	}
	
	public static int toGrayScaleRGB(int r, int g, int b) {
		int gray = toGrayScaleValue(r, g, b);
		if(gray < 0 && gray >= 256) {
			System.out.println(gray);
		}
		return getRGB(gray, gray, gray);
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

}
