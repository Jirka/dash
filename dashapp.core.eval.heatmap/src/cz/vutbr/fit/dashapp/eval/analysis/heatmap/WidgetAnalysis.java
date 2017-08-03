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
import ac.essex.ooechs.imaging.commons.edge.hough.HoughTransform;
import ac.essex.ooechs.imaging.commons.edge.hough.HoughLine.Orientation;
import cz.vutbr.fit.dashapp.eval.analysis.AbstractAnalysis;
import cz.vutbr.fit.dashapp.model.Dashboard;
import cz.vutbr.fit.dashapp.model.GraphicalElement;
import cz.vutbr.fit.dashapp.model.GraphicalElement.GEType;
import cz.vutbr.fit.dashapp.model.WorkspaceFolder;
import cz.vutbr.fit.dashapp.util.DashboardCollection;
import cz.vutbr.fit.dashapp.util.FileUtils;
import cz.vutbr.fit.dashapp.util.matrix.GrayMatrix;
import cz.vutbr.fit.dashapp.util.matrix.StatsUtils;
import cz.vutbr.fit.dashapp.util.matrix.GrayMatrix.EntrophyNormalization;
import cz.vutbr.fit.dashapp.util.matrix.GrayMatrix.ThresholdCalculator;

public class WidgetAnalysis extends AbstractAnalysis {
	
	public static final String LABEL = "Widget Detection";
	public static final String FILE = "_widget";
	
	private int actDashboardsCount;
	EntrophyNormalization entrophyCalculator;
	
	double actHeatMean;
	double actInversedEntrophyMean;
	double actThreshlod;
	
	@Override
	public String getLabel() {
		return LABEL;
	}

	@Override
	public void processFolder(WorkspaceFolder actWorkspaceFolder, DashboardCollection actDashboards) {
		this.actDashboardsCount = actDashboards.length;
		int[][] printMatrix = actDashboards.printDashboards(null, true);
		int[][] heatMatrix = GrayMatrix.normalize(printMatrix, actDashboards.length, true);
		actHeatMean = StatsUtils.meanValue(heatMatrix);
		int[][] entrophyMatrix = GrayMatrix.update(printMatrix, new EntrophyNormalization(actDashboardsCount), true);
		actInversedEntrophyMean = GrayMatrix.WHITE-StatsUtils.meanValue(entrophyMatrix);
		actThreshlod = (actHeatMean+actInversedEntrophyMean)/2;
		int[][] thresholdMatrix = GrayMatrix.update(heatMatrix, new ThresholdCalculator((int)actThreshlod), false);
		BufferedImage image = GrayMatrix.printMatrixToImage(null, thresholdMatrix);
		Dashboard dashboard = generateDashboard(thresholdMatrix, image);
		//FileUtils.saveImage(image, actWorkspaceFolder.getPath(), FILE + "_tb1");
		//FileUtils.saveDashboard(dashboard, actWorkspaceFolder.getPath(), FILE + "_tb1");
		//FileUtils.saveImage(image, actWorkspaceFolder.getPath() + "/../_000-sum", actWorkspaceFolder.getFileName() + FILE + "_tb1");
		//FileUtils.saveDashboard(dashboard, actWorkspaceFolder.getPath() + "/../_000-sum", actWorkspaceFolder.getFileName() + FILE + "_tb1");
		int[][] widgetMatrix = new DashboardCollection(new Dashboard[] { dashboard }).printDashboards(GEType.ALL_TYPES);
		GrayMatrix.normalize(widgetMatrix, 1, false);
		BufferedImage widgetImage = GrayMatrix.printMatrixToImage(null, widgetMatrix);
		FileUtils.saveImage(widgetImage, actWorkspaceFolder.getPath() + "/../_000-widget", actWorkspaceFolder.getFileName().substring(1));
	}

	@Override
	public void sumarizeFolders(WorkspaceFolder actWorkspaceFolder, List<WorkspaceFolder> analyzedFolders) {
		// do nothing
	}
	
