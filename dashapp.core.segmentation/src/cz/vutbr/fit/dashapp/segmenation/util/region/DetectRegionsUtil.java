package cz.vutbr.fit.dashapp.segmenation.util.region;

import java.util.LinkedList;
import java.util.List;

import cz.vutbr.fit.dashapp.image.floodfill.BasicFloodFill;
import cz.vutbr.fit.dashapp.image.util.HistogramUtils;
import cz.vutbr.fit.dashapp.util.matrix.GrayMatrix;

/**
 * 
 * @author Jiri Hynek
 *
 */
public class DetectRegionsUtil {
	
	/**
	 * Method finds all regions which consists of neighboring pixels of the same color.
	 * 
	 * @param matrix
	 * @return
	 */
	public static List<Region> findRegions(int[][] matrix) {
		// get used values
		List<Integer> usedValues = HistogramUtils.getUsedValues(matrix);
		List<Region> regions = new LinkedList<>();
		List<Region> actRegions;
		for (Integer usedValue : usedValues) {
			if(usedValue == GrayMatrix.BLACK) {
				actRegions = findRegions(matrix, usedValue, 3, 3);
			} else {
				actRegions = findRegions(matrix, usedValue, 10, 10);
			}
			regions.addAll(actRegions);
		}
		
		return regions;
	}

	/**
	 * 
	 * Method finds all regions which consists of neighboring pixels of specified color.
	 * 
	 * @param matrix
	 * @param color
	 * @return
	 */
	public static List<Region> findRegions(int[][] matrix, int color, int minWidth, int minHeight) {
		DetectRegionsFloodFill floodFill = new DetectRegionsFloodFill(matrix, color, minWidth, minHeight);
		floodFill.process();
		
		return floodFill.getRegions();
	}
	
	/**
	 * 
	 * @author Jiri Hynek
	 *
	 */
	public static class DetectRegionsFloodFill extends BasicFloodFill {
		
		private int minWidth;
		private int minHeight;
		List<Region> regions;
		
		public DetectRegionsFloodFill(int[][] matrix, int refColor, int minWidth, int minHeight) {
			super(matrix, true, refColor);
			this.minWidth = minWidth;
			this.minHeight = minHeight;
			regions = new LinkedList<>();
		}
		
		@Override
		protected void postProcessSeedPixel(int x1, int y1, int x2, int y2) {
			int w = x2-x1;
			int h = y2-y1;
			if(w >= minWidth && h >= minHeight) {
				//System.out.println(region);
				Region region = new Region(x1, y1, w, h, refColor);
				setRegionsProperties(x1, y1, x2, y2, region);
				regions.add(region);
			}
		}
		
		private void setRegionsProperties(int x1, int y1, int x2, int y2, Region region) {
			double areaShare = regionAreaShare(region);
			double usedArea = usedArea(x1, y1, x2, y2, region);
			double usedEdges = usedEdges(x1, y1, x2, y2, region);
			
			// heuristics to split and categorize regions
			if((double) areaShare < 0.01) {
				// small data rectangles
				region.type = Region.TYPE_DATA;
			} else {
				if(usedEdges >= 0.7) {
					if(usedArea > 0.5) {
						// fill rectangle
						region.type = Region.TYPE_RECT_FILL;
					} else if(usedArea > 0.1) {
						// medium rectangle
						region.type = Region.TYPE_RECT_MEDIUM;
					} else {
						// border rectangle
						region.type = Region.TYPE_RECT_BORDER;
					}
				} else {
					//if((double) rA/mA > 0.01) {
						// larger ambiguous rectangles
						region.type = Region.TYPE_AMBIGUOUS;
						// TODO possible split
					//} else {
					//	// small data rectangles
					//	r.type = Region.DATA;
					//}
				}
			}
		}
		
		private double regionAreaShare(Region region) {
			return (double) region.area()/(mW*mH);
		}

		private double usedArea(int x1, int y1, int x2, int y2, Region region) {
			return (double) area/region.area();
		}
		
		private double usedEdges(int x1, int y1, int x2, int y2, Region region) {
			int x2_last = x2-1;
			int y2_last = y2-1;
			int borderEdgesCount = 0;
			for (int x = x1; x < x2; x++) {
				for (int y = y1; y < y2; y++) {
					if(markMatrix[x][y] == markColor) {
						if(x == x1 || x == x2_last || y == y1 || y == y2_last) {
							borderEdgesCount++;
						}
					}
				}
			}
			return (double) borderEdgesCount/region.perimeter();
		}
		
		public List<Region> getRegions() {
			return regions;
		}
	}

}
