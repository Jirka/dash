package cz.vutbr.fit.dashapp.util.matrix;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

import cz.vutbr.fit.dashapp.util.MathUtils;

public class GrayMatrix {
	
	public static final int BLACK = 0;
	public static final int WHITE = 255;
	public static final int SAME_COLOR = -1;
	
	
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
		int mW = MatrixUtils.width(matrix);
		int mH = MatrixUtils.height(matrix);
		
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
	
	public static int[][] toColorMatrix(int[][] matrix, boolean createNew) {
		int mW = MatrixUtils.width(matrix);
		int mH = MatrixUtils.height(matrix);
		
		int[][] resultMatrix = matrix;
		if(createNew) {
			resultMatrix = new int[mW][mH];
		}
		
		for (int i = 0; i < mW; i++) {
			for (int j = 0; j < mH; j++) {
				resultMatrix[i][j] = getRGB(matrix[i][j]);
			}
		}
		return resultMatrix;
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
	
	public static int[][] copyPixels(int[][] to, int[][] from, int copyColor, int drawColor) {
		int mW_max = Math.max(MatrixUtils.width(to), MatrixUtils.width(from));
		int mH_max = Math.max(MatrixUtils.height(to), MatrixUtils.height(from));
		
		for (int x = 0; x < mW_max; x++) {
			for (int y = 0; y < mH_max; y++) {
				if(from[x][y] == copyColor) {
					if(drawColor == SAME_COLOR) {
						to[x][y] = from[x][y];
					} else {
						to[x][y] = drawColor;
					}
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
	
	public static int[][] convolve(int[][] matrix, int[][] filter) {
		return convolve(matrix, filter, 1);
	}
	
	/**
	 * Makes convolution between matrix and filter
	 * 
	 * @param matrix
	 * @param filter
	 * @param createNew
	 * @return
	 */
	public static int[][] convolve(int[][] matrix, int[][] filter, int div) {
		int mW = MatrixUtils.width(matrix);
		int mH = MatrixUtils.height(matrix);
		
		int output[][] = new int[mW][mH];
		
		int fW = MatrixUtils.width(filter);
		int fW_2 = (fW-1)/2;
		int fH = MatrixUtils.height(filter);
		int fH_2 = (fH-1)/2;
		int value = 0;
		
		for (int x = 0; x < mW; x++) {
			for (int y = 0; y < mH; y++) {
				value = 0;
				for (int i = 0; i < fW; i++) {
					for (int j = 0; j < fH; j++) {
						// border pixels use pixels from the other side
						int imageX = (x - fW_2 + i + mW) % mW;
					    int imageY = (y - fH_2 + j + mH) % mH;
					    value += matrix[imageX][imageY]*filter[i][j];
					}
				}
				output[x][y] = Math.min(Math.max(value/div,0), 255);
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
		int mW = MatrixUtils.width(matrix);
		int mH = MatrixUtils.height(matrix);
		
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
	
	public static int[][] medianFilter(int[][] matrix, int kernelDepth) {
		return medianFilter(matrix, kernelDepth, false);
	}
	
	public static int[][] medianFilter(int[][] matrix, int kernelDepth, boolean noiseOnly) {
		int mW = MatrixUtils.width(matrix);
		int mH = MatrixUtils.height(matrix);
		int mW_crop = mW-kernelDepth;
		int mH_crop = mH-kernelDepth;
		
		int resultMatrix[][] = copy(matrix);
		
		int kernelWidth = kernelDepth*2+1;
		int middle = (kernelWidth*kernelWidth)/2;
		int[] kernel = new int[kernelWidth*kernelWidth];
		int k; // kernel iterator
		boolean allowFilter = false;
		int imageX, imageY;
		int x1, x2, y1, y2;
		
		int sum = 0;
		for (int x = kernelDepth; x < mW_crop; x++) {
			for (int y = kernelDepth; y < mH_crop; y++) {
				
				allowFilter = true;
				if(noiseOnly) {
					x1 = x-1;
					x2 = x+1;
					y1 = y-1;
					y2 = y+1;
					for (int i = x1; i <= x2 && allowFilter; i++) {
						if(i >= 0 && i < mW) {
							for (int j = y1; j <= y2 && allowFilter; j++) {
								if(j >= 0 && j < mH) {
									if(matrix[x][y] == matrix[i][j] && (i != x || j != y)) {
										allowFilter = false;
										//System.out.println("GrayMatrix.medianFilter()");
										sum++;
										// pixel is different from all neighbors
									}
								}
							}
						}
					}
				}
				
				if(allowFilter) {
					k = 0;
					for (int i = 0; i < kernelWidth; i++) {
						for (int j = 0; j < kernelWidth; j++) {
							// save pixel of kernel
							// border pixels use pixels from the other side
							imageX = (x - kernelDepth + i + mW_crop) % mW_crop;
						    imageY = (y - kernelDepth + j + mH_crop) % mH_crop;
						    kernel[k] = matrix[imageX][imageY];
						    k++;
						}
					}
					Arrays.sort(kernel);
					resultMatrix[x][y] = kernel[middle];
				}/* else {
					// keep same pixel
					resultMatrix[x][y] = matrix[x][y];
				}*/
			}
		}
		System.out.println((double)sum/(mW*mH));
		
		return resultMatrix;
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
	
	/**
	 * Sharpens image
	 * 
	 * @param matrix
	 * @param createNew
	 * @return
	 */
	public static int[][] sharpen(int[][] matrix) {
		
		//int edgesMatrix[][] = inverse(matrix, true);
		
		// calculate edges
		int[][] filter =
			{
				{ 0, -1, 0 },
				{ -1, 5, -1 },
				{ 0, -1, 0 }
			};
			
		int edgesMatrix[][] = convolve(matrix, filter);
		
		return edgesMatrix;
	}

	public static int[][] normalize(int[][] matrix, int maxValue, boolean createNew) {
		int mW = MatrixUtils.width(matrix);
		int mH = MatrixUtils.height(matrix);
		
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
		int mW = MatrixUtils.width(matrix);
		int mH = MatrixUtils.height(matrix);
		
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
		int mW_max = Math.max(MatrixUtils.width(matrix), MatrixUtils.width(matrix2));
		int mH_max = Math.max(MatrixUtils.height(matrix), MatrixUtils.height(matrix2));
		int mW_min = Math.min(MatrixUtils.width(matrix), MatrixUtils.width(matrix2));
		int mH_min = Math.min(MatrixUtils.height(matrix), MatrixUtils.height(matrix2));
		
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
	
	public static int[][] newMatrix(int mW, int mH, int color) {
		int[][] matrix = new int[mW][mH];
		
		for (int i = 0; i < mW; i++) {
			for (int j = 0; j < mH; j++) {
				matrix[i][j] = color;
			}
		}
		
		return matrix;
	}
	
	public static void clearMatrix(int[][] matrix, int color) {
		int mW = MatrixUtils.width(matrix);
		int mH = MatrixUtils.height(matrix);
		
		for (int i = 0; i < mW; i++) {
			for (int j = 0; j < mH; j++) {
				matrix[i][j] = color;
			}
		}
	}

	public static int getColorCount(int[][] matrix, int color) {
		int count = 0;
		
		int mW = MatrixUtils.width(matrix);
		int mH = MatrixUtils.height(matrix);
		
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
		int mW = MatrixUtils.width(matrix);
		int mH = MatrixUtils.height(matrix);
		
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
	
	public static int[][] emphasize(int[][] matrix, int size) {
		int mW = matrix.length;
		int mH = matrix[0].length;
		
		int fS = size*2+1;
		
		int emphasizedMatrix[][] = new int[mW][mH];
		clearMatrix(emphasizedMatrix, WHITE);
		
		int actX, actY;
		for (int x = 0; x < mW; x++) {
			for (int y = 0; y < mH; y++) {
				if(matrix[x][y] != WHITE) {
					if(matrix[x][y] < emphasizedMatrix[x][y]) {
						emphasizedMatrix[x][y] = matrix[x][y];
					}
					for (int i = 0; i < fS; i++) {
						actX = (x - size + i);
						if(actX >= 0 && actX < mW) {
							for (int j = 0; j < fS; j++) {
								// border pixels use pixels from the other side
							    actY = (y - size + j);
							    if(actY >= 0 && actY < mH) {
							    	if(matrix[x][y] < emphasizedMatrix[actX][actY]) {
							    		emphasizedMatrix[actX][actY] = matrix[x][y];
							    	}
							    }
							}
						}
					}
				}
			}
		}
		
		return emphasizedMatrix;
	}
	
	/**
	 * Expects black and white matrix.
	 * 
	 * @param matrix
	 * @param createNew
	 * @return
	 */
	public static int[][] createRectangles(int[][] matrix, boolean createNew) {
		int mW = MatrixUtils.width(matrix);
		int mH = MatrixUtils.height(matrix);
		
		int rectangleMatrix[][] = matrix;
		if(createNew) {
			rectangleMatrix = new int[mW][mH];
			copy(rectangleMatrix, matrix);
		}
		
		// process matrix
		int color = -1;
		for (int i = 0; i < mW; i++) {
			for (int j = 0; j < mH; j++) {
				if(rectangleMatrix[i][j] != WHITE) {
					processSeedPixel(i, j, color, rectangleMatrix);
					color--;
				}
			}
		}
		
		// convert colors to black
		for (int i = 0; i < mW; i++) {
			for (int j = 0; j < mH; j++) {
				if(rectangleMatrix[i][j] < 0) {
					rectangleMatrix[i][j] = BLACK;
				}
			}
		}
		
		return rectangleMatrix;
	}
	
	private static void processSeedPixel(int i, int j, int color, int[][] matrix) {
		int mW = MatrixUtils.width(matrix);
		int mH = MatrixUtils.height(matrix);
		
		// do flood fill algorithm
		Queue<Point> queue = new LinkedList<Point>();
        queue.add(new Point(i, j));
        int x1 = i, x2 = i, y1 = j, y2 = j;
        while (!queue.isEmpty()) {
            Point p = queue.remove();
            if ((p.x >= 0) && (p.x < mW) && (p.y >= 0) && (p.y < mH)) {
                if (matrix[p.x][p.y] == GrayMatrix.BLACK) {
                	matrix[p.x][p.y] = color;
                	
                	// update min/max points 
                	if(p.x < x1) {
                		x1 = p.x;
                	} else if(p.x > x2) {
                		x2 = p.x;
                	}
                	if(p.y < y1) {
                		y1 = p.y;
                	} else if(p.y > y2) {
                		y2 = p.y;
                	}

                	// add neighbour points
                    queue.add(new Point(p.x + 1, p.y));
                    queue.add(new Point(p.x - 1, p.y));
                    queue.add(new Point(p.x, p.y + 1));
                    queue.add(new Point(p.x, p.y - 1));
                    
                    queue.add(new Point(p.x + 1, p.y + 1));
                    queue.add(new Point(p.x + 1, p.y - 1));
                    queue.add(new Point(p.x - 1, p.y + 1));
                    queue.add(new Point(p.x - 1, p.y - 1));
                }
            }
        }
        
        x2++; y2++;
        
        // test rectangle size
        if(x2-x1 > mW/2 && y2-y1 > mH/2) {
        	// delete area
           for (int x = 0; x < mW; x++) {
    			for (int y = 0; y < mH; y++) {
    				if(matrix[x][y] == color) {
    					matrix[x][y] = WHITE;
    				}
    			}
    		}
        } else {
        	// create rectangle
            for (int x = x1; x < x2; x++) {
    			for (int y = y1; y < y2; y++) {
    				matrix[x][y] = color;
    			}
    		}
        }
	}

	public static int[][] filterPixels(int[][] matrix, int[][] maskMatrix, boolean createNew) {
		int mW = Math.min(matrix.length, maskMatrix.length);
		int mH = Math.min(matrix[0].length, maskMatrix[0].length);
		
		int resultMatrix[][] = matrix;
		if(createNew) {
			resultMatrix = new int[mW][mH];
		}
		
		for (int i = 0; i < mW; i++) {
			for (int j = 0; j < mH; j++) {
				if(maskMatrix[i][j] == BLACK) {
					resultMatrix[i][j] = matrix[i][j];
				} else {
					resultMatrix[i][j] = WHITE;
				}
			}
		}
		
		return resultMatrix;
	}

	public static int[][] lines(int[][] matrix, int verticalLineLength, int horizontalLineLength) {
		int mW = MatrixUtils.width(matrix);
		int mH = MatrixUtils.height(matrix);
		int lastW = mW-1;
		int lastH = mH-1;
		
		int lineMatrix[][] = new int[mW][mH];
		clearMatrix(lineMatrix, WHITE);
		
		int start;
		
		// vertical lines
		for (int x = 0; x < mW; x++) {
			start = -1; // new column starts
			for (int y = 0; y < mH; y++) {
				if(matrix[x][y] != WHITE && y < lastH) {
					// non-white pixel, not the last one
					if(start < 0) {
						// first non-white pixel
						start = y;
					}
				} else {
					// white or last pixel
					if(start >= 0) {
						if(y - start > verticalLineLength) {
							// draw line
							copyPixels(lineMatrix, matrix, x, start, x+1, y);
							// only last pixel can be not-white
							if(matrix[x][y] != WHITE) {
								lineMatrix[x][y] = matrix[x][y];
							}
						}
						start = -1;
					}
				}
			}
		}
		
		// vertical lines
		for (int y = 0; y < mH; y++) {
			start = -1; // new row starts
			for (int x = 0; x < mW; x++) {
				if(matrix[x][y] != WHITE && x < lastW) {
					// non-white pixel, not the last one
					if(start < 0) {
						// first non-white pixel
						start = x;
					}
				} else {
					// white or last pixel
					if(start >= 0) {
						// long enough
						if(x - start > horizontalLineLength) {
							// draw line
							copyPixels(lineMatrix, matrix, start, y, x, y+1);
							// only last pixel can be not-white
							if(matrix[x][y] != WHITE) {
								lineMatrix[x][y] = matrix[x][y];
							}
						}
						start = -1;
					}
				}
			}
		}
		
		return lineMatrix;
	}
	
	public static int[][] posterizeMatrix(int[][] matrix, int bit, boolean createCopy) {
		return posterizeMatrix_mod(matrix, 256/(int)(Math.pow(2, bit)), createCopy);
	}
	
	public static int[][] posterizeMatrix_mod(int[][] matrix, int mod, boolean createCopy) {
		int mW = MatrixUtils.width(matrix);
		int mH = MatrixUtils.height(matrix);
		
		int[][] posterizedMatrix = matrix;
		if(createCopy) {
			posterizedMatrix = new int[mW][mH];
		}
		
		for (int x = 0; x < mW; x++) {
			for (int y = 0; y < mH; y++) {
				posterizedMatrix[x][y] = (matrix[x][y] - (matrix[x][y] % mod));
			}
		}
		
		return posterizedMatrix;
	}
	
}
