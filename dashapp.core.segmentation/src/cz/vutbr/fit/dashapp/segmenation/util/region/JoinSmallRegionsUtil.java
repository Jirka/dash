package cz.vutbr.fit.dashapp.segmenation.util.region;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import com.tree.TreeNode;

import cz.vutbr.fit.dashapp.image.floodfill.BasicFloodFill;
import cz.vutbr.fit.dashapp.image.floodfill.SeedPixelUtil;
import cz.vutbr.fit.dashapp.model.Constants;
import cz.vutbr.fit.dashapp.segmenation.XYCutFinal;
import cz.vutbr.fit.dashapp.segmenation.util.bottomup.BUBasicUtil;
import cz.vutbr.fit.dashapp.segmenation.util.bottomup.BUJoinLineUtil;
import cz.vutbr.fit.dashapp.util.matrix.GrayMatrix;
import cz.vutbr.fit.dashapp.util.matrix.MatrixUtils;

/**
 * 
 * @author Jiri Hynek
 *
 */
public class JoinSmallRegionsUtil {
	
	private static final int CLR_WHITE_SPACE = GrayMatrix.WHITE; 
	private static final int CLR_MAIN_REGION = GrayMatrix.BLACK;
	private static final int CLR_CANDIDATE_REGION = (GrayMatrix.WHITE+GrayMatrix.BLACK)/2;
	
	private static final int TYPE_MAIN = 1;
	private static final int TYPE_CANDIDATE = 2;
	
	private static final int TYPE_JOIN = 100;
	
	private static class Regions {
		
		private int w;
		private int h;
		public List<Region> main;
		public List<Region> candidate;
		
		public Regions(int w, int h) {
			this.w = w;
			this.h = h;
		}
		
		public Regions shallowCopy() {
			Regions r = new Regions(w, h);
			r.main = this.main;
			r.candidate = this.candidate;
			return r;
		}
		
		public int[][] printMatrix(int color) {
			int matrix[][] = GrayMatrix.newMatrix(w, h, CLR_WHITE_SPACE);
			
			if(main != null) {
				DrawRegionsUtil.drawRegions(matrix, main, color, true);
			}
			
			if(candidate != null) {
				DrawRegionsUtil.drawRegions(matrix, candidate, color, true);
			}
			
			return matrix;
		}
		
		public int[][] printMatrix() {
			int matrix[][] = GrayMatrix.newMatrix(w, h, CLR_WHITE_SPACE);
			
			if(main != null) {
				DrawRegionsUtil.drawRegions(matrix, main, CLR_MAIN_REGION, true);
			}
			
			if(candidate != null) {
				DrawRegionsUtil.drawRegions(matrix, candidate, CLR_CANDIDATE_REGION, true);
			}
			
			return matrix;
		}
		
		public int[][] printMainRegions() {
			int matrix[][] = GrayMatrix.newMatrix(w, h, CLR_WHITE_SPACE);
			
			if(main != null) {
				DrawRegionsUtil.drawRegions(matrix, main, GrayMatrix.BLACK, true);
			}
			
			return matrix;
		}
		
		public int[][] printCandidateRegions() {
			int matrix[][] = GrayMatrix.newMatrix(w, h, CLR_WHITE_SPACE);
			
			if(candidate != null) {
				DrawRegionsUtil.drawRegions(matrix, candidate, GrayMatrix.BLACK, true);
			}
			
			return matrix;
		}

		public List<Region> getAll() {
			List<Region> all = new LinkedList<>();
			all.addAll(main);
			all.addAll(candidate);
			return all;
		}

		public int size() {
			return main.size()+candidate.size();
		}
	}
	
