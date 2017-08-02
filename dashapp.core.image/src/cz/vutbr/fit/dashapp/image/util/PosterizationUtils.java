package cz.vutbr.fit.dashapp.image.util;

import cz.vutbr.fit.dashapp.image.colorspace.Gray;
import cz.vutbr.fit.dashapp.util.matrix.ColorMatrix;

public class PosterizationUtils {
	
	public static int[][] posterizeMatrix(int[][] matrix, int mod, boolean createCopy) {
		int[][] workingCopy = matrix;
		if(matrix.length > 0) {
			if(createCopy) {
				workingCopy = new int[matrix.length][matrix[0].length];
			}
			for (int i = 0; i < matrix.length; i++) {
				for (int j = 0; j < matrix[i].length; j++) {
					workingCopy[i][j] = posterizePixel(matrix[i][j], mod);
				}
			}
		}
		return workingCopy;
	}
	
	public static Gray[][] posterizeMatrix(Gray[][] matrix, int mod, boolean createCopy) {
		Gray[][] workingCopy = matrix;
		if(matrix.length > 0) {
			if(createCopy) {
				workingCopy = new Gray[matrix.length][matrix[0].length];
			}
			for (int i = 0; i < matrix.length; i++) {
				for (int j = 0; j < matrix[i].length; j++) {
					workingCopy[i][j].grayValue = posterizeGrayValue(matrix[i][j].grayValue, mod);
				}
			}
		}
		return workingCopy;
	}
	
	public static int posterizeGrayValue(int grayValue, int mod) {
		return grayValue - (grayValue % mod);
	}
	
	public static int posterizePixel(int pixel, int mod) {
		int red = ColorMatrix.getRed(pixel);
		int green = ColorMatrix.getGreen(pixel);
		int blue = ColorMatrix.getBlue(pixel);
		//if(red != 255 || green != 255 || blue != 255) {
			red = (red - (red % mod));
			green = (green - (green % mod));
			blue = (blue - (blue % mod));
		//}
		return ColorMatrix.getRGB(red, green, blue);
	}

}
