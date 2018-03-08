package cz.vutbr.fit.dashapp.segmenation.util.region;

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
		int rrotH = root.data.height;
		int rootA = rootW*rrotH;
		
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

}