	/**
	 * 
	 * @param mainRegions
	 * @param root
	 * @param alg
	 * @return
	 */
	public static List<Region> completeEmptySpaces(List<Region> mainRegions, TreeNode<Region> root, XYCutFinal alg) {
		Regions regions = new Regions(root.data.width, root.data.height);
		
		// get main regions (filter small main regions)
		regions.main = filterMainRegions(mainRegions, root, alg);
		//alg.debug("main regions", GrayMatrix.printMatrixToImage(null, regions.printMatrix()));
		
		// find candidate regions (they don't intersects main regions)
		regions.candidate = findCandidateRegions(regions, root, alg);
		//alg.debug("main + candidate regions", GrayMatrix.printMatrixToImage(null, regions.printMatrix()));

		// join small regions ordered in lines (update candidate regions)
		regions = joinLines(regions, alg);
		//alg.debug("joined lines", GrayMatrix.printMatrixToImage(null, regions.printMatrix()));

		// try to join candidate regions (update main and candidate regions)
		int distanceLimit = 5;//Math.max(regions.w, regions.h)/160;
		int change = distanceLimit;
		int maxDistanceLimit = 20;
		System.out.println(regions.w + "px " + regions.h + "px : distance limit: " + maxDistanceLimit);
		while(regions.size() > 10 && distanceLimit <= 5
				|| regions.size() > 15 && distanceLimit <= 10 
				|| regions.size() > 20 && distanceLimit <= maxDistanceLimit) {
			regions = connectSmallRegions(regions, alg, distanceLimit);
			distanceLimit += change;
		}
		//alg.debug("connected", GrayMatrix.printMatrixToImage(null, regions.printMatrix()));
		// TODO: test amount of regions

		// TODO: filter small candidate nodes

		return regions.getAll();
	}

	/**
	 * 
	 * @param mainRegions
	 * @param root
	 * @param alg
	 */
	private static List<Region> filterMainRegions(List<Region> mainRegions, TreeNode<Region> root, XYCutFinal alg) {
		List<Region> resultRegions = new LinkedList<>();
		
		double shareOfMainRegionsArea = (double) area(mainRegions) / root.data.area();
		System.out.println("main regions share: " + shareOfMainRegionsArea);
		
		// filter only if there is a lot of empty space area
		if (shareOfMainRegionsArea < 0.50) {
			int limit_w = (int) (root.data.width * 0.75);
			int limit_h = (int) (root.data.height * 0.75);
			int limit_a = (int) (root.data.area() * 0.10);
			for (Region mainRegion : mainRegions) {
				if (mainRegion.width > limit_w || mainRegion.height > limit_h || mainRegion.area() > limit_a) {
					mainRegion.joinType = TYPE_MAIN;
					resultRegions.add(mainRegion);
				}
			}
		} else {
			// else add all main regions
			//resultRegions.addAll(mainRegions);
			int limit_a = (int) (root.data.area() * 0.8);
			for (Region mainRegion : mainRegions) {
				if(mainRegion.area() < limit_a) {
					mainRegion.joinType = TYPE_MAIN;
					resultRegions.add(mainRegion);
				}
			}
		}
		
		return resultRegions;
	}
	
	/**
	 * 
	 * @param regions
	 * @return
	 */
	private static int area(List<Region> regions) {
		int a = 0;
		for (Region region : regions) {
			a += region.area();
		}
		return a;
	}
	
	/**
	 * 
	 * @param matrix
	 * @param root
	 * @param alg
	 * @return
	 */
	private static List<Region> findCandidateRegions(Regions regions, TreeNode<Region> root, XYCutFinal alg) {
		List<Region> candidateRegions = new LinkedList<>();
		processEmptySpaceNodes(root, regions.printMatrix(), candidateRegions);
		return candidateRegions;
	}
	
	/**
	 * 
	 * @param node
	 * @param matrix
	 * @param candidateRegions
	 */
	private static void processEmptySpaceNodes(TreeNode<Region> node, int[][] matrix, List<Region> candidateRegions) {
		if(intersectsMainRegions(node.data, matrix) || isBigRegion(node.data, matrix)) {
			// if node intersects main region do split
			for (TreeNode<Region> child : node.children) {
				processEmptySpaceNodes(child, matrix, candidateRegions);
			}
		} else {
			candidateRegions.add(node.data);
		}
	}

