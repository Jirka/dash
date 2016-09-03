package cz.vutbr.fit.dash.util;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import cz.vutbr.fit.dash.model.Dashboard;
import cz.vutbr.fit.dash.model.GraphicalElement;
import cz.vutbr.fit.dash.model.GraphicalElement.Type;
import cz.vutbr.fit.dash.util.MatrixUtils.ColorChannel;
import cz.vutbr.fit.dash.util.MatrixUtils.ColorChannel.ColorChannelType;

public class MatrixUtils {
	
	public static final int WHITE = Color.white.getRGB();
	public static final int BLACK = Color.black.getRGB();
	
	public interface ColorChannel {
		public enum ColorChannelType {
			HUE, SATURATION, BRIGHTNESS, LIGHTNESS, A, B, CHROMA
		}
		
		public double getColorChannel(ColorChannelType type);
	}
	
	public static class HSB implements ColorChannel {
		public float h;
		public float s;
		public float b;
		
		public HSB(int rgb) {
			float[] hsbvals = new float[3];
			Color.RGBtoHSB(getRed(rgb), getGreen(rgb), getBlue(rgb), hsbvals);
			this.h = hsbvals[0];
			this.s = hsbvals[1];
			this.b = hsbvals[2];
		}

		@Override
		public double getColorChannel(ColorChannelType type) {
			switch (type) {
				case HUE:
					return h;
				case SATURATION:
					return s;
				case BRIGHTNESS:
					return b;
				default:
					return 0;
			}
		}
	}
	
	public static class LCH implements ColorChannel {
		public double l;
		public double a;
		public double b;
		public double c;
		public double h;
		
		public LCH(CIELab cielab, int rgb) {
			//float[] rgbvals = { getRed(rgb), getGreen(rgb), getBlue(rgb) } ;
			//float[] rgbvals = { ((float) getRed(rgb))/255, ((float) getGreen(rgb))/255, ((float) getBlue(rgb))/255 } ;
			//float[] lchvals = cielab.fromRGB(rgbvals);
			double[] lchvals = toColorSpace(rgb);
			if(lchvals[0] == 0) {
				lchvals = toColorSpace(rgb);
			}
			this.l = lchvals[0];
			this.a = lchvals[1];
			this.b = lchvals[2];
			this.h = Math.atan2(this.b, this.a);
			
			// convert from radians to degrees
            if (h > 0)
            {
                h = (h / Math.PI) * 180.0;
            }
            else
            {
                h = 360 - (Math.abs(h) / Math.PI) * 180.0;
            }

            if (h < 0)
            {
                h += 360.0;
            }
            else if (h >= 360)
            {
                h -= 360.0;
            }

            this.c = Math.sqrt(this.a * this.a + this.b * this.b);
		}

		@Override
		public double getColorChannel(ColorChannelType type) {
			switch (type) {
				case LIGHTNESS:
					return l;
				case A:
					return a;
				case B:
					return b;
				case CHROMA:
					return c;
				case HUE:
					return h;
				case SATURATION:
					if(l == 0) return 0;
					return c/l;
				default:
					return 0;
			}
		}
	}
	
	private static double[] toColorSpace(int rgb)
    {
		double r = PivotRgb(getRed(rgb) / 255.0);
        double g = PivotRgb(getGreen(rgb) / 255.0);
        double b = PivotRgb(getBlue(rgb) / 255.0);

        // Observer. = 2°, Illuminant = D65
        double X = r * 0.4124 + g * 0.3576 + b * 0.1805;
        double Y = r * 0.2126 + g * 0.7152 + b * 0.0722;
        double Z = r * 0.0193 + g * 0.1192 + b * 0.9505;

        double x = PivotXyz(X / 95.047);
        double y = PivotXyz(Y / 100.000);
        double z = PivotXyz(Z / 108.883);

        double L = Math.max(0, 116 * y - 16);
        double A = 500 * (x - y);
        double B = 200 * (y - z);
        
        return new double[] { L, A, B };
    }
	
	private static double PivotRgb(double n)
    {
        return (n > 0.04045 ? Math.pow((n + 0.055) / 1.055, 2.4) : n / 12.92) * 100.0;
    }

