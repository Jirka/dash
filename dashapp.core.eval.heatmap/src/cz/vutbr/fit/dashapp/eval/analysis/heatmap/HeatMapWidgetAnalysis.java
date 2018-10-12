package cz.vutbr.fit.dashapp.eval.analysis.heatmap;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Vector;

import ac.essex.ooechs.imaging.commons.edge.hough.HoughLine;
import ac.essex.ooechs.imaging.commons.edge.hough.HoughLine.Orientation;
import ac.essex.ooechs.imaging.commons.edge.hough.HoughTransform;
import cz.vutbr.fit.dashapp.model.Dashboard;
import cz.vutbr.fit.dashapp.model.GraphicalElement;
import cz.vutbr.fit.dashapp.model.GraphicalElement.GEType;
import cz.vutbr.fit.dashapp.model.WorkspaceFolder;
import cz.vutbr.fit.dashapp.util.DashboardCollection;
import cz.vutbr.fit.dashapp.util.matrix.GrayMatrix;
import cz.vutbr.fit.dashapp.util.matrix.MatrixUtils;
import cz.vutbr.fit.dashapp.util.matrix.StatsUtils;
import cz.vutbr.fit.dashapp.util.matrix.GrayMatrix.EntrophyNormalization;
import cz.vutbr.fit.dashapp.util.matrix.GrayMatrix.ThresholdCalculator;

public class HeatMapWidgetAnalysis extends AbstractHeatMapAnalysis {
	
	public static final String LABEL = "Heatmap Widget Detection";
	public static final String NAME = "widget";
	public static final String FILE = "_" + NAME;
	
	// dynamic threshold is preferred
	private static final double DEFAULT_THRESHOLD = 0.8;
	
	// enable/disable according to requirements
	public boolean enable_basic_output = true;
	public boolean enable_basic_body_output = true;
	public boolean enable_borders_output = true;
	public boolean enable_borders_body_output = true;
	public boolean enable_act_folder_output = true;
	public boolean enable_all_folder_output = true;
	public boolean enable_custom_threshold = false;
	public double threshold = DEFAULT_THRESHOLD;
	public String inputFilesRegex = DEFAULT_FILE_REGEX;
	public String outputFolderPath = DEFAULT_OUTPUT_PATH + NAME;
	public String outputFile = FILE;
	
	private int actDashboardsCount;
	//private EntrophyNormalization entrophyCalculator;
	
	/*double actHeatMean;
	double actInversedEntrophyMean;*/
	private double actThreshlod;
	
	public HeatMapWidgetAnalysis() {
		init();
	}
	
	@Override
	public String getLabel() {
		return LABEL;
	}
	
	@Override
	public void init() {
	}
	
	@Override
	public void processFolder(WorkspaceFolder actWorkspaceFolder) {
		DashboardCollection actDashboards = getDashboardCollection(actWorkspaceFolder, inputFilesRegex);
		int matrix[][] = actDashboards.printDashboards(null, false);
		if(enable_basic_output) {
			processHeatMap(actWorkspaceFolder, actDashboards, matrix, FILE_SUFFIX_BASIC);
		}
		if(enable_basic_body_output) {
			processHeatMap(actWorkspaceFolder, actDashboards, cropMatrix(actWorkspaceFolder, matrix), FILE_SUFFIX_BASIC_BODY);
		}
		matrix = actDashboards.printDashboards(null, true);
		if(enable_borders_output) {
			processHeatMap(actWorkspaceFolder, actDashboards, matrix, FILE_SUFFIX_BORDERS);
		}
		if(enable_borders_body_output) {
			processHeatMap(actWorkspaceFolder, actDashboards, cropMatrix(actWorkspaceFolder, matrix), FILE_SUFFIX_BORDERS_BODY);
		}
	}

