package cz.vutbr.fit.dashapp.segmenation.util.region;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.tree.TreeNode;

import cz.vutbr.fit.dashapp.segmenation.util.region.Region.HierarchyComparator;
import cz.vutbr.fit.dashapp.util.matrix.GrayMatrix;
import cz.vutbr.fit.dashapp.util.matrix.MatrixUtils;

public class ProcessRegionsUtil {
	
	public static TreeNode<Region> constructTree(List<Region> regions, int x, int y, int w, int h) {
		return constructTree(regions, new Region(0, 0, w, h, -1));
	}
	
	/**
	 * 
	 * @param regions
	 * @return
	 */
	public static TreeNode<Region> constructTree(List<Region> regions, Region rootRegion) {
		TreeNode<Region> rootNode = new TreeNode<>(rootRegion);
		
		HierarchyComparator regionComparator = new HierarchyComparator(null);
		
		for (Region region : regions) {
			regionComparator.setRegion(region);
			rootNode.insertToTree(region, regionComparator);
		}
		
		return rootNode;
	}
	
	/**
	 * 
	 * @param root
	 * @return
	 */
	public static List<Region> getMainRegions(TreeNode<Region> root) {
		List<Region> mainRegions = new ArrayList<>(); // result rectangles
		List<TreeNode<Region>> filteredDataNodes = new ArrayList<>();
		
		int rootW = root.data.width;
		int rootH = root.data.height;
		int rootA = rootW*rootH;
		
		// ------ go through main frames
		TreeNode<Region> actNode = root;
		Region sidebar = null;
		Region topbar = null;
		List<TreeNode<Region>> children = filterDataNodes(actNode.children, filteredDataNodes);
		while(children.size() == 1) {
			actNode = children.get(0);
			
			// left sidebar
			if(actNode.data.x >= 10 && sidebar == null) {
				sidebar = new Region(0, actNode.data.y, actNode.data.x, actNode.data.height, Region.AMBIGUOUS);
				mainRegions.add(sidebar);
			}
			
			// top sidebar
			if(actNode.data.y >= 10 && topbar == null) {
				topbar = new Region(0, 0, actNode.data.x+actNode.data.width, actNode.data.y, Region.AMBIGUOUS);
				mainRegions.add(topbar);
			}
			
			children = filterDataNodes(actNode.children, filteredDataNodes);
		}
		
		// ------ possible unrecognized side-bars
		if(actNode.data.area() < rootA*0.90) {
			System.out.println("smaller main frame");
			// TODO possible unrecognized side-bars
		}
		
		// ----- one main region without any split
		if(children.isEmpty()) {
			// TODO take the appropriate one from the hierarchy
			System.out.println("one main region");
			mainRegions.add(actNode.data);
			return mainRegions;
		}
		
		// ----- analyze split into one large region and smaller ones
		while(!children.isEmpty()) {			
			if(children.size() == 1 && actNode.data.area() < rootA*0.70) {
				mainRegions.add(actNode.data);
				return mainRegions;
			}
			
			sort(children);
			Collections.reverse(children);
			int n = children.size();
			
			TreeNode<Region> largest = children.get(0);
			int largestArea = largest.data.area();
			if(largestArea > actNode.data.area()*0.4 && largestArea > rootA*0.3) {
				// one dominant region and possible side-bars, tool-bars, buttons etc.
				for (TreeNode<Region> node : children) {
					if(node != largest) {
						//if(node.data.type != Region.DATA) {
							mainRegions.add(node.data);
						//}
					}
				}
				
				// continue with the largest
				actNode = largest;
				children = filterDataNodes(actNode.children, filteredDataNodes);
			} else {
				break;
			}
		}
		
		// ----- analyze small children
		int largeChildrenN = children.size();
		if(largeChildrenN == 0) {
			System.out.println("small children");
			//mainRegions.add(actNode.data);
		} else {
			for (TreeNode<Region> largeChild : children) {
				mainRegions.add(largeChild.data);
			}
		}
		
		/*Queue<TreeNode<Region>> queue = new LinkedList<>();
		queue.add(root);*/
		
		return mainRegions;
	}

