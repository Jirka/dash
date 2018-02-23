package cz.vutbr.fit.dashapp.segmenation;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Vector;

import com.tree.TreeNode;

import ac.essex.ooechs.imaging.commons.edge.hough.HoughLine;
import ac.essex.ooechs.imaging.commons.edge.hough.HoughLine.Orientation;
import ac.essex.ooechs.imaging.commons.edge.hough.HoughTransform;
import cz.vutbr.fit.dashapp.image.util.HistogramUtils;
import cz.vutbr.fit.dashapp.image.util.PosterizationUtils;
import cz.vutbr.fit.dashapp.model.Dashboard;
import cz.vutbr.fit.dashapp.model.GraphicalElement;
import cz.vutbr.fit.dashapp.segmenation.Region.HierarchyComparator;
import cz.vutbr.fit.dashapp.util.MathUtils;
import cz.vutbr.fit.dashapp.util.matrix.ColorMatrix;
import cz.vutbr.fit.dashapp.util.matrix.GrayMatrix;
import cz.vutbr.fit.dashapp.util.matrix.GrayMatrix.ThresholdCalculator;
import cz.vutbr.fit.dashapp.util.matrix.MatrixUtils;
import extern.ImagePreview;

public class XYCut4 implements ISegmentationAlgorithm {
	
	// ----------------- internal for debugging
	
	private DebugMode debugMode;
	private Map<String, BufferedImage> debugMatrices;
	
	public static enum DebugMode {
		NONE,
		SILENT,
		INTERACTIVE
	}
	
	public XYCut4() {
		this(DebugMode.INTERACTIVE);
	}
	
	public XYCut4(DebugMode debugMode) {
		this.debugMode = debugMode;
		if(debugMode == DebugMode.SILENT) {
			debugMatrices = new LinkedHashMap<>();
		}
	}
	
	private void debug(String name, BufferedImage image) {
		if(debugMode == DebugMode.INTERACTIVE) {
			new ImagePreview(image, name).openWindow(800,600,0.8);
		} else if(debugMode == DebugMode.SILENT) {
			debugMatrices.put(name, image);
		}
	}
	
	public Map<String, BufferedImage> getDebugImages() {
		return debugMatrices;
	}
	
	// ----------------------------------------------------------------------