	public void processHeatMap(WorkspaceFolder actWorkspaceFolder, DashboardCollection actDashboards,
			int[][] heatMap, String suffix) {
		this.actDashboardsCount = actDashboards.length;
		this.actThreshlod = getThreshold(actWorkspaceFolder, actDashboards, heatMap);
		int[][] heatNormMatrix = GrayMatrix.normalize(heatMap, actDashboards.length, true);
		int[][] thresholdMatrix = GrayMatrix.update(heatNormMatrix, new ThresholdCalculator((int)actThreshlod), false);
		BufferedImage image = GrayMatrix.printMatrixToImage(null, thresholdMatrix); // image for visualization of rectangles decomposition
		Dashboard dashboard = generateDashboard(thresholdMatrix, image);
		if(enable_act_folder_output || enable_all_folder_output) {
			//FileUtils.saveImage(image, actWorkspaceFolder.getPath(), FILE + "_tb1");
			//FileUtils.saveDashboard(dashboard, actWorkspaceFolder.getPath(), FILE + "_tb1");
			//FileUtils.saveImage(image, actWorkspaceFolder.getPath() + "/../_000-sum", actWorkspaceFolder.getFileName() + FILE + "_tb1");
			//FileUtils.saveDashboard(dashboard, actWorkspaceFolder.getPath() + "/../_000-sum", actWorkspaceFolder.getFileName() + FILE + "_tb1");
			
			// print detected rectangles as image
			int[][] widgetMatrix = new DashboardCollection(new Dashboard[] { dashboard }).printDashboards(GEType.ALL_TYPES);
			GrayMatrix.normalize(widgetMatrix, 1, false);
			BufferedImage widgetImage = GrayMatrix.printMatrixToImage(null, widgetMatrix);
			String thresholdLabel = "_" + String.format("%.2f", actThreshlod).replace('.', ',');
			if(enable_act_folder_output) {
				printImage(actWorkspaceFolder, widgetImage, actWorkspaceFolder.getPath(), outputFile + thresholdLabel + suffix);
				printDashboard(actWorkspaceFolder, dashboard, actWorkspaceFolder.getPath(), outputFile + thresholdLabel + suffix);
			}
			if(enable_all_folder_output) {
				printImage(actWorkspaceFolder, widgetImage, actWorkspaceFolder.getPath() + "/../" + outputFolderPath + "/" + NAME + suffix, actWorkspaceFolder.getFileName() + thresholdLabel + outputFile + suffix);
				printDashboard(actWorkspaceFolder, dashboard, actWorkspaceFolder.getPath() + "/../" + outputFolderPath + "/" + NAME + suffix, actWorkspaceFolder.getFileName() + thresholdLabel + outputFile + suffix);
			}
		}
	}
	
	private double getThreshold(WorkspaceFolder actWorkspaceFolder, DashboardCollection actDashboards, int[][] matrix) {
		if(enable_custom_threshold) {
			return threshold;
		} else {
			// provide dynamic threshold according to requirements
			int[][] heatNormMatrix = GrayMatrix.normalize(matrix, actDashboards.length, true);
			double actHeatMean = StatsUtils.meanValue(heatNormMatrix);
			int[][] entrophyNormMatrix = GrayMatrix.update(matrix, new EntrophyNormalization(actDashboardsCount), true);
			double actInversedEntrophyMean = GrayMatrix.WHITE-StatsUtils.meanValue(entrophyNormMatrix);
			return (actHeatMean+actInversedEntrophyMean)/2;
		}
	}

	@Override
	public void sumarizeFolders(WorkspaceFolder actWorkspaceFolder, List<WorkspaceFolder> analyzedFolders) {
		// do nothing
	}
	
	private Dashboard generateDashboard(int[][] thresholdMatrix, BufferedImage image) {
		int mW = MatrixUtils.width(thresholdMatrix);
		int mH = MatrixUtils.height(thresholdMatrix);
		
		// calculate edges
		int[][] edgesMatrix = GrayMatrix.edges(thresholdMatrix);
		
		// calculate graphical elements
		Dashboard dashboard = new Dashboard();
		dashboard.setDimension(0, 0, mW, mH);
		
		int color = -1;
		for (int i = 0; i < mW; i++) {
			for (int j = 0; j < mH; j++) {
				if(thresholdMatrix[i][j] == GrayMatrix.BLACK) {
					processSeedPixel(i, j, color, thresholdMatrix, edgesMatrix, image, dashboard);
					color--;
				}
			}
		}
		
		return dashboard;
	}

