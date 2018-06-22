package cz.vutbr.fit.dashapp.segmenation.util.region;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Jiri Hynek
 *
 */
public class RegionsOverlapUtil {
	
	/**
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
						analysedRegion.intersects++;
					}
				}
				if(analysedRegion.intersects == 0) {
					resultRegions.add(analysedRegion);
				} else {
					intersectRegions.add(analysedRegion);
			}
		}
		
		// filter intersections
		while(!intersectRegions.isEmpty()) {
			processHighestIntersection(dashboard, intersectRegions, resultRegions);
		}
		
		return resultRegions;
	}

	/**
	 * 
	 * @param dashboard
	 * @param intersectRegions - regions that has intersection
	 * @param resultRegions - regions which will be drawn
	 */
	private static void processHighestIntersection(Region dashboard, List<Region> intersectRegions, List<Region> resultRegions) {
		// find max intersect region
		Region region1 = intersectRegions.get(0);
		System.out.println("actual region: " + region1.x + " " + region1.y + " " + region1.width + " " + region1.height);
		for (Region region : intersectRegions) {
			if(region.intersects > region1.intersects) {
				region1 = region;
			}
		}
		
		List<Region> actIntersections = getIntersectRegions(region1, intersectRegions);
		int allIntersectArea = 0;
		for (Region region : actIntersections) {
			allIntersectArea += region.tmpIntersectArea;
		}
		
		System.out.println("region area: " + region1.area() + " " + (double) allIntersectArea/region1.area());
		
		// filter large region
		if(!((double) region1.area()/dashboard.area() > 0.5 && (double) allIntersectArea/region1.area() > 0.33)) {
			// regions that can be joined with region1 
			List<Region> joinRegions = new ArrayList<>();
			List<Region> ignore = new ArrayList<>();
			Region join = new Region(region1.x, region1.y, region1.width, region1.height, Region.JOIN);
			boolean isJoin = true;
			while(isJoin) {
				
				// get regions which intersects max intersect region
				actIntersections = getIntersectRegions(region1, intersectRegions);
				
				// look for regions that can be joined
				joinRegions.clear();
				for (Region region2 : actIntersections) {
					double share1 = (double) region2.tmpIntersectArea/region1.area();
					double share2 = (double) region2.tmpIntersectArea/region2.area();
					if((share1 >= 0.85 && share2 >= 0.85) || 
						(share1 <= 0.15 && share2 >= 0.85) || 
						(share2 <= 0.15 && share1 >= 0.85)) {
						// join
						join.x = Math.min(join.x, region2.x);
						join.y = Math.min(join.y, region2.y);
						join.width = Math.max(join.x+join.width, region2.x+region2.width)-join.x;
						join.height = Math.max(join.y+join.height, region2.y+region2.height)-join.y;
						joinRegions.add(region2);
					}
				}
				
				// join regions
				isJoin = false;
				if(!joinRegions.isEmpty()) {
					// remove join regions from intersetion lists
					for (Region region2 : joinRegions) {
						intersectRegions.remove(region2);
						actIntersections.remove(region2);
					}
					// update region 1
					region1.x = join.x;
					region1.y = join.y;
					region1.width = join.width;
					region1.height = join.height;
					region1.intersects = actIntersections.size();
					// we try to look for another join regions
					isJoin = true;
				}
			}
			
			actIntersections = getIntersectRegions(region1, intersectRegions);
			
			// regions that can be ignored
			for (Region region2 : actIntersections) {
				double share1 = (double) region2.tmpIntersectArea/region1.area();
				double share2 = (double) region2.tmpIntersectArea/region2.area();
				 if(
						 //(share1 <= 0.15 && share2 > 0.15 && share2 < 0.85) ||
						 (share1 <= 0.15 && share2 <= 0.33) ||
						 (share2 <= 0.15 && share1 <= 0.33)
					) {
					ignore.add(region2);
				}
			}
			
			if(!ignore.isEmpty()) {
				for (Region region2 : ignore) {
					actIntersections.remove(region2);
					// we can't remove them from intersect regions since they might intersect another regions
				}
			}
			
			if(actIntersections.size() == 0) {
				resultRegions.add(region1);
			} else if(actIntersections.size() == 1) {
				// two region intersection
				Region region2 = actIntersections.get(0);
				double share1 = (double) region2.tmpIntersectArea/region1.area();
				double share2 = (double) region2.tmpIntersectArea/region2.area();
				
				// similarity
				double similarity1 = similarity(region1, resultRegions);
				double similarityRest = similarity(actIntersections, resultRegions);
				
				// one in another
				if(share1 > 0.95 || share2 > 0.95) {
					// join 
					join.x = Math.min(region1.x, region2.x);
					join.y = Math.min(region1.y, region2.y);
					join.width = Math.max(region1.x+region1.width, region2.x+region2.width)-join.x;
					join.height = Math.max(region1.y+region1.height, region2.y+region2.height)-join.y;
					// check similarity
					intersectRegions.remove(region2);
					resultRegions.add(join);
				} else {
					resultRegions.add(region1);
				}
				
				// region is hidden in larger region
			} else if(actIntersections.size() > 1) {
				// intersection of more regions
				
				// big wrong region
				if(region1.area() > dashboard.area()*0.25 || region1.width > dashboard.width*0.8 || region1.height > dashboard.height*0.8) {
					intersectRegions.remove(region1);
					return;
				}
			}
		}
		
		intersectRegions.remove(region1);
	}
	
	/**
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
	 * 
	 * @param actIntersections
	 * @param resultRegions
	 * @return
	 */
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