	@Override
	public Dashboard processImage(BufferedImage image) {
		// convert buffered image to 2D array
		int[][] matrix = ColorMatrix.printImageToMatrix(image);
		
		final int w = image.getWidth();
		final int h = image.getHeight();
		final int a = w*h;
		
		// ------ gray posterized
		ColorMatrix.toGrayScale(matrix, false, false); // convert to gray scale
		PosterizationUtils.posterizeMatrix(matrix, 256/(int)(Math.pow(2, 6)), false); // 6 bits posterization
		int[][] rawMatrix = ColorMatrix.toGrayScale(matrix, true, true); // convert to raw values (0-255) for simple use
		
		// ------ edges (experiment)
		//int[][] edgesMatrix = GrayMatrix.edges(rawMatrix);
		//GrayMatrix.inverse(edgesMatrix, false);
		//debug("edges", GrayMatrix.printMatrixToImage(null, edgesMatrix));
		
		// ------ lines (experiment)
		//int[][] linesMatrix = GrayMatrix.lines(edgesMatrix, 20, 20);
		//debug("lines", GrayMatrix.printMatrixToImage(null, linesMatrix));
		
		// ------ median filter (experiment)
		//int[][] blurMatrix = GrayMatrix.medianFilter(rawMatrix, 1);
		//debug("blur", GrayMatrix.printMatrixToImage(null, blurMatrix));		
		
		// ------ sharpen (experiment)
		//int[][] sharpenMatrix = GrayMatrix.sharpen(rawMatrix);
		//int[][] sharpenEdgesMatrix = GrayMatrix.edges(sharpenMatrix);
		//GrayMatrix.inverse(sharpenEdgesMatrix, false);
		//new ImagePreview(GrayMatrix.printMatrixToImage(null, sharpenMatrix), "sharpen").openWindow(800,600,0.8);
		//new ImagePreview(GrayMatrix.printMatrixToImage(null, sharpenEdgesMatrix), "sharpen edges").openWindow(800,600,0.8);
		
		// ------ Hough Transform to detect lines (experiment)
		/*int[][] edgesMatrix2 = GrayMatrix.copy(edgesMatrix); // debug
		int[][] lineMatrix = GrayMatrix.newMatrix(w, h, GrayMatrix.WHITE); // debug
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
		debug("hough_lines", GrayMatrix.printMatrixToImage(null, lineMatrix));
		debug("hough_lines_edges", GrayMatrix.printMatrixToImage(null, edgesMatrix2));
		int[][] houghFileterMatrix = GrayMatrix.filterPixels(edgesMatrix, lineMatrix, true);
		debug("hough_lines_filtered", GrayMatrix.printMatrixToImage(null, houghFileterMatrix));*/
		
		// ------ threshold according to histogram (the most frequent values)
		// matrix can contain several color values (mostly 2 - 5)
		List<Integer> frequentValues = findFrequentValues(rawMatrix);
		int[][] frequentColorMatrix = threshold(rawMatrix, frequentValues);
		debug("histogram_70", GrayMatrix.printMatrixToImage(null, frequentColorMatrix));
		
		// ------ histogram threshold + edges + lines (experiment)
		//int[][] frequentColorMatrixEdges = GrayMatrix.edges(frequentColorMatrix);
		//GrayMatrix.inverse(frequentColorMatrixEdges, false);
		//frequentColorMatrixEdges = GrayMatrix.lines(frequentColorMatrixEdges, 20, 20);
		//debug("histogram_70_edges", GrayMatrix.printMatrixToImage(null, frequentColorMatrixEdges));
		
		// ------ find rectangle regions
		List<Region> regions = findRegions(frequentColorMatrix);
		
		// debug
		int[][] rectanglesMatrix = GrayMatrix.newMatrix(MatrixUtils.width(matrix), MatrixUtils.height(matrix), GrayMatrix.WHITE);
		int[][] rectangleTypesMatrix = GrayMatrix.newMatrix(MatrixUtils.width(matrix), MatrixUtils.height(matrix), GrayMatrix.WHITE);
		drawRegions(rectanglesMatrix, regions);
		drawRegionTypes(rectangleTypesMatrix, regions);
		debug("rectangles", GrayMatrix.printMatrixToImage(null, rectanglesMatrix));
		debug("rectangle types", GrayMatrix.printMatrixToImage(null, rectangleTypesMatrix));
		
		// ------ construct tree
		Region rootRegion = new Region(0, 0, w, h, -1);
		TreeNode<Region> root = constructTree(regions, rootRegion);
		
		// debug
		int[][] treeRectanglesMatrix = GrayMatrix.newMatrix(MatrixUtils.width(matrix), MatrixUtils.height(matrix), GrayMatrix.WHITE);
		for (TreeNode<Region> node : root) {
			//System.out.println(node.getIndent() + node.data);
			if(node.getLevel() < 4) {
				//System.out.println(node.data);
				drawRegionType(treeRectanglesMatrix, node.data);
			}
		}
		debug("tree rectangle types", GrayMatrix.printMatrixToImage(null, treeRectanglesMatrix));
		
		List<Region> mainRegions = getMainRegions(root); // result rectangles
		
		//XYstep(rawMatrix, new Rectangle(0, 0, w, h), rectangles, true); // first step of recursive XY-cut
		
		// create dashboard (represents graphical regions)
		Dashboard dashboard = new Dashboard();
		dashboard.setDimension(0, 0, w, h);
		
		for (Rectangle r : mainRegions) {
			dashboard.addChildGE(new GraphicalElement(dashboard, r.x, r.y, r.width, r.height));
		}
		
		return dashboard;
	}

	/**
	 * 
	 * @param matrix
	 * @return
	 */
	private List<Integer> findFrequentValues(int[][] matrix) {
		int a = MatrixUtils.area(matrix);
		
		 // make histogram
		int[] histogram = HistogramUtils.getGrayscaleHistogram(matrix);
		
		// find the most frequent value (possible background)
		int mostFrequentValue = HistogramUtils.findMax(histogram, -1);
		//System.out.println(mostFrequentValue + " " + (double) histogram[mostFrequentValue]/a);
		
		// store frequent value
		List<Integer> frequentValues = new ArrayList<>();
		frequentValues.add(mostFrequentValue);
		int sum = histogram[mostFrequentValue];
		
		// find another frequent values
		mostFrequentValue = HistogramUtils.findMax(histogram, mostFrequentValue); // find next frequent value
		while(
				(double) histogram[mostFrequentValue]/a >= 0.001 && (double) sum/a < 0.5 ||
				(double) histogram[mostFrequentValue]/a >= 0.05 && (double) sum/a < 0.6 ||
				(double) histogram[mostFrequentValue]/a >= 0.1 && (double) sum/a < 0.7
		) {
			frequentValues.add(mostFrequentValue);
			sum += histogram[mostFrequentValue];
			mostFrequentValue = HistogramUtils.findMax(histogram, mostFrequentValue); // find next frequent value
		}
		
		//System.out.println((double) sum/a);
		//int colorAmount=frequentValues.size()+1;
		//System.out.println(colorAmount);
		
		return frequentValues;
	}
	
