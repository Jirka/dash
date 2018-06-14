package cz.vutbr.fit.dashapp.segmenation.util.region;

import java.util.List;

import com.tree.TreeNode;

import cz.vutbr.fit.dashapp.util.matrix.GrayMatrix;
import cz.vutbr.fit.dashapp.util.matrix.MatrixUtils;

public class DrawRegionsUtil {
	
	/**
	 * 
	 * @param matrix
	 * @param regions
	 */
	public static int[][] drawRegions(int[][] matrix, List<Region> regions) {
		for (Region region : regions) {
			MatrixUtils.drawRectangle(matrix, region, region.color, true);
		}
		return matrix;
	}
	
	/**
	 * 
	 * @param matrix
	 * @param regions
	 */
	public static int[][] drawRegions(int[][] matrix, List<Region> regions, int color) {
		for (Region region : regions) {
			MatrixUtils.drawRectangle(matrix, region, color, true);
		}
		return matrix;
	}
	
	/**
	 * 
	 * @param matrix
	 * @param regions
	 */
	public static int[][] drawRegions(int[][] matrix, List<Region> regions, int color, boolean fill) {
		for (Region region : regions) {
			MatrixUtils.drawRectangle(matrix, region, color, !fill);
		}
		return matrix;
	}
	
	/**
	 * 
	 * @param matrix
	 * @param regions
	 */
	public static int[][] drawRegionTypes(int[][] matrix, List<Region> regions) {
		for (Region region : regions) {
			drawRegionType(matrix, region);
		}
		return matrix;
	}
	
	/**
	 * 
	 * @param matrix
	 * @param region
	 */
	public static int[][] drawRegionType(int[][] matrix, Region region) {
		if(region.type == Region.R_FILL) {
			MatrixUtils.drawRectangle(matrix, region, 0, true);
		} else if(region.type == Region.R_MEDIUM) {
			MatrixUtils.drawRectangle(matrix, region, 0, true);
		} else if(region.type == Region.R_BORDER) {
			MatrixUtils.drawRectangle(matrix, region, 0, true);
		} else if(region.type == Region.DATA) {
			MatrixUtils.drawRectangle(matrix, region, 225, true);
		} else {
			MatrixUtils.drawRectangle(matrix, region, 125, true);
		}
		return matrix;
	}
	
	/**
	 * 
	 * @param root
	 */
	public static int[][] drawRegions(TreeNode<Region> root, int depth) {
		int[][] matrix = GrayMatrix.newMatrix(root.data.width, root.data.height, GrayMatrix.WHITE);
		for (TreeNode<Region> node : root) {
			if(node.getLevel() <= depth || depth < 0) {
				DrawRegionsUtil.drawRegionType(matrix, node.data);
			}
		}
		return matrix;
	}

}
