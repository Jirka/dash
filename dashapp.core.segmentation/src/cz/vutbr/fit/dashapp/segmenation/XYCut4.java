package cz.vutbr.fit.dashapp.segmenation;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import ac.essex.ooechs.imaging.commons.edge.hough.HoughLine;
import ac.essex.ooechs.imaging.commons.edge.hough.HoughLine.Orientation;
import ac.essex.ooechs.imaging.commons.edge.hough.HoughTransform;
import cz.vutbr.fit.dashapp.image.util.HistogramUtils;
import cz.vutbr.fit.dashapp.image.util.PosterizationUtils;
import cz.vutbr.fit.dashapp.model.Dashboard;
import cz.vutbr.fit.dashapp.model.GraphicalElement;
import cz.vutbr.fit.dashapp.util.matrix.ColorMatrix;
import cz.vutbr.fit.dashapp.util.matrix.GrayMatrix;
import cz.vutbr.fit.dashapp.util.matrix.GrayMatrix.ThresholdCalculator;
import cz.vutbr.fit.dashapp.util.matrix.MatrixUtils;
import extern.ImagePreview;

public class XYCut4 implements ISegmentationAlgorithm {

	@Override
	public Dashboard processImage(BufferedImage image) {
		// convert buffered image to 2D array
		int[][] matrix = ColorMatrix.printImageToMatrix(image);
		
		final int w = image.getWidth();
		final int h = image.getHeight();
		final int a = w*h;
		
		// image preprocessing
		
		// gray posterized
		ColorMatrix.toGrayScale(matrix, false, false); // convert to gray scale
		PosterizationUtils.posterizeMatrix(matrix, 256/(int)(Math.pow(2, 6)), false); // 6 bits posterization
		int[][] rawMatrix = ColorMatrix.toGrayScale(matrix, true, true); // convert to raw values (0-255) for simple use
		
		// edges
		int[][] edgesMatrix = GrayMatrix.edges(rawMatrix);
		GrayMatrix.inverse(edgesMatrix, false);
		new ImagePreview(GrayMatrix.printMatrixToImage(null, edgesMatrix), "edges").openWindow(800,600,0.8);
		
		// threshold (most frequent value)
		int[] histogram = HistogramUtils.getGrayscaleHistogram(rawMatrix); // make histogram
		
		List<Integer> frequentValues = new ArrayList<>();
		int mostFrequentValue = HistogramUtils.findMax(histogram, -1); // find most frequent value (possible background)
		frequentValues.add(mostFrequentValue);
		
		mostFrequentValue = HistogramUtils.findMax(histogram, mostFrequentValue); // find next frequent value
		while((double) histogram[mostFrequentValue]/a >= 0.01) {
			frequentValues.add(mostFrequentValue);
			mostFrequentValue = HistogramUtils.findMax(histogram, mostFrequentValue); // find next frequent value
			if(frequentValues.size() > 5 && (double) histogram[mostFrequentValue]/a < 0.05) {
				break;
			}
		}
		double actColor = 255.0;
		double colorInterval = actColor/frequentValues.size();
		int[][] frequentColorMatrix = new int[w][h];
		GrayMatrix.clearMatrix(frequentColorMatrix, GrayMatrix.BLACK);
		for (Integer frequentValue : frequentValues) {
			GrayMatrix.copyPixels(frequentColorMatrix, rawMatrix, frequentValue, (int) actColor);
			actColor-=colorInterval;
		}
		new ImagePreview(GrayMatrix.printMatrixToImage(null, frequentColorMatrix), "histogram threshold").openWindow(800,600,0.8);
		
		// sharpen
		/*int[][] sharpenMatrix = GrayMatrix.sharpen(rawMatrix);
		int[][] edgesMatrix2 = GrayMatrix.edges(sharpenMatrix);
		GrayMatrix.inverse(edgesMatrix2, false);
		new ImagePreview(GrayMatrix.printMatrixToImage(null, sharpenMatrix), "sharpen").openWindow(800,600,0.8);
		new ImagePreview(GrayMatrix.printMatrixToImage(null, edgesMatrix2), "sharpen edges").openWindow(800,600,0.8);*/
		
		//int[][] linesMatrix = GrayMatrix.lines(edgesMatrix, 40, 40);
		//new ImagePreview(GrayMatrix.printMatrixToImage(null, linesMatrix), "result 2").openWindow(800,600,0.8);
		
		//int[][] edgesMatrix2 = new int[edgesMatrix.length][edgesMatrix[0].length];
		//GrayMatrix.clearMatrix(edgesMatrix2, GrayMatrix.WHITE);
		//GrayMatrix.copy(edgesMatrix2, edgesMatrix, new Rectangle(305, 0, 3, edgesMatrix[0].length));
		//GrayMatrix.copy(edgesMatrix2, edgesMatrix, new Rectangle(0, 0, 308, edgesMatrix[0].length));
		//GrayMatrix.copy(edgesMatrix2, edgesMatrix, new Rectangle(311, 0, edgesMatrix.length-311, edgesMatrix[0].length));
		//new ImagePreview(GrayMatrix.printMatrixToImage(null, edgesMatrix2), "result 2").openWindow(800,600,0.8);
		
		/*int[][] edgesMatrix2 = new int[MatrixUtils.width(edgesMatrix)][MatrixUtils.height(edgesMatrix)];
		GrayMatrix.copy(edgesMatrix2, edgesMatrix);
		
		// use Hough Transform to detect lines
		int[][] lineMatrix = new int[w][h];
		GrayMatrix.clearMatrix(lineMatrix, GrayMatrix.WHITE);
		HoughTransform t = new HoughTransform(w, h);
		t.addPoints(edgesMatrix);
		Vector<HoughLine> lines = t.getLines((int) (w/4.0));
		for (HoughLine line : lines) {
			Orientation orientation = line.getOrientation();
			if(orientation == Orientation.H) {
				//line.draw(matrix, Color.RED.getRGB());
				//line.draw(edgesMatrix, Color.RED.getRGB());
				line.draw(lineMatrix, GrayMatrix.BLACK, true);
				line.draw(edgesMatrix2, Color.RED.getRGB(), true);
			}
		}
		lines = t.getLines((int) (h/4.0));
		for (HoughLine line : lines) {
			Orientation orientation = line.getOrientation();
			if(orientation == Orientation.V) {
				//line.draw(matrix, Color.RED.getRGB());
				//line.draw(edgesMatrix, Color.RED.getRGB());
				line.draw(lineMatrix, GrayMatrix.BLACK, true);
				line.draw(edgesMatrix2, Color.RED.getRGB(), true);
			}
		}
		//new ImagePreview(GrayMatrix.printMatrixToImage(null, matrix), "result 3").openWindow(800,600,0.8);
		//new ImagePreview(GrayMatrix.printMatrixToImage(null, edgesMatrix), "result 3").openWindow(800,600,0.8);
		
		new ImagePreview(GrayMatrix.printMatrixToImage(null, lineMatrix), "line matrix").openWindow(800,600,0.8);
		new ImagePreview(GrayMatrix.printMatrixToImage(null, edgesMatrix2), "line matrix").openWindow(800,600,0.8);
		int[][] houghFileterMatrix = GrayMatrix.filterPixels(edgesMatrix, lineMatrix, true);
		new ImagePreview(GrayMatrix.printMatrixToImage(null, houghFileterMatrix), "hough filtered matrix").openWindow(800,600,0.8);*/
		
		List<Rectangle> rectangles = new ArrayList<>(); // result rectangles
		XYstep(rawMatrix, new Rectangle(0, 0, w, h), rectangles, true); // first step of recursive XY-cut
		
		// create dashboard (represents graphical regions)
		Dashboard dashboard = new Dashboard();
		dashboard.setDimension(0, 0, w, h);
		
		for (Rectangle r : rectangles) {
			dashboard.addChildGE(new GraphicalElement(dashboard, r.x, r.y, r.width, r.height));
		}
		
		return dashboard;
	}