	private Dashboard generateDashboard(int[][] thresholdMatrix, BufferedImage image) {
		
		int width = thresholdMatrix.length;
		int height = thresholdMatrix[0].length;
		
		// calculate edges
		int[][] edgesMatrix = GrayMatrix.edges(thresholdMatrix);
		
		// calculate graphical elements
		Dashboard dashboard = new Dashboard();
		dashboard.setDimension(0, 0, width, height);
		
		int color = -1;
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				if(thresholdMatrix[i][j] == GrayMatrix.BLACK) {
					processSeedPixel(i, j, color, thresholdMatrix, edgesMatrix, image, dashboard);
					color--;
				}
			}
		}
		
		return dashboard;
	}

	private void processSeedPixel(int i, int j, int color, int[][] thresholdMatrix, int[][] edgesMatrix, BufferedImage image, Dashboard dashboard) {
		
		int actWidth = thresholdMatrix.length;
		int actHeight = thresholdMatrix[0].length;
		
		// do flood fill algorithm
		Queue<Point> queue = new LinkedList<Point>();
        queue.add(new Point(i, j));
        int x1 = i, x2 = i, y1 = j, y2 = j;
        while (!queue.isEmpty()) {
            Point p = queue.remove();
            if ((p.x >= 0) && (p.x < actWidth && (p.y >= 0) && (p.y < actHeight))) {
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
            			dashboard.addChildGE(new GraphicalElement(dashboard, rectangle.x, rectangle.y, rectangle.width, rectangle.height));
                	} else {
                		// split rectangle...
                		
                		// use Hough Transform to detect lines 
                		HoughTransform t = new HoughTransform(actWidth, actHeight);
                		
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
                				int h1 = linePosition(line.r, orientation, actWidth, actHeight)-rectangle.y;
                				int h2 = rectangle.height-h1;
                				rectangles.add(new Rectangle(rectangle.x, rectangle.y, rectangle.width, h1));
                				rectangles.add(new Rectangle(rectangle.x, rectangle.y+h1, rectangle.width, h2));
                			} else if(orientation == Orientation.V) {
                				int w1 = linePosition(line.r, orientation, actWidth, actHeight)-rectangle.x;
                				int w2 = rectangle.width-w1;
                				rectangles.add(new Rectangle(rectangle.x, rectangle.y, w1, rectangle.height));
                				rectangles.add(new Rectangle(rectangle.x+w1, rectangle.y, w2, rectangle.height));
                			}
                			
                			// log the line to the outpu image
                			line.draw(image, Color.RED.getRGB());
                		} else {
                			if(colorShare > 0.75) {
                				dashboard.addChildGE(new GraphicalElement(dashboard, rectangle.x, rectangle.y, rectangle.width, rectangle.height));
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
		int actWidth = outputMatrix.length;
		int actHeight = outputMatrix[0].length;
		
		int r_x1 = rectangle.x;
		int r_x2 = rectangle.x+rectangle.width;
		int r_y1 = rectangle.y;
		int r_y2 = rectangle.y+rectangle.height;
		//int objectMask[][] = new int[actWidth][actHeight];
		for (int ii = 0; ii < actWidth; ii++) {
			for (int jj = 0; jj < actHeight; jj++) {
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
		int actWidth = outputMatrix.length;
		int actHeight = outputMatrix[0].length;
		
		HoughLine resultLine = null;
		int crossedPixels = Math.max(actWidth, actWidth);
		for (HoughLine line : lines) {
			
			Orientation orientation = line.getOrientation();
			
			// is horizontal
			if(orientation == Orientation.H) {
				int linePositon = linePosition(line.r, orientation, actWidth, actHeight);
				int actCrossedPixels = Math.min(
						calculateCrossedPixels(0, linePositon-1, 1, 0, outputMatrix, color),
						calculateCrossedPixels(0, linePositon+1, 1, 0, outputMatrix, color)
					);
				if(actCrossedPixels < crossedPixels) {
					resultLine = line;
					crossedPixels = actCrossedPixels;
				}
			} else if(orientation == Orientation.V) {
				int linePositon = linePosition(line.r, orientation, actWidth, actHeight);
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
		int actWidth = outputMatrix.length;
		int actHeight = outputMatrix[0].length;
		
		int count = 0;
		while(x >= 0 && x < actWidth && y >= 0 && y < actHeight) {
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
