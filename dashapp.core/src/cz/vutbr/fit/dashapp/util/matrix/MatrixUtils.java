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

}
