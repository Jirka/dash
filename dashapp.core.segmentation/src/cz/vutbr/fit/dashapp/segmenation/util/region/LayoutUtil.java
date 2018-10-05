package cz.vutbr.fit.dashapp.segmenation.util.region;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.tree.TreeNode;

import cz.vutbr.fit.dashapp.segmenation.util.region.Region.HierarchyComparator;
import cz.vutbr.fit.dashapp.util.matrix.GrayMatrix;
import cz.vutbr.fit.dashapp.util.matrix.MatrixUtils;

/**
 * Help methods which works with dashboard layout (tree of regions)
 * 
 * @author Jiri Hynek
 *
 */
public class LayoutUtil {
	
	/**
	 * Method construct layout tree which consists of regions located within specific area
	 * 
	 * @param regions
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @return
	 */
	public static TreeNode<Region> constructTree(List<Region> regions, int x, int y, int w, int h) {
		return constructTree(regions, new Region(0, 0, w, h, -1));
	}
	
	/**
	 * Method construct layout tree which consists of regions located within specific region
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
	 * Calculates area of nodes regions.
	 * 
	 * @param nodes
	 * @param parent
	 * @return
	 */
	public static int area(List<TreeNode<Region>> nodes, TreeNode<Region> parent) {
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
	
	/**
	 * Sort nodes by area.
	 * 
	 * @param nodes
	 */
	public static void sortByArea(List<TreeNode<Region>> nodes) {		
		Collections.sort(nodes, new Comparator<TreeNode<Region>>() {

			@Override
			public int compare(TreeNode<Region> node1, TreeNode<Region> node2) {				
				return node1.data.area() - node2.data.area();
			}
		});
	}

}
