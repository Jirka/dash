package cz.vutbr.fit.dashapp.util.matrix;

public class MatrixUtils {
	
	public static int width(int[][] matrix) {
		return matrix.length;
	}
	
	public static int height(int[][] matrix) {
		if(matrix.length == 0) {
			return 0;
		}
		return matrix[0].length;
	}
	
	public static int area(int[][] matrix) {
		return width(matrix)*height(matrix);
	}
	
	public static int width(boolean[][] matrix) {
		return matrix.length;
	}
	
	public static int height(boolean[][] matrix) {
		if(matrix.length == 0) {
			return 0;
		}
		return matrix[0].length;
	}
	
	public static int area(boolean[][] matrix) {
		return width(matrix)*height(matrix);
	}
	
	public static int width(Object[][] matrix) {
		return matrix.length;
	}
	
	public static int height(Object[][] matrix) {
		if(matrix.length == 0) {
			return 0;
		}
		return matrix[0].length;
	}
	
	public static int area(Object[][] matrix) {
		return width(matrix)*height(matrix);
	}
	
	public static int amount(int[][] matrix, int value) {
		int n = 0;
		
		int mW = width(matrix);
		int mH = height(matrix);
		
		for (int x = 0; x < mW; x++) {
			for (int y = 0; y < mH; y++) {
				if(matrix[x][y] == value) {
					n++;
				}
			}
		}
		
		return n;
	}
	
	public static int amount(boolean[][] matrix, boolean value) {
		int n = 0;
		
		for (int x = 0; x < matrix.length; x++) {
			for (int y = 0; y < matrix.length; y++) {
				if(matrix[x][y] == value) {
					n++;
				}
			}
		}
		
		return n;
	}
	
	public static int amount(Object[][] matrix, Object value) {
		int n = 0;
		
		for (int x = 0; x < matrix.length; x++) {
			for (int y = 0; y < matrix.length; y++) {
				if(value.equals(matrix[x][y])) {
					n++;
				}
			}
		}
		
		return n;
	}

}
