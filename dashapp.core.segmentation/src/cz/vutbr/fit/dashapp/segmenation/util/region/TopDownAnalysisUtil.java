package cz.vutbr.fit.dashapp.segmenation.util.region;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.tree.TreeNode;

/**
 * Help methods for the top-down strategy used to process dashboard layout.
 * 
 * @author Jiri Hynek
 *
 */
public class TopDownAnalysisUtil {
	
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
				sidebar = new Region(0, actNode.data.y, actNode.data.x, actNode.data.height, Region.TYPE_AMBIGUOUS, Region.CATEGORY_MAIN);
				// TODO add to layout
				mainRegions.add(sidebar);
			}
			
			// top sidebar
			if(actNode.data.y >= 10 && topbar == null) {
				topbar = new Region(0, 0, actNode.data.x+actNode.data.width, actNode.data.y, Region.TYPE_AMBIGUOUS, Region.CATEGORY_MAIN);
				// TODO add to layout
				mainRegions.add(topbar);
			}
			
			children = filterDataNodes(actNode.children, filteredDataNodes);
		}
		
		// ------ possible unrecognized side-bars
		if(actNode.data.area() < rootA*0.90) {
			System.out.println("segmentation: top-down: smaller main frame");
			// TODO possible unrecognized side-bars
		}
		
		// ----- one main region without any split
		if(children.isEmpty()) {
			// TODO take the appropriate one from the hierarchy
			System.out.println("segmentation: top-down: one main region");
			mainRegions.add(actNode.data);
			actNode.data.category = Region.CATEGORY_MAIN;
			return mainRegions;
		}
		
		// ----- analyze split into one large region and smaller ones
		while(!children.isEmpty()) {			
			if(children.size() == 1 && actNode.data.area() < rootA*0.70) {
				mainRegions.add(actNode.data);
				actNode.data.category = Region.CATEGORY_MAIN;
				return mainRegions;
			}
			
			LayoutUtil.sortByArea(children);
			Collections.reverse(children);
			
			TreeNode<Region> largest = children.get(0);
			int largestArea = largest.data.area();
			if(largestArea > actNode.data.area()*0.4 && largestArea > rootA*0.3) {
				// one dominant region and possible side-bars, tool-bars, buttons etc.
				for (TreeNode<Region> node : children) {
					if(node != largest) {
						//if(node.data.type != Region.DATA) {
							mainRegions.add(node.data);
							actNode.data.category = Region.CATEGORY_MAIN;
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
			System.out.println("segmentation: top-down: small children");
			// mainRegions.add(actNode.data);
		} else {
			for (TreeNode<Region> largeChild : children) {
				mainRegions.add(largeChild.data);
				actNode.data.category = Region.CATEGORY_MAIN;
			}
		}
		
		return mainRegions;
	}
	
	private static List<TreeNode<Region>> filterDataNodes(List<TreeNode<Region>> nodes, List<TreeNode<Region>> filteredDataNodes) {
		List<TreeNode<Region>> filtered = new ArrayList<>();
		for (TreeNode<Region> node : nodes) {
			if(node.data.type != Region.TYPE_DATA) {
				filtered.add(node);
			} else {
				filteredDataNodes.add(node);
			}
		}
		return filtered;
	}

	/**
	 * Method for experimental purposes.
	 * 
	 * @param nodes
	 * @param refNode
	 * @return
	 */
	@SuppressWarnings("unused")
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

}
