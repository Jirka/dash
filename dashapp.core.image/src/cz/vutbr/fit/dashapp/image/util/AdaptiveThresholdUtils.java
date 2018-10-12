package cz.vutbr.fit.dashapp.image.util;

import java.awt.Color;

/**
 * Experimental implementation of adaptive threshold.
 * 
 * (based on school project of Jiri Janda, Ivo Skolek and Jiri Hynek
 * at Brno University of Technology)
 * 
 * @author Jiri Hynek
 * 
 */
public class AdaptiveThresholdUtils {
	
	/**
	 * Makes adaptive threshold.
	 * 
	 * @param inversion
	 */
	public static int[][] adaptiveThreshold(int matrix[][], boolean inversion, int s1, int t1, boolean createCopy) {
		
		int WHITE = Color.white.getRGB();
		int BLACK = Color.black.getRGB();
		
		int workingCopy[][] = matrix;
		
		if(matrix.length > 0) {
			
			int height = matrix.length;
			int width = height > 0 ? matrix[0].length : 0;
			
			if(createCopy) {
				workingCopy = new int[height][width];
			}
			
			if(s1 <= 0) {
				s1 = 8;
			}
			if(t1 <= 0) {
				t1 = 6;
			}
			
			int[] g_integral = new int[width*height];
			
			int sum, pos;
			int x1, x2, y1, y2, count;
			int s = height/s1;
			int t = t1;

	        for (int i = 0; i < width; i++) {
	            sum = 0;
	            for (int j = 0; j < height; j++) {
	                pos = point_num(i, j, width, height);
	                if(!inversion) {
	                	sum += getGray(matrix[j][i]);
	                } else {
	                	sum += (matrix[j][i]%256)*(-1);
	                }
	                if (i == 0)
	                    g_integral[pos] = sum;
	                else
	                    g_integral[pos] = sum +  g_integral[point_num(i - 1, j, width, height)];
	            }
	        }

			for (int i = 0; i < width; i++) {

				x1 = Math.max(i - s/2, 0);
				x2 = Math.min(i + s/2, width-1);

				for (int j = 0; j < height; j++) {
					y1 = Math.max(j - s/2, 0);
					y2 = Math.min(j + s/2, height-1);
					pos = point_num(i, j, width, height);
					sum = g_integral[point_num(x2, y2, width, height)] - g_integral[point_num(x2, y1 - 1, width, height)] - g_integral[point_num(x1 - 1, y2, width, height)] + g_integral[point_num(x1 - 1, y1 - 1, width, height)];
					count = (x2 - x1) * (y2 - y1);

					if(!inversion) {
						int gray = getGray(matrix[j][i]);
						
						if ((gray * count) <= (sum * (100-t)/100)) {
							workingCopy[j][i] = BLACK;
						}
						else {
							workingCopy[j][i] = WHITE;
						}
					} else {
						if ((((matrix[j][i]%256*-1)) * count) <= (sum * (100-t)/100))
							workingCopy[j][i] = WHITE;
						else
							workingCopy[j][i] = BLACK;
					}
				}
			}
		}
		
	    return workingCopy;
	}
	
	/**
	 * 
	 * Converts 2D image coordinates to 1D array.
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @return num
	 */
	private static int point_num(int x, int y, int width, int height) {
	    if (x < 0) {
	        x = 0;
	    }
	    if (y < 0) {
	        y = 0;
	    }
	    width--;
	    height--;
	    if (x > width) {
	        x = width;
	    }
	    if (y > height) {
	        y = height;
	    }

	    int num = x + (y * (width + 1));

	    return num;
	}
	
	/**
	 * Converts color to gray scale.
	 * 
	 * @param rgb
	 * @return gray
	 */
	public static int getGray(int rgb) {
		Color color = new Color(rgb);
		//System.out.println(color.getRed() + " " + color.getGreen() + " " + color.getBlue());
		return (int) ((color.getRed()*0.299 + color.getGreen()*0.587 + color.getBlue()*0.114));
		
	}

}