	/**
	 * 
	 * @param region
	 * @param matrix
	 * @return
	 */
	private static boolean isBigRegion(Region region, int[][] matrix) {
		if(region.area() > MatrixUtils.area(matrix)*0.8) {
			return true;
		}
		
		return false;
	}

	/**
	 * 
	 * @param region
	 * @param matrix
	 * @return
	 */
	private static boolean intersectsMainRegions(Region region, int[][] matrix) {
		int x1 = region.x;
		int x2 = region.x+region.width;
		int y1 = region.y;
		int y2 = region.y+region.height;
		
		for (int i = x1; i < x2; i++) {
			for (int j = y1; j < y2; j++) {
				if(matrix[i][j] == CLR_MAIN_REGION) {
					return true;
				}
			}
			
		}
		return false;
	}
	
	/**
	 * Method joins vertical and horizontal lines
	 * 
	 * @param matrix
	 * @param candidateRegions
	 * @param mainRegions
	 * @param alg
	 * @return
	 */
	private static Regions joinLines(Regions regions, XYCutFinal alg) {
		Regions resultRegions = regions.shallowCopy();
		
		// print regions to matrix
		int[][] matrix = regions.printCandidateRegions();
		
		// join rows
		int rowLimit = regions.w/100;
		System.out.println("rowLimit: " + rowLimit);
		int[][] actMatrix = MatrixUtils.copy(matrix);
		BUJoinLineUtil.joinLine(actMatrix, Constants.X, 10, rowLimit);
		do {
			//alg.debug("rows", GrayMatrix.printMatrixToImage(null, actMatrix));
			MatrixUtils.copy(matrix, actMatrix);
			actMatrix = new ModifiedRectangleFloodFill(actMatrix, false, GrayMatrix.BLACK, regions.main).process();
			//actMatrix = BUBasicUtil.createRectangles(resultMatrix);
			//alg.debug("rows-rect", GrayMatrix.printMatrixToImage(null, actMatrix));
		} while (!BUBasicUtil.areEqualMatrices(matrix, actMatrix));
	
		// join columns
		int columnLimit = regions.h/100;
		System.out.println("columnLimit: " + columnLimit);
		BUJoinLineUtil.joinLine(actMatrix, Constants.Y, 10, columnLimit);
		do {
			//alg.debug("cols", GrayMatrix.printMatrixToImage(null, actMatrix));
			MatrixUtils.copy(matrix, actMatrix);
			actMatrix = new ModifiedRectangleFloodFill(actMatrix, false, GrayMatrix.BLACK, regions.main).process();
			//actMatrix = BUBasicUtil.createRectangles(resultMatrix);
			//alg.debug("cols-rect", GrayMatrix.printMatrixToImage(null, actMatrix));
		} while (!BUBasicUtil.areEqualMatrices(matrix, actMatrix));
		
		resultRegions.candidate = BUBasicUtil.getRegions(matrix);
		return resultRegions;
	}
	