	private void XYstep(int[][] matrix, Rectangle rect, List<Rectangle> rectangles, boolean bAlternate) {
		
		List<Rectangle> stepRectangles;
		
		if(bAlternate) {
			stepRectangles = XYvertical(matrix, rect);
		} else {
			stepRectangles = XYhorizontal(matrix, rect);
		}
		
		if(stepRectangles == null || stepRectangles.isEmpty()) {
			rectangles.add(rect);
		} else {
			for (Rectangle stepRectangle : stepRectangles) {
				XYstep(matrix, stepRectangle, rectangles, !bAlternate);
			}
		}
	}
	
	private List<Rectangle> XYvertical(int[][] matrix, Rectangle rect) {
		List<Rectangle> result = null;
		
		// minimum rectangle height threshold
		if(rect.height > 10) {
			// boundary
			int left = rect.x;
			int right = rect.x+rect.width;
			int up = rect.y;
			int down = rect.y+rect.height;
			
			// histogram of object frequency
			int[] histogram = new int[rect.height];
			
			// iterate over vertical axis and for line calculate object frequency 
			for (int i = up, hi = 0; i < down; i++, hi++) {
				histogram[hi] = 0;
				// go through line
				for (int j = left; j < right; j++) {
					// check simple pixel color threshold
					if(matrix[j][i] == GrayMatrix.BLACK) {
						histogram[hi]++;
					}
				}
			}
			
			// analyze histogram and find borders of new rectangles
			normalizeHistogram(histogram, rect);
			
			// find the the biggest gap
			Point biggestGap = findBiggestGap(histogram);
			
			if(biggestGap.y > 0) {
				result = new ArrayList<>();
				
				if(biggestGap.x > 0) {
					result.add(new Rectangle(rect.x, rect.y, rect.width, biggestGap.x));
				}
				int secondSize = rect.height-(biggestGap.x+biggestGap.y);
				if(secondSize > 0) {
					result.add(new Rectangle(rect.x, rect.y+biggestGap.x+biggestGap.y, rect.width, secondSize));
				}
			}
		}
		
		return result;
	}
	