	private static List<TreeNode<Region>> filterDataNodes(List<TreeNode<Region>> nodes, List<TreeNode<Region>> filteredDataNodes) {
		List<TreeNode<Region>> filtered = new ArrayList<>();
		for (TreeNode<Region> node : nodes) {
			if(node.data.type != Region.DATA) {
				filtered.add(node);
			} else {
				filteredDataNodes.add(node);
			}
		}
		return filtered;
	}

	private static List<TreeNode<Region>> findSimilar(List<TreeNode<Region>> nodes, TreeNode<Region> refNode) {
		List<TreeNode<Region>> similar = new ArrayList<>();
		for (TreeNode<Region> node : nodes) {
			if(node != refNode) {
				if(node.data.color == refNode.data.color) {
					similar.add(node);
				}
			}
		}
		return similar;
	}

	private static int area(List<TreeNode<Region>> nodes, TreeNode<Region> parent) {
		int area = 0;
		
		int[][] matrix = GrayMatrix.newMatrix(parent.data.width, parent.data.height, GrayMatrix.WHITE);
		int pX = parent.data.x;
		int pY = parent.data.y;
		
		Region r;
		int x1, y1;
		for (TreeNode<Region> node : nodes) {
			r = node.data;
			x1=r.x-pX;
			y1=r.y-pY;
			MatrixUtils.drawPixels(matrix, x1, y1, x1+r.width, y1+r.height, GrayMatrix.BLACK);
		}
		
		area = MatrixUtils.amount(matrix, GrayMatrix.BLACK);
		
		return area;
	}
	
	private static void sort(List<TreeNode<Region>> nodes) {		
		Collections.sort(nodes, new Comparator<TreeNode<Region>>() {

			@Override
			public int compare(TreeNode<Region> node1, TreeNode<Region> node2) {				
				return node1.data.area() - node2.data.area();
			}
		});
	}

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

	private static List<Region> getIntersectRegions(Region region1, List<Region> intersectRegions) {
		List<Region> actIntersections = new ArrayList<>();
		
		for (Region region : intersectRegions) {
			if(region != region1) {
				if(region1.intersects(region)) {
					Rectangle iR = region1.intersection(region);
					int iA = iR.width*iR.height;
					region.tmpIntersectArea = iA;
					actIntersections.add(region);
				}
			}
		}
		return actIntersections;
	}

	private static double similarity(List<Region> actIntersections, List<Region> resultRegions) {
		double rank = 0.0;
		for (Region region : actIntersections) {
			rank += similarity(region, resultRegions);
		}
		return rank/actIntersections.size();
	}
	
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

	public static List<Region> completeEmptySpaces(List<Region> mainRegions, TreeNode<Region> root) {
		
		int matrix[][] = GrayMatrix.newMatrix(root.data.width, root.data.height, 0);
		
		// print main regions
		for (Region region : mainRegions) {
			MatrixUtils.drawRectangle(matrix, region, -1, false);
		}
		
		// get candidate nodes
		List<TreeNode<Region>> candidateNodes = new ArrayList<>(); 
		processEmptySpaceNodes(root, matrix, candidateNodes);
		
		// analyze intersections of candidate nodes
		
		// filter candidate nodes
		
		for (TreeNode<Region> treeNode : candidateNodes) {
			mainRegions.add(treeNode.data);
		}
		
		return mainRegions;
	}

	private static void processEmptySpaceNodes(TreeNode<Region> node, int[][] matrix, List<TreeNode<Region>> candidateNodes) {
		if(intersectsMainRegions(node.data, matrix)) {
			for (TreeNode<Region> child : node.children) {
				processEmptySpaceNodes(child, matrix, candidateNodes);
			}
		} else {
			candidateNodes.add(node);
		}
	}

	private static boolean intersectsMainRegions(Region region, int[][] matrix) {
		int x1 = region.x;
		int x2 = region.x+region.width;
		int y1 = region.y;
		int y2 = region.y+region.height;
		
		for (int i = x1; i < x2; i++) {
			for (int j = y1; j < y2; j++) {
				if(matrix[i][j] == -1) {
					return true;
				}
			}
			
		}
		return false;
	}

}