    private static double PivotXyz(double n)
    {
        return n > 0.008856 ? CubicRoot(n) : (903.3 * n + 16) / 116;
    }

    private static double CubicRoot(double n)
    {
        return Math.pow(n, 1.0 / 3.0);
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
	
	public static int getRGB(int r, int g, int b) {
		int rgb = 255;
		rgb = (rgb << 8) + r;
		rgb = (rgb << 8) + g;
		return rgb = ((rgb << 8) + b)/* | -16777216*/;
	}
	
	public static int getGrayScaleValue(int r, int g, int b) {
		return (int) Math.sqrt(0.299*r*r+0.587*g*g+0.114*b*b);
	}
	
	public static int getGrayScale(int r, int g, int b) {
		int gray = getGrayScaleValue(r, g, b);
		if(gray < 0 && gray >= 256) {
			System.out.println(gray);
		}
		return getRGB(gray, gray, gray);
	}

	public static void initMattrix(boolean[][] matrix, boolean initValue) {
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[i].length; j++) {
				matrix[i][j] = initValue;
			}
		}
		/*for(boolean[] row : matrix) {
			for(boolean item : row) {
				item = initValue;
			}
		}*/
	}

	public static void printDashboard(boolean[][] matrix, Dashboard dashboard, boolean clear, Type[] types) {
		if(clear) {
			initMattrix(matrix, false);
		}
		for(GraphicalElement graphicalElement : dashboard.getGraphicalElements(types)) {
			printGraphicalElement(matrix, graphicalElement);
		}
	}

	public static void printGraphicalElement(boolean[][] matrix, GraphicalElement graphicalElement) {
		// optimization
		int x2 = graphicalElement.x2();
		int y2 = graphicalElement.y2();
		// print
		for(int i = graphicalElement.x; i < x2; i++) {
			for(int j = graphicalElement.y; j < y2; j++) {
				matrix[i][j] = true;
			}
		}
	}
	
	public static int[][] printBufferedImage(BufferedImage image) {
		int[][] matrix = new int[image.getWidth()][image.getHeight()];
		for (int i = 0; i < image.getWidth(); i++) {
			for (int j = 0; j < image.getHeight(); j++) {
				matrix[i][j] = image.getRGB(i, j);
			}
		}
		return matrix;
	}
	
	/*public static int[][] printBufferedImage(BufferedImage image, Dashboard dashboard) {
		int[][] matrix = new int[dashboard.width][dashboard.height];
		//int dashX, dashY;
		for (int i = 0, dashX = dashboard.x; i < dashboard.width; i++, dashX++) {
			for (int j = 0, dashY = dashboard.y; j < dashboard.height; j++, dashY++) {
				matrix[i][j] = image.getRGB(dashX, dashY);
			}
		}
		return matrix;
	}*/
	
	public static int[][] printBufferedImage(BufferedImage image, GraphicalElement graphicalElement) {
		int[][] matrix = new int[graphicalElement.width][graphicalElement.height];
		
		//int dashX, dashY;
		for (int i = 0, dashX = graphicalElement.absoluteX(); i < graphicalElement.width; i++, dashX++) {
			for (int j = 0, dashY = graphicalElement.absoluteY(); j < graphicalElement.height; j++, dashY++) {
				matrix[i][j] = image.getRGB(dashX, dashY);
			}
		}
		return matrix;
	}
	
	public static void updateBufferedImage(BufferedImage image, int[][] matrix) {
		for (int i = 0; i < image.getWidth(); i++) {
			for (int j = 0; j < image.getHeight(); j++) {
				image.setRGB(i, j, matrix[i][j]);
			}
		}
	}
	
	public static void updateBufferedImage(BufferedImage image, int[][] matrix, Dashboard dashboard) {
		for (int i = 0, dashX = dashboard.x; i < dashboard.width; i++, dashX++) {
			for (int j = 0, dashY = dashboard.y; j < dashboard.height; j++, dashY++) {
				image.setRGB(dashX, dashY, matrix[i][j]);
			}
		}
	}
	
	public static int[][] convertBufferedImageToRGB(BufferedImage image, Dashboard dashboard) {
		int[][] matrix = new int[dashboard.width][dashboard.height];
		//int dashX, dashY;
		for (int i = 0, dashX = dashboard.x; i < dashboard.width; i++, dashX++) {
			for (int j = 0, dashY = dashboard.y; j < dashboard.height; j++, dashY++) {
				int rgb = image.getRGB(dashX, dashY);
				image.setRGB(dashX, dashY, getGrayScale(getRed(rgb), getGreen(rgb), getBlue(rgb)));;
			}
		}
		return matrix;
	}
	
	/**
	 * Makes adaptive treshold.
	 * 
	 * @param inversion
	 */
	public static int[][] adaptiveThreshold(int matrix[][], boolean inversion, int s1, int t1, boolean createCopy) {
		
		int workingCopy[][] = matrix;
		
		if(matrix.length > 0) {
			
			int height = matrix.length;
			int width = matrix[0].length;
			
			if(createCopy) {
				workingCopy = new int[height][width];
			}
			
			if(s1 <= 0) {
				s1 = 8;
			}
			if(t1 <= 0) {
				t1 = 6;
			}
			
			/*for(int i = 0; i < width; i++) {
				for(int j = 0; j < height; j++) {
					System.out.println(256+image.getRGB(i, j)%256);
				}
			}*/
			
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
	 * Converts color to gray scale.
	 * 
	 * @param my_color
	 * @return gray
	 */
	public static int getGray(int my_color) {
		
		Color color = new Color(my_color);
		//System.out.println(color.getRed() + " " + color.getGreen() + " " + color.getBlue());
		int a = (int) ((color.getRed()*0.299 + color.getGreen()*0.587 + color.getBlue()*0.114));
		//if(a > 255 || a < 0)
		  //System.out.println(a);
		
		return a;

		/*int red = (my_color&0xff0000 ) >> 24;
		int green = (my_color&0x00ff00 ) >> 16;
		int blue = (my_color&0x0000ff ) >> 8; 

        int l = (int) (.299 * red + .587 * green + .114 * blue);*/
        //System.out.println(l);
        
        //return 0;
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

	public static int[][] grayScaleToValues(int[][] matrix, boolean createCopy) {
		int[][] workingCopy = matrix;
		if(matrix.length > 0) {
			if(createCopy) {
				workingCopy = new int[matrix.length][matrix[0].length];
			}
			int rgb;
			for (int i = 0; i < matrix.length; i++) {
				for (int j = 0; j < matrix[i].length; j++) {
					rgb = matrix[i][j];
					workingCopy[i][j] = (getRed(rgb)+getGreen(rgb)+getBlue(rgb))/3;
				}
			}
		}
		return workingCopy;
	}
	
	public static int[][] grayScale(int[][] matrix, boolean rawValues, boolean createCopy) {
		int[][] workingCopy = matrix;
		if(matrix.length > 0) {
			if(createCopy) {
				workingCopy = new int[matrix.length][matrix[0].length];
			}
			int rgb;
			for (int i = 0; i < matrix.length; i++) {
				for (int j = 0; j < matrix[i].length; j++) {
					rgb = matrix[i][j];
					if(rawValues) {
						workingCopy[i][j] = getGrayScaleValue(getRed(rgb), getGreen(rgb), getBlue(rgb));
					} else {
						workingCopy[i][j] = getGrayScale(getRed(rgb), getGreen(rgb), getBlue(rgb));
					}
				}
			}
		}
		return workingCopy;
	}

	public static int[] getGrayscaleHistogram(int[][] matrix) {
		int histogram[] = new int[256];
		for (int i = 0; i < histogram.length; i++) {
			histogram[i] = 0;
		}
		
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[i].length; j++) {
				histogram[matrix[i][j]]++;
			}
		}
		
		return histogram;
	}

	public static int[] reduceHistogram(int[] histogram, int n) {
		int size = histogram.length;
		if(n > size) {
			n = size;
		}
		int chunkSize = size/n;
		int mod = size%n;
		int[] reducedHistogram = new int[n];
		for (int i = 0; i < reducedHistogram.length; i++) {
			reducedHistogram[i] = 0;
		}
		
		int chunkI = 0;
		for (int i = 0, j = 0; i < size; i++) {
			reducedHistogram[j] += histogram[i];
			chunkI++;
			if(chunkI == chunkSize) {
				if(mod > 0) {
					i++;
					reducedHistogram[j] += histogram[i];
					mod--;
				}
				chunkI = 0;
				j++;
			}
		}
		
		return reducedHistogram;
	}
	
	public static Map<Integer, Integer> getColorHistogram(int[][] matrix) {
		Map<Integer, Integer> map = new HashMap<>();
		int histogram[] = new int[256];
		for (int i = 0; i < histogram.length; i++) {
			histogram[i] = 0;
		}
		
		Integer val;
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[i].length; j++) {
				val = map.get(matrix[i][j]);
				if(val == null) {
					map.put(matrix[i][j], 1);
				} else {
					map.put(matrix[i][j], val+1);
				}
			}
		}
		
		return map;
	}
	
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
	
	public static int posterizePixel(int pixel, int mod) {
		int red = getRed(pixel);
		int green = getGreen(pixel);
		int blue = getBlue(pixel);
		//if(red != 255 || green != 255 || blue != 255) {
			red = (red - (red % mod));
			green = (green - (green % mod));
			blue = (blue - (blue % mod));
		//}
		return getRGB(red, green, blue);
	}
	
	public static HSB[][] RGBtoHSB(int[][] matrix) {
		if(matrix.length > 0) {
			HSB[][] matrixHSB = new HSB[matrix.length][matrix[0].length];
			for (int i = 0; i < matrix.length; i++) {
				for (int j = 0; j < matrix[i].length; j++) {
					matrixHSB[i][j] = new HSB(matrix[i][j]);
				}
			}
			return matrixHSB;
		}
		return null;
	}
	
	public static LCH[][] RGBtoLCH(int[][] matrix) {
		if(matrix.length > 0) {
			CIELab cielab = new CIELab();
			LCH[][] matrixLCH = new LCH[matrix.length][matrix[0].length];
			for (int i = 0; i < matrix.length; i++) {
				for (int j = 0; j < matrix[i].length; j++) {
					matrixLCH[i][j] = new LCH(cielab, matrix[i][j]);
				}
			}
			return matrixLCH;
		}
		return null;
	}
	
	public static double getColorChannelMean(ColorChannel[][] matrix, ColorChannelType type) {
		double mean = 0.0;
		if(matrix.length > 0) {
			for (int i = 0; i < matrix.length; i++) {
				for (int j = 0; j < matrix[i].length; j++) {
					 mean += matrix[i][j].getColorChannel(type);
				}
			}
			return mean/(matrix.length*matrix[0].length);
		}
		return mean;
	}
	
	public static double getColorChannelVariance(ColorChannel[][] matrix, double mean, ColorChannelType type) {
		double variance = 0.0;
		if(matrix.length > 0) {
			for (int i = 0; i < matrix.length; i++) {
				for (int j = 0; j < matrix[i].length; j++) {
					double act = matrix[i][j].getColorChannel(type);
					variance += (mean-act)*(mean-act);
				}
			}
			return variance/(matrix.length*matrix[0].length);
		}
		return variance;
	}
	
	public static double getColorChannelStdDev(ColorChannel[][] matrix, double mean, ColorChannelType type) {
		return Math.sqrt(getColorChannelVariance(matrix, mean, type));
	}

	public static void normalizeColorChannel(ColorChannel[][] matrixHSB, int[][] matrix, ColorChannelType type) {
		for (int i = 0; i < matrixHSB.length; i++) {
			for (int j = 0; j < matrixHSB[i].length; j++) {
				int act = 255-(int)((matrixHSB[i][j].getColorChannel(type)/4)*255);
				matrix[i][j] = getRGB(act, act, act);
			}
		}
	}
	
	public static void normalizeSaturationVariance(ColorChannel[][] matrixHSB, int[][] matrix, ColorChannelType type) {
		double mean = getColorChannelMean(matrixHSB, type);
		for (int i = 0; i < matrixHSB.length; i++) {
			for (int j = 0; j < matrixHSB[i].length; j++) {
				double variance = matrixHSB[i][j].getColorChannel(type);
				variance = Math.abs((mean-variance)*(mean-variance));
				int act = 255-(int)((variance/4)*255);
				matrix[i][j] = getRGB(act, act, act);
			}
		}
	}
}