	/**
	 * Method connects small regions with neighbors.
	 * 
	 * @param matrix
	 * @param candidateRegions
	 * @param mainRegions
	 * @param alg
	 * @param dl 
	 * @return
	 */
	private static Regions connectSmallRegions(Regions regions, XYCutFinal alg, int dl) {
		Regions resultRegions = regions.shallowCopy();
		
		// actual regions matrix
		int matrix[][] = regions.printMatrix();
		
		// create result matrix
		int resultMatrix[][] = GrayMatrix.newMatrix(MatrixUtils.width(matrix), MatrixUtils.height(matrix), GrayMatrix.WHITE);
		
		// sort candidate regions
		Collections.sort(regions.candidate, new Comparator<Region>() {

			@Override
			public int compare(Region r1, Region r2) {
				return r1.area()-r2.area();
			}
		});
		
		resultRegions.candidate = new LinkedList<>(regions.candidate);
		resultRegions.main = new LinkedList<>(regions.main);
		
		// go through all regions and try to join them with larger
		CandidatePoint[] candidatePoints = null;
		List<CandidatePoint> chosenPoints;
		//CandidatePoint chosenPoint;
		Region joinRegion = null;
		boolean joinPerformed;
		AffectedArea affectedArea;
		//int a_limit = (int) (MatrixUtils.area(matrix)*0.1);
		//for (Region region : regions.candidate) {
		int unchangedCount = 0;
		int candidatesCount = resultRegions.candidate.size();
		while (!resultRegions.candidate.isEmpty() && unchangedCount < candidatesCount) {
			Region region = resultRegions.candidate.remove(0);
			candidatesCount--;
			
			//System.out.println(region);
			
			//if(region.area() < a_limit || region.type == TYPE_JOIN) {
				candidatePoints = findCandidatePoints(region, resultRegions, matrix, alg);
				chosenPoints = chooseCandidatePoint(candidatePoints, region, resultRegions, matrix, dl, alg);
				
				joinPerformed = false;
				//if(!chosenPoints.isEmpty()) {
				for (CandidatePoint chosenPoint : chosenPoints) {
					//CandidatePoint chosenPoint = chosenPoints.get(0);
					joinRegion = chosenPoint.joinRegion;
					
					// analyze affected regions
					affectedArea = getAffectedRegions(resultRegions, joinRegion);
					if(!isAffectingImportantRegions(affectedArea, region, chosenPoint, resultRegions)) {
						if(chosenPoint.r.joinType == TYPE_MAIN) {
							resultRegions.main.remove(chosenPoint.r);
							// update the Regions object
							joinRegion.joinType = TYPE_MAIN;
							resultRegions.main.add(joinRegion);
							MatrixUtils.drawRectangle(matrix, joinRegion, CLR_MAIN_REGION, false);
							
							//alg.debug("d1", GrayMatrix.printMatrixToImage(null, resultRegions.printMatrix()));
						} else {
							// draw region
							//MatrixUtils.drawRectangle(resultMatrix, joinRegion, GrayMatrix.BLACK, false);
							resultRegions.candidate.remove(chosenPoint.r);
							// update the Regions object
							joinRegion.joinType = TYPE_CANDIDATE;
							joinRegion.type = TYPE_JOIN;
							resultRegions.candidate.add(joinRegion);
							MatrixUtils.drawRectangle(matrix, joinRegion, CLR_CANDIDATE_REGION, false);
							// draw connection region and point
							//connectRegionWithPoint(resultMatrix, region, candidate.p);
						}
						unchangedCount = 0;
						joinPerformed = true;
						//System.out.println("join");
						
						//alg.debug("join", GrayMatrix.printMatrixToImage(null, resultRegions.printMatrix()));
						
						break;
					}
				}
				
				if(!joinPerformed) {
					resultRegions.candidate.add(region);
					//MatrixUtils.drawRectangle(resultMatrix, region, GrayMatrix.BLACK, false);
					candidatesCount++;
					unchangedCount++;
				}
			/*} else {
				if(region.type == TYPE_JOIN) {
					continue;
				} else {
					break;
				}
			}*/
		}
		
		resultMatrix = resultRegions.printCandidateRegions();
		//alg.debug("d1", GrayMatrix.printMatrixToImage(null, resultMatrix));
		
		int[][] prevMatrix = new int[MatrixUtils.width(matrix)][MatrixUtils.height(matrix)];
		do {
			MatrixUtils.copy(prevMatrix, resultMatrix);
			resultMatrix = new ModifiedRectangleFloodFill(resultMatrix, false, GrayMatrix.BLACK, regions.main).process();
		} while (!BUBasicUtil.areEqualMatrices(resultMatrix, prevMatrix));
		
		//alg.debug("d1", GrayMatrix.printMatrixToImage(null, resultMatrix));
		
		resultRegions.candidate = BUBasicUtil.getRegions(resultMatrix);
		
		// TODO filter candidate regions stored in main regions
		
		return resultRegions;
	}


	private static class CandidatePoint {
		
		public Region joinRegion;
		public Point p;
		public int d;
		public Region r;
		public double share;
		public int clr;
		
