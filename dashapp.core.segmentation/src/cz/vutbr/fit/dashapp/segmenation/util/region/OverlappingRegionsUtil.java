package cz.vutbr.fit.dashapp.segmenation.util.region;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

/**
 * Help methods which analyze and arrange overlapping regions.
 * 
 * @author Jiri Hynek
 *
 */
public class OverlappingRegionsUtil {
	
	/**
	 * Methods analyzes intersections of main regions and join highly overlapping regions.
	 * 
	 * @param dashboard
	 * @param mainRegions
	 * @return
	 */
	public static List<Region> arrangeOverlaps(Region dashboard, List<Region> mainRegions) {
		List<Region> resultRegions = new ArrayList<>();
		List<Region> intersectRegions = new ArrayList<>();
		
		// find independent regions and intersect regions
		for (Region analysedRegion : mainRegions) {
				for (Region secondRegion : mainRegions) {
					if(analysedRegion != secondRegion && analysedRegion.intersects(secondRegion)) {
						analysedRegion.tmpNumberOfIntersections++;
					}
				}
				if(analysedRegion.tmpNumberOfIntersections == 0) {
					resultRegions.add(analysedRegion);
					analysedRegion.category = Region.CATEGORY_MAIN;
				} else {
					intersectRegions.add(analysedRegion);
			}
		}
		
		// iteratively process all intersections
		while(!intersectRegions.isEmpty()) {
			processHighestIntersection(dashboard, intersectRegions, resultRegions);
		}
		
		return resultRegions;
	}

	/**
	 * Method process region which contains the highest number of intersections.
	 * 
	 * @param dashboard
	 * @param intersectRegions - regions that has intersection
	 * @param resultRegions - regions which will be drawn
	 */
	private static void processHighestIntersection(Region dashboard, List<Region> intersectRegions, List<Region> resultRegions) {
		// find max intersect region
		Region region1 = intersectRegions.get(0);
		for (Region region : intersectRegions) {
			if(region.tmpNumberOfIntersections > region1.tmpNumberOfIntersections) {
				region1 = region;
			}
		}
		
		// measure sum of intersected area
		// TODO consider overlaps of more than 2 regions (print into matrix and calculate intersection pixels)
		// the sum might be inaccurate
		int allIntersectArea = 0;
		List<Region> actIntersectRegions = getIntersectRegions(region1, intersectRegions);
		for (Region region : actIntersectRegions) {
			allIntersectArea += region.tmpIntersectArea;
		}
		
		// debug
		//System.out.println("actual region: " + region1.x + " " + region1.y + " " + region1.width + " " + region1.height);
		//System.out.println("region area: " + region1.area() + " " + (double) allIntersectArea/region1.area());
		
		// Heuristic 1: filter large region which intersects other regions
		// region was probably incorrectly detected
		// (caused by insufficient pre-processing of image, low quality of image, etc...)
		if(!((double) region1.area()/dashboard.area() > 0.5 && (double) allIntersectArea/region1.area() > 0.33)) {
			
			// Heuristic 2: join highly overlapping regions
			// a) two highly overlapping regions
			// b) one region overlaps highly and the second one little (one small region intersects a big region)
			region1 = joinRegions(region1, intersectRegions, resultRegions);
			
			// Heuristic 3: filter insignificant intersections (due to size of regions)
			// Overlapping layouts might contain unarranged objects which might overlap with each other
			actIntersectRegions = getSignifficantIntersections(region1, intersectRegions);
			
			// test final region and remaining intersections
			if(actIntersectRegions.size() == 0) {
				resultRegions.add(region1);
				region1.category = Region.CATEGORY_MAIN;
			} else if(actIntersectRegions.size() == 1) {
				// Heuristic 4: test possibility that region is hidden in larger region
				testEncapsulation(region1, actIntersectRegions.get(0), intersectRegions, resultRegions);
			} else if(actIntersectRegions.size() > 1) {
				// Heuristic 5: the region still overlaps more regions -> filter such region
				
				/*if(region1.area() > dashboard.area()*0.25 || region1.width > dashboard.width*0.8 || region1.height > dashboard.height*0.8) {
					intersectRegions.remove(region1);
					return;
				}*/
			}
		}
		
		// finally, remove the region1 from the list intersections
		intersectRegions.remove(region1);
	}
	
	/**
	 * 
	 * @param region1
	 * @param region2
	 */
	private static void testEncapsulation(Region region1, Region region2, List<Region> intersectRegions, List<Region> resultRegions) {
		// analyze share of intersect area in region area
		double share1 = (double) region2.tmpIntersectArea/region1.area();
		double share2 = (double) region2.tmpIntersectArea/region2.area();
		
		// similarity (experimental)
		//double similarity1 = similarity(region1, resultRegions);
		//double similarityRest = similarity(actIntersections, resultRegions);
		
		// one within the second one
		Region joinRegion = new Region(region1.x, region1.y, region1.width, region1.height, Region.TYPE_JOIN);
		if(share1 > 0.95 || share2 > 0.95) {
			// join 
			joinRegion.x = Math.min(region1.x, region2.x);
			joinRegion.y = Math.min(region1.y, region2.y);
			joinRegion.width = Math.max(region1.x+region1.width, region2.x+region2.width)-joinRegion.x;
			joinRegion.height = Math.max(region1.y+region1.height, region2.y+region2.height)-joinRegion.y;
			
			intersectRegions.remove(region2);
			resultRegions.add(joinRegion);
			joinRegion.category = Region.CATEGORY_MAIN;
		} else {
			resultRegions.add(region1);
			joinRegion.category = Region.CATEGORY_MAIN;
		}
	}