	/**
	 * 
	 * @param matrix
	 * @param frequentValues
	 * @return
	 */
	private int[][] threshold(int[][] matrix, List<Integer> frequentValues) {
		final int w = MatrixUtils.width(matrix);
		final int h = MatrixUtils.height(matrix);
		double actColor = 225.0; // white color (255.0) is not appropriate for further debug purposes
		double colorInterval = actColor/frequentValues.size();
		int[][] frequentColorMatrix = new int[w][h];
		GrayMatrix.clearMatrix(frequentColorMatrix, GrayMatrix.BLACK);
		for (Integer frequentValue : frequentValues) {
			GrayMatrix.copyPixels(frequentColorMatrix, matrix, frequentValue, (int) actColor);
			actColor-=colorInterval;
		}
		
		//int colorAmount=frequentValues.size()+1;
		//debug("sort/" + (colorAmount > 4 ? "x" : colorAmount) + "/histogram_70", GrayMatrix.printMatrixToImage(null, frequentColorMatrix));
		
		return frequentColorMatrix;
	}
	
	/**
	 * 
	 * @param matrix
	 * @return
	 */
	private List<Region> findRegions(int[][] matrix) {		
		// get used values
		List<Integer> usedValues = HistogramUtils.getUsedValues(matrix);
		List<Region> regions = new ArrayList<>();
		for (Integer usedValue : usedValues) {
			List<Region> actRegions = findRegions(matrix, usedValue);
			regions.addAll(actRegions);
		}
		
		return regions;
	}

	/**
	 * 
	 * @param matrix
	 * @param color
	 * @return
	 */
	private List<Region> findRegions(int[][] matrix, int color) {
		final int mW = MatrixUtils.width(matrix);
		final int mH = MatrixUtils.height(matrix);
		
		// working copy
		int[][] workingCopy = GrayMatrix.copy(matrix);
		int[][] edgesMatrix = GrayMatrix.inverse(GrayMatrix.edges(matrix), false);
		
		// debug
		//int[][] workingCopy = GrayMatrix.newMatrix(mW, mH, GrayMatrix.WHITE);
		//GrayMatrix.copyPixels(workingCopy, matrix, color, GrayMatrix.BLACK);
		//debug("debug", GrayMatrix.printMatrixToImage(null, workingCopy));
		
		int markColor = -1;
		List<Region> regions = new ArrayList<>();
		for (int x = 0; x < mW; x++) {
			for (int y = 0; y < mH; y++) {
				if(workingCopy[x][y] == color) {
					Region region = processSeedPixel(workingCopy, x, y, color, markColor);
					
					// analyze region
					analyzeRegion(region, workingCopy, edgesMatrix, markColor);
					
					if((region.width > 2 && region.height > 2 && color == GrayMatrix.BLACK) ||
							region.width > 9 && region.height > 9 && color != GrayMatrix.BLACK) {
						//System.out.println(region);
						regions.add(region);
					}
					markColor--;
				}
			}
		}
		return regions;
	}