		public CandidatePoint(Point p, int d, int clr) {
			this.p = p;
			this.d = d;
			this.clr = clr;
		}
	}

	/**
	 * 
	 * @param region
	 * @param regions 
	 * @param matrix
	 * @param alg
	 * @return
	 */
	private static CandidatePoint[] findCandidatePoints(Region region, Regions regions, int[][] matrix, XYCutFinal alg) {
		int mW = regions.w;
		int mH = regions.h;
		
		int x1 = region.x;
		int x2 = region.x2();
		int y1 = region.y;
		int y2 = region.y2();
		
		// find candidate points in all directions 
		CandidatePoint[] candidates = new CandidatePoint[4];
		
		// up
		int actDistance = 1;
		for (int y = y1-1; y >= 0 && candidates[0] == null; y--) {
			// check one line
			for (int x = x1; x < x2; x++) {
				if(matrix[x][y] != CLR_WHITE_SPACE) {
					candidates[0] = new CandidatePoint(new Point(x, y), actDistance, matrix[x][y]);
					break;
				}
			}
			
			// move up
			actDistance++;
		}
		
		// down
		actDistance = 1;
		for (int y = y2+1; y < mH && candidates[1] == null; y++) {
			// check one line
			for (int x = x1; x < x2; x++) {
				if(matrix[x][y] != CLR_WHITE_SPACE) {
					candidates[1] = new CandidatePoint(new Point(x, y), actDistance, matrix[x][y]);
					break;
				}
			}
			
			// move down
			actDistance++;
		}
		
		// left
		actDistance = 1;
		for (int x = x1-1; x >= 0 && candidates[2] == null; x--) {
			// check one line
			for (int y = y1; y < y2; y++) {
				if(matrix[x][y] != CLR_WHITE_SPACE) {
					candidates[2] = new CandidatePoint(new Point(x, y), actDistance, matrix[x][y]);
					break;
				}
			}
			
			// move left
			actDistance++;
		}
		
		// right
		actDistance = 1;
		for (int x = x2+1; x < mW && candidates[3] == null; x++) {
			// check one line
			for (int y = y1; y < y2; y++) {
				if(matrix[x][y] != CLR_WHITE_SPACE) {
					candidates[3] = new CandidatePoint(new Point(x, y), actDistance, matrix[x][y]);
					break;
				}
			}
			
			// move right
			actDistance++;
		}
		
		return candidates;
		
	}

	/**
	 * 
	 * @param candidates
	 * @param r
	 * @param regions
	 * @param matrix
	 * @param dl 
	 * @param alg
	 * @return
	 */
	private static List<CandidatePoint> chooseCandidatePoint(CandidatePoint[] candidates, Region r, Regions regions, int[][] matrix, int dl, XYCutFinal alg) {		
		List<CandidatePoint> resultPoints = new LinkedList<>();
		
		//double a = regions.w*regions.h;
		// go through all candidates and find the optimal one
		for (int i = 0; i < candidates.length; i++) {
			if(candidates[i] != null) {
				if(candidates[i].d > dl) {
					// filter big distances
					candidates[i] = null;
				} else {
					// get region from point
					candidates[i] = getRegion(regions, r, candidates[i]);
					candidates[i].joinRegion = r.joinWith(candidates[i].r);
					if(candidates[i] != null) {
						// apply heuristics
						if(candidates[i].share < 0.75) {
							
							//System.out.println(points[i].x + "," + points[i].y);
							//System.out.println(r + " " + r.area() + " " + r2 + " " + r2.area());
							//System.out.println("share: " + candidates[i].share);
						}
						//if(share[i] < (0.01*((double) r2.area()/r.area()))) {
						if(candidates[i].share < 0.4) {
						//if(candidates[i].share < 0.4 || candidates[i].share < 0.75 && candidates[i].joinRegion.area()/a > 0.15) {
							// filter connection of small region with a very large one
							candidates[i] = null;
						} else {
							int j = 0;
							for (CandidatePoint sortedPoint : resultPoints) {
								if(candidates[i].share > sortedPoint.share) {
									break;
								}
								j++;
							}
							resultPoints.add(j, candidates[i]);
						}
					}
				}
			}
		}
		
		// TODO we sort region according to the share variable
		// heuristics could be improved
		
		return resultPoints;
	}