	/**
	 * Returns list of regions which intersects region1.
	 * It also stores measures intersection area of every found region.
	 * 
	 * @param region1
	 * @param intersectRegions
	 * @return
	 */
	private static List<Region> getIntersectRegions(Region region1, List<Region> intersectRegions) {
		List<Region> actIntersections = new ArrayList<>();
		
		for (Region region2 : intersectRegions) {
			if(region2 != region1) {
				if(region1.intersects(region2)) {
					Rectangle iR = region1.intersection(region2);
					int iA = iR.width*iR.height;
					region2.tmpIntersectArea = iA;
					actIntersections.add(region2);
				}
			}
		}
		return actIntersections;
	}

	/**
	 * Method finds regions which can be joined with region1.
	 * 
	 * a) two highly overlapping regions
	 * b) one region overlaps highly and the second one little (one smaller region intersects a bigger region)
	 * 
	 * @param region1
	 * @param intersectRegions
	 * @param resultRegions
	 * @return
	 */
	private static Region joinRegions(Region region1, List<Region> intersectRegions, List<Region> resultRegions) {
		// regions which can be joined with region1
		List<Region> joinRegions = new ArrayList<>();
		
		// initialize join region
		Region joinRegion = region1.copy();
		joinRegion.type = Region.TYPE_JOIN;
		
		// iteratively find candidate regions which can be joined with region1
		// list of candidate regions can be different in every iteration
		boolean isJoin = true; // flag which indicates change of region1 -> we need to analyze new candidate regions
		while(isJoin) {
			// get actual regions which intersects max intersect region
			List<Region> actIntersectRegions = getIntersectRegions(region1, intersectRegions);
			
			// look for regions that can be joined
			joinRegions.clear();
			for (Region region2 : actIntersectRegions) {
				// heuristics...
				double share1 = (double) region2.tmpIntersectArea/region1.area();
				double share2 = (double) region2.tmpIntersectArea/region2.area();
				if((share1 >= 0.85 && share2 >= 0.85) || 
					(share1 <= 0.15 && share2 >= 0.85) || 
					(share2 <= 0.15 && share1 >= 0.85)) {
					// join
					joinRegion = joinRegion.joinWith(region2);
					joinRegions.add(region2);
				}
			}
			
			// join regions
			isJoin = false;
			if(!joinRegions.isEmpty()) {
				// remove join regions from intersetion lists
				for (Region region2 : joinRegions) {
					intersectRegions.remove(region2);
					actIntersectRegions.remove(region2);
				}
				// update region 1
				intersectRegions.remove(region1);
				region1 = joinRegion;
				region1.tmpNumberOfIntersections = actIntersectRegions.size();
				intersectRegions.add(region1);
				// we try to look for another join regions
				isJoin = true;
			}
		}
		
		return region1;
	}
	
	/**
	 * Method filters small intersections.
	 * 
	 * @param region1
	 * @param intersectRegions
	 * @return 
	 */
	private static List<Region> getSignifficantIntersections(Region region1, List<Region> intersectRegions) {
		// regions which can be ignored
		List<Region> ignoreRegions = new ArrayList<>();
		
		List<Region> actIntersectRegions = getIntersectRegions(region1, intersectRegions);
		for (Region region2 : actIntersectRegions) {
			double share1 = (double) region2.tmpIntersectArea/region1.area();
			double share2 = (double) region2.tmpIntersectArea/region2.area();
			 if(
					 //(share1 <= 0.15 && share2 > 0.15 && share2 < 0.85) ||
					 (share1 <= 0.15 && share2 <= 0.33) ||
					 (share2 <= 0.15 && share1 <= 0.33)
				) {
				ignoreRegions.add(region2);
			}
		}
		
		if(!ignoreRegions.isEmpty()) {
			for (Region region2 : ignoreRegions) {
				actIntersectRegions.remove(region2);
				// we can't remove them from intersect regions since they might intersect another regions
			}
		}
		
		return actIntersectRegions;
	}
	
	/**
	 * For experimental purposes.
	 * 
	 * @param actIntersections
	 * @param resultRegions
	 * @return
	 */
	@SuppressWarnings("unused")
	private static double similarity(List<Region> actIntersections, List<Region> resultRegions) {
		double rank = 0.0;
		for (Region region : actIntersections) {
			rank += similarity(region, resultRegions);
		}
		return rank/actIntersections.size();
	}
	
	/**
	 * 
	 * @param region
	 * @param resultRegions
	 * @return
	 */
	private static double similarity(Region region, List<Region> resultRegions) {
		int rank = 0;
		for (Region resultRegion : resultRegions) {
			rank = rank + (region.type == resultRegion.type ? 4 : 0);
			rank = rank + (Math.abs(region.width-resultRegion.width) < 2 ? 1 : 0);
			rank = rank + (Math.abs(region.height-resultRegion.height) < 2 ? 1 : 0);
			rank = rank + (Math.abs(region.x-resultRegion.x) < 2 ? 1 : 0);
			rank = rank + (Math.abs(region.y-resultRegion.y) < 2 ? 1 : 0);
		}
		return (double) rank/(8*resultRegions.size());
	}

}