	private void processSeedPixel(int i, int j, int color, int[][] thresholdMatrix, int[][] edgesMatrix, BufferedImage image, Dashboard dashboard) {
		int mW = MatrixUtils.width(thresholdMatrix);
		int mH = MatrixUtils.height(thresholdMatrix);
		
		// do flood fill algorithm
		Queue<Point> queue = new LinkedList<Point>();
        queue.add(new Point(i, j));
        int x1 = i, x2 = i, y1 = j, y2 = j;
        while (!queue.isEmpty()) {
            Point p = queue.remove();
            if ((p.x >= 0) && (p.x < mW && (p.y >= 0) && (p.y < mH))) {
                if (thresholdMatrix[p.x][p.y] == GrayMatrix.BLACK) {
                	thresholdMatrix[p.x][p.y] = color;
                	
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
                }
            }
        }
        
        // construct filled area boundaries 
        Queue<Rectangle> rectangles = new LinkedList<Rectangle>();
        rectangles.add(new Rectangle(x1, y1, x2-x1, y2-y1));
        
        // analyze rectangle (divide into smaller rectangles if appropriate)
        while(!rectangles.isEmpty()) {
        	Rectangle rectangle = rectangles.remove();
        	// filter small objects
        	if(rectangle.width > 5 && rectangle.height > 5) {
        		int colorCount = getColorCount(rectangle, thresholdMatrix, color);
        		if(colorCount > 25) {
        			double colorShare = colorCount/((double)(rectangle.width*rectangle.height));
                	//double screenShare = colorCount/((double)(actWidth*actHeight));
                	
            		if(colorShare > 0.95) {
            			dashboard.addChildGE(new GraphicalElement(rectangle.x, rectangle.y, rectangle.width, rectangle.height));
                	} else {
                		// split rectangle...
                		
                		// use Hough Transform to detect lines 
                		HoughTransform t = new HoughTransform(mW, mH);
                		
                		// print pixels of important edges
                		findRectangleEdgesPoints(rectangle, thresholdMatrix, edgesMatrix, color, t);
                		
                		// find and filter lines
                		HoughLine line = null;
                		int precision = Math.min(rectangle.width, rectangle.height)/3; // depends on size of rectangle
                		while(line == null && precision > 2) {
                			Vector<HoughLine> lines = t.getLines(precision);
                    		line = getCandidateLine(lines, thresholdMatrix, color);
                    		precision -= 2; // decrease precision
                		}
                		
                		// divide rectangle according to line and update queue
                		if(line != null) {
                			Orientation orientation = line.getOrientation();
                			if(orientation == Orientation.H) {
                				int h1 = linePosition(line.r, orientation, mW, mH)-rectangle.y;
                				int h2 = rectangle.height-h1;
                				rectangles.add(new Rectangle(rectangle.x, rectangle.y, rectangle.width, h1));
                				rectangles.add(new Rectangle(rectangle.x, rectangle.y+h1, rectangle.width, h2));
                			} else if(orientation == Orientation.V) {
                				int w1 = linePosition(line.r, orientation, mW, mH)-rectangle.x;
                				int w2 = rectangle.width-w1;
                				rectangles.add(new Rectangle(rectangle.x, rectangle.y, w1, rectangle.height));
                				rectangles.add(new Rectangle(rectangle.x+w1, rectangle.y, w2, rectangle.height));
                			}
                			
                			// log the line to the outpu image
                			line.draw(image, Color.RED.getRGB());
                		} else {
                			if(colorShare > 0.75) {
                				dashboard.addChildGE(new GraphicalElement(rectangle.x, rectangle.y, rectangle.width, rectangle.height));
                			}
                		}
                	}
        		}
        	}
        }
	}
	
