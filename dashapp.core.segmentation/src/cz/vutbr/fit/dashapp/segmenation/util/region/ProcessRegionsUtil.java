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
		List<TreeNode<Region>> children = filterDataNodes(actNode.children, filteredDataNodes);
		while(children.size() == 1) {
			actNode = children.get(0);
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
			GrayMatrix.drawPixels(matrix, x1, y1, x1+r.width, y1+r.height, GrayMatrix.BLACK);
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

	private static void processHighestIntersection(Region dashboard, List<Region> intersectRegions, List<Region> resultRegions) {
		// find max intersect region
		Region region1 = intersectRegions.get(0);
		for (Region region : intersectRegions) {
			if(region.intersects > region1.intersects) {
				region1 = region;
			}
		}
		
		// get regions which intersects max intersect region
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
		
		// similar regions
		List<Region> similar = new ArrayList<>();
		Region join = new Region(region1.x, region1.y, region1.width, region1.height, Region.JOIN);
		for (Region region2 : actIntersections) {
			double share1 = (double) region2.tmpIntersectArea/region1.area();
			double share2 = (double) region2.tmpIntersectArea/region2.area();
			if(share1 > 0.90 && share2 > 0.90) {
				// join
				join.x = Math.min(join.x, region2.x);
				join.y = Math.min(join.y, region2.y);
				join.width = Math.max(join.x+join.width, region2.x+region2.width)-join.x;
				join.height = Math.max(join.y+join.height, region2.y+region2.height)-join.y;
				similar.add(region2);
			}
		}
		
		if(!similar.isEmpty()) {
			for (Region region2 : similar) {
				intersectRegions.remove(region2);
				actIntersections.remove(region2);
			}
			intersectRegions.remove(region1);
			region1 = join;
			region1.intersects = actIntersections.size();
			intersectRegions.add(region1);
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
		
		intersectRegions.remove(region1);
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

}