	private static CandidatePoint getRegion(Regions regions, Region r, CandidatePoint candidatePoint) {
		Point p = candidatePoint.p;
		double actShare;
		candidatePoint.share = 0;
		for (Region r2 : regions.candidate) {
			if(r2.intersects(p.x, p.y)) {
				actShare = shareOfJoinArea(r, r2);
				if(actShare > candidatePoint.share) {
					candidatePoint.r = r2;
					candidatePoint.share = actShare;
				}
			}
		}
		if(candidatePoint.r == null) {
			for (Region r2 : regions.main) {
				if(r2.intersects(p.x, p.y)) {
					actShare = shareOfJoinArea(r, r2);
					if(actShare > candidatePoint.share) {
						candidatePoint.r = r2;
						candidatePoint.share = actShare;
					}
				}
			}
		}
		return candidatePoint;
	}
	
	private static class AffectedArea {
		List<Region> regions;
		Rectangle joinRectangle;
	}
	
	private static AffectedArea getAffectedRegions(Regions regions, Region refRegion) {
		// make flood-fill for the reference region in matrix
		int[][] matrix = regions.printMatrix(GrayMatrix.BLACK);
		Rectangle r_prev, r = refRegion;
		do {
			// draw the reference region in black color to see intersection with other regions
			MatrixUtils.drawRectangle(matrix, r, GrayMatrix.BLACK, false);
			r_prev = r;
			r = SeedPixelUtil.processSeedPixel(r_prev.x, r_prev.y, GrayMatrix.BLACK, CLR_CANDIDATE_REGION, matrix);
		} while (!r.equals(r_prev));
		
		AffectedArea affectedArea = new AffectedArea();
		affectedArea.joinRectangle = r;
		
		// find regions that intersects the flood-fill region
		affectedArea.regions = new LinkedList<>();
		for (Region actRegion : regions.candidate) {
			if(r.intersects(actRegion)) {
				affectedArea.regions.add(actRegion);
			}
		}
		for (Region actRegion : regions.main) {
			if(r.intersects(actRegion)) {
				affectedArea.regions.add(actRegion);
			}
		}
		
		return affectedArea;
	}
	
	private static boolean isAffectingImportantRegions(AffectedArea affectedArea, Region r1, CandidatePoint chosenPoint, Regions regions) {
		int a = regions.w*regions.h;
		int a_affected = affectedArea.joinRectangle.width*affectedArea.joinRectangle.height;
		int a_join = chosenPoint.joinRegion.area();
		int a_sum = r1.area()+chosenPoint.r.area();
		//System.out.println(a + " " + (double )a_affected/a + " " + (double) a_affected/a_sum);
		if(((double) a_affected/a > 0.2 && (double) a_affected/a_sum > 3)
			|| ((double) a_affected/a > 0.4 && (double) a_affected/a_sum > 1.5)
			/* || (double) a_affected/a_join > 3*/) {
			return true;
		}
		
		int limit = (int) (a*0.01);
		for (Region region : affectedArea.regions) {
			if(region != r1 && region != chosenPoint.r) {
				if(region.joinType == TYPE_MAIN) {
					return true;
				}
				
				/*if(region.area() > limit) {
					return true;
				}*/
			}
		}
		
		return false;
	}

	private static double shareOfJoinArea(Region r, Region r2) {
		int joinArea = (Math.max(r.x2(), r2.x2())-Math.min(r.x, r2.x))
		* (Math.max(r.y2(), r2.y2())-Math.min(r.y, r2.y));
		
		return (r.area()+r2.area())/(double) joinArea;
	}

