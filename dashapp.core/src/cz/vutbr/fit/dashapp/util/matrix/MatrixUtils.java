package cz.vutbr.fit.dashapp.util.matrix;

import java.awt.Rectangle;

import cz.vutbr.fit.dashapp.util.MathUtils;

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
	
	public static int[][] copy(int[][] matrix) {				
		return copy(new int[MatrixUtils.width(matrix)][MatrixUtils.height(matrix)], matrix);
	}
	
	public static int[][] copy(int[][] to, int[][] from) {
		int mW = Math.min(to.length, from.length);
		int mH = Math.min(to[0].length, from[0].length);
		
		for (int x = 0; x < mW; x++) {
			for (int y = 0; y < mH; y++) {
				to[x][y] = from[x][y];
			}
		}
		
		return to;
	}
	
	public static int[][] copy(int[][] to, int[][] from, Rectangle rectangle) {
		
		int x1 = rectangle.x;
		int x2 = rectangle.x+rectangle.width;
		
		int y1 = rectangle.y;
		int y2 = rectangle.y+rectangle.height;
		
		for (int x = x1; x < x2; x++) {
			for (int y = y1; y < y2; y++) {
				to[x][y] = from[x][y];
			}
		}
		
		return to;
	}
	
	public static int[][] copyPixels(int[][] to, int[][] from, int x1, int y1, int x2, int y2) {
		int mW_max = Math.max(MatrixUtils.width(to), MatrixUtils.width(from));
		int mH_max = Math.max(MatrixUtils.height(to), MatrixUtils.height(from));
		
		x1 = MathUtils.roundInRange(x1, 0, mW_max);
		x2 = MathUtils.roundInRange(x2, 0, mW_max);
		y1 = MathUtils.roundInRange(y1, 0, mH_max);
		y2 = MathUtils.roundInRange(y2, 0, mH_max);
		
		for (int x = x1; x < x2; x++) {
			for (int y = y1; y < y2; y++) {
				to[x][y] = from[x][y];
			}
		}
		
		return to;
	}
	
	public static int[][] copyPixels(int[][] to, int[][] from, int testColor, int drawColor) {
		int mW_max = Math.max(MatrixUtils.width(to), MatrixUtils.width(from));
		int mH_max = Math.max(MatrixUtils.height(to), MatrixUtils.height(from));
		
		for (int x = 0; x < mW_max; x++) {
			for (int y = 0; y < mH_max; y++) {
				if(from[x][y] == testColor) {
					to[x][y] = drawColor;
				}
			}
		}
		
		return to;
	}
	
	public static int[][] copyPixels(int[][] to, int[][] from, int testColor) {
		int mW_max = Math.max(MatrixUtils.width(to), MatrixUtils.width(from));
		int mH_max = Math.max(MatrixUtils.height(to), MatrixUtils.height(from));
		
		for (int x = 0; x < mW_max; x++) {
			for (int y = 0; y < mH_max; y++) {
				if(from[x][y] == testColor) {
					to[x][y] = from[x][y];
				}
			}
		}
		
		return to;
	}
	
	public static int[][] drawPixels(int[][] matrix, int x1, int y1, int x2, int y2, int color) {
		int mW = MatrixUtils.width(matrix);
		int mH = MatrixUtils.height(matrix);
		
		x1 = MathUtils.roundInRange(x1, 0, mW);
		x2 = MathUtils.roundInRange(x2, 0, mW);
		y1 = MathUtils.roundInRange(y1, 0, mH);
		y2 = MathUtils.roundInRange(y2, 0, mH);
		
		for (int x = x1; x < x2; x++) {
			for (int y = y1; y < y2; y++) {
				matrix[x][y] = color;
			}
		}
		
		return matrix;
	}
	
	public static int[][] drawRectangle(int[][] matrix, Rectangle r, int color, boolean onlyBorders) {
		int mW = MatrixUtils.width(matrix);
		int mH = MatrixUtils.height(matrix);
		
		int x1 = MathUtils.roundInRange(r.x, 0, mW);
		int x2 = MathUtils.roundInRange(r.x + r.width, 0, mW);
		int y1 = MathUtils.roundInRange(r.y, 0, mH);
		int y2 = MathUtils.roundInRange(r.y + r.height, 0, mH);
		
		if(onlyBorders) {
			// draw rectangle borders
			boolean x1InRange = MathUtils.isInRange(r.x, 0, mW);
			boolean x2InRange = MathUtils.isInRange(r.x + r.width, 0, mW);
			boolean y1InRange = MathUtils.isInRange(r.y, 0, mH);
			boolean y2InRange = MathUtils.isInRange(r.y + r.height, 0, mH);
			
			int x2_last = x2-1;
			int y2_last = y2-1;
			
			//if(y2InRange == false) {
			//	System.out.println("y2 is not in range");
			//}
			
			// optimization
			if(y1InRange) {
				if(y2InRange) {
					for (int x = x1; x < x2; x++) {
						matrix[x][y1] = color;
						matrix[x][y2_last] = color;
					}
				} else {
					for (int x = x1; x < x2; x++) {
						matrix[x][y1] = color;
					}
				}
			} else if(y2InRange) {
				for (int x = x1; x < x2; x++) {
					matrix[x][y2_last] = color;
				}
			}
			
			if(x1InRange) {
				if(x2InRange) {
					for (int y = y1; y < y2; y++) {
						matrix[x1][y] = color;
						matrix[x2_last][y] = color;
					}
				} else {
					for (int y = y1; y < y2; y++) {
						matrix[x1][y] = color;
						matrix[x2_last][y] = color;
					}
				}
			} else if(x2InRange) {
				for (int y = y1; y < y2; y++) {
					matrix[x1][y] = color;
					matrix[x2_last][y] = color;
				}
			}
		} else {
			// fill rectangle
			for (int x = x1; x < x2; x++) {
				for (int y = y1; y < y2; y++) {
					matrix[x][y] = color;
				}
			}
		}
		
		return matrix;
	}

	public static int[][] transposeMatrix(int[][] matrix) {
		int mW = MatrixUtils.width(matrix);
		int mH = MatrixUtils.height(matrix);
		
		int[][] resultMatrix = new int[mH][mW];
		
		for (int i = 0; i < mH; i++) {
			for (int j = 0; j < mW; j++) {
				resultMatrix[i][j] = matrix[j][i];
			}
		}
		
		return resultMatrix;
	}

}