	private List<Rectangle> XYhorizontal(int[][] matrix, Rectangle rect) {
		List<Rectangle> result = null;
		
		// minimum rectangle height threshold
		if(rect.height > 10) {
			// boundary
			int left = rect.x;
			int right = rect.x+rect.width;
			int up = rect.y;
			int down = rect.y+rect.height;
			
			// histogram of object frequency
			int[] histogram = new int[rect.width];
			
			// iterate over vertical axis and for line calculate object frequency 
			for (int i = left, hi = 0; i < right; i++, hi++) {
				histogram[hi] = 0;
				// go through line
				for (int j = up; j < down; j++) {
					// check simple pixel color threshold
					if(matrix[i][j] == GrayMatrix.BLACK) {
						histogram[hi]++;
					}				
				}
			}
			
			// analyze histogram and find borders of new rectangles
			normalizeHistogram(histogram, rect);
			
			// find the the biggest gap
			Point biggestGap = findBiggestGap(histogram);
			
			if(biggestGap.y > 0) {
				result = new ArrayList<>();
				
				if(biggestGap.x > 0) {
					result.add(new Rectangle(rect.x, rect.y, biggestGap.x, rect.height));
				}
				int secondSize = rect.width-(biggestGap.x+biggestGap.y);
				if(secondSize > 0) {
					result.add(new Rectangle(rect.x+biggestGap.x+biggestGap.y, rect.y, secondSize, rect.height));
				}
			}
		}
		
		return result;
	}

	private void normalizeHistogram(int[] histogram, Rectangle rect) {
		
		int frequencyThreshold = 0;//rect.width/20;
		for (int i = 0; i < histogram.length; i++) {
			// threshold of object frequency
			if(histogram[i] > frequencyThreshold) {
				histogram[i] = 1;
			} else {
				histogram[i] = 0;
			}
		}
	}

	private Point findBiggestGap(int[] histogram) {
		int biggestGapStart = -1, biggestGapSize = 0;
		int actGapStart = 0, actGapSize = 0;
		int actWidgetSize = 0;
		int prev = 1;
		for (int i = 0; i < histogram.length; i++) {
			if(histogram[i] == 0) {
				if(prev == 1) {
					if(actWidgetSize < 3) {
						// ignore small widgets
						actGapSize += actWidgetSize+1;
					} else {
						// start of new gap
						actGapSize = 1;
						actGapStart = i;
					}
				} else {
					// continuation of gap
					actGapSize++;
				}
			} else {
				if(prev == 0) {
					actWidgetSize = 1;
				} else {
					actWidgetSize++;
					if(actWidgetSize >= 3) {
						if(actGapSize > biggestGapSize) {
							biggestGapStart = actGapStart;
							biggestGapSize = actGapSize;
						}
					}
				}
			}
			prev=histogram[i];
		}
		
		return new Point(biggestGapStart, biggestGapSize);
	}
	
	

	@Override
	public String getName() {
		return "XY-cut";
	}

}