	private static Region getRectangle(int[][] matrix, Point point, int color) {
		int mW = MatrixUtils.width(matrix);
		int mH = MatrixUtils.height(matrix);
		
		Region r = new Region(0,0,0,0,Region.R_FILL);
		
		// x1
		int i = point.x;
		while(i > 0 && matrix[i][point.y] == color) {
			i--;
		}
		r.x = ++i;
		
		// y1
		i = point.y;
		while(i > 0 && matrix[point.x][i] == color) {
			i--;
		}
		r.y = ++i;
		
		// x2
		i = point.x;
		while(i < mW && matrix[i][point.y] == color) {
			i++;
		}
		r.width = i-r.x;
		
		// y2
		i = point.y;
		while(i < mH && matrix[point.x][i] == color) {
			i++;
		}
		r.height = i-r.y;
		
		return r;
	}
	
	/**
	 * 
	 * @param matrix
	 * @param region
	 * @param p
	 */
	private static void connectRegionWithPoint(int[][] matrix, Region region, Point p) {
		int x1, x2, y1, y2;
		if(p.x >= region.x && p.x < region.x2()) { // x
			x1 = x2 = p.x;
			if(p.y < region.y) {
				y1 = p.y;
				y2 = region.y;
			} else {
				y1 = region.y2();
				y2 = p.y;
			}
		} else { // y
			y1 = y2 = p.y;
			if(p.x < region.x) {
				x1 = p.x;
				x2 = region.x;
			} else {
				x1 = region.x2();
				x2 = p.x;
			}
		}
		
		for (int i = x1; i <= x2; i++) {
			for (int j = y1; j <= y2; j++) {
				matrix[i][j] = Color.RED.getRGB();
			}
		}
	}
	
	/*private static Point findClosest(Region region, Regions regions, int[][] matrix, XYCutFinal alg) {
	
	Region prefRegion = null;
	int prefDistance = Integer.MAX_VALUE;
	
	int d;		

	
	// analyze candidate regions
	for (Region r2 : regions.candidate) {
		if(r2 != region) {
			d = region.distance(r2);
			if(d >= 0) {
				// r2 is in line with region
				if(d < prefDistance) {
					// get the nearest r2
					prefDistance = d;
					region = r2;
				}
			}
		}
	}
	
	return null;
}*/
	
	/**
	 * 
	 * @author Jiri Hynek
	 *
	 */
	public static class ModifiedRectangleFloodFill extends BasicFloodFill {
		
		private List<Region> mainRegions;

		public ModifiedRectangleFloodFill(int[][] matrix, boolean createNew, int refColor, List<Region> mainRegions) {
			super(matrix, createNew, refColor);
			this.mainRegions = mainRegions;
		}

		@Override
		protected void postProcessMatrix(int[][] resultMatrix) {
			super.postProcessMatrix(resultMatrix);
			// convert colors to black
			for (int i = 0; i < mW; i++) {
				for (int j = 0; j < mH; j++) {
					if (resultMatrix[i][j] < 0) {
						resultMatrix[i][j] = GrayMatrix.BLACK;
					}
				}
			}
		}
		
		@Override
		protected void postProcessSeedPixel(int x1, int y1, int x2, int y2, int markColor) {
			super.postProcessSeedPixel(x1, y1, x2, y2, markColor);
			// test rectangle size
			int rW = x2 - x1;
			int rH = y2 - y1;
			
			// calculate number of main regions in rectangle
			int mainRegionsCount = 0;
			for (Region mainRegion : mainRegions) {
				if(mainRegion.intersects(x1, y1, rW, rH)) {
					mainRegionsCount++;
				}
			}
			
			if ((rW > mW / 2 || rH > mH / 2) && (double) area/(rW*rH) < 0.6 || mainRegionsCount > 1) {
			//if(mainRegionsCount > 1) {
				// delete area
				// update: don't delete area
				/*for (int x = 0; x < mW; x++) {
					for (int y = 0; y < mH; y++) {
						if (matrix[x][y] == markColor) {
							matrix[x][y] = GrayMatrix.WHITE;
						}
					}
				}*/
			} else {
				// create rectangle
				MatrixUtils.drawPixels(matrix, x1, y1, x2, y2, markColor);
			}
		}
	}

}
