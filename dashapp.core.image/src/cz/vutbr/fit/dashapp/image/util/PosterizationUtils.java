package cz.vutbr.fit.dashapp.image.util;

import cz.vutbr.fit.dashapp.image.colorspace.Gray;
import cz.vutbr.fit.dashapp.util.matrix.ColorMatrix;
import cz.vutbr.fit.dashapp.util.matrix.MatrixUtils;

public class PosterizationUtils {
	
	public static int[][] posterizeMatrix(int[][] matrix, int mod, boolean createCopy) {
		int mW = MatrixUtils.width(matrix);
		int mH = MatrixUtils.height(matrix);
		
		int[][] workingCopy = matrix;
		if(createCopy) {
			workingCopy = new int[mW][mH];
		}
		
		for (int i = 0; i < mW; i++) {
			for (int j = 0; j < mH; j++) {
				workingCopy[i][j] = posterizePixel(matrix[i][j], mod);
			}
		}
		return workingCopy;
	}
	
	public static Gray[][] posterizeMatrix(Gray[][] matrix, int mod, boolean createCopy) {
		int mW = MatrixUtils.width(matrix);
		int mH = MatrixUtils.height(matrix);
		
		Gray[][] workingCopy = matrix;
		if(createCopy) {
			workingCopy = new Gray[mW][mH];
		}
		
		for (int i = 0; i < mW; i++) {
			for (int j = 0; j < mH; j++) {
				workingCopy[i][j].grayValue = posterizeGrayValue(matrix[i][j].grayValue, mod);
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