	/**
	 * 
	 * @param matrix
	 * @param i
	 * @param j
	 * @param color
	 * @param markColor
	 * @return
	 */
	private Region processSeedPixel(int[][] matrix, int i, int j, int color, int markColor) {
		int mW = MatrixUtils.width(matrix);
		int mH = MatrixUtils.height(matrix);
		
		// do flood fill algorithm
		Queue<Point> queue = new LinkedList<Point>();
        queue.add(new Point(i, j));
        int x1 = i, x2 = i, y1 = j, y2 = j;
        while (!queue.isEmpty()) {
            Point p = queue.remove();
            if ((p.x >= 0) && (p.x < mW) && (p.y >= 0) && (p.y < mH)) {
                if (matrix[p.x][p.y] == color) {
                	matrix[p.x][p.y] = markColor;
                	
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
        
        return new Region(x1, y1, x2-x1, y2-y1, color);
	}
	
	/**
	 * 
	 * @param r
	 * @param matrix
	 * @param edgesMatrix
	 * @param markColor
	 */
	private void analyzeRegion(Region r, int[][] matrix, int[][] edgesMatrix, int markColor) {
		int mW = MatrixUtils.width(matrix);
		int mH = MatrixUtils.height(matrix);
		int mA = mW*mH;
		
		int x1 = MathUtils.roundInRange(r.x, 0, mW);
		int x2 = MathUtils.roundInRange(r.x + r.width, 0, mW);
		int y1 = MathUtils.roundInRange(r.y, 0, mH);
		int y2 = MathUtils.roundInRange(r.y + r.height, 0, mH);
		int rA = r.width*r.height;
		int rO = 2*(r.width+r.height);
		
		int x2_last = x2-1;
		int y2_last = y2-1;
		
		int realArea = 0;
		int edgesCount = 0;
		
		// copy region to the special matrix representing borders (black) and area (mark color)
		//int[][] regionMatrix = GrayMatrix.newMatrix(mW, mH, GrayMatrix.WHITE);
		//boolean edgeFound;
		
		for (int x = x1; x < x2; x++) {
			for (int y = y1; y < y2; y++) {
				
				// real area increment
				if(matrix[x][y] == markColor) {
					realArea++;
					
					//regionMatrix[x][y] = markColor;
					
					// edges
					/*edgeFound = false;
					for (int i = x-1; i <= x+1 && !edgeFound; i++) {
						if(i >= 0 && i < mW) {
							for (int j = y-1; j <= y+1 && !edgeFound; j++) {
								// it is border pixel
								if(j >= 0 && j < mH) {
									if(matrix[i][j] != markColor) {
										edgeFound = true;
									}
								} else {
									edgeFound = true;
								}
							}
						} else {
							edgeFound = true;
						}
					}
					
					if(edgeFound) {
						regionMatrix[x][y] = GrayMatrix.BLACK;
					}*/
					
					// border edges
					if(x == x1 || x == x2_last || y == y1 || y == y2_last) {
						edgesCount++;
					}
				}
			}
		}
		
		// test left region border
		// TODO add heuristics for alignment of non-rectangle objects
		/*int borderCount = 0;
		int limit = mH/4;
		int x;
		for (x = x1; x < x2 && borderCount < limit; x++) {
			borderCount = 0;
			for(int y = y1; y < y2; y++) {
				if(matrix[x][y] == markColor) {
					borderCount++;
				}
			}	
		}*/		
		
		double usedArea = (double) realArea/rA;
		double usedEdges = (double) edgesCount/rO;
		
		// debug
		// System.out.println(r);
		// System.out.println("used area: " + (double) realArea/rA);
		// System.out.println("used edges: " + (double) edgesCount/rO);
		
		// heuristics to split and categorize regions
		if(usedEdges >= 0.7) {
			if(usedArea > 0.5) {
				// fill rectangle
				r.type = Region.R_FILL;
			} else if(usedArea > 0.1) {
				// medium rectangle
				r.type = Region.R_MEDIUM;
			} else {
				// border rectangle
				r.type = Region.R_BORDER;
			}
		} else {
			if((double) rA/mA > 0.01) {
				// larger ambiguous rectangles
				r.type = Region.AMBIGUOUS;
				// TODO possible split
			} else {
				// small data rectangles
				r.type = Region.DATA;
			}
		}
	}
	
	/**
	 * 
	 * @param matrix
	 * @param regions
	 */
	private void drawRegions(int[][] matrix, List<Region> regions) {
		for (Region region : regions) {
			GrayMatrix.drawRectangle(matrix, region, region.color, true);
		}
	}
	
	/**
	 * 
	 * @param matrix
	 * @param regions
	 */
	private void drawRegionTypes(int[][] matrix, List<Region> regions) {
		for (Region region : regions) {
			drawRegionType(matrix, region);
		}
	}
	
	private void drawRegionType(int[][] matrix, Region region) {
		if(region.type == Region.R_FILL) {
			GrayMatrix.drawRectangle(matrix, region, 0, true);
		} else if(region.type == Region.R_MEDIUM) {
			GrayMatrix.drawRectangle(matrix, region, 0, true);
		} else if(region.type == Region.R_BORDER) {
			GrayMatrix.drawRectangle(matrix, region, 0, true);
		} else if(region.type == Region.DATA) {
			GrayMatrix.drawRectangle(matrix, region, 225, true);
		} else {
			GrayMatrix.drawRectangle(matrix, region, 125, true);
		}
	}
	
	/**
	 * 
	 * @param regions
	 * @return
	 */
	private TreeNode<Region> constructTree(List<Region> regions, Region rootRegion) {
		TreeNode<Region> rootNode = new TreeNode<>(rootRegion);
		
		HierarchyComparator regionComparator = new HierarchyComparator(null);
		
		for (Region region : regions) {
			regionComparator.setRegion(region);
			rootNode.insertToTree(region, regionComparator);
		}
		
		return rootNode;
	}
	
	private List<Region> getMainRegions(TreeNode<Region> root) {
		List<Region> mainRegions = new ArrayList<>(); // result rectangles
		
		Queue<TreeNode<Region>> queue = new LinkedList<>();
		queue.add(root);
		
		/*while (!queue.isEmpty()) {
			
		}*/
		
		return mainRegions;
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