	private int getColorCount(Rectangle rectangle, int[][] outputMatrix, int color) {
		int count = 0;
		int x1 = rectangle.x;
		int x2 = x1+rectangle.width;
		int y1 = rectangle.y;
		int y2 = y1+rectangle.height;
		for (int ii = x1; ii < x2; ii++) {
			for (int jj = y1; jj < y2; jj++) {
				if(outputMatrix[ii][jj] == color) {
					count++;
				}
			}
		}
		return count;
	}

	private void findRectangleEdgesPoints(Rectangle rectangle, int[][] outputMatrix, int[][] edgesMatrix, int color, HoughTransform t) {
		int mW = MatrixUtils.width(outputMatrix);
		int mH = MatrixUtils.height(outputMatrix);
		
		int r_x1 = rectangle.x;
		int r_x2 = rectangle.x+rectangle.width;
		int r_y1 = rectangle.y;
		int r_y2 = rectangle.y+rectangle.height;
		//int objectMask[][] = new int[actWidth][actHeight];
		for (int ii = 0; ii < mW; ii++) {
			for (int jj = 0; jj < mH; jj++) {
				// do not consider border edges of selected rectangle area
				// filter 5 pixels of border space
				if(ii > r_x1+5 && ii < r_x2-5 && jj > r_y1+5 && jj < r_y2-5) {
					// consider only edges
					if(edgesMatrix[ii][jj] == 255) {
						if(outputMatrix[ii][jj] == color) {
							t.addPoint(ii, jj);
							//objectMask[ii][jj] = 0;
						} /*else {
							objectMask[ii][jj] = 255;
						}*/
					}/* else {
						objectMask[ii][jj] = 255;
					}*/
				}/* else {
					objectMask[ii][jj] = 255;
				}*/
			}
		}
		//return objectMask;
	}

	private HoughLine getCandidateLine(Vector<HoughLine> lines, int[][] outputMatrix, int color) {
		int mW = MatrixUtils.width(outputMatrix);
		int mH = MatrixUtils.height(outputMatrix);
		
		HoughLine resultLine = null;
		int crossedPixels = Math.max(mW, mW);
		for (HoughLine line : lines) {
			
			Orientation orientation = line.getOrientation();
			
			// is horizontal
			if(orientation == Orientation.H) {
				int linePositon = linePosition(line.r, orientation, mW, mH);
				int actCrossedPixels = Math.min(
						calculateCrossedPixels(0, linePositon-1, 1, 0, outputMatrix, color),
						calculateCrossedPixels(0, linePositon+1, 1, 0, outputMatrix, color)
					);
				if(actCrossedPixels < crossedPixels) {
					resultLine = line;
					crossedPixels = actCrossedPixels;
				}
			} else if(orientation == Orientation.V) {
				int linePositon = linePosition(line.r, orientation, mW, mH);
				int actCrossedPixels = Math.min(
						calculateCrossedPixels(linePositon-1, 0, 0, 1, outputMatrix, color),
						calculateCrossedPixels(linePositon+1, 0, 0, 1, outputMatrix, color)
					);
				if(actCrossedPixels < crossedPixels) {
					resultLine = line;
					crossedPixels = actCrossedPixels;
				}
			}
		}
		return resultLine;
	}
	
	private int calculateCrossedPixels(int x, int y, int dx, int dy, int[][] outputMatrix, int color) {
		int mW = MatrixUtils.width(outputMatrix);
		int mH = MatrixUtils.height(outputMatrix);
		
		int count = 0;
		while(x >= 0 && x < mW && y >= 0 && y < mH) {
			if(outputMatrix[x][y] == color) {
				count++;
			}
			x += dx;
			y += dy;
		}
		return count;
	}
	
	public int linePosition(double r, Orientation orientation, int width, int height) {
    	int houghHeight = (int) (Math.sqrt(2) * Math.max(height, width)) / 2; 
    	if(orientation == Orientation.V) {
    		return (int) (((r - houghHeight)) + width/2);
    	} else if(orientation == Orientation.H) {
    		return (int) (((r - houghHeight)) + height/2); 
    	}
    	return -1;
    }
}
