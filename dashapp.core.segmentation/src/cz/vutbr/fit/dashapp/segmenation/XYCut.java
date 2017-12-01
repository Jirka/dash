package cz.vutbr.fit.dashapp.segmenation;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import cz.vutbr.fit.dashapp.image.util.HistogramUtils;
import cz.vutbr.fit.dashapp.image.util.PosterizationUtils;
import cz.vutbr.fit.dashapp.model.Dashboard;
import cz.vutbr.fit.dashapp.model.GraphicalElement;
import cz.vutbr.fit.dashapp.util.matrix.ColorMatrix;
import cz.vutbr.fit.dashapp.util.matrix.GrayMatrix;
import cz.vutbr.fit.dashapp.util.matrix.GrayMatrix.ThresholdCalculator;
import extern.ImagePreview;

public class XYCut implements ISegmentationAlgorithm {

	@Override
	public Dashboard processImage(BufferedImage image) {
		// convert buffered image to 2D array
		int[][] matrix = ColorMatrix.printImageToMatrix(image);
		
		final int w = image.getWidth();
		final int h = image.getHeight();
		
		// image preprocessing
		ColorMatrix.toGrayScale(matrix, false, false); // convert to gray scale
		PosterizationUtils.posterizeMatrix(matrix, 256/(int)(Math.pow(2, 4)), false); // 4 bits posterization
		new ImagePreview(ColorMatrix.printMatrixToImage(null, matrix), "result 1").openWindow(800,600,0.8);
		
		int[][] rawMatrix = ColorMatrix.toGrayScale(matrix, true, true); // convert to raw values (0-255) for simple use
		int[] histogram = HistogramUtils.getGrayscaleHistogram(rawMatrix); // make histogram
		int mostFrequentValue = HistogramUtils.findMax(histogram, -1); // find most frequent value (possible background)
		if(mostFrequentValue < (GrayMatrix.WHITE/2)) {
			GrayMatrix.inverse(rawMatrix, false);
			mostFrequentValue = GrayMatrix.WHITE-mostFrequentValue;
		}
		GrayMatrix.update(rawMatrix, new ThresholdCalculator((int) mostFrequentValue-1), false); // threshold according to background
		new ImagePreview(GrayMatrix.printMatrixToImage(null, rawMatrix), "result 2").openWindow(800,600,0.8);
		rawMatrix = GrayMatrix.medianFilter(rawMatrix, 1); // median filter (remove noise)
		new ImagePreview(GrayMatrix.printMatrixToImage(null, rawMatrix), "result 2").openWindow(800,600,0.8);
		//rawMatrix = GrayMatrix.medianFilter(rawMatrix, 2); // median filter (remove noise)
		//new ImagePreview(GrayMatrix.printMatrixToImage(null, rawMatrix), "result 2").openWindow(800,600,0.8);
		
		int[][] edgesMatrix = GrayMatrix.edges(rawMatrix);
		GrayMatrix.inverse(edgesMatrix, false);
		new ImagePreview(GrayMatrix.printMatrixToImage(null, edgesMatrix), "result 2").openWindow(800,600,0.8);
		
		GrayMatrix.createRectangles(rawMatrix, false);
		GrayMatrix.createRectangles(rawMatrix, false);
		new ImagePreview(GrayMatrix.printMatrixToImage(null, rawMatrix), "result 2").openWindow(800,600,0.8);
		
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
