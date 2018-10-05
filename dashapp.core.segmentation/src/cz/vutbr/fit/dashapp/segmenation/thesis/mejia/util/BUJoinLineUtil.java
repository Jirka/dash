package cz.vutbr.fit.dashapp.segmenation.thesis.mejia.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cz.vutbr.fit.dashapp.model.Constants;
import cz.vutbr.fit.dashapp.segmenation.util.region.Region;
import cz.vutbr.fit.dashapp.util.matrix.GrayMatrix;
import cz.vutbr.fit.dashapp.util.matrix.MatrixUtils;

/**
 * 
 * @author Santiago Mejia (algorithms)
 * @author Jiri Hynek (code refactoring, modification of algorithms)
 *
 */
public class BUJoinLineUtil {
	
	public static int[][] joinLine(int[][] matrix, int dm, int offsetLimit) {
		return joinLine(matrix, dm, offsetLimit, -1);
	}
	
	/**
	 * Method creates list of potentially joinable regions beneath each other and according
	 * to the calculated threshold joins them to one region or leaves them separated.
	 * @param matrix
	 * @param w
	 * @param h
	 */	
	public static int[][] joinLine(int[][] matrix, int dm, int offsetLimit, int joinLimit) {
		int dm2 = Constants.getComplementDimension(dm);
		int limitTop = joinLimit;
		int limitBottom = joinLimit;
		
		List<Region> regions = BUBasicUtil.getRegions(matrix);
		
		// arrange regions into lists
		// check if regions are located in lines
		int lineOffsetLimit = 5;
		List<List<Region>> regionDoubleListTop = new ArrayList<List<Region>>();
		List<List<Region>> regionDoubleListBottom = new ArrayList<List<Region>>();
		boolean added = false;
		for (Region r : regions) {
			// try to add region to existing top list
			added = false;
			for (List<Region> currentRegionList : regionDoubleListTop) {
				if ((Math.abs(r.p(dm2) - currentRegionList.get(0).p(dm2)) < lineOffsetLimit)) {
					currentRegionList.add(r.copy());
					added = true;
					break;
				}
			}
			// region has not been added to any current list
			if (!added) {
				// create new list and add it to the last current list
				addAsLast(regionDoubleListTop, r);
			}
			
			// try to add region to existing bottom list
			added = false;
			for (List<Region> currentRegionList : regionDoubleListBottom) {
				if ((Math.abs((r.p(dm2) + r.size(dm2)) - (currentRegionList.get(0).p(dm2) + currentRegionList.get(0).size(dm2))) < offsetLimit)) {
					currentRegionList.add(r.copy());
					added = true;
					break;
				}
			}
			// region has not been added to any current list
			if (!added) {
				// create new list and add it to the last current list
				addAsLast(regionDoubleListBottom, r);
			}
		}

		// sort nested lists
		sortDoubleList(regionDoubleListTop, dm);
		sortDoubleList(regionDoubleListBottom, dm);

		// get join limit
		if(limitTop < 0) {
			limitTop = getLimit(regionDoubleListTop, matrix, dm);
		}
		if(limitBottom < 0) {
			limitBottom = getLimit(regionDoubleListBottom, matrix, dm);
		}

		// join regions
		List<Region> tmpRegionListTop = reDrawRowsOrColumns(regionDoubleListTop, limitTop, dm);
		List<Region> tmpRegionListBottom = reDrawRowsOrColumns(regionDoubleListBottom, limitBottom, dm);

		// print regions
		// TODO clear matrix?
		for (Region region : tmpRegionListTop) {
			MatrixUtils.drawRectangle(matrix, region, GrayMatrix.BLACK, false);
		}

		for (Region region : tmpRegionListBottom) {
			MatrixUtils.drawRectangle(matrix, region, GrayMatrix.BLACK, false);
		}

		return matrix;
	}
	
	private static void addAsLast(List<List<Region>> regionDoubleList, Region r) {
		List<Region> lastRegionList = new ArrayList<>();
		lastRegionList.add(r.copy());
		regionDoubleList.add(lastRegionList);
		
	}
	
	/**
	 * Method sorts List of regions contained in List according to their
	 * x or y coordinate.
	 * @param inRegionDoubleList
	 * @param dm
	 */	
	private static void sortDoubleList(List<List<Region>> inRegionDoubleList, int dm) {
		for (List<Region> currentList : inRegionDoubleList) {
			
			Collections.sort(currentList, new Comparator<Region>() {

				@Override
				public int compare(Region r1, Region r2) {
					return r1.p(dm)-r2.p(dm);
				}
			});
		}
	}
	
	/**
	 * Method calculates limit of distance between rows and columns
	 * according to the direction given.
	 * @param inRegionList
	 * @param dm
 	 * @param w
	 * @param h
	 */	
	private static int getLimit(List<List<Region>> inRegionList, int[][] matrix, int dm) {
		int size = dm == Constants.X ? MatrixUtils.width(matrix) : MatrixUtils.height(matrix);
		int limit, count;
		limit = count = 0;
		// go through all nested lists
		for (List<Region> currentList : inRegionList) {
			if (currentList.size() > 1) {
				Region r_prev = null;
				// go through all regions
				for (Region r : currentList) {
					if(r_prev != null) {
						int diff = r.p(dm) - (r_prev.p2(dm));
						// TODO: can be optimized for every dimension in different way
						if (diff > 0 && diff < size * 0.3) {
							limit += diff;
							count++;
						}
					}
					r_prev = r;
				}
			}
		}
		if (count == 0)
			return 0;
		// TODO: can be optimized for every dimension in different way
		limit = limit / count;
		limit *= 0.5;
		return limit;
	}
	
	/**
	 * Method connects regions in a rows or columns, according to the direction
	 *  give, if the distance is smaller than the limit.
	 * @param regionDoubleList
	 * @param limit
	 * @param dm
 	 * @param w
	 * @param h
	 */	
	private static List<Region> reDrawRowsOrColumns(List<List<Region>> regionDoubleList, int limit, int dm) {
		List<Region> resultList = new ArrayList<>();
		Region r_prev, r_join; // previous and actual join region
		
		// go through all nested lists
		for (List<Region> currentList : regionDoubleList) {
			// initialize iteration variables for every nested list
			r_prev = null;
			r_join = null;
			// go through all regions in nested list and find sequences which can be joined
			for (Region r : currentList) {
				if (r_prev != null && (r.p(dm) - (r_prev.p2(dm)) < limit)) {
					// distance between actual and previous region is lower than the limit
					// we can join the actual region with the previous one (update the actual r_join)
					r_join.x = Math.min(r_join.x, r.x);
					r_join.y = Math.min(r_join.y, r.y);
					r_join.width = Math.max(r_join.x2(), r.x2())-r_join.x;
					r_join.height = Math.max(r_join.y2(), r.y2())-r_join.y;
				} else {
					// start of new sequence
					// create new r_join and add it to the result list
					r_join = r.copy();
					resultList.add(r_join);
				}
				// continue to next iteration
				r_prev = r;
			}
		}
		return resultList;
	}

}
